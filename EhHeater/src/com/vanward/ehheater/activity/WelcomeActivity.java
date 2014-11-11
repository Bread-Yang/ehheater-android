package com.vanward.ehheater.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.configure.DummySendBindingReqActivity;
import com.vanward.ehheater.activity.configure.ShitActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.info.SelectDeviceActivity;
import com.vanward.ehheater.activity.login.LoginActivity;
import com.vanward.ehheater.activity.main.MainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceMainActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;

public class WelcomeActivity extends GeneratedActivity {

	private TextView mTvInfo;

//	/** 建立的连接类型, LAN(小循环) / MQTT(大) */
//	private int connType = Integer.MAX_VALUE;
//
//	/** 小循环扫描设备周期,ms */
//	private int defaultScanInterval = 1000;
//
//	private final static int LAN_NONE = 0;
//	private final static int LAN_SEARCHING = 1;
//	private final static int LAN_FOUND = 2;
//
//	/** 当前小循环搜索设备的状态, 同一时间点只可能有一个小循环搜索任务在执行 */
//	private int currentLanSearchingState = LAN_NONE;

	@Override
	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		boolean isReEnter = getIntent().getBooleanExtra(
				Consts.INTENT_EXTRA_FLAG_REENTER, false);
		if (!isReEnter) {
			XPGConnectClient.initClient(this);
		}
		
		boolean asDialog = getIntent().getBooleanExtra(Consts.INTENT_EXTRA_FLAG_AS_DIALOG, false);
		if (asDialog) {
			setContentView(R.layout.activity_welcome_as_dialog);
			mTvInfo = (TextView) findViewById(R.id.awad_tv);
		} else {
			setTheme(R.style.AppBaseTheme);
			setContentView(R.layout.activity_welcome);
			mTvInfo = (TextView) findViewById(R.id.aw_tv);
		}
		


		if (!NetworkStatusUtil.isConnected(getBaseContext())) {
			// 无任何网络连接
			mTvInfo.setText("无网络连接");
			return;
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				checkLoginAndCurrentDeviceStatus(getBaseContext(), flowHandler);
			}
		}, 1000);
		
		MobclickAgent.updateOnlineConfig(this);
	}

//	private void tryConnectByBigCycle() {
//
//		XPGConnShortCuts.connect2big(getCurrentDevice().getMac(),
//				getCurrentDevice().getDid(), getCurrentDevice().getPasscode());
//
//		connType = XPG_WAN_LAN.MQTT.swigValue();
//
//		runOnUiThread(new Runnable() {
//			public void run() {
//				mTvInfo.setText("通过机智云连接中...(大循环)");
//			};
//		});
//	}

//	/**
//	 * 小循环唯一入口
//	 * 
//	 * @param scanInterval
//	 * @param timeOut
//	 * @param t
//	 *            超时未找到, 则执行t
//	 */
//	private void tryConnectBySmallCycle(final int scanInterval, int timeOut,
//			final TimerTask t) {
//
//		final Timer startFind = new Timer();
//		startFind.scheduleAtFixedRate(new TimerTask() {
//			public void run() {
//				XPGConnectClient.xpgcFindDevice();
//			}
//		}, 0, scanInterval);
//
//		new Timer().schedule(new TimerTask() {
//			@Override
//			public void run() {
//				if (startFind != null) {
//					startFind.cancel();
//				}
//				if (currentLanSearchingState == LAN_SEARCHING && t != null) {
//					new Timer().schedule(t, (long) (scanInterval * 1.2));
//					currentLanSearchingState = LAN_NONE;
//				}
//			}
//		}, timeOut);
//
//	}

