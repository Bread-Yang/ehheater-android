package com.vanward.ehheater.activity.main.furnace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class FurnaceSeasonActivity extends EhHeaterBaseActivity {

	public static int SET_SUMMER_MODE = 0;
	public static int SET_WINNER_MODE = 1;

	private RadioGroup rg_mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_season);
		setTopText(R.string.mode);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.GONE);
		findViewById();
		init();
		setListener();
	}

	private void findViewById() {
		rg_mode = (RadioGroup) findViewById(R.id.rg_winner);
	}

	private void setListener() {
		rg_mode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				Intent intent = new Intent();
				switch (checkedId) {
				case R.id.rb_mode_summer:
					FurnaceSendMsgModel.setToSummerMode();
					intent.putExtra("seasonMode", SET_SUMMER_MODE);
					setResult(RESULT_OK, intent);
					break;
				case R.id.rb_mode_winner:
					FurnaceSendMsgModel.setToWinnerMode();
					intent.putExtra("seasonMode", SET_WINNER_MODE);
					setResult(RESULT_OK, intent);
					break;
				}
			}
		});

	}

	private void init() {
		if (getIntent() != null) {
			if (FurnaceSeasonActivity.SET_SUMMER_MODE == getIntent()
					.getIntExtra("seasonMode",
							FurnaceSeasonActivity.SET_SUMMER_MODE)) {
				rg_mode.check(R.id.rb_mode_summer);
			} else {
				rg_mode.check(R.id.rb_mode_winner);
			}
		}
	}
}
