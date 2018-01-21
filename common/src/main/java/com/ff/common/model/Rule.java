package com.ff.common.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangjiang on 2016/6/8.
 */
public class Rule implements Parcelable {
    public String title;
    public String money_txt;

    public Rule() {

    }

    // test
    public Rule(String title, String money_txt) {
        this.title = title;
        this.money_txt = money_txt;
    }

    public static final Creator<Rule> CREATOR = new Creator<Rule>() {
        @Override
        public Rule createFromParcel(Parcel source) {
            Rule rule = new Rule();
            rule.title = source.readString();
            rule.money_txt = source.readString();
            return rule;
        }

        @Override
        public Rule[] newArray(int size) {
            return new Rule[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(money_txt);
    }
}
