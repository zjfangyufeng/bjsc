package com.ff.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fangyufeng on 2015/9/28.
 */
public class DownloadTaskBean implements Serializable{
    private static final long serialVersionUID = 0L;

    public String task_id;
    public String app_logo;
    public String app_name;
    public String package_name;
    public String app_title;
    public String app_size;
    public String app_intro;
    public String app_url;
    public String ccount;
    public String content = "";
    public String total;
    public int need_run_time = 30;

    /*@fileId @file 图片地址*/
    public List<HashMap<String,String>> app_image_list;

    public DownloadTaskBean(TaskManageTaskItem item) {
        task_id = item.id;
        app_logo = item.app_logo;
        app_name = item.name;
        package_name = item.apkname;
        app_url = item.app_url;
        need_run_time = item.need_run_time;
    }

    public DownloadTaskBean(String task_id,String app_name,String package_name,String app_url) {
        this.task_id = task_id;
        this.app_name = app_name;
        this.package_name = package_name;
        this.app_url = app_url;
    }
}
