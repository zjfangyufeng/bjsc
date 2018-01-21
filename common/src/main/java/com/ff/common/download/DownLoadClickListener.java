package com.ff.common.download;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ff.common.LogUtil;
import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.model.DownloadTaskBean;
import com.ff.common.model.TaskType;
import com.ff.common.service.GetTopTaskService;
import com.ff.common_tools.R;

import java.io.File;

public class DownLoadClickListener implements OnClickListener {

    public static final int CLICK_INTERVAL = 500;
    public long last_click_time;
    String from;
    ClickObserver callback = null;

    public void removeCallback() {
        callback = null;
    }

    public interface ClickObserver {
        /**
         * @return true, if callback want to consume this event completely.
         */
        boolean prepare();
        boolean normalClick();
        boolean launchClick();

    }

    public DownLoadClickListener(String from) {
        super();
        this.from = from;
    }

    public DownLoadClickListener(String from, ClickObserver callback) {
        this(from);
        this.callback = callback;
    }

    @Override
    public void onClick(final View v) {
//        if(!checkClickInterval())return;
//        last_click_time = System.currentTimeMillis();
        int i = v.getId();
        if (i == R.id.download_btn) {
            if (callback != null && callback.prepare()) {
                return;
            }
            final DownloadTaskBean downloadTaskBean = (DownloadTaskBean) v.getTag();
            TextView tv = (TextView) v;
            String state = tv.getText().toString();
            if (state.equals(MyConstants.NARMAL) || state.equals(MyConstants.NARMAL2) || state.equals(MyConstants.NARMAL3)) {
                if (callback == null || !callback.normalClick()) {
                    MyDownloadManager.getInstance().requestDownload(
                            downloadTaskBean, from);
                }
            } else if (state.equals(MyConstants.PAUSE) || state.endsWith("%")) {
                MyDownloadManager.getInstance().pauseDownload(
                        downloadTaskBean.package_name);
            } else if (state.equals(MyConstants.CONTINUE)) {
                MyDownloadManager.getInstance().requestDownload(
                        downloadTaskBean, from);
            } else if (state.equals(MyConstants.COMPLETE)) {
                DownLoadFile df = MyDownloadManager.getInstance()
                        .getDownLoadFileTask(downloadTaskBean.package_name);
                if (df != null && df.getDownLoadPath() != null && new File(df.getDownLoadPath()).exists()) {
                    ToolUtils.installApk(df.getDownLoadPath());
                } else {
                    showReDownloadDialog(v.getContext(), downloadTaskBean, "文件不存在，是否重新下载?", from, callback);
                }
            } else if (state.equals(MyConstants.OPEN)) {
                if (callback == null || !callback.launchClick()) {
                    performLaunch(v.getContext(), downloadTaskBean, from);
                }
            } else if (state.equals(MyConstants.RETRY)) {
                MyDownloadManager.getInstance().requestDownload(
                        downloadTaskBean, from);
            }

        }
    }

    private boolean checkClickInterval() {
        return System.currentTimeMillis()-last_click_time>CLICK_INTERVAL;
    }

    public static void performLaunch(Context context,DownloadTaskBean downloadTaskBean,String from){
        final Intent intent = ApplicationProxy.getInstance().getContext().getPackageManager().getLaunchIntentForPackage(downloadTaskBean.package_name);
        if (intent == null) {
            showReDownloadDialog(context, downloadTaskBean, "无法执行打开操作，可能应用已被卸载，是否重新下载安装?",from);
            return;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // 对签到任务和下载任务 实行应用运行时间监测
        if (TaskType.SIGNTASK.equals(from) || TaskType.DGET.equals(from)) {
            context.startService(GetTopTaskService.getIntent(context, downloadTaskBean.package_name, downloadTaskBean.app_name, downloadTaskBean.task_id, from, downloadTaskBean.need_run_time));
        }
        context.startActivity(intent);
    }

    public static void showReDownloadDialog(final Context context, final DownloadTaskBean item, String msg, final String from) {
        showReDownloadDialog(context,item,msg,from,null);
    }

    public static void showReDownloadDialog(final Context context, final DownloadTaskBean item, String msg, final String from,final ClickObserver callback) {
        AlertDialog ad = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                if (callback == null || !callback.normalClick()) {
                                    MyDownloadManager
                                            .getInstance()
                                            .requestDownload(
                                                    item, from);
                                }
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                            }
                        }).create();
        ad.show();
    }

    public static void initDownLoadLay(DownloadTaskBean softItem,
                                       TextView download_btn, ProgressBar download_progress,
                                       OnClickListener clickListener, int textColor, int textBg) {
        download_btn.setTag(softItem);
        download_btn.setOnClickListener(clickListener);
        resetIfCanOpen(softItem, download_btn, download_progress, textColor, textBg);
    }

    public static void resetIfCanOpen(DownloadTaskBean softItem, TextView download_btn,
                                      ProgressBar download_progress, int textColor, int textBg) {
        if (softItem != null && download_btn != null
                && download_progress != null) {
            if (ToolUtils.isApplicationInstalledByPackageName(softItem.package_name)) {
                download_btn.setText(MyConstants.OPEN);
                download_progress.setProgress(100);
                download_btn.setTextColor(Color.WHITE);
                return;
            }
        }
        DownLoadFile df = MyDownloadManager.getInstance().getDownLoadFileTask(softItem.package_name);
        if (df == null) {
            download_btn.setText(MyConstants.NARMAL);
            download_progress.setProgress(100);
            download_btn.setTextColor(textColor);
        } else {
            LogUtil.i(df.toString());
            String state = df.getState();
            if (state.equals(MyConstants.PAUSE)) {
                download_btn.setText(df.getProgresspercentage() + "%");
            } else {
                download_btn.setText(state);
                if (state.equals(MyConstants.COMPLETE)) {
                    download_btn.setTextColor(Color.WHITE);
                } else {
                    download_btn.setTextColor(textColor);
                }
            }
            download_progress.setProgress(df.getProgresspercentage());
        }
    }

}
