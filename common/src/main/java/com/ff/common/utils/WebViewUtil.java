package com.ff.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ff.common.ImmediatelyToast;
import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.model.TaskType;
import com.ff.common.model.UserInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressLint("JavascriptInterface")
public class WebViewUtil {

	// public static void initWebview(WebView mWebView) {
	// WebSettings settings = mWebView.getSettings();
	// // 设置字符集编码
	// settings.setDefaultTextEncodingName("UTF-8");
	// // 开启JavaScript支持
	// settings.setJavaScriptEnabled(true);
	// settings.setSupportZoom(true);// 支持缩放
	// settings.setBuiltInZoomControls(true);// 启用内置缩放装置
	// settings.setJavaScriptCanOpenWindowsAutomatically(true);
	// settings.setDomStorageEnabled(true);
	// settings.setRenderPriority(RenderPriority.HIGH);
	// mWebView.setWebChromeClient(new WebChromeClient());
	// mWebView.setWebViewClient(new WebViewClient() {
	// @Override
	// public boolean shouldOverrideUrlLoading(WebView view, String url) {
	// view.loadUrl(url);
	// return true;
	// }
	//
	// });
	// }

	public static void initWebview(Context context, WebView mWebView, final ProgressBar pb) {
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
		mWebView.setDownloadListener(new MyWebViewDownLoadListener(context));
		mWebView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				pb.setProgress(newProgress);
			}
		});
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("wtai://wp/mc;")) {//http://wpa.qq.com/msgrd?v=3&uin=1536257130&site=oicqzone.com&menu=yes
					try {//mqqwpa://im/chat?chat_type=wpa&uin=1536257130&version=1&src_type=web&web_src=null
						String replace = url.replace("wtai://wp/mc;", "tel:");
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(replace));
						view.getContext().startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (url.startsWith("mqqwpa")|| url.startsWith("tencent://")
						|| url.startsWith("mailto") || url.startsWith("http://wapp.baidu.com")) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						view.getContext().startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (url.contains("www.doudou.com/?index")) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					view.getContext().startActivity(intent);
				} else if (url.startsWith("tbfrs://")) {//百度贴吧
				} else if (url.startsWith("wvjbscheme://")) {
				} else if (url.startsWith("http:") || url.startsWith("https:")) {
					view.loadUrl(url);
				}
				return true;
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
		});

		try {
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setDomStorageEnabled(true);
			mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			mWebView.getSettings().setUseWideViewPort(true);
		} catch (Exception e) {
		}
	}

	/*getUserId.onAndroidClick()*/
	public static void addGetUserIdJSInterface(WebView mWebView) {
		mWebView.addJavascriptInterface(new GetUserId(), "getUserId");
	}

	public static class GetUserId extends Object{
		@JavascriptInterface
		public String onAndroidClick() {
			return UserInfo.getUserId();
		}
	}

	public static void addSetBalanceJSInterface(Activity activity,WebView mWebView) {
		mWebView.addJavascriptInterface(new SetBalance(activity), "setBalance");
	}

	public static class SetBalance extends Object{
		Activity activity;

		public SetBalance(Activity c) {
			this.activity = c;
		}

		@JavascriptInterface
		public void onAndroidClick(String balance) {
			UserInfo.getUserInfo().saveBalance(balance);
		}

		@JavascriptInterface
		public void startDownloadTaskDetailActivityJSInterface(String taskId) {
			ARouter.getInstance().build("/app/DownloadTaskActivity").withString("id",taskId).withString("from",TaskType.DGET).navigation(activity, new NavigationCallback() {
				@Override
				public void onFound(Postcard postcard) {
				}
				@Override
				public void onLost(Postcard postcard) {
					ImmediatelyToast.showLongMsg("此版本暂不支持");
				}
				@Override
				public void onArrival(Postcard postcard) {
				}
				@Override
				public void onInterrupt(Postcard postcard) {
				}
			});
//			activity.startActivity(DownloadTaskActivity.getLaunchIntent(activity, taskId, TaskType.DGET));
		}

		@JavascriptInterface
		public void startQuestionActivityJSInterface(String taskId) {
			ARouter.getInstance().build("/app/QuestionTaskActivity").withString("id",taskId).withString("from",TaskType.QGET).navigation(activity, new NavigationCallback() {
				@Override
				public void onFound(Postcard postcard) {
				}
				@Override
				public void onLost(Postcard postcard) {
					ImmediatelyToast.showLongMsg("此版本暂不支持");
				}
				@Override
				public void onArrival(Postcard postcard) {
				}
				@Override
				public void onInterrupt(Postcard postcard) {
				}
			});
//			activity.startActivity(QuestionTaskActivity.getLaunchIntent(activity, taskId, TaskType.QGET));
		}

		@JavascriptInterface
		public void startTaskManageActivityJSInterface() {
			ARouter.getInstance().build("/app/TaskManageActivity").navigation(activity, new NavigationCallback() {
				@Override
				public void onFound(Postcard postcard) {
				}
				@Override
				public void onLost(Postcard postcard) {
					ImmediatelyToast.showLongMsg("此版本暂不支持");
				}
				@Override
				public void onArrival(Postcard postcard) {
				}
				@Override
				public void onInterrupt(Postcard postcard) {
				}
			});
//			activity.startActivity(TaskManageActivity.getLaunchIntent(activity));
		}

		@JavascriptInterface
		public void setReloadOnResumeJSInterface() {
			try {
				Field webviewrefresh = activity.getClass().getField("WEBVIEWREFRESH");
				webviewrefresh.set(activity,true);
//				if(activity instanceof  MyWebview){
//					((MyWebview)activity).WEBVIEWREFRESH = true;
//				}
			} catch (Exception e) {
			}
		}

		@JavascriptInterface
		public void openInviteActivityJSInterface() {
			ARouter.getInstance().build("/app/InviteActivity").withString("from","notmaintabactivity").navigation(activity, new NavigationCallback() {
				@Override
				public void onFound(Postcard postcard) {
				}
				@Override
				public void onLost(Postcard postcard) {
					ImmediatelyToast.showLongMsg("此版本暂不支持");
				}
				@Override
				public void onArrival(Postcard postcard) {
				}
				@Override
				public void onInterrupt(Postcard postcard) {
				}
			});
//			activity.startActivity(InviteActivity.getLaunchIntent(activity,InviteActivity.FROMNOTMAINTABACTIVITY));
		}

		@JavascriptInterface
		public void openLockscreenActivityJSInterface() {
			ARouter.getInstance().build("/app/LockScreenActivity").withBoolean("useCache",false).navigation(activity, new NavigationCallback() {
				@Override
				public void onFound(Postcard postcard) {
				}
				@Override
				public void onLost(Postcard postcard) {
					ImmediatelyToast.showLongMsg("此版本暂不支持");
				}
				@Override
				public void onArrival(Postcard postcard) {
				}
				@Override
				public void onInterrupt(Postcard postcard) {
				}
			});
//			activity.startActivity(LockScreenActivity.getLaunchIntent(activity, false));
		}

		@JavascriptInterface
		public void openScreenshotTaskListActivityJSInterface() {
			ARouter.getInstance().build("/app/ScreenshotTaskActivity").navigation(activity, new NavigationCallback() {
				@Override
				public void onFound(Postcard postcard) {
				}
				@Override
				public void onLost(Postcard postcard) {
					ImmediatelyToast.showLongMsg("此版本暂不支持");
				}
				@Override
				public void onArrival(Postcard postcard) {
				}
				@Override
				public void onInterrupt(Postcard postcard) {
				}
			});
//			activity.startActivity(new Intent(activity, ScreenshotTaskActivity.class));
		}

		@JavascriptInterface
		public void openScreenshotGameTaskListActivityJSInterface() {
			ARouter.getInstance().build("/app/ScreenshotGameTaskActivity").navigation(activity, new NavigationCallback() {
				@Override
				public void onFound(Postcard postcard) {
				}
				@Override
				public void onLost(Postcard postcard) {
					ImmediatelyToast.showLongMsg("此版本暂不支持");
				}
				@Override
				public void onArrival(Postcard postcard) {
				}
				@Override
				public void onInterrupt(Postcard postcard) {
				}
			});
//			activity.startActivity(new Intent(activity, ScreenshotGameTaskActivity.class));
		}

		@JavascriptInterface
		public void openTransferOutToWXActivityJSInterface() {
			ARouter.getInstance().build("/app/TransferOutToWXActivity").navigation(activity, new NavigationCallback() {
				@Override
				public void onFound(Postcard postcard) {
				}
				@Override
				public void onLost(Postcard postcard) {
					ImmediatelyToast.showLongMsg("此版本暂不支持");
				}
				@Override
				public void onArrival(Postcard postcard) {
				}
				@Override
				public void onInterrupt(Postcard postcard) {
				}
			});
//			activity.startActivity(new Intent(activity, TransferOutToWXActivity.class));
		}

		@JavascriptInterface
		public void shareToWXJSInterface(String share_msg) {
			try {
				Class<?> aClass = Class.forName("com.ddfun.wxapi.WXUtil");
				Method method = aClass.getMethod("shareUrlType",Activity.class, int.class, String.class, String.class, String.class, String.class, String.class);
				method.invoke(null,activity,1,share_msg,null,"WebViewUtil",null,null);
			} catch (Exception e) {
			}
//			WXUtil.shareUrlType( activity,1,share_msg,null,"WebViewUtil",null,null);
		}

		@JavascriptInterface
		public void copyInterface(String s) {
			ClipboardManager clipboardManager = (ClipboardManager) ApplicationProxy.getInstance().getContext().getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setPrimaryClip(ClipData.newPlainText("s",s));
			ImmediatelyToast.showLongMsg("复制成功");
		}

		@JavascriptInterface
		public void openAPPInterface(String packageName) {
			ToolUtils.launchApplication(activity,packageName);
		}
	}

}

class MyWebViewDownLoadListener implements DownloadListener {
	Context context;

	public MyWebViewDownLoadListener(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		Uri uri = Uri.parse(url);
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(intent);
		} catch (Exception e) {
			ImmediatelyToast.showLongMsg("无法下载，请确认下载链接正确");
		}
	}
}
