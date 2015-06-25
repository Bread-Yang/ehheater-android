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
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.EasyLinkDialogUtil;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.wifi.WifiAdmin;
import com.xtremeprog.xpgconnect.XPGConnectClient;
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
		
		if (getIntent().getBooleanExtra("isFurnace", false)) {
			TextView tv_connect_device_title = (TextView) findViewById(R.id.tv_connect_device_title);
			tv_connect_device_title.setText("连接壁挂炉");
			
			TextView tv_tips = (TextView) findViewById(R.id.tv_tips);
			tv_tips.setText(R.string.set_device_tip3_eh_furnace);
		}

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

			if (mTimer == null) {
				mTimer = new Timer();

				TimerTask timerTask = new TimerTask() {

					@Override
					public void run() {
						L.e(this, "在发送ssid和密码");
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
		L.e(this, "onEasyLinkResp()");
		// if (dialog_easylink.isShowing()) {
		//
		// L.e(this, "打印productKey前");
		// L.e(this, (null == endpoint.getSzProductKey()) + "");
		// L.e(this, ("".equals(endpoint.getSzProductKey()) + ""));
		// if (endpoint.getSzProductKey() == null
		// || "".equals(endpoint.getSzProductKey())) {
		// return;
		// }
		// if (endpoint.getSzMac() == null || "".equals(endpoint.getSzMac())) {
		// return;
		// }
		//
		// counter.cancel();
		// dialog_easylink.dismiss();
		//
		// finishingConfig(endpoint);
		// }
	}

	@Override
	public void onAirLinkResp(XpgEndpoint endpoint) {
		super.onAirLinkResp(endpoint);
		L.e(this, TAG + "的onAirLinkResp");
		if (dialog_easylink.isShowing()) {

			L.e(this, "打印productKey前");
			L.e(this, (null == endpoint.getSzProductKey()) + "");
			L.e(this, ("".equals(endpoint.getSzProductKey()) + ""));
			if (endpoint.getSzProductKey() == null
					|| "".equals(endpoint.getSzProductKey())) {
				return;
			}
			if (endpoint.getSzMac() == null || "".equals(endpoint.getSzMac())) {
				return;
			}

			counter.cancel();
			dialog_easylink.dismiss();

			finishingConfig(endpoint);
		}
	}

	private void finishingConfig(XpgEndpoint endpoint) {

		HeaterInfo hinfo = new HeaterInfo(endpoint);
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

		SharedPreferUtils spu = new SharedPreferUtils(this);
		if (hser.getHeaterType(hinfo) == HeaterType.ELECTRIC_HEATER) {
			spu.put(ShareKey.PollingElectricHeaterDid, hinfo.getDid());
			spu.put(ShareKey.PollingElectricHeaterMac, hinfo.getMac());
		} else if (hser.getHeaterType(hinfo) == HeaterType.GAS_HEATER) {
			spu.put(ShareKey.PollingGasHeaterDid, hinfo.getDid());
			spu.put(ShareKey.PollingGasHeaterMac, hinfo.getMac());
		} else if (hser.getHeaterType(hinfo) == HeaterType.FURNACE) {
			spu.put(ShareKey.PollingFurnaceDid, hinfo.getDid());
			spu.put(ShareKey.PollingFurnaceMac, hinfo.getMac());
		}

		new SharedPreferUtils(this).put(ShareKey.CurDeviceDid,
				endpoint.getSzDid());
		new SharedPreferUtils(this).put(ShareKey.CurDeviceAddress,
				endpoint.getAddr());

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
		
		boolean directLinkAfterEasyLink = true;

		switch (hser.getHeaterType(hinfo)) {

		case ELECTRIC_HEATER:

			intent = new Intent(getBaseContext(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK,
					directLinkAfterEasyLink);

			XPGConnectClient.RemoveActivity(this);
			finish();

			startActivity(intent);
			break;

		case GAS_HEATER:

			intent = new Intent(getBaseContext(), GasMainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK,
					directLinkAfterEasyLink);

			XPGConnectClient.RemoveActivity(this);
			finish();

			startActivity(intent);
			break;

		case FURNACE:

			intent = new Intent(getBaseContext(), FurnaceMainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(EasyLinkConfigureActivity.DIRECT_CONNECT_AFTER_EASYLINK,
					directLinkAfterEasyLink);

			XPGConnectClient.RemoveActivity(this);
			finish();

			startActivity(intent);
			break;

		default:
			Toast.makeText(getBaseContext(), "无法识别该设备", Toast.LENGTH_LONG)
					.show();
			break;

		}
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

		L.e(this, "OnOnboardingSetResp()");

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
				L.e(this,
						"scanResultList" + i + "----->"
								+ scanResultList.get(i).SSID);
				if (scanResultList.get(i).SSID.equals(lastConnectWifiSSID)) {
					canConnectable = true;
					break;
				}
			}
			if (canConnectable) {
				L.e(this, "%%%%%在可连接范围内%%%%%%%%");

				// 表示wifi是否已经配置过了
				boolean isConfigurated = false;
				int networkId = 0;
				// 判断是否连接上制定的wifi
				boolean isConnected = false;
				if (configuratedList != null) {
					L.e(this, "*********************");
					for (int j = 0; j < configuratedList.size(); j++) {
						L.e(this, "configuratedList" + j + "------->"
								+ configuratedList.get(j).SSID);
						if (configuratedList.get(j).SSID == null) {
							continue;
						}
						if (configuratedList.get(j).SSID
								.equals(lastConnectWifiConfigureSSID)) {
							isConfigurated = true;
							networkId = configuratedList.get(j).networkId;
							L.e(this, "#########################" + j);
							break;
						}
						// isConfigurated = false;
					}
					L.e(this, "%%%%%%%%%%%%%%%%%%%%%%" + isConfigurated);
					if (isConfigurated) {
						L.e(this, "^^^^^^^连接前");
						wifiAdmin.connectWifi(networkId);
						L.e(this, "#########连接后");
						L.e(this, "isConnected--------->" + isConnected);
						while (!isConnected) {
							L.e(this, "*************连接上指定wifi*************");
							break;
						}
						L.e(this, "**************************");
					}
				}
			} else {
				L.e(this, "$$$$$$$$$$不在可连接范围内$$$$$$$$$$$$$");
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
}
