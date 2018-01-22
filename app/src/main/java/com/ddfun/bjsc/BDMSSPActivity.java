package com.ddfun.bjsc;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddfun.bjsc.bean.BDMSSPCategoryBean;
import com.ff.common.activity.BaseActivity;
import com.ff.common.mvp_v.ILoadingDialogView;
import com.ff.common.utils.WebViewUtil;

import java.util.List;

public class BDMSSPActivity extends BaseActivity implements View.OnClickListener,IBDMSSPActivityView, ILoadingDialogView {

    WebView mWebView;

    RecyclerView recyclerView;

    View loading_progressBar,net_err_lay,fail_btn;

    ProgressBar pb;

    BDMSSPActivityPresenter presenter;
    RecyclerView.Adapter<ViewHolder> adapter;
    List<BDMSSPCategoryBean> l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bdmssp);
        mWebView = (WebView) findViewById(R.id.wv);
        recyclerView = (RecyclerView) findViewById(R.id.layout_head);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        net_err_lay =  findViewById(R.id.net_err_lay);
        findViewById(R.id.fail_btn).setOnClickListener(this);
        initWebView();

        l = getIntent().getParcelableArrayListExtra("data");
        adapter = new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(BDMSSPActivity.this).inflate(R.layout.bdmssp_activity_item_lay,parent,false));
            }
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.setData(l.get(position));
                if(position==0 && selected ==null) {
                    holder.onClick(null);
                }
            }
            @Override
            public int getItemCount() {
                return l == null ? 0 : l.size();
            }
        };

        recyclerView.setLayoutManager(new GridLayoutManager(this, l.size()));
        recyclerView.setAdapter(adapter);

        presenter = new BDMSSPActivityPresenter(this);
        presenter.initData();
    }

    private void loadWebViewData(BDMSSPCategoryBean bean) {
        mWebView.loadUrl(bean.url);
    }

    ViewHolder selected;
    int selectedId;

    ProgressDialog pd;

    @Override
    public void showWaitingDialog() {
        if(pd==null){
            pd = new ProgressDialog(this);
            pd.setCancelable(true);
        }
        pd.show();
    }

    @Override
    public void dismissWaitingDialog() {
        if(pd!=null&&pd.isShowing())
            pd.dismiss();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name;

        BDMSSPCategoryBean bean;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_name.setOnClickListener(this);
        }

        public void setData(BDMSSPCategoryBean bean){
            this.bean = bean;
            tv_name.setText(bean.name);
            if(selectedId!=0) {
                if(selectedId != bean.id) {
                    tv_name.setSelected(false);
                    tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                }else {
                    tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                    tv_name.setSelected(true);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if(selectedId!=0){
                if(selectedId == bean.id) return;
                selected.tv_name.setSelected(false);
                selected.tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            }
            selected = this;
            selectedId = bean.id;
            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
            tv_name.setSelected(true);
            loadWebViewData(bean);
        }
    }

    public void initWebView() {
        pb = (ProgressBar) findViewById(R.id.pb);
        WebViewUtil.initWebview(this, mWebView, pb);
        WebViewUtil.addGetUserIdJSInterface(mWebView);
        WebViewUtil.addSetBalanceJSInterface(this,mWebView);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fail_btn) {
            presenter.initData();
        }
    }

    @Override
    public void showLoadingLayout() {
        loading_progressBar.setVisibility(View.VISIBLE);
        net_err_lay.setVisibility(View.GONE);
        mWebView.setVisibility(View.GONE);
    }

    @Override
    public void showSuccessLayout() {
        loading_progressBar.setVisibility(View.GONE);
        net_err_lay.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorLayout() {
        loading_progressBar.setVisibility(View.GONE);
        net_err_lay.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
    }

    @Override
    public void setData(List<BDMSSPCategoryBean> l) {
        this.l = l;
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}
