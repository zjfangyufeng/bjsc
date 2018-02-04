package com.ddfun.bjsc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/1/28.
 */

public class NewsBean implements Parcelable{
    public String aid;
    public String type;
    public String canpublic;
    public String hits;
    public String title;
    public String nickname;
    public String ispublic;
    public String cover;
    public String paytype;
    public String publishtime;
    public String expiretime;
    public String typename;
    public String headimg;
    public String shortcontent;
    public String threehitrate;
    public String freecontent;
    public String uname;
    public String comment;
    public String url;

    @Override
    public boolean equals(Object o) {
        if(o instanceof NewsBean){
            return ((NewsBean)o).aid.equals(aid);
        }
        return super.equals(o);
    }

    protected NewsBean(Parcel in) {
        aid = in.readString();
        type = in.readString();
        canpublic = in.readString();
        hits = in.readString();
        title = in.readString();
        nickname = in.readString();
        ispublic = in.readString();
        cover = in.readString();
        paytype = in.readString();
        publishtime = in.readString();
        expiretime = in.readString();
        typename = in.readString();
        headimg = in.readString();
        shortcontent = in.readString();
        threehitrate = in.readString();
        freecontent = in.readString();
        uname = in.readString();
        comment = in.readString();
        url = in.readString();
    }

    public static final Creator<NewsBean> CREATOR = new Creator<NewsBean>() {
        @Override
        public NewsBean createFromParcel(Parcel in) {
            return new NewsBean(in);
        }

        @Override
        public NewsBean[] newArray(int size) {
            return new NewsBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(aid);
        dest.writeString(type);
        dest.writeString(canpublic);
        dest.writeString(hits);
        dest.writeString(title);
        dest.writeString(nickname);
        dest.writeString(ispublic);
        dest.writeString(cover);
        dest.writeString(paytype);
        dest.writeString(publishtime);
        dest.writeString(expiretime);
        dest.writeString(typename);
        dest.writeString(headimg);
        dest.writeString(shortcontent);
        dest.writeString(threehitrate);
        dest.writeString(freecontent);
        dest.writeString(uname);
        dest.writeString(comment);
        dest.writeString(url);
    }
}
