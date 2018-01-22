package com.ddfun.bjsc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fangyufeng on 2015/11/12.
 */
public class BDMSSPCategoryBean implements Parcelable {
    public int id;
    public String name;
    public String url;

    public BDMSSPCategoryBean() {
    }

    protected BDMSSPCategoryBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
        url = in.readString();
    }

    public static final Creator<BDMSSPCategoryBean> CREATOR = new Creator<BDMSSPCategoryBean>() {
        @Override
        public BDMSSPCategoryBean createFromParcel(Parcel in) {
            return new BDMSSPCategoryBean(in);
        }

        @Override
        public BDMSSPCategoryBean[] newArray(int size) {
            return new BDMSSPCategoryBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(url);
    }
}
