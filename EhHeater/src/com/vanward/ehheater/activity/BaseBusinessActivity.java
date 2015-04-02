package com.vanward.ehheater.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.configure.ConnectActivity;
import com.vanward.ehheater.activity.configure.EasyLinkConfigureActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.AlterDeviceHelper;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.CheckOnlineUtil;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.vanward.ehheater.util.wifi.ConnectChangeReceiver;
import com.vanward.ehheater.view.fragment.BaseSlidingFragmentActivity;
import com.vanward.ehheater.view.fragment.SlidingMenu;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.DERYStatusResp_t;
import com.xtremeprog.xpgconnect.generated.DeviceOnlineStateResp_t;
import com.xtremeprog.xpgconnect.generated.GasWaterHeaterStatusResp_t;
import com.xtremeprog.xpgconnect.generated.StateResp_t;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

/**
 * 电热, 燃热, 壁挂炉三个MainActivity共用业务:
 * 
 * 1: 自动重连处理 2: 设备上下线相关逻辑 3: 初始化slidingmenu 4: setTitle 5: suicideReceiver:
 * 收到广播时finish自己
 * 
 * 100: 取当前设备-getCurHeater() 101: 连接当前设备: connectCurDevice()
 * 
 * @author Administrator
 * 
 */
public abstract class BaseBusinessActivity extends BaseSlidingFragmentActivity {

	private static final String TAG = "BaseBusinessActivity";

	abstract protected void changeToOfflineUI();

	private boolean shouldReconnect = false;
	private boolean paused = false;

	protected boolean isActived = false;

	private Dialog dialog_exit;

