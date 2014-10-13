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
import com.vanward.ehheater.dao.BaseDao;
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
	SeekBar seekBarwater, seekBartem;
	TextView waterttv, temtv;

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
		seekBarwater = (SeekBar) dialog_morning_wash_number_setting
				.findViewById(R.id.seekBar1);
		seekBartem = (SeekBar) dialog_morning_wash_number_setting
				.findViewById(R.id.SeekBar01);

		waterttv = (TextView) dialog_morning_wash_number_setting
				.findViewById(R.id.textView2);
		temtv = (TextView) dialog_morning_wash_number_setting
				.findViewById(R.id.TextView01);
		temtv.setTag(35);
		waterttv.setTag(10);
		seekBartem.setOnSeekBarChangeListener(this);
		seekBarwater.setOnSeekBarChangeListener(this);
		BathSettingVo bathSettingVo = new BaseDao(context).getDb().findById(1,
				BathSettingVo.class);
		if (bathSettingVo != null) {
			seekBartem.setProgress(bathSettingVo.getTem());
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
								(Integer) waterttv.getTag(), (Integer) temtv
										.getTag());
						new BaseDao(context).getDb().replace(bathSettingVo);
						dissmiss();
					}
				});
		dialog_morning_wash_number_setting.show();
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		if (arg0.equals(seekBartem)) {
			temtv.setText((arg1 + 35) + "℃");
			temtv.setTag(arg1);
		} else if (arg0.equals(seekBarwater)) {
			waterttv.setText((arg1 * 10) + "L");
			waterttv.setTag(arg1);
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

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
