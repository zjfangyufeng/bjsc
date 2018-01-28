package com.ddfun.bjsc.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.application.IApplication;
import com.ff.common.http.MyHttpClient;
import com.ff.common.model.UserInfo;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApp extends Application implements IApplication {

	public static Handler handler = new Handler();
	public ExecutorService coreThreadPool = Executors.newCachedThreadPool();
	private static MyApp instance;

	DisplayMetrics mDisplayMetrics;

	public static MyApp getInstance() {
		return instance;
	}

	public int getHeightPixels() {
		return getDisplayMetrics().heightPixels;
	}

	public Float getDensity() {
		return getDisplayMetrics().density;
	}

	public int getWidthPixels() {
		return getDisplayMetrics().widthPixels;
	}

	public synchronized DisplayMetrics getDisplayMetrics(){
		if(mDisplayMetrics == null){
			mDisplayMetrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
		}
		return mDisplayMetrics;
	}

	public int getDPsize(int old) {
		return (int) (old * getDensity());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		ApplicationProxy.initInstance(this);

//		if (!BuildConfig.isRelease) {
//			ARouter.openLog();
//			ARouter.openDebug();
//			ARouter.printStackTrace();
//		}
		ARouter.init(this);
	}

	@Override
	public void loginInvalid(){
	}

	@Override
	public boolean isRelease() {
		return true;
	}

	@Override
	public Handler getHandler() {
		return handler;
	}

	@Override
	public ExecutorService getThreadPool() {
		return coreThreadPool;
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public String getBASE_URL() {
		return "";
	}

	@Override
	public String getIMG_BASE_URL() {
		return "";
	}

	@Override
	public String getInvite_code() {
		return UserInfo.getUserInfo().getInvite_code();
	}

	@Override
	public String getUserId() {
		if(UserInfo.getUserInfo()!=null)
			return UserInfo.getUserInfo().getUser_id();
		return null;
	}

	@Override
	public OkHttpClient getOkHttpClient() {
		return MyHttpClient.getOkHttpClient();
	}
}
