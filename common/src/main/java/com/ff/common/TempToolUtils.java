package com.ff.common;

import com.ff.common.model.NoviceCostBean;
import com.ff.common.model.UserInfo;

import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by fangyufeng on 2016/8/18.
 */
public class TempToolUtils {

    /*获取图文分享标题*/
    public static String getDynamicShareTitle() {
        String title;
        Random random = new Random();
        int index;
        if (NoviceCostBean.regTxt != null && NoviceCostBean.regTxt.size() > 0) {
            index = random.nextInt(NoviceCostBean.regTxt.size());
            title = NoviceCostBean.regTxt.get(index);
        } else {
            index = random.nextInt(MyConstant.words.length);
            title = MyConstant.words[index];
        }
        return title;
    }

    public static String MD5(String task_id){
        MessageDigest md5;
        String s = UserInfo.getUserId()+task_id+"cf8ba055480e";
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes());
            byte[] digest = md5.digest();
            String result = ToolUtils.byteArrayToHex(digest);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
