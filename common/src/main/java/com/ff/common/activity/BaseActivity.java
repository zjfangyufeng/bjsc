package com.ff.common.activity;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by fangyufeng on 2015/10/28.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
