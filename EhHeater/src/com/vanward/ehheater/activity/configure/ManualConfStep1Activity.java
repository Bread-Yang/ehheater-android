package com.vanward.ehheater.activity.configure;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.easylink.android.EasyLinkWifiManager;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.util.TextStyleUtil;
import com.vanward.ehheater.util.wifi.WifiAdmin;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.OnboardingSetResp_t;
import com.xtremeprog.xpgconnect.generated.XpgDataField;
import com.xtremeprog.xpgconnect.generated.generated;

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
		TextStyleUtil.setColorStringInTextView(t,
				getResources().getColor(R.color.title_color),
				new String[] { heaterSsid });

//		XPGConnectClient.initClient(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		TimerTask timerTask = new TimerTask() {

			@Override 
			public void run() {
				checkCurrentWifi();
			}
		};

		if (mTimer == null) {
			mTimer = new Timer();
		}
		mTimer.schedule(timerTask, 0, 5000);
	}
	
	private void checkCurrentWifi() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		wifiManager.startScan();
		List<ScanResult> result = wifiManager.getScanResults();
		for (ScanResult item : result) {
			// Log.e(TAG, "得到的wifi的SSID是 : " + item.SSID);
			// Log.e(TAG, "得到的wifi的BSSID是 : " + item.BSSID);
			// Log.e(TAG, "得到的wifi的level是 : " + item.level);
			String ssid = item.SSID;
			String mac = item.BSSID;
			Log.e(TAG, "ssid是 : " + ssid);
			if (!ssid.startsWith("XPG-GAgent")) {
				continue;
			}
			String last5Characters = mac.substring(mac.length() - 5,
					mac.length());
			String[] strings = last5Characters.split(":");
			String targetSSID = "XPG-GAgent-"
					+ strings[0].toUpperCase()
					+ strings[1].toUpperCase();
//			if (targetSSID.equals(ssid)) {
				Log.e(TAG, "找到AP的热点了");

				WifiInfo info = wifiManager.getConnectionInfo();
				String currentLinkSSID = info.getSSID();
				if (currentLinkSSID == null) {
					return;
				}
				Log.e(TAG, "当前已经连上的热点名称是 : " + currentLinkSSID);
				
				Log.e(TAG, "改之前currentLinkSSID : " + currentLinkSSID);
				
				currentLinkSSID = currentLinkSSID.replace("\"", "");
				
				Log.e(TAG, "改之后currentLinkSSID : " + currentLinkSSID);
				Log.e(TAG, "ssid : " + ssid);
				Log.e(TAG, "targetSSID : " + targetSSID);
				
//				if (!currentLinkSSID.equals("\"" + targetSSID + "\"")) {
				if (!currentLinkSSID.equals(ssid)) {
					Log.e(TAG, "正在连上设备热点");
//					WifiAdmin wifiAdmin = new WifiAdmin(
//							ManualConfStep1Activity.this);
//					wifiAdmin.openWifi();
//					wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid,
//							"123456789", 3));
				} else {
					if (mTimer != null) {
						mTimer.cancel();
						mTimer = null;
					}

					startActivity(new Intent(getBaseContext(),
							ManualConfStep2Activity.class));
					finish();
				}
//				break;
//			}
		}
	
	}
}
