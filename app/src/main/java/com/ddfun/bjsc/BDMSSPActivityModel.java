package com.ddfun.bjsc;

import android.os.Bundle;

import com.ddfun.bjsc.bean.BDMSSPCategoryBean;
import com.ff.common.MyConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangyufeng on 2015/9/23.
 */
public class BDMSSPActivityModel {

    public List<BDMSSPCategoryBean> list = new ArrayList<>();

    public Bundle initList() {
        Bundle result = new Bundle();
        List<BDMSSPCategoryBean> list = new ArrayList<>();
        BDMSSPCategoryBean bankBean = new BDMSSPCategoryBean();
        bankBean.id= 1;
        bankBean.name= "七乐彩";
        bankBean.url= "http://i.sdcp.cn/zst/qlc.do";
        list.add(bankBean);
        bankBean = new BDMSSPCategoryBean();
        bankBean.id= 2;
        bankBean.name= "双色球";
        bankBean.url= "http://i.sdcp.cn/zst/ssq.do";
        list.add(bankBean);
        bankBean = new BDMSSPCategoryBean();
        bankBean.id= 3;
        bankBean.name= "3D";
        bankBean.url= "http://i.sdcp.cn/zst/3d.do";
        list.add(bankBean);
        bankBean = new BDMSSPCategoryBean();
        bankBean.id= 4;
        bankBean.name= "群英会";
        bankBean.url= "http://i.sdcp.cn/zst/qyh.do";
        list.add(bankBean);
        result.putBoolean(MyConstant.ISSUCCESS, true);
        this.list = list;
        return result;
    }

}
