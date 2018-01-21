package com.ff.common.model;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.http.MyHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserInfo {
	static UserInfo userInfo;

	public static UserInfo getUserInfo() {
		if (userInfo == null) {
			try {
				initUserInfoLocal();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userInfo;
	}

	String account;
	String password;
	String userId;
	String token;

	String invite_code;
	String balance;
	String daibal;
	String mobile;
	String sex;
	String birthday;

	String zhifb;//支付宝账号
	String realname;
	String zhifb_mobile;

	String bank_type_id;
	String bankBranch;
	String sfz_code;
	/*银行卡开户地区*/
	String area;
	String bank_account_num;
	String bank_name;
	String bank_code;
	private String WXOfficialAccounts;//微信昵称为空 未绑定 否则 已绑定微信
	private String wx_icon;//微信头像
	int hasSettledWithdrawPassword;//0未设置 否则已设置 提现密码
	private String WX_realName;
	private String withdraw_coupon;//空 为无有效提现券
//	private String WX_phoneNumber;

	public static SharedPreferences getUserInfoSharedPreferences() {
		return ApplicationProxy.getInstance().getContext().getSharedPreferences("mpu", 0);
	}

	public static SharedPreferences getCurrentUserSharedPreferences() {
		return ApplicationProxy.getInstance().getContext().getSharedPreferences(getUserId()==null?"20151114":getUserId(), 0);
	}

	public static void clearUserInfo() {
		final String userId = getUserId();
		ApplicationProxy.getInstance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				MyHttpClient.executePost(MyHttpClient.getBASE_URL()+"/app/pass/logout/"+userId, new HashMap<String,String>());
			}
		});
		userInfo = null;
		ApplicationProxy.getInstance().getContext().getSharedPreferences("mpu", 0).edit().clear()
				.commit();
	}

	public static void invalidUserInfo() {
		userInfo = new UserInfo();
		ApplicationProxy.getInstance().getContext().getSharedPreferences("mpu", 0).edit().clear()
				.commit();
	}

	public static UserInfo setUserInfo(JSONObject root, String account,
									   String password) throws JSONException {
		if (root == null)
			return null;
		UserInfo tempUserInfo = new UserInfo();
		tempUserInfo.account = account;
		tempUserInfo.password = password;
		if (!root.isNull("user_id"))
			tempUserInfo.userId = root.getString("user_id");
		if (!root.isNull("token"))
			tempUserInfo.token = root.getString("token");
		userInfo = tempUserInfo;
		return userInfo;
	}

	public static UserInfo addUserInfo(JSONObject r) throws JSONException {
		if (r == null || !isLogined())
			return null;
		JSONObject root = r.getJSONObject("data");
		if (!root.isNull("invite_code"))
			userInfo.invite_code = root.getString("invite_code");
		if (!root.isNull("balance"))
			userInfo.balance = root.getString("balance");
		if (!root.isNull("daibal"))
			userInfo.daibal = root.getString("daibal");
		if (!root.isNull("mobile"))
			userInfo.mobile = root.getString("mobile");
		if (!root.isNull("sex"))
			userInfo.sex = root.getString("sex");
		if (!root.isNull("birthday"))
			userInfo.birthday = root.getString("birthday");

		if (!root.isNull("zhifb"))
			userInfo.zhifb = root.getString("zhifb");
		if (!root.isNull("realname"))
			userInfo.realname = root.getString("realname");
		if (!root.isNull("zhifb_mobile"))
			userInfo.zhifb_mobile = root.getString("zhifb_mobile");

		if (!root.isNull("bank_id"))
			userInfo.bank_type_id = root.getString("bank_id");
		if (!root.isNull("bank"))
			userInfo.bankBranch = root.getString("bank");
		if (!root.isNull("realname"))
			userInfo.realname = root.getString("realname");
		if (!root.isNull("sfz_code"))
			userInfo.sfz_code = root.getString("sfz_code");
		if (!root.isNull("area"))
			userInfo.area = root.getString("area");
		if (!root.isNull("banknum"))
			userInfo.bank_account_num = root.getString("banknum");
		if (!root.isNull("bank_name"))
			userInfo.bank_name = root.getString("bank_name");
		if (!root.isNull("bank_code"))
			userInfo.bank_code = root.getString("bank_code");
		if (!root.isNull("WXOfficialAccounts"))
			userInfo.WXOfficialAccounts = root.getString("WXOfficialAccounts");
		if (!root.isNull("wx_icon"))
			userInfo.wx_icon = root.getString("wx_icon");
		if (!root.isNull("hasSettledWithdrawPassword"))
			userInfo.hasSettledWithdrawPassword = root.getInt("hasSettledWithdrawPassword");
		if (!root.isNull("WX_realName"))
			userInfo.WX_realName = root.getString("WX_realName");
		if (!root.isNull("withdraw_coupon"))
			userInfo.withdraw_coupon = root.getString("withdraw_coupon");
//		if (!root.isNull("WX_phoneNumber"))
//			userInfo.WX_phoneNumber = root.getString("WX_phoneNumber");

		return userInfo;
	}

	private static void initUserInfoLocal() {
		UserInfo tempUserInfo = new UserInfo();
		SharedPreferences sp = getUserInfoSharedPreferences();
		tempUserInfo.account = sp.getString("account", null);
		if (tempUserInfo.account == null
					|| TextUtils.isEmpty(tempUserInfo.account))
			return;
		tempUserInfo.password = sp.getString("password", null);
		tempUserInfo.userId = sp.getString("userId", null);
		tempUserInfo.token = sp.getString("token", null);

		tempUserInfo.invite_code = sp.getString("invite_code", null);
		tempUserInfo.balance = sp.getString("balance", null);
		tempUserInfo.daibal = sp.getString("daibal", null);
		tempUserInfo.mobile = sp.getString("mobile", null);
		tempUserInfo.sex = sp.getString("sex", null);
		tempUserInfo.birthday = sp.getString("birthday", null);

		tempUserInfo.zhifb = sp.getString("zhifb", null);
		tempUserInfo.zhifb_mobile = sp.getString("zhifb_mobile", null);
		tempUserInfo.realname = sp.getString("realname", null);

		tempUserInfo.bank_type_id = sp.getString("bank_type_id", null);
		tempUserInfo.bankBranch = sp.getString("bankBranch", null);
		tempUserInfo.realname = sp.getString("realname", null);
		tempUserInfo.area = sp.getString("area", null);
		tempUserInfo.sfz_code = sp.getString("sfz_code", null);
		tempUserInfo.bank_account_num = sp.getString("bank_account_num", null);
		tempUserInfo.bank_name = sp.getString("bank_name", null);
		tempUserInfo.bank_code = sp.getString("bank_code", null);
		tempUserInfo.WXOfficialAccounts = sp.getString("WXOfficialAccounts", null);
		tempUserInfo.wx_icon = sp.getString("wx_icon", null);
		tempUserInfo.hasSettledWithdrawPassword = sp.getInt("hasSettledWithdrawPassword", 0);
		tempUserInfo.WX_realName = sp.getString("WX_realName", null);
		tempUserInfo.withdraw_coupon = sp.getString("withdraw_coupon", null);
//		tempUserInfo.WX_phoneNumber = sp.getString("WX_phoneNumber", null);

		userInfo = tempUserInfo;
	}

	private UserInfo() {

	}

	public static void saveUserInfo() {
		if (!isLogined())
			return;
		Editor edit = getUserInfoSharedPreferences().edit().putString(
				"account", userInfo.account);
		edit.putString("password", userInfo.password);
		edit.putString("userId", userInfo.userId);
		edit.putString("token", userInfo.token);

		edit.putString("invite_code", userInfo.invite_code);
		edit.putString("balance", userInfo.balance);
		edit.putString("daibal", userInfo.daibal);
		edit.putString("mobile", userInfo.mobile);
		edit.putString("sex", userInfo.sex);
		edit.putString("birthday", userInfo.birthday);

		edit.putString("zhifb", userInfo.zhifb);
		edit.putString("zhifb_mobile", userInfo.zhifb_mobile);
		edit.putString("realname", userInfo.realname);

		edit.putString("bank_type_id", userInfo.bank_type_id);
		edit.putString("bankBranch", userInfo.bankBranch);
		edit.putString("sfz_code", userInfo.sfz_code);
		edit.putString("area", userInfo.area);
		edit.putString("bank_account_num", userInfo.bank_account_num);
		edit.putString("bank_name", userInfo.bank_name);
		edit.putString("bank_code", userInfo.bank_code);
		edit.putString("WXOfficialAccounts", userInfo.WXOfficialAccounts);
		edit.putString("wx_icon", userInfo.wx_icon);
		edit.putInt("hasSettledWithdrawPassword", userInfo.hasSettledWithdrawPassword);
		edit.putString("WX_realName", userInfo.WX_realName);
		edit.putString("withdraw_coupon", userInfo.withdraw_coupon);
//		edit.putString("WX_phoneNumber", userInfo.WX_phoneNumber);

		edit.commit();
	}

	public static boolean isLogined() {
		return ToolUtils.isNull(getUserId()) ? false : true;
	}

	public static String getUserId() {
		if (getUserInfo() != null) {
			return userInfo.getUser_id();
		}
		return null;
	}

	public static String gett() {
		if (getUserInfo() != null) {
			return userInfo.getToken();
		}
		return "";
	}

	public boolean hasSetAlipay(){
		return ToolUtils.isNull(zhifb)?false:true;
	}

	public boolean hasSetBankAccount(){
		return ToolUtils.isNull(bank_account_num)?false:true;
	}

	public static void saveAccount() {
		if (!isLogined())
			return;
		getUserInfoSharedPreferences().edit()
				.putString("account", userInfo.account).commit();
	}

	public static void savePassword() {
		if (!isLogined())
			return;
		getUserInfoSharedPreferences().edit()
				.putString("password", userInfo.password).commit();
	}

	public String getWX_realName() {
		return WX_realName;
	}

	public String getWithdrawCoupon() {
		return withdraw_coupon;
	}

	public static void saveSex() {
		if (!isLogined())
			return;
		getUserInfoSharedPreferences().edit()
				.putString("sex", userInfo.sex).commit();
	}

	public static void saveBirthday() {
		if (!isLogined())
			return;
		getUserInfoSharedPreferences().edit()
				.putString("birthday", userInfo.birthday).commit();
	}

	public static void saveBalance(String b) {
		if (!isLogined())
			return;
		userInfo.balance = b;
		getUserInfoSharedPreferences().edit()
				.putString("balance", userInfo.balance).commit();
	}

	public static void saveTobeUnfreezeMoney(String b) {
		if (!isLogined())
			return;
		userInfo.daibal = b;
		getUserInfoSharedPreferences().edit()
				.putString("daibal", userInfo.daibal).commit();
	}

