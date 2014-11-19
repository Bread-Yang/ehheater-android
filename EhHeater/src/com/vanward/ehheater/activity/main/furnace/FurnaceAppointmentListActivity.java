package com.vanward.ehheater.activity.main.furnace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class FurnaceAppointmentListActivity extends EhHeaterBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_appointment_list);
		setTopText(R.string.appointment);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButtonBackground(R.drawable.icon_add);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {

	}

	private void setListener() {
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FurnaceAppointmentListActivity.this,
						FurnaceAppointmentTimeActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	private void init() {

	}
}
