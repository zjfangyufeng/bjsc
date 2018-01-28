package com.ddfun.bjsc.news;

import com.ddfun.bjsc.bean.NewsBean;
import com.ff.common.MyConstant;
import com.ff.common.http.MyHttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by fangyufeng on 2015/9/22.
 */
public class NewsFragmentModel {

    public List<NewsBean> data;

    static NewsFragmentModel instance = new NewsFragmentModel();

    private NewsFragmentModel(){
    }

    public static NewsFragmentModel getInstance(){
        return instance;
    }

    Gson gson = new Gson();
    String result;

    public String initData() {
        try {
            JSONObject resultObj = MyHttpClient.executeGet("http://zx.500.com/ajax.php?type=news&sortid=4&pageCount=0");

            this.data = gson.fromJson(resultObj.getString("data"),new TypeToken<List<NewsBean>>(){}.getType());

            result = MyConstant.SUCCESS;
        } catch (Exception e) {
            result = null;
        }
        return  result;
    }

}
