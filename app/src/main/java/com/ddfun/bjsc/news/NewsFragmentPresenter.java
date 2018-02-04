package com.ddfun.bjsc.news;

import android.os.Bundle;
import android.os.Parcelable;

import com.ddfun.bjsc.bean.NewsBean;
import com.ff.common.ImmediatelyToast;
import com.ff.common.MyConstant;

import java.util.ArrayList;

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
        iModel = new NewsFragmentModel();
    }

    public void initData(final int sortid,final int page) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(iModel.initData(sortid,page));
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

    public void fetchData(final int sortid,final int page) {
        Observable.create(new Observable.OnSubscribe<Bundle>() {
            @Override
            public void call(Subscriber<? super Bundle> subscriber) {
                subscriber.onNext(iModel.fetchData(sortid,page));
            }
        })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bundle>() {
                    @Override
                    public void call(Bundle s) {
                        if (MyConstant.SUCCESS.equals(s.getString(MyConstant.ISSUCCESS))) {
                            ArrayList<NewsBean> data = s.getParcelableArrayList("data");
                            iView.addData(data);
                        } else {
                            ImmediatelyToast.showLongMsg("获取数据失败");
                        }
                    }
                });
    }

}
