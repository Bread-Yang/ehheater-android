package com.vanward.ehheater.activity.main.furnace;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.xtremeprog.xpgconnect.generated.DERYStatusResp_t;

public class FurnaceSeasonActivity extends EhHeaterBaseActivity {

	public static int SET_SUMMER_MODE = 0;
	public static int SET_WINNER_MODE = 1;

	private RadioGroup rg_mode;

	private Dialog summer_dialog, winner_dialog;

	private boolean isCheckByHand;

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
				if (!isCheckByHand) {
					Intent intent = new Intent();
					switch (checkedId) {
					case R.id.rb_mode_summer:
						isCheckByHand = true;
						rg_mode.check(R.id.rb_mode_winner);
						isCheckByHand = false;
						summer_dialog.show();
						break;
					case R.id.rb_mode_winner:
						isCheckByHand = true;
						rg_mode.check(R.id.rb_mode_summer);
						isCheckByHand = false;
						winner_dialog.show();
						break;
					}
				}
			}
		});

	}

	private void init() {
		summer_dialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.switch_summer,
						BaoDialogShowUtil.DEFAULT_RESID,
						BaoDialogShowUtil.DEFAULT_RESID, null,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								FurnaceSendCommandService.getInstance()
										.setToSummerMode();
								intent.putExtra("seasonMode", SET_SUMMER_MODE);
								// setResult(RESULT_OK, intent);
								summer_dialog.dismiss();
							}
						});

		winner_dialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.switch_winner,
						BaoDialogShowUtil.DEFAULT_RESID,
						BaoDialogShowUtil.DEFAULT_RESID, null,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								FurnaceSendCommandService.getInstance()
										.setToWinnerMode();
								intent.putExtra("seasonMode", SET_WINNER_MODE);
								// setResult(RESULT_OK, intent);
								winner_dialog.dismiss();
							}
						});

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

	@Override
	public void OnDERYStatusResp(DERYStatusResp_t pResp, int nConnId) {
		super.OnDERYStatusResp(pResp, nConnId);

		isCheckByHand = true;
		if (pResp.getSeasonState() == 0) {
			rg_mode.check(R.id.rb_mode_summer);
		} else {
			rg_mode.check(R.id.rb_mode_winner);
		}
		isCheckByHand = false;
	}
}
