package com.vanward.ehheater.util;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.util.BaoDialogShowUtil.BaoTimeoutDailog;

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

	private BaoTimeoutDailog loadingDialog;

	public boolean showTips = true;

	private Dialog serverFailureDialog;

	/**
	 * 
	 * @return
	 */
	static public HttpFriend create(Context context) {
		HttpFriend hf = new HttpFriend();
		hf.mContext = context;
		hf.pcs = new PreferencesCookieStore(context);
		hf.serverFailureDialog = BaoDialogShowUtil.getInstance(hf.mContext)
				.createDialogWithOneButton(R.string.server_failure,
						BaoDialogShowUtil.DEFAULT_RESID, null);
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
			if (showTips) {
				Toast.makeText(mContext, "网络访问不了,确保打开GPRS或者WiFi网络",
						Toast.LENGTH_LONG).show();
			}
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
		// L.e(this, "地址是 : " + this.url);
		// L.e(this, "executePostJson发送过去的json数据是 : " + json);
		// AjaxParams params = new AjaxParams();
		// params.put("data", json);
		showRequestDialog(callBack);
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onSuccess(String jsonString) {
				dismissRequestDialog();
				callBack.onSuccess(jsonString);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				dismissRequestDialog();
				callBack.onFailure(t, errorNo, strMsg);
				if (showTips) {
					if (!((Activity)mContext).isFinishing()) {
						serverFailureDialog.show();
					}
//					Toast.makeText(mContext, "服务器错误", Toast.LENGTH_SHORT)
//							.show();
				}
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

		showRequestDialog(callBack);
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onSuccess(String jsonString) {
				dismissRequestDialog();
				callBack.onSuccess(jsonString);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				dismissRequestDialog();
				callBack.onFailure(t, errorNo, strMsg);
				if (showTips) {
					if (!((Activity)mContext).isFinishing()) {
						serverFailureDialog.show();
					}
//					Toast.makeText(mContext, "服务器错误", Toast.LENGTH_SHORT)
//							.show();
				}
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

	public HttpFriend executeGet(AjaxParams params, final AjaxCallBack callBack) {
		if (!NetworkStatusUtil.isConnected(mContext)) {
			if (showTips) {
				Toast.makeText(mContext, "网络访问不了,确保打开GPRS或者WiFi网络",
						Toast.LENGTH_LONG).show();
			}

		} else {
			FinalHttp fh = new FinalHttp();
			showRequestDialog(callBack);
			AjaxCallBack ajaxCallBack = new AjaxCallBack<String>() {

				@Override
				public void onSuccess(String jsonString) {
					dismissRequestDialog();
					callBack.onSuccess(jsonString);
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					dismissRequestDialog();
					callBack.onFailure(t, errorNo, strMsg);
					if (showTips) {
						if (!((Activity)mContext).isFinishing()) {
							serverFailureDialog.show();
						}
//						Toast.makeText(mContext, "服务器错误", Toast.LENGTH_SHORT)
//								.show();
					}

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

			};
			if (params != null) {
				fh.get(url, params, ajaxCallBack);
			} else {
				fh.get(this.url, ajaxCallBack);
			}
		}
		return this;
	}

	private void showRequestDialog(AjaxCallBack callBack) {
		if (mContext instanceof Activity) {
			if (!((Activity) mContext).isFinishing()) {
				if (loadingDialog == null) {
					loadingDialog = BaoDialogShowUtil.getInstance(mContext)
							.createLoadingDialog();
				}
				loadingDialog.show(callBack);
			}
		}
	}

	private void dismissRequestDialog() {
		if (mContext instanceof Activity) {
			if (!((Activity) mContext).isFinishing()) {
				if (loadingDialog != null) {
					loadingDialog.dismiss();
				}
			}
		}
	}
}