//	@Override
//	public void onDeviceFound(XpgEndpoint endpoint) {
//		super.onDeviceFound(endpoint);
//
//		if (currentLanSearchingState == LAN_FOUND) {
//			return;
//		}
//
//		String macFound = endpoint.getSzMac().toLowerCase();
//		Log.d("emmm",
//				"onDeviceFound:found : " + macFound + "-" + endpoint.getSzDid()
//						+ "-" + endpoint.getSzPasscode());
//
//		if (!TextUtils.isEmpty(macFound)
//				&& macFound.equals(getCurrentDevice().getMac().toLowerCase())) {
//
//			if (TextUtils.isEmpty(getCurrentDevice().getDid())) {
//				Log.d("emmm", "onDeviceFound: saving did" + endpoint.getSzDid());
//				new HeaterInfoService(getBaseContext()).updateDid(
//						getCurrentDevice().getMac(), endpoint.getSzDid());
//			}
//			XPGConnShortCuts.connect2small(endpoint.getAddr());
//			currentLanSearchingState = LAN_FOUND;
//			connType = XPG_WAN_LAN.LAN.swigValue();
//		}
//
//	}

//	@Override
//	public void onConnectEvent(int connId, int event) {
//		super.onConnectEvent(connId, event);
//		Global.connectId = connId;
//		Log.d("emmm", "onConnectEvent@WelcomeActivity" + connId + "-" + event);
//
//		if (connType == XPG_WAN_LAN.LAN.swigValue()) {
//			String passcode = getCurrentDevice().getPasscode();
//			if (!TextUtils.isEmpty(passcode)) {
//				XPGConnectClient.xpgcLogin(Global.connectId, null, passcode);
//				Log.d("emmm", "onConnectEvent:connecting by small");
//			} else {
//				generated.SendPasscodeReq(Global.connectId);
//				Log.d("emmm", "onConnectEvent:requesting passcode");
//			}
//		}
//
//		if (connType == XPG_WAN_LAN.MQTT.swigValue()) {
//			XPGConnectClient.xpgcLogin(Global.connectId,
//					AccountService.getUserId(getBaseContext()),
//					AccountService.getUserPsw(getBaseContext()));
//			Log.d("emmm", "onConnectEvent:connecting by big");
//		}
//	}
//
//	@Override
//	public void OnPasscodeResp(PasscodeResp_t pResp, int nConnId) {
//		super.OnPasscodeResp(pResp, nConnId);
//
//		String passcode = generated.XpgData2String(pResp.getPasscode());
//
//		Log.d("emmm", "OnPasscodeResp: saving passcode-" + passcode);
//		new HeaterInfoService(getBaseContext()).updatePasscode(
//				getCurrentDevice().getMac(), passcode);
//
//		Log.d("emmm", "OnPasscodeResp: connecting by small");
//		XPGConnectClient.xpgcLogin(Global.connectId, null, passcode);
//	}
//
//	@Override
//	public void OnLanLoginResp(LanLoginResp_t pResp, int nConnId) {
//		super.OnLanLoginResp(pResp, nConnId);
//		Log.d("emmm", "OnLanLoginResp@welcome: " + pResp.getResult());
//
//		if (pResp.getResult() == 0) {
//			mTvInfo.setText("设备已连接! 查询设备状态中..");
//			generated.SendStateReq(Global.connectId);
//		}
//	}
//
//	@Override
//	public void onLoginCloudResp(int result, String mac) {
//		super.onLoginCloudResp(result, mac);
//		switch (result) {
//		case 0: // 可以控制
//			mTvInfo.setText("设备已连接! 查询设备状态中..");
//			generated.SendStateReq(Global.connectId);
//			break;
//		case 1: // 不能控制, 需enableCtrl()
//			break;
//		}
//	}
//	int reqCount;
//
//	@Override
//	public void onTcpPacket(byte[] data, int connId) {
//		super.onTcpPacket(data, connId);
//		if (TcpPacketCheckUtil.isEhStateData(data)) {
//
//			mTvInfo.setText("状态查询成功!");
//			XPGConnectClient.RemoveActivity(this);
//			
//			EhHeaterApplication.currentEhState = new EhState(data);
//			Intent intent = new Intent(getBaseContext(), MainActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			finish();
//
//		}
//	}
//	
//	@Override
//	public void OnGasWaterHeaterStatusResp(GasWaterHeaterStatusResp_t pResp, int nConnId) {
//		super.OnGasWaterHeaterStatusResp(pResp, nConnId);
//		
//		if (count++ == 0) {
//			mTvInfo.setText("状态查询成功!");
//			XPGConnectClient.RemoveActivity(this);
//			
//			EhHeaterApplication.currentGasHeaterStatus = pResp;
//			Intent intent = new Intent(getBaseContext(), GasMainActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			finish();
//		}
//	}
//	int count;

	public HeaterInfo getCurrentDevice() {

		if (curHeater == null) {
			curHeater = new HeaterInfoService(getBaseContext())
					.getCurrentSelectedHeater();
		}

		return curHeater;

//		 HeaterInfo hinfo = new HeaterInfo();
//		 hinfo.setMac("C8934641B3B4");
//		 hinfo.setDid("1");
//		 hinfo.setPasscode("FKAIDJKART");
//		 return hinfo;
		


//		 HeaterInfo hinfo = new HeaterInfo();
//		 hinfo.setMac("C8934642E4C7");
//		 hinfo.setDid("o4kvBWCq5QwcWuZZbm4w4Z");
//		 hinfo.setPasscode("JPDRRIXEKX");
//		 hinfo.setBinded(1);
//		 return hinfo;

	}

	HeaterInfo curHeater;

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

		if (!AccountService.isLogged(context)) {
			// 未登录, 跳转到登录页面
			// startActivity(new Intent(this, LoginActivity.class));
			// return STATE_JUMPED_OUT;
			flowHandler.sendEmptyMessage(STATE_JUMPED_OUT_TO_LOGIN);
			return;
		}

		// 已登录

		if (getCurrentDevice() == null) {
			// 无当前设备, 跳转到选择/新增设备页面
			// startActivity(new Intent(this, ShitActivity.class));
			// return STATE_JUMPED_OUT;
			flowHandler.sendEmptyMessage(STATE_JUMPED_OUT_TO_CONFIGURE);
			return;
		}

		// 已登录且有当前设备

