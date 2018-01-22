package com.ddfun.bjsc.bean;

/**
 * Created by Ace on 2018/1/22.
 */

public class MessageBean {
    public boolean success;
    public AppConfig AppConfig;

    public class AppConfig {
        public String PushKey;
        public String ShowWeb; //0不跳转， 1跳转
        public String Url;

        public boolean shouldShow(){
            return "1".equals(ShowWeb);
        }
    }
}
