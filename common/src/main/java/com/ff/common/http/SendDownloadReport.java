package com.ff.common.http;

import com.ff.common.ImmediatelyToast;
import com.ff.common.TempToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.download.FileDB;
import com.ff.common.download.MyDownloadManager;
import com.ff.common.model.TaskType;
import com.ff.common.model.UserInfo;
import com.ff.common.notification.MyNotificationManager;
import com.ff.common.utils.TaskStatusManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SendDownloadReport {
	/*点击下载*/
	public static final String TYPE_CLICK_DOWNLOAD = "0";
	/*下载完成*/
	public static final String TYPE_DOWNLOAD_COMPLETE = "1";
	/*安装完成*/
	public static final String TYPE_INSTALLED = "2";
	/*打开*/
	public static final String TYPE_OPEN = "3";

	public static void sendDownloadReport(final String task_id, final String appName, final String from,
										  final String status, final OnDownloadReportResult callback) {
		ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				Map<String, String> param = new HashMap();
				try {
					param.put("task_id", task_id);
					param.put("type", from);
					param.put("status", status);
					param.put("passcode", TempToolUtils.MD5(task_id));
					final JSONObject resultObj = MyHttpClient.executePost(
							MyHttpClient.getBASE_URL() + getUrl(from) + UserInfo.getUserId(), param);
					if (resultObj != null) {
						if (resultObj.getString("code").equals("200")) {
							try {
								double price = Double.parseDouble(resultObj.opt("price").toString());
								if (price > 0) {
									MyNotificationManager.sendNotification(price + "");
									UserInfo.saveBalance(resultObj.opt("balance").toString());
								}
							} catch (Exception e) {
							}
						}
						if(callback != null) {
							callback.onSuccess();
						}
					} else {
						if(callback != null){
							callback.onFail();
						} else {
							FileDB.getInstance().insertDownloadReportLog(task_id, appName, from, status);
							if (TYPE_OPEN.equals(status)) {
								ImmediatelyToast.showShortMsg("网络异常，奖励稍后发放");
							}
						}
					}
				} catch (Exception e) {
				}
				try {
					UmengEvent.sendUmengDownloadClick(status, from, task_id,appName);
					MyDownloadManager.getInstance().onStatusChanged(task_id, status);
					if (Integer.valueOf(status) < Integer.valueOf(TYPE_OPEN)) {
						TaskStatusManager.getInstance().store(task_id, status);
					}
				} catch (Exception e) {
				}
			}
		});
	}

	public static String getUrl(String from){
		if(TaskType.SCREENSHOT_TASK.equals(from) || TaskType.SCREENSHOT_GAME_TASK.equals(from) || TaskType.INVESTMENT_TASK.equals(from)|| TaskType.SCREENSHOT_CPL_TASK.equals(from)){
			return "/app/open/ptask/";
		}
		return "/app/open/download/";
	}

	public interface OnDownloadReportResult{
		void onSuccess();
		void onFail();
	}

	/* Notice: It doesn't matter with network. */
	public interface DownloadTaskStatusObserver {
		void onStatusChanged(String taskId, String status);
	}

}
