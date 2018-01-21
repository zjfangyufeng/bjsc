package com.ff.common.http;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.ff.common.application.ApplicationProxy;
import com.ff.common.model.TaskItem;
import com.ff.common.model.UserInfo;
import com.ff.common.notification.MyNotificationManager;
import com.ff.common.ImmediatelyToast;
import com.ff.common.TempToolUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class SendTaskReport {
	public static void sendLeftReadReport(final String task_id) {
		sendLeftReadReport(task_id, true);
	}

	public static void sendLeftReadShareReport(final String task_id) {
		sendLeftReadShareReport(task_id, true);
	}

	public static void sendLeftShareReport(final String task_id) {
		sendLeftShareReport(task_id, true);
	}

	public static void sendRightUnlockReport(final Object d) {
		sendRightUnlockReport(d, true);
	}

	public static void sendQTaskShareReport(final String task_id) {
		sendQTaskShareReport(task_id, true);
	}

	private static void sendLeftReadReport(final String task_id, final boolean cache) {
		ApplicationProxy.getInstance().getHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						Map<String, String> param = new HashMap();
						try {
							param.put("task_id", task_id);
							param.put("passcode", TempToolUtils.MD5(task_id));
							final JSONObject resultObj = MyHttpClient.executePost(
									MyHttpClient.getBASE_URL() + "/app/open/lvtask/" + UserInfo.getUserId(), param);
							if (resultObj != null) {
								if (resultObj.getString("code").equals("200")) {
									double price = Double.parseDouble(resultObj.opt("price").toString());
									MyNotificationManager.sendNotification(price + "");
									UserInfo.saveBalance(resultObj.opt("balance").toString());
								}
								remove(task_id, gCallback);
							} else if (cache) {
								add(new TaskReport(task_id, TaskReport.LEFT_READ, TaskReport.ZERO), gCallback);
								ImmediatelyToast.showShortMsg("网络异常，奖励稍后发放");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}, 5000);
	}

	private static void sendLeftReadShareReport(final String task_id, final boolean cache) {
		ApplicationProxy.getInstance().getHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						Map<String, String> param = new HashMap();
						try {
							param.put("task_id", task_id);
							param.put("passcode", TempToolUtils.MD5(task_id));
							final JSONObject resultObj = MyHttpClient.executePost(
									MyHttpClient.getBASE_URL() + "/app/open/zhuan/", param);
							if (resultObj != null) {
								if (resultObj.getString("code").equals("200")) {
									double price = Double.parseDouble(resultObj.opt("price").toString());
									MyNotificationManager.sendNotification(price + "");
									UserInfo.saveBalance(resultObj.opt("balance").toString());
								}
								remove(task_id, gCallback);
							} else if (cache) {
								add(new TaskReport(task_id, TaskReport.LEFT_READ_SHARE, TaskReport.ZERO), gCallback);
								ImmediatelyToast.showShortMsg("网络异常，奖励稍后发放");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}, 20000);
	}

	private static void sendLeftShareReport(final String task_id, final boolean cache) {
				ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						Map<String, String> param = new HashMap();
						try {
							param.put("task_id", task_id);
							param.put("passcode", TempToolUtils.MD5(task_id));
							final JSONObject resultObj = MyHttpClient.executePost(
									MyHttpClient.getBASE_URL() + "/app/open/lshare/" + UserInfo.getUserId(), param);
							if (resultObj != null) {
								if (resultObj.getString("code").equals("200")) {
									double price = Double.parseDouble(resultObj.opt("price").toString());
									MyNotificationManager.sendNotification(price + "");
									UserInfo.saveBalance(resultObj.opt("balance").toString());
								}
								remove(task_id, gCallback);
							} else if (cache) {
								add(new TaskReport(task_id, TaskReport.LEFT_SHARE, TaskReport.ZERO), gCallback);
								ImmediatelyToast.showShortMsg("网络异常，奖励稍后发放");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}

	public static void sendLockScreenViewReport(final TaskItem item) {
		ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				Map<String, String> param = new HashMap();
				try {
					param.put("task_id", item.id);
					param.put("passcode", TempToolUtils.MD5(item.id));
					MyHttpClient.executePost(
							MyHttpClient.getBASE_URL() + "/app/catch/showTaskCount/" + UserInfo.getUserId(), param);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	private static void sendRightUnlockReport(final Object d, final boolean cache) {
		ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				Map<String, String> param = new HashMap();
				try {
					double price = (double) d;
					param.put("price", "" + price);
					final JSONObject resultObj = MyHttpClient.executePost(
							MyHttpClient.getBASE_URL() + "/app/open/rslide/" + UserInfo.getUserId(), param);
					if (resultObj != null) {
						if (resultObj.getString("code").equals("200")) {
							double price1 = Double.parseDouble(resultObj.opt("price").toString());
							MyNotificationManager.sendNotification(price1 + "");
							UserInfo.saveBalance(resultObj.opt("balance").toString());
						}
						remove(TaskReport.RIGHT_UNLOCK_ID, gCallback);
					} else if (cache) {
						add(new TaskReport(TaskReport.RIGHT_UNLOCK_ID, TaskReport.RIGHT_UNLOCK, price), gCallback);
						ImmediatelyToast.showShortMsg("网络异常，奖励稍后发放");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception ignored) {
				}
			}
		});
	}


	private static void sendQTaskShareReport(final String task_id, final boolean cache) {
		ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				Map<String, String> param = new HashMap();
				try {
					param.put("task_id", task_id);
					param.put("passcode", TempToolUtils.MD5(task_id));
					final JSONObject resultObj = MyHttpClient.executePost(
							MyHttpClient.getBASE_URL() + "/app/open/qshare/" + UserInfo.getUserId(), param);
					if (resultObj != null) {
						if (resultObj.getString("code").equals("200")) {
							double price = Double.parseDouble(resultObj.opt("price").toString());
							MyNotificationManager.sendNotification(String.valueOf(price));
							UserInfo.saveBalance(resultObj.opt("balance").toString());
						}
						remove(task_id, gCallback);
					} else if (cache) {
						add(new TaskReport(task_id, TaskReport.Q_TASK_SHARE, TaskReport.ZERO), gCallback);
						ImmediatelyToast.showShortMsg("网络异常，奖励稍后发放");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	// Storing the failed report to local.
	static class TaskReport {
		String taskId; // "0" means right unlock.
		int type;
		double price; // For right unlock.

		private final static String ID   	= "id";
		private final static String TYPE 	= "type";
		private final static String PRICE 	= "price";

		// Id for right unlock.
		public final static String RIGHT_UNLOCK_ID = "0";
		// Price for other tasks in addition to right unlock.
		public final static double ZERO = 0.00;

		// Type
		public final static int LEFT_READ 		= 0;
		public final static int LEFT_READ_SHARE = 1;
		public final static int LEFT_SHARE 		= 2;
		public final static int RIGHT_UNLOCK 	= 3;
		public final static int Q_TASK_SHARE 	= 4;

		public TaskReport(String taskId, int type, double price) {
			this.taskId = taskId;
			this.type = type;
			this.price = price;
		}

		@Override
		public String toString() {
			try {
				JSONObject json = new JSONObject();
				json.put(ID, taskId);
				json.put(TYPE, type);
				json.put(PRICE, price);
				return json.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		public static TaskReport fromString(String string) {
			try {
				JSONObject json = new JSONObject(string);
				return new TaskReport(json.getString(TaskReport.ID), json.getInt(TaskReport.TYPE), json.getDouble(TaskReport.PRICE));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private final static boolean DEBUG = false;
	private final static String TAG = "SendTaskReport";
	private final static String TASK_REPORT = "task_report";
	private final static Set<TaskReport> set = Collections.newSetFromMap(new HashMap<TaskReport, Boolean>());
	interface CallBack {
		void onAdd(TaskReport taskReport);
		void onDelete(TaskReport taskReport);
	}

	private final static CallBack gCallback = new CallBack() {
		@Override
		public void onAdd(TaskReport taskReport) {
			set.add(taskReport);
			if (DEBUG) {
				Log.d(TAG, "Add " + taskReport);
			}
		}

		@Override
		public void onDelete(TaskReport taskReport) {
			set.remove(taskReport);
			if (DEBUG) {
				Log.d(TAG, "Delete " + taskReport);
			}
		}
	};

	public static void upload() {
		ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				Iterator iterator = set.iterator();
				while (iterator.hasNext()) {
					TaskReport taskReport = (TaskReport) iterator.next();
					switch (taskReport.type) {
						case TaskReport.LEFT_READ:
							sendLeftReadReport(taskReport.taskId, false);
							break;
						case TaskReport.LEFT_READ_SHARE:
							sendLeftReadShareReport(taskReport.taskId, false);
							break;
						case TaskReport.LEFT_SHARE:
							sendLeftShareReport(taskReport.taskId, false);
							break;
						case TaskReport.RIGHT_UNLOCK:
							sendRightUnlockReport(taskReport.price, false);
							break;
						case TaskReport.Q_TASK_SHARE:
							sendQTaskShareReport(taskReport.taskId, false);
							break;
						default:
							break;
					}
				}
			}
		});
	}

	private static void add(TaskReport report, CallBack callBack) {
		String string = PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getString(TASK_REPORT, null);
		JSONObject json;
		try {
			if (TextUtils.isEmpty(string)) {
				json = new JSONObject();
			} else {
				json = new JSONObject(string);
			}
			json.put(report.taskId, report.toString());
			PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).edit().putString(TASK_REPORT, json.toString()).commit();
			callBack.onAdd(report);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private static void remove(String taskId, CallBack callBack) {
		String string = PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getString(TASK_REPORT, null);
		if (!TextUtils.isEmpty(string)) {
			try {
				JSONObject json = new JSONObject(string);
				Object obj = json.remove(taskId);
				if (obj != null) {
					PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).edit().putString(TASK_REPORT, json.toString()).commit();
					callBack.onDelete(TaskReport.fromString((String)obj));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	static {
		ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				String string = PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getString(TASK_REPORT, null);
				if (!TextUtils.isEmpty(string)) {
					try {
						JSONObject json = new JSONObject(string);
						for (Iterator<String> keys = json.keys(); keys.hasNext(); ) {
							String key = keys.next();
							String str = json.getString(key);
							TaskReport taskReport = TaskReport.fromString(str);
							if (taskReport != null) {
								if (DEBUG) {
									Log.d(TAG, "Init: " + taskReport);
								}
								set.add(taskReport);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
