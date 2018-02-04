package com.ddfun.bjsc.news;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddfun.bjsc.R;
import com.ddfun.bjsc.bean.NewsBean;
import com.ddfun.bjsc.bean.NewsDetailsBean;
import com.ff.common.DisplayMetricsTool;
import com.ff.common.application.ApplicationProxy;
import com.ff.imgloader.ImageLoader;

import java.util.List;

public class NewsDetailActivity extends Activity implements INewsDetailActivityView, View.OnClickListener {

    View loading_progressBar,net_err_lay,layout_success;
    NewsDetailActivityPresenter presenter;
    TextView tv_title,tv_content;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        net_err_lay =  findViewById(R.id.net_err_lay);
        layout_success =  findViewById(R.id.layout_success);
        findViewById(R.id.fail_btn).setOnClickListener(this);

        iv = (ImageView) findViewById(R.id.iv);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        presenter = new NewsDetailActivityPresenter(this);
        presenter.initData(getFid());
    }

    private String getFid(){
        return getIntent().getStringExtra("fid");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.maintab_activity_head_left_btn:
                finish();
                break;
            case R.id.fail_btn:
                presenter.initData(getFid());
                break;

        }
    }

    @Override
    public void showLoadingLayout() {
        loading_progressBar.setVisibility(View.VISIBLE);
        net_err_lay.setVisibility(View.GONE);
        layout_success.setVisibility(View.GONE);
    }

    @Override
    public void showSuccessLayout() {
        loading_progressBar.setVisibility(View.GONE);
        net_err_lay.setVisibility(View.GONE);
        layout_success.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorLayout() {
        loading_progressBar.setVisibility(View.GONE);
        net_err_lay.setVisibility(View.VISIBLE);
        layout_success.setVisibility(View.GONE);
    }

    @Override
    public void setData(NewsDetailsBean bean) {
        tv_title.setText(Html.fromHtml(bean.title));
        tv_content.setText(Html.fromHtml(bean.freecontent, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(final String source) {
                final HtmlDrawable result = new HtmlDrawable();
                ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap = ImageLoader.getInstance().loadImageFromDiskThenInternet(source, ImageLoader.FULLWIDTH, ImageLoader.FULLWIDTH, false);
                        if(bitmap!=null){
                            result.setBitmap(bitmap);
                            tv_content.post(new Runnable() {
                                @Override
                                public void run() {
                                    tv_content.setText(tv_content.getText());
                                }
                            });
                        }
                    }
                });
                return result;
            }
        },null));
    }

    class HtmlDrawable extends BitmapDrawable{

        Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if(bitmap!=null)
                canvas.drawBitmap(bitmap,0,0,null);
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            setBounds(0,0,bitmap.getScaledWidth(DisplayMetricsTool.getInstance().getDisplayMetrics()),bitmap.getScaledHeight(DisplayMetricsTool.getInstance().getDisplayMetrics()));
        }

    }

}
