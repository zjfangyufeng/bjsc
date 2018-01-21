package com.ff.common.utils;

import android.database.Cursor;
import android.net.Uri;

import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.model.SMSBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangyufeng on 2016/6/8.
 */
public class SMSUtil {

    public List<SMSBean> list = new ArrayList<>();

    public SMSUtil() {
        Cursor cur = null;
        try {
            cur = ApplicationProxy.getInstance().getContext().getContentResolver().query(Uri.parse("content://sms/"), new String[]{"body", "date", "type"}, null, null, "date desc");
            if (null == cur)
                return;
            while (cur.moveToNext()) {
                String body = cur.getString(cur.getColumnIndex("body"));
                if(!ToolUtils.isNull(body) && body.contains("验证码") && !body.contains("银行")){
                    SMSBean smsBean = new SMSBean();
                    smsBean.type = cur.getString(cur.getColumnIndex("type"));
                    smsBean.date = cur.getString(cur.getColumnIndex("date"));
                    smsBean.body = body;
                    list.add(smsBean);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }

//    public boolean haveSMS(String sms_key){
//        if(list.size()>0 && !ToolUtils.isNull(sms_key)){
//            for(SMSBean bean : list){
//                if( bean.body.contains(sms_key)){
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

}
