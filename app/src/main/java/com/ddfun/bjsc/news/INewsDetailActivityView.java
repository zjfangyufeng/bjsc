package com.ddfun.bjsc.news;

import com.ddfun.bjsc.bean.NewsDetailsBean;
import com.ff.common.mvp_v.ILoadingView;

/**
 * Created by fangyufeng on 2015/9/22.
 */
public interface INewsDetailActivityView extends ILoadingView {

    void setData(NewsDetailsBean bean);
}
