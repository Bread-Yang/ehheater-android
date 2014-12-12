package com.vanward.ehheater.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.xtremeprog.xpgconnect.generated.DeviceOnlineStateResp_t;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;


/**
 * 电热, 燃热, 壁挂炉三个MainActivity共用业务:
 * 
 * 1: 自动重连处理
 * 2: 设备上下线相关逻辑
 * 3: 初始化slidingmenu
 * 4: setTitle
 * 5: suicideReceiver: 收到广播时finish自己
 * 
 * 100: 取当前设备-getCurHeater()
 * 101: 连接当前设备: connectCurDevice()
 * 
 * @author Administrator
 *
 */
public abstract class BaseBusinessActivity extends BaseSlidingFragmentActivity {
	
	abstract protected void changeToOfflineUI();

	private boolean shouldReconnect = false;
	private boolean paused = false;
	
	BroadcastReceiver deviceOnlineReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("emmm", "deviceOnlineReceiver:onReceive@BaseBusinessActivity:");
			if (isFinishing()) {
				return;
			}
			connectCurDevice();
		}
	};
	
	BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("emmm", "logoutReceiver@BaseBusinessActivity:");
			XPGConnectClient.RemoveActivity(BaseBusinessActivity.this);
		}
	};
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		shouldReconnect = false;
		paused = false;
		
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				deviceOnlineReceiver, new IntentFilter(CheckOnlineUtil.ACTION_DEVICE_ONLINE));
		
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				logoutReceiver, new IntentFilter(Consts.INTENT_ACTION_LOGOUT));
		
		registerSuicideReceiver();
		
		CheckOnlineUtil.ins().reset(getCurHeater().getMac());
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
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(deviceOnlineReceiver);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(logoutReceiver);
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
	public void OnDeviceOnlineStateResp(DeviceOnlineStateResp_t pResp, int nConnId) {
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
				connectCurDevice();
			}
		} 
		
	}
	

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		Log.d("emmm", "onConnectEvent@BaseBusinessActivity:" + connId + "-" + event);

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

		if (curHeater == null) {
			curHeater = getCurHeater();
		}
		
		String mac = curHeater.getMac();
		String userId = AccountService.getUserId(getBaseContext());
		String userPsw = AccountService.getUserPsw(getBaseContext());

		ConnectActivity.connectToDevice(this, mac, "", userId, userPsw, connectText);
	}
	
	private HeaterInfo getCurHeater() {
		if (curHeater == null) {
			curHeater = new HeaterInfoService(getBaseContext()).getCurrentSelectedHeater();
		}
		
		return curHeater;
	}
	private HeaterInfo curHeater;
	
	private void registerSuicideReceiver() {

		IntentFilter filter = new IntentFilter(Consts.INTENT_FILTER_KILL_MAIN_ACTIVITY);
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, filter);
	}
}
