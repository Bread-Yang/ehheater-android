package com.vanward.ehheater.activity.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.configure.EasyLinkConfigureActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.login.LoginActivity;

public class SelectDeviceActivity extends Activity implements OnClickListener {
	TextView name, time, detail;
	private Button rightbButton;
	private View leftbutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_device);
		initview();
	}

	public void initview() {
		leftbutton = ((Button) findViewById(R.id.ivTitleBtnLeft));
		leftbutton.setOnClickListener(this);
		if (getIntent().getBooleanExtra("isDeleteAll", false)) {
			leftbutton.setVisibility(View.GONE);
		}
		rightbButton = ((Button) findViewById(R.id.ivTitleBtnRigh));
		rightbButton.setVisibility(View.GONE);
		leftbutton.setBackgroundResource(R.drawable.icon_back);
		TextView title = (TextView) findViewById(R.id.ivTitleName);
		title.setText("设备选择");

		findViewById(R.id.elect).setOnClickListener(this);
		findViewById(R.id.gas).setOnClickListener(this);
		findViewById(R.id.rlt_furnace).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.ivTitleBtnLeft) {
			onBackPressed();
		} else if (arg0.getId() == R.id.elect) {
			Intent intent = new Intent(getBaseContext(),
					EasyLinkConfigureActivity.class);
			intent.putExtra("type", "elect");
			startActivity(intent);

		} else if (arg0.getId() == R.id.gas) {
			Intent intent = new Intent(getBaseContext(),
					EasyLinkConfigureActivity.class);
			intent.putExtra("type", "gas");
			startActivity(intent);

		} else if (arg0.getId() == R.id.rlt_furnace) {
			Intent intent = new Intent(getBaseContext(),
					EasyLinkConfigureActivity.class);
			intent.putExtra("type", "furnace");
			startActivity(intent);

		}

	}

	@Override
	public void onBackPressed() {
		boolean shouldKill = getIntent()
				.getBooleanExtra(
						Consts.INTENT_EXTRA_CONFIGURE_ACTIVITY_SHOULD_KILL_PROCESS_WHEN_FINISH,
						false);
		if (shouldKill) {
			android.os.Process.killProcess(android.os.Process.myPid());
		} else {
			if (getIntent().getBooleanExtra("fromWelcomeActivity", false)) {
				startActivity(new Intent(this, LoginActivity.class));
				finish();
			} else {
				super.onBackPressed();
			}
		}
	}
}
