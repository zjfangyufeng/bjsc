package com.ddfun.bjsc.news;

import com.ddfun.bjsc.bean.NewsBean;
import com.ff.common.mvp_v.ILoadingView;

import java.util.List;

/**
 * Created by fangyufeng on 2015/9/22.
 */
public interface INewsFragmentView extends ILoadingView {

    void setData(List<NewsBean> data);

    void showWaitingDialog();
    void dismissWaitingDialog();


}
