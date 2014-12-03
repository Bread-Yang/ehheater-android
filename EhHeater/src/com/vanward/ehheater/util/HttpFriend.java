package com.vanward.ehheater.util;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * 在原有的HttpFriend增加了网络连接状态监测功能和清除参数功能
 * 
 * @author yrb
 * 
 */
public class HttpFriend {

	private static final String TAG = "HttpFriend";

	private Object bean;

	private AjaxParams params = null;

	private String url = null;

	private PreferencesCookieStore pcs;

	private Context mContext;

	/**
	 * 
	 * @return
	 */
	static public HttpFriend create(Context context) {
		HttpFriend hf = new HttpFriend();
		hf.mContext = context;
		hf.pcs = new PreferencesCookieStore(context);
		return hf;
	}

	public HttpFriend postBean(Object bean) {
		this.bean = bean;
		return this;
	}

	public HttpFriend addParam(String key, String value) {
		if (params == null) {
			params = new AjaxParams();
		}
		params.put(key, value);
		return this;
	}

	public HttpFriend clearParams() {
		params = null;
		return this;
	}

	public HttpFriend toUrl(String url) {
		this.url = url;
		return this;
	}

	public HttpFriend executePost(AjaxParams params, AjaxCallBack callBack) {
		if (!NetworkStatusUtil.isConnected(mContext)) {
			Toast.makeText(mContext, "网络访问不了,确保打开GPRS或者WiFi网络",
					Toast.LENGTH_LONG).show();
		} else {
			if (bean != null) {
				executePostJson(params, callBack);
			} else {
				executePostParams(params, callBack);
			}
		}
		return this;
	}

	private HttpFriend executePostJson(AjaxParams params,
			final AjaxCallBack<String> callBack) {
		for (Cookie c : pcs.getCookies()) {
			System.out.println("打印cookies是 : getName : " + c.getName()
					+ "  getValue : " + c.getValue() + "  getExpiryDate : "
					+ c.getExpiryDate());
		}
		FinalHttp fh = new FinalHttp();
		fh.addHeader("Accept-Charset", "UTF-8");// 配置http请求头
		fh.configCharset("UTF-8");
		fh.configCookieStore(pcs);
		// Gson gson = new Gson();
		// String json = gson.toJson(this.bean);
		// Log.e(TAG, "地址是 : " + this.url);
		// Log.e(TAG, "executePostJson发送过去的json数据是 : " + json);
		// AjaxParams params = new AjaxParams();
		// params.put("data", json);
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onSuccess(String jsonString) {
				callBack.onSuccess(jsonString);
				Log.e(TAG, "添加成功返回的json : " + jsonString);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				callBack.onFailure(t, errorNo, strMsg);
			}

			@Override
			public void onLoading(long count, long current) {
				callBack.onLoading(count, current);
			}

			@Override
			public void onStart() {
				callBack.onStart();
			}

			@Override
			public AjaxCallBack<String> progress(boolean progress, int rate) {
				callBack.progress(progress, rate);
				return super.progress(progress, rate);

			}

			@Override
			public int getRate() {
				return callBack.getRate();
			}

			@Override
			public boolean isProgress() {
				return callBack.isProgress();
			}

		});

		return this;
	}

	private HttpFriend executePostParams(AjaxParams params,
			final AjaxCallBack callBack) {
		FinalHttp fh = new FinalHttp();
		fh.configCookieStore(pcs);

		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onSuccess(String jsonString) {
				callBack.onSuccess(jsonString);
				Log.e(TAG, "添加成功返回的json : " + jsonString);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				callBack.onFailure(t, errorNo, strMsg);
				Toast.makeText(mContext, "服务器错误", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onLoading(long count, long current) {
				callBack.onLoading(count, current);
			}

			@Override
			public void onStart() {
				callBack.onStart();
			}

			@Override
			public AjaxCallBack<String> progress(boolean progress, int rate) {
				callBack.progress(progress, rate);
				return super.progress(progress, rate);

			}

			@Override
			public int getRate() {
				return callBack.getRate();
			}

			@Override
			public boolean isProgress() {
				return callBack.isProgress();
			}

		});
		return this;
	}

	public HttpFriend executeGet(AjaxParams params, AjaxCallBack callBack) {
		FinalHttp fh = new FinalHttp();
		if (params != null) {
			fh.get(url, params, callBack);
		} else {
			fh.get(this.url, callBack);
		}
		return this;
	}
}
