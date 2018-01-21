package com.ff.common.application;

/**
 * Created by fangyufeng on 2016/8/16.
 */
public class ApplicationProxy {
    static IApplication application;

    public static void initInstance(IApplication a){
        application = a;
    }

    public static IApplication getInstance(){
        return application;
    }




}