//		if (TextUtils.isEmpty(getCurrentDevice().getDid())
//				|| TextUtils.isEmpty(getCurrentDevice().getPasscode())) {
//
//			// 当前设备没有did或者没有passcode, 此时只能通过小循环控制
//			// return STATE_LAN_ONLY;
//			flowHandler.sendEmptyMessage(STATE_LAN_ONLY);
//			return;
//		}

		// 已登录且有当前设备且当前设备有did和passcode, 最健康的状态
		// return STATE_NORMAL;

//		if (getCurrentDevice().getBinded() == 0
//				&& !TextUtils.isEmpty(getCurrentDevice().getPasscode()) 
//				&& !TextUtils.isEmpty(getCurrentDevice().getDid()) ) {
//			// 发现当前设备没有被绑定至用户账号, 执行绑定
//			flowHandler.sendEmptyMessage(STATE_INVOKE_BINDING_ACTIVITY);
//			return;
//		}

		flowHandler.sendEmptyMessage(STATE_NORMAL);
		return;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("emmm", "onActivityResult(RESULT_OK = -1):" + requestCode + "~"
				+ resultCode);

		if (requestCode == Consts.REQUESTCODE_UPLOAD_BINDING) {
			
			if (resultCode == RESULT_OK) {
				// binded
				new HeaterInfoService(getBaseContext()).updateBinded(
						getCurrentDevice().getMac(), true);
			}
			
			flowHandler.sendEmptyMessage(STATE_NORMAL);
			
		}
		
		if (requestCode == Consts.REQUESTCODE_LOGIN) {
			
			if (resultCode == RESULT_OK) {
				flowHandler.sendEmptyMessage(STATE_NORMAL);
			} else {
				// 返回非RESULT_OK时, 如果是在login页面点击返回的话, 直接杀进程, 此外的情况不杀进程
				finish();
				if (resultCode == -256) {
					// LoginActivity被其他activity关闭时, 关闭之前, 会setResult 256
					// 此时不杀进程
				} else {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
			
		}
		
	}

	private Handler flowHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {

			case STATE_JUMPED_OUT_TO_LOGIN:
				startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), Consts.REQUESTCODE_LOGIN);
				XPGConnectClient.RemoveActivity(WelcomeActivity.this);
				break;

			case STATE_JUMPED_OUT_TO_CONFIGURE:
				Intent intent = new Intent(getBaseContext(), ShitActivity.class);
				intent.putExtra(Consts.INTENT_EXTRA_CONFIGURE_ACTIVITY_SHOULD_KILL_PROCESS_WHEN_FINISH, true);
				startActivity(intent);
				XPGConnectClient.RemoveActivity(WelcomeActivity.this);
				finish();
				break;

