package com.ddfun.bjsc;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ddfun.bjsc.bean.BDMSSPCategoryBean;
import com.ddfun.bjsc.bean.NewsCategoryBean;
import com.ddfun.bjsc.news.NewsFragment;
import com.ff.common.mvp_v.ILoadingDialogView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends FragmentActivity implements View.OnClickListener,IBDMSSPActivityView, ILoadingDialogView {

    RecyclerView recyclerView;
    ViewPager vp;

    View loading_progressBar,net_err_lay,fail_btn;

    BDMSSPActivityPresenter presenter;
    RecyclerView.Adapter<ViewHolder> adapter;
    List<NewsCategoryBean> l = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        recyclerView = (RecyclerView) findViewById(R.id.layout_head);
        vp = (ViewPager) findViewById(R.id.vp);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        net_err_lay =  findViewById(R.id.net_err_lay);
        findViewById(R.id.fail_btn).setOnClickListener(this);

        NewsCategoryBean bean = new NewsCategoryBean();
        bean.position = 0;
        bean.name = "双色球";
        bean.sortid = 4;
        l.add(bean);
        bean = new NewsCategoryBean();
        bean.position = 1;
        bean.name = "3D";
        bean.sortid = 6;
        l.add(bean);
        bean = new NewsCategoryBean();
        bean.position = 2;
        bean.name = "大乐透";
        bean.sortid = 5;
        l.add(bean);

        adapter = new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(NewsActivity.this).inflate(R.layout.bdmssp_activity_item_lay,parent,false));
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

        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return NewsFragment.newInstance(l.get(position));
            }

            @Override
            public int getCount() {
                return l.size();
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, l.size()));
        recyclerView.setAdapter(adapter);

        presenter = new BDMSSPActivityPresenter(this);
        presenter.initData();
    }

    private void resetCategoryView(NewsCategoryBean bean) {
        Toast.makeText(this,bean.name,Toast.LENGTH_LONG).show();
        vp.setCurrentItem(bean.position);
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

    @Override
    public void setData(List<BDMSSPCategoryBean> l) {

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name;

        NewsCategoryBean bean;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_name.setOnClickListener(this);
        }

        public void setData(NewsCategoryBean bean){
            this.bean = bean;
            tv_name.setText(bean.name);
            if(selectedId!=0) {
                if(selectedId != bean.sortid) {
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
                if(selectedId == bean.sortid) return;
                selected.tv_name.setSelected(false);
                selected.tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            }
            selected = this;
            selectedId = bean.sortid;
            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
            tv_name.setSelected(true);
            resetCategoryView(bean);
        }
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
        vp.setVisibility(View.GONE);
    }

    @Override
    public void showSuccessLayout() {
        loading_progressBar.setVisibility(View.GONE);
        net_err_lay.setVisibility(View.GONE);
        vp.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorLayout() {
        loading_progressBar.setVisibility(View.GONE);
        net_err_lay.setVisibility(View.VISIBLE);
        vp.setVisibility(View.GONE);
    }

}
