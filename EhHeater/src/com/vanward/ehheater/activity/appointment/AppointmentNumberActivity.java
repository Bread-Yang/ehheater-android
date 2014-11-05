package com.vanward.ehheater.activity.appointment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class AppointmentNumberActivity extends EhHeaterBaseActivity implements
		OnClickListener {

	private int number;
	private RadioGroup rg_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment_number);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		rg_number = (RadioGroup) findViewById(R.id.rg_number);
	}

	private void setListener() {
		btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("number", number);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		rg_number.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_one:
					number = 1;
					break;
				case R.id.rb_two:
					number = 2;
					break;
				case R.id.rb_three:
					number = 3;
					break;
				}
			}
		});
	}

	private void init() {
		Intent intent = getIntent();
		number = intent.getIntExtra("number", 2);
		if (number == 1) {
			rg_number.check(R.id.rb_one);
		}
		if (number == 2) {
			rg_number.check(R.id.rb_two);
		}
		if (number == 3) {
			rg_number.check(R.id.rb_three);
		}
	}

	@Override
	public void onClick(View v) {
		int resId = v.getId();
		int isCheck = ((CheckBox) v).isChecked() ? 1 : 0;
		switch (resId) {
		}
	}

}
