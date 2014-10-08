package com.vanward.ehheater.activity.appointment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class AppointmentDaysActivity extends EhHeaterBaseActivity implements
		OnClickListener {

	private int[] days;

	private CheckBox cb_Monday, cb_Tuesday, cb_Wednesday, cb_Thursday,
			cb_Friday, cb_Saturday, cb_Sunday;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_appointment_days);
		setTopText(R.string.repeate);
		setRightButton(View.INVISIBLE);
		findViewById();
		setListener();
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
	}

	private void setListener() {
		btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("days", days);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void init() {
		Intent intent = getIntent();
		days = intent.getIntArrayExtra("days");
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
		}
	}

}
