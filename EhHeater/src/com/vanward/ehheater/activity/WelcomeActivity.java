package com.vanward.ehheater.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.umeng.analytics.MobclickAgent;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.info.SelectDeviceActivity;
import com.vanward.ehheater.activity.login.LoginActivity;
import com.vanward.ehheater.activity.main.MainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceMainActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.notification.PollingService;
import com.vanward.ehheater.notification.PollingUtils;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;

public class WelcomeActivity extends GeneratedActivity {

	private final String TAG = "WelcomeActivity";

	private TextView mTvInfo;

	private final int HANDLE_OUTSIDE_NETWORK = 0;
	private final int HANDLE_INSIDE_NETWORK = 1;

	boolean isPingTimeout = false;

	public static final String IS_LOGOUT_TO_WELCOME = "is_logout_to_welcome";

	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_OUTSIDE_NETWORK:
				L.e(this, "能上外网");
				// XPGConnectClient.xpgc4Login(Consts.VANWARD_APP_ID,
				// AccountService.getUserId(getBaseContext()),
				// AccountService.getUserPsw(getBaseContext()));
				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				if (!getIntent().getBooleanExtra(IS_LOGOUT_TO_WELCOME, false)) {
					intent.putExtra("queryDevicesListAgain", true);
				}
				startActivityForResult(intent, Consts.REQUESTCODE_LOGIN);
				break;
			case HANDLE_INSIDE_NETWORK:
				L.e(this, "只能上内网");
				if (getIntent().getBooleanExtra(IS_LOGOUT_TO_WELCOME, false)) {
					flowHandler.sendEmptyMessage(STATE_JUMPED_OUT_TO_LOGIN);
					return;
				}
				if (getCurrentDevice() == null) {
					// 无当前设备, 跳转到选择/新增设备页面
					// startActivity(new Intent(this,
					// EasyLinkConfigureActivity.class));
					// return STATE_JUMPED_OUT;
					
					L.e(this, "选择电热或者燃热,木有选择壁挂炉");
					
					HeaterInfoService hser = new HeaterInfoService(
							WelcomeActivity.this);

					List<HeaterInfo> allEIDevices = new HeaterInfoDao(
							getBaseContext())
							.getAllDeviceOfType(HeaterType.ELECTRIC_HEATER);
					if (allEIDevices != null & allEIDevices.size() > 0) {
						hser.setCurrentSelectedHeater(allEIDevices.get(0)
								.getMac());
						flowHandler.sendEmptyMessage(STATE_NORMAL);
						return;
					}

					List<HeaterInfo> allGasDevices = new HeaterInfoDao(
							getBaseContext())
							.getAllDeviceOfType(HeaterType.GAS_HEATER);

					if (allGasDevices != null & allGasDevices.size() > 0) {
						hser.setCurrentSelectedHeater(allGasDevices.get(0)
								.getMac());
						flowHandler.sendEmptyMessage(STATE_NORMAL);
						return;
					}

					flowHandler.sendEmptyMessage(STATE_JUMPED_OUT_TO_CONFIGURE);
					return;
				}

				flowHandler.sendEmptyMessage(STATE_NORMAL);
				break;
			}
		};
	};

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
			// XPGConnectClient.initClient(this);
		}

		boolean asDialog = getIntent().getBooleanExtra(
				Consts.INTENT_EXTRA_FLAG_AS_DIALOG, false);
		if (asDialog) {
			setContentView(R.layout.activity_welcome_as_dialog);
			mTvInfo = (TextView) findViewById(R.id.awad_tv);
		} else {
			setTheme(R.style.AppBaseTheme);
			setContentView(R.layout.activity_welcome);
			mTvInfo = (TextView) findViewById(R.id.aw_tv);
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				checkLoginAndCurrentDeviceStatus(getBaseContext(), flowHandler);
			}
		}, 1000);

		MobclickAgent.updateOnlineConfig(this);

		// 每60秒请求一次
