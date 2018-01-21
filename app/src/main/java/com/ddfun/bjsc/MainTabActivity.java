package com.ddfun.bjsc;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ff.common.ImmediatelyToast;
import com.ff.imgloader.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = "/app/MainTabActivity")
public class MainTabActivity extends TabActivity implements OnClickListener {

    private TabHost tabHost;
    private String maintab_activity_foot_radiogbutton1_string,
            maintab_activity_foot_radiogbutton2_string,
            maintab_activity_foot_radiogbutton3_string,
            maintab_activity_foot_radiogbutton4_string,
            maintab_activity_foot_radiogbutton5_string;

    private ImageView msg_indicator;
    TextView tv_transfer_out;
    View maintab_activity_foot_lay;

    @BindView(R.id.layout_discover)
    View layout_discover;
    @BindView(R.id.layout_invite)
    View layout_invite;
    @BindView(R.id.btn_home)
    View btn_home;
    @BindView(R.id.layout_transfer_out)
    View layout_transfer_out;
    @BindView(R.id.layout_more)
    View layout_more;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintab_activity);
        initTabHost();
        msg_indicator = (ImageView) findViewById(R.id.msg_indicator);
//        startActivity(new Intent(this,AgreementActivity.class));
        maintab_activity_foot_lay = findViewById(R.id.maintab_activity_foot_lay);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ButterKnife.bind(this);
        tv_transfer_out = (TextView) findViewById(R.id.tv_transfer_out);
        layout_discover.setOnClickListener(this);
        layout_invite.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        layout_transfer_out.setOnClickListener(this);
        layout_more.setOnClickListener(this);
        btn_home.performClick();
        dealCurrentPage();
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
            case 4:
                choose5();
                break;
        }
    }

    private void initTabHost() {
        tabHost = getTabHost();
        maintab_activity_foot_radiogbutton1_string = getString(R.string.maintab_activity_foot_radiogbutton1_string);
        maintab_activity_foot_radiogbutton2_string = getString(R.string.maintab_activity_foot_radiogbutton2_string);
        maintab_activity_foot_radiogbutton3_string = getString(R.string.maintab_activity_foot_radiogbutton3_string);
        maintab_activity_foot_radiogbutton4_string = getString(R.string.maintab_activity_foot_radiogbutton4_string);
        maintab_activity_foot_radiogbutton5_string = getString(R.string.maintab_activity_foot_radiogbutton5_string);


        tabHost.addTab(tabHost
                .newTabSpec(maintab_activity_foot_radiogbutton1_string)
                .setIndicator(maintab_activity_foot_radiogbutton1_string)
                .setContent(MyWebview.getStartIntent(this,"http://i.sdcp.cn/zst/qlc.do",MyWebview.NORMALTYPE)));
        tabHost.addTab(tabHost
                .newTabSpec(maintab_activity_foot_radiogbutton2_string)
                .setIndicator(maintab_activity_foot_radiogbutton2_string)
                .setContent(MyWebview.getStartIntent(this,"http://app.sdcp.cn/fccms/site/num/search21.jsp",MyWebview.NORMALTYPE)));
        tabHost.addTab(tabHost
                .newTabSpec(maintab_activity_foot_radiogbutton3_string)
                .setIndicator(maintab_activity_foot_radiogbutton3_string)
                .setContent(MyWebview.getStartIntent(this,"http://api.sdcp.cn/weixin/tools/xuanhao/yj_qlc/",MyWebview.NORMALTYPE)));
        tabHost.addTab(tabHost
                .newTabSpec(maintab_activity_foot_radiogbutton4_string)
                .setIndicator(maintab_activity_foot_radiogbutton4_string)
                .setContent(MyWebview.getStartIntent(this,"http://app.sdcp.cn/fccms/site/num/tzsearch.jsp",MyWebview.NORMALTYPE)));
        tabHost.addTab(tabHost
                .newTabSpec(maintab_activity_foot_radiogbutton5_string)
                .setIndicator(maintab_activity_foot_radiogbutton5_string)
                .setContent(MyWebview.getStartIntent(this,"http://api.sdcp.cn/weixin/tools/xuanhao/yj_3d/",MyWebview.NORMALTYPE)));
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

    public void choose5() {
        tabHost.setCurrentTabByTag(maintab_activity_foot_radiogbutton5_string);
    }

    @Override
    public void onClick(View v) {
        if(selected!=null){
            if(v==selected)return;
            selected.setSelected(false);
        }
        switch (v.getId()){
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
            case R.id.btn_home:
                selected = v;
                v.setSelected(true);
                choose3();
                break;
            case R.id.layout_transfer_out:
                selected = v;
                v.setSelected(true);
                choose4();
                break;
            case R.id.layout_more:
                selected = v;
                v.setSelected(true);
                choose5();
                break;
        }
    }

}
