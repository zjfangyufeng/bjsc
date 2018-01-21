package com.ff.common.model;

import com.ff.common.DisplayMetricsTool;
import com.ff.common.PhoneUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangyufeng on 2015/11/12.
 */
public class UserEnvironment{

    public static final String TYPEREG = "reg";
    public static final String TYPELOGIN = "login";
    public static final String TYPEEXCHANGE = "exchange";
    public static final String TYPEVERIFICATIONCODE_REGIST = "typeverificationcode_regist";
    public static final String TYPEVERIFICATIONCODE_FINDPWD = "typeverificationcode_findpwd";
    public static final String SET_WITHDRAW_PASSWORD = "set_withdraw_password";
    public static final String TYPE_VERIFICATION_CODE_WX_TRANSFER_OUT = "type_verification_code_wx_transfer_out";


    public UserEnvironment(String type) {
        try {
            this.type = type;
            user_id = UserInfo.getUserId();
            model = PhoneUtils.getPhoneModel();
            version = PhoneUtils.getSystemVer();
            screen = DisplayMetricsTool.getInstance().getWidthPixels()+"x"+DisplayMetricsTool.getInstance().getHeightPixels();
            ssid = PhoneUtils.getSSID();
            bssid = PhoneUtils.getBSSID();
            number = PhoneUtils.getImsi();
            imei = PhoneUtils.getImei();
//            whole_imei = PhoneUtils.getWholeImei();
            memlist = PhoneUtils.getMemlist();
            mac = PhoneUtils.getMacAddress();
//            create_ip = PhoneUtils.getIp();
        }catch (Exception e){
        }
    }

    String user_id;
//    String create_ip;
    String mac;
    String model;
    String version;
    String screen;
    String ssid;
    String bssid;
    String gps;
    String number;
    String imei;
//    String whole_imei;
    List<String> memlist = new ArrayList<>();
    String type;
}
