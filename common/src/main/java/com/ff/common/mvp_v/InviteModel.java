package com.ff.common.mvp_v;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ff.common.MyConstant;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.http.MyHttpClient;
import com.ff.common.model.InviteIncomeBean;
import com.ff.common.model.NoviceCostBean;
import com.ff.common.model.UserInfo;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangyufeng on 2015/9/22.
 */
public class InviteModel {

    public InviteIncomeBean bean;
    public NoviceCostBean nc_bean;
    Gson gson = new Gson();

    public String initData() {
        Map<String, String> param = new HashMap();
        JSONObject resultObj = MyHttpClient.executePost(
                MyHttpClient.getBASE_URL() + "/app/invite/index/" + UserInfo.getUserId(), param);
        try {
            bean = gson.fromJson(resultObj.getString("data"), InviteIncomeBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        resultObj = MyHttpClient.executePost(
                MyHttpClient.getBASE_URL() + "/app/about/appnum", param);
        try {
            nc_bean = gson.fromJson(resultObj.getString("data"), NoviceCostBean.class);
            return bean==null || nc_bean==null?null:MyConstant.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void cacheNoviceCostData() {
        Map<String, String> param = new HashMap();
        Gson gson = new Gson();
        JSONObject resultObj = MyHttpClient.executePost(MyHttpClient.getBASE_URL() + "/app/about/appnum", param);
        try {
            NoviceCostBean bean = gson.fromJson(resultObj.getString("data"), NoviceCostBean.class);
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).edit();
            edit.putString("be_invite_money", bean.be_invite_money);
            edit.putString("invite_money", bean.invite_money);
            edit.putString("reg_money", bean.reg_money);
            edit.putString("wx_appid", bean.wx_appid);
            MyHttpClient.SHARE_LINK = bean.domain + MyHttpClient.SHARE_SUFFIX;
            edit.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