//	public static void decreaseBalance(String b) {
//		if (userInfo == null)
//			return;
//		float nb = Float.parseFloat(userInfo.balance) - Float.parseFloat(b);
//		userInfo.balance = nb+"";
//		getUserInfoSharedPreferences().edit()
//				.putString("balance", userInfo.balance).commit();
//	}

	public static void saveAlipayAccount() {
		if (!isLogined())
			return;
		getUserInfoSharedPreferences().edit()
				.putString("zhifb", userInfo.zhifb)
				.putString("zhifb_mobile", userInfo.zhifb_mobile)
				.putString("realname", userInfo.realname).commit();
	}

	public static void saveWXAccount() {
		if (!isLogined())
			return;
		getUserInfoSharedPreferences().edit()
				.putString("WX_realName", userInfo.WX_realName).commit();
	}

	public static void saveWithdrawCoupon() {
		if (!isLogined())
			return;
		getUserInfoSharedPreferences().edit()
				.putString("withdraw_coupon", userInfo.withdraw_coupon).commit();
	}

	public static void saveBankAccount() {
		if (!isLogined())
			return;
		getUserInfoSharedPreferences().edit()
				.putString("bank_type_id", userInfo.bank_type_id)
				.putString("bankBranch", userInfo.bankBranch)
				.putString("realname", userInfo.realname)
				.putString("sfz_code", userInfo.sfz_code)
				.putString("area", userInfo.area)
				.putString("bank_account_num", userInfo.bank_account_num)
				.putString("bank_name", userInfo.bank_name)
				.putString("bank_code", userInfo.bank_code).commit();
	}

	public String getUser_id() {
		return userId;
	}

	public void setUser_id(String userId) {
		this.userId = userId;
	}

	public String getAccount() {
		return account;
	}

	public String getToken() {
		return token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static void setUserInfo(UserInfo userInfo) {
		UserInfo.userInfo = userInfo;
	}

	public String getInvite_code() {
		return invite_code;
	}

	public void setInvite_code(String invite_code) {
		this.invite_code = invite_code;
	}

	public String getBalance() {
		return balance;
	}

	public float getBalanceF() {
		float v = 0;
		try {
			v = Float.parseFloat(balance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getZhifb() {
		return zhifb;
	}

	public void setZhifb(String zhifb) {
		this.zhifb = zhifb;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getZhifb_mobile() {
		return zhifb_mobile;
	}

	public void setZhifb_mobile(String zhifb_mobile) {
		this.zhifb_mobile = zhifb_mobile;
	}

	public String getBank_type_id() {
		return bank_type_id;
	}

	public void setBank_type_id(String bank_type_id) {
		this.bank_type_id = bank_type_id;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBank_account_num() {
		return bank_account_num;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public void setBank_account_num(String bank_account_num) {
		this.bank_account_num = bank_account_num;
	}

	public String getSfz_code() {
		return sfz_code;
	}

	public void setSfz_code(String sfz_code) {
		this.sfz_code = sfz_code;
	}

	public String getSex() {
		if(ToolUtils.isNull(sex))
			return null;
		if(sex.equals("1")){
			return "男";
		}
		return "女";
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public boolean hasBindWXOfficialAccounts() {
		return !ToolUtils.isNull(WXOfficialAccounts);
	}

	public boolean hasSettledWithdrawPassword() {
		return hasSettledWithdrawPassword!=0;
	}

	public String getWXOfficialAccounts() {
		return WXOfficialAccounts;
	}

	public void setHasSettledWithdrawPassword() {
		hasSettledWithdrawPassword = 1;
		getUserInfoSharedPreferences().edit()
				.putInt("hasSettledWithdrawPassword", userInfo.hasSettledWithdrawPassword).commit();
	}

	public void setWXOfficialAccounts(String account) {
		WXOfficialAccounts = account;
		getUserInfoSharedPreferences().edit()
				.putString("WXOfficialAccounts", userInfo.WXOfficialAccounts).commit();
	}

	public String getWx_icon() {
		return wx_icon;
	}

	public void setWx_icon(String wx_icon) {
		this.wx_icon = wx_icon;
		getUserInfoSharedPreferences().edit()
				.putString("wx_icon", userInfo.wx_icon).commit();
	}

	public void setWXTransferOutAccount(String realName) {
		this.WX_realName = realName;
		saveWXAccount();
	}

	public void setWithdrawCoupon(String withdraw_coupon) {
		this.withdraw_coupon = withdraw_coupon;
		saveWithdrawCoupon();
	}

	public void subtractBalance(String amount) {
		try {
			float v = getBalanceF() - Float.parseFloat(amount);
			setBalance(v+"");
			saveBalance(balance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
