package com.ddfun.bjsc.news;

import android.os.Bundle;

import com.ddfun.bjsc.bean.NewsBean;
import com.ff.common.MyConstant;
import com.ff.common.http.MyHttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangyufeng on 2015/9/22.
 */
public class NewsFragmentModel {

    public List<NewsBean> data;

    Gson gson = new Gson();
    String result;

    public String initData(int sortid,int page) {
        try {
            JSONObject resultObj = MyHttpClient.executeGet("http://zx.500.com/ajax.php?type=news&sortid="+sortid+"&pageCount="+page);
            this.data = gson.fromJson(resultObj.getString("data"),new TypeToken<List<NewsBean>>(){}.getType());
            result = MyConstant.SUCCESS;
        } catch (Exception e) {
            result = null;
        }
        return  result;
    }

    public Bundle fetchData(int sortid,int page) {
        Bundle result = new Bundle();
        try {
            JSONObject resultObj = MyHttpClient.executeGet("http://zx.500.com/ajax.php?type=news&sortid="+sortid+"&pageCount="+page);
            ArrayList<NewsBean> data = gson.fromJson(resultObj.getString("data"), new TypeToken<ArrayList<NewsBean>>() {
            }.getType());
            result.putParcelableArrayList("data",data);
            result.putString(MyConstant.ISSUCCESS,MyConstant.SUCCESS);
        } catch (Exception e) {
        }
        return result;
    }

}
