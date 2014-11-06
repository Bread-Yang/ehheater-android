package com.vanward.ehheater.view;

import java.util.List;

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
import com.vanward.ehheater.activity.main.gas.GasCustomSetVo;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class AddPatternGasSettingDialogUtil implements OnSeekBarChangeListener,
		OnCheckedChangeListener {

	int num;

	int defaultCheck = 0;

	TextView value;

	public AddPatternGasSettingDialogUtil setDefaultCheck(int defaultCheck) {
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
	private static AddPatternGasSettingDialogUtil model;

	private AddPatternGasSettingDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall, lastButtonCall;

	private SeekBar seekBar;

	private RadioGroup radioGroup;

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public void setNextButtonCall(NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
	}

	public static AddPatternGasSettingDialogUtil instance(Context context) {
		if (model == null) {
			model = new AddPatternGasSettingDialogUtil(context);
		}
		AddPatternGasSettingDialogUtil.context = context;
		return model;
	}

	public AddPatternGasSettingDialogUtil nextButtonCall(
			NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public AddPatternGasSettingDialogUtil lastButtonCall(
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

	private int customSetVoId;

	public AddPatternGasSettingDialogUtil initName(String name,
			int customSetVoId) {
		setting = new Dialog(context, R.style.custom_dialog);
		setting.setContentView(R.layout.dialog_add_gas_pattern_setting);
		((TextView) setting.findViewById(R.id.tv_order_title)).setText(name);
		this.name = name;
		this.customSetVoId = customSetVoId;
		radioGroup = (RadioGroup) setting.findViewById(R.id.radioGroup1);

		return this;
	}

	public GasCustomSetVo getData() {
		GasCustomSetVo customSetVo = new GasCustomSetVo();
		customSetVo.setConnid(Global.connectId);
		customSetVo.setName(name);
		customSetVo.setTempter(seekBar.getProgress() + 35);
		List list = new BaseDao(context).getDb().findAll(GasCustomSetVo.class);
		customSetVo.setId(list.size() + 1);
		int waterval = 0;
		View view = null;
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			if (radioGroup.getCheckedRadioButtonId() == radioGroup
					.getChildAt(i).getId()) {
				view = radioGroup.getChildAt(i);
			}
		}
		System.out.println("waterval:　" + view.getTag());
		customSetVo.setWaterval(Integer.parseInt((String) view.getTag()));
		return customSetVo;
	}

	public void showDialog() {
		((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
		seekBar = (SeekBar) setting.findViewById(R.id.seekBar1);

		value = (TextView) setting.findViewById(R.id.textView2);
		GasCustomSetVo customSetVo = new BaseDao(context).getDb().findById(
				customSetVoId, GasCustomSetVo.class);
		seekBar.setOnSeekBarChangeListener(this);
		if (customSetVo != null) {
			seekBar.setProgress(customSetVo.getTempter() - 35);

			for (int i = 0; i < radioGroup.getChildCount(); i++) {
				int value = Integer.parseInt((String) radioGroup.getChildAt(i)
						.getTag());
				if (value == customSetVo.getWaterval()) {
					((RadioButton) radioGroup.getChildAt(i)).setChecked(true);
				}

			}
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

		int position=arg0.getProgress();
		int temp= position + 35;

		if (48>=temp) 
			;
		else if(49 ==temp)
			temp = 48;
		else if(50 <=temp&& 53>temp)
			temp  = 50;
		else if(53<=temp&&58>temp)
			temp  = 55;
		else if(58<=temp&&63>temp)
			temp  = 60;
		else if(63<=temp&&65>=temp)
			temp  = 65;
		
		
		value.setText(temp + "℃");
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		seekBar.setProgress(heatmakeRange(arg0.getProgress() + 35) - 35);
	}

	public int heatmakeRange(int value) {
		int index = 0;
		if (value == 49) {
			index = 50;
		} else if (value > 49) {
			int[] rangs = { 50, 55, 60, 65 };
			for (int i = 0; i < rangs.length; i++) {
				if (Math.abs(value - rangs[i]) <= 3) {
					index = rangs[i];
					break;
				}
			}
		} else {
			index = value;
		}
		return index;
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
