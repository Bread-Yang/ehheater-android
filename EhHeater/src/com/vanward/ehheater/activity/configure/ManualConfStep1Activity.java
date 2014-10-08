package com.vanward.ehheater.activity.configure;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.easylink.android.EasyLinkWifiManager;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.util.TextStyleUtil;

public class ManualConfStep1Activity extends EhHeaterBaseActivity {
	
	String heaterSsid;

	@Override
	public void initUI() {
		super.initUI();
		setTopText(R.string.manual_conf_guide);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.INVISIBLE);
		setCenterView(R.layout.activity_manual_conf_1);
		
		heaterSsid = getResources().getString(R.string.heater_wifi_ssid);
		
		TextView t = (TextView) findViewById(R.id.amc1_tv);
		TextStyleUtil.setColorStringInTextView(t, getResources().getColor(R.color.title_color), 
				new String[]{heaterSsid});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//check current connected wifi ssid == XPG-GAgent
		String curWifiSsid = new EasyLinkWifiManager(getBaseContext()).getCurrentSSID();
		if (!TextUtils.isEmpty(heaterSsid) && heaterSsid.equals(curWifiSsid)) {
			// jump
			startActivity(new Intent(getBaseContext(), ManualConfStep2Activity.class));
			finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/**only for test*/
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

			startActivity(new Intent(getBaseContext(), ManualConfStep2Activity.class));
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
