package com.ddfun.bjsc;

import com.ddfun.bjsc.bean.BDMSSPCategoryBean;
import com.ff.common.mvp_v.ILoadingView;

import java.util.List;


/**
 * Created by fangyufeng on 2015/9/22.
 */
public interface IBDMSSPActivityView extends ILoadingView {

    void setData(List<BDMSSPCategoryBean> l);

}
