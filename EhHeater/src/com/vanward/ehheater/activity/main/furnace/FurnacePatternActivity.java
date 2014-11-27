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
		init();
		setListener();
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
							FurnaceSendMsgModel.setToNormalHeating();
							break;
						case R.id.rb_mode_outdoor:
							FurnaceSendMsgModel.setToOutdoorHeating();
							// 供暖温度30℃（散热器与地暖一样）；可以设置温度，但不支持温度调节，不管设置多少温度，都是以默认30℃运行
							FurnaceSendMsgModel.setHeatingTemperature(30);
							break;
						case R.id.rb_mode_night:
							FurnaceSendMsgModel.setToNightHeating();
							// 温度自动转为当前设置温度的80%；如当前设置60℃，当你按下夜间模式符号后，温度自动转为48℃；设置的温度可以调节。
							// App直接发当前设置温度即可
							int temp = (int) (FurnaceMainActivity.statusResp
									.getHeatingTemTarget());
							if (temp < 30) {
								temp = 30;
							}
							FurnaceSendMsgModel
							.setHeatingTemperature(temp);
							break;
						}
						finish();
					}
				});

		rg_bath_mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				switch (checkedId) {
				case R.id.rb_mode_comfort:
					FurnaceSendMsgModel.setToComfortBath();
					break;
				case R.id.rb_mode_normal:
					FurnaceSendMsgModel.setToNormalBath();
					break;
				}
				finish();
			}
		});
	}

	private void init() {
		int seasonTag = getIntent().getIntExtra("seasonMode",
				FurnaceSeasonActivity.SET_SUMMER_MODE);
		if (seasonTag == FurnaceSeasonActivity.SET_SUMMER_MODE) {
			llt_heating_mode.setVisibility(View.GONE);
		}

		String bathMode = getIntent().getStringExtra("bathMode");
		if ("normal".equals(bathMode)) {
			rg_bath_mode.check(R.id.rb_mode_normal);
		} else {
			rg_bath_mode.check(R.id.rb_mode_comfort);
		}

		String heatingMode = getIntent().getStringExtra("heatingMode");
		if ("normal".equals(heatingMode)) {
			rg_heating_mode.check(R.id.rb_model_default);
		} else if ("night".equals(heatingMode)) {
			rg_heating_mode.check(R.id.rb_mode_night);
		} else if ("outdoor".equals(heatingMode)) {
			rg_heating_mode.check(R.id.rb_mode_outdoor);
		}
	}
}
