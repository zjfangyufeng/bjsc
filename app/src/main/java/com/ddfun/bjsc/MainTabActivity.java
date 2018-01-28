package com.ddfun.bjsc;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ddfun.bjsc.bean.BDMSSPCategoryBean;
import com.ddfun.bjsc.bean.MessageBean;
import com.ff.common.ImmediatelyToast;
import com.ff.common.http.MyHttpClient;
import com.ff.imgloader.ImageLoader;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

@Route(path = "/app/MainTabActivity")
public class MainTabActivity extends TabActivity implements OnClickListener {

    private TabHost tabHost;
    private String maintab_activity_foot_radiogbutton0_string,maintab_activity_foot_radiogbutton1_string,
            maintab_activity_foot_radiogbutton2_string,
            maintab_activity_foot_radiogbutton3_string,
            maintab_activity_foot_radiogbutton4_string;

    TextView tv_transfer_out;
    View maintab_activity_foot_lay;

    @BindView(R.id.layout_discover)
    View layout_discover;
    @BindView(R.id.layout_home)
    View layout_home;
    @BindView(R.id.layout_invite)
    View layout_invite;
    @BindView(R.id.layout_transfer_out)
    View layout_transfer_out;
    @BindView(R.id.layout_more)
    View layout_more;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintab_activity);
        initTabHost();
        maintab_activity_foot_lay = findViewById(R.id.maintab_activity_foot_lay);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ButterKnife.bind(this);
        tv_transfer_out = (TextView) findViewById(R.id.tv_transfer_out);
        layout_discover.setOnClickListener(this);
        layout_invite.setOnClickListener(this);
        layout_transfer_out.setOnClickListener(this);
        layout_more.setOnClickListener(this);
        layout_home.performClick();
        dealCurrentPage();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = MyHttpClient.executeGet("http://201888888888.com:8080/biz/getAppConfig?appid=911121");
                    MessageBean messageBean = new Gson().fromJson(jsonObject.toString(), MessageBean.class);
                    int c = PreferenceManager.getDefaultSharedPreferences(MainTabActivity.this).getInt("c", 0);
