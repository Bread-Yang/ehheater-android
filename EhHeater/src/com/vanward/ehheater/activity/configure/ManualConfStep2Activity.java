package com.vanward.ehheater.activity.configure;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easylink.android.EasyLinkWifiManager;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.WelcomeActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.main.MainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceMainActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.EasyLinkDialogUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.wifi.WifiAdmin;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.EasylinkResp_t;
import com.xtremeprog.xpgconnect.generated.OnboardingSetResp_t;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

public class ManualConfStep2Activity extends EhHeaterBaseActivity {

	private static final String TAG = "ManualConfStep2Activity";

	private EditText ed_input_ssid, mEtPsw;
	private Button mBtnNext;
	private Dialog dialog_easylink;
	private EasyLinkTimeoutCounter counter = new EasyLinkTimeoutCounter(60000,
			1000);

	private Timer mTimer;

	private String lastConnectWifiSSID;

	private String lastConnectWifiConfigureSSID;

	boolean isWaitingCallback = false;

	@Override
	public void initUI() {
		super.initUI();
		setTopText(R.string.manual_conf_guide);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.INVISIBLE);
		setCenterView(R.layout.activity_manual_conf_2);

		ed_input_ssid = (EditText) findViewById(R.id.et_input_ssid);
		mEtPsw = (EditText) findViewById(R.id.amc2_et_psw);
		mBtnNext = (Button) findViewById(R.id.amc2_btn_next);

		lastConnectWifiSSID = new SharedPreferUtils(getBaseContext()).get(
				ShareKey.PendingSsid, "").trim();

		lastConnectWifiConfigureSSID = "\"" + lastConnectWifiSSID + "\"";

		ed_input_ssid.setText(lastConnectWifiSSID);
		mBtnNext.setOnClickListener(this);

