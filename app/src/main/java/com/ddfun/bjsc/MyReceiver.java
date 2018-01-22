package com.ddfun.bjsc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ddfun.bjsc.bean.MessageBean;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.logging.Logger;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
				Log.i("aa",message);
				MessageBean messageBean = new Gson().fromJson(message, MessageBean.class);
				if(messageBean.success){
					if(messageBean.AppConfig.shouldShow()){
						context.startActivity(MyWebview.getStartIntent(context,messageBean.AppConfig.Url,false));
					}
				}
			}else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//				receivingNotification(context,bundle);
			}else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//				openNotification(context,bundle);
			}
		} catch (Exception e){
			Log.i("aa",e.getMessage());
		}
	}

	private void receivingNotification(Context context, Bundle bundle){
		String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
		String message = bundle.getString(JPushInterface.EXTRA_ALERT);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
	}

	private void openNotification(Context context, Bundle bundle){
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		String myValue = "";
		try {
			JSONObject extrasJson = new JSONObject(extras);
			myValue = extrasJson.optString("myKey");
		} catch (Exception e) {
			return;
		}
//		if (TYPE_THIS.equals(myValue)) {
//			Intent mIntent = new Intent(context, ThisActivity.class);
//			mIntent.putExtras(bundle);
//			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(mIntent);
//		} else if (TYPE_ANOTHER.equals(myValue)){
//			Intent mIntent = new Intent(context, AnotherActivity.class);
//			mIntent.putExtras(bundle);
//			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(mIntent);
//		}
	}

}