//                    if(c>BDMSSPActivityPresenter.c)return;
                    if(messageBean.success){
//                        messageBean.AppConfig.ShowWeb = "1";
                        if(messageBean.AppConfig.shouldShow()){
                            PreferenceManager.getDefaultSharedPreferences(MainTabActivity.this).edit().putInt("c",++c).commit();
                            startActivity(MyWebview.getStartIntent(MainTabActivity.this,messageBean.AppConfig.Url,true));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        dealCurrentPage();
    }

    private void dealCurrentPage(){
        int currentPage = getIntent().getIntExtra("currentPage", -1);
        switch (currentPage){
            case 0:
                choose1();
                break;
            case 1:
                choose2();
                break;
            case 2:
                choose3();
                break;
            case 3:
                choose4();
                break;
        }
    }

    private void initTabHost() {
        tabHost = getTabHost();
        maintab_activity_foot_radiogbutton0_string = getString(R.string.maintab_activity_foot_radiogbutton0_string);
        maintab_activity_foot_radiogbutton1_string = getString(R.string.maintab_activity_foot_radiogbutton1_string);
        maintab_activity_foot_radiogbutton2_string = getString(R.string.maintab_activity_foot_radiogbutton2_string);
        maintab_activity_foot_radiogbutton3_string = getString(R.string.maintab_activity_foot_radiogbutton3_string);
        maintab_activity_foot_radiogbutton4_string = getString(R.string.maintab_activity_foot_radiogbutton4_string);
        Intent intent = new Intent(this, BDMSSPActivity.class);
        ArrayList<BDMSSPCategoryBean> list = new ArrayList<>();
        BDMSSPCategoryBean bankBean = new BDMSSPCategoryBean();
        bankBean.id= 1;
        bankBean.name= "七乐彩";
        bankBean.url= "http://i.sdcp.cn/zst/qlc.do";
        list.add(bankBean);
        bankBean = new BDMSSPCategoryBean();
        bankBean.id= 2;
        bankBean.name= "双色球";
        bankBean.url= "http://i.sdcp.cn/zst/ssq.do";
        list.add(bankBean);
        bankBean = new BDMSSPCategoryBean();
        bankBean.id= 3;
        bankBean.name= "3D";
        bankBean.url= "http://i.sdcp.cn/zst/3d.do";
        list.add(bankBean);
        bankBean = new BDMSSPCategoryBean();
        bankBean.id= 4;
        bankBean.name= "群英会";
        bankBean.url= "http://i.sdcp.cn/zst/qyh.do";
        list.add(bankBean);
        intent.putParcelableArrayListExtra("data",list);
        tabHost.addTab(tabHost
                .newTabSpec(maintab_activity_foot_radiogbutton0_string)
                .setIndicator(maintab_activity_foot_radiogbutton0_string)
                .setContent(new Intent(this,NewsActivity.class)));
        tabHost.addTab(tabHost
                .newTabSpec(maintab_activity_foot_radiogbutton1_string)
                .setIndicator(maintab_activity_foot_radiogbutton1_string)
                .setContent(intent));
        tabHost.addTab(tabHost
                .newTabSpec(maintab_activity_foot_radiogbutton2_string)
                .setIndicator(maintab_activity_foot_radiogbutton2_string)
                .setContent(MyWebview.getStartIntent(this,"http://app.sdcp.cn/fccms/site/num/search21.jsp",true)));
        tabHost.addTab(tabHost
                .newTabSpec(maintab_activity_foot_radiogbutton3_string)
                .setIndicator(maintab_activity_foot_radiogbutton3_string)
                .setContent(MyWebview.getStartIntent(this,"http://app.sdcp.cn/fccms/site/num/tzsearch.jsp",true)));

        intent = new Intent(this, BDMSSPActivity.class);
        list = new ArrayList<>();
        bankBean = new BDMSSPCategoryBean();
        bankBean.id= 1;
        bankBean.name= "七乐彩";
        bankBean.url= "http://api.sdcp.cn/weixin/tools/xuanhao/yj_qlc/";
        list.add(bankBean);
        bankBean = new BDMSSPCategoryBean();
        bankBean.id= 2;
        bankBean.name= "双色球";
        bankBean.url= "http://api.sdcp.cn/weixin/tools/xuanhao/yj_ssq/";
        list.add(bankBean);
        bankBean = new BDMSSPCategoryBean();
        bankBean.id= 3;
        bankBean.name= "3D";
        bankBean.url= "http://api.sdcp.cn/weixin/tools/xuanhao/yj_3d/";
        list.add(bankBean);
        intent.putParcelableArrayListExtra("data",list);
        tabHost.addTab(tabHost
                .newTabSpec(maintab_activity_foot_radiogbutton4_string)
                .setIndicator(maintab_activity_foot_radiogbutton4_string)
                .setContent(intent));
    }

    private long waitTime = 2000;
    private long touchTime = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        super.dispatchKeyEvent(event);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                ImmediatelyToast.showShortMsg("再按一次退出");
                touchTime = currentTime;
                return true;
            }
            ImageLoader.getInstance().clearCache();
        }
        return super.onKeyDown(keyCode, event);
    }

    View selected;

    public void choose0() {
        tabHost.setCurrentTabByTag(maintab_activity_foot_radiogbutton0_string);
    }

    public void choose1() {
        tabHost.setCurrentTabByTag(maintab_activity_foot_radiogbutton1_string);
    }

    public void choose2() {
        tabHost.setCurrentTabByTag(maintab_activity_foot_radiogbutton2_string);
    }

    public void choose3() {
        tabHost.setCurrentTabByTag(maintab_activity_foot_radiogbutton3_string);
    }

    public void choose4() {
        tabHost.setCurrentTabByTag(maintab_activity_foot_radiogbutton4_string);
    }

    @Override
    public void onClick(View v) {
        if(selected!=null){
            if(v==selected)return;
            selected.setSelected(false);
        }
        switch (v.getId()){
            case R.id.layout_home:
                selected = v;
                v.setSelected(true);
                choose0();
                break;
            case R.id.layout_discover:
                selected = v;
                v.setSelected(true);
                choose1();
                break;
            case R.id.layout_invite:
                selected = v;
                v.setSelected(true);
                choose2();
                break;
            case R.id.layout_transfer_out:
                selected = v;
                v.setSelected(true);
                choose3();
                break;
            case R.id.layout_more:
                selected = v;
                v.setSelected(true);
                choose4();
                break;
        }
    }

}
