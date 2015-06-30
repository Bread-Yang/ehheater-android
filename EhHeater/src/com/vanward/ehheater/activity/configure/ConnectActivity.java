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

	private static final String TAG = "ConnectActivity";

	/** 小循环扫描设备周期,ms */
	private final static int defaultScanInterval = 2000;

	private final static int defaultScanTimeout = 10000;

	private final static int LAN_NONE = 0;
	private final static int LAN_SEARCHING = 1;
	private final static int LAN_FOUND = 2;

	/** 建立的连接类型, LAN / MQTT(大) */
	private int connType = Integer.MAX_VALUE;

	/** 当前小循环搜索设备的状态, 同一时间点只可能有一个小循环搜索任务在执行 */
	private int currentLanSearchingState = LAN_NONE;

	private int tempConnId = -1;

	private String mMac = "";

	private String mPasscode = "";

	private String passcodeRetrieved = "";

	private String didRetrieved = "";

	private boolean jobDone = false;

	private int onDeviceFoundCounter;

	private boolean isActived = false;

	private List<XpgEndpoint> tempEndpointList = new ArrayList<XpgEndpoint>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.e(this, "onCreate()");

		DialogUtil.instance().dismissDialog();

		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().build());
		}

		setContentView(R.layout.activity_connect_as_dialog);

		connectToDevice();
	}

	@Override
	protected void onResume() {
		super.onResume();
		L.e(this, "onResume()");
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

	private void initTemporaryFields() {
		connType = Integer.MAX_VALUE;
		currentLanSearchingState = LAN_NONE;
		tempConnId = -1;
		mMac = "";
		mPasscode = "";
		passcodeRetrieved = "";
		didRetrieved = "";
		onDeviceFoundCounter = 0;
		jobDone = false;
		for (XpgEndpoint item : tempEndpointList) {
			// item.delete();
		}
		tempEndpointList.clear();
	}

	private void connectToDevice() {
		L.e(this, "connectToDevice()");

		DialogUtil.instance().dismissDialog();

		if (!NetworkStatusUtil.isConnected(getBaseContext())) {
			setOfflineResult();
			return;
		}

		initTemporaryFields();
		initTargetDeviceInfo();

		checkLoginAndCurrentDeviceStatus(getBaseContext(), flowHandler);
	}

	private Handler timeoutHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// if (!jobDone) {
			setOfflineResult();
			// }
			return false;
		}
	});

	/**
	 * 小循环唯一入口
	 * 
	 * @param scanInterval
	 * @param timeOut
	 * @param t
	 *            超时未找到, 则执行t
	 */
	private void tryConnectBySmallCycle(final int scanInterval, int timeOut,
			final TimerTask t) {
		L.e(this, "tryConnectBySmallCycle()");

		connType = XPG_WAN_LAN.LAN.swigValue();

		L.e(this, "XPGConnectClient.xpgcStartDiscovery()");
		XPGConnectClient.xpgcStartDiscovery();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				L.e(this, "XPGConnectClient.xpgcStopDiscovery()");
				XPGConnectClient.xpgcStopDiscovery();
				if (currentLanSearchingState == LAN_SEARCHING && t != null) {
					new Timer().schedule(t, (long) (scanInterval * 1.2));
					currentLanSearchingState = LAN_NONE;
				}
			}
		}, timeOut);
	}

	private void tryConnectByBigCycle() {
		L.e(this, "tryConnectByBigCycle()");

		connType = XPG_WAN_LAN.MQTT.swigValue();

		// XPGConnectClient.xpgcLogin2Wan(
		// AccountService.getUserId(getBaseContext()),
		// AccountService.getUserPsw(getBaseContext()), "", "");
		// XPGConnShortCuts.connect2big();

		if ("".equals(Global.token) || "".equals(Global.uid)) {
			XPGConnectClient.xpgc4Login(Consts.VANWARD_APP_ID,
					AccountService.getUserId(getBaseContext()),
					AccountService.getUserPsw(getBaseContext()));
		} else {
			XPGConnectClient.xpgc4GetMyBindings(Consts.VANWARD_APP_ID,
					Global.token, 20, 0);
		}

		timeoutHandler.sendEmptyMessageDelayed(0, 30000);
	}

	@Override
	public void onV4GetMyBindings(int errorCode, XpgEndpoint endpoint) {
		super.onV4GetMyBindings(errorCode, endpoint);

		L.e(this,
				"onV4GetMyBindings: mac : " + endpoint.getSzMac() + "- did : "
						+ endpoint.getSzDid() + "- isOnline : "
						+ endpoint.getIsOnline());

		if ("".equals(endpoint.getSzMac()) || "".equals(endpoint.getSzDid())) {
			return;
		}

		if (onDeviceFoundCounter++ == 0) {

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// 接收绑定的设备列表完毕
					doAfterBindingDevicesReceivedFromMQTT(tempEndpointList);
				}
			}, 8000);

		}

		tempEndpointList.add(endpoint);

	}

	@Override
	public void onDeviceFound(XpgEndpoint endpoint) {
		super.onDeviceFound(endpoint);
		L.e(this, "onDeviceFound()");
		if (null == endpoint) {
			return;
		}

		boolean directConnectAfterEasyLink = getIntent().getBooleanExtra(
				EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK, false);

		if (connType == XPG_WAN_LAN.LAN.swigValue()
				|| directConnectAfterEasyLink) {

			L.e(this,
					"onDeviceFound@ConnectActivity(SMALL): "
							+ endpoint.getSzMac() + "-" + endpoint.getSzDid()
							+ "-" + endpoint.getIsOnline());

			if (endpoint.getSzMac() == null || endpoint.getSzDid() == null) {
				return;
			}

			String macFound = endpoint.getSzMac().toLowerCase();
			String didFound = endpoint.getSzDid();

			L.e(this, "返回的endpoint.getSzMac()是否为null : " + (null == macFound));
			L.e(this, "返回的endpoint.getSzMac()是否为空字符串 : "
					+ ("".equals(macFound)));

			L.e(this, "endpoint.getSzDid() : " + endpoint.getSzDid());
			
			L.e(this, "mMac : " + mMac.toLowerCase());
			L.e(this, "endpoint.getSzMac().toLowerCase() : "
					+ endpoint.getSzMac().toLowerCase());
			L.e(this, "endpoint.getSzDid(): " + endpoint.getSzDid());

			if (!TextUtils.isEmpty(macFound)
					&& macFound.equals(mMac.toLowerCase())) {
				didRetrieved = didFound;
				L.e(this, "onDeviceFound:found target, connecting by small");
				timeoutHandler.sendEmptyMessageDelayed(0, 5000);
				L.e(this, "要连接设备的IP : " + endpoint.getAddr());
				L.e(this, "XPGConnShortCuts.connect2small()");
				XPGConnShortCuts.connect2small(endpoint.getAddr());
				// XPGConnectClient.xpgcLogin2Lan(endpoint.getAddr(), null);

				L.e(this, "didRetrieved : " + didRetrieved);
				L.e(this, "endpoint.getAddr() : " + endpoint.getAddr());

				currentLanSearchingState = LAN_FOUND;
				L.e(this, "XPGConnectClient.xpgcStopDiscovery()");
				XPGConnectClient.xpgcStopDiscovery();
			}

		} else if (connType == XPG_WAN_LAN.MQTT.swigValue()) {

			L.e(this,
					"onDeviceFound@ConnectActivity(BIG): mac : "
							+ endpoint.getSzMac() + "- did : "
							+ endpoint.getSzDid() + "- isOnline : "
							+ endpoint.getIsOnline());

			if (onDeviceFoundCounter++ == 0) {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// 接收绑定的设备列表完毕
						doAfterBindingDevicesReceivedFromMQTT(tempEndpointList);
					}
				}, 8000);

			}

			tempEndpointList.add(endpoint);
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
	public void onV4Login(int errorCode, String uid, String token,
			String expire_at) {
		L.e(this, "onV4Login()");
		L.e(this, "errorCode : " + errorCode);
		if (errorCode == 0) {
			Global.uid = uid;
			Global.token = token;

			L.e(this, "uid : " + Global.uid);
			L.e(this, "token : " + Global.token);
			L.e(this, "app_id : " + Consts.VANWARD_APP_ID);

			XPGConnectClient.xpgc4GetMyBindings(Consts.VANWARD_APP_ID,
					Global.token, 20, 0);
		}
	}

	@Override
	public void onWanLoginResp(int result, int connId) {
		super.onWanLoginResp(result, connId);
		L.e(this, "onWanLoginResp@ConnectActivity : result : " + result
				+ " connId : " + connId);
		tempConnId = connId;
		switch (result) {
		case 0: // 可以控制
			// generated.SendStateReq(Global.connectId);

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
			// binding get, save them to a list
			// find cur device
			// if in, check online state, if online, enable ctrl, setresult, if
			// offline, enter app with offline state
			// if not in, can't control -- enter app with offline state
			L.e(this, "generated.SendBindingGetV2Req");
			generated.SendBindingGetV2Req(tempConnId);
			break;
		case 5:
			// 账号密码错误
			break;
		}
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);

		L.e(this, "onConnectEvent回调了");
		L.e(this, "onConnectEvent@ConnectActivity" + connId + "-" + event);

		L.e(this, "是否小循环连接 : " + (connType == XPG_WAN_LAN.LAN.swigValue()));
		L.e(this, "是否大循环连接 : " + (connType == XPG_WAN_LAN.MQTT.swigValue()));

		L.e(this, "mPasscode : " + mPasscode);

		tempConnId = connId;

		if (connType == XPG_WAN_LAN.LAN.swigValue()) {

			if (TextUtils.isEmpty(mPasscode)) {
				if (isActived) {
					L.e(this, "发送请求密码指令");
					generated.SendPasscodeReq(tempConnId);
				}
				L.e(this, "onConnectEvent:requesting passcode");
			} else {
				L.e(this, "XPGConnectClient.xpgcLogin()");
				XPGConnectClient.xpgcLogin(tempConnId, null, mPasscode);
			}
		}

		// if (connType == XPG_WAN_LAN.MQTT.swigValue()) {
		// XPGConnectClient.xpgcLogin(tempConnId, getUserId(), getUserPsw());
		// L.e(this, "onConnectEvent:connecting by big");
		// }
	}

	@Override
	public void OnPasscodeResp(PasscodeResp_t pResp, int nConnId) {
		super.OnPasscodeResp(pResp, nConnId);

		L.e(this, "OnPasscodeResp()回调了");

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

		// pResp.delete();
	}

	@Override
	public void OnLanLoginResp(LanLoginResp_t pResp, int nConnId) {
		super.OnLanLoginResp(pResp, nConnId);

		L.e(this, "OnLanLoginResp@ConnectActivity: " + pResp.getResult());

		L.e(this, "OnLanLoginResp()返回的nConnId : " + nConnId);

		tempConnId = nConnId;

		if (pResp.getResult() == 0) {
			jobDone = true;
			// generated.SendStateReq(Global.connectId);

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

	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
		L.e(this, "onLoginCloudResp()回调了");
		L.e(this, "onLoginCloudResp@ConnectActivity: result : " + result
				+ " mac : " + mac);
		// switch (result) {
		// case 0: // 可以控制
		// mTvInfo.setText("设备已连接!");
		// // generated.SendStateReq(Global.connectId);
		//
		// Intent data = new Intent();
		// data.putExtra(Consts.INTENT_EXTRA_CONNID, tempConnId);
		// data.putExtra(Consts.INTENT_EXTRA_ISONLINE, true);
		//
		// data.putExtra(Consts.INTENT_EXTRA_MAC, mac);
		// data.putExtra(Consts.INTENT_EXTRA_PASSCODE, passcodeRetrieved);
		// data.putExtra(Consts.INTENT_EXTRA_DID, didRetrieved);
		//
		// String conntext = getIntent().getStringExtra(
		// Consts.INTENT_EXTRA_CONNECT_TEXT);
		// if (conntext == null) {
		// conntext = "";
		// }
		// data.putExtra(Consts.INTENT_EXTRA_CONNECT_TEXT, conntext);
		//
		// setResult(RESULT_OK, data);
		// finish();
		// break;
		// case 1:
		// // binding get, save them to a list
		// // find cur device
		// // if in, check online state, if online, enable ctrl, setresult, if
		// // offline, enter app with offline state
		// // if not in, can't control -- enter app with offline state
		// generated.SendBindingGetV2Req(tempConnId);
		// break;
		// }
	}

	private void doAfterBindingDevicesReceivedFromMQTT(List<XpgEndpoint> devList) {
		L.e(this, "doAfterBindingDevicesReceivedFromMQTT()");
		jobDone = true;
		boolean isBinded = false;
		for (XpgEndpoint ep : devList) {
			if (ep.getSzMac().toLowerCase().equals(mMac.toLowerCase())) {

				isBinded = true;

				passcodeRetrieved = ep.getSzPasscode();
				didRetrieved = ep.getSzDid();
				L.e(this, mMac + " isOnline ? " + ep.getIsOnline());

				if (ep.getIsOnline() == 1) {
					// is online
					L.e(this, "XPGConnectClient.xpgcEnableCtrl()");
					// XPGConnectClient.xpgcEnableCtrl(tempConnId,
					// ep.getSzDid(),
					// ep.getSzPasscode());
					L.e(this, "AccountService.getUserId(getBaseContext() : "
							+ AccountService.getUserId(getBaseContext()));
					L.e(this, "AccountService.getUserPsw(getBaseContext() : "
							+ AccountService.getUserPsw(getBaseContext()));
					L.e(this, "ep.getSzDid() : " + ep.getSzDid());
					L.e(this, "ep.getSzPasscode() : " + ep.getSzPasscode());
					String userName = "2$" + Consts.VANWARD_APP_ID + "$"
							+ Global.uid;
					L.e(this, "XPGConnectClient.xpgcLogin2Wan()");
					XPGConnectClient.xpgcLogin2Wan(userName, Global.token,
							ep.getSzDid(), ep.getSzPasscode());
					// XPGConnectClient.xpgcLogin2Wan(
					// AccountService.getUserId(getBaseContext()),
					// AccountService.getUserPsw(getBaseContext()),
					// ep.getSzDid(), ep.getSzPasscode());
					return;
				} else {
					// is offline
					setOfflineResult();
					return;
				}
			}
		}

		// 如果服务器上没有该设备，则再上传绑定关系
		if (!isBinded) {
			// HeaterInfoService hser = new HeaterInfoService(getBaseContext());
			// HeaterInfo curHeater = hser.getCurrentSelectedHeater();
			//
			// L.e(this, "HeaterInfoService.setBinding()");
			// HeaterInfoService.setBinding(this, curHeater.getDid(),
			// curHeater.getPasscode());
			setOfflineResult();
		} else {
			// is offline
			setOfflineResult();
			L.e(this, mMac + " isOnline? " + "未绑定此设备");
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

	private String getUserId() {
		return getIntent().getStringExtra(Consts.INTENT_EXTRA_USERNAME);
	}

	private String getUserPsw() {
		return getIntent().getStringExtra(Consts.INTENT_EXTRA_USERPSW);
	}

	private void initTargetDeviceInfo() {
		if (!getIntent().getBooleanExtra(
				EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK, false)) {
			mMac = getIntent().getStringExtra(Consts.INTENT_EXTRA_MAC);

			L.e(this, "mMac == null : " + (mMac == null));
			L.e(this, "mMac为空 : " + ("".equals(mMac)));

			L.e(this, "getIntent().getStringExtra(Consts.INTENT_EXTRA_MAC) : "
					+ getIntent().getStringExtra(Consts.INTENT_EXTRA_MAC));

			if (TextUtils.isEmpty(mMac)) {
				setOfflineResult();
			}
		}

		mPasscode = passcodeRetrieved = getIntent().getStringExtra(
				Consts.INTENT_EXTRA_PASSCODE);

		L.e(this, "mPasscode : " + mPasscode);

		String connectText = getIntent().getStringExtra(
				Consts.INTENT_EXTRA_CONNECT_TEXT);
	}

	public static String testTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss'.'SSS");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		return sdf.format(c.getTime());
	}

	/**
	 * 检查是否已登录账号, 未登录账号则跳转到登录页面, 返回STATE_JUMPED_OUT </br> 然后检查是否有当前设备, 无则跳转到
	 * 选择/新增 设备页面 , 返回STATE_JUMPED_OUT</br> 然后检查当前设备是否有did, 无did说明设备未上大循环,
	 * 此时只能通过小循环控制, 返回STATE_LAN_ONLY </br> 最后, 如果已登录&有当前设备&当前设备有did, 则大小循环都可以控制,
	 * 返回STATE_NORMAL
	 */
	private void checkLoginAndCurrentDeviceStatus(Context context,
			Handler flowHandler) {

		L.e(this, "checkLoginAndCurrentDeviceStatus()");

		flowHandler.sendEmptyMessage(STATE_NORMAL);
		return;
	}

	private Handler flowHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {

			L.e(this,
					"NetworkStatusUtil.isConnectedByMobileData(getBaseContext() : "
							+ NetworkStatusUtil
									.isConnectedByMobileData(getBaseContext()));

			L.e(this,
					"NetworkStatusUtil.isConnectedByWifi(getBaseContext()) : "
							+ NetworkStatusUtil
									.isConnectedByWifi(getBaseContext()));

			switch (msg.what) {

			case STATE_NORMAL:

				L.e(this, "flowHandler 的 STATE_NORMAL Message");

				DialogUtil.instance().dismissDialog();

				if (NetworkStatusUtil.isConnectedByWifi(getBaseContext())) {
					// 先试小循环, 不行则大
					currentLanSearchingState = LAN_SEARCHING;
					L.e(this, "小循环");
					if (getIntent()
							.getBooleanExtra(
									EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK,
									false)) { // easylink后直接通过endpoint连接
						connectDirectlyAfterEasyLink();
					} else {
						tryConnectBySmallCycle(defaultScanInterval,
								defaultScanTimeout, new TimerTask() {
									@Override
									public void run() {
										runOnUiThread(new Runnable() {
											public void run() {
											};
										});
										// 启动大循环
										tryConnectByBigCycle();
									}
								});
					}
				} else if (NetworkStatusUtil
						.isConnectedByMobileData(getBaseContext())) {
					L.e(this, "大循环");
					// 只能大循环
					tryConnectByBigCycle();
				}

				break;

			case STATE_LAN_ONLY:
				// 当前设备没有did, 仅能通过小循环控制
				if (NetworkStatusUtil.isConnectedByWifi(getBaseContext())) {
					currentLanSearchingState = LAN_SEARCHING;
					tryConnectBySmallCycle(defaultScanInterval,
							defaultScanTimeout, new TimerTask() {
								@Override
								public void run() {
									runOnUiThread(new Runnable() {
										public void run() {
										};
									});
								}
							});

				} else {
					// 无法控制
				}

				break;

			}

			return false;
		}
	});

	private final static int STATE_NORMAL = 1;
	private final static int STATE_LAN_ONLY = 2;

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
		L.e(this, "在外面");
		if (requestCode == Consts.REQUESTCODE_UPLOAD_BINDING
				&& resultCode == RESULT_OK) {
			L.e(this, "在里面");
			onDeviceFoundCounter = 0;
			jobDone = false;
			tryConnectByBigCycle();
		}
	}
}
