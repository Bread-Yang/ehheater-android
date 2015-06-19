package com.vanward.ehheater.activity.login;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.info.SelectDeviceActivity;
import com.vanward.ehheater.activity.user.FindPasswordActivity;
import com.vanward.ehheater.activity.user.RegisterActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.GizwitsErrorMsg;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.XPG_RESULT;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

public class LoginActivity extends EhHeaterBaseActivity {

	private static final String TAG = "LoginActivity";

	Button btn_new_device, btn_login;
	EditText et_user, et_pwd;
	TextView mTvReg, tv_find_passcode;

	SharedPreferUtils spu;

	int timeoutSecond = 20000;

	boolean setJPushAliasSuccess = false;

	boolean loginSuccess = false;

	boolean isTimeout = false;

	private Handler acquireDeviceListTimeout = new Handler() {
		public void handleMessage(android.os.Message msg) {
			DialogUtil.dismissDialog();
			Toast.makeText(getBaseContext(), "获取设备列表超时！", 3000)
					.show();
		};
	};

	@Override
	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onStop() {
		super.onStop();
		DialogUtil.dismissDialog();
	}

	@Override
	public void initUI() {
		super.initUI();
		setTopDismiss();
		setCenterView(R.layout.activity_login);

		btn_new_device = (Button) findViewById(R.id.new_device_btn);
		btn_login = (Button) findViewById(R.id.login_btn);
		et_user = (EditText) findViewById(R.id.login_user_et);
		et_pwd = (EditText) findViewById(R.id.login_pwd_et);

		mTvReg = (TextView) findViewById(R.id.tv_register);
		tv_find_passcode = (TextView) findViewById(R.id.tv_find_passcode);

		spu = new SharedPreferUtils(getBaseContext());
		preSelectedDeviceMac = spu.get(ShareKey.CurDeviceMac, "");

		findViewById(R.id.llt_root).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

		findViewById(R.id.llt_root).setOnFocusChangeListener(
				new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View arg0, boolean hasFocus) {
						if (hasFocus) {
							((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
									.hideSoftInputFromWindow(
											LoginActivity.this
													.getCurrentFocus()
													.getWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}
				});

		IntentFilter filter = new IntentFilter(
				Consts.INTENT_FILTER_KILL_LOGIN_ACTIVITY);
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				LoginActivity.this.setResult(-256);
				finish();
			}
		};
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				receiver, filter);

