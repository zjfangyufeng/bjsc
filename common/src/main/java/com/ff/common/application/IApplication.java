package com.ff.common.application;

import android.content.Context;
import android.os.Handler;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.ExecutorService;

/**
 * Created by fangyufeng on 2016/8/18.
 */
public interface IApplication {
    boolean isRelease();
    Handler getHandler();
    ExecutorService getThreadPool();
    Context getContext();
    String getIMG_BASE_URL();
    String getInvite_code();
    String getUserId();
    OkHttpClient getOkHttpClient();

    void loginInvalid();
    String getBASE_URL();
}
