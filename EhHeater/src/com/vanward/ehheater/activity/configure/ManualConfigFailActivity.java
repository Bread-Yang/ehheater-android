package com.vanward.ehheater.activity.configure;

import android.content.Intent;
import android.view.View;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class ManualConfigFailActivity extends EhHeaterBaseActivity {

	@Override
	public void initUI() {
		super.initUI();
		setTopText(R.string.manual_conf_fail);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.INVISIBLE);
		setCenterView(R.layout.activity_manual_fail);
		
		findViewById(R.id.amf_btn_retry).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), EasyLinkConfigureActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
	
}
