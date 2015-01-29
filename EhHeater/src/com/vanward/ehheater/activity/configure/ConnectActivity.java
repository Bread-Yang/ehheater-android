package com.vanward.ehheater.activity.configure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.vanward.ehheater.BuildConfig;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.DialogUtil;
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

	private final static int defaultScanTimeout = 8000;

	private final static int LAN_NONE = 0;
	private final static int LAN_SEARCHING = 1;
	private final static int LAN_FOUND = 2;

	private Button mBtnRetry;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate()");

		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().build());
		}

		setContentView(R.layout.activity_connect_as_dialog);
		mBtnRetry = (Button) findViewById(R.id.awad_btn_retry);

		mBtnRetry.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				connectToDevice();
			}
		});

		connectToDevice();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "onResume()");
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
		Log.e(TAG, "initTemporaryFields()执行了");
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
		Log.e(TAG, "connectToDevice()");

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
			if (!jobDone) {
				setOfflineResult();
			}
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
		Log.e(TAG, "tryConnectBySmallCycle()");

		XPGConnectClient.xpgcStartDiscovery();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				XPGConnectClient.xpgcStopDiscovery();
				if (currentLanSearchingState == LAN_SEARCHING && t != null) {
					new Timer().schedule(t, (long) (scanInterval * 1.2));
					currentLanSearchingState = LAN_NONE;
				}
			}
		}, timeOut);

		connType = XPG_WAN_LAN.LAN.swigValue();

	}
	
	private void tryConnectByBigCycle() {
		Log.e(TAG, "tryConnectByBigCycle()");

		XPGConnectClient.xpgcLogin2Wan(
				AccountService.getUserId(getBaseContext()),
				AccountService.getUserPsw(getBaseContext()), "", "");
		// XPGConnShortCuts.connect2big();

		connType = XPG_WAN_LAN.MQTT.swigValue();

		timeoutHandler.sendEmptyMessageDelayed(0, 45000);
	}

	@Override
	public void onDeviceFound(XpgEndpoint endpoint) {
		super.onDeviceFound(endpoint);
		Log.e(TAG, "onDeviceFound()");
		if (null == endpoint) {
			return;
		}

		boolean directConnectAfterEasyLink = getIntent().getBooleanExtra(
				EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK, false);

		if (connType == XPG_WAN_LAN.LAN.swigValue()
				|| directConnectAfterEasyLink) {
			
			Log.e(TAG,
					"onDeviceFound@ConnectActivity(SMALL): "
							+ endpoint.getSzMac() + "-" + endpoint.getSzDid()
							+ "-" + endpoint.getIsOnline());

			String macFound = endpoint.getSzMac().toLowerCase();
			String didFound = endpoint.getSzDid();

			Log.e(TAG, "返回的endpoint.getSzMac()是否为null : " + (null == macFound));
			Log.e(TAG,
					"返回的endpoint.getSzMac()是否为空字符串 : " + ("".equals(macFound)));

			Log.e(TAG, "endpoint.getSzDid() : " + endpoint.getSzDid());

			Log.e(TAG, "mMac : " + mMac.toLowerCase());
			Log.e(TAG, "endpoint.getSzMac().toLowerCase() : "
					+ endpoint.getSzMac().toLowerCase());
			Log.e(TAG, "endpoint.getSzDid(): " + endpoint.getSzDid());

			if (!TextUtils.isEmpty(macFound)
					&& macFound.equals(mMac.toLowerCase())) {
				didRetrieved = didFound;
				Log.e(TAG, "onDeviceFound:found target, connecting by small");
				timeoutHandler.sendEmptyMessageDelayed(0, 5000);
				XPGConnShortCuts.connect2small(endpoint.getAddr());
//				 XPGConnectClient.xpgcLogin2Lan(endpoint.getAddr(), null);

				Log.e(TAG, "didRetrieved : " + didRetrieved);
				Log.e(TAG, "endpoint.getAddr() : " + endpoint.getAddr());

				currentLanSearchingState = LAN_FOUND;
				XPGConnectClient.xpgcStopDiscovery();
			}

		} else if (connType == XPG_WAN_LAN.MQTT.swigValue()) {

			Log.e(TAG,
					"onDeviceFound@ConnectActivity(BIG): "
							+ endpoint.getSzMac() + "-" + endpoint.getSzDid()
							+ "-" + endpoint.getIsOnline());

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
		Log.e(TAG, "connectDirectlyAfterEasyLink()执行了");
		didRetrieved = new SharedPreferUtils(this).get(ShareKey.CurDeviceDid,
				"");
		String ip = new SharedPreferUtils(this).get(ShareKey.CurDeviceAddress,
				"");

		Log.e(TAG, "didRetrieved : " + didRetrieved);
		connType = XPG_WAN_LAN.LAN.swigValue();
		Log.e(TAG, "ip : " + ip);

		timeoutHandler.sendEmptyMessageDelayed(0, 5000);
		XPGConnShortCuts.connect2small(ip);
//		 XPGConnectClient.xpgcLogin2Lan(ip, null);
		Log.e(TAG, "执行了");
	}

	@Override
	public void onWanLoginResp(int result, int connId) {
		super.onWanLoginResp(result, connId);
		Log.e(TAG, "onWanLoginResp()执行了");
		Log.e(TAG, "onWanLoginResp@ConnectActivity : result : " + result
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
			finish();
			break;
		case 1:
			// binding get, save them to a list
			// find cur device
			// if in, check online state, if online, enable ctrl, setresult, if
			// offline, enter app with offline state
			// if not in, can't control -- enter app with offline state
			generated.SendBindingGetV2Req(tempConnId);
			break;
		}
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		tempConnId = connId;
		Log.e(TAG, "onConnectEvent回调了");
		Log.e(TAG, "onConnectEvent@ConnectActivity" + connId + "-" + event);

		Log.e(TAG, "是否小循环连接 : " + (connType == XPG_WAN_LAN.LAN.swigValue()));
		Log.e(TAG, "是否大循环连接 : " + (connType == XPG_WAN_LAN.MQTT.swigValue()));

		if (connType == XPG_WAN_LAN.LAN.swigValue()) {

			if (TextUtils.isEmpty(mPasscode)) {
				if (isActived) {
					generated.SendPasscodeReq(tempConnId);
				}
				Log.e(TAG, "onConnectEvent:requesting passcode");
			} else {
				Log.e(TAG, "XPGConnectClient.xpgcLogin()执行");
				XPGConnectClient.xpgcLogin(tempConnId, null, mPasscode);
			}
		}

		// if (connType == XPG_WAN_LAN.MQTT.swigValue()) {
		// XPGConnectClient.xpgcLogin(tempConnId, getUserId(), getUserPsw());
		// Log.e(TAG, "onConnectEvent:connecting by big");
		// }
	}

	@Override
	public void OnPasscodeResp(PasscodeResp_t pResp, int nConnId) {
		super.OnPasscodeResp(pResp, nConnId);
		Log.e(TAG, "OnPasscodeResp()回调了");

		passcodeRetrieved = generated.XpgData2String(pResp.getPasscode());

		Log.e(TAG, "OnPasscodeResp: connecting by small");
		XPGConnectClient.xpgcLogin(tempConnId, null, passcodeRetrieved);

		// pResp.delete();
	}

	@Override
	public void OnLanLoginResp(LanLoginResp_t pResp, int nConnId) {
		super.OnLanLoginResp(pResp, nConnId);
		Log.e(TAG, "OnLanLoginResp@ConnectActivity: " + pResp.getResult());

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
			finish();
		}
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
		Log.e(TAG, "onLoginCloudResp()回调了");
		Log.e(TAG, "onLoginCloudResp@ConnectActivity: result : " + result
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
		Log.e(TAG, "doAfterBindingDevicesReceivedFromMQTT()");
		jobDone = true;
		for (XpgEndpoint ep : devList) {
			if (ep.getSzMac().toLowerCase().equals(mMac.toLowerCase())) {

				passcodeRetrieved = ep.getSzPasscode();
				didRetrieved = ep.getSzDid();
				Log.e(TAG, mMac + " isOnline? " + ep.getIsOnline());

				if (ep.getIsOnline() == 1) {
					// is online
					Log.e(TAG, "XPGConnectClient.xpgcEnableCtrl()执行了");
					XPGConnectClient.xpgcEnableCtrl(tempConnId, ep.getSzDid(),
							ep.getSzPasscode());
					return;
				} else {
					// is offline
					setOfflineResult();
					return;
				}
			}
		}

		// is offline
		setOfflineResult();
		Log.e(TAG, mMac + " isOnline? " + "未绑定此设备");
	}

	private void setOfflineResult() {
		Log.e(TAG, "setOfflineResult()");
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
		Log.e(TAG, "initTargetDeviceInfo()执行了");
		if (!getIntent().getBooleanExtra(
				EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK, false)) {
			mMac = getIntent().getStringExtra(Consts.INTENT_EXTRA_MAC);

			Log.e(TAG, "getIntent().getStringExtra(Consts.INTENT_EXTRA_MAC) : "
					+ getIntent().getStringExtra(Consts.INTENT_EXTRA_MAC));

			if (TextUtils.isEmpty(mMac)) {
				setOfflineResult();
			}
		}

		mPasscode = passcodeRetrieved = getIntent().getStringExtra(
				Consts.INTENT_EXTRA_PASSCODE);
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

		flowHandler.sendEmptyMessage(STATE_NORMAL);
		return;
	}

	private Handler flowHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {

			Log.e(TAG,
					"NetworkStatusUtil.isConnectedByMobileData(getBaseContext() : "
							+ NetworkStatusUtil
									.isConnectedByMobileData(getBaseContext()));

			Log.e(TAG,
					"NetworkStatusUtil.isConnectedByWifi(getBaseContext()) : "
							+ NetworkStatusUtil
									.isConnectedByWifi(getBaseContext()));

			switch (msg.what) {

			case STATE_NORMAL:

				DialogUtil.instance().dismissDialog();

				if (NetworkStatusUtil.isConnectedByWifi(getBaseContext())) {
					// 先试小循环, 不行则大
					currentLanSearchingState = LAN_SEARCHING;
					Log.e(TAG, "小循环");
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
					Log.e(TAG, "大循环");
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
}
