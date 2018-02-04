package com.ddfun.bjsc;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.ff.common.ImmediatelyToast;
import com.ff.common.MyConstant;
import com.ff.common.application.ApplicationProxy;

/**
 * Created by fangyufeng on 2015/9/22.
 */
public class BDMSSPActivityPresenter {
    IBDMSSPActivityView iView;
    BDMSSPActivityModel iModel;

    public BDMSSPActivityPresenter(IBDMSSPActivityView iView) {
        this.iView = iView;
        iModel = new BDMSSPActivityModel();
    }

    public void initData() {
        iView.showLoadingLayout();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bundle result = iModel.initList();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        boolean aBoolean = result.getBoolean(MyConstant.ISSUCCESS);
                        if (aBoolean) {
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
