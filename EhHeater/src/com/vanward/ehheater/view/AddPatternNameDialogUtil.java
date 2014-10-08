package com.vanward.ehheater.view;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.main.CustomSetVo;
import com.vanward.ehheater.activity.main.MainActivity;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.vanward.ehheater.view.wheelview.WheelView;
import com.vanward.ehheater.view.wheelview.adapters.AbstractWheelTextAdapter;
import com.vanward.ehheater.view.wheelview.adapters.NumericWheelAdapter;

public class AddPatternNameDialogUtil {

	int num;

	int defaultCheck = 0;

	public AddPatternNameDialogUtil setDefaultCheck(int defaultCheck) {
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
	private static AddPatternNameDialogUtil model;
	String name;

	public String getName() {
		name = ((EditText) setting.findViewById(R.id.name)).getText()
				.toString();
		return name;
	}

	public boolean isNameExit() {
		List<CustomSetVo> list = new BaseDao(context).getDb().findAll(
				CustomSetVo.class);
		boolean flag = false;
		for (int i = 0; i < list.size(); i++) {
			if (name.equals(list.get(i).getName())) {
				flag = true;
			}
		}
		return flag;

	}

	private AddPatternNameDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall, lastButtonCall;

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public AddPatternNameDialogUtil setNextButtonCall(
			NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
		return this;
	}

	public static AddPatternNameDialogUtil instance(Context context) {
		if (model == null) {
			model = new AddPatternNameDialogUtil(context);
		}
		AddPatternNameDialogUtil.context = context;
		return model;
	}

	public AddPatternNameDialogUtil nextButtonCall(NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public AddPatternNameDialogUtil lastButtonCall(NextButtonCall lastButtonCall) {
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
		setting.setContentView(R.layout.dialog_add_pattern_name);
		setting.findViewById(R.id.diss).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dissmiss();
					}
				});

		setting.findViewById(R.id.btn_confirm).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						nextButtonCall.oncall(v);
					}
				});
		setting.show();
	}
}
