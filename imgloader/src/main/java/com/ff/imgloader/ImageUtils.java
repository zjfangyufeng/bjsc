package com.ff.imgloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.ff.common.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public abstract class ImageUtils {

	public static String compressBitmapReturnPath(String picPath,String destinationDirectory,String destinationNameWithoutSuffix) {
		Bitmap image = ImageLoader.extractThumbNail(picPath, 1080, 1080,
				false);
		File f = new File(destinationDirectory,destinationNameWithoutSuffix+"."+FileUtils.getFileSuffix(picPath));
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			image.compress(Bitmap.CompressFormat.JPEG, 85, out);
			out.flush();
			out.close();
			return f.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return picPath;
	}

	public static boolean saveBitmap(Bitmap image, String fileName) {
		File f = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/mp/" + fileName);
		if (f.exists()) {
			f.delete();
		}
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			image.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 */
	public static Bitmap res2Bitmap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * convert Bitmap to byte array
	 * 
	 * @param b
	 * @return
	 */
	public static byte[] bitmapToByte(Bitmap b) {
		if (b == null) {
			return null;
		}

		ByteArrayOutputStream o = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, 100, o);
		return o.toByteArray();
	}

	/**
	 * convert byte array to Bitmap
	 * 
	 * @param b
	 * @return
	 */
	public static Bitmap byteToBitmap(byte[] b) {
		return decode2Bitmap(b);
	}

	/**
	 * convert Drawable to Bitmap
	 * 
	 * @param d
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable d) {
		return d == null ? null : ((BitmapDrawable) d).getBitmap();
	}

	/**
	 * convert Bitmap to Drawable
	 * 
	 * @param b
	 * @return
	 */
	public static Drawable bitmapToDrawable(Bitmap b) {
		return b == null ? null : new BitmapDrawable(b);
	}

	/**
	 * convert Drawable to byte array
	 * 
	 * @param d
	 * @return
	 */
	public static byte[] drawableToByte(Drawable d) {
		return bitmapToByte(drawableToBitmap(d));
	}

	/**
	 * convert byte array to Drawable
	 * 
	 * @param b
	 * @return
	 */
	public static Drawable byteToDrawable(byte[] b) {
		return bitmapToDrawable(byteToBitmap(b));
	}

	/**
	 * scale image
	 * 
	 * @param org
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
		return scaleImage(org, (float) newWidth / org.getWidth(),
				(float) newHeight / org.getHeight());
	}

	/**
	 * scale image
	 * 
	 * @param org
	 * @param scaleWidth
	 *            sacle of width
	 * @param scaleHeight
	 *            scale of height
	 * @return
	 */
	public static Bitmap scaleImage(Bitmap org, float scaleWidth,
			float scaleHeight) {
		if (org == null) {
			return null;
		}

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(),
				matrix, true);
	}

	public static Bitmap decode2Bitmap(byte[] bytes, int w, int h) {

		InputStream is = null;

		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		try {
			opts.inJustDecodeBounds = true;

			BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);

			if (w == 0 || h == 0) {
				opts.inSampleSize = computeSampleSize(opts, -1, opts.outHeight
						* opts.outWidth);
			} else {
				opts.inSampleSize = computeSampleSize(opts, -1, w * h);
			}
			opts.inJustDecodeBounds = false;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			is = new ByteArrayInputStream(bytes);
			bitmap = BitmapFactory.decodeStream(is, null, opts);

		} catch (Exception e) {

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		return bitmap;
	}

	public static Bitmap decode2Bitmap(byte[] bytes) {

		return decode2Bitmap(bytes, 0, 0);
	}

	private static int getResizedDimension(int maxPrimary, int maxSecondary,
			int actualPrimary, int actualSecondary) {
		// If no dominant value at all, just return the actual.
		if (maxPrimary == 0 && maxSecondary == 0) {
			return actualPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling
		// ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;
		if (resized * ratio > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}

	static int findBestSampleSize(int actualWidth, int actualHeight,
			int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}

		return (int) n;
	}

	public static Bitmap doParse(byte[] data, final int mMaxWidth,
			final int mMaxHeight) {
		if (mMaxWidth == 0 && mMaxHeight == 0) {
			return byteToBitmap(data);
		}

		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		Bitmap bitmap = null;

		// If we have to resize this image, first get the natural bounds.
		decodeOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
		int actualWidth = decodeOptions.outWidth;
		int actualHeight = decodeOptions.outHeight;

		// Then compute the dimensions we would ideally like to decode to.
		int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
				actualWidth, actualHeight);
		int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
				actualHeight, actualWidth);

		// Decode to the nearest power of two scaling factor.
		decodeOptions.inJustDecodeBounds = false;
		// TODO(ficus): Do we need this or is it okay since API 8 doesn't
		// support it?
		// decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
		decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
				actualHeight, desiredWidth, desiredHeight);
		Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
				decodeOptions);

		// If necessary, scale down to the maximal acceptable size.
		if (tempBitmap != null
				&& (tempBitmap.getWidth() > desiredWidth || tempBitmap
						.getHeight() > desiredHeight)) {
			bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth,
					desiredHeight, true);
			tempBitmap.recycle();
		} else {
			bitmap = tempBitmap;
		}

		return bitmap;

	}

	public static Bitmap doParse(String path, final int mMaxWidth,
			final int mMaxHeight) {
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		Bitmap bitmap = null;

		// If we have to resize this image, first get the natural bounds.
		decodeOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, decodeOptions);
		int actualWidth = decodeOptions.outWidth;
		int actualHeight = decodeOptions.outHeight;

		// Then compute the dimensions we would ideally like to decode to.
		int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
				actualWidth, actualHeight);
		int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
				actualHeight, actualWidth);

		// Decode to the nearest power of two scaling factor.
		decodeOptions.inJustDecodeBounds = false;
		// TODO(ficus): Do we need this or is it okay since API 8 doesn't
		// support it?
		// decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
		decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
				actualHeight, desiredWidth, desiredHeight);
		Bitmap tempBitmap = BitmapFactory.decodeFile(path, decodeOptions);

		// If necessary, scale down to the maximal acceptable size.
		if (tempBitmap != null
				&& (tempBitmap.getWidth() > desiredWidth || tempBitmap
						.getHeight() > desiredHeight)) {
			bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth,
					desiredHeight, true);
			tempBitmap.recycle();
		} else {
			bitmap = tempBitmap;
		}

		return bitmap;

	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap clipBitmap(Bitmap src, float size) {
		return clipBitmap(src, size, size);
	}

	public static Bitmap clipBitmap(Bitmap src, float imageWidth,
			float imageHeight) {
		int width = 0, height = 0;
		if (null == src || (width = src.getWidth()) <= 0
				|| (height = src.getHeight()) <= 0 || width == imageWidth
				&& height == imageHeight) {
			return src;
		}
		// if (0 > imageWidth)
		// {
		// imageWidth = PhoneInfo.width;
		// }
		// if (0 > imageHeight)
		// {
		// imageHeight = PhoneInfo.height;
		// }
		Matrix matrix = new Matrix();
		matrix.postScale(imageWidth / width, imageHeight / height);
		return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
	}

}
