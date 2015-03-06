package com.vanward.ehheater.activity.configure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;

public class AutoConfigureFailActivity extends EhHeaterBaseActivity {

	@Override
	public void initUI() {
		super.initUI();
		setTopText(R.string.auto_conf_fail);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.INVISIBLE);
		setCenterView(R.layout.activity_auto_conf_fail);

		findViewById(R.id.aacf_btn_retry).setOnClickListener(this);
		findViewById(R.id.aacf_btn_manual).setOnClickListener(this);
		
		boolean isFurnace = getIntent().getBooleanExtra("isFurnace", false);
		if (isFurnace) {
			((TextView) findViewById(R.id.tv_fail_title)).setText(R.string.setup_furnace_fail_title);
			((TextView) findViewById(R.id.tv_fail_tips)).setText(R.string.setup_furnace_fail_tips);
		}

		IntentFilter filter = new IntentFilter(
				Consts.INTENT_FILTER_KILL_AUTO_CONFIGURE_FAIL_ACTIVITY);
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
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.aacf_btn_retry:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.aacf_btn_manual:
			startActivity(new Intent(getBaseContext(),
					ManualConfStep1Activity.class));
			break;
		}
	}

}
