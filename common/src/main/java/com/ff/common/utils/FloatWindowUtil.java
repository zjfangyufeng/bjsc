package com.ff.common.utils;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.ff.common.LogUtil;
import com.ff.common.application.ApplicationProxy;
import com.ff.common_tools.R;

import java.lang.reflect.Method;

/**
 * Created by Ace on 2017/11/23.
 */

public class FloatWindowUtil {

    public static OpenFloatWindowDialog showOpenQuestionTaskFloatWindowDialog(final Context context, String title, String subTitle) {
        final OpenFloatWindowDialog ad = new OpenFloatWindowDialog(context);
        ad.show();
        View v = View.inflate(context,R.layout.dialog_open_float_window,null);
        TextView tv_title = (TextView) v.findViewById(R.id.tv_title);
        tv_title.setText(title);
        TextView msg_tv = (TextView) v.findViewById(R.id.msg_tv);
        msg_tv.setText(subTitle);
        TextView confirm_btn = (TextView) v.findViewById(R.id.confirm_btn);
        confirm_btn.setText("去开启");
        v.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        context.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                    } catch (Exception e) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + ApplicationProxy.getInstance().getContext().getPackageName()));
                            context.startActivity(intent);
                        } catch (Exception ee) {
                        }
                    }
                } else {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + ApplicationProxy.getInstance().getContext().getPackageName()));
                        context.startActivity(intent);
                    } catch (Exception ee) {
                    }
                }
                ad.dismiss();
            }
        });
        v.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
        ad.setContentView(v);
        return ad;
    }

    public static class OpenFloatWindowDialog extends AlertDialog{

        View v;

        protected OpenFloatWindowDialog(Context context) {
            super(context);
        }

        @Override
        public void setContentView(@NonNull View view) {
            super.setContentView(view);
            v = view;
        }

        public void setOnCancel(@Nullable View.OnClickListener listener) {
            v.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        }

        public View getConfirmBtn() {
            return v.findViewById(R.id.confirm_btn);
        }
    }

    public static boolean checkFloatWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(ApplicationProxy.getInstance().getContext());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // AppOpsManager添加于API 19
            return checkOps();
        } else {
            //4.4以下一般都可以直接添加悬浮窗
            return true;
        }
    }

    private static boolean checkOps() {
        try {
            Object object = ApplicationProxy.getInstance().getContext().getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = ApplicationProxy.getInstance().getContext().getPackageName();
            int m = (Integer) method.invoke(object, arrayOfObject1);
            LogUtil.i("appops "+m);
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ignore) {
        }
        return false;
    }

}
