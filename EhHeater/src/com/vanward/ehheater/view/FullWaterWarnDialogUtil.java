package com.vanward.ehheater.view;

import java.util.List;

import net.tsz.afinal.annotation.sqlite.Id;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.MoringSeVo;
import com.vanward.ehheater.activity.main.gas.SendMsgModel;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class FullWaterWarnDialogUtil {

	int num;

	int defaultCheck = 0;

	public FullWaterWarnDialogUtil setDefaultCheck(int defaultCheck) {
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
	public Dialog getDialog() {
		return dialog_morning_wash_number_setting;
	}

	private static FullWaterWarnDialogUtil model;
	SeekBar seekBarwater, seekBartem;
	TextView waterttv, temtv;

	private FullWaterWarnDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall, lastButtonCall;

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public void setNextButtonCall(NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
	}

	public static FullWaterWarnDialogUtil instance(Context context) {
		if (model == null) {
			model = new FullWaterWarnDialogUtil(context);
		}
		FullWaterWarnDialogUtil.context = context;
		return model;
	}

	public FullWaterWarnDialogUtil nextButtonCall(NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public FullWaterWarnDialogUtil lastButtonCall(NextButtonCall lastButtonCall) {
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
				.setContentView(R.layout.dialog_full_water_warn);

		dialog_morning_wash_number_setting.findViewById(R.id.btn_error_handle)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dissmiss();
					}
				});
		dialog_morning_wash_number_setting.show();
	}

}
