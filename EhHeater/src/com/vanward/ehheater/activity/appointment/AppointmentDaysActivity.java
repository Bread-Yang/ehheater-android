package com.vanward.ehheater.activity.appointment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class AppointmentDaysActivity extends Activity implements
		OnClickListener {

	private int[] days;

	private CheckBox cb_Monday, cb_Tuesday, cb_Wednesday, cb_Thursday,
			cb_Friday, cb_Saturday, cb_Sunday;

	@ViewInject(id = R.id.ivTitleName, click = "onClick")
	TextView ivTitleName;
	@ViewInject(id = R.id.ivTitleBtnLeft, click = "onClick")
	Button ivTitleBtnLeft;
	@ViewInject(id = R.id.ivTitleBtnRigh, click = "onClick")
	Button ivTitleBtnRigh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment_days);
		FinalActivity.initInjectedView(this);
		findViewById();
		init();
	}

	private void findViewById() {
		cb_Monday = (CheckBox) findViewById(R.id.cb_Monday);
		cb_Tuesday = (CheckBox) findViewById(R.id.cb_Tuesday);
		cb_Wednesday = (CheckBox) findViewById(R.id.cb_Wednesday);
		cb_Thursday = (CheckBox) findViewById(R.id.cb_Thursday);
		cb_Friday = (CheckBox) findViewById(R.id.cb_Friday);
		cb_Saturday = (CheckBox) findViewById(R.id.cb_Saturday);
		cb_Sunday = (CheckBox) findViewById(R.id.cb_Sunday);
		ivTitleName.setText("重复");
		ivTitleBtnLeft.setBackgroundResource(R.drawable.icon_back);
		ivTitleBtnLeft.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("days", days);
				setResult(RESULT_OK, intent);
				finish();				
			}
		});
		ivTitleBtnRigh.setBackgroundResource(R.drawable.icon_add);
		ivTitleBtnRigh.setVisibility(View.GONE);
	}


	private void init() {
		days = getIntent().getIntArrayExtra("days");
		if (days == null) {
			days = new int[7];
		}
		if (days[0] == 1) {
			cb_Monday.setChecked(true);
		}
		if (days[1] == 1) {
			cb_Tuesday.setChecked(true);
		}
		if (days[2] == 1) {
			cb_Wednesday.setChecked(true);
		}
		if (days[3] == 1) {
			cb_Thursday.setChecked(true);
		}
		if (days[4] == 1) {
			cb_Friday.setChecked(true);
		}
		if (days[5] == 1) {
			cb_Saturday.setChecked(true);
		}
		if (days[6] == 1) {
			cb_Sunday.setChecked(true);
		}
	}

	@Override
	public void onClick(View v) {
		int resId = v.getId();
		int isCheck = ((CheckBox) v).isChecked() ? 1 : 0;
		switch (resId) {
		case R.id.cb_Monday:
			days[0] = isCheck;
			break;
		case R.id.cb_Tuesday:
			days[1] = isCheck;
			break;
		case R.id.cb_Wednesday:
			days[2] = isCheck;
			break;
		case R.id.cb_Thursday:
			days[3] = isCheck;
			break;
		case R.id.cb_Friday:
			days[4] = isCheck;
			break;
		case R.id.cb_Saturday:
			days[5] = isCheck;
			break;
		case R.id.cb_Sunday:
			days[6] = isCheck;
			break;
		case R.id.ivTitleBtnLeft:
			
			break;

		}
	}

}