		dialog_easylink = EasyLinkDialogUtil.initEasyLinkDialog(this,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// cancel listener
						dialog_easylink.dismiss();
						counter.cancel();
					}
				});

	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

		switch (view.getId()) {

		case R.id.amc2_btn_next:

			if (TextUtils.isEmpty(ed_input_ssid.getText())) {
				Toast.makeText(getBaseContext(), "请输入wifi名称", 500).show();
				return;
			}

			dialog_easylink.show();
			counter.start();

			// XpgDataField sendSSID = generated
			// .String2XpgData("yoghourt_bao");
			// XpgDataField sendPSW = generated
			// .String2XpgData("aaabbccdd");
			// Log.e(TAG, "发送请求了"); 
			// generated.SendOnboardingSetReq(sendSSID,
			// sendPSW);

			if (mTimer == null) {
				mTimer = new Timer();

				TimerTask timerTask = new TimerTask() {

					@Override
					public void run() { 
						Log.e(TAG, "在发送ssid和密码");
						generated.SendOnboardingSetReq(
								generated.String2XpgData(ed_input_ssid
										.getText().toString().trim()),
								generated.String2XpgData(mEtPsw.getText()
										.toString().trim()));
					}
				};

				mTimer.schedule(timerTask, 0, 5000);
			}

			break;
		}

	}

	@Override
	public void onEasyLinkResp(XpgEndpoint endpoint) {
		Log.e(TAG, TAG + "的onEasyLinkResp执行了");
		if (dialog_easylink.isShowing()) {
			// 配置成功, 保存设备(此时密码为空), 跳转回welcome
			tempEndpoint = endpoint;

			if (endpoint.getSzProductKey() == null
					|| "".equals(endpoint.getSzProductKey())) {
				return;
			}
			if (endpoint.getSzMac() == null || "".equals(endpoint.getSzMac())) {
				return;
			}
			finishingConfig();
		}
	}

	XpgEndpoint tempEndpoint;

	private void finishingConfig() {

		counter.cancel();
		dialog_easylink.dismiss();
		Toast.makeText(getBaseContext(), "配置成功!", 1000).show();

		Log.e("打印productKey前", "打印productKey前");
		HeaterInfo hinfo = new HeaterInfo(tempEndpoint);
		Log.e("productKey是 : ", hinfo.getProductKey());
		HeaterInfoService hser = new HeaterInfoService(getBaseContext());

		if (!hser.isValidDevice(hinfo)) {
			Toast.makeText(getBaseContext(), "无法识别该设备", Toast.LENGTH_LONG)
					.show();
			return;
		}

		Toast.makeText(getBaseContext(), "配置成功!", 1000).show();

		HeaterInfoDao hdao = new HeaterInfoDao(this);
		List<HeaterInfo> list = hdao.getAll();
		boolean flag = false;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getMac().equals(hinfo.getMac())) {
				flag = true;
			}
		}

		if (flag) {
			Toast.makeText(this, "此设备已存在", 1000).show();
		} else {
			hser.addNewHeater(hinfo);
		}

		Log.d("emmm", "finishingConfig:new heater saved!" + hinfo.getMac()
				+ "-" + hinfo.getPasscode());

		String username = AccountService.getPendingUserId(getBaseContext());
		String userpsw = AccountService.getPendingUserPsw(getBaseContext());
		if (!AccountService.isLogged(getBaseContext())
				&& !TextUtils.isEmpty(username) && !TextUtils.isEmpty(userpsw)) {
			AccountService.setUser(getBaseContext(), username, userpsw);
		}

		// send a broad cast to finish login activity
		Intent killerIntent = new Intent(
				Consts.INTENT_FILTER_KILL_LOGIN_ACTIVITY);
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				killerIntent);

		Intent intent = null;

		switch (hser.getHeaterType(hinfo)) {

		case Eh:

			intent = new Intent(getBaseContext(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			XPGConnectClient.RemoveActivity(this);
			finish();

			startActivity(intent);
			break;

		case ST:

			intent = new Intent(getBaseContext(), GasMainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			XPGConnectClient.RemoveActivity(this);
			finish();

			startActivity(intent);
			break;

		case EH_FURNACE:

			intent = new Intent(getBaseContext(), FurnaceMainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			XPGConnectClient.RemoveActivity(this);
			finish();

			startActivity(intent);
			break;

		default:
			Toast.makeText(getBaseContext(), "无法识别该设备", Toast.LENGTH_LONG)
					.show();
			break;

		}

		// Log.e("打印productKey前", "打印productKey前");
		// HeaterInfo hinfo = new HeaterInfo(tempEndpoint);
		// Log.e("productKey是 : ", hinfo.getProductKey());
		// // hinfo.setPasscode(generated.XpgData2String(pResp.getPasscode()));
		// new HeaterInfoService(getBaseContext()).addNewHeater(hinfo);
		// Log.d("emmm", "finishingConfig:new heater saved!" + hinfo.getMac()
		// + "-" + hinfo.getPasscode());
		//
		// String username = AccountService.getPendingUserId(getBaseContext());
		// String userpsw = AccountService.getPendingUserPsw(getBaseContext());
		// if (!AccountService.isLogged(getBaseContext())
		// && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(userpsw)) {
		// AccountService.setUser(getBaseContext(), username, userpsw);
		// }

		// send broadcasts to finish previous activities
		// LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
		// new Intent(Consts.INTENT_FILTER_KILL_LOGIN_ACTIVITY));
		// LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
		// new Intent(Consts.INTENT_FILTER_KILL_CONFIGURE_ACTIVITY));
		// LocalBroadcastManager
		// .getInstance(getBaseContext())
		// .sendBroadcast(
		// new Intent(
		// Consts.INTENT_FILTER_KILL_AUTO_CONFIGURE_FAIL_ACTIVITY));
		//
		// waitWifiChange();
	}

	private void waitWifiChange() {
		Dialog waitDialog = EasyLinkDialogUtil.initWaitWifiDialog(this, null);
		waitDialog.show();
		new CountDownTimer(120000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				String curSsid = new EasyLinkWifiManager(getBaseContext())
						.getCurrentSSID();
				String pendingSsid = lastConnectWifiSSID;
				if (pendingSsid.equals(curSsid)) {
					Intent intent = new Intent();
					intent.setClass(getBaseContext(), WelcomeActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_FLAG_REENTER, true);
					startActivity(intent);

					XPGConnectClient
							.RemoveActivity(ManualConfStep2Activity.this);
					finish();
				}
			}

			@Override
			public void onFinish() {

			}

		}.start();
	}

	@Override
	public void OnOnboardingSetResp(OnboardingSetResp_t pResp) {
		super.OnOnboardingSetResp(pResp);

		Log.e(TAG, "OnOnboardingSetResp执行了");

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		linkLastConnectWifi();
		
		// WifiAdmin wifiAdmin = new WifiAdmin(ManualConfStep2Activity.this);
		// wifiAdmin.openWifi();
		// wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, "123456789", 3));

	}

	private void linkLastConnectWifi() {
		WifiAdmin wifiAdmin = new WifiAdmin(ManualConfStep2Activity.this);
		List<WifiConfiguration> configuratedList = wifiAdmin
				.getConfigurationList();
		List<ScanResult> scanResultList = wifiAdmin.getScanResultList();
		if (scanResultList != null) {
			// 表示是否在可连接范围内
			boolean canConnectable = false;
			for (int i = 0; i < scanResultList.size(); i++) {
				Log.e(TAG,
						"scanResultList" + i + "----->"
								+ scanResultList.get(i).SSID);
				if (scanResultList.get(i).SSID.equals(lastConnectWifiSSID)) {
					canConnectable = true;
					break;
				}
			}
			if (canConnectable) {
				Log.e(TAG, "%%%%%在可连接范围内%%%%%%%%");

				// 表示wifi是否已经配置过了
				boolean isConfigurated = false;
				int networkId = 0;
				// 判断是否连接上制定的wifi
				boolean isConnected = false;
				if (configuratedList != null) {
					Log.e(TAG, "*********************");
					for (int j = 0; j < configuratedList.size(); j++) {
						Log.e(TAG, "configuratedList" + j + "------->"
								+ configuratedList.get(j).SSID);
						if (configuratedList.get(j).SSID
								.equals(lastConnectWifiConfigureSSID)) {
							isConfigurated = true;
							networkId = configuratedList.get(j).networkId;
							Log.e(TAG, "#########################" + j);
							break;
						}
						// isConfigurated = false;
					}
					Log.e(TAG, "%%%%%%%%%%%%%%%%%%%%%%" + isConfigurated);
					if (isConfigurated) {
						Log.e(TAG, "^^^^^^^连接前");
						wifiAdmin.connectWifi(networkId);
						Log.e(TAG, "#########连接后");
						Log.e(TAG, "isConnected--------->" + isConnected);
						while (!isConnected) {
							Log.e(TAG, "*************连接上指定wifi*************");
							break;
						}
						Log.e(TAG, "**************************");
					}
				}
			} else {
				Log.e(TAG, "$$$$$$$$$$不在可连接范围内$$$$$$$$$$$$$");
			}
		}
	}

	private class EasyLinkTimeoutCounter extends CountDownTimer {

		public EasyLinkTimeoutCounter(long millisInFuture,
				long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			dialog_easylink.dismiss();
			// fail
			startActivity(new Intent(getBaseContext(),
					ManualConfigFailActivity.class));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			((TextView) dialog_easylink.findViewById(R.id.easylink_time_tv))
					.setText(millisUntilFinished / 1000 + "");
		}
	}

}
