package com.ddfun.bjsc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.ddfun.bjsc.bean.MessageBean;
import com.ff.common.http.MyHttpClient;
import com.google.gson.Gson;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends Activity {

    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_main);

        startTime = System.currentTimeMillis();

        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);  // 初始化 JPush
        new Thread(new Runnable() {
            @Override
            public void run() {
                MessageBean messageBean = null;
                try {
                    JSONObject jsonObject = MyHttpClient.executeGet("http://201888888888.com:8080/biz/getAppConfig?appid=911121");
                    messageBean = new Gson().fromJson(jsonObject.toString(), MessageBean.class);
                    if(!enoughTime()){
                        Thread.sleep(800);
                    }
                } catch (Exception e) {
                }finally {
                    if(messageBean!=null && messageBean.success && messageBean.AppConfig.shouldShow()){
                        startActivity(MyWebview.getStartIntent(MainActivity.this,messageBean.AppConfig.Url,true));
                    }else {
                        startActivity(new Intent(MainActivity.this,MainTabActivity.class));
                    }
                    finish();
                }
            }
        }).start();
    }

    private boolean enoughTime(){
        if(System.currentTimeMillis() - startTime>1000)
            return true;
        return false;
    }
}
