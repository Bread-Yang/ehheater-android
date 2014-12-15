package com.vanward.ehheater.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.info.SelectDeviceActivity;
import com.vanward.ehheater.activity.login.LoginActivity;
import com.vanward.ehheater.activity.main.MainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceMainActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.notification.PollingService;
import com.vanward.ehheater.notification.PollingUtils;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;

public class WelcomeActivity extends GeneratedActivity {

	private TextView mTvInfo;

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

		// 每5分钟请求一次
//		PollingUtils.startPollingService(this, 5 * 60, PollingService.class,
//				PollingService.ACTION);
	}

	public HeaterInfo getCurrentDevice() {

		if (curHeater == null) {
			curHeater = new HeaterInfoService(getBaseContext())
					.getCurrentSelectedHeater();
		}

		return curHeater;

		// HeaterInfo hinfo = new HeaterInfo();
		// hinfo.setMac("C8934641B3B4");
		// hinfo.setDid("1");
		// hinfo.setPasscode("FKAIDJKART");
		// return hinfo;

		// HeaterInfo hinfo = new HeaterInfo();
		// hinfo.setMac("C8934642E4C7");
		// hinfo.setDid("o4kvBWCq5QwcWuZZbm4w4Z");
		// hinfo.setPasscode("JPDRRIXEKX");
		// hinfo.setBinded(1);
		// return hinfo;

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

		flowHandler.sendEmptyMessage(STATE_NORMAL);
		return;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("emmm", "onActivityResult(RESULT_OK = -1):" + requestCode + "~"
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
				intent.putExtra("isDeleteAll", true);
				startActivity(intent);
				XPGConnectClient.RemoveActivity(WelcomeActivity.this);
				finish();
				break;

			case STATE_NORMAL:
				// 直接进入MainActivity
				HeaterInfoService heaterService = new HeaterInfoService(
						getBaseContext());
				HeaterType type = heaterService.getCurHeaterType();
				switch (type) {
				case Eh:
					startActivity(new Intent(getBaseContext(),
							MainActivity.class));
					finish();
					break;
				case ST:
					startActivity(new Intent(getBaseContext(),
							GasMainActivity.class));
					finish();
					break;
				case EH_FURNACE:
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

	// private void setBinding() {
	// Intent intent = new Intent();
	// intent.setClass(getBaseContext(), DummySendBindingReqActivity.class);
	// intent.putExtra(Consts.INTENT_EXTRA_USERNAME,
	// AccountService.getUserId(getBaseContext()));
	// intent.putExtra(Consts.INTENT_EXTRA_USERPSW,
	// AccountService.getUserPsw(getBaseContext()));
	// intent.putExtra(Consts.INTENT_EXTRA_DID2BIND, getCurrentDevice()
	// .getDid());
	// intent.putExtra(Consts.INTENT_EXTRA_PASSCODE2BIND, getCurrentDevice()
	// .getPasscode());
	// startActivityForResult(intent, Consts.REQUESTCODE_UPLOAD_BINDING);
	// }

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
	// private final static int STATE_LAN_ONLY = 2;
	private final static int STATE_JUMPED_OUT_TO_LOGIN = 3;
	private final static int STATE_JUMPED_OUT_TO_CONFIGURE = 4;
	// private final static int STATE_INVOKE_BINDING_ACTIVITY = 5;
}