//			case STATE_NORMAL:
//
//				if (NetworkStatusUtil.isConnectedByWifi(getBaseContext())) {
//					// 先试小循环, 不行则大
//					mTvInfo.setText("通过局域网连接中...(小循环)");
//					currentLanSearchingState = LAN_SEARCHING;
//					tryConnectBySmallCycle(defaultScanInterval, 8000,
//							new TimerTask() {
//								@Override
//								public void run() {
//									runOnUiThread(new Runnable() {
//										public void run() {
//											mTvInfo.setText("通过局域网连接热水器失败(小循环连接失败)");
//										};
//									});
//									// 启动大循环
//									tryConnectByBigCycle();
//								}
//							});
//
//				} else if (NetworkStatusUtil
//						.isConnectedByMobileData(getBaseContext())) {
//					// 只能大循环
//					tryConnectByBigCycle();
//				}
//
//				break;
//
//			case STATE_LAN_ONLY:
//				// 当前设备没有did, 仅能通过小循环控制
//				if (NetworkStatusUtil.isConnectedByWifi(getBaseContext())) {
//					mTvInfo.setText("通过局域网连接中...(小循环)");
//					currentLanSearchingState = LAN_SEARCHING;
//					tryConnectBySmallCycle(defaultScanInterval, 10000,
//							new TimerTask() {
//								@Override
//								public void run() {
//									runOnUiThread(new Runnable() {
//										public void run() {
//											mTvInfo.setText("通过局域网连接热水器失败(小循环连接失败)");
//										};
//									});
//								}
//							});
//
//				} else {
//					// 无法控制
//					mTvInfo.setText("无法连接设备(设备未上大循环)");
//				}
//
//				break;

			case STATE_INVOKE_BINDING_ACTIVITY:
				setBinding();
				break;
				
			case STATE_NORMAL:
				// 直接进入MainActivity
				HeaterType type = new HeaterInfoService(getBaseContext()).getCurHeaterType();
				switch (type) {
				case Eh:
					startActivity(new Intent(getBaseContext(), MainActivity.class));
					finish();
					break;
				case ST:
					startActivity(new Intent(getBaseContext(), GasMainActivity.class));
					finish();
					break;
				case EH_FURNACE:
					startActivity(new Intent(getBaseContext(), FurnaceMainActivity.class));
					finish();
					break;
					
				default:
					// 无法识别当前选择的设备, 请进入app删除此设备并选择其他设备
					startActivity(new Intent(getBaseContext(), SelectDeviceActivity.class));
					Toast.makeText(WelcomeActivity.this, "无法识别设备", Toast.LENGTH_LONG).show();
					break;
				}
				break;

			}

			return false;
		}
	});

	private void setBinding() {
		Intent intent = new Intent();
		intent.setClass(getBaseContext(), DummySendBindingReqActivity.class);
		intent.putExtra(Consts.INTENT_EXTRA_USERNAME,
				AccountService.getUserId(getBaseContext()));
		intent.putExtra(Consts.INTENT_EXTRA_USERPSW,
				AccountService.getUserPsw(getBaseContext()));
		intent.putExtra(Consts.INTENT_EXTRA_DID2BIND, getCurrentDevice()
				.getDid());
		intent.putExtra(Consts.INTENT_EXTRA_PASSCODE2BIND, getCurrentDevice()
				.getPasscode());
		startActivityForResult(intent, Consts.REQUESTCODE_UPLOAD_BINDING);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		/** test only */
//		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//			generated.SendOnOffReq(Global.connectId, (short) 1);
//		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//			generated.SendOnOffReq(Global.connectId, (short) 0);
//		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private final static int STATE_NORMAL = 1;
//	private final static int STATE_LAN_ONLY = 2;
	private final static int STATE_JUMPED_OUT_TO_LOGIN = 3;
	private final static int STATE_JUMPED_OUT_TO_CONFIGURE = 4;
	private final static int STATE_INVOKE_BINDING_ACTIVITY = 5;
}