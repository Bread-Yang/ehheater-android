package com.vanward.ehheater.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.configure.ConnectActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.AlterDeviceHelper;
import com.vanward.ehheater.util.CheckOnlineUtil;
import com.vanward.ehheater.util.DialogUtil;
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

	BroadcastReceiver deviceOnlineReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("emmm",
					"deviceOnlineReceiver:onReceive@BaseBusinessActivity:");
			if (isFinishing()) {
				return;
			}
			connectCurDevice();
		}
	};

	BroadcastReceiver alterDeviceDueToDeleteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("emmm",
					"alterDeviceDueToDeleteReceiver@BaseBusinessActivity:");
			if (isFinishing()) {
				return;
			}

			AlterDeviceHelper.hostActivity = BaseBusinessActivity.this;

			if (Global.connectId > 0) {
				// 触发BaseBusinessActivity里的断开连接回调, 具体的切换逻辑在该回调中处理
				XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
			} else {
				// 如果当前未建立连接, 直接调用此方法
				AlterDeviceHelper.alterDevice();
			}
		}
	};

	BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("emmm", "logoutReceiver@BaseBusinessActivity:");
			XPGConnectClient.RemoveActivity(BaseBusinessActivity.this);
		}
	};

	private boolean stateQueried;

	public static long connectTime = 10000;

	@Override
	public void onWriteEvent(int result, int connId) {
		super.onWriteEvent(result, connId);
		// Log.e("TAG", "onWriteEvent调用了");
		// DialogUtil.instance().showLoadingDialog(this, "");
//		if (DialogUtil.instance().getIsShowing()) {
//			return;
//		}
//		generated.SendStateReq(Global.connectId);
//		stateQueried = false;
//		Handler handler = new Handler(Looper.getMainLooper());
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				if (!stateQueried) {
//					changeToOfflineUI();
//					DialogUtil.instance().showReconnectDialog(
//							BaseBusinessActivity.this);
//				}
//			}
//		}, connectTime);
	}

	@Override
	public void OnStateResp(StateResp_t pResp, int nConnId) {
		stateQueried = true;
		Log.e("TAG", "OnStateResp被调用了");
		super.OnStateResp(pResp, nConnId);
	}

	@Override
	public void onTcpPacket(byte[] data, int connId) {
		stateQueried = true;
		super.onTcpPacket(data, connId);
		Log.e("TAG", "onTcpPacket被调用了");
	}

	@Override
	public void OnGasWaterHeaterStatusResp(GasWaterHeaterStatusResp_t pResp,
			int nConnId) {
		stateQueried = true;
		super.OnGasWaterHeaterStatusResp(pResp, nConnId);
		Log.e("TAG", "OnGasWaterHeaterStatusResp被调用了");
	}

	@Override
	public void OnDERYStatusResp(DERYStatusResp_t pResp, int nConnId) {
		stateQueried = true;
		super.OnDERYStatusResp(pResp, nConnId);
		Log.e("TAG", "OnDERYStatusResp被调用了");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		shouldReconnect = false;
		paused = false;

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
	}

	@Override
	protected void onResume() {
		super.onResume();

		paused = false;

		CheckOnlineUtil.ins().resume();

		if (shouldReconnect) {
			shouldReconnect = false;
			connectCurDevice("连接已断开, 正在重新连接...");
		}

	}

	@Override
	protected void onPause() {
		super.onPause();

		CheckOnlineUtil.ins().pause();
		paused = true;
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
				deviceOnlineReceiver);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				alterDeviceDueToDeleteReceiver);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				logoutReceiver);
	}

	@Override
	public void onBackPressed() {
		if (Global.connectId > 0) {
			XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
		}

		if (Global.checkOnlineConnId > 0) {
			XPGConnectClient.xpgcDisconnectAsync(Global.checkOnlineConnId);
		}

		android.os.Process.killProcess(android.os.Process.myPid());
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
		Log.d("emmm", "OnDeviceOnlineStateResp@BaseBusinessActivity:");

		// if current device went offline
		// offline
		// if current device went online
		// auto reconnect

		if (pResp.getIsOnline() == 0) {

			// offline ui
			changeToOfflineUI();

		}

		if (pResp.getIsOnline() == 1) {

			if (paused) {
				shouldReconnect = true;
			} else {
				// connectCurDevice();
				connectCurDevice("连接已断开, 正在重新连接...");
			}
		}

	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		Log.d("emmm", "onConnectEvent@BaseBusinessActivity:" + connId + "-"
				+ event);

		if (connId == Global.connectId && event == -7) {

			if (AlterDeviceHelper.hostActivity != null) {
				AlterDeviceHelper.alterDevice();
				return;
			}

			// 连接断开
			if (paused) {
				shouldReconnect = true;
			} else {
				connectCurDevice("连接已断开, 正在重新连接...");
			}

		}

	}

	protected void initSlidingMenu() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int mScreenWidth = dm.widthPixels;
		setBehindContentView(R.layout.main_left_fragment);
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setShadowWidth(mScreenWidth / 40);
		mSlidingMenu.setBehindOffset(mScreenWidth / 4);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
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

	protected SlidingMenu mSlidingMenu;

	protected void connectCurDevice() {
		connectCurDevice("");
	}
	
	protected void connectCurDevice(String connectText) {

		String mac = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeaterMac();
		String userId = AccountService.getUserId(getBaseContext());
		String userPsw = AccountService.getUserPsw(getBaseContext());

		ConnectActivity.connectToDevice(this, mac, "", userId, userPsw,
				connectText);
	}
	
	protected void connectDevice(String connectText, String mac) {

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
