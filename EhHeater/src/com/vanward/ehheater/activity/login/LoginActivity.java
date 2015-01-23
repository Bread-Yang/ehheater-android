package com.vanward.ehheater.activity.login;

import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.configure.EasyLinkConfigureActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.info.SelectDeviceActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceAppointmentTimeActivity;
import com.vanward.ehheater.activity.user.RegisterActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.XPG_RESULT;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

public class LoginActivity extends EhHeaterBaseActivity {

	private static final String TAG = "LoginActivity";

	Button btn_new_device, btn_login;
	EditText et_user, et_pwd;
	TextView mTvReg;

	SharedPreferUtils spu;

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
		setCenterView(R.layout.login_layout);

		btn_new_device = (Button) findViewById(R.id.new_device_btn);
		btn_login = (Button) findViewById(R.id.login_btn);
		et_user = (EditText) findViewById(R.id.login_user_et);
		et_pwd = (EditText) findViewById(R.id.login_pwd_et);

		mTvReg = (TextView) findViewById(R.id.ll_tv_register);

		spu = new SharedPreferUtils(getBaseContext());
		preSelectedDeviceMac = spu.get(ShareKey.CurDeviceMac, "");

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
	}

	@Override
	public void initListener() {
		super.initListener();
		btn_new_device.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		mTvReg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.new_device_btn:
			break;
		case R.id.login_btn:

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
					if (!loginCloudResponseTriggered) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DialogUtil.dismissDialog();
								Toast.makeText(getBaseContext(), "登录超时", 3000)
										.show();
							}
						});
					}
				}
			}, 9000);
			XPGConnShortCuts.connect2big();
			break;
		case R.id.ll_tv_register:
			startActivity(new Intent(this, RegisterActivity.class));
			break;
		}
	}

	Timer mLoginTimeoutTimer; // 点击登录时触发此timer, 在登录回调中取消此timer

	@Override
	protected void onPause() {
		super.onPause();
		Log.e("emmm", "login paused");
		XPGConnectClient.RemoveActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("emmm", "login resumed");

		et_user.setText(AccountService.getUserId(this));
		et_pwd.setText("");

		XPGConnectClient.AddActivity(this);
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		Log.e(TAG, "onConnectEvent@LoginActivity@: " + connId + "-" + event);
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

		loginCloudResponseTriggered = true;
		if (mLoginTimeoutTimer != null) {
			mLoginTimeoutTimer.cancel();
		}

		if (result == 0 || result == 1) {
			// 获取昵称
			HttpFriend httpFriend = HttpFriend.create(this);

			String requestURL = "userinfo/getUsageInformation?uid="
					+ et_user.getText().toString().trim();

			httpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						public void onSuccess(String jsonString) {
							Log.e(TAG, "请求昵称返回的数据是 : " + jsonString);
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
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					if (!onDeviceFoundTriggered) {
						DialogUtil.dismissDialog();
						startActivity(new Intent(getBaseContext(),
								SelectDeviceActivity.class));
					}
				}
			}, 6000);
		} else {
			// 登录失败
			DialogUtil.dismissDialog();
			Toast.makeText(getBaseContext(), "用户名或密码错误", 3000).show();
		}
	}

	boolean loginCloudResponseTriggered;

	@Override
	public void onDeviceFound(XpgEndpoint endpoint) {
		super.onDeviceFound(endpoint);

		if (endpoint == null) {
			Log.e(TAG, "endpoint为null,返回");
			return;
		}

		if (endpoint.getIsDisabled() == 1) {
			Log.e(TAG, "endpoint.getIsDisabled() == 1,返回");
			return;
		}

		HeaterInfoService hser = new HeaterInfoService(getBaseContext());
		HeaterInfo hi = new HeaterInfo(endpoint);
		Log.e(TAG, "onDeviceFound:HeaterInfo Downloaded: " + hi);

		if (!(hser.isValidDevice(hi))) {
			// 非有效设备, 不予保存
			Log.e(TAG, "非有效设备, 不予保存");
			return;
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
}
