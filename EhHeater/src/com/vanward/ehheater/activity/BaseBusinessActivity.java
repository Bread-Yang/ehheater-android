package com.vanward.ehheater.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.configure.EasyLinkConfigureActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.common.BaseSendCommandService;
import com.vanward.ehheater.activity.main.common.BaseSendCommandService.BeforeSendCommandCallBack;
import com.vanward.ehheater.activity.main.electric.ElectricHeaterSendCommandService;
import com.vanward.ehheater.activity.main.electric.ElectricMainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceMainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceSendCommandService;
import com.vanward.ehheater.activity.main.gas.GasHeaterSendCommandService;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.AlterDeviceHelper;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.CheckOnlineUtil;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.vanward.ehheater.util.wifi.ConnectChangeReceiver;
import com.vanward.ehheater.view.fragment.BaseSlidingFragmentActivity;
import com.vanward.ehheater.view.fragment.SlidingMenu;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.DERYStatusResp_t;
import com.xtremeprog.xpgconnect.generated.DeviceOnlineStateResp_t;
import com.xtremeprog.xpgconnect.generated.GasWaterHeaterStatusResp_t;
import com.xtremeprog.xpgconnect.generated.LanLoginResp_t;
import com.xtremeprog.xpgconnect.generated.PasscodeResp_t;
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
public abstract class BaseBusinessActivity extends BaseSlidingFragmentActivity
		implements BeforeSendCommandCallBack {

	abstract protected void changeToOfflineUI();

	abstract protected void queryState();

	private boolean shouldReconnect = false;
	private boolean paused = false;

	protected boolean isActived = false;

	private boolean firstSendStateReq = true;

	protected Dialog dialog_exit, dialog_reconnect;

	protected boolean isBinding;

	protected RelativeLayout rlt_loading;

	protected TextView tv_loading_tips;

	private HeaterInfoService heaterInfoService;

	private final static int smallCycleConnectTimeout = 10000; // 小循环连接超时时间
	private final static int bigCycleConnnectTimeout = 30000; // 大循环连接超时时间

	private boolean isConnectingBySmallCycle = false; // 当前是否小循环连接中

	private boolean isAlreadyTryConnectBySmallCycle = false; // XPGConnShortCuts.connect2small()已经被调用,则不再尝试大循环
	private boolean isAlreadyTryConnectByBigCycle = false; // XPGConnectClient.xpgcLogin2Wan()已经被调用,则服务器再返回设备,则不再调用
	private boolean isNeedToUploadBindAfterEasyLink = false; // 通过easylink配置后,要上传绑定关系到服务器

	private String connectDeviceMac;

	protected boolean isConnecting;
 
	private boolean isAlreadyReceiveDeviceStatus;

	private XpgEndpoint bigCycleConnectEndpoint;
	
	private final int SMALL_CYCLE_CONNECT_ALREADY_TIMEOUT = 1001;
	private final int BIG_CYCLE_CONNECT_ALREADY_TIMEOUT = 2001;

	private BroadcastReceiver wifiConnectedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			L.e(BaseBusinessActivity.this, "wifiConnectedReceiver的onReceive()");
			boolean isConnected = intent.getBooleanExtra("isConnected", false);
			if (isConnected) {
				// if (isActived) {
				if (!NetworkStatusUtil.isConnected(BaseBusinessActivity.this)) {
					L.e(this, "=================");
					dialog_reconnect.show();
				} else {
					connectToDevice();
				}
				// }
			} else {
				L.e(this, "@@@@@@@@@@@@@@@");
				changeToOfflineUI();
			}
		}
	};

	private BroadcastReceiver deviceOnlineReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			L.e(this, "deviceOnlineReceiver的onReceive()");
			if (isFinishing()) {
				return;
			}
			connectToDevice();
		}
	};

	private BroadcastReceiver alterDeviceDueToDeleteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			L.e(this, "alterDeviceDueToDeleteReceiver的onReceive()");
			if (isFinishing()) {
				return;
			}

			AlterDeviceHelper.hostActivity = BaseBusinessActivity.this;

			if (Global.connectId > -1) {
				// 触发BaseBusinessActivity里的断开连接回调, 具体的切换逻辑在该回调中处理
				L.e(this, "XPGConnectClient.xpgcDisconnectAsync()");
				XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
			} else {
				// 如果当前未建立连接, 直接调用此方法
				L.e(this,
						"alterDeviceDueToDeleteReceiver : AlterDeviceHelper.alterDevice();");
				AlterDeviceHelper.alterDevice();
			}
		}
	};

	private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			L.e(this, "logoutReceiver的onReceive()");
			XPGConnectClient.RemoveActivity(BaseBusinessActivity.this);
		}
	};

	public static int connectTime = 10000;

	private Handler reconnectHandler = new Handler() {

		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (isAlreadyReceiveDeviceStatus) { // 起码要接受到一次设备状态前提下,才能重连,避免刚刚配置上的时候,能连上设备,但收不到设备状态时代码死循环的问题
					L.e(this, "在reconnectHandler里面调用connectToDevice()重连");
					connectToDevice();
				} else {
					reconnectHandler.sendEmptyMessageDelayed(1, connectTime);
					reconnectHandler.removeMessages(0);
				}
				// if (firstSendStateReq) {
				// tv_loading_tips.setText(R.string.waiting_device_response);
				// rlt_loading.setVisibility(View.VISIBLE);
				// firstSendStateReq = false;
				//
				// if (BaseBusinessActivity.this instanceof MainActivity) {
				// L.e(BaseBusinessActivity.this, "查询电热状态");
				// generated.SendStateReq(Global.connectId);
				// } else if (BaseBusinessActivity.this instanceof
				// GasMainActivity) {
				// L.e(BaseBusinessActivity.this, "查询燃热状态");
				// generated
				// .SendGasWaterHeaterMobileRefreshReq(Global.connectId);
				// } else if (BaseBusinessActivity.this instanceof
				// FurnaceMainActivity) {
				// L.e(BaseBusinessActivity.this, "查询壁挂炉状态");
				// generated.SendDERYRefreshReq(Global.connectId);
				// }
				//
				// reconnectHandler.sendEmptyMessageDelayed(1, connectTime);
				// reconnectHandler.removeMessages(0);
				// }
				break;
			case 1:
				L.e(this, "@@@@@@@@@@@@@@@");
				changeToOfflineUI();
				L.e(this, "$$$$$$$");
				rlt_loading.setVisibility(View.GONE);
				if (!isFinishing()) {
					L.e(this, "=================");
					dialog_reconnect.show();
				}
				break;
			}
		};
	};

	@Override
	public void onSendPacket(byte[] data, int connId) {
		super.onSendPacket(data, connId);
		L.e(this, "onSendPacket调用了");
	};

	@Override
	public void beforeSendCommand() {
		L.e(this, "beforeSendCommand调用了");
		reconnectHandler.removeMessages(0);
		reconnectHandler.sendEmptyMessageDelayed(0, connectTime);
	}

	// @Override
	// public void onWriteEvent(int result, int connId) {
	// super.onWriteEvent(result, connId);
	// L.e(this, "onWriteEvent调用了");
	// reconnectHandler.removeMessages(0);
	// reconnectHandler.sendEmptyMessageDelayed(0, connectTime);
	// }

	@Override
	public void OnStateResp(StateResp_t pResp, int nConnId) {
		L.e(this, "OnStateResp被调用了");
		super.OnStateResp(pResp, nConnId);
	}

	@Override
	public void onTcpPacket(byte[] data, int connId) {
		L.e(this, "onTcpPacket被调用了");
		super.onTcpPacket(data, connId);

		if (!isConnecting) {
			if (this instanceof ElectricMainActivity) {
				isAlreadyReceiveDeviceStatus = true;

				firstSendStateReq = true;
				reconnectHandler.removeMessages(0);
				reconnectHandler.removeMessages(1);
				dialog_reconnect.dismiss();
				L.e(this, "$$$$$$$");
				rlt_loading.setVisibility(View.GONE);

				ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningTaskInfo> list = am.getRunningTasks(1);
				if (list != null && list.size() > 0) {
					ComponentName cpn = list.get(0).topActivity;
					// if (className.equals(cpn.getClassName())) {
					// }
				}
			}
		}
	}

	@Override
	public void OnGasWaterHeaterStatusResp(GasWaterHeaterStatusResp_t pResp,
			int nConnId) {
		L.e(this, "OnGasWaterHeaterStatusResp被调用了");
		super.OnGasWaterHeaterStatusResp(pResp, nConnId);

		if (!isConnecting) {
			if (this instanceof GasMainActivity) {
				isAlreadyReceiveDeviceStatus = true;

				firstSendStateReq = true;
				reconnectHandler.removeMessages(0);
				reconnectHandler.removeMessages(1);
				dialog_reconnect.dismiss();
				L.e(this, "$$$$$$$");
				rlt_loading.setVisibility(View.GONE);

				ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningTaskInfo> list = am.getRunningTasks(1);
				if (list != null && list.size() > 0) {
					ComponentName cpn = list.get(0).topActivity;
					// if (className.equals(cpn.getClassName())) {
					// }
				}
			}
		}
	}

	@Override
	public void OnDERYStatusResp(DERYStatusResp_t pResp, int nConnId) {
		L.e(this, "OnDERYStatusResp被调用了");
		super.OnDERYStatusResp(pResp, nConnId);

		if (!isConnecting) {
			if (this instanceof FurnaceMainActivity) {
				isAlreadyReceiveDeviceStatus = true;

				firstSendStateReq = true;
				reconnectHandler.removeMessages(0);
				reconnectHandler.removeMessages(1);
				dialog_reconnect.dismiss();
				rlt_loading.setVisibility(View.GONE);

				ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningTaskInfo> list = am.getRunningTasks(1);
				if (list != null && list.size() > 0) {
					ComponentName cpn = list.get(0).topActivity;
					// if (className.equals(cpn.getClassName())) {
					// }
				}
			}
		} else {
			return;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		L.e(this, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(getLayoutInflater().inflate(
				R.layout.activity_device_base, null));

		tv_loading_tips = (TextView) findViewById(R.id.tv_loading_tips);
		rlt_loading = (RelativeLayout) findViewById(R.id.rlt_loading);
		rlt_loading.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

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

		heaterInfoService = new HeaterInfoService(getBaseContext());

		String mac = heaterInfoService.getCurrentSelectedHeaterMac();
		// CheckOnlineUtil.ins().reset(mac);

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

		dialog_reconnect = new Dialog(this, R.style.custom_dialog);
		dialog_reconnect.setContentView(R.layout.dialog_reconnect);

		dialog_reconnect.findViewById(R.id.dr_btn_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog_reconnect.dismiss();
					}
				});

		dialog_reconnect.findViewById(R.id.dr_btn_reconnect)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						connectToDevice();
						dialog_reconnect.dismiss();
					}
				});
	}

	public void setSlidingView(int id) {
		RelativeLayout rlt_content = (RelativeLayout) findViewById(R.id.rlt_content);

		View content = getLayoutInflater().inflate(id, null);
		rlt_content.addView(content);
	}

	@Override
	protected void onResume() {
		L.e(this, "onResume");
		super.onResume();

		isActived = true;

		paused = false;

		CheckOnlineUtil.ins().resume();
	}

	@Override
	protected void onPause() {
		L.e(this, "onPause");
		super.onPause();

		isActived = false;

		CheckOnlineUtil.ins().pause();
		paused = true;

		// XPGConnectClient.RemoveActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		L.e(this, "onStop()");
		// CheckOnlineUtil.ins().stop();
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		L.e(this, "onDestroy()");
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				wifiConnectedReceiver);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				deviceOnlineReceiver);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				alterDeviceDueToDeleteReceiver);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				logoutReceiver);

