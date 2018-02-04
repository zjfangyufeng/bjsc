package com.ddfun.bjsc.news;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.ddfun.bjsc.BDMSSPActivityModel;
import com.ddfun.bjsc.IBDMSSPActivityView;
import com.ddfun.bjsc.bean.NewsBean;
import com.ddfun.bjsc.bean.NewsCategoryBean;
import com.ff.common.ImmediatelyToast;
import com.ff.common.MyConstant;

/**
 * Created by fangyufeng on 2015/9/22.
 */
public class NewsDetailActivityPresenter {
    INewsDetailActivityView iView;
    NewsDetailActivityModel iModel;

    public NewsDetailActivityPresenter(INewsDetailActivityView iView) {
        this.iView = iView;
        iModel = new NewsDetailActivityModel();
    }

    public void initData(final String fid) {
        iView.showLoadingLayout();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bundle result = iModel.initList(fid);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        boolean aBoolean = result.getBoolean(MyConstant.ISSUCCESS);
                        if (aBoolean) {
                            iView.setData(iModel.bean);
                            iView.showSuccessLayout();
                        } else {
                            ImmediatelyToast.showLongMsg(result.getString("msg"));
                            iView.showErrorLayout();
                        }
                    }
                });
            }
        }).start();
    }

}
