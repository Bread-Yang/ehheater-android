package com.vanward.ehheater.activity.main.furnace;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class FurnacePatternActivity extends EhHeaterBaseActivity {

	private LinearLayout llt_heating_mode, llt_bath_mode;

	private RadioGroup rg_heating_mode, rg_bath_mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_pattern);
		setTopText(R.string.mode);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.GONE);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		llt_heating_mode = (LinearLayout) findViewById(R.id.llt_heating_mode);
		llt_bath_mode = (LinearLayout) findViewById(R.id.llt_bath_mode);
		rg_heating_mode = (RadioGroup) findViewById(R.id.rg_heating_mode);
		rg_bath_mode = (RadioGroup) findViewById(R.id.rg_bath_mode);
	}

	private void setListener() {
		rg_heating_mode
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup arg0, int checkedId) {
						switch (checkedId) {
						case R.id.rb_model_default:

							break;
						case R.id.rb_mode_outdoor:

							break;
						case R.id.rb_mode_night:

							break;
						}
					}
				});

		rg_bath_mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				switch (checkedId) {
				case R.id.rb_model_comfort:

					break;
				case R.id.rb_mode_bath:

					break;
				}
			}
		});
	}

	private void init() {

	}
}
