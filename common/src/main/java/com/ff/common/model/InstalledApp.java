package com.ff.common.model;

/**
 * Created by fangyufeng on 2016/4/7.
 */
public class InstalledApp{
    String en; //包名
    String cn; //应用名
    long firstInstallTime;

    public InstalledApp(String en, String cn,long firstInstallTime) {
        this.en = en;
        this.cn = cn;
        this.firstInstallTime = firstInstallTime;
    }
}