		if (getIntent().getBooleanExtra("queryDevicesListAgain", false)) { // 用之前登录过的账号密码重新请求设备列表

			String userName = AccountService.getUserId(getBaseContext());
			String psw = AccountService.getUserPsw(getBaseContext());

			et_user.setText(userName);
			et_pwd.setText(psw);

			new SharedPreferUtils(getBaseContext()).clear();
			new HeaterInfoService(getBaseContext()).deleteAllHeaters();

			L.e(this, "userName : " + userName);
			L.e(this, "psw : " + psw);

			DialogUtil.instance().showLoadingDialog(this, "正在登录，请稍后...");
			XPGConnectClient.xpgc4Login(Consts.VANWARD_APP_ID, userName, psw);

			loginCloudResponseTriggered = false;

			mLoginTimeoutTimer = new Timer();
			mLoginTimeoutTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (!(loginCloudResponseTriggered && setJPushAliasSuccess)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DialogUtil.dismissDialog();
								Toast.makeText(getBaseContext(), "登录超时！", 3000)
										.show();
								isTimeout = true;
							}
						});
					}
				}
			}, timeoutSecond);
		}
	}

	@Override
	public void initListener() {
		super.initListener();
		btn_new_device.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		mTvReg.setOnClickListener(this);
		tv_find_passcode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.new_device_btn:
			break;
		case R.id.login_btn:

			L.e(this, "登陆之前");

			isTimeout = false;

			if (!NetworkStatusUtil.isConnected(this)) {
				Toast.makeText(this, R.string.check_network, Toast.LENGTH_LONG)
						.show();
				return;
			}

			if (et_user.getText().length() <= 0
					|| et_pwd.getText().length() <= 0) {
				Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_LONG).show();
				return;
			}
			DialogUtil.instance().showLoadingDialog(this, "正在登录，请稍后...");

			loginCloudResponseTriggered = false;

			mLoginTimeoutTimer = new Timer();
			mLoginTimeoutTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (!(loginCloudResponseTriggered && setJPushAliasSuccess)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DialogUtil.dismissDialog();
								Toast.makeText(getBaseContext(), "登录超时！", 3000)
										.show();
								isTimeout = true;
							}
						});
					}
				}
			}, timeoutSecond);
			// XPGConnShortCuts.connect2big();
			XPGConnectClient.xpgc4Login(Consts.VANWARD_APP_ID, et_user
					.getText().toString(), et_pwd.getText().toString());
			break;
		case R.id.tv_register:
			startActivity(new Intent(this, RegisterActivity.class));
			break;
		case R.id.tv_find_passcode:
			startActivityForResult(
					new Intent(this, FindPasswordActivity.class), 0);
			break;
		}
	}

	Timer mLoginTimeoutTimer; // 点击登录时触发此timer, 在登录回调中取消此timer

	@Override
	protected void onPause() {
		super.onPause();
		XPGConnectClient.RemoveActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if ("".equals(et_user.getText().toString())) {
			et_user.setText(AccountService.getUserId(this));
		}
//		et_pwd.setText("111111");

		XPGConnectClient.AddActivity(this);
	}

	@Override
	public void onV4Login(int errorCode, String uid, String token,
			String expire_at) {
		L.e(this, "onV4Login() : errorCode : " + errorCode);

		if (isTimeout) {
			return;
		}

		if (errorCode == 0) {
			loginSuccess = true;

			Global.uid = uid;
			Global.token = token;

			L.e(this, "uid : " + uid);
			L.e(this, "token : " + token);

			loginCloudResponseTriggered = true;
			if (mLoginTimeoutTimer != null) {
				mLoginTimeoutTimer.cancel();
			}

			L.e(this, "et_user.getText().toString().trim() : "
					+ et_user.getText().toString().trim());
			// JPushInterface.setAlias(getApplicationContext(),
			// et_user.getText().toString().trim(), mAliasCallback);

			Set<String> tagSet = new LinkedHashSet<String>();
			tagSet.add(et_user.getText().toString().trim());
			JPushInterface.setTags(getApplicationContext(), tagSet,
					mAliasCallback);

			// 获取昵称
			HttpFriend httpFriend = HttpFriend.create(this);
			httpFriend.showTips = false;

			String requestURL = "userinfo/getUsageInformation?uid="
					+ et_user.getText().toString().trim();

			httpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						public void onSuccess(String jsonString) {
							L.e(this, "请求昵称返回的数据是 : " + jsonString);
							JSONObject json;
							try {
								json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									JSONObject result = json
											.getJSONObject("result");
									String nickName = result
											.getString("userName");
									new SharedPreferUtils(getBaseContext())
											.put(ShareKey.UserNickname,
													nickName);
								} else if ("402".equals(responseCode)) {
									new SharedPreferUtils(getBaseContext())
											.put(ShareKey.UserNickname, "");
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						};

					});

			// 0和1都是登录成功
			SharedPreferUtils.saveUsername(this, et_user.getText().toString());
			AccountService.setUser(getBaseContext(), et_user.getText()
					.toString(), et_pwd.getText().toString());

			// generated.SendBindingGetV2Req(tempConnId);
			XPGConnectClient.xpgc4GetMyBindings(Consts.VANWARD_APP_ID, token,
					20, 0);

			onDeviceFoundTriggered = false;
			
			acquireDeviceListTimeout.sendEmptyMessageDelayed(0, 20000);

		} else {
			// 登录失败
			mLoginTimeoutTimer.cancel();
			DialogUtil.dismissDialog();
			Toast.makeText(getBaseContext(),
					GizwitsErrorMsg.getEqual(errorCode).getCHNDescript(), 3000)
					.show();
		}
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		L.e(this, "onConnectEvent@LoginActivity@: " + connId + "-" + event);
		tempConnId = connId;

		if (event == XPG_RESULT.ERROR_NONE.swigValue()) { // 建立连接成功

			XPGConnectClient.xpgcLogin(tempConnId,
					et_user.getText().toString(), et_pwd.getText().toString());

		} else if (event == XPG_RESULT.ERROR_CONNECTION_CLOSED.swigValue()) { // 连接断开

		} else if (event == XPG_RESULT.ERROR_CONNECTION_ERROR.swigValue()) { // 连接错误

		} else {
			// Unknown connection event?
		}

	}

	private int tempConnId = -1;

	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
		L.e(this, "onLoginCloudResp()");

		loginCloudResponseTriggered = true;
		if (mLoginTimeoutTimer != null) {
			mLoginTimeoutTimer.cancel();
		}

		if (result == 0 || result == 1) {
			// 获取昵称
			HttpFriend httpFriend = HttpFriend.create(this);
			httpFriend.showTips = false;

			String requestURL = "userinfo/getUsageInformation?uid="
					+ et_user.getText().toString().trim();

			httpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						public void onSuccess(String jsonString) {
							L.e(this, "请求昵称返回的数据是 : " + jsonString);
							JSONObject json;
							try {
								json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									JSONObject result = json
											.getJSONObject("result");
									String nickName = result
											.getString("userName");
									new SharedPreferUtils(getBaseContext())
											.put(ShareKey.UserNickname,
													nickName);
								} else if ("402".equals(responseCode)) {
									new SharedPreferUtils(getBaseContext())
											.put(ShareKey.UserNickname, "");
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						};

					});

			// 0和1都是登录成功
			SharedPreferUtils.saveUsername(this, et_user.getText().toString());
			AccountService.setUser(getBaseContext(), et_user.getText()
					.toString(), et_pwd.getText().toString());
			// AccountService.setPendingUser(getBaseContext(), et_user.getText()
			// .toString(), et_pwd.getText().toString());
			generated.SendBindingGetV2Req(tempConnId);

			onDeviceFoundTriggered = false;

			acquireDeviceListTimeout.sendEmptyMessageDelayed(0, 20000);
		} else {
			// 登录失败
			DialogUtil.dismissDialog();
			Toast.makeText(getBaseContext(), "用户名或密码错误", 3000).show();
		}
	}

	boolean notBindedDevice = true;

	@Override
	public void onV4GetMyBindings(int errorCode, XpgEndpoint endpoint) {
		L.e(this, "onV4GetMyBindings()");

		L.e(this, "errorCode : " + errorCode);

		acquireDeviceListTimeout.removeMessages(0);
		
		if (errorCode != 0) {
			return;
		}

		if (endpoint == null) {
			L.e(this, "endpoint为null,返回");
			return;
		}

		L.e(this, "(endpoint.getIsDisabled() : " + (endpoint.getIsDisabled()));
		if (endpoint.getIsDisabled() == 1) {
			L.e(this, "endpoint.getIsDisabled() == 1,返回");
			return;
		}

		if (null == endpoint.getSzMac() || "".equals(endpoint.getSzMac())) {
			L.e(this, "该用户还没有绑定设备，跳进设备配置界面");
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (notBindedDevice) {
						DialogUtil.dismissDialog();
						startActivity(new Intent(getBaseContext(),
								SelectDeviceActivity.class));
					}
				}
			}, 3000);
			return;
		}

		HeaterInfoService hser = new HeaterInfoService(getBaseContext());
		HeaterInfo hi = new HeaterInfo(endpoint);
		L.e(this, "onDeviceFound:HeaterInfo Downloaded: " + hi);

		if (!(hser.isValidDevice(hi))) {
			// 非有效设备, 不予保存
			L.e(this, "非有效设备, 不予保存");
			return;
		}
		
		notBindedDevice = false;

		SharedPreferUtils spu = new SharedPreferUtils(this);
		if (hser.getHeaterType(hi).equals(HeaterType.ELECTRIC_HEATER)) {
			if ("".equals(spu.get(ShareKey.PollingElectricHeaterDid, ""))) {
				spu.put(ShareKey.PollingElectricHeaterDid, hi.getDid());
				spu.put(ShareKey.PollingElectricHeaterMac, hi.getMac());
			}
		} else if (hser.getHeaterType(hi).equals(HeaterType.GAS_HEATER)) {
			if ("".equals(spu.get(ShareKey.PollingGasHeaterDid, ""))) {
				spu.put(ShareKey.PollingGasHeaterDid, hi.getDid());
				spu.put(ShareKey.PollingGasHeaterMac, hi.getMac());
			}
		} else if (hser.getHeaterType(hi).equals(HeaterType.FURNACE)) {
			if ("".equals(spu.get(ShareKey.PollingFurnaceDid, ""))) {
				spu.put(ShareKey.PollingFurnaceDid, hi.getDid());
				spu.put(ShareKey.PollingFurnaceMac, hi.getMac());
			}
		}

		onDeviceFoundTriggered = true;

		if (count++ == 0) {
			AccountService.setUser(getBaseContext(), et_user.getText()
					.toString(), et_pwd.getText().toString());

			// if (TextUtils.isEmpty(preSelectedDeviceMac)) {
			spu.put(ShareKey.CurDeviceMac, hi.getMac());
			// }

			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					// 接收设备列表结束, 这时候只要重新进入welcomeActivity, 就可以正常连接设备了

					// Intent intent = new Intent();
					// intent.setClass(getBaseContext(), WelcomeActivity.class);
					// intent.putExtra(Consts.INTENT_EXTRA_FLAG_REENTER, true);
					// startActivity(intent);
					// finish();

					setResult(RESULT_OK);
					DialogUtil.instance().dismissDialog();
					finish();
				}
			}, 2000);

		}

		L.e(this, "hser.saveDownloadedHeater(hi)调用了");
		hser.saveDownloadedHeater(hi);

		HttpFriend httpFriend = HttpFriend.create(this);
		httpFriend.showTips = false;

		String requestURL = "userinfo/saveAlias?did=" + endpoint.getSzDid()
				+ "&uid=" + et_user.getText().toString().trim()
				+ "&isLogout=false";

		L.e(LoginActivity.this, "上传的requestURL : " + requestURL);

		httpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(null,
				new AjaxCallBack<String>() {
					public void onSuccess(String jsonString) {
						JSONObject json;
						try {
							json = new JSONObject(jsonString);
							String responseCode = json
									.getString("responseCode");
							if ("200".equals(responseCode)) {
								L.e(LoginActivity.this, "上传设备到志聪的JPush服务器成功");
							} else if ("402".equals(responseCode)) {
								L.e(LoginActivity.this, "上传设备到志聪的JPush服务器失败");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					};

				});

		if (preSelectedDeviceMac.equals(hi.getMac())) {
			spu.put(ShareKey.CurDeviceMac, hi.getMac());
		}
		// endpoint.delete();
	}

	boolean loginCloudResponseTriggered;

	@Override
	public void onDeviceFound(XpgEndpoint endpoint) {
		super.onDeviceFound(endpoint);

		L.e(this, "onDeviceFound()");

		if (endpoint == null) {
			L.e(this, "endpoint为null,返回");
			return;
		}

		if (endpoint.getIsDisabled() == 1) {
			L.e(this, "endpoint.getIsDisabled() == 1,返回");
			return;
		}

		HeaterInfoService hser = new HeaterInfoService(getBaseContext());
		HeaterInfo hi = new HeaterInfo(endpoint);
		L.e(this, "onDeviceFound:HeaterInfo Downloaded: " + hi);

		if (!(hser.isValidDevice(hi))) {
			// 非有效设备, 不予保存
			L.e(this, "非有效设备, 不予保存");
			return;
		}

		SharedPreferUtils spu = new SharedPreferUtils(this);
		if (hser.getHeaterType(hi).equals(HeaterType.ELECTRIC_HEATER)) {
			if ("".equals(spu.get(ShareKey.PollingElectricHeaterDid, ""))) {
				spu.put(ShareKey.PollingElectricHeaterDid, hi.getDid());
				spu.put(ShareKey.PollingElectricHeaterMac, hi.getMac());
			}
		} else if (hser.getHeaterType(hi).equals(HeaterType.GAS_HEATER)) {
			if ("".equals(spu.get(ShareKey.PollingGasHeaterDid, ""))) {
				spu.put(ShareKey.PollingGasHeaterDid, hi.getDid());
				spu.put(ShareKey.PollingGasHeaterMac, hi.getMac());
			}
		} else if (hser.getHeaterType(hi).equals(HeaterType.FURNACE)) {
			if ("".equals(spu.get(ShareKey.PollingFurnaceDid, ""))) {
				spu.put(ShareKey.PollingFurnaceDid, hi.getDid());
				spu.put(ShareKey.PollingFurnaceMac, hi.getMac());
			}
		}

		onDeviceFoundTriggered = true;

		if (count++ == 0) {
			AccountService.setUser(getBaseContext(), et_user.getText()
					.toString(), et_pwd.getText().toString());

			// if (TextUtils.isEmpty(preSelectedDeviceMac)) {
			spu.put(ShareKey.CurDeviceMac, hi.getMac());
			// }

			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					// 接收设备列表结束, 这时候只要重新进入welcomeActivity, 就可以正常连接设备了

					// Intent intent = new Intent();
					// intent.setClass(getBaseContext(), WelcomeActivity.class);
					// intent.putExtra(Consts.INTENT_EXTRA_FLAG_REENTER, true);
					// startActivity(intent);
					// finish();

					setResult(RESULT_OK);
					finish();
				}
			}, 2000);

		}

		hser.saveDownloadedHeater(hi);
		if (preSelectedDeviceMac.equals(hi.getMac())) {
			spu.put(ShareKey.CurDeviceMac, hi.getMac());
		}
		// endpoint.delete();
	};

	int count;
	boolean onDeviceFoundTriggered;
	String preSelectedDeviceMac;

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLoginTimeoutTimer != null) {
			mLoginTimeoutTimer.cancel();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				String account = data.getStringExtra("account");
				et_user.setText(account);
				et_pwd.setText("");
				break;
			}
		}
	}

	private static final int MSG_SET_ALIAS = 1001;

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Log.i(TAG, logs);

				setJPushAliasSuccess = true;
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.i(TAG, logs);
				// mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS,
				// alias), 1000 * 60);
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Log.e(TAG, logs);
			}
		}

		private final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_SET_ALIAS:
					Log.d(TAG, "Set alias in handler.");
					JPushInterface.setAliasAndTags(getApplicationContext(),
							(String) msg.obj, null, mAliasCallback);
					break;
				}
			}
		};

	};
}
