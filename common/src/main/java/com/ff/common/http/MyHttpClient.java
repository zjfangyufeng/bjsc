package com.ff.common.http;

import android.util.Log;

import com.ff.common.LogUtil;
import com.ff.common.PhoneUtils;
import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common.model.UserInfo;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyHttpClient {
	private static OkHttpClient client;

    public static final String SHARE_SUFFIX = "/share.html?sfrom=android&id="; //分享链接后缀
	public static String SHARE_LINK; //分享链接
	public static final String SHARE_LINK_IMG="http://www.doudou.com/static/images/share_link_img.png"; //分享小图
	public static final String SHARE_LOGO = "http://www.doudou.com/static/images/share_logo.png"; //分享应用图标

	public static String getBASE_URL(){
		return ApplicationProxy.getInstance().getBASE_URL();
	}

	public static String getActivityBoardUrl(String id){
		return ApplicationProxy.getInstance().getIMG_BASE_URL()+"post/"+id+".html";
	}

	public static String getCompleteFileUrl(String url) {
		return ApplicationProxy.getInstance().getIMG_BASE_URL() + url;
	}

	public static String getOfficialUrl(){
		return ApplicationProxy.getInstance().getIMG_BASE_URL()+"m.html";
	}

	public static String getPartnerUrl(){
		return ApplicationProxy.getInstance().getIMG_BASE_URL()+"partner.html";
	}

	public static String getFaqUrl(){
		return ApplicationProxy.getInstance().getIMG_BASE_URL()+"faq.html";
	}

	public static synchronized OkHttpClient getOkHttpClient() {
		if (null == client) {
			client= new OkHttpClient();
			client.setConnectTimeout(20, TimeUnit.SECONDS);
			client.setWriteTimeout(20, TimeUnit.SECONDS);
			client.setReadTimeout(20, TimeUnit.SECONDS);
		}
		return client;
	}

	public static JSONObject executePost(String url, Map<String,String> param){
		String result;
		try {
			OkHttpClient client = getOkHttpClient();
			addBaseParam(param);
			FormEncodingBuilderWrapper formEncodingBuilderWrapper = new FormEncodingBuilderWrapper();
			RequestBody body = formEncodingBuilderWrapper.build(param);
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.build();
			Response response = client.newCall(request).execute();
			result = response.body().string();
			LogUtil.i(url +"  \r\n  "+ result);
			if (result != null) {
				JSONObject jsonObject = new JSONObject(result);
				try {
					if("444".equals(jsonObject.getString("code"))){
						ApplicationProxy.getInstance().loginInvalid();
					}
				}catch (Exception e){
				}
				return jsonObject;
			}
		} catch (Exception e) {
//			if(!BuildConfig.isRelease)
				LogUtil.i_to_sdcard(e);
		}
		return null;
	}

	public static JSONObject executePostFile(String url,Map<String,String> param,Map<String,String> list){
		String result;
		OkHttpClient client = getOkHttpClient();
		MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
		addBaseParam(builder);
		for(Map.Entry<String,String> entry:param.entrySet()){
			if(entry.getKey()!=null && entry.getValue()!=null)
				builder.addFormDataPart(entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String,String> entry :list.entrySet()){
			String s = entry.getValue();
			String[] split = s.split("\\.");
			String suffix=null;
			if(split!=null && split.length>0){
				suffix = split[split.length-1];
			}
			builder.addFormDataPart(entry.getKey(),suffix , RequestBody.create(MediaType.parse("image/*"), new File(s)));
		}
		RequestBody body = builder.build();
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
		try {
			Response response = client.newCall(request).execute();
			result = response.body().string();
			if (result != null) {
                JSONObject jsonObject = new JSONObject(result);
                if("444".equals(jsonObject.optString("code"))){
                    ApplicationProxy.getInstance().loginInvalid();
                }
                return jsonObject;
            }
		}catch (FileNotFoundException e) {
			JSONObject resultObj = new JSONObject();
			try {
				resultObj.put("msg", "上传截图失败。无法找到截图，请确认截图未被删除");
			} catch (JSONException e1) {
			}
			return resultObj;
		}catch (SocketException e) {
			JSONObject resultObj = new JSONObject();
			try {
				resultObj.put("msg", "上传截图超时，请检查网络正常或换个网络尝试。se");
			} catch (JSONException e1) {
			}
			return resultObj;
		}catch (SocketTimeoutException e) {
			JSONObject resultObj = new JSONObject();
			try {
				resultObj.put("msg", "上传截图超时，请检查网络正常或换个网络尝试。");
			} catch (JSONException e1) {
			}
			return resultObj;
		}catch (Exception e) {
			JSONObject resultObj = new JSONObject();
			try {
				resultObj.put("msg", "上传截图失败,如尝试多次仍旧无法上传截图，请截图此提示联系客服---"+Log.getStackTraceString(e));
			} catch (JSONException e1) {
			}
			return resultObj;
		}
		return null;
	}

	public static void executePost(String url, Map<String,String> param,OnResult callback) {
		String errorMsg = "连接失败请重试";
		callback.onPrepare();
		String result;
		try {
			OkHttpClient client = getOkHttpClient();
			addBaseParam(param);
			FormEncodingBuilderWrapper formEncodingBuilder = new FormEncodingBuilderWrapper();
			RequestBody body = formEncodingBuilder.build(param);
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.build();
			Response response = client.newCall(request).execute();
			result = response.body().string();
			JSONObject resultObj = new JSONObject(result);
			if (resultObj != null) {
				callback.onSuccess(resultObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			callback.onFail(errorMsg);
		}finally {
			callback.onFinally();
		}
	}

	private static void addBaseParam(Map<String,String> param) {
		param.put("token", UserInfo.gett());
		param.put("user_id", UserInfo.getUserId());
		param.put("imei", PhoneUtils.getImei());
		param.put("whole_imei", PhoneUtils.getWholeImei());
		param.put("number", PhoneUtils.getImsi());
		param.put("mac", PhoneUtils.getMacAddress());
		param.put("channel", ToolUtils.getChannel());
		param.put("ver",ToolUtils.getVer());
		param.put("client_ver",ToolUtils.getClientVersion());
		param.put("ssid",PhoneUtils.getSSID());
//		try {
//			param.put("fingerprint", Build.FINGERPRINT);
//		} catch (Throwable e) {
//		}
	}

	private static void addBaseParam(MultipartBuilder builder) {
		try {
			builder.addFormDataPart("token", UserInfo.gett());
		} catch (Exception e) {
		}
		try {
			builder.addFormDataPart("user_id", UserInfo.getUserId() == null ? "" : UserInfo.getUserId());
		} catch (Exception e) {
		}
		try {
			builder.addFormDataPart("imei", PhoneUtils.getImei());
		} catch (Exception e) {
		}
		try {
			builder.addFormDataPart("whole_imei", PhoneUtils.getWholeImei());
		} catch (Exception e) {

		}
		try {
			builder.addFormDataPart("number", PhoneUtils.getImsi());
		} catch (Exception e) {
		}
		try {
			builder.addFormDataPart("mac", PhoneUtils.getMacAddress());
		} catch (Exception e) {
		}
		try {
			builder.addFormDataPart("channel", ToolUtils.getChannel());
		} catch (Exception e) {
		}
		try {
			builder.addFormDataPart("ver",ToolUtils.getVer());
		} catch (Exception e) {
		}
		try {
			builder.addFormDataPart("client_ver",ToolUtils.getClientVersion());
		} catch (Exception e) {
		}
		try {
			builder.addFormDataPart("ssid",PhoneUtils.getSSID());
		} catch (Exception e) {
		}
	}

}