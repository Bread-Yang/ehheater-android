package com.vanward.ehheater.activity.configure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;

import com.vanward.ehheater.BuildConfig;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;
import com.xtremeprog.xpgconnect.generated.LanLoginResp_t;
import com.xtremeprog.xpgconnect.generated.PasscodeResp_t;
import com.xtremeprog.xpgconnect.generated.XPG_WAN_LAN;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

public class ConnectActivity extends GeneratedActivity {

	private final static int smallCycleConnectTimeout = 10000;
	private final static int bigCycleConnnectTimeout = 30000;

	/** 建立的连接类型, LAN / MQTT(大) */
	private int connType = Integer.MAX_VALUE;

	private int tempConnId = -1;

	private String mMac = "";

	private String mPasscode = "";

	private String passcodeRetrieved = "";

	private String didRetrieved = "";

	private int onSameDeviceFoundCounter;

	private boolean isActived = false;

	private boolean isAlreadyTryConnectBySmallCycle = false; // XPGConnShortCuts.connect2small()已经被调用,则不再尝试大循环

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.e(this, "onCreate()");

		DialogUtil.instance().dismissDialog();

		// if (BuildConfig.DEBUG) {
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectAll().penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
		// .penaltyLog().penaltyDeath().build());
		// }

		setContentView(R.layout.activity_connect_as_dialog);

