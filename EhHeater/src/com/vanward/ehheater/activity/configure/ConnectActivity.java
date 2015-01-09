package com.vanward.ehheater.activity.configure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
	
	private static final String TAG = "ConnectActivity";

	/** 小循环扫描设备周期,ms */
	private final static int defaultScanInterval = 2000;

	private final static int LAN_NONE = 0;
	private final static int LAN_SEARCHING = 1;
	private final static int LAN_FOUND = 2;

	private TextView mTvInfo, mTvInfo2;
	
	private Button mBtnRetry;
	
	private ProgressBar mPbar;
	

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
	
	private List<XpgEndpoint> tempEndpointList = new ArrayList<XpgEndpoint>();
	
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
//			item.delete();
		}
		tempEndpointList.clear();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_connect_as_dialog);
		mTvInfo = (TextView) findViewById(R.id.awad_tv);
		mTvInfo2 = (TextView) findViewById(R.id.awad_tv_2);
		mBtnRetry = (Button) findViewById(R.id.awad_btn_retry);
		mPbar = (ProgressBar) findViewById(R.id.acad_pbar);

		mBtnRetry.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				helper1();
			}
		});
		
		
		helper1();
	}
	
	@Override
	public void onBackPressed() {
		
	};
	
	private void helper1() {

//		mPbar.setVisibility(View.VISIBLE);
//		mBtnRetry.setVisibility(View.GONE);
//		mTvInfo2.setText("连接中...");
		if (!NetworkStatusUtil.isConnected(getBaseContext())) {
			// 无任何网络连接
//			mTvInfo.setText("无网络连接");
//			mTvInfo2.setText("无网络连接");
//			mBtnRetry.setVisibility(View.VISIBLE);
//			mPbar.setVisibility(View.GONE);

			setOfflineResult();
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
				mTvInfo.setText("通过云端连接中...");
			};
		});
		
		timeoutHandler.sendEmptyMessageDelayed(0, 45000);

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

		startFind = new Timer();
		startFind.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				Log.e(TAG, "finding device");
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
	Timer startFind;

	@Override
	public void onDeviceFound(XpgEndpoint endpoint) {
		super.onDeviceFound(endpoint);
		
		if (null == endpoint) {
			return;
		}
		
		if (connType == XPG_WAN_LAN.LAN.swigValue()) {

			if (currentLanSearchingState == LAN_FOUND) {
				return;
			}
			
			Log.e(TAG, "onDeviceFound@ConnectActivity(SMALL): " + endpoint.getSzMac() + "-" + 
					endpoint.getSzDid() + "-" + endpoint.getIsOnline());

			String macFound = endpoint.getSzMac().toLowerCase();
			String didFound = endpoint.getSzDid();
			
			Log.e(TAG, "endpoint.getSzDid() : " + endpoint.getSzDid());
			
			Log.e(TAG, "mMac : " + mMac.toLowerCase());
			Log.e(TAG, "endpoint.getSzMac().toLowerCase() : " + endpoint.getSzMac().toLowerCase());
			Log.e(TAG, "endpoint.getSzDid(): " +  endpoint.getSzDid());

			if (!TextUtils.isEmpty(macFound) && macFound.equals(mMac.toLowerCase())) {
				didRetrieved = didFound;
				Log.e(TAG, "onDeviceFound:found target, connecting by small");
				timeoutHandler.sendEmptyMessageDelayed(0, 5000);
				XPGConnShortCuts.connect2small(endpoint.getAddr());
				currentLanSearchingState = LAN_FOUND;
				if (startFind != null) {
					startFind.cancel();
				}
			}
			
		}
		

		if (connType == XPG_WAN_LAN.MQTT.swigValue()) {
			
			Log.e(TAG, "onDeviceFound@ConnectActivity(BIG): " + endpoint.getSzMac() + "-" + 
					endpoint.getSzDid() + "-" + endpoint.getIsOnline());
			
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

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		tempConnId = connId;
		Log.e(TAG, "onConnectEvent@ConnectActivity" + connId + "-" + event);

		if (connType == XPG_WAN_LAN.LAN.swigValue()) {
			
			if (TextUtils.isEmpty(mPasscode)) {
				generated.SendPasscodeReq(tempConnId);
				Log.e(TAG, "onConnectEvent:requesting passcode");
			} else {
				XPGConnectClient.xpgcLogin(tempConnId, null, mPasscode);
			}
			
		}

		if (connType == XPG_WAN_LAN.MQTT.swigValue()) {
			XPGConnectClient.xpgcLogin(tempConnId, getUserId(), getUserPsw());
			Log.e(TAG, "onConnectEvent:connecting by big");
		}
	}

	@Override
	public void OnPasscodeResp(PasscodeResp_t pResp, int nConnId) {
		super.OnPasscodeResp(pResp, nConnId);

		passcodeRetrieved = generated.XpgData2String(pResp.getPasscode());

		XPGConnectClient.xpgcLogin(tempConnId, null, passcodeRetrieved);
		Log.e(TAG, "OnPasscodeResp: connecting by small");
		
//		pResp.delete();
	}

	@Override
	public void OnLanLoginResp(LanLoginResp_t pResp, int nConnId) {
		super.OnLanLoginResp(pResp, nConnId);
		Log.e(TAG, "OnLanLoginResp@ConnectActivity: " + pResp.getResult());

		if (pResp.getResult() == 0) {
			mTvInfo.setText("设备已连接!");
			jobDone = true;
//			generated.SendStateReq(Global.connectId);
			
			Intent data = new Intent();
			data.putExtra(Consts.INTENT_EXTRA_CONNID, tempConnId);
			data.putExtra(Consts.INTENT_EXTRA_ISONLINE, true);
			
			data.putExtra(Consts.INTENT_EXTRA_MAC, mMac);
			data.putExtra(Consts.INTENT_EXTRA_PASSCODE, passcodeRetrieved);
			data.putExtra(Consts.INTENT_EXTRA_DID, didRetrieved);
			
			String conntext = getIntent().getStringExtra(Consts.INTENT_EXTRA_CONNECT_TEXT);
			if (conntext == null) {
				conntext = "";
			}
			data.putExtra(Consts.INTENT_EXTRA_CONNECT_TEXT, conntext);	
			
			setResult(RESULT_OK, data);
			finish();
		}
//		pResp.delete();
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
		Log.e(TAG, "onLoginCloudResp@ConnectActivity: " + result);
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
			
			String conntext = getIntent().getStringExtra(Consts.INTENT_EXTRA_CONNECT_TEXT);
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
			// if in, check online state, if online, enable ctrl, setresult, if offline, enter app with offline state
			// if not in, can't control -- enter app with offline state
			generated.SendBindingGetV2Req(tempConnId);
			break;
		}
	}
	
	private void doAfterBindingDevicesReceivedFromMQTT(List<XpgEndpoint> devList) {
		jobDone = true;
		for (XpgEndpoint ep : devList) {
			if (ep.getSzMac().toLowerCase().equals(mMac.toLowerCase())) {
				
				passcodeRetrieved = ep.getSzPasscode();
				didRetrieved = ep.getSzDid();
				Log.e(TAG, mMac + " isOnline? " + ep.getIsOnline());
				
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
		Log.e(TAG, mMac + " isOnline? " + "未绑定此设备");
	}
	
	private void setOfflineResult() {
		mTvInfo.setText("设备不在线");
		Intent data = new Intent();
		data.putExtra(Consts.INTENT_EXTRA_CONNID, tempConnId);
		data.putExtra(Consts.INTENT_EXTRA_ISONLINE, false);

		data.putExtra(Consts.INTENT_EXTRA_MAC, mMac);
		data.putExtra(Consts.INTENT_EXTRA_PASSCODE, "");
		data.putExtra(Consts.INTENT_EXTRA_DID, "");
		
		String conntext = getIntent().getStringExtra(Consts.INTENT_EXTRA_CONNECT_TEXT);
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
		mMac = getIntent().getStringExtra(Consts.INTENT_EXTRA_MAC);
		if (TextUtils.isEmpty(mMac)) {
			setOfflineResult();
		}
		
		mPasscode = passcodeRetrieved = getIntent().getStringExtra(Consts.INTENT_EXTRA_PASSCODE);
		String connectText = getIntent().getStringExtra(Consts.INTENT_EXTRA_CONNECT_TEXT);
//		if (!TextUtils.isEmpty(connectText)) {
//			mTvInfo2.setText(connectText);
//		}
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
					mTvInfo.setText("通过局域网连接中...");
					currentLanSearchingState = LAN_SEARCHING;
					tryConnectBySmallCycle(defaultScanInterval, 8000,
							new TimerTask() {
								@Override
								public void run() {
									runOnUiThread(new Runnable() {
										public void run() {
											mTvInfo.setText("通过局域网连接热水器失败");
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
					mTvInfo.setText("通过局域网连接中...");
					currentLanSearchingState = LAN_SEARCHING;
					tryConnectBySmallCycle(defaultScanInterval, 8000,
							new TimerTask() {
								@Override
								public void run() {
									runOnUiThread(new Runnable() {
										public void run() {
											mTvInfo.setText("通过局域网连接热水器失败");
										};
									});
								}
							});

				} else {
					// 无法控制
					mTvInfo.setText("无法连接设备");
				}

				break;

			}

			return false;
		}
	});

	private final static int STATE_NORMAL = 1;
	private final static int STATE_LAN_ONLY = 2;
	
	public static void connectToDevice(Activity act, String mac, String userId, String userPsw) {
		connectToDevice(act, mac, "", userId, userPsw, "");
	}
	
	public static void connectToDevice(Activity act, String mac, String passcode, String userId, String userPsw) {
		connectToDevice(act, mac, passcode, userId, userPsw, "");
	}
	
	public static void connectToDevice(Activity act, String mac, String passcode, String userId, String userPsw, String connectText) {

		Intent intent = new Intent(act.getBaseContext(), ConnectActivity.class);
		intent.putExtra(Consts.INTENT_EXTRA_MAC, mac);
		intent.putExtra(Consts.INTENT_EXTRA_PASSCODE, passcode);
		intent.putExtra(Consts.INTENT_EXTRA_USERNAME, userId);
		intent.putExtra(Consts.INTENT_EXTRA_USERPSW, userPsw);	
		intent.putExtra(Consts.INTENT_EXTRA_CONNECT_TEXT, connectText);	
		
		act.startActivityForResult(intent, Consts.REQUESTCODE_CONNECT_ACTIVITY);
		
	/* 以下是在调用者activity的onActivityResult中取出结果的代码, 为了省力, 写到这里
		
		int connId = data.getIntExtra(Consts.INTENT_EXTRA_CONNID, -1);
		boolean isOnline = data.getBooleanExtra(Consts.INTENT_EXTRA_ISONLINE, true);
		String did = data.getStringExtra(Consts.INTENT_EXTRA_DID);
		String passCode = data.getStringExtra(Consts.INTENT_EXTRA_PASSCODE);
		
	*/
		
	}
}
