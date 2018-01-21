package com.ff.common.http;

import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.model.TaskItem;
import com.ff.common.model.TaskType;
import com.ff.common.model.UserInfo;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangyufeng on 2015/12/11.
 */
public class UmengEvent {
    public static final String DOWNLOADTYPE_SHOW = "1";
    public static final String DOWNLOADTYPE_CHECK_DETAIL = "2";
    public static final String DOWNLOADTYPE_START_DOWNLOAD = "3";
    public static final String DOWNLOADTYPE_DOWNLOAD_COMPLETE = "4";
    public static final String DOWNLOADTYPE_INSTALL = "5";
    public static final String DOWNLOADTYPE_OPEN_AFTERINSTALL = "6";
    public static final String DOWNLOADTYPE_OPEN_DAILY_SIGN = "7";

    public static final String READTYPE_SHOW = "8";
    public static final String READTYPE_CHECK_DETAIL = "9";
    public static final String READTYPE_SHARE = "10";

    public static final String SHARETYPE_SHOW = "11";
    public static final String SHARETYPE_SHARE = "12";

    public static final String SIGNINTYPE_SHOW = "13";
    public static final String SIGNINTYPE_OPEN = "14";

    public static final String QUESTIONTYPE_SHOW = "15";
    public static final String QUESTIONTYPE_CHECK_DETAIL = "16";
    public static final String QUESTIONTYPE_SUBMIT_ANSWER = "17";
    public static final String QUESTIONTYPE_OPEN_AFTERINSTALL = "18";

    public static final String QSIGNINTYPE_SHOW = "19";
    public static final String QSIGNINTYPE_OPEN = "20";
    public static final String QSIGNINTYPE_OPEN_DAILY_SIGN = "21";

    public static final String BDMSSP_ACTIVITY_CLICK = "22";
    public static final String BDMSSP_FLOAT_VIEW_CLICK = "23";

    public static final String HOME_ACTIVITY_ENTRY_CLICK = "home_activity_entry_click";

    public static void sendUmengDownloadClick(final String status,String from,String taskId,String appName){
        Map<String,String> param =  new HashMap<>();
        if(ToolUtils.isNull(taskId)){
            param.put("taskId","");
        }else{
            param.put("taskId",taskId);
        }
        if(ToolUtils.isNull(appName)){
            param.put("appName", "");
        }else{
            param.put("appName",appName);
        }
        param.put("userId", UserInfo.getUserId());
        if(SendDownloadReport.TYPE_CLICK_DOWNLOAD.equals(status)){
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), DOWNLOADTYPE_START_DOWNLOAD, param, 0);
        }else if(SendDownloadReport.TYPE_DOWNLOAD_COMPLETE.equals(status)){
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), DOWNLOADTYPE_DOWNLOAD_COMPLETE, param, 0);
        }else if(SendDownloadReport.TYPE_INSTALLED.equals(status)){
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), DOWNLOADTYPE_INSTALL, param, 0);
        }else if(TaskType.DGET.equals(from) && SendDownloadReport.TYPE_OPEN.equals(status)) {
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), DOWNLOADTYPE_OPEN_AFTERINSTALL, param, 0);
        } else if (TaskType.QGET.equals(from) && SendDownloadReport.TYPE_OPEN.equals(status)) {
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), QUESTIONTYPE_OPEN_AFTERINSTALL, param, 0);
        } else if(TaskType.SIGNTASK.equals(from) && SendDownloadReport.TYPE_OPEN.equals(status)){
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), DOWNLOADTYPE_OPEN_DAILY_SIGN, param, 0);
        } else if(TaskType.QSIGNTASK.equals(from) && SendDownloadReport.TYPE_OPEN.equals(status)){
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), QSIGNINTYPE_OPEN_DAILY_SIGN, param, 0);
        }
    }

    public static void sendUmengSubmitQuestionAnswer(final String status, String taskId, String appName){
        Map<String,String> param =  new HashMap<>();
        param.put("taskId",taskId);
        param.put("appName",appName);
        param.put("userId", UserInfo.getUserId());
        MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), status, param, 0);
    }

    public static void sendUmengCheckDetailStatistics(final String status,String taskId,String appName){
        Map<String,String> param =  new HashMap<>();
        param.put("taskId",taskId);
        param.put("appName",appName);
        param.put("userId", UserInfo.getUserId());
        MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), status, param, 0);
    }

    public static void sendClickStatistics(final String status){
        Map<String,String> param =  new HashMap<>();
        param.put("userId", UserInfo.getUserId());
        MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), status, param, 0);
    }

    public static void sendClickStatistics(final String event_id,Map<String,String> param){
        MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), event_id, param, 0);
    }

    public static void sendUmengShowStatistics(TaskItem item) {
        Map<String,String> param =  new HashMap<>();
        param.put("taskId",item.id);
        param.put("appName",item.name);
        param.put("userId", UserInfo.getUserId());
        if (TaskType.DOWNLOADTASK.equals(item.type)) {
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), DOWNLOADTYPE_SHOW, param, 0);
        } else if (TaskType.SIGNTASK.equals(item.type)) {
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), SIGNINTYPE_SHOW, param, 0);
        } else if (TaskType.QSIGNTASK.equals(item.type)) {
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), QSIGNINTYPE_SHOW, param, 0);
        } else if (TaskType.BROWSETASK.equals(item.type)) {
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), READTYPE_SHOW, param, 0);
        } else if (TaskType.SHARETASK.equals(item.type)) {
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), SHARETYPE_SHOW, param, 0);
        } else if (TaskType.QUESTIONTASK.equals(item.type)) {
            MobclickAgent.onEventValue(ApplicationProxy.getInstance().getContext(), QUESTIONTYPE_SHOW, param, 0);
        }
    }

}
