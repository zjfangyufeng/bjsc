package com.ff.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.ff.common.application.ApplicationProxy;
import com.ff.common.model.InstalledApp;
import com.ff.common.model.UserEnvironment;

import java.util.ArrayList;
import java.util.List;


public class PhoneUtils {

	public static UserEnvironment getUserEnvironment(String type){
		return new UserEnvironment(type);
	}

	public static String getMacAddress() {
		WifiInfo info = null;
		try {
			WifiManager wifi = (WifiManager) ApplicationProxy.getInstance().getContext().getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
			info = wifi.getConnectionInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info == null ? "" : info.getMacAddress();
	}

	public static String getPhoneModel() {
		return android.os.Build.MODEL;
	}

	public static String getSystemVer() {
		return Build.VERSION.RELEASE;
	}

	public static String getManufacturer() {
		return android.os.Build.MANUFACTURER;
	}

	public static String getImei() {
		String imei;
		if(!ApplicationProxy.getInstance().isRelease() && (imei = PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getString("IMEI",null))!=null){
			return imei;
		}else {
			String imeiStr = "";
			try {
				imeiStr = ((TelephonyManager) ApplicationProxy.getInstance().getContext()
						.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return imeiStr == null ? "" : imeiStr;
		}
	}

	public static String getWholeImei() {
		String imeiStr = null,imeiStr1= ",",imeiStr2= ",",imeiStr3= ",",imeiStr4= ",";
		try {
			imeiStr = ((TelephonyManager) ApplicationProxy.getInstance().getContext()
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			imeiStr1 += ((TelephonyManager) ApplicationProxy.getInstance().getContext()
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId(TelephonyManager.PHONE_TYPE_NONE);
			imeiStr2 += ((TelephonyManager) ApplicationProxy.getInstance().getContext()
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId(TelephonyManager.PHONE_TYPE_GSM);
			imeiStr3 += ((TelephonyManager) ApplicationProxy.getInstance().getContext()
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId(TelephonyManager.PHONE_TYPE_CDMA);
			imeiStr4 += ((TelephonyManager) ApplicationProxy.getInstance().getContext()
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId(TelephonyManager.PHONE_TYPE_SIP);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return imeiStr+=imeiStr1+=imeiStr2+=imeiStr3+=imeiStr4;
	}

	public static String getImsi() {
		String imsiStr = null;
		try {
			imsiStr = ((TelephonyManager) ApplicationProxy.getInstance().getContext()
                    .getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imsiStr == null ? "" : imsiStr;
	}

	public static String getIccid() {
		String iccid = "";
		try {
			TelephonyManager telephonyManager = ((TelephonyManager) ApplicationProxy.getInstance().getContext()
                    .getSystemService(Context.TELEPHONY_SERVICE));
			iccid = telephonyManager.getSimSerialNumber();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iccid == null ? "" : iccid;
	}

	public static String getBSSID() {
		WifiInfo info = null;
		try {
			WifiManager wifi = (WifiManager)ApplicationProxy.getInstance().getContext().getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
			info = wifi.getConnectionInfo();
		} catch (Exception e) {
		}
		return info == null ? "" : info.getBSSID()+"";
	}

	public static String getSSID() {
		WifiInfo info = null;
		try {
			WifiManager wifi = (WifiManager) ApplicationProxy.getInstance().getContext().getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
			info = wifi.getConnectionInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info == null ? "" : info.getSSID()+"";
	}

//	public static String getLocation() {
//		TelephonyManager  telephonyManager = (TelephonyManager) MyApp.getInstance()
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		CdmaCellLocation cellLocation = (CdmaCellLocation) telephonyManager.getCellLocation();
//		cellLocation.getBaseStationLatitude();
//		return "";
//	}

//	public static String getLocalIpAddress(){
//		try{
//			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
//				NetworkInterface intf = en.nextElement();
//				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
//					InetAddress inetAddress = enumIpAddr.nextElement();
//					if (!inetAddress.isLoopbackAddress()){
//						return inetAddress.getHostAddress().toString();
//					}
//				}
//			}
//		}catch (SocketException ex){
//		}
//		return null;
//	}

	public static List<String> getMemlist() {
		List<String> list =new ArrayList<>();
		ActivityManager am = (ActivityManager) ApplicationProxy.getInstance().getContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo:runningAppProcesses){
			String processName = runningAppProcessInfo.processName;
			String[] pkgList = runningAppProcessInfo.pkgList;
			if(pkgList!=null&&pkgList.length>0){
				boolean isSystemApplication = ToolUtils.isSystemApplication(pkgList[0]);
				if(!isSystemApplication){
					list.add(processName);
				}
			}
		}
		return list;
	}

	public static List<InstalledApp> getInstalledlist() {
		List<InstalledApp> list =new ArrayList<>();
		List<PackageInfo> packages = ApplicationProxy.getInstance().getContext().getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				String label = (String) packageInfo.applicationInfo.loadLabel(ApplicationProxy.getInstance().getContext().getPackageManager());
				list.add(new InstalledApp(packageInfo.packageName,label,packageInfo.firstInstallTime));
			}
		}
		return list;
	}

}

