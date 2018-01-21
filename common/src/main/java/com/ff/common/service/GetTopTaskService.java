package com.ff.common.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.ff.common.ImmediatelyToast;
import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.http.SendDownloadReport;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetTopTaskService extends Service {
    protected final long DELAY_TIME = 3000;
    int need_run_time = 30;
    protected final long SERVICE_RUN_TIME = 1000 * 60 * 10;
    private Handler mHandler = new Handler();
    private long startTime = 0; //新栈顶应用的开始运行时间
    private long serviceStartTime = 0; //服务开启运行时间
    boolean isTargetPackage = false;
    boolean hasDetected=false;

    private String topPackageName = "";
    public String targetPackageName = "";
    private String softId = "";
    private String appName = "";
    private String from = "";

    @Override
    public void onCreate() {
    }

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacksAndMessages(null);
            boolean activityExist = ToolUtils.isUsageAccessSettingActivityExist();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                if (activityExist) {
                    topPackageName = getTopPackageNameOnL(GetTopTaskService.this,SERVICE_RUN_TIME*6);
                } else {
                    ApplicationProxy.getInstance().getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SendDownloadReport.sendDownloadReport(softId, appName, from, SendDownloadReport.TYPE_OPEN, null);
                        }
                    }, getNeedRunTimeMillisecond());
                    GetTopTaskService.this.stopSelf();
                    return;
                }
            } else {
                topPackageName = getTopTask();
            }

            if (topPackageName.equals(targetPackageName) && !isTargetPackage) {
                startTime = SystemClock.uptimeMillis();
                isTargetPackage = true;
                hasDetected=true;
            }

            if (isTargetPackage) {
                long endTime = SystemClock.uptimeMillis();
                long lastTime = endTime - startTime;
                if (lastTime >= getNeedRunTimeMillisecond()) {
                    SendDownloadReport.sendDownloadReport(softId, appName, from, SendDownloadReport.TYPE_OPEN, null);
                    GetTopTaskService.this.stopSelf();
                    return;
                }
            }

            if (!topPackageName.equals(targetPackageName) && isTargetPackage) {
                isTargetPackage = false;
                long endTime = SystemClock.uptimeMillis();
                long lastTime = endTime - startTime;
                if (lastTime < getNeedRunTimeMillisecond()) {
                    ImmediatelyToast.showLongMsg("【豆豆趣玩提示您】哎呀，时间不够哦，再回去使用一会儿就能有奖励哦");
                }
            }

            long endTime = SystemClock.uptimeMillis();
            long lastTime = endTime - serviceStartTime;
            if (lastTime >= SERVICE_RUN_TIME) {
                GetTopTaskService.this.stopSelf();
                return;
            }
            startScan();
        }
    };

    // 在5.0系统之前的获取当前栈顶的任务的包名
    private String getTopTask() {
        List<RunningTaskInfo> runningTasks = ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningTasks(1);
        return runningTasks.get(0).topActivity.getPackageName();
    }

    /**
     * @param context
     * @return 返回使用情况数据的集合
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getTopPackageNameOnL(Context context,long time) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (null != usageStatsManager) {
            long current = System.currentTimeMillis();
            List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, current - time, current);
            if (null != usageStatsList && usageStatsList.size() > 0) {
                //按最后访问时间，降序排列
                Collections.sort(usageStatsList, new Comparator<UsageStats>() {
                    @Override
                    public int compare(UsageStats lhs, UsageStats rhs) {
                        long result = rhs.getLastTimeUsed() - lhs.getLastTimeUsed();
                        if(result>0)
                            return 1;
                        else if(result<0)
                            return -1;
                        return 0;
                    }
                });
//                ImmediatelyToast.showLongMsg("top:"+usageStatsList.get(0).getPackageName()+" - "+usageStatsList.size());
                for (UsageStats us : usageStatsList){
                    if(us.getLastTimeUsed()!=0)
                        return us.getPackageName();
                }
            }
//            ImmediatelyToast.showLongMsg("top:"+usageStatsList.size());
        }
        return "";
    }

    public String getTopActivity() {
        return "";
    }

    public void startScan() {
        mHandler.postDelayed(timeRunnable, DELAY_TIME);
    }

    public void stopScan() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        stopScan();
    }

    public static Intent getIntent(Context context,String targetPackageName,String appName,String softId,String from,int need_run_time){
        return getIntent(context,targetPackageName,appName,softId,from,need_run_time,null);
    }

    public static Intent getIntent(Context context,String targetPackageName,String appName,String softId,String from,int need_run_time,String operation_request){
        Intent intent = new Intent(context,GetTopTaskService.class);
        intent.putExtra("targetPackageName",targetPackageName);
        intent.putExtra("appName",appName);
        intent.putExtra("softId",softId);
        intent.putExtra("from",from);
        intent.putExtra("need_run_time",need_run_time);
        intent.putExtra("operation_request",operation_request);
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
        targetPackageName = intent.getStringExtra("targetPackageName");
        softId = intent.getStringExtra("softId");
        appName = intent.getStringExtra("appName");
        from = intent.getStringExtra("from");
        need_run_time = intent.getIntExtra("need_run_time", need_run_time);
        if(need_run_time==0)
            need_run_time = 30;
        serviceStartTime = SystemClock.uptimeMillis();
        startScan();
        String operation_request = intent.getStringExtra("operation_request");
        if(ToolUtils.isNull(operation_request))
            ImmediatelyToast.showLongMsg("【豆豆趣玩提示您】按照提示使用足够时间才能获得奖励哦");
        else
            ImmediatelyToast.showLongMsg("任务要求:"+operation_request);
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int getNeedRunTimeSecond(){
        return need_run_time;
    }
    public long getNeedRunTimeMillisecond(){
        return getNeedRunTimeSecond()*1000;
    }

}
