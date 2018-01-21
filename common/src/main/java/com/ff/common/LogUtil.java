package com.ff.common;

import android.os.Environment;
import android.util.Log;

import com.ff.common.application.ApplicationProxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class LogUtil {
	private static final String TAG = "aa";

	public static void i(String paramString) {
		if (!ApplicationProxy.getInstance().isRelease())
			i_whole(TAG, paramString);
	}

	public static void e(String paramString) {
		if (!ApplicationProxy.getInstance().isRelease())
			e_whole(TAG, paramString);
	}

	public static void e(Exception e) {
		if (!ApplicationProxy.getInstance().isRelease())
			LogUtil.e(Log.getStackTraceString(e));
	}

	public static void i(Exception e) {
		if (!ApplicationProxy.getInstance().isRelease())
			LogUtil.i(Log.getStackTraceString(e));
	}

	public static void i_to_sdcard(Exception e) {
		i_to_sdcard(Log.getStackTraceString(e));
	}

	public static void i_to_sdcard(String str) {
		File file = new File(Environment.getExternalStorageDirectory(), "ff.txt");
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file,true);
			fileWriter.write(str);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(fileWriter!=null)
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static void i_to_sdcard(String tag,Map<String,String> param,String result) {
		String aa="\n"+tag+" param start:\n";
		for(Map.Entry<String,String> entry :param.entrySet()){
			aa+=entry.getKey()+":"+entry.getValue()+"   ";
		}
		aa+="\n"+tag+" param end:\n";
		aa+="\n"+tag+" result start:"+result+"\n"+tag+" result end:";
		i_to_sdcard(aa);
	}

	public static void i(String tag, String paramString2) {
		if (!ApplicationProxy.getInstance().isRelease())
			i_whole(tag, paramString2);
	}

	private static void i_whole(String tag, String paramString2) {
		if(paramString2.length() > 4000) {
			for(int i=0;i<paramString2.length();i+=4000){
				if(i+4000<paramString2.length())
					Log.i(tag+i,paramString2.substring(i, i+4000));
				else
					Log.i(tag+i,paramString2.substring(i, paramString2.length()));
			}
		} else
			Log.i(tag,paramString2);
	}

	private static void e_whole(String tag, String paramString2) {
		if(paramString2.length() > 4000) {
			for(int i=0;i<paramString2.length();i+=4000){
				if(i+4000<paramString2.length())
					Log.e(tag+i,paramString2.substring(i, i+4000));
				else
					Log.e(tag+i,paramString2.substring(i, paramString2.length()));
			}
		} else
			Log.e(tag,paramString2);
	}

}