		connectToDevice();
	}

	@Override
	protected void onResume() {
		L.e(this, "onResume()");
		super.onResume();

		DialogUtil.instance().dismissDialog();
		isActived = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		isActived = false;
	}

	@Override
	public void onBackPressed() {

	};

	private void connectToDevice() {
		L.e(this, "connectToDevice()");

		DialogUtil.instance().dismissDialog();

		if (!NetworkStatusUtil.isConnected(getBaseContext())) {
			setOfflineResult();
			return;
		}

		initTemporaryFields();
		initTargetDeviceInfo();

		if (NetworkStatusUtil.isConnectedByWifi(getBaseContext())) {
			if (getIntent().getBooleanExtra(
					EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK,
					false)) { // easylink后直接通过endpoint连接
				connectDirectlyAfterEasyLink();
			} else { // 先试小循环, 不行则大循环
				isAlreadyTryConnectBySmallCycle = false;
				tryConnectBySmallCycle(smallCycleConnectTimeout);
			}
		} else if (NetworkStatusUtil.isConnectedByMobileData(getBaseContext())) {
			L.e(this, "大循环");
			// 只能大循环
			tryConnectByBigCycle();
		}
	}

	private void initTemporaryFields() {
		connType = Integer.MAX_VALUE;
		tempConnId = -1;
		mMac = "";
		mPasscode = "";
		passcodeRetrieved = "";
		didRetrieved = "";
		onSameDeviceFoundCounter = 0;
	}

	private void initTargetDeviceInfo() {
		if (!getIntent().getBooleanExtra(
				EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK, false)) {
			mMac = getIntent().getStringExtra(Consts.INTENT_EXTRA_MAC);

			L.e(this, "mMac : " + mMac);

			if (TextUtils.isEmpty(mMac)) {
				setOfflineResult();
			}
		}

		mPasscode = passcodeRetrieved = getIntent().getStringExtra(
				Consts.INTENT_EXTRA_PASSCODE);

		L.e(this, "要重连的设备的passcode : " + mPasscode);
	}

	private Handler timeoutHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			setOfflineResult();
			return false;
		}
	});

	/**
	 * 小循环连接入口
	 * 
	 */
	private void tryConnectBySmallCycle(int timeout) {
		L.e(this, "tryConnectBySmallCycle()");

		connType = XPG_WAN_LAN.LAN.swigValue();

		L.e(this, "XPGConnectClient.xpgcStartDiscovery()");
		XPGConnectClient.xpgcStartDiscovery();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {

				L.e(this, "XPGConnectClient.xpgcStopDiscovery()");
				XPGConnectClient.xpgcStopDiscovery();

				if (!isAlreadyTryConnectBySmallCycle) {
					tryConnectByBigCycle();
				}
			}
		}, timeout);
	}

	/**
	 * 大循环连接入口
	 */
	private void tryConnectByBigCycle() {
		L.e(this, "tryConnectByBigCycle()");

		connType = XPG_WAN_LAN.MQTT.swigValue();

		if ("".equals(Global.token) || "".equals(Global.uid)) {
			XPGConnectClient.xpgc4Login(Consts.VANWARD_APP_ID,
					AccountService.getUserId(getBaseContext()),
					AccountService.getUserPsw(getBaseContext()));
		} else {
			XPGConnectClient.xpgc4GetMyBindings(Consts.VANWARD_APP_ID,
					Global.token, 20, 0);
		}

		timeoutHandler.sendEmptyMessageDelayed(0, bigCycleConnnectTimeout);
	}

	@Override
	public void onV4Login(int errorCode, String uid, String token,
			String expire_at) {
		L.e(this, "onV4Login() errorCode : " + errorCode);

		if (errorCode == 0) {
			Global.uid = uid;
			Global.token = token;

			XPGConnectClient.xpgc4GetMyBindings(Consts.VANWARD_APP_ID,
					Global.token, 20, 0);
		}
	}

	@Override
	public void onV4GetMyBindings(int errorCode, final XpgEndpoint endpoint) {
		super.onV4GetMyBindings(errorCode, endpoint);

		synchronized (this) {

			L.e(this, "onV4GetMyBindings: mac : " + endpoint.getSzMac()
					+ "- did : " + endpoint.getSzDid() + "- isOnline : "
					+ endpoint.getIsOnline());

			if ("".equals(endpoint.getSzMac())
					|| "".equals(endpoint.getSzDid())) {
				return;
			}

			if (!mMac.equals(endpoint.getSzMac().toLowerCase())) {
				L.e(this, "服务器返回的设备Mac与要连接的设备不匹配, 要连接的mac :" + mMac
						+ ", 服务器返回的mac : " + endpoint.getSzMac());
				return;
			}

			if (onSameDeviceFoundCounter++ == 0) {

				L.e(this,
						"执行了8秒后执行connectAfterGetBindingDevicesReceivedFromMQTT()方法");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// 接收绑定的设备列表完毕
						connectAfterGetBindingDevicesReceivedFromMQTT(endpoint);
					}
				}, 8000);
			}
		}
	}

	@Override
	public void onDeviceFound(final XpgEndpoint endpoint) {
		super.onDeviceFound(endpoint);
		L.e(this,
				"onDeviceFound@ConnectActivity(SMALL): " + endpoint.getSzMac()
						+ "-" + endpoint.getSzDid() + "-"
						+ endpoint.getIsOnline());

		if (null == endpoint) {
			return;
		}

		boolean directConnectAfterEasyLink = getIntent().getBooleanExtra(
				EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK, false);

		if (connType == XPG_WAN_LAN.LAN.swigValue()
				|| directConnectAfterEasyLink) {

			if (endpoint.getSzMac() == null || endpoint.getSzDid() == null) {
				return;
			}

			String macFound = endpoint.getSzMac().toLowerCase();
			String didFound = endpoint.getSzDid();

			L.e(this, "endpoint.getSzMac().toLowerCase() : "
					+ endpoint.getSzMac().toLowerCase());
			L.e(this, "didRetrieved : " + didRetrieved);
			L.e(this, "endpoint.getAddr() : " + endpoint.getAddr());

			if (!TextUtils.isEmpty(macFound)
					&& macFound.equals(mMac.toLowerCase())) {
				L.e(this, "XPGConnectClient.xpgcStopDiscovery()");
				XPGConnectClient.xpgcStopDiscovery();

				didRetrieved = didFound;
				timeoutHandler.sendEmptyMessageDelayed(0, 5000);

				isAlreadyTryConnectBySmallCycle = true;

				L.e(this, "XPGConnShortCuts.connect2small()");
				XPGConnShortCuts.connect2small(endpoint.getAddr());
				// XPGConnectClient.xpgcLogin2Lan(endpoint.getAddr(), null);
			}

		} else if (connType == XPG_WAN_LAN.MQTT.swigValue()) {

			L.e(this,
					"onDeviceFound@ConnectActivity(BIG): mac : "
							+ endpoint.getSzMac() + "- did : "
							+ endpoint.getSzDid() + "- isOnline : "
							+ endpoint.getIsOnline());

			if (onSameDeviceFoundCounter++ == 0) {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// 接收绑定的设备列表完毕
						connectAfterGetBindingDevicesReceivedFromMQTT(endpoint);
					}
				}, 8000);

			}
		}
	}

	public void connectDirectlyAfterEasyLink() {
		L.e(this, "connectDirectlyAfterEasyLink()");

		didRetrieved = new SharedPreferUtils(this).get(ShareKey.CurDeviceDid,
				"");
		String ip = new SharedPreferUtils(this).get(ShareKey.CurDeviceAddress,
				"");

		L.e(this, "didRetrieved : " + didRetrieved);
		L.e(this, "ip : " + ip);

		connType = XPG_WAN_LAN.LAN.swigValue();

		timeoutHandler.sendEmptyMessageDelayed(0, 10000);

		L.e(this, "XPGConnShortCuts.connect2small(ip)");
		L.e(this, "要连接设备的IP : " + ip);
		XPGConnShortCuts.connect2small(ip);
		// XPGConnectClient.xpgcLogin2Lan(ip, null);
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
		L.e(this, "onLoginCloudResp() : result : " + result + " mac : " + mac);
	}

	@Override
	public void onWanLoginResp(int result, int connId) { // 调用XPGConnectClient.xpgcLogin2Wan()连接设备后回调
		super.onWanLoginResp(result, connId);
		L.e(this, "onWanLoginResp@ConnectActivity : result : " + result
				+ " connId : " + connId);
		tempConnId = connId;
		switch (result) {
		case 0: // 可以控制

			Intent data = new Intent();
			data.putExtra(Consts.INTENT_EXTRA_CONNID, tempConnId);
			data.putExtra(Consts.INTENT_EXTRA_ISONLINE, true);

			data.putExtra(Consts.INTENT_EXTRA_PASSCODE, passcodeRetrieved);
			data.putExtra(Consts.INTENT_EXTRA_DID, didRetrieved);

			String conntext = getIntent().getStringExtra(
					Consts.INTENT_EXTRA_CONNECT_TEXT);
			if (conntext == null) {
				conntext = "";
			}
			data.putExtra(Consts.INTENT_EXTRA_CONNECT_TEXT, conntext);

			setResult(RESULT_OK, data);
			timeoutHandler.removeMessages(0);
			finish();
			break;
		case 1:
			L.e(this, "generated.SendBindingGetV2Req");
			generated.SendBindingGetV2Req(tempConnId);
			break;
		case 5:
			// 账号密码错误
			break;
		}
	}

	@Override
	public void onConnectEvent(int connId, int event) { // connect2small()之后回调
		super.onConnectEvent(connId, event);
		L.e(this, "onConnectEvent() connId : " + connId + "- event : " + event);

		L.e(this, "是否小循环连接 : " + (connType == XPG_WAN_LAN.LAN.swigValue()));
		L.e(this, "是否大循环连接 : " + (connType == XPG_WAN_LAN.MQTT.swigValue()));

		L.e(this, "mPasscode : " + mPasscode);

		tempConnId = connId;

		if (connType == XPG_WAN_LAN.LAN.swigValue()) {

			if (TextUtils.isEmpty(mPasscode)) {
				if (isActived) {
					L.e(this, "SendPasscodeReq()");
					generated.SendPasscodeReq(tempConnId);
				}
			} else {
				L.e(this, "XPGConnectClient.xpgcLogin()");
				XPGConnectClient.xpgcLogin(tempConnId, null, mPasscode);
			}
		}
	}

	@Override
	public void OnPasscodeResp(PasscodeResp_t pResp, int nConnId) { // generated.SendPasscodeReq()之后回调
		super.OnPasscodeResp(pResp, nConnId);
		L.e(this, "OnPasscodeResp()返回的nConnId : " + nConnId);

		tempConnId = nConnId;

		passcodeRetrieved = generated.XpgData2String(pResp.getPasscode());

		L.e(this, "passcodeRetrieved == null : " + (passcodeRetrieved == null));
		L.e(this, "passcodeRetrieved 是否为空 : " + ("".equals(passcodeRetrieved)));

		L.e(this, "请求回来的passcode是 : passcodeRetrieved ==  " + passcodeRetrieved);

		if (passcodeRetrieved == null || "".equals(passcodeRetrieved)) {
			L.e(this, "请求回到的passcode为空");
		}

		L.e(this, "XPGConnectClient.xpgcLogin()");
		XPGConnectClient.xpgcLogin(tempConnId, null, passcodeRetrieved);
	}

	@Override
	public void OnLanLoginResp(LanLoginResp_t pResp, int nConnId) { // XPGConnectClient.xpgcLogin()之后回调
		super.OnLanLoginResp(pResp, nConnId);

		L.e(this, "OnLanLoginResp()返回的nConnId : " + nConnId
				+ " pResp.getResult() : " + pResp.getResult());

		tempConnId = nConnId;

		if (pResp.getResult() == 0) {

			Intent data = new Intent();
			data.putExtra(Consts.INTENT_EXTRA_CONNID, tempConnId);
			data.putExtra(Consts.INTENT_EXTRA_ISONLINE, true);

			data.putExtra(Consts.INTENT_EXTRA_MAC, mMac);
			data.putExtra(Consts.INTENT_EXTRA_PASSCODE, passcodeRetrieved);
			data.putExtra(Consts.INTENT_EXTRA_DID, didRetrieved);

			String conntext = getIntent().getStringExtra(
					Consts.INTENT_EXTRA_CONNECT_TEXT);
			if (conntext == null) {
				conntext = "";
			}
			data.putExtra(Consts.INTENT_EXTRA_CONNECT_TEXT, conntext);

			setResult(RESULT_OK, data);
			timeoutHandler.removeMessages(0);
			finish();
		}
	}

	private void connectAfterGetBindingDevicesReceivedFromMQTT(
			XpgEndpoint endpoint) {
		L.e(this, "connectAfterGetBindingDevicesReceivedFromMQTT()");

		boolean isBinded = false;

		L.e(this, "要连接的mMac : " + mMac);
		L.e(this, "endpoint.getSzMac() : " + endpoint.getSzMac());
		if (endpoint.getSzMac().toLowerCase().equals(mMac.toLowerCase())) {

			isBinded = true;

			passcodeRetrieved = endpoint.getSzPasscode();
			didRetrieved = endpoint.getSzDid();

			L.e(this, mMac + " isOnline : " + (endpoint.getIsOnline() == 1));

			if (endpoint.getIsOnline() == 1) {
				L.e(this, "AccountService.getUserId(getBaseContext() : "
						+ AccountService.getUserId(getBaseContext()));
				L.e(this, "AccountService.getUserPsw(getBaseContext() : "
						+ AccountService.getUserPsw(getBaseContext()));
				L.e(this, "ep.getSzDid() : " + endpoint.getSzDid());
				L.e(this, "ep.getSzPasscode() : " + endpoint.getSzPasscode());

				String userName = "2$" + Consts.VANWARD_APP_ID + "$"
						+ Global.uid;
				L.e(this, "XPGConnectClient.xpgcLogin2Wan()来连接设备");
				XPGConnectClient.xpgcLogin2Wan(userName, Global.token,
						endpoint.getSzDid(), endpoint.getSzPasscode()); // 连接设备
				return;
			} else { // offline
				setOfflineResult();
				return;
			}
		}

		// 如果服务器上没有该设备，则显示不在线
		if (!isBinded) {
			L.e(this, mMac + " 未绑定此设备");
			// HeaterInfoService hser = new HeaterInfoService(getBaseContext());
			// HeaterInfo curHeater = hser.getCurrentSelectedHeater();
			//
			// L.e(this, "HeaterInfoService.setBinding()");
			// HeaterInfoService.setBinding(this, curHeater.getDid(),
			// curHeater.getPasscode());
			setOfflineResult();
		}
	}

	private void setOfflineResult() {
		L.e(this, "setOfflineResult()");

		Intent data = new Intent();
		data.putExtra(Consts.INTENT_EXTRA_CONNID, tempConnId);
		data.putExtra(Consts.INTENT_EXTRA_ISONLINE, false);

		data.putExtra(Consts.INTENT_EXTRA_MAC, mMac);
		data.putExtra(Consts.INTENT_EXTRA_PASSCODE, "");
		data.putExtra(Consts.INTENT_EXTRA_DID, "");

		String conntext = getIntent().getStringExtra(
				Consts.INTENT_EXTRA_CONNECT_TEXT);
		if (conntext == null) {
			conntext = "";
		}
		data.putExtra(Consts.INTENT_EXTRA_CONNECT_TEXT, conntext);

		setResult(RESULT_OK, data);
		finish();
	}

	public static void connectToDevice(Activity act, String mac, String userId,
			String userPsw) {
		connectToDevice(act, mac, "", userId, userPsw, "");
	}

	public static void connectToDevice(Activity act, String mac,
			String passcode, String userId, String userPsw) {
		connectToDevice(act, mac, passcode, userId, userPsw, "");
	}

	public static void connectToDevice(Activity act, String mac,
			String passcode, String userId, String userPsw, String connectText) {

		DialogUtil.instance().dismissDialog();

		Intent intent = new Intent(act.getBaseContext(), ConnectActivity.class);
		intent.putExtra(Consts.INTENT_EXTRA_MAC, mac);
		intent.putExtra(Consts.INTENT_EXTRA_PASSCODE, passcode);
		intent.putExtra(Consts.INTENT_EXTRA_USERNAME, userId);
		intent.putExtra(Consts.INTENT_EXTRA_USERPSW, userPsw);
		intent.putExtra(Consts.INTENT_EXTRA_CONNECT_TEXT, connectText);

		act.startActivityForResult(intent, Consts.REQUESTCODE_CONNECT_ACTIVITY);
	}

	public static void connectDirectly(Activity act, String passcode,
			String userId, String userPsw, String connectText) {
		Intent intent = new Intent(act.getBaseContext(), ConnectActivity.class);
		intent.putExtra(
				EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK, true);

		intent.putExtra(Consts.INTENT_EXTRA_PASSCODE, passcode);
		intent.putExtra(Consts.INTENT_EXTRA_USERNAME, userId);
		intent.putExtra(Consts.INTENT_EXTRA_USERPSW, userPsw);
		intent.putExtra(Consts.INTENT_EXTRA_CONNECT_TEXT, connectText);

		act.startActivityForResult(intent, Consts.REQUESTCODE_CONNECT_ACTIVITY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		L.e(this, "onActivityResult()");
		if (requestCode == Consts.REQUESTCODE_UPLOAD_BINDING
				&& resultCode == RESULT_OK) {
			onSameDeviceFoundCounter = 0;
			// tryConnectByBigCycle();
		}
	}
}
