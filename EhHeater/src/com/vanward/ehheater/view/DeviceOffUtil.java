package com.vanward.ehheater.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class DeviceOffUtil {

	int num;

	int defaultCheck = 0;

	public DeviceOffUtil setDefaultCheck(int defaultCheck) {
		this.defaultCheck = defaultCheck;
		return this;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	private static Context context;
	private Dialog dialog_morning_wash_number_setting;
	private static DeviceOffUtil model;
	SeekBar seekBarwater, seekBartem;
	TextView waterttv, temtv;

	private DeviceOffUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall, lastButtonCall;

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public void setNextButtonCall(NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
	}

	public static DeviceOffUtil instance(Context context) {
		if (model == null) {
			model = new DeviceOffUtil(context);
		}
		DeviceOffUtil.context = context;
		return model;
	}

	public DeviceOffUtil nextButtonCall(NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public DeviceOffUtil lastButtonCall(NextButtonCall lastButtonCall) {
		setLastButtonCall(lastButtonCall);
		return this;
	}

	public void dissmiss() {
		if (dialog_morning_wash_number_setting != null
				&& dialog_morning_wash_number_setting.isShowing()) {
			dialog_morning_wash_number_setting.dismiss();
		}

	}

	public void showDialog() {
		dialog_morning_wash_number_setting = new Dialog(context,
				R.style.custom_dialog);
		dialog_morning_wash_number_setting
				.setContentView(R.layout.dialog_deviceoff_setting);

		dialog_morning_wash_number_setting.findViewById(R.id.diss)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dissmiss();
					}
				});

		dialog_morning_wash_number_setting.findViewById(R.id.btn_confirm)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						nextButtonCall.oncall(v);
					}
				});
		dialog_morning_wash_number_setting.show();
	}

}
