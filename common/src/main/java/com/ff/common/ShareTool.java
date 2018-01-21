package com.ff.common;

import android.preference.PreferenceManager;

import com.ff.common.application.ApplicationProxy;

/**
 * Created by fangyufeng on 2016/8/18.
 */
public class ShareTool {

    public static String getShare_url_prefix() {
        return PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getString("share_url", "http://www.doudou.com/share.html");
    }

    public static String getShare_url(String id) {
        return getShare_url_prefix()+"?sfrom=android&id="+id;
    }

}
