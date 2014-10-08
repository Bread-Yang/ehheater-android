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

public class AddPatternSettingDialogUtil implements OnSeekBarChangeListener,
		OnCheckedChangeListener {

	int num;

	int defaultCheck = 0;

	TextView value;

	public AddPatternSettingDialogUtil setDefaultCheck(int defaultCheck) {
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
	private static AddPatternSettingDialogUtil model;

	private AddPatternSettingDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall, lastButtonCall;

	private SeekBar seekBar;
	RadioGroup peopleradioGroup;

	private RadioGroup radioGroup, peopleRadioGroup;

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public void setNextButtonCall(NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
	}

	public static AddPatternSettingDialogUtil instance(Context context) {
		if (model == null) {
			model = new AddPatternSettingDialogUtil(context);
		}
		AddPatternSettingDialogUtil.context = context;
		return model;
	}

	public AddPatternSettingDialogUtil nextButtonCall(
			NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public AddPatternSettingDialogUtil lastButtonCall(
			NextButtonCall lastButtonCall) {
		setLastButtonCall(lastButtonCall);
		return this;
	}

	public void dissmiss() {
		if (setting != null && setting.isShowing()) {
			setting.dismiss();
		}
	}

	String name = "";

	public AddPatternSettingDialogUtil initName(String name) {
		setting = new Dialog(context, R.style.custom_dialog);
		setting.setContentView(R.layout.dialog_add_pattern_setting);
		((TextView) setting.findViewById(R.id.tv_order_title)).setText(name);
		this.name = name;
		radioGroup = (RadioGroup) setting.findViewById(R.id.radioGroup1);
		peopleRadioGroup = (RadioGroup) setting
				.findViewById(R.id.power_radiogroup);
		peopleRadioGroup.setOnCheckedChangeListener(this);

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
		((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
		seekBar = (SeekBar) setting.findViewById(R.id.seekBar1);
		peopleradioGroup = (RadioGroup) setting
				.findViewById(R.id.power_radiogroup);

		value = (TextView) setting.findViewById(R.id.textView2);
		CustomSetVo customSetVo = new BaseDao(context).getDb().findById(name,
				CustomSetVo.class);
		seekBar.setOnSeekBarChangeListener(this);
		if (customSetVo != null) {
			seekBar.setProgress(customSetVo.getTempter() - 35);
			((RadioButton) radioGroup.getChildAt(customSetVo.getPower() - 1))
					.setChecked(true);
			((RadioButton) peopleradioGroup.getChildAt(customSetVo
					.getPeoplenum())).setChecked(true);
		}

		setting.findViewById(R.id.btn_confirm).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						nextButtonCall.oncall(v);
					}
				});

		setting.show();
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		value.setText(arg1 + 35 + "℃");
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	public void setUI(boolean flag, int people) {
		seekBar.setEnabled(flag);
		radioGroup.setEnabled(flag);
		TextView textView = ((TextView) setting.findViewById(R.id.textView1));
		TextView textView2 = ((TextView) setting.findViewById(R.id.textView3));
		if (flag) {
			textView.setTextColor(context.getResources()
					.getColor(R.color.white));
			textView2.setTextColor(context.getResources().getColor(
					R.color.white));
		} else {
			textView.setTextColor(context.getResources().getColor(
					R.color.an_hui));
			textView2.setTextColor(context.getResources().getColor(
					R.color.an_hui));
		}

		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			radioGroup.getChildAt(i).setEnabled(flag);
		}
		 try {
		 ((RadioButton) radioGroup.getChildAt(people - 1)).setChecked(true);
		 } catch (Exception e) {
		 // TODO: handle exception
		 }
		
		 if (people == 1) {
		 seekBar.setProgress(10);
		 } else if (people == 2) {
		 seekBar.setProgress(20);
		 } else if (people == 3) {
		 seekBar.setProgress(30);
		 }
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		if (arg1 == R.id.RadioButton04) {
			// 无
			setUI(true, 0);
		} else if (arg1 == R.id.RadioButton03) {
			setUI(false, 1);
			// 1人
		} else if (arg1 == R.id.RadioButton02) {
			setUI(false, 3);

			// 3人

		} else if (arg1 == R.id.RadioButton01) {
			setUI(false, 2);
			// 2人

		}

	}
}
