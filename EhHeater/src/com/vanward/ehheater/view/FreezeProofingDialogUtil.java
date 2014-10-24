package com.vanward.ehheater.view;

import u.aly.l;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.CustomSetVo;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class FreezeProofingDialogUtil {

	int num;

	int defaultCheck = 0;

	TextView value;

	public FreezeProofingDialogUtil setDefaultCheck(int defaultCheck) {
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
	private Dialog setting;
	private static FreezeProofingDialogUtil model;

	private FreezeProofingDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall, lastButtonCall;

	private SeekBar seekBar;
	RadioGroup peopleradioGroup;

	private RadioGroup radioGroup, peopleRadioGroup;

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public FreezeProofingDialogUtil setNextButtonCall(
			NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
		return this;
	}

	public static FreezeProofingDialogUtil instance(Context context) {
		if (model == null) {
			model = new FreezeProofingDialogUtil(context);
		}
		FreezeProofingDialogUtil.context = context;
		return model;
	}

	public void dissmiss() {
		if (setting != null && setting.isShowing()) {
			setting.dismiss();
		}
	}

	String name = "";

	public FreezeProofingDialogUtil initName(String name) {
		setting = new Dialog(context, R.style.custom_dialog);
		setting.setContentView(R.layout.dialog_device_error_tips);

		return this;
	}

	public CustomSetVo getData() {
		CustomSetVo customSetVo = new CustomSetVo();
		customSetVo.setConnid(Global.connectId);
		customSetVo.setName(name);
		customSetVo.setTempter(seekBar.getProgress() + 35);
		int num = 0;
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			if (radioGroup.getCheckedRadioButtonId() == radioGroup
					.getChildAt(i).getId()) {
				num = i;
			}
		}
		int peoplenum = 0;
		for (int i = 0; i < peopleRadioGroup.getChildCount(); i++) {
			if (peopleRadioGroup.getCheckedRadioButtonId() == peopleRadioGroup
					.getChildAt(i).getId()) {
				peoplenum = i;
			}
		}
		customSetVo.setPeoplenum(peoplenum);
		customSetVo.setPower(num + 1);
		return customSetVo;
	}

	public void showDialog() {

		setting.findViewById(R.id.btn_error_bypass).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dissmiss();
					}
				});

		setting.findViewById(R.id.btn_error_handle).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						nextButtonCall.oncall(v);
						dissmiss();
					}
				});

		setting.show();
	}

}
