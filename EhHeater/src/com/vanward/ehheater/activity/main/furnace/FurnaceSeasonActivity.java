package com.vanward.ehheater.activity.main.furnace;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class FurnaceSeasonActivity extends EhHeaterBaseActivity {

	private RadioGroup rg_mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_season);
		setTopText(R.string.mode);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.GONE);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		rg_mode = (RadioGroup) findViewById(R.id.rg_winner);
	}

	private void setListener() {
		rg_mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				switch (checkedId) {
				case R.id.rb_mode_summer:

					break;
				case R.id.rb_mode_winner:

					break;
				}
			}
		});

	}

	private void init() {

	}
}
