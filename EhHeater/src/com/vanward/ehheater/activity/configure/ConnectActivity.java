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
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;
import com.xtremeprog.xpgconnect.generated.LanLoginResp_t;
import com.xtremeprog.xpgconnect.generated.PasscodeResp_t;
import com.xtremeprog.xpgconnect.generated.XPG_WAN_LAN;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

public class ConnectActivity extends GeneratedActivity {

	private TextView mTvInfo;

	/** 建立的连接类型, LAN(小循环) / MQTT(大) */
	private int connType = Integer.MAX_VALUE;

	/** 小循环扫描设备周期,ms */
	private int defaultScanInterval = 2000;

	private final static int LAN_NONE = 0;
	private final static int LAN_SEARCHING = 1;
	private final static int LAN_FOUND = 2;

	/** 当前小循环搜索设备的状态, 同一时间点只可能有一个小循环搜索任务在执行 */
	private int currentLanSearchingState = LAN_NONE;
	
	private int tempConnId = -1;

	private String mac = "";
	
	private String passcode = "";

	private String passcodeRetrieved = "";

	private String didRetrieved = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_connect_as_dialog);
		mTvInfo = (TextView) findViewById(R.id.awad_tv);

		if (!NetworkStatusUtil.isConnected(getBaseContext())) {
			// 无任何网络连接
			mTvInfo.setText("无网络连接");
			return;
		}
		
		initTemporaryFields();
		initTargetDeviceInfo();

		checkLoginAndCurrentDeviceStatus(getBaseContext(), flowHandler);
	}

	private void tryConnectByBigCycle() {

		XPGConnShortCuts.connect2big();

		connType = XPG_WAN_LAN.MQTT.swigValue();

		runOnUiThread(new Runnable() {
			public void run() {
				mTvInfo.setText("通过机智云连接中...(大循环)");
			};
		});
	}

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

		final Timer startFind = new Timer();
		startFind.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				XPGConnectClient.xpgcFindDevice();
			}
		}, 0, scanInterval);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (startFind != null) {
					startFind.cancel();
				}
				if (currentLanSearchingState == LAN_SEARCHING && t != null) {
					new Timer().schedule(t, (long) (scanInterval * 1.2));
					currentLanSearchingState = LAN_NONE;
				}
			}
		}, timeOut);

		connType = XPG_WAN_LAN.LAN.swigValue();

	}

	@Override
	public void onDeviceFound(XpgEndpoint endpoint) {
		super.onDeviceFound(endpoint);
		Log.d("emmm", "onDeviceFound@ConnectActivity: " + endpoint.getSzMac());
		
		if (connType == XPG_WAN_LAN.LAN.swigValue()) {

			if (currentLanSearchingState == LAN_FOUND) {
				return;
			}

			String macFound = endpoint.getSzMac().toLowerCase();
			String didFound = endpoint.getSzDid();
			Log.d("emmm", "onDeviceFound:found : " + macFound + "-" + endpoint.getSzDid() + "-" + endpoint.getSzPasscode());

			if (!TextUtils.isEmpty(macFound) && macFound.equals(mac.toLowerCase())) {
				didRetrieved = didFound;
				XPGConnShortCuts.connect2small(endpoint.getAddr());
				currentLanSearchingState = LAN_FOUND;
			}
			
		}
		

		if (connType == XPG_WAN_LAN.MQTT.swigValue()) {
			
			if (onDeviceFoundCounter++ == 0) {
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// 接收绑定的设备列表完毕
						doAfterBindingDevicesReceivedFromMQTT(tempEndpointList);
					}
				}, 1000);
				
			}
			
			tempEndpointList.add(endpoint);
		}

	}
	int onDeviceFoundCounter;
	List<XpgEndpoint> tempEndpointList = new ArrayList<XpgEndpoint>();

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		tempConnId = connId;
		Log.d("emmm", "onConnectEvent@ConnectActivity" + connId + "-" + event);

		if (connType == XPG_WAN_LAN.LAN.swigValue()) {
			
			if (TextUtils.isEmpty(passcode)) {
				generated.SendPasscodeReq(tempConnId);
				Log.d("emmm", "onConnectEvent:requesting passcode");
			} else {
				XPGConnectClient.xpgcLogin(tempConnId, null, passcode);
			}
			
		}

		if (connType == XPG_WAN_LAN.MQTT.swigValue()) {
			XPGConnectClient.xpgcLogin(tempConnId, getUserId(), getUserPsw());
			Log.d("emmm", "onConnectEvent:connecting by big");
		}
	}

	@Override
	public void OnPasscodeResp(PasscodeResp_t pResp, int nConnId) {
		super.OnPasscodeResp(pResp, nConnId);

		passcodeRetrieved = generated.XpgData2String(pResp.getPasscode());

		Log.d("emmm", "OnPasscodeResp: connecting by small");
		XPGConnectClient.xpgcLogin(tempConnId, null, passcodeRetrieved);
	}

	@Override
	public void OnLanLoginResp(LanLoginResp_t pResp, int nConnId) {
		super.OnLanLoginResp(pResp, nConnId);
		Log.d("emmm", "OnLanLoginResp@ConnectActivity: " + pResp.getResult());

		if (pResp.getResult() == 0) {
			mTvInfo.setText("设备已连接!");
//			generated.SendStateReq(Global.connectId);
			
			Intent data = new Intent();
			data.putExtra(Consts.INTENT_EXTRA_CONNID, tempConnId);
			data.putExtra(Consts.INTENT_EXTRA_ISONLINE, true);
			
			data.putExtra(Consts.INTENT_EXTRA_MAC, mac);
			data.putExtra(Consts.INTENT_EXTRA_PASSCODE, passcodeRetrieved);
			data.putExtra(Consts.INTENT_EXTRA_DID, didRetrieved);
			
			setResult(RESULT_OK, data);
			finish();
		}
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
		Log.d("emmm", "onLoginCloudResp@ConnectActivity: " + result);
		switch (result) {
		case 0: // 可以控制
			mTvInfo.setText("设备已连接!");
//			generated.SendStateReq(Global.connectId);
			
			Intent data = new Intent();
			data.putExtra(Consts.INTENT_EXTRA_CONNID, tempConnId);
			data.putExtra(Consts.INTENT_EXTRA_ISONLINE, true);

			data.putExtra(Consts.INTENT_EXTRA_MAC, mac);
			data.putExtra(Consts.INTENT_EXTRA_PASSCODE, passcodeRetrieved);
			data.putExtra(Consts.INTENT_EXTRA_DID, didRetrieved);
			
			setResult(RESULT_OK, data);
			finish();
			break;
		case 1: 
			// binding get, save them to a list
			// find cur device 
			// if in, check online state, if online, enable ctrl, setresult, if offline, enter app with offline state
			// if not in, can't control -- enter app with offline state
			generated.SendBindingGetV2Req(tempConnId);
			break;
		}
	}
	
	private void initTemporaryFields() {
		onDeviceFoundCounter = 0;
		tempEndpointList.clear();
	}
	
	private void doAfterBindingDevicesReceivedFromMQTT(List<XpgEndpoint> devList) {
		for (XpgEndpoint ep : devList) {
			if (ep.getSzMac().toLowerCase().equals(mac.toLowerCase())) {
				
				passcodeRetrieved = ep.getSzPasscode();
				didRetrieved = ep.getSzDid();
				
				if (ep.getIsOnline() == 1) {
					// is online
					XPGConnectClient.xpgcEnableCtrl(tempConnId, ep.getSzDid(), ep.getSzPasscode());
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
	}
	
	private void setOfflineResult() {
		mTvInfo.setText("设备不在线");
		Intent data = new Intent();
		data.putExtra(Consts.INTENT_EXTRA_CONNID, tempConnId);
		data.putExtra(Consts.INTENT_EXTRA_ISONLINE, false);

		data.putExtra(Consts.INTENT_EXTRA_MAC, mac);
		data.putExtra(Consts.INTENT_EXTRA_PASSCODE, "");
		data.putExtra(Consts.INTENT_EXTRA_DID, "");
		
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
		mac = getIntent().getStringExtra(Consts.INTENT_EXTRA_MAC);
		passcode = passcodeRetrieved = getIntent().getStringExtra(Consts.INTENT_EXTRA_PASSCODE);
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
	private void checkLoginAndCurrentDeviceStatus(Context context, Handler flowHandler) {

		flowHandler.sendEmptyMessage(STATE_NORMAL);
		return;
	}

	private Handler flowHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {

			case STATE_NORMAL:

				if (NetworkStatusUtil.isConnectedByWifi(getBaseContext())) {
					// 先试小循环, 不行则大
					mTvInfo.setText("通过局域网连接中...(小循环)");
					currentLanSearchingState = LAN_SEARCHING;
					tryConnectBySmallCycle(defaultScanInterval, 8000,
							new TimerTask() {
								@Override
								public void run() {
									runOnUiThread(new Runnable() {
										public void run() {
											mTvInfo.setText("通过局域网连接热水器失败(小循环连接失败)");
										};
									});
									// 启动大循环
									tryConnectByBigCycle();
								}
							});

				} else if (NetworkStatusUtil.isConnectedByMobileData(getBaseContext())) {
					// 只能大循环
					tryConnectByBigCycle();
				}

				break;

			case STATE_LAN_ONLY:
				// 当前设备没有did, 仅能通过小循环控制
				if (NetworkStatusUtil.isConnectedByWifi(getBaseContext())) {
					mTvInfo.setText("通过局域网连接中...(小循环)");
					currentLanSearchingState = LAN_SEARCHING;
					tryConnectBySmallCycle(defaultScanInterval, 10000,
							new TimerTask() {
								@Override
								public void run() {
									runOnUiThread(new Runnable() {
										public void run() {
											mTvInfo.setText("通过局域网连接热水器失败(小循环连接失败)");
										};
									});
								}
							});

				} else {
					// 无法控制
					mTvInfo.setText("无法连接设备(设备未上大循环)");
				}

				break;

			}

			return false;
		}
	});

	private final static int STATE_NORMAL = 1;
	private final static int STATE_LAN_ONLY = 2;
	
	/**
	 * 连接成功后会触发调用者activity的onActivityResult回调, 回调参数中会有获取到的connId, isOnline, 
	 * did和passCode, requestCode = REQUESTCODE_CONNECT_ACTIVITY, 点进来有惊喜
	 * 
	 * @param act	调用者activity
	 */
	public static void connectToDevice(Activity act, String mac, String userId, String userPsw) {

		Intent intent = new Intent(act.getBaseContext(), ConnectActivity.class);
		intent.putExtra(Consts.INTENT_EXTRA_MAC, mac);
		intent.putExtra(Consts.INTENT_EXTRA_USERNAME, userId);
		intent.putExtra(Consts.INTENT_EXTRA_USERPSW, userPsw);				
		act.startActivityForResult(intent, Consts.REQUESTCODE_CONNECT_ACTIVITY);
		
		
	/* 以下是在调用者activity的onActivityResult中取出结果的代码, 为了省力, 写到这里
		
		int connId = data.getIntExtra(Consts.INTENT_EXTRA_CONNID, -1);
		boolean isOnline = data.getBooleanExtra(Consts.INTENT_EXTRA_ISONLINE, true);
		String did = data.getStringExtra(Consts.INTENT_EXTRA_DID);
		String passCode = data.getStringExtra(Consts.INTENT_EXTRA_PASSCODE);
		
	*/
		
	}
	
	/**
	 * 连接成功后会触发调用者activity的onActivityResult回调, 回调参数中会有获取到的connId, isOnline, 
	 * did和passCode, requestCode = REQUESTCODE_CONNECT_ACTIVITY, 点进来有惊喜
	 * <br /> 
	 * <br /> 
	 * 此版本可以传进passCode, 以使小循环连接时减少一个步骤
	 * 
	 * @param act	调用者activity
	 */
	public static void connectToDevice(Activity act, String mac, String passcode, String userId, String userPsw) {

		Intent intent = new Intent(act.getBaseContext(), ConnectActivity.class);
		
		intent.putExtra(Consts.INTENT_EXTRA_MAC, mac);
		intent.putExtra(Consts.INTENT_EXTRA_PASSCODE, passcode);
		
		intent.putExtra(Consts.INTENT_EXTRA_USERNAME, userId);
		intent.putExtra(Consts.INTENT_EXTRA_USERPSW, userPsw);			
		
		act.startActivityForResult(intent, Consts.REQUESTCODE_CONNECT_ACTIVITY);
		
		
	/* 以下是在调用者activity的onActivityResult中取出结果的代码, 为了省力, 写到这里
		
		int connId = data.getIntExtra(Consts.INTENT_EXTRA_CONNID, -1);
		boolean isOnline = data.getBooleanExtra(Consts.INTENT_EXTRA_ISONLINE, true);
		String did = data.getStringExtra(Consts.INTENT_EXTRA_DID);
		String passCode = data.getStringExtra(Consts.INTENT_EXTRA_PASSCODE);
		
	*/
		
	}
}
