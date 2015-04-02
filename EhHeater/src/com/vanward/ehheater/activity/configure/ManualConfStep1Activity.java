package com.vanward.ehheater.activity.configure;

import java.util.List;
import java.util.Timer;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.util.L;

public class ManualConfStep1Activity extends EhHeaterBaseActivity {

	private final String TAG = "ManualConfStep1Activity";

	String heaterSsid;

	private Timer mTimer;

	@Override 
	public void initUI() {
		super.initUI();
		setTopText(R.string.manual_conf_guide);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.INVISIBLE);
		setCenterView(R.layout.activity_manual_conf_1);

		heaterSsid = getResources().getString(R.string.heater_wifi_ssid);

		TextView t = (TextView) findViewById(R.id.amc1_tv);
		t.setText(Html.fromHtml(getString(R.string.manual_operation_step)));
		
		if (getIntent().getBooleanExtra("isFurnace", false)) {
			TextView tv_connect_device_title = (TextView) findViewById(R.id.tv_connect_device_title);
			tv_connect_device_title.setText("连接壁挂炉");
		}
		
//		TextStyleUtil.setColorStringInTextView(t,
//				getResources().getColor(R.color.title_color),
//				new String[] { heaterSsid });

		// XPGConnectClient.initClient(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// TimerTask timerTask = new TimerTask() {
		//
		// @Override
		// public void run() {
		// checkCurrentWifi();
		// }
		// };
		//
		// if (mTimer == null) {
		// mTimer = new Timer();
		// }
		// mTimer.schedule(timerTask, 0, 5000);

		checkCurrentWifi();
	}

	private void checkCurrentWifi() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		wifiManager.startScan();
		List<ScanResult> result = wifiManager.getScanResults();
		for (ScanResult item : result) {
			// L.e(this, "得到的wifi的SSID是 : " + item.SSID);
			// L.e(this, "得到的wifi的BSSID是 : " + item.BSSID);
			// L.e(this, "得到的wifi的level是 : " + item.level);
			String ssid = item.SSID;
			String mac = item.BSSID;
			L.e(this, "ssid是 : " + ssid);
			if (!ssid.startsWith("XPG-GAgent")) {
				continue;
			}
			String last5Characters = mac.substring(mac.length() - 5,
					mac.length());
			String[] strings = last5Characters.split(":");
			String targetSSID = "XPG-GAgent-" + strings[0].toUpperCase()
					+ strings[1].toUpperCase();
			// if (targetSSID.equals(ssid)) {
			L.e(this, "找到AP的热点了");

			WifiInfo info = wifiManager.getConnectionInfo();
			String currentLinkSSID = info.getSSID();
			if (currentLinkSSID == null) {
				return;
			}
			L.e(this, "当前已经连上的热点名称是 : " + currentLinkSSID);

			L.e(this, "改之前currentLinkSSID : " + currentLinkSSID);

			currentLinkSSID = currentLinkSSID.replace("\"", "");

			L.e(this, "改之后currentLinkSSID : " + currentLinkSSID);
			L.e(this, "ssid : " + ssid);
			L.e(this, "targetSSID : " + targetSSID);

			// if (!currentLinkSSID.equals("\"" + targetSSID + "\"")) {
			if (!currentLinkSSID.equals(ssid)) {
				L.e(this, "正在连上设备热点");
				// WifiAdmin wifiAdmin = new WifiAdmin(
				// ManualConfStep1Activity.this);
				// wifiAdmin.openWifi();
				// wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid,
				// "123456789", 3));
			} else {
				if (mTimer != null) {
					mTimer.cancel();
					mTimer = null;
				}

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						Intent intent = new Intent(ManualConfStep1Activity.this, ManualConfStep2Activity.class);
						intent.putExtra("isFurnace",
								getIntent().getBooleanExtra("isFurnace", false));
						startActivity(intent);
						finish();
					}
				}, 5000);
			}
			// break;
			// }
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
