package com.ff.common.model;

/**
 * Created by fangyufeng on 2015/11/12.
 */
public class InviteIncomeBean{
    public String invite_money;//邀请收入
    public boolean show_ios_invitation;//是否显示ios邀请框
    public String master_android;//空 不显示
    public String master_ios;
    public int is_master;//0 不是
    public String official_service_url;//打开客服扣扣链接
    public String invite_money_ios;//ios邀请收入
    public String invite_code;
    public String percentage_ios; //ios邀请收入提成比例
    public String invite_income;//累计邀请收益
    public String pending_reward;//待入账收入
    public String invite_count;//累计成功邀请人数
    public String today_invite_count;//今日累计成功邀请人数
    public String today_invite_income;//今日累计成功邀请收益

    public boolean isMaster() {
        return is_master>0;
    }
}
