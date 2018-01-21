package com.ff.imgloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.ff.common.DisplayMetricsTool;
import com.ff.common.FastBlur;
import com.ff.common.LogUtil;
import com.ff.common.SdMemoryStatus;
import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.custom_view.GifView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import junit.framework.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ImageLoader {
	private static ImageLoader instance = new ImageLoader();
	public final static String CACHE_IMAGE_PATH = ApplicationProxy.getInstance().getContext().getFilesDir()+"/images/";
	public final static int HEADPICSIZE = DisplayMetricsTool.getInstance().getWidthPixels()/2;
	public final static int PREVIEWPICSIZE = DisplayMetricsTool.getInstance().getWidthPixels()/2;
	public final static int FULLWIDTH = DisplayMetricsTool.getInstance().getWidthPixels();
	public final static int FULL_HEIGHT = DisplayMetricsTool.getInstance().getHeightPixels();

	int defaultImg = R.drawable.null_img_default_drawable;

	private ImageLoader() {
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		int cacheSize = maxMemory / 5;
		LogUtil.i("maxMemory:"+maxMemory+" cacheSize:"+cacheSize);
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				int i = bitmap.getRowBytes() * bitmap.getHeight() / 1024;
				LogUtil.i(key +" width " +bitmap.getRowBytes() +" height "+bitmap.getHeight()+" = "+ i);
				return i;
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
										Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
			}

		};
	}

	public synchronized static ImageLoader getInstance() {
		return instance;
	}

	private LruCache<String, Bitmap> mMemoryCache;
	private Handler handler = ApplicationProxy.getInstance().getHandler();

	public Bitmap getBitmapFromMemCache(String key) {
		if (key == null)
			return null;
		return mMemoryCache.get(key);
	}

	public void clearCache() {
		mMemoryCache.evictAll();
	}

	public static String getPrefixedUrl(String name) {
		return ApplicationProxy.getInstance().getIMG_BASE_URL() + name;
	}

	public void loadIcon(final String iconUrl, final View view, final int height, final int width,
						 final boolean crop) {
		loadImg(iconUrl, view, height, width, crop, true,defaultImg);
	}

	public void loadIcon(final String iconUrl, final ImageView iv, final int height, final int width,
						 final boolean crop,int defaultImg) {
		loadImg(iconUrl, iv, height, width, crop, true,defaultImg);
	}

	public void loadIconNotCache(final String iconUrl, final ImageView iv, final int height, final int width,
						 final boolean crop) {
		loadImg(iconUrl,iv,height,width,crop,false,defaultImg);
	}

	private void loadImg(final String iconUrl, final View view, final int height, final int width,
						 final boolean crop, final boolean addToMemoryCache, final int defaultImg) {
		if(view!=null)
			view.setTag(iconUrl);
		if(setImgFromMemCache(iconUrl,view)){
			return;
		}
		ThreadManager.getInstance().execute(iconUrl,
			new TaskIdRunnable(iconUrl) {
				@Override
				public void run() {
					try {
						if (!TextUtils.isEmpty(iconUrl)
								&& !iconUrl.equals("null")) {
							setImgFromDiskThenInternet(iconUrl, view,
									height, width, crop,addToMemoryCache,defaultImg);
						}
					} catch (Exception e) {
					} finally {
						ThreadManager.getInstance().remove(iconUrl);
					}
				}
			});
	}

	public void loadGif(final String url, final GifView view) {
		if(view!=null)
			view.setTag(url);
		ThreadManager.getInstance().execute(url,new TaskIdRunnable(url) {
			@Override
			public void run() {
				try {
					if (!TextUtils.isEmpty(url)
							&& !url.equals("null")) {
						setGifFromDiskThenInternet(url, view);
					}
				} catch (Exception e) {
				} finally {
					ThreadManager.getInstance().remove(url);
				}
			}
		});
	}

	void setImgFromDiskThenInternet(final String url, final View view, final int height, final int width, final boolean crop, boolean addToMemoryCache, final int defaultImg) {
		final Bitmap bitmap = loadImageFromDiskThenInternet(url, height, width, crop);
		if (bitmap == null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					setDefeatImg(view,defaultImg);
				}
			});
			return;
		}
		if(addToMemoryCache)
			addBitmapToMemoryCache(url, bitmap);
		if (view != null) {
			final String newUrl = view.getTag() + "";
			if (!url.equals(newUrl)) {
				return;
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (view != null){
						if(view instanceof ImageView){
							((ImageView)view).setImageBitmap(bitmap);
						}else {
							view.setBackgroundDrawable(new BitmapDrawable(ApplicationProxy.getInstance().getContext().getResources(),bitmap));
						}
					}
				}
			});
		}
	}

	void setGifFromDiskThenInternet(final String url, final GifView view) {
		final Movie movie = loadGifFromDiskThenInternet(url);
		if (movie == null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					setDefeatImg(view,defaultImg);
				}
			});
			return;
		}
		if (view != null) {
			final String newUrl = view.getTag() + "";
			if (!url.equals(newUrl)) {
				return;
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (view != null){
						view.setBackgroundDrawable(null);
						view.setMovie(movie);
					}
				}
			});
		}
	}

	public Bitmap loadImageFromDiskThenInternet(String url, final int height, final int width, final boolean crop) {
		Bitmap bitmap = null;
		String fileName = null;
		InputStream in = null;
		String fullPath = null;
		OutputStream os = null;
		File fullPathFile = null;
		try {
			fileName = getFileNameFromUrl(url);
			if (fileName == null) {
				return null;
			}
			bitmap = getLocalImage(fileName, height, width, crop);
			if (bitmap != null) {
				return bitmap;
			}
			if (ToolUtils.isSDcardExist()
					&& SdMemoryStatus.getSDFreeSize() >= ToolUtils.SDCARD_MIN_SPACE) {
				fullPath = ToolUtils.initSDcardPicturePath() + fileName;
			} else if (SdMemoryStatus.getAvailableInternalMemorySize() >= ToolUtils.MEMORY_MIN_SPACE) {
				fullPath = CACHE_IMAGE_PATH + fileName;
			}
			if (fullPath == null)
				return null;
			fullPathFile = new File(fullPath);
			if (!fullPathFile.getParentFile().exists()) {
				fullPathFile.getParentFile().mkdirs();
			}
			Request request = new Request.Builder()
					.url(url)
					.build();
			OkHttpClient client = ApplicationProxy.getInstance().getOkHttpClient();
			Response response = client.newCall(request).execute();
			in = response.body().byteStream();
			if (in != null) {
				try {
					os = new FileOutputStream(fullPathFile);
					CopyStream(in, os);
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fullPathFile.exists()) {
					// bitmap = decodeFixedBitmapFromDir(fullPath, -1, -1);
					bitmap = extractThumbNail(fullPath, height, width, crop);
				}
				return bitmap;
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	public Movie loadGifFromDiskThenInternet(String url) {
		Movie movie;
		String fileName;
		InputStream in = null;
		String fullPath = null;
		OutputStream os = null;
		File fullPathFile;
		try {
			fileName = getFileNameFromUrl(url);
			if (fileName == null) {
				return null;
			}
			movie = getLocalGif(fileName);
			if (movie != null) {
				return movie;
			}
			if (ToolUtils.isSDcardExist()
					&& SdMemoryStatus.getSDFreeSize() >= ToolUtils.SDCARD_MIN_SPACE) {
				fullPath = ToolUtils.initSDcardPicturePath() + fileName;
			} else if (SdMemoryStatus.getAvailableInternalMemorySize() >= ToolUtils.MEMORY_MIN_SPACE) {
				fullPath = CACHE_IMAGE_PATH + fileName;
			}
			if (fullPath == null)
				return null;
			fullPathFile = new File(fullPath);
			if (!fullPathFile.getParentFile().exists()) {
				fullPathFile.getParentFile().mkdirs();
			}
			Request request = new Request.Builder()
					.url(url)
					.build();
			OkHttpClient client = ApplicationProxy.getInstance().getOkHttpClient();
			Response response = client.newCall(request).execute();
			in = response.body().byteStream();
			if (in != null) {
				try {
					os = new FileOutputStream(fullPathFile);
					CopyStream(in, os);
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fullPathFile.exists()) {
					// movie = decodeFixedBitmapFromDir(fullPath, -1, -1);
					movie = Movie.decodeFile(fullPath);
				}
				return movie;
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return movie;
	}

	private boolean setImgFromMemCache(String iconUrl, View view){
		final Bitmap bitmap = getBitmapFromMemCache(iconUrl);
		if (bitmap != null) {
			if(view!=null){
				if(view instanceof ImageView)
					((ImageView)view).setImageBitmap(bitmap);
				else
					view.setBackgroundDrawable(new BitmapDrawable(ApplicationProxy.getInstance().getContext().getResources(),bitmap));
			}
			return true;
		} else {
			setDefeatImg(view,defaultImg);
		}
		return false;
	}

	public static Bitmap blur(final Bitmap src) {
		int scaleFactor = 25;
		int radius = 5;
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(src, src.getWidth() / scaleFactor, src.getHeight() / scaleFactor, true);
		Bitmap bitmap = FastBlur.doBlur(scaledBitmap, radius, true);
		return bitmap;
	}

	public void setDefeatImg(View view,int resId) {
		if (view != null) {
			if(view instanceof ImageView)
				((ImageView)view).setImageResource(resId);
		}
	}
//    public static String addWatermark(String text, String imgUrl, float textSize) {
//		String waterMarkImagePath = ImageLoader.getLocalImageFilePath(imgUrl) + MyConstant.waterMarkPath; //水印图片地址
//		File file = new File(waterMarkImagePath);
//		Bitmap bitmap,bmpTemp;
//		if (file.exists()) {
////            file.delete();
//			return waterMarkImagePath;
//		}else {
//			bitmap = ImageLoader.getInstance().loadImageFromDiskThenInternet(imgUrl, FULLWIDTH, FULLWIDTH, false);
//			if(bitmap!=null){
//				int width = bitmap.getWidth();
//				int height = bitmap.getHeight();
//				bmpTemp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//				Canvas canvas = new Canvas(bmpTemp);
//
//				Paint paint = new Paint();
//				paint.setFlags(Paint.ANTI_ALIAS_FLAG);
//				paint.setAntiAlias(true);
//				canvas.drawBitmap(bitmap, 0, 0, paint);
//
//				//画背景
//				Paint paint1 = new Paint();
//				paint1.setColor(Color.parseColor("#000000"));
//				paint1.setFlags(Paint.ANTI_ALIAS_FLAG);
//				paint1.setStyle(Paint.Style.FILL);
//				paint1.setAntiAlias(true);
//				paint1.setAlpha(70);
//				RectF rectF = new RectF(width * 0.155f, height * 0.85f, width * 0.840f, height * 0.95f);
//				canvas.drawRoundRect(rectF, 10, 10, paint1);
//
//				Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
//				paint.setTypeface(font);
//				paint.setColor(Color.parseColor("#ffffff"));
//				paint.setTextSize(textSize);
//				paint.setAlpha(153);
//				for (int i = 0; i < text.length(); i++) {
//					//打水印
//					String num = text.charAt(i) + "";
//					canvas.drawText(num, width * 0.220f + width * 0.070f * i, height * 0.920f, paint);  //设置字符间距
//				}
////        canvas.drawText(text, width * 0.250f, height * 0.920f, paint);  //设置字符间距
//
//				canvas.save(Canvas.ALL_SAVE_FLAG);
//				canvas.restore();
//				//将水印图片缓存到文件中
//
//				FileOutputStream fos = null;
//				try {
//					fos = new FileOutputStream(waterMarkImagePath);
//					bmpTemp.compress(Bitmap.CompressFormat.PNG, 100, fos);
//					fos.flush();
//					return waterMarkImagePath;
//				} catch (Exception e) {
//					e.printStackTrace();
//				} finally {
//					try {
//						if(bmpTemp!=null)
//							bmpTemp.recycle();
//						if(bitmap!=null)
//							bitmap.recycle();
//						if (fos != null) {
//							fos.close();
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		return null;
//	}

	public static final String qrcodeMarkPath="_qrcodemark";

	public synchronized static String addQRCode(String imgUrl) {
		String qrcodeMarkImagePath = ImageLoader.getLocalImageFilePath(imgUrl) + qrcodeMarkPath; //水印图片地址
		File file = new File(qrcodeMarkImagePath);
		Bitmap bitmap;
		if (file.exists()) {
//            file.delete();
			return qrcodeMarkImagePath;
		}else {
			bitmap = ImageLoader.getInstance().loadImageFromDiskThenInternet(imgUrl, FULLWIDTH, FULLWIDTH, false);
			if(bitmap!=null){
				FileOutputStream fos = null;
				Bitmap qrcode = null;
				try {
					Canvas canvas = new Canvas(bitmap);
					Paint paint = new Paint();
					paint.setFlags(Paint.ANTI_ALIAS_FLAG);
					paint.setAntiAlias(true);
//					qrcode = QRCodeEncoder.getInstance(ApplicationProxy.getInstance().getContext(), ShareTool.getShare_url(ApplicationProxy.getInstance().getInvite_code())).encodeAsBitmap();
					canvas.drawBitmap(qrcode, 100, 250, paint);

					canvas.save(Canvas.ALL_SAVE_FLAG);
					canvas.restore();
					//将水印图片缓存到文件中

					fos = new FileOutputStream(qrcodeMarkImagePath);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
					fos.flush();
					return qrcodeMarkImagePath;
				} catch (Throwable e) {
					e.printStackTrace();
				} finally {
					try {
						if(qrcode!=null)
							qrcode.recycle();
						if(bitmap!=null)
							bitmap.recycle();
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	public void addQRCodeAsync(final String shareImageUrl, final ImageView iv) {
		final String tag = ImageLoader.getLocalImageFilePath(shareImageUrl) + qrcodeMarkPath; //水印图片地址
		if(iv!=null)
			iv.setTag(tag);
		ThreadManager.getInstance().execute(tag,
				new TaskIdRunnable(tag) {
					@Override
					public void run() {
						try {
							String qrcodeMarkImagePath = ImageLoader.addQRCode(shareImageUrl);
							if (null != qrcodeMarkImagePath) {
								final Bitmap bitmap = BitmapFactory.decodeFile(qrcodeMarkImagePath);
								if(bitmap!=null){
									if (iv != null) {
										final String newUrl = iv.getTag() + "";
										if (!tag.equals(newUrl)) {
											return;
										}
										handler.post(new Runnable() {
											@Override
											public void run() {
												if (iv != null)
													iv.setImageBitmap(bitmap);
											}
										});
									}
								}
							}
						} catch (Throwable e) {
						} finally {
							ThreadManager.getInstance().remove(tag);
						}
					}
				});
	}

	public Bitmap getImageWithPath(final String path, final int height,final int width, final boolean crop) {
		Bitmap bitmap = getBitmapFromMemCache(path);
		if (bitmap != null) {
			return bitmap;
		}
		// bitmap = decodeFixedBitmapFromDir(path, -1, -1);
		bitmap = extractThumbNail(path, height, width, crop);
		if (bitmap != null) {
			addBitmapToMemoryCache(path, bitmap);
		}
		return bitmap;
	}

	public void setImgFromDiskThenInternet(final String url, final ImageView iv, final String type, final int height, final int width, final boolean crop) {
		if (iv == null)
			return;
		final Bitmap bitmap = loadImageFromDiskThenInternet(url, height, width, crop);// 获取网络图片
		if (bitmap == null) {
			return;
		}
		addBitmapToMemoryCache(url, bitmap);
		if (iv != null) {
			final String newUrl = iv.getTag() + "";
			if (!url.equals(newUrl))
				return;
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (!url.equals(newUrl))
						return;
					iv.setImageBitmap(bitmap);
				}
			});
		}
	}

	// 壁纸详情大图下载 不添加到运行缓存 421 唯一
	public Bitmap getFixImageLoadTask(final String url, final ImageView iv,int width, int height) {
		final Bitmap bitmap = loadImageFromDiskThenInternet(url, height, width, false);// 获取网络图片
		if (bitmap == null) {
			return bitmap;
		}
		if (iv != null) {
			final String newUrl = iv.getTag() + "";
			if (!url.equals(newUrl))
				return bitmap;
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (!url.equals(newUrl))
						return;
					if (iv != null)
						iv.setImageBitmap(bitmap);
				}
			});
		}
		return bitmap;
	}

	public void setImgFromDiskThenInternet(String url, final int height,
										   final int width, final boolean crop) {
		final Bitmap bitmap = loadImageFromDiskThenInternet(url, height, width, crop);// 获取网络图片
		if (bitmap == null) {
			return;
		}

		addBitmapToMemoryCache(url, bitmap);
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (key == null || bitmap == null) {
			return;
		}
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public static void CopyStream(InputStream is, OutputStream os)
			throws IOException {
		final int buffer_size = 1 * 1024;
		byte[] bytes = new byte[buffer_size];
		for (;;) {
			int count = is.read(bytes, 0, buffer_size);
			if (count == -1)
				break;
			os.write(bytes, 0, count);
		}
	}

	public static String getFileNameFromUrl(String url) {
		String fileName = null;
		String[] arr = url.split("/");
		if (arr != null) {
			fileName = arr[arr.length - 1];
			fileName = fileName.replace("?", "").replace(":", "")
					.replace("*", "").replace("\"", "").replace("/", "")
					.replace("\\", "").replace("<", "").replace(">", "")
					.replace("|", "");
			int index = fileName.lastIndexOf(".");
			if (index > 0) {
				fileName = fileName.substring(0, index);
			}
		}
		return fileName;
	}

	private static Bitmap getLocalImage(String fileName, final int height,final int width, final boolean crop) {
		String fullPath = null;
		File file = null;
		if (ToolUtils.isSDcardExist()) {
			fullPath = ToolUtils.initSDcardPicturePath() + fileName;
		} else {
			fullPath = CACHE_IMAGE_PATH + fileName;
			file = new File(CACHE_IMAGE_PATH);
			if (!file.exists()) {
				file.mkdirs();
				ToolUtils.chmodPath(file.getAbsolutePath());
			}
		}
		if (new File(fullPath).exists()) {
			return extractThumbNail(fullPath, height, width, crop);
		}
		return null;
	}

	private static Movie getLocalGif(String fileName) {
		String fullPath;
		File file;
		if (ToolUtils.isSDcardExist()) {
			fullPath = ToolUtils.initSDcardPicturePath() + fileName;
		} else {
			fullPath = CACHE_IMAGE_PATH + fileName;
			file = new File(CACHE_IMAGE_PATH);
			if (!file.exists()) {
				file.mkdirs();
				ToolUtils.chmodPath(file.getAbsolutePath());
			}
		}
		if (new File(fullPath).exists()) {
			return Movie.decodeFile(fullPath);
		}
		return null;
	}

	public static String getLocalImageFilePath(String url) {
		String fileName = getFileNameFromUrl(url);
		if (fileName == null) {
			return null;
		}
		String fullPath = null;
		File file = null;
		if (ToolUtils.isSDcardExist()) {
			fullPath = ToolUtils.initSDcardPicturePath() + fileName;
		} else {
			fullPath = CACHE_IMAGE_PATH + fileName;
			file = new File(CACHE_IMAGE_PATH);
			if (!file.exists()) {
				file.mkdirs();
				ToolUtils.chmodPath(file.getAbsolutePath());
			}
		}
		if (new File(fullPath).exists()) {
			try {
				// return decodeFixedBitmapFromDir(fullPath, -1, -1);
				return fullPath;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Bitmap decodeFixedBitmapFromDir(String path, int reqWidth,int reqHeight) {
		Bitmap bitmap;
		if (!new File(path).exists())
			return null;
		if (reqWidth == -1 || reqHeight == -1) {
			reqWidth = 480;
			reqHeight = 800;
		}
		// 获取源图片的高度和宽度
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// 计算要缩小的值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;// 允许在内存不足时回收图片
		options.inInputShareable = true;
		int digree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int ori = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);
			switch (ori) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					digree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					digree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					digree = 270;
					break;
				default:
					digree = 0;
					break;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			Bitmap tempBitmap = BitmapFactory.decodeFile(path, options);
			if (digree != 0) {
				Matrix m = new Matrix();
				m.postRotate(digree);
				Bitmap tempBitmap2 = tempBitmap;
				tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0,
						tempBitmap.getWidth(), tempBitmap.getHeight(), m, true);
				if (!tempBitmap2.equals(tempBitmap))
					tempBitmap2.recycle();
			}
			return tempBitmap;
		} catch (OutOfMemoryError o) {
			System.gc();
		} catch (Exception e) {
		}
		return null;
	}

	public static Bitmap postRotateBitmap(String path, Bitmap sourceBitmap) {
		int digree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int ori = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);
			switch (ori) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					digree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					digree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					digree = 270;
					break;
				default:
					digree = 0;
					break;
			}
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		try {
			if (digree != 0) {
				Matrix m = new Matrix();
				m.postRotate(digree);
				Bitmap temp = sourceBitmap;
				sourceBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
						sourceBitmap.getWidth(), sourceBitmap.getHeight(), m,
						true);
				if (!temp.equals(sourceBitmap))
					temp.recycle();
			}
		} catch (OutOfMemoryError o) {
			System.gc();
		} catch (Exception e) {
		}
		return sourceBitmap;
	}

	private static final int MAX_DECODE_PICTURE_SIZE = 1024 * 1280;

	public static Bitmap extractThumbNail(final String path, final int height,
										  final int width, final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0
				&& width > 0);

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
			}

			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY)
					: (beY < beX ? beX : beY));
			LogUtil.i(options.outWidth +" "+width+" "+ beX+" "+options.outHeight +" "+height+" "+beY+" "+options.inSampleSize);
			if (options.inSampleSize < 1) {
				options.inSampleSize = 1;
			}

			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			options.inPurgeable = true;// 允许在内存不足时回收图片
			options.inInputShareable = true;
			options.inMutable = true;

			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				return null;
			}
			bm = postRotateBitmap(path, bm);

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm,
						(bm.getWidth() - width) >> 1,
						(bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}
				if (!bm.equals(cropped))
					bm.recycle();
				bm = cropped;
			}
			return bm;
		} catch (OutOfMemoryError e) {
		}
		return null;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
											int reqWidth, int reqHeight) {
		int inSampleSize = 1;
		if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
			final int heightRatio = (int) Math.ceil((float) options.outHeight
					/ (float) reqHeight);
			final int widthRatio = (int) Math.ceil((float) options.outWidth
					/ (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
			// inSampleSize = (heightRatio + widthRatio) / 2;
		}
		if (inSampleSize < 1) {
			inSampleSize = 1;
		}
		while (options.outHeight * options.outWidth / inSampleSize > MAX_DECODE_PICTURE_SIZE) {
			inSampleSize++;
		}
		return inSampleSize;
	}

	private Bitmap loadFileFromInternet(String url, final int height,
										final int width, final boolean crop) {
		Bitmap bitmap = null;
		String fileName = null;
		InputStream in = null;
		String fullPath = null;
		OutputStream os = null;
		File fullPathFile = null;
		try {
			fileName = getFileNameFromUrl(url);
			if (fileName == null) {
				return null;
			}
			bitmap = getLocalImage(fileName, height, width, crop);
			if (bitmap != null) {
				return bitmap;
			}

			if (ToolUtils.isSDcardExist()
					&& SdMemoryStatus.getSDFreeSize() >= ToolUtils.SDCARD_MIN_SPACE) {
				fullPath = ToolUtils.initSDcardPicturePath() + fileName;
			} else if (SdMemoryStatus.getAvailableInternalMemorySize() >= ToolUtils.MEMORY_MIN_SPACE) {
				fullPath = CACHE_IMAGE_PATH + fileName;
			}
			if (fullPath == null)
				return null;
			fullPathFile = new File(fullPath);
			if (!fullPathFile.getParentFile().exists()) {
				fullPathFile.getParentFile().mkdirs();
			}
			// if (fullPathFile.exists()) {
			// fullPathFile.delete();
			// }
			Request request = new Request.Builder()
					.url(url)
					.build();
			OkHttpClient client = ApplicationProxy.getInstance().getOkHttpClient();
			Response response = client.newCall(request).execute();
			in = response.body().byteStream();
			if (in != null) {
				try {
					os = new FileOutputStream(fullPathFile);
					CopyStream(in, os);
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fullPathFile.exists()) {
					// bitmap = decodeFixedBitmapFromDir(fullPath, -1, -1);
					bitmap = extractThumbNail(fullPath, height, width, crop);
				}
				return bitmap;
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	public static void fetchImageToDisk(String url) {
		String fileName ;
		InputStream in = null;
		String fullPath = null;
		OutputStream os = null;
		File fullPathFile ;
		try {
			fileName = getFileNameFromUrl(url);
			if (fileName == null) {
				return ;
			}

			if (isLocalFileExistWithName(fileName)) {
				return;
			}

			if (ToolUtils.isSDcardExist()
					&& SdMemoryStatus.getSDFreeSize() >= ToolUtils.SDCARD_MIN_SPACE) {
				fullPath = ToolUtils.initSDcardPicturePath() + fileName;
			} else if (SdMemoryStatus.getAvailableInternalMemorySize() >= ToolUtils.MEMORY_MIN_SPACE) {
				fullPath = CACHE_IMAGE_PATH + fileName;
			}
			if (fullPath == null)
				return;
			fullPathFile = new File(fullPath);
			if (!fullPathFile.getParentFile().exists()) {
				fullPathFile.getParentFile().mkdirs();
			}
			// if (fullPathFile.exists()) {
			// fullPathFile.delete();
			// }
			Request request = new Request.Builder()
					.url(url)
					.build();
			OkHttpClient client = ApplicationProxy.getInstance().getOkHttpClient();
			Response response = client.newCall(request).execute();
			in = response.body().byteStream();
			if (in != null) {
				try {
					os = new FileOutputStream(fullPathFile);
					CopyStream(in, os);
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fullPathFile.exists()) {
					// bitmap = decodeFixedBitmapFromDir(fullPath, -1, -1);
				}
				return ;
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			return ;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isLocalFileExistWithUrl(String url) {
		String fileName;
		try {
			fileName = getFileNameFromUrl(url);
			if (fileName == null) {
				return false;
			}
			boolean b = isLocalFileExistWithName(fileName);
			if (b) {
				return true;
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}

	public static boolean isLocalFileExistWithName(String fileName) {
		String fullPath;
		File file;
		if (ToolUtils.isSDcardExist()) {
			fullPath = ToolUtils.initSDcardPicturePath() + fileName;
			file = new File(ToolUtils.initSDcardPicturePath());
		} else {
			fullPath = CACHE_IMAGE_PATH + fileName;
			file = new File(CACHE_IMAGE_PATH);
			if (!file.exists()) {
				file.mkdirs();
				ToolUtils.chmodPath(file.getAbsolutePath());
			}
		}
		if (file.exists()) {
			if (new File(fullPath).exists())
				return true;
		}
		return false;
	}

}
