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
import com.vanward.ehheater.view.SeekBarHint.OnSeekBarHintProgressChangeListener;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class BathSettingDialogUtil implements OnSeekBarChangeListener {

	int num;

	int defaultCheck = 0;

	public BathSettingDialogUtil setDefaultCheck(int defaultCheck) {
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
	private static BathSettingDialogUtil model;
	SeekBarHint seekBarwater, seekBartem;
	int waterttv, temtv;

	private BathSettingDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall, lastButtonCall;

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public void setNextButtonCall(NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
	}

	public static BathSettingDialogUtil instance(Context context) {
		if (model == null) {
			model = new BathSettingDialogUtil(context);
		}
		BathSettingDialogUtil.context = context;
		return model;
	}

	public BathSettingDialogUtil nextButtonCall(NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public BathSettingDialogUtil lastButtonCall(NextButtonCall lastButtonCall) {
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
				.setContentView(R.layout.dialog_bath_setting);
		seekBarwater = (SeekBarHint) dialog_morning_wash_number_setting
				.findViewById(R.id.seekBarwater);
		seekBartem = (SeekBarHint) dialog_morning_wash_number_setting
				.findViewById(R.id.seekBartem);

		temtv = 35;
		waterttv = 1;
		seekBartem.setOnSeekBarChangeListener(this);
		seekBarwater.setOnSeekBarChangeListener(this);
		BathSettingVo bathSettingVo = new BaseDao(context).getDb().findById(1,
				BathSettingVo.class);
		if (bathSettingVo != null) {
			seekBartem.setProgress(bathSettingVo.getTem() - 35);
			seekBarwater.setProgress(bathSettingVo.getWater());
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
						BathSettingVo bathSettingVo = new BathSettingVo(1 + "",
								waterttv, temtv);
						new BaseDao(context).getDb().replace(bathSettingVo);
						nextButtonCall.oncall(v);
						dissmiss();
					}
				});
		dialog_morning_wash_number_setting.show();

		seekBartem
				.setOnProgressChangeListener(new OnSeekBarHintProgressChangeListener() {

					@Override
					public String onHintTextChanged(SeekBarHint seekBarHint,
							int progress) {

						int temp = progress + 35;

						if (48 >= temp)
							;
						else if (49 == temp)
							temp = 48;
						else if (50 <= temp && 53 > temp)
							temp = 50;
						else if (53 <= temp && 58 > temp)
							temp = 55;
						else if (58 <= temp && 63 > temp)
							temp = 60;
						else if (63 <= temp && 65 >= temp)
							temp = 65;

						temtv = temp;

						return String.format("%s℃", temp);
					}
				});

		seekBarwater
				.setOnProgressChangeListener(new OnSeekBarHintProgressChangeListener() {

					@Override
					public String onHintTextChanged(SeekBarHint seekBarHint,
							int progress) {
						waterttv = progress;
						if (progress == 0) {
							progress = 1;
						}
						return (progress * 10) + "L";
					}
				});
	}

	@Override
	public void onProgressChanged(SeekBar seekbar, int value, boolean arg2) {
		// if (seekbar.equals(seekBartem)) {
		//
		// int temp = value + 35;
		//
		// if (48 >= temp)
		// ;
		// else if (49 == temp)
		// temp = 48;
		// else if (50 <= temp && 53 > temp)
		// temp = 50;
		// else if (53 <= temp && 58 > temp)
		// temp = 55;
		// else if (58 <= temp && 63 > temp)
		// temp = 60;
		// else if (63 <= temp && 65 >= temp)
		// temp = 65;
		//
		// temtv = temp;
		// } else if (seekbar.equals(seekBarwater)) {
		// if (value == 0) {
		// seekBarwater.setProgress(1);
		// } else {
		// waterttv = value;
		// }
		// }

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {

	}

	public static class BathSettingVo {
		@Id
		String nconnid;
		int water;
		int tem;

		public BathSettingVo() {
		};

		public BathSettingVo(String nconnid, int water, int tem) {
			this.nconnid = nconnid;
			this.water = water;
			this.tem = tem;
		}

		public String getNconnid() {
			return nconnid;
		}

		public void setNconnid(String nconnid) {
			this.nconnid = nconnid;
		}

		public int getWater() {
			return water;
		}

		public void setWater(int water) {
			this.water = water;
		}

		public int getTem() {
			return tem;
		}

		public void setTem(int tem) {
			this.tem = tem;
		}

	}
}
