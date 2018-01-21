package com.ff.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ff.common.ToolUtils;

/**
 * Created by fangyufeng on 2015/9/14.
 */
public class TaskItem implements Parcelable, TaskType {
    public String id;
    public String name;
    public String package_name;
    String filepath;
//    public String sms_key;//注册短信关键字 服务端过滤 客户端并不用....
    public String title;
    public String price_show;
    public String image_thumb;
    public String type_id;
    /*dtask 下载 vget: 签到任务 vtask 浏览 ntask 分享 ztask 转发*/
    public String type;
    public int need_run_time = 30;
    /**
     * 分享模式 1表示链接  2表示加水印图片  3表示不加水印图片
     */
    public String share_mode;
    /*分享类型 1 微信朋友圈 2 微信好友 3 qq空间  4 qq好友 */
    public String share_type;
    public String share_img_url;
    public String is_free;
    public String vurl;//浏览任务url
    public String vurl_z;//浏览任务转发url，转发任务才有
    public int mine;// 0不是从我们这下载的
    public int day;

    public static final int OTHERS = 0; // 不是从我们这下载的

    public boolean isFree(){// 0 收费任务
        return "0".equals(is_free)?false:true;
    }

    public boolean isRewardsEnable(){
        boolean result = false;
        try {
            if (ToolUtils.isNull(price_show) || "0".equals(price_show) || "0.0".equals(price_show) || "0.00".equals(price_show)) {
                result = false;
            } else {
                result = true;
            }
        }catch (Exception e){
        }
        return result;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilepath() {
        return filepath;
    }

    public boolean isShareLink(){
        return "1".equals(share_mode)?true:false;
    }

    public boolean isShareWaterMarkImage(){
        return "2".equals(share_mode)?true:false;
    }

    public boolean isShareImage(){
        return "3".equals(share_mode)?true:false;
    }

    public boolean isShareToWXTimeline(){// 1 微信朋友圈
        return "1".equals(share_type)?true:false;
    }

    public boolean isShareToWXFriend(){// 2 微信好友
        return "2".equals(share_type)?true:false;
    }

    public boolean isShareToQzone(){// 3 QQ空间
        return "3".equals(share_type)?true:false;
    }

    public boolean isShareToQQ(){// 4 QQ好友
        return "4".equals(share_type)?true:false;
    }

    public static final Creator<TaskItem> CREATOR = new Creator<TaskItem>() {
        @Override
        public TaskItem createFromParcel(Parcel source) {
            TaskItem item = new TaskItem();
            item.id = source.readString();
            item.name = source.readString();
            item.package_name = source.readString();
            item.filepath = source.readString();
            item.price_show = source.readString();
            item.image_thumb = source.readString();
            item.type_id = source.readString();
            item.type = source.readString();
            item.share_mode=source.readString();
            item.share_type = source.readString();
            item.share_img_url = source.readString();
            item.is_free = source.readString();
            item.vurl = source.readString();
            item.vurl_z=source.readString();
            item.mine = source.readInt();
            item.day = source.readInt();
            return item;
        }

        @Override
        public TaskItem[] newArray(int size) {
            return new TaskItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(package_name);
        dest.writeString(filepath);
        dest.writeString(price_show);
        dest.writeString(image_thumb);
        dest.writeString(type_id);
        dest.writeString(type);
        dest.writeString(share_mode);
        dest.writeString(share_type);
        dest.writeString(share_img_url);
        dest.writeString(is_free);
        dest.writeString(vurl);
        dest.writeString(vurl_z);
        dest.writeInt(mine);
        dest.writeInt(day);
    }
}
