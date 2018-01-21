package com.ddfun.bjsc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ff.common.DisplayMetricsTool;
import com.ff.common.ToolUtils;
import com.ff.common.activity.BaseActivity;
import com.ff.common.utils.WebViewUtil;

@Route(path = "/app/MyWebview")
public class MyWebview extends BaseActivity implements OnClickListener {
    WebView mWebView;
    ProgressBar pb;
    TextView maintab_activity_head_middle;
    ImageView maintab_activity_head_left_btn, btn_share, btn_refresh, btn_more;
    public static final String READTYPE = "readtype";
    public static final String ACTIVITYBOARDTYPE = "activityboardtype";
    public static final String NORMALTYPE = "normaltype";
    public static final String LOTTERY_TYPE = "lottery";

    public boolean WEBVIEWREFRESH ;

    View view;
    private String title;
    private String url;
    private String summary;
    private static final String from = "MyWebview";

    public static Intent getStartIntent(Context mContext, String url, String type) {
        return getStartIntent(mContext, null, url, type, null);
    }

    public static Intent getStartIntent(Context mContext, String url, String type, String summary) {
        return getStartIntent(mContext, null, url, type, summary);
    }

    public static Intent getStartIntent(Context mContext, String title, String url,  String type, String summary) {
        Intent intent = new Intent(mContext, MyWebview.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("type", type);
        intent.putExtra("summary", summary);
        return intent;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebView();
        reset();
    }

    public void initWebView() {
        view = View.inflate(this, R.layout.mywebview, null);
        setContentView(view);
        btn_share = (ImageView) findViewById(R.id.btn_share);
        btn_refresh = (ImageView) findViewById(R.id.btn_refresh);
        btn_more = (ImageView) findViewById(R.id.btn_more);
        btn_share.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_more.setOnClickListener(this);
        maintab_activity_head_middle = (TextView) findViewById(R.id.maintab_activity_head_middle);
        maintab_activity_head_left_btn = (ImageView) findViewById(R.id.maintab_activity_head_left_btn);
        maintab_activity_head_left_btn.setOnClickListener(this);
        mWebView = (WebView) findViewById(R.id.wv);
        pb = (ProgressBar) findViewById(R.id.pb);
        WebViewUtil.initWebview(this, mWebView, pb);
        WebViewUtil.addGetUserIdJSInterface(mWebView);
        WebViewUtil.addSetBalanceJSInterface(this,mWebView);
    }

    public void reset() {
        title = getIntent().getExtras().getString("title");
        if (!ToolUtils.isNull(title)) {
            maintab_activity_head_middle.setText(title);
        }

        String type = getIntent().getExtras().getString("type");
        if (READTYPE.equals(type)) {
            maintab_activity_head_left_btn.setImageResource(R.mipmap.public_close);
            btn_refresh.setVisibility(View.GONE);
        } else if (ACTIVITYBOARDTYPE.equals(type)) {
            btn_more.setVisibility(View.GONE);
        } else {
            btn_more.setVisibility(View.GONE);
            btn_share.setVisibility(View.GONE);
        }
        summary = getIntent().getExtras().getString("summary");
        loadUrl();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WEBVIEWREFRESH = false;
        reset();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(WEBVIEWREFRESH) {
            loadUrl();
        }
    }

    public void loadUrl() {
        url = getUrl();
        if (url != null) {
            mWebView.loadUrl(url);
        }
    }

    public String getUrl() {
        return getIntent().getExtras().getString("url");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            ((ViewGroup) mWebView.getParent()).removeAllViews();
            mWebView.destroy();
        }
        if (ad != null && ad.isShowing()) {
            ad.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.maintab_activity_head_left_btn:
                finish();
                break;
            case R.id.btn_more:
                showMoreDialog();
                break;
            case R.id.cancel_btn:
                ad.dismiss();
                break;
            case R.id.btn_refresh:
                mWebView.reload();
                break;
            case R.id.refresh_lay:
                mWebView.reload();
                ad.dismiss();
                break;
            case R.id.onpen_on_browse_lay:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrl()));
                startActivity(intent);
                ad.dismiss();
                break;
            default:
                break;
        }
    }

    AlertDialog ad;

    public void showMoreDialog() {
        if (ad != null) {
            ad.show();
            return;
        }
        ad = new AlertDialog.Builder(this).create();
        ad.show();
        ad.getWindow().setGravity(Gravity.BOTTOM);
        View v = View.inflate(this, R.layout.btn_more_dialog, null);
        v.findViewById(R.id.onpen_on_browse_lay).setOnClickListener(this);
        v.findViewById(R.id.refresh_lay).setOnClickListener(this);
        v.findViewById(R.id.cancel_btn).setOnClickListener(this);
        ad.setContentView(v, new ViewGroup.LayoutParams(DisplayMetricsTool.getInstance().getWidthPixels(), ViewGroup.LayoutParams.WRAP_CONTENT));
    }

}