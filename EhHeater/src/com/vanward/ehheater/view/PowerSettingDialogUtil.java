package com.vanward.ehheater.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.vanward.ehheater.R;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class PowerSettingDialogUtil {

	int power;

	public int getPower() {
		return power;
	}

	private static Context context;
	private Dialog dialog_power_setting;
	private static PowerSettingDialogUtil model;

	private PowerSettingDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall;

	public PowerSettingDialogUtil setNextButtonCall(
			NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
		return this;
	}

	public static PowerSettingDialogUtil instance(Context context) {
		if (model == null) {
			model = new PowerSettingDialogUtil(context);
		}
		PowerSettingDialogUtil.context = context;
		return model;
	}

	public PowerSettingDialogUtil nextButtonCall(NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public void dissmiss() {
		if (dialog_power_setting != null && dialog_power_setting.isShowing()) {
			dialog_power_setting.dismiss();
		}

	}

	public void showDialog(int defaultnum) {
		System.out.println("int defaultnum: " + defaultnum);
		dialog_power_setting = new Dialog(context, R.style.custom_dialog);
		dialog_power_setting.setContentView(R.layout.dialog_power_setting);

		final RadioGroup rGroup = (RadioGroup) dialog_power_setting
				.findViewById(R.id.radiogroup);
		try {
			((RadioButton) rGroup.getChildAt(defaultnum - 1)).setChecked(true);
		} catch (Exception e) {
			// TODO: handle exception
		}

		dialog_power_setting.findViewById(R.id.btn_confirm).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						for (int i = 0; i < rGroup.getChildCount(); i++) {
							if (rGroup.getCheckedRadioButtonId() == rGroup
									.getChildAt(i).getId()) {
								power = i + 1;
							}
						}
						nextButtonCall.oncall(arg0);
					}
				});

		dialog_power_setting.findViewById(R.id.diss).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dissmiss();

					}
				});

		dialog_power_setting.show();
	}
}
