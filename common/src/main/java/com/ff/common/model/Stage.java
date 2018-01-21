package com.ff.common.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangjiang on 2016/6/8.
 */
public class Stage implements Parcelable {
    public String title; // 第一天答题
    public String operation_request;
    public int status; // unstarted: 0, 1, 2 ; finished: 3; failed: 4
    public String guide; // 6小时开始 、 已完成 、 未开始 等
    public String reward; // 0.1-1元
    public int tomorrow; // 默认是 0 后天的未开始， 1 代表明天的未开始

    public final static int FINISHED    = 0;
    public final static int UNSTARTED   = 1;
    public final static int FAILED      = 2;

    public int getState() {
        switch (status) {
            case 0:
            case 1:
            case 2:
                return UNSTARTED;
            case 3:
                return FINISHED;
            case 4:
                return FAILED;
            default:
                return FAILED;
        }
    }

    public static final Creator<Stage> CREATOR = new Creator<Stage>() {
        @Override
        public Stage createFromParcel(Parcel source) {
            Stage bean = new Stage();
            bean.title = source.readString();
            bean.operation_request = source.readString();
            bean.status = source.readInt();
            bean.guide = source.readString();
            bean.reward = source.readString();
            bean.tomorrow = source.readInt();
            return bean;
        }

        @Override
        public Stage[] newArray(int size) {
            return new Stage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(operation_request);
        dest.writeInt(status);
        dest.writeString(guide);
        dest.writeString(reward);
        dest.writeInt(tomorrow);
    }
}
