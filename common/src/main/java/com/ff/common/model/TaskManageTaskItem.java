package com.ff.common.model;

import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;

import com.ff.common.utils.CountDownCallback;

import java.util.List;

/**
 * Created by fangyufeng on 2015/11/12.
 */
public class TaskManageTaskItem implements TaskType,Parcelable {
    public String id;
    public String name;
    public String title;
    public String dget;
    public String vget;
    public String image_thumb;
    public String intro;
    public int status;// 0 代表 今天任务没开始或进行中， 1 代表今天任务中断了，今天任务失败  2 代表任务永久失败 （卸载或者第一天答题失败） 3 代表今天任务完成 4 代表任务完结
    public String cpdate;//2016-06-08形式日期字段
    public String type;// 答题 或 签到
    public String apkname;
    public int need_run_time = 30;
    public String app_logo;
    public String app_url;
    public String ut_id;

    int task_remain;//任务剩余字段 -1 不限量 0 下时段还有 -66 真没了
    int quota; // 签到任务剩余份额 sign task use only 同上
    public String expire_time;//任务过期时间
    public String about_to_start_time;//明日几点开始字段
    public String got_reward;//已获得奖励字段
    String is_doing;//1 进行中 否则不是

    public String operation_request_short;
    public String today_operation_request; // 今天试玩要求
    public String size;

    /*@fileId @file 图片地址*/
    public List<String> app_shot_images;

    public String total_reward;
    public String today_preview_reward;
    public String preview_time; // 明天任务开始时间
    public String preview_reward; // 明天任务奖励
    public String today_reward; // 今天任务奖励
    public String msg; // 任务完结时的贺词

    public int day; // 代表第几天， 0 代表第一天
    public List<Stage> timeline;//rename to timeline from stages
    public List<Rule> rule;

    public int relative_screenshot_task_id; // 相关截图任务ID 不为0时显示界面
    public String relative_screenshot_task_reward; // 相关截图任务奖励
    public boolean mine;
    public boolean isFillingTask;
    public boolean novice;
    public transient CountDownCallback callBack;

    public boolean isDoing(){
        return "1".equals(is_doing)?true:false;
    }

    transient CountDownTimer  countDownTimer;

    public boolean startCountDown(){
        if(isDoing() && TaskManageTaskItem.QGET.equals(type)){
            stopCountDown();
            countDownTimer = new CountDownTimer(Integer.parseInt(expire_time) * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(callBack!=null)
                        callBack.onTick((int) (millisUntilFinished/1000));
                }

                @Override
                public void onFinish() {
                    if(callBack!=null)
                        callBack.onFinish();
                }
            };
            countDownTimer.start();
            return true;
        }
        return false;
    }

    public void stopCountDown(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TaskManageTaskItem){
            TaskManageTaskItem item = (TaskManageTaskItem) o;
            return item.id.equals(this.id);
        }
        return false;
    }

    public boolean isNoRemain(){
        if(task_remain == 0 || task_remain == -66)
            return true;
        return false;
    }

    public boolean isNoRemainForever(){
        if(task_remain == -66)
            return true;
        return false;
    }

    public boolean isSignTaskNoRemain(){
        if(quota == 0 || quota == -66)
            return true;
        return false;
    }

    public boolean isSignTaskNoRemainForever(){
        if(quota == -66)
            return true;
        return false;
    }

    public int getTask_remain() {
        return task_remain == -1 ? 99:task_remain;
    }

    public int getSignTask_remain() {
        return quota == -1 ? 99:quota;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(title);
        dest.writeString(dget);
        dest.writeString(vget);
        dest.writeString(image_thumb);
        dest.writeString(intro);
        dest.writeInt(status);
        dest.writeString(cpdate);
        dest.writeString(type);
        dest.writeString(apkname);
        dest.writeInt(need_run_time);
        dest.writeString(app_logo);
        dest.writeString(app_url);
        dest.writeString(ut_id);
        dest.writeInt(task_remain);
        dest.writeInt(quota);
        dest.writeString(expire_time);
        dest.writeString(about_to_start_time);
        dest.writeString(got_reward);
        dest.writeString(is_doing);
        dest.writeString(operation_request_short);
        dest.writeString(today_operation_request);
        dest.writeString(size);
        dest.writeList(app_shot_images);
        dest.writeString(total_reward);
        dest.writeString(today_preview_reward);
        dest.writeString(preview_time);
        dest.writeString(preview_reward);
        dest.writeString(today_reward);
        dest.writeString(msg);
        dest.writeInt(day);
        dest.writeList(timeline);
        dest.writeInt(relative_screenshot_task_id);
        dest.writeString(relative_screenshot_task_reward);
        dest.writeInt(mine?1:0);
    }

    protected TaskManageTaskItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        title = in.readString();
        dget = in.readString();
        vget = in.readString();
        image_thumb = in.readString();
        intro = in.readString();
        status = in.readInt();
        cpdate = in.readString();
        type = in.readString();
        apkname = in.readString();
        need_run_time = in.readInt();
        app_logo = in.readString();
        app_url = in.readString();
        ut_id = in.readString();
        task_remain = in.readInt();
        quota = in.readInt();
        expire_time = in.readString();
        about_to_start_time = in.readString();
        got_reward = in.readString();
        is_doing = in.readString();
        operation_request_short = in.readString();
        today_operation_request = in.readString();
        size = in.readString();
        app_shot_images = in.readArrayList(ClassLoader.getSystemClassLoader());
        total_reward = in.readString();
        today_preview_reward = in.readString();
        preview_time = in.readString();
        preview_reward = in.readString();
        today_reward = in.readString();
        msg = in.readString();
        day = in.readInt();
        timeline = in.readArrayList(Stage.class.getClassLoader());
        relative_screenshot_task_id = in.readInt();
        relative_screenshot_task_reward = in.readString();
        mine = in.readInt()!=0;
    }

    public static final Creator<TaskManageTaskItem> CREATOR = new Creator<TaskManageTaskItem>() {
        @Override
        public TaskManageTaskItem createFromParcel(Parcel in) {
            return new TaskManageTaskItem(in);
        }

        @Override
        public TaskManageTaskItem[] newArray(int size) {
            return new TaskManageTaskItem[size];
        }
    };

    public boolean isQget() {
        return TaskType.QGET.equals(type);
    }

    public boolean haveRelativeScreenshot(){
        return relative_screenshot_task_id!=0;
    }

    public boolean isAvailableStatus() {
        return status==0;
    }
}
