package com.ff.common;

import android.widget.Toast;

import com.ff.common.application.ApplicationProxy;

/**
 * Created by fangyufeng on 2015/12/18.
 */
public class ImmediatelyToast {

    private static Toast toast;

    public static void showmsg(final String msg, final int time) {
        ApplicationProxy.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(ApplicationProxy.getInstance().getContext(), msg, time);
                } else {
                    toast.setText(msg);
                }
                toast.show();
            }
        });
    }


    public static void showShortMsg(final String msg) {
        showmsg(msg, Toast.LENGTH_SHORT);
    }

    public static void showLongMsg(final String msg) {
        showmsg(msg, Toast.LENGTH_LONG);
    }
}
