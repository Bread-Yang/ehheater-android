package com.vanward.ehheater.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class AddPatternButtonDialogUtil {

	int num;

	int defaultCheck = 0;

	public AddPatternButtonDialogUtil setDefaultCheck(int defaultCheck) {
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
	private static AddPatternButtonDialogUtil model;
	String name;

	private AddPatternButtonDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall, lastButtonCall, editButtonListener;

	public void setEditButtonListener(NextButtonCall editButtonListener) {
		this.editButtonListener = editButtonListener;
	}

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public AddPatternButtonDialogUtil setNextButtonCall(
			NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
		return this;
	}

	public static AddPatternButtonDialogUtil instance(Context context) {
		if (model == null) {
			model = new AddPatternButtonDialogUtil(context);
		}
		AddPatternButtonDialogUtil.context = context;
		return model;
	}

	public AddPatternButtonDialogUtil nextButtonCall(
			NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public AddPatternButtonDialogUtil lastButtonCall(
			NextButtonCall lastButtonCall) {
		setLastButtonCall(lastButtonCall);
		return this;
	}

	public AddPatternButtonDialogUtil editButtonCall(
			NextButtonCall lastButtonCall) {
		setEditButtonListener(lastButtonCall);
		return this;
	}

	public void dissmiss() {
		if (setting != null && setting.isShowing()) {
			setting.dismiss();
		}

	}

	public void showDialog() {
		setting = new Dialog(context, R.style.custom_dialog);
		setting.setContentView(R.layout.dialog_add_pattern_button);
		setting.findViewById(R.id.diss).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dissmiss();
					}
				});

		setting.findViewById(R.id.rename).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						nextButtonCall.oncall(v);
					}
				});

		setting.findViewById(R.id.edit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						editButtonListener.oncall(v);
					}
				});

		setting.findViewById(R.id.delete).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						lastButtonCall.oncall(v);
					}
				});
		if (!((Activity) context).isFinishing()) {
			setting.show();
		}

	}
}