	private BroadcastReceiver wifiConnectedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			L.e(this, "wifiConnectedReceiver的onReceive()执行了");
			boolean isConnected = intent.getBooleanExtra("isConnected", false);
			if (isConnected) {
				if (isActived) {
					if (!NetworkStatusUtil
							.isConnected(BaseBusinessActivity.this)) {
						DialogUtil.instance().showReconnectDialog(null,
								BaseBusinessActivity.this);
					} else {
						connectCurDevice();
					}
				}
			} else {
				changeToOfflineUI();
			}
		}
	};

	private BroadcastReceiver deviceOnlineReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			L.e(this, "deviceOnlineReceiver的onReceive()执行了");
			if (isFinishing()) {
				return;
			}
			connectCurDevice();
		}
	};

	private BroadcastReceiver alterDeviceDueToDeleteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			L.e(this, "alterDeviceDueToDeleteReceiver的onReceive()执行了");
			if (isFinishing()) {
				return;
			}

			AlterDeviceHelper.hostActivity = BaseBusinessActivity.this;

			if (Global.connectId > -1) {
				// 触发BaseBusinessActivity里的断开连接回调, 具体的切换逻辑在该回调中处理
				XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
			} else {
				// 如果当前未建立连接, 直接调用此方法
				AlterDeviceHelper.alterDevice();
			}
		}
	};

	private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			L.e(this, "logoutReceiver的onReceive()执行了");
			XPGConnectClient.RemoveActivity(BaseBusinessActivity.this);
		}
	};

	public static int connectTime = 10000;

	private Handler reconnectHandler = new Handler() {

		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (!DialogUtil.instance().getIsShowing()) {
					if (isActived) {
						DialogUtil.instance().showLoadingDialog(
								BaseBusinessActivity.this, "");
					}
					generated.SendStateReq(Global.connectId);
					reconnectHandler.sendEmptyMessageDelayed(1, connectTime);
					reconnectHandler.removeMessages(0);
				}
				break;
			case 1:
				changeToOfflineUI();
				if (isActived) {
					DialogUtil.instance().showReconnectDialog(
							BaseBusinessActivity.this);
				}
				break;
			}
		};
	};

	@Override
	public void onWriteEvent(int result, int connId) {
		super.onWriteEvent(result, connId);
		L.e(this, "onWriteEvent调用了");
		reconnectHandler.removeMessages(0);
		reconnectHandler.sendEmptyMessageDelayed(0, connectTime);
	}

	@Override
	public void OnStateResp(StateResp_t pResp, int nConnId) {
		L.e(this, "OnStateResp被调用了");
		super.OnStateResp(pResp, nConnId);
	}

	@Override
	public void onTcpPacket(byte[] data, int connId) {
		super.onTcpPacket(data, connId);
		L.e(this, "onTcpPacket被调用了");
		reconnectHandler.removeMessages(0);
	}

	@Override
	public void OnGasWaterHeaterStatusResp(GasWaterHeaterStatusResp_t pResp,
			int nConnId) {
		L.e(this, "OnGasWaterHeaterStatusResp被调用了");
		super.OnGasWaterHeaterStatusResp(pResp, nConnId);
	}

	@Override
	public void OnDERYStatusResp(DERYStatusResp_t pResp, int nConnId) {
		L.e(this, "OnDERYStatusResp被调用了");
		super.OnDERYStatusResp(pResp, nConnId);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		L.e(this, "onCreate");
		super.onCreate(savedInstanceState);

		shouldReconnect = false;
		paused = false;

		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				wifiConnectedReceiver,
				new IntentFilter(ConnectChangeReceiver.CONNECTED));

		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				deviceOnlineReceiver,
				new IntentFilter(CheckOnlineUtil.ACTION_DEVICE_ONLINE));

		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				alterDeviceDueToDeleteReceiver,
				new IntentFilter(
						Consts.INTENT_ACTION_ALTER_DEVICE_DUE_TO_DELETE));

		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				logoutReceiver, new IntentFilter(Consts.INTENT_ACTION_LOGOUT));

		registerSuicideReceiver();

		String mac = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeaterMac();
		CheckOnlineUtil.ins().reset(mac);

		dialog_exit = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.confirm_exit,
						BaoDialogShowUtil.DEFAULT_RESID,
						BaoDialogShowUtil.DEFAULT_RESID, null,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (Global.connectId > -1) {
									XPGConnectClient
											.xpgcDisconnectAsync(Global.connectId);
								}

								if (Global.checkOnlineConnId > 0) {
									XPGConnectClient
											.xpgcDisconnectAsync(Global.checkOnlineConnId);
								}

								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						});
	}

	@Override
	protected void onResume() {
		L.e(this, "onResume");
		super.onResume();

		isActived = true;

		paused = false;

		CheckOnlineUtil.ins().resume();

		if (shouldReconnect) {
			shouldReconnect = false;
			L.e(this, "onResume里面执行了connectCurDevice()");
			connectCurDevice("连接已断开, 正在重新连接...");
		}

	}

	@Override
	protected void onPause() {
		L.e(this, "onPause");
		super.onPause();

		DialogUtil.dismissDialog();

		isActived = false;

		CheckOnlineUtil.ins().pause();
		paused = true;

		// XPGConnectClient.RemoveActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		CheckOnlineUtil.ins().stop();
		DialogUtil.dismissDialog();
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				wifiConnectedReceiver);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				deviceOnlineReceiver);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				alterDeviceDueToDeleteReceiver);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				logoutReceiver);
	}

	@Override
	public void onBackPressed() {
		// if (Global.connectId > -1) {
		// XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
		// }
		//
		// if (Global.checkOnlineConnId > 0) {
		// XPGConnectClient.xpgcDisconnectAsync(Global.checkOnlineConnId);
		// }
		//
		// android.os.Process.killProcess(android.os.Process.myPid());
		dialog_exit.show();
	}

	@Override
	public void onDeviceFound(XpgEndpoint endpoint) {
		super.onDeviceFound(endpoint);
		CheckOnlineUtil.ins().receiveEndpoint(endpoint);
	}

	@Override
	public void OnDeviceOnlineStateResp(DeviceOnlineStateResp_t pResp,
			int nConnId) {
		super.OnDeviceOnlineStateResp(pResp, nConnId);
		L.e(this, "OnDeviceOnlineStateResp@BaseBusinessActivity:");

		// if current device went offline
		// offline
		// if current device went online
		// auto reconnect

		if (pResp.getIsOnline() == 0) {

			// offline ui
			changeToOfflineUI();

		}

//		if (pResp.getIsOnline() == 1) {
//
//			if (paused) {
//				shouldReconnect = true;
//			} else {
//				connectCurDevice("连接已断开, 正在重新连接...");
//			}
//		}

	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		L.e(this, "onConnectEvent@BaseBusinessActivity: connId : " + connId + " event : "
				+ event);

		if (connId == Global.connectId && event == -7) {

			if (AlterDeviceHelper.hostActivity != null) {
				AlterDeviceHelper.alterDevice();
				return;
			}

			// 连接断开
//			if (paused) {
//				shouldReconnect = true;
//			} else {
//				connectCurDevice("连接已断开, 正在重新连接...");
//			}

		}

	}

	protected void initSlidingMenu() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int mScreenWidth = dm.widthPixels;
		setBehindContentView(R.layout.main_left_fragment);
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		mSlidingMenu.setShadowWidth(mScreenWidth / 40);
		mSlidingMenu.setBehindOffset(mScreenWidth / 4);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		mSlidingMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);
		mSlidingMenu.setFadeEnabled(true);
		mSlidingMenu.setBehindScrollScale(0.333f);
	}

	protected void updateTitle(TextView title) {
		HeaterInfo heaterInfo = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeater();
		if (heaterInfo != null) {
			title.setText(Consts.getHeaterName(heaterInfo));
		}
	}

	public SlidingMenu mSlidingMenu;

	protected void connectCurDevice() {
		L.e(this, "connectCurDevice()@BaseBusinessActivity:");
		DialogUtil.dismissDialog();
		connectCurDevice("");
	}

	protected void connectCurDevice(String connectText) {
		L.e(this, "connectCurDevice(String)@BaseBusinessActivity:");
		DialogUtil.dismissDialog();
		String mac = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeaterMac();
		String userId = AccountService.getUserId(getBaseContext());
		String userPsw = AccountService.getUserPsw(getBaseContext());
		L.e(this, "从start进入的mac是 : " + mac);
		L.e(this, "从start进入的userId是 : " + userId);
		L.e(this, "从start进入的userPsw是 : " + userPsw);

		if (getIntent().getBooleanExtra(
				EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK, false)) {
			ConnectActivity.connectDirectly(this, "", userId, userPsw,
					connectText);
		} else {
			ConnectActivity.connectToDevice(this, mac, "", userId, userPsw,
					connectText);
		}
	}

	protected void connectDevice(String connectText, String mac) {
		L.e(this, "connectCurDevice(String, String)@BaseBusinessActivity:");

		// XPGConnectClient.initClient(this);

		String userId = AccountService.getUserId(getBaseContext());
		String userPsw = AccountService.getUserPsw(getBaseContext());

		ConnectActivity.connectToDevice(this, mac, "", userId, userPsw,
				connectText);
	}

	private void registerSuicideReceiver() {

		IntentFilter filter = new IntentFilter(
				Consts.INTENT_FILTER_KILL_MAIN_ACTIVITY);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			// changeToOfflineUI();
			// XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
		}

		return super.onKeyDown(keyCode, event);
	}
}
