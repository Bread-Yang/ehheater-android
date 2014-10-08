package com.vanward.ehheater.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.CustomSetVo;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class SureDelDialogUtil {

	int defaultCheck = 0;

	public SureDelDialogUtil setDefaultCheck(int defaultCheck) {
		this.defaultCheck = defaultCheck;
		return this;
	}

	private static Context context;
	private Dialog setting;
	private static SureDelDialogUtil model;

	private SureDelDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall, lastButtonCall;

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public SureDelDialogUtil setNextButtonCall(NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
		return this;
	}

	public static SureDelDialogUtil instance(Context context) {
		if (model == null) {
			model = new SureDelDialogUtil(context);
		}
		SureDelDialogUtil.context = context;
		return model;
	}

	public SureDelDialogUtil nextButtonCall(NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public SureDelDialogUtil lastButtonCall(NextButtonCall lastButtonCall) {
		setLastButtonCall(lastButtonCall);
		return this;
	}

	public void dissmiss() {
		if (setting != null && setting.isShowing()) {
			setting.dismiss();
		}
	}

	public void showDialog() {
		setting = new Dialog(context, R.style.custom_dialog);
		setting.setContentView(R.layout.dialog_sure_del_setting);
		setting.findViewById(R.id.btn_confirm).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						nextButtonCall.oncall(v);
					}
				});

		setting.findViewById(R.id.diss).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dissmiss();
					}
				});
		setting.show();
	}

}
