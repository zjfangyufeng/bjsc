package com.ddfun.bjsc.news;

import com.ff.common.MyConstant;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by fangyufeng on 2015/9/22.
 */
public class NewsFragmentPresenter {
    INewsFragmentView iView;
    public NewsFragmentModel iModel;

    public NewsFragmentPresenter(INewsFragmentView iView) {
        this.iView = iView;
        iModel = NewsFragmentModel.getInstance();
    }

    public void initData() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(iModel.initData());
            }
        })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        iView.showLoadingLayout();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (MyConstant.SUCCESS.equals(s)) {
                            iView.setData(iModel.data);
                            iView.showSuccessLayout();
                        } else {
                            iView.showErrorLayout();
                        }
                    }
                });
    }

}
