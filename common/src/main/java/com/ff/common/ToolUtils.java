package com.ff.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ff.common.application.ApplicationProxy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolUtils {
	public final static int SDCARD_MIN_SPACE = 20 * 1024 * 1024;
	public final static int MEMORY_MIN_SPACE = 20 * 1024 * 1024;

	/**
	 * @param s
	 * @param start include(start with zero)
	 * @param end exclude
	 * @return
	 */
	public static String encryptString(String s,int start,int end){
		try {
			char[] buffer = new char[s.length()];
			s.getChars(0,s.length(),buffer,0);
			for(int i = 0;i<buffer.length;i++){
                if(i>=start && i<end){
                    buffer[i] = '*';
                }
            }
			return new String(buffer);
		} catch (Exception e) {
		}
		return null;
	}

	public static Drawable getApplicationIcon(String pname){
		List<PackageInfo> packages = ApplicationProxy.getInstance().getContext().getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			String packageName = packageInfo.packageName;
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && packageName.equals(pname)) {
				return packageInfo.applicationInfo.loadIcon(ApplicationProxy.getInstance().getContext().getPackageManager());
			}
		}
		return null;
	}

	public static String getApplicationLabel(String pname){
		String appName = null;
		List<PackageInfo> packages = ApplicationProxy.getInstance().getContext().getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			String packageName = packageInfo.packageName;
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && packageName.equals(pname)) {
				appName = (String) packageInfo.applicationInfo.loadLabel(ApplicationProxy.getInstance().getContext().getPackageManager());
			}
		}
		return appName;
	}

	public static boolean isApplicationInstalled(String appName){
		boolean result = false;
		List<PackageInfo> packages = ApplicationProxy.getInstance().getContext().getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			String appLabel = (String) packageInfo.applicationInfo.loadLabel(ApplicationProxy.getInstance().getContext().getPackageManager());
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && appLabel.equals(appName)) {
				result = true;
			}
		}
		return result;
	}

	public static boolean isApplicationInstalledByPackageName(String package_name){
		if (TextUtils.isEmpty(package_name)) return false;
		try {
			ApplicationProxy.getInstance().getContext().getPackageManager().getPackageInfo(package_name,0);
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
		return true;
	}

	public static boolean isSystemApplication(String pkgName){
		boolean result = true;
		List<PackageInfo> packages = ApplicationProxy.getInstance().getContext().getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && packageInfo.applicationInfo.packageName.equals(pkgName)) {
				result = false;
			}
		}
		return result;
	}

	public static Intent getLaunchIntentForAPPName(String appName){
		Intent result = null;
		List<PackageInfo> packages = ApplicationProxy.getInstance().getContext().getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			String appLabel = (String) packageInfo.applicationInfo.loadLabel(ApplicationProxy.getInstance().getContext().getPackageManager());
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && appLabel.equals(appName)) {
				result = ApplicationProxy.getInstance().getContext().getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
			}
		}
		return result;
	}

	public static String getPackageName(String appName){
		List<PackageInfo> packages = ApplicationProxy.getInstance().getContext().getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			String appLabel = (String) packageInfo.applicationInfo.loadLabel(ApplicationProxy.getInstance().getContext().getPackageManager());
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && appLabel.equals(appName)) {
				return packageInfo.packageName;
			}
		}
		return null;
	}

	public static boolean isFirstIn(Context c) {
		return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(
				"first_in", true);
	}

	public static void setIsFirstIn(Context c, boolean b) {
		PreferenceManager.getDefaultSharedPreferences(c).edit()
				.putBoolean("first_in", false).commit();
	}

	public static boolean ifIsPictureMessage(String message){
		return message.startsWith("~^") && message.endsWith("~^");
	}

	public static String getPicName(String message){
		String result = null;
		if(message.length() > 4){
			result = message.substring(2,message.length() - 2);
			if(result != null && (result.endsWith(".png") || result.endsWith(".PNG")
					|| result.endsWith(".jpeg")
					|| result.endsWith(".JPEG")
					|| result.endsWith(".jpg") || result.endsWith(".JPG"))){
				return result;
			}else{
				return null;
			}
		}
		return  result;
	}

	public static boolean isMobileNum(String mobiles) {
		if(!isNull(mobiles)){
			Pattern p = Pattern.compile("^1[0-9]{10}$");
			Matcher m = p.matcher(mobiles);
			return m.matches();
		}
		return false;
	}

	public static boolean isEmail(String strEmail) {
		String strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	/** 计算年龄 */
	public static String getAge(Date birthDay) throws Exception {
		Calendar cal = Calendar.getInstance();

//		if (cal.before(birthDay)) {
//			throw new IllegalArgumentException(
//					"The birthDay is before Now.It's unbelievable!");
//		}		
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(birthDay);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth + 1;

//		if (monthNow <= monthBirth) {
//			if (monthNow == monthBirth) {
//				// monthNow==monthBirth
//				if (dayOfMonthNow < dayOfMonthBirth) {
//					age--;
//				}
//			} else {
//				// monthNow>monthBirth
//				age--;
//			}
//		}

		return age + "";
	}

	public static boolean checkPasswordFormat(String pwd) {
		Pattern p = Pattern.compile("[a-zA-Z0-9]{6,14}");
		Matcher m = p.matcher(pwd);
		return m.matches();
	}

	public static boolean checkDigitFormat(String pwd) {
		Pattern p = Pattern.compile("[0-9]{6}");
		Matcher m = p.matcher(pwd);
		return m.matches();
	}

	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = "00:"+unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return timeStr;
	}

	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

	public static String formatDateLong2String(long time) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return dateFormat.format(new Date(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatDateLong2String2(long time) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
			return dateFormat.format(new Date(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatDateLong2StringEntire(long time) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return dateFormat.format(new Date(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatDateLong2StringMMdd(long time) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			return dateFormat.format(new Date(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatDateLong2StringMDHM(long time) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			return dateFormat.format(new Date(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatDateLong2StringHM(long time) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			return dateFormat.format(new Date(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static long formatDateString2Long(String time) {
		long timelong = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d = dateFormat.parse(time);
			timelong = d.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timelong;
	}

	public static String getBeforeDate(String oldTime) {
		StringBuffer sb = new StringBuffer();
		try {
			long time = System.currentTimeMillis() - Long.parseLong(oldTime);
			long mill = (long) Math.ceil(time / 1000);
			long minute = (long) Math.ceil(time / 60 / 1000.0f);
			long hour = (long) Math.floor(time / 60 / 60 / 1000.0f);
			long day = (long) Math.floor(time / 24 / 60 / 60 / 1000.0f);
			if (day > 0) {
				sb.append(day + "天");
			} else if (hour > 0) {
				if (hour >= 24) {
					sb.append("1天");
				} else {
					sb.append(hour + "小时");
				}
			} else if (minute - 1 > 0) {
				if (minute == 60) {
					sb.append("1小时");
				} else {
					sb.append(minute + "分钟");
				}
			} else if (mill - 1 > 0) {
				if (mill == 60) {
					sb.append("1分钟");
				} else {
					sb.append(mill + "秒");
				}
			} else {
				sb.append("刚刚");
			}
			if (!sb.toString().equals("刚刚")) {
				sb.append("前");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String getBeforeDateOnlyDay(String oldTime) {
		StringBuffer sb = new StringBuffer();
		try {
			long time = System.currentTimeMillis() - Long.parseLong(oldTime);
			long day = (long) Math.floor(time / 24 / 60 / 60 / 1000.0f);
			if (day > 0) {
				sb.append(day + "天");
			} else {
				sb.append("今天");
			}
			if (!sb.toString().equals("今天")) {
				sb.append("前");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void setWindowStatusBarColor(Activity activity, int colorResId) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = activity.getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(activity.getResources().getColor(colorResId));
			}
		} catch (Exception e) {
		}
	}

	public static String getPackageNameFromFile() {
		PackageManager pm = ApplicationProxy.getInstance().getContext().getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(
				Environment.getExternalStorageDirectory() + "/7.apk",
				PackageManager.GET_ACTIVITIES);
		ApplicationInfo appInfo = null;
		if (info != null) {
			appInfo = info.applicationInfo;
			String packageName = appInfo.packageName;
			return packageName;
		}
		return null;
	}

	public static boolean isWifi() {
		ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationProxy.getInstance().getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public static String getChannel() {
		String market = PackerNg.getMarket(ApplicationProxy.getInstance().getContext());
		try {
			String [] info = market.split(",");
			return info[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try {
//			ApplicationInfo info = ApplicationProxy.getInstance().getContext()
//					.getPackageManager()
//					.getApplicationInfo(ApplicationProxy.getInstance().getContext().getPackageName(),
//							PackageManager.GET_META_DATA);
//			return info.metaData.get("UMENG_CHANNEL").toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return "";
	}

	public static String getVer() {
		String market = PackerNg.getMarket(ApplicationProxy.getInstance().getContext());
		try {
			String [] info = market.split(",");
			return info[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try {
//			ApplicationInfo info = ApplicationProxy.getInstance().getContext()
//					.getPackageManager()
//					.getApplicationInfo(ApplicationProxy.getInstance().getContext().getPackageName(),
//							PackageManager.GET_META_DATA);
//			return info.metaData.get("ver").toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return "";
	}

	public static String getClientVersion() {
		try {
			int versionCode = ApplicationProxy.getInstance().getContext().getPackageManager().getPackageInfo(ApplicationProxy.getInstance().getContext().getPackageName(), 0).versionCode;
			return versionCode+"";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isNull(String a) {
		if (a == null || a.length() == 0 || a.equalsIgnoreCase("null")) {
			return true;
		}
		return false;
	}

	public static boolean isActivityDestroyed(Activity activity){
		boolean isDestroyed = activity.isFinishing();
		if((Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())){
			isDestroyed = true;
		}
		return isDestroyed;
	}

	public static boolean deleteFile(File rootFile, boolean showMsg) {
		boolean result = false;
		if (rootFile.exists()) {
			if (rootFile.isDirectory()) {
				File[] fileList = rootFile.listFiles();
				if (fileList != null) {
					for (int i = 0; i < fileList.length; i++) {
						deleteFile(fileList[i], false);
					}
				}
			}
			result = rootFile.delete();
			if (showMsg) {
				if (result) {
					ToolUtils.showmsg("清除成功");
				} else {
					ToolUtils.showmsg("清除失败");
				}
			}
			return result;
		} else {
//			ToolUtils.showmsg("文件不存在");
			ToolUtils.showmsg("清除成功");
			return result;
		}
	}

	private static Toast toast;

	public static void showmsg(final String msg) {
		if (msg == null || msg.length() == 0)
			return;
		ApplicationProxy.getInstance().getHandler().post(new Runnable() {
			@Override
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(ApplicationProxy.getInstance().getContext(), msg,
							Toast.LENGTH_LONG);
				} else {
					toast.setText(msg);
				}
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});
	}

	private static ProgressDialog pd;

	public static final void showpd(final Context mContext) {
		ApplicationProxy.getInstance().getHandler().post(new Runnable() {
			@Override
			public void run() {
				if (pd == null) {
					pd = ToolUtils.mypd(mContext, null, "请稍候...", false, false,
							new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
								}
							});
				}
				if (!pd.isShowing()) {
					pd.setMessage("请稍候...");
					pd.show();
				}
			}
		});
	}

	public static final void dismissDialog() {
		try {
			if (pd != null) {
				pd.dismiss();
				pd = null;
			}
		} catch (Exception e) {
		}
	}

	public static ProgressDialog mypd(Context context, CharSequence title,
									  CharSequence message, boolean indeterminate, boolean cancelable,
									  OnCancelListener cancelListener) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		return dialog;
	}

	public static void installApk(String path) {
		ToolUtils.chmodPath(path);
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(path));
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		ApplicationProxy.getInstance().getContext().startActivity(intent);
	}

	public static String byteArrayToHex(byte[] byteArray) {
		// 首先初始化一个字符数组，用来存放每个16进制字符
		char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'a','b','c','d','e','f' };
		// new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
		char[] resultCharArray =new char[byteArray.length * 2];
		// 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		// 字符数组组合成字符串返回
		return new String(resultCharArray);
	}

	public static final String byte2hexString(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}

	/**
	 * 判断当前手机是否有ROOT权限
	 *
	 * @return
	 */
	public static int isRoot() {
		int bool = 0;
		try {
			if ((!new File("/system/bin/su").exists())
					&& (!new File("/system/xbin/su").exists())) {
				bool = 0;
			} else {
				bool = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bool;
	}

	public static boolean isSDcardExist() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	public static String initSDcardDownloadPath() {
		return ApplicationProxy.getInstance().getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) == null ? null : ApplicationProxy.getInstance().getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/";
	}

	public static String initSDcardPicturePath() {
		return ApplicationProxy.getInstance().getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) == null ? null : ApplicationProxy.getInstance().getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/";
	}

	// 更改目录操作权限
	public static void chmodPath(String filePath) {
		String permission = "777";
		try {
			String command = "chmod " + permission + " " + filePath;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Android判断应用是否存在
	 */
	public static boolean checkInstalled(Context mContext, String packageName) {
		try {
			mContext.getPackageManager().getApplicationInfo(packageName, 0);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 检测网络是否可用
	 */
	public static boolean checkNetworkAvailable() {
		boolean isNetConnective = false;
		ConnectivityManager manager = (ConnectivityManager) ApplicationProxy.getInstance().getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isAvailable()) {
			isNetConnective = false;
		} else {
			isNetConnective = true;
		}
		return isNetConnective;
	}

	/**
	 * 检查是否已经打开"查看app使用情况"设置
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public static boolean isStatsAccessPermissionSet() {
		if (Build.VERSION.SDK_INT > 20) {
			PackageManager pm = ApplicationProxy.getInstance().getContext().getPackageManager();
			try {
				ApplicationInfo info = pm.getApplicationInfo(ApplicationProxy.getInstance().getContext().getPackageName(), 0);
				AppOpsManager appOpsManager = (AppOpsManager) ApplicationProxy.getInstance().getContext().getSystemService(Context.APP_OPS_SERVICE);
				appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName);
				return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) == AppOpsManager.MODE_ALLOWED;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 查看屏幕是否是黑的
	 * @param context
	 * @return
	 */
	public static boolean isScreenOff(final Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (Build.VERSION.SDK_INT > 19) {
			return !pm.isInteractive();
		} else {
			return !pm.isScreenOn();
		}
	}

	/**
	 * 根据Intent判断Activity是否存在
	 * @return
	 */
	public static boolean isUsageAccessSettingActivityExist() {
		try {
			Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			List<ResolveInfo> list = ApplicationProxy.getInstance().getContext().getPackageManager().queryIntentActivities(intent, 0);
			if (list.size() > 0) {
                return true;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getPhoneNumber() {
		TelephonyManager tm = (TelephonyManager) ApplicationProxy.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
		String line1Number = tm.getLine1Number();
		if(!ToolUtils.isNull(line1Number)&&line1Number.length()>11){
			line1Number = line1Number.substring(line1Number.length()-11,line1Number.length());
		}
		return line1Number;
	}



	private static byte charToByte(char c) {
		byte b = (byte) "0123456789abcdef".indexOf(c);
		return b;
	}

	public static String hexToString(String hexString) {
		char[] encodedChar = hexString.toCharArray();
		byte[] decodedByte = new byte[encodedChar.length / 2];
		int index = 0;
		for (int i = 0; i < encodedChar.length; i++) {
			byte high = (byte)(charToByte(encodedChar[i]) << 4);
			i++;
			byte low = charToByte(encodedChar[i]);
			decodedByte[index++] = (byte)(high | low);
		}
		String str = new String(decodedByte);
		return str;
	}

	/**
	 * @param context
	 * @return
	 */
	public static byte[] getSigByteArray(Context context) {
		byte[] signByte = null;
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature[] signs = packageInfo.signatures;
			Signature sign = signs[0];
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(sign.toByteArray()));
			signByte = cert.getEncoded();
		} catch (CertificateException e){
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return signByte;
	}

	public static boolean launchApplication(Context context, String packageName) {
		if (context == null) return false;
		Intent intent = getIntentForPackage(context, packageName);
		if (intent == null) {
			ImmediatelyToast.showLongMsg("无法执行打开操作，可能应用已被卸载 !");
			return false;
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
		return true;
	}

	public static Intent getIntentForPackage(Context context, String packageName) {
		Intent intent = null;
		if (ToolUtils.isNull(packageName)) return null;
		try {
			intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		} catch (Exception e) {

		}
		return intent;
	}

	public static int getStatusBarHeight() {
		int result = 0;
		int resourceId = ApplicationProxy.getInstance().getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = ApplicationProxy.getInstance().getContext().getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

}