//		L.e(this, "XPGConnectClient.xpgcDisconnectAsync()");
		// XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
		timeoutHandler.removeMessages(0);
		reconnectHandler.removeMessages(0);
		reconnectHandler.removeMessages(1);
	}

	@Override
	public void onBackPressed() {
		dialog_exit.show();
	}

	@Override
	public void OnDeviceOnlineStateResp(DeviceOnlineStateResp_t pResp,
			int nConnId) {
		super.OnDeviceOnlineStateResp(pResp, nConnId);
		L.e(this, "OnDeviceOnlineStateResp()");

		// if current device went offline
		// offline
		// if current device went online
		// auto reconnect

		if (pResp.getIsOnline() == 0) {
			L.e(this,
					"大循环下设备断线,OnDeviceOnlineStateResp()返回pResp.getIsOnline() == 0");
			// offline ui
			if (rlt_loading.getVisibility() != View.VISIBLE) {
				L.e(this, "@@@@@@@@@@@@@@@");
				changeToOfflineUI();

				// 收到主动断开,重连一次
				connectToDevice();
			}
		} else {
			L.e(this,
					"大循环下设备上线,OnDeviceOnlineStateResp()返回pResp.getIsOnline() == 1");
		}

		// if (pResp.getIsOnline() == 1) {
		//
		// if (paused) {
		// shouldReconnect = true;
		// } else {
		// connectCurDevice("连接已断开, 正在重新连接...");
		// }
		// }

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
		HeaterInfo heaterInfo = heaterInfoService.getCurrentSelectedHeater();
		if (heaterInfo != null) {
			title.setText(Consts.getHeaterName(heaterInfo));
		}
	}

	public SlidingMenu mSlidingMenu;

	// protected void connectCurDevice() {
	// L.e(this, "connectCurDevice()@BaseBusinessActivity:");
	// connectCurDevice("");
	// }
	//
	// protected void connectCurDevice(String connectText) {
	// L.e(this, "connectCurDevice(String)@BaseBusinessActivity:");
	// if (!NetworkStatusUtil.isConnected(getApplicationContext())) {
	// return;
	// }
	// String mac = heaterInfoService.getCurrentSelectedHeaterMac();
	// String userId = AccountService.getUserId(getBaseContext());
	// String userPsw = AccountService.getUserPsw(getBaseContext());
	// L.e(this, "从start进入的mac是 : " + mac);
	// L.e(this, "从start进入的userId是 : " + userId);
	// L.e(this, "从start进入的userPsw是 : " + userPsw);
	//
	// if (getIntent().getBooleanExtra(
	// EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK, false)) {
	// ConnectActivity.connectDirectly(this, "", userId, userPsw,
	// connectText);
	// } else {
	// ConnectActivity.connectToDevice(this, mac, "", userId, userPsw,
	// connectText);
	// }
	// }

	// protected void connectDevice(String connectText, String mac) {
	// L.e(this, "connectCurDevice(String, String)@BaseBusinessActivity:");
	//
	// XPGConnectClient.initClient(this);
	//
	// String userId = AccountService.getUserId(getBaseContext());
	// String userPsw = AccountService.getUserPsw(getBaseContext());
	//
	// ConnectActivity.connectToDevice(this, mac, "", userId, userPsw,
	// connectText);
	// }

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

	private void showOffline() {
		L.e(this, "$$$$$$$");
		rlt_loading.setVisibility(View.GONE);
		L.e(this, "=================");

		if (!isFinishing()) {
			dialog_reconnect.show();
		}
	}

	private Handler timeoutHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			L.e(this, "!!!!!!!!!");
			isConnecting = false;
			showOffline();
			if (msg.what == 1) {
				L.e(this, "连接大循环30秒后超时,弹出重连对话框");
			}
			return false;
		}
	});

	public void connectToDevice() {

		if (rlt_loading.getVisibility() != View.VISIBLE) {

			L.e(this, "connectToDevice()");

			if (!NetworkStatusUtil.isConnected(this)) {
				Toast.makeText(this, R.string.check_network, Toast.LENGTH_SHORT)
						.show();
				return;
			}

			dialog_reconnect.dismiss();

			// 断开之前的连接
			L.e(this, "XPGConnectClient.xpgcDisconnectAsync()");
			XPGConnectClient.xpgcDisconnectAsync(Global.connectId);

			HeaterInfo connectingDevice = heaterInfoService
					.getCurrentSelectedHeater();
			if (connectingDevice == null) {
				return;
			}

			connectDeviceMac = connectingDevice.getMac().toLowerCase();

			if (NetworkStatusUtil.isConnectedByWifi(getBaseContext())) {

				tv_loading_tips.setText(R.string.connecting);
				rlt_loading.setVisibility(View.VISIBLE);

				isConnecting = true;

				isAlreadyReceiveDeviceStatus = false;

				if (getIntent()
						.getBooleanExtra(
								EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK,
								false)) { // easylink后直接通过ip地址连接设备
					isNeedToUploadBindAfterEasyLink = true;
					getIntent()
							.putExtra(
									EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK,
									false);
					connectDirectlyAfterEasyLink();
				} else { // 先试小循环, 不行则大循环
					isConnectingBySmallCycle = false;
					isAlreadyTryConnectBySmallCycle = false;
					isAlreadyTryConnectByBigCycle = false;
					bigCycleConnectEndpoint = null;
					tryConnectBySmallCycle(smallCycleConnectTimeout);
				}
			} else if (NetworkStatusUtil
					.isConnectedByMobileData(getBaseContext())) {
				// 只能大循环
				tryConnectByBigCycle();
			}
		}
	}

	private void connectDirectlyAfterEasyLink() {
		L.e(this, "connectDirectlyAfterEasyLink()");

		String ip = new SharedPreferUtils(this).get(ShareKey.CurDeviceAddress,
				"");

		timeoutHandler.sendEmptyMessageDelayed(0, 10000);

		XPGConnShortCuts.connect2small(ip);
	}

	// 以下是小循环连接流程

	private void tryConnectBySmallCycle(int timeout) {
		L.e(this, "tryConnectBySmallCycle()");

		L.e(this, "==============开始小循环连接==============");

		isConnectingBySmallCycle = true;

		L.e(this, "XPGConnectClient.xpgcStartDiscovery()");
		XPGConnectClient.xpgcStartDiscovery();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {

				if (!isAlreadyTryConnectBySmallCycle) {

					isConnectingBySmallCycle = false;

					L.e(this, "XPGConnectClient.xpgcStopDiscovery()");
					XPGConnectClient.xpgcStopDiscovery();

					tryConnectByBigCycle();
				}
			}
		}, timeout);
	}

	@Override
	public void onEasyLinkResp(XpgEndpoint endpoint) {
		super.onEasyLinkResp(endpoint);

		synchronized (this) {

			if (rlt_loading.getVisibility() == View.VISIBLE
					&& isConnectingBySmallCycle) {

				if (null == endpoint) {
					L.e(this, "endpoint返回为null");
					return;
				}

				L.e(this, "==============start onEasyLinkResp()==============");

				L.e(this, "小循环返回的设备信息, mac : " + endpoint.getSzMac()
						+ ", did : " + endpoint.getSzDid());

				if (endpoint.getSzMac() == null || endpoint.getSzDid() == null) {
					return;
				}

				if (!connectDeviceMac.equals(endpoint.getSzMac().toLowerCase())) {
					L.e(this,
							"小循环返回的设备Mac与要连接的设备不匹配, 要连接的mac :"
									+ connectDeviceMac + ", 小循环返回的mac : "
									+ endpoint.getSzMac());
					return;
				}

				String macFound = endpoint.getSzMac().toLowerCase();
				String didFound = endpoint.getSzDid();

				L.e(this, "connnect device Mac : " + connectDeviceMac);
				L.e(this, "connnect device did : "
						+ heaterInfoService.getCurrentSelectedHeater().getDid());
				L.e(this, "endpoint.getSzPasscode()" + endpoint.getSzPasscode());
				L.e(this, "endpoint.getSzMac() : "
						+ endpoint.getSzMac().toLowerCase());
				L.e(this, "didFound : " + didFound);
				L.e(this, "endpoint.getAddr() : " + endpoint.getAddr());

				if (!isAlreadyTryConnectBySmallCycle) {
					if (!TextUtils.isEmpty(macFound)
							&& macFound.equals(connectDeviceMac)) {

						isConnectingBySmallCycle = false;
						isAlreadyTryConnectBySmallCycle = true;

						L.e(this, "XPGConnectClient.xpgcStopDiscovery()");
						XPGConnectClient.xpgcStopDiscovery();

						timeoutHandler.sendEmptyMessageDelayed(0, 5000);

						// HeaterInfo device = heaterInfoService
						// .getCurrentSelectedHeater();
						// device.setDid(endpoint.getSzDid());
						// new
						// HeaterInfoDao(getApplicationContext()).save(device);

						L.e(this,
								"========小循环返回对的设备,通过小循环连接 : XPGConnShortCuts.connect2small()=========");
						XPGConnShortCuts.connect2small(endpoint.getAddr());
					}
				}
				L.e(this, "==============end onEasyLinkResp()==============");
			}
		}
	}

	@Override
	public void onDeviceFound(XpgEndpoint endpoint) {
		super.onDeviceFound(endpoint);
		// CheckOnlineUtil.ins().receiveEndpoint(endpoint);

		synchronized (this) {

			if (isConnectingBySmallCycle) {

				if (null == endpoint) {
					L.e(this, "endpoint返回为null");
					return;
				}
				
				if (isAlreadyTryConnectBySmallCycle) {
					return;
				}

				L.e(this, "==============start onDeviceFound()==============");

				L.e(this, "小循环返回的设备信息, mac : " + endpoint.getSzMac()
						+ ", did : " + endpoint.getSzDid());

				if (endpoint.getSzMac() == null || endpoint.getSzDid() == null) {
					return;
				}

				if (!connectDeviceMac.equals(endpoint.getSzMac().toLowerCase())) {
					L.e(this,
							"小循环返回的设备Mac与要连接的设备不匹配, 要连接的mac :"
									+ connectDeviceMac + ", 小循环返回的mac : "
									+ endpoint.getSzMac());
					return;
				}

				String macFound = endpoint.getSzMac().toLowerCase();
				String didFound = endpoint.getSzDid();

				L.e(this, "connnect device Mac : " + connectDeviceMac);
				L.e(this, "connnect device did : "
						+ heaterInfoService.getCurrentSelectedHeater().getDid());
				L.e(this, "endpoint.getSzPasscode()" + endpoint.getSzPasscode());
				L.e(this, "endpoint.getSzMac() : "
						+ endpoint.getSzMac().toLowerCase());
				L.e(this, "didFound : " + didFound);
				L.e(this, "endpoint.getAddr() : " + endpoint.getAddr());

				if (!isAlreadyTryConnectBySmallCycle) {
					if (!TextUtils.isEmpty(macFound)
							&& macFound.equals(connectDeviceMac)) {
						
						isAlreadyTryConnectBySmallCycle = true;

						L.e(this, "XPGConnectClient.xpgcStopDiscovery()");
						XPGConnectClient.xpgcStopDiscovery();
						
						timeoutHandler.sendEmptyMessageDelayed(0, 5000);

						// HeaterInfo device = heaterInfoService
						// .getCurrentSelectedHeater();
						// device.setDid(endpoint.getSzDid());
						// new
						// HeaterInfoDao(getApplicationContext()).save(device);

						L.e(this,
								"========小循环返回对的设备,通过小循环连接 : XPGConnShortCuts.connect2small()=========");
						XPGConnShortCuts.connect2small(endpoint.getAddr());
					}
				}
				L.e(this, "==============end onDeviceFound()==============");
			}
		}
	}

	@Override
	public void onConnectEvent(int connId, int event) { // connect2small()之后回调
		super.onConnectEvent(connId, event);
		L.e(this, "onConnectEvent@BaseBusinessActivity: connId : " + connId
				+ " event : " + event);

		if (connId == Global.connectId && event == -7) {
//			L.e(this, "小循环下设备断线,因为onConnectEvent()主动返回event == -7");
			if (AlterDeviceHelper.hostActivity != null) {
				L.e(this, "onConnectEvent() : AlterDeviceHelper.alterDevice();");
//				AlterDeviceHelper.alterDevice();
				return;
			}
		} else if (event == 0) { // 连接设备,connect2small()之后回调
			Global.connectId = connId;

			String devicePasscode = heaterInfoService
					.getCurrentSelectedHeater().getPasscode();

			// 之前有没有保存passcode都重新请求一次passcode
			L.e(this, "generated.SendPasscodeReq(connId) 请求设备密码");
			generated.SendPasscodeReq(connId);

			// if (TextUtils.isEmpty(devicePasscode)) {
			// L.e(this, "SendPasscodeReq()");
			// generated.SendPasscodeReq(connId);
			// } else {
			// L.e(this, "XPGConnectClient.xpgcLogin()");
			// XPGConnectClient.xpgcLogin(connId, null, devicePasscode);
			// }
		}
	}

	@Override
	public void OnPasscodeResp(PasscodeResp_t pResp, int nConnId) { // generated.SendPasscodeReq()之后回调
		super.OnPasscodeResp(pResp, nConnId);
		L.e(this, "OnPasscodeResp()返回的nConnId : " + nConnId);

		Global.connectId = nConnId;

		// 请求到的passcode
		String retrievedPasscode = generated
				.XpgData2String(pResp.getPasscode());

		if (TextUtils.isEmpty(retrievedPasscode)) {
			L.e(this, "请求回到的passcode为空");
		} else {
			L.e(this, "请求返回的passcode是 : " + retrievedPasscode);
		}

		HeaterInfo device = heaterInfoService.getCurrentSelectedHeater();
		device.setPasscode(retrievedPasscode);
		new HeaterInfoDao(getApplicationContext()).save(device);

		new HeaterInfoDao(getApplicationContext()).save(device);

		L.e(this, "XPGConnectClient.xpgcLogin()");
		XPGConnectClient.xpgcLogin(nConnId, null, retrievedPasscode);
	}

	@Override
	public void OnLanLoginResp(LanLoginResp_t pResp, int nConnId) { // XPGConnectClient.xpgcLogin()之后回调
		super.OnLanLoginResp(pResp, nConnId);

		L.e(this, "OnLanLoginResp()返回的nConnId : " + nConnId
				+ " pResp.getResult() : " + pResp.getResult());

		Global.connectId = nConnId;

		isConnecting = false;

		if (pResp.getResult() == 0) {
			timeoutHandler.removeMessages(0);
			L.e(this, "小循环连接设备成功,发送查询指令");
			queryState();

			// 是否需要上传绑定关系到服务器
			if (isNeedToUploadBindAfterEasyLink) {
				uploadDeviceToServer();
			}
		}
	}

	// 以下是大循环连接流程

	private void tryConnectByBigCycle() {
		L.e(this, "tryConnectByBigCycle()");

		L.e(this, "==============结束小循环连接,小循环网络没有找到要连接的设备==============");
		L.e(this, "==============开始大循环连接==============");

		if ("".equals(Global.token) || "".equals(Global.uid)) {
			L.e(this, "XPGConnectClient.xpgc4Login");
			XPGConnectClient.xpgc4Login(Consts.VANWARD_APP_ID,
					AccountService.getUserId(getBaseContext()),
					AccountService.getUserPsw(getBaseContext()));
		} else {
			L.e(this, "XPGConnectClient.xpgc4GetMyBindings");
			XPGConnectClient.xpgc4GetMyBindings(Consts.VANWARD_APP_ID,
					Global.token, 20, 0);
		}

		timeoutHandler.sendEmptyMessageDelayed(0, bigCycleConnnectTimeout);
	}

	@Override
	public void onV4Login(int errorCode, String uid, String token,
			String expire_at) {
		super.onV4Login(errorCode, uid, token, expire_at);
		L.e(this, "onV4Login() errorCode : " + errorCode);

		if (errorCode == 0) {
			Global.uid = uid;
			Global.token = token;

			isAlreadyTryConnectByBigCycle = false;

			if (isNeedToUploadBindAfterEasyLink) {
				HeaterInfo device = heaterInfoService
						.getCurrentSelectedHeater();
				XPGConnectClient
						.xpgc4BindDevice(Consts.VANWARD_APP_ID, Global.token,
								device.getDid(), device.getPasscode(), "");
			} else {
				XPGConnectClient.xpgc4GetMyBindings(Consts.VANWARD_APP_ID,
						Global.token, 20, 0);
			}
		}
	}

	@Override
	public void onV4GetMyBindings(int errorCode, final XpgEndpoint endpoint) {
		super.onV4GetMyBindings(errorCode, endpoint);

		synchronized (this) {

			L.e(this, "==============start onV4GetMyBindings()==============");

			L.e(this,
					"服务器返回设备的信息, mac : " + endpoint.getSzMac() + "- did : "
							+ endpoint.getSzDid() + "- isOnline : "
							+ (endpoint.getIsOnline() == 1));

			if ("".equals(endpoint.getSzMac())) { // 假如mac地址为空,说明onV4GetMyBindings()最后一次回调
				if (bigCycleConnectEndpoint != null
						&& !isAlreadyTryConnectByBigCycle) { // 假如有设备,则执行大循环连接
					isAlreadyTryConnectByBigCycle = true;

					L.e(this,
							"========服务器上有该设备,执行大循环连接 : XPGConnectClient.xpgcLogin2Wan()=========");

					connectAfterGetBindingDevicesReceivedFromMQTT(bigCycleConnectEndpoint);

					// L.e(this,
					// "========大循环返回对的设备,8秒后执行大循环连接 : XPGConnectClient.xpgcLogin2Wan()=========");
					// new Handler().postDelayed(new Runnable() {
					// @Override
					// public void run() {
					// // 接收绑定的设备列表完毕
					// connectAfterGetBindingDevicesReceivedFromMQTT(endpoint);
					// }
					// }, 8000);
				} else {
					L.e(this,
							"========服务器上没有该设备,不执行大循环连接,弹出重连对话框bigCycleConnectEndpoint");
					timeoutHandler.removeMessages(0);
					timeoutHandler.sendEmptyMessage(0);
				}
			} else {
				if (!connectDeviceMac.equals(endpoint.getSzMac().toLowerCase())) {
					L.e(this,
							"服务器返回的设备Mac与要连接的设备不匹配, 要连接的mac :"
									+ connectDeviceMac + ", 服务器返回的mac : "
									+ endpoint.getSzMac());
					return;
				} else {
					L.e(this, "!!!服务器返回要连接的设备信息!!!");
					bigCycleConnectEndpoint = endpoint;
				}
			}
			L.e(this, "==============end onV4GetMyBindings()==============");
		}
	}

	private void connectAfterGetBindingDevicesReceivedFromMQTT(
			XpgEndpoint endpoint) {

		if (endpoint.getSzMac().toLowerCase().equals(connectDeviceMac)) {

			HeaterInfo device = heaterInfoService.getCurrentSelectedHeater();
			device.setPasscode(endpoint.getSzPasscode());
			device.setDid(endpoint.getSzDid());

			new HeaterInfoDao(getApplicationContext()).save(device);

			L.e(this,
					connectDeviceMac + " : isOnline : "
							+ (endpoint.getIsOnline() == 1));

			if (endpoint.getIsOnline() == 1) {
				L.e(this, "AccountService.getUserId(getBaseContext() : "
						+ AccountService.getUserId(getBaseContext()));
				L.e(this, "AccountService.getUserPsw(getBaseContext() : "
						+ AccountService.getUserPsw(getBaseContext()));
				L.e(this, "ep.getSzDid() : " + endpoint.getSzDid());
				L.e(this, "ep.getSzPasscode() : " + endpoint.getSzPasscode());

				String userName = "2$" + Consts.VANWARD_APP_ID + "$"
						+ Global.uid;
				L.e(this, "XPGConnectClient.xpgcLogin2Wan()来大循环连接设备");
				XPGConnectClient.xpgcLogin2Wan(userName, Global.token,
						endpoint.getSzDid(), endpoint.getSzPasscode()); // 连接设备
				return;
			} else { // offline
				L.e(this, "通过大循环连接的设备不在线,无法连接");
				timeoutHandler.removeMessages(0);
				showOffline();
				return;
			}
		}
		bigCycleConnectEndpoint = null;
	}

	@Override
	public void onWanLoginResp(int result, int connId) { // 调用XPGConnectClient.xpgcLogin2Wan()连接设备后回调
		super.onWanLoginResp(result, connId);

		L.e(this, "onWanLoginResp() : result : " + result + " connId : "
				+ connId);

		Global.connectId = connId;

		isConnecting = false;

		switch (result) {
		case 0: // 可以控制
			L.e(this, "大循环连接设备成功,发送查询指令");
			queryState();
			timeoutHandler.removeMessages(0);
			break;
		case 1:
			break;
		case 5:
			// 账号密码错误
			break;
		}
	}

	private void uploadDeviceToServer() {
		L.e(this, "uploadDeviceToServer()");

		HeaterInfo device = heaterInfoService.getCurrentSelectedHeater();

		if ("".equals(Global.token) || "".equals(Global.uid)) {
			XPGConnectClient.xpgc4Login(Consts.VANWARD_APP_ID,
					AccountService.getUserId(getBaseContext()),
					AccountService.getUserPsw(getBaseContext()));
		} else {
			L.e(this, "上传到服务器的设备是 : " + device);
			XPGConnectClient.xpgc4BindDevice(Consts.VANWARD_APP_ID,
					Global.token, device.getDid(), device.getPasscode(), "");
		}
	}

	@Override
	public void onV4BindDevce(int errorCode, String successString,
			String failString) {
		L.e(this, "onV4BindDevce() : errorCode : " + errorCode
				+ ", successString : " + successString);

		super.onV4BindDevce(errorCode, successString, failString);

		if (errorCode == 0
				&& successString.equals(heaterInfoService
						.getCurrentSelectedHeater().getDid())) {

		} else {
			Toast.makeText(this, R.string.upload_bind_fail, Toast.LENGTH_SHORT)
					.show();
		}

		isNeedToUploadBindAfterEasyLink = false;
	}

}
