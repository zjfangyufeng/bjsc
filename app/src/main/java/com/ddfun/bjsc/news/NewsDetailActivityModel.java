package com.ddfun.bjsc.news;

import android.os.Bundle;

import com.ddfun.bjsc.bean.BDMSSPCategoryBean;
import com.ddfun.bjsc.bean.NewsBean;
import com.ddfun.bjsc.bean.NewsDetailsBean;
import com.ff.common.MyConstant;
import com.ff.common.http.MyHttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fangyufeng on 2015/9/23.
 */
public class NewsDetailActivityModel {

    NewsDetailsBean bean;

    public Bundle initList(String fid) {
        Bundle result = new Bundle();
        try {
            HashMap<String, String> param = new HashMap<>();
            param.put("fid",fid);
            JSONObject resultObj = MyHttpClient.executePost("http://zx.500.com/openplatform/getinfo.php",param);
            bean = new Gson().fromJson(resultObj.getString("detail"),NewsDetailsBean.class);
            result.putBoolean(MyConstant.ISSUCCESS, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
