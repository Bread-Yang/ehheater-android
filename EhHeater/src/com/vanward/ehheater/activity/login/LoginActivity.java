package com.vanward.ehheater.activity.login;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.WelcomeActivity;
import com.vanward.ehheater.activity.configure.ShitActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.user.RegisterActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.XPG_RESULT;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

public class LoginActivity extends EhHeaterBaseActivity {
	Button btn_new_device, btn_login;
	EditText et_user, et_pwd;
	TextView mTvReg;

	@Override
	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
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

		IntentFilter filter = new IntentFilter(
				Consts.INTENT_FILTER_KILL_LOGIN_ACTIVITY);
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
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
			DialogUtil.instance().showLoadingDialog(this, "");
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
		Log.d("emmm", "login paused");
		XPGConnectClient.RemoveActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("emmm", "login resumed");
		XPGConnectClient.AddActivity(this);
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		Global.connectId = connId;

		if (event == XPG_RESULT.ERROR_NONE.swigValue()) { // 建立连接成功

			XPGConnectClient.xpgcLogin(Global.connectId, et_user.getText()
					.toString(), et_pwd.getText().toString());

		} else if (event == XPG_RESULT.ERROR_CONNECTION_CLOSED.swigValue()) { // 连接断开

		} else if (event == XPG_RESULT.ERROR_CONNECTION_ERROR.swigValue()) { // 连接错误

		} else {
			// Unknown connection event?
		}

	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);

		loginCloudResponseTriggered = true;
		if (mLoginTimeoutTimer != null) {
			mLoginTimeoutTimer.cancel();
		}

		if (result == 0 || result == 1) {
			// 0和1都是登录成功
			AccountService.setPendingUser(getBaseContext(), et_user.getText()
					.toString(), et_pwd.getText().toString());
			generated.SendBindingGetReq(Global.connectId);
			onDeviceFoundTriggered = false;
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					if (!onDeviceFoundTriggered) {
						DialogUtil.dismissDialog();
						startActivity(new Intent(getBaseContext(),
								ShitActivity.class));
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

		onDeviceFoundTriggered = true;
		HeaterInfo hi = new HeaterInfo(endpoint);

		if (count++ == 0) {
			AccountService.setUser(getBaseContext(), et_user.getText()
					.toString(), et_pwd.getText().toString());
			new SharedPreferUtils(getBaseContext()).put(ShareKey.CurDeviceMac,
					hi.getMac());
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					// 接收设备列表结束, 这时候只要重新进入welcomeActivity, 就可以正常连接设备了
					Intent intent = new Intent();
					intent.setClass(getBaseContext(), WelcomeActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_FLAG_REENTER, true);
					startActivity(intent);
					finish();
				}
			}, 2000);
		}

		Log.d("emmm", "onDeviceFound:HeaterInfo Downloaded: " + hi);
		new HeaterInfoService(getBaseContext()).saveDownloadedHeater(hi);
	};

	int count;
	boolean onDeviceFoundTriggered;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/** only for test */
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

			// HeaterInfo hinfo = new HeaterInfo();
			// hinfo.setMac("C8934641B3B4");
			// hinfo.setDid("1");
			// hinfo.setPasscode("FKAIDJKART");
			// return hinfo;
			//
			// HeaterInfo hinfo = new HeaterInfo();
			// hinfo.setMac("C8934642E4C7");
			// hinfo.setDid("o4kvBWCq5QwcWuZZbm4w4Z");
			// hinfo.setPasscode("JPDRRIXEKX");
			// hinfo.setBinded(1);
			// return hinfo;

			HeaterInfo hinfo1 = new HeaterInfo();
			hinfo1.setMac("C8934642E4C7");
			hinfo1.setPasscode("JPDRRIXEKX");

			HeaterInfo hinfo2 = new HeaterInfo();
			hinfo2.setMac("C8934642F763");
			hinfo2.setPasscode("UMBXIWTCEM");

			HeaterInfo hinfo3 = new HeaterInfo();
			hinfo3.setMac("C893464073FA");
			hinfo3.setPasscode("GIKQLYIVFQ");

			HeaterInfo hinfo4 = new HeaterInfo();
			hinfo4.setMac("C8934641968D");
			hinfo4.setPasscode("JYUSQKCLJE");

			HeaterInfoService hser = new HeaterInfoService(getBaseContext());
			// hser.addNewHeater(hinfo1);
			// hser.addNewHeater(hinfo2);
			// hser.addNewHeater(hinfo3);
			hser.addNewHeater(hinfo4);
			Context context = getBaseContext();
			AccountService.setUser(context, "111111", "111111");

		}
		return super.onKeyDown(keyCode, event);
	}

}
