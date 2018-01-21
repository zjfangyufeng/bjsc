package com.ff.common;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.ff.common.application.ApplicationProxy;

/**
 * Created by fangyufeng on 2016/8/18.
 */
public class DisplayMetricsTool {
    DisplayMetrics mDisplayMetrics;
    static DisplayMetricsTool instance;

    public static DisplayMetricsTool getInstance() {
        if(instance ==null)
            instance = new DisplayMetricsTool();
        return instance;
    }

    public static boolean isOrientationPortrait(){
        return ApplicationProxy.getInstance().getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
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
            WindowManager wm = (WindowManager) ApplicationProxy.getInstance().getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        }
        return mDisplayMetrics;
    }

    public int getDPsize(float old) {
        return (int) (old * getDensity());
    }
}
