package com.ff.common.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.ff.common.ReflectNavigation;
import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.mvp_v.SettingsModel;
import com.ff.common_tools.R;

/**
 * Created by fangyufeng on 2015/10/27.
 */
public class MyNotificationManager {
    public static final int MyNotificationId = 151027;
    static NotificationManager manager;

    public static NotificationManager getManager(){
        if(manager ==null)
            manager = (NotificationManager) ApplicationProxy.getInstance().getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    public static void sendNotification(final String money){
        if(SettingsModel.getMessageNotificationSwitch()){
            if(ToolUtils.isNull(money)||money.equals("0")||money.equals("0.0")||money.equals("0.00")){
                return;
            }
            String ticker = "您获得了" + money + "元奖励";
            sendNotificationWithTicker(ticker);
        }
    }

    public static void sendNotificationWithTicker(final String ticker){
        Intent navigationIntent = ReflectNavigation.getNavigationIntent(ApplicationProxy.getInstance().getContext(), "com.ddfun.activity.Welcome");
        PendingIntent pendingIntent = PendingIntent.getActivity(ApplicationProxy.getInstance().getContext(), 1,
                navigationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        sendNotificationWithTicker("福彩3D",ticker,pendingIntent);
    }

    public static void sendNotificationWithTicker(String title,String ticker,PendingIntent pendingIntent){
        RemoteViews remoteViews = new RemoteViews(ApplicationProxy.getInstance().getContext().getPackageName(), R.layout.my_notification_layout);
        remoteViews.setTextViewText(R.id.tv_title,title);
        remoteViews.setTextViewText(R.id.tv_sub_title,ticker);
        Notification notification = new NotificationCompat.Builder(ApplicationProxy.getInstance().getContext()).setSmallIcon(R.mipmap.push)
                .setContent(remoteViews).setTicker(ticker).setVisibility(Notification.VISIBILITY_PUBLIC).build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.contentIntent = pendingIntent;
        getManager().notify(MyNotificationId, notification);
    }

}
