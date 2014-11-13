package com.vanward.ehheater.view;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.main.MoringSeVo;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class WashNumDialogUtil {

	int num;

	int defaultCheck = 0;

	public WashNumDialogUtil setDefaultCheck(int defaultCheck) {
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
	private static WashNumDialogUtil model;

	private WashNumDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall, lastButtonCall;

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public void setNextButtonCall(NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
	}

	public static WashNumDialogUtil instance(Context context) {
		if (model == null) {
			model = new WashNumDialogUtil(context);
		}
		WashNumDialogUtil.context = context;
		return model;
	}

	public WashNumDialogUtil nextButtonCall(NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public WashNumDialogUtil lastButtonCall(NextButtonCall lastButtonCall) {
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
				.setContentView(R.layout.dialog_morning_wash_number_setting);
		final RadioGroup radioGroup = (RadioGroup) dialog_morning_wash_number_setting
				.findViewById(R.id.readiogroup);
		((RadioButton)radioGroup.getChildAt(defaultCheck)).setChecked(true);
		MoringSeVo moringSeVo = new BaseDao(context).getDb().findById("1",
				MoringSeVo.class);

		if (moringSeVo != null) {
			System.out.println("people: " + moringSeVo.getPeople());
			try {
				((RadioButton) radioGroup
						.getChildAt(moringSeVo.getPeople() - 1))
						.setChecked(true);
			} catch (Exception e) {
				// TODO: handle exception

			}
		}

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
						for (int i = 0; i < radioGroup.getChildCount(); i++) {
							if (radioGroup.getCheckedRadioButtonId() == radioGroup
									.getChildAt(i).getId()) {
								num = i + 1;
							}
						}
						System.out.println("选择了：" + num);
						nextButtonCall.oncall(v);
					}
				});
		dialog_morning_wash_number_setting.show();
	}
}
