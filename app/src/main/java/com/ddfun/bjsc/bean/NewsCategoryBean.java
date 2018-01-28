package com.ddfun.bjsc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fangyufeng on 2015/11/12.
 */
public class NewsCategoryBean implements Parcelable {
    public int position;
    public int sortid;
    public String name;
    public String type = "news";

    public NewsCategoryBean(Parcel in) {
        position = in.readInt();
        sortid = in.readInt();
        name = in.readString();
        type = in.readString();
    }

    public static final Creator<NewsCategoryBean> CREATOR = new Creator<NewsCategoryBean>() {
        @Override
        public NewsCategoryBean createFromParcel(Parcel in) {
            return new NewsCategoryBean(in);
        }

        @Override
        public NewsCategoryBean[] newArray(int size) {
            return new NewsCategoryBean[size];
        }
    };

    public NewsCategoryBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeInt(sortid);
        dest.writeString(name);
        dest.writeString(type);
    }
}
