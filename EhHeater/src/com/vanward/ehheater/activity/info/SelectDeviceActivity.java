package com.vanward.ehheater.activity.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.configure.ShitActivity;

public class SelectDeviceActivity extends Activity implements OnClickListener {
	TextView name, time, detail;
	private Button rightbButton;
	private View leftbutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_device);
		initview();
	}

	public void initview() {
		leftbutton = ((Button) findViewById(R.id.ivTitleBtnLeft));
		leftbutton.setOnClickListener(this);
		rightbButton = ((Button) findViewById(R.id.ivTitleBtnRigh));
		rightbButton.setVisibility(View.GONE);
		leftbutton.setBackgroundResource(R.drawable.icon_back);
		TextView title = (TextView) findViewById(R.id.ivTitleName);
		title.setText("设备选择");

		findViewById(R.id.buttonrela).setOnClickListener(this);
		findViewById(R.id.RelativeLayout01).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.ivTitleBtnLeft) {
			finish();
		} else {
			startActivity(new Intent(getBaseContext(), ShitActivity.class));
		}

	}

}
