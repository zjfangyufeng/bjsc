package com.ff.common.mvp_v;

import com.ff.common.ToolUtils;
import com.ff.common.model.AppInfo;
import com.ff.common.model.UserInfo;
import com.google.gson.Gson;

/**
 * Created by fangyufeng on 2015/9/30.
 */
public class SettingsModel {

    public static final String SHORTCUTLAUNCHAPPSWITCH = "SHORTCUTLAUNCHAPPSWITCH";
    public static final String LOCKSCREENSWITCH = "LOCKSCREENSWITCH";
    public static final String MESSAGE_NOTIFICATION_SWITCH = "MESSAGE_NOTIFICATION_SWITCH";
    public static final String DATA_FLOW_INTELLIGENT_SWITCH = "DATA_FLOW_INTELLIGENT_SWITCH";
    public static final String QUESTION_TASK_FLOAT_WINDOW_SWITCH = "QUESTION_TASK_FLOAT_WINDOW_SWITCH";

    public static final String ALREADYSETAPP1 = "ALREADYSETAPP1";
    public static final String ALREADYSETAPP2 = "ALREADYSETAPP2";
    public static final String ALREADYSETAPP3= "ALREADYSETAPP3";

    static AppInfo alreadySetApp1,alreadySetApp2,alreadySetApp3;
    static {
        Gson gson = new Gson();
        String string1 = UserInfo.getCurrentUserSharedPreferences().getString(ALREADYSETAPP1, null);
        if(string1!=null){
            alreadySetApp1 = gson.fromJson(string1, AppInfo.class);
            alreadySetApp1.icon = ToolUtils.getApplicationIcon(alreadySetApp1.packageName);
        }
        String string2 = UserInfo.getCurrentUserSharedPreferences().getString(ALREADYSETAPP2, null);
        if(string2!=null){
            alreadySetApp2 = gson.fromJson(string2, AppInfo.class);
            alreadySetApp2.icon = ToolUtils.getApplicationIcon(alreadySetApp2.packageName);
        }
        String string3 = UserInfo.getCurrentUserSharedPreferences().getString(ALREADYSETAPP3, null);
        if(string3!=null){
            alreadySetApp3 = gson.fromJson(string3, AppInfo.class);
            alreadySetApp3.icon = ToolUtils.getApplicationIcon(alreadySetApp3.packageName);
        }
    }
    public static boolean hasAlreadySet1(){
        return alreadySetApp1==null?false:true;
    }

    public static boolean hasAlreadySet2(){
        return alreadySetApp2==null?false:true;
    }

    public static boolean hasAlreadySet3(){
        return alreadySetApp3==null?false:true;
    }

    public static AppInfo getAlreadySetApp1(){
        return alreadySetApp1;
    }

    public static AppInfo getAlreadySetApp2(){
        return alreadySetApp2;
    }

    public static AppInfo getAlreadySetApp3(){
        return alreadySetApp3;
    }

    public static void unSetApp1(){
        alreadySetApp1=null;
        UserInfo.getCurrentUserSharedPreferences().edit().remove(ALREADYSETAPP1).commit();
    }

    public static void unSetApp2(){
        alreadySetApp2=null;
        UserInfo.getCurrentUserSharedPreferences().edit().remove(ALREADYSETAPP2).commit();
    }

    public static void unSetApp3(){
        alreadySetApp3=null;
        UserInfo.getCurrentUserSharedPreferences().edit().remove(ALREADYSETAPP3).commit();
    }

    public static void setApp1(AppInfo a){
        alreadySetApp1=a;
        String json = new Gson().toJson(a);
        UserInfo.getCurrentUserSharedPreferences().edit().putString(ALREADYSETAPP1,json).commit();
    }

    public static void setApp2(AppInfo a){
        alreadySetApp2=a;
        String json = new Gson().toJson(a);
        UserInfo.getCurrentUserSharedPreferences().edit().putString(ALREADYSETAPP2,json).commit();
    }

    public static void setApp3(AppInfo a){
        alreadySetApp3=a;
        String json = new Gson().toJson(a);
        UserInfo.getCurrentUserSharedPreferences().edit().putString(ALREADYSETAPP3,json).commit();
    }

    public static boolean getShortCutLaunchAppSwitch(){
        return UserInfo.getCurrentUserSharedPreferences().getBoolean(SHORTCUTLAUNCHAPPSWITCH,false);
    }

    public static void setShortCutLaunchAppSwitch(boolean isChecked){
        UserInfo.getCurrentUserSharedPreferences().edit().putBoolean(SHORTCUTLAUNCHAPPSWITCH,isChecked).commit();
    }

    public static boolean getLockScreenSwitch(){
        return UserInfo.getCurrentUserSharedPreferences().getBoolean(LOCKSCREENSWITCH,true);
    }

    public static void setLockScreenSwitch(boolean isChecked){
        UserInfo.getCurrentUserSharedPreferences().edit().putBoolean(LOCKSCREENSWITCH,isChecked).commit();
    }

    public static boolean getMessageNotificationSwitch(){
        return UserInfo.getCurrentUserSharedPreferences().getBoolean(MESSAGE_NOTIFICATION_SWITCH,true);
    }

    public static void setMessageNotificationSwitch(boolean isChecked){
        UserInfo.getCurrentUserSharedPreferences().edit().putBoolean(MESSAGE_NOTIFICATION_SWITCH,isChecked).commit();
    }

    public static boolean getDataFlowIntelligentSwitch(){
        return UserInfo.getCurrentUserSharedPreferences().getBoolean(DATA_FLOW_INTELLIGENT_SWITCH,true);
    }

    //0未知 1 开 2 关
    public static int getQuestion_task_float_windowSwitch(){
        int result = UserInfo.getCurrentUserSharedPreferences().getInt(QUESTION_TASK_FLOAT_WINDOW_SWITCH, 0);
        return result;
    }

    public static void setQuestion_task_float_windowSwitch(int isChecked){
        UserInfo.getCurrentUserSharedPreferences().edit().putInt(QUESTION_TASK_FLOAT_WINDOW_SWITCH,isChecked).commit();
    }

    public static void setDataFlowIntelligentSwitch(boolean isChecked){
        UserInfo.getCurrentUserSharedPreferences().edit().putBoolean(DATA_FLOW_INTELLIGENT_SWITCH,isChecked).commit();
    }
}