//		PollingUtils.startPollingService(this, 60, PollingService.class,
//				PollingService.ACTION);
	}

	HeaterInfo curHeater;

	public HeaterInfo getCurrentDevice() {

		if (curHeater == null) {
			curHeater = new HeaterInfoService(getBaseContext())
					.getCurrentSelectedHeater();
		}

		return curHeater;
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
			final Handler flowHandler) {

		if (!NetworkStatusUtil.isConnected(this)) {
			Toast.makeText(this, R.string.check_network, Toast.LENGTH_LONG)
					.show();
			flowHandler.sendEmptyMessage(STATE_JUMPED_OUT_TO_LOGIN);
			return;
		}

		if (!AccountService.isLogged(context)) {
			// 未登录, 跳转到登录页面
			// startActivity(new Intent(this, LoginActivity.class));
			// return STATE_JUMPED_OUT;
			flowHandler.sendEmptyMessage(STATE_JUMPED_OUT_TO_LOGIN);
			return;
		} else { // 之前已登录
			final CountDownTimer timer = new CountDownTimer(5000, 1000) {

				@Override
				public void onTick(long arg0) {

				}

				@Override
				public void onFinish() {
					isPingTimeout = true;
					mHandler.sendEmptyMessage(HANDLE_INSIDE_NETWORK);
				}
			};

			FinalHttp finalHttp = new FinalHttp();

			finalHttp.get("http://www.baidu.com", new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					timer.cancel();
					if (!isPingTimeout) {
						mHandler.sendEmptyMessage(HANDLE_OUTSIDE_NETWORK);
					}
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					timer.cancel();
					if (!isPingTimeout) {
						mHandler.sendEmptyMessage(HANDLE_INSIDE_NETWORK);
						// flowHandler.sendEmptyMessage(STATE_JUMPED_OUT_TO_LOGIN);
						// 现在改成了只能用外网登录
					}
				}
			});

			timer.start();

			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// if (PingUtil.ping(WelcomeActivity.this)) { // 可以上外网
			//
			// } else { // 只能用内网
			//
			// }
			// }
			// }).start();
		}
	}

	Handler pingHandler = new Handler() {
		public void handleMessage(Message msg) {

		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		L.e(this, "onActivityResult(RESULT_OK = -1):" + requestCode + "~"
				+ resultCode);

		// if (requestCode == Consts.REQUESTCODE_UPLOAD_BINDING) {
		//
		// if (resultCode == RESULT_OK) {
		// // binded
		// new HeaterInfoService(getBaseContext()).updateBinded(
		// getCurrentDevice().getMac(), true);
		// }
		//
		// flowHandler.sendEmptyMessage(STATE_NORMAL);
		//
		// }

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
				startActivityForResult(new Intent(getBaseContext(),
						LoginActivity.class), Consts.REQUESTCODE_LOGIN);
				XPGConnectClient.RemoveActivity(WelcomeActivity.this);
				break;

			case STATE_JUMPED_OUT_TO_CONFIGURE:
				Intent intent = new Intent(getBaseContext(),
						SelectDeviceActivity.class);
				intent.putExtra("fromWelcomeActivity", true);
				startActivity(intent);
				XPGConnectClient.RemoveActivity(WelcomeActivity.this);
				finish();
				break;

			case STATE_NORMAL:
				L.e(this, "STATE_NORMAL()被选择");
				
				HeaterInfoService heaterService = new HeaterInfoService(
						getBaseContext());
				SharedPreferUtils spu = new SharedPreferUtils(
						WelcomeActivity.this);
				String did = heaterService.getCurrentSelectedHeater().getDid();
				String mac = heaterService.getCurrentSelectedHeater().getMac();
				HeaterType type = heaterService.getCurHeaterType();
				switch (type) {
				case ELECTRIC_HEATER:
					L.e(this, "跳进了电热水器");
					spu.put(ShareKey.PollingElectricHeaterDid, did);
					spu.put(ShareKey.PollingElectricHeaterMac, mac);

					startActivity(new Intent(getBaseContext(),
							MainActivity.class));
					finish();
					break;
				case GAS_HEATER:
					spu.put(ShareKey.PollingGasHeaterDid, did);
					spu.put(ShareKey.PollingGasHeaterMac, mac);

					startActivity(new Intent(getBaseContext(),
							GasMainActivity.class));
					finish();
					break;
				case FURNACE:
					spu.put(ShareKey.PollingFurnaceDid, did);
					spu.put(ShareKey.PollingFurnaceMac, mac);

					startActivity(new Intent(getBaseContext(),
							FurnaceMainActivity.class));
					finish();
					break;

				default:
					// 无法识别当前选择的设备, 请进入app删除此设备并选择其他设备
					Toast.makeText(WelcomeActivity.this,
							"下载到了无法识别的设备, 请进入app切换至别的设备", Toast.LENGTH_LONG)
							.show();
					startActivity(new Intent(getBaseContext(),
							MainActivity.class));
					finish();
					break;
				}
				break;

			}

			return false;
		}
	});

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		JPushInterface.onResume(this);
		isPingTimeout = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		JPushInterface.onPause(this);
	}

	private final static int STATE_NORMAL = 1;
	// private final static int STATE_LAN_ONLY = 2;
	private final static int STATE_JUMPED_OUT_TO_LOGIN = 3;
	private final static int STATE_JUMPED_OUT_TO_CONFIGURE = 4;
	// private final static int STATE_INVOKE_BINDING_ACTIVITY = 5;
}