package com.vanward.ehheater.view;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.electric.CustomSetVo;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class ErrorDialogUtil {

	int num;

	int defaultCheck = 0;

	TextView value;

	public ErrorDialogUtil setDefaultCheck(int defaultCheck) {
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
	private static ErrorDialogUtil model;

	public static Map<String, String> getMap() {
		return map;
	}

	public static void setMap(Map<String, String> map) {
		ErrorDialogUtil.map = map;
	}

	static Map<String, String> map = new HashMap<String, String>();

	static Map<String, String> dialogmap = new HashMap<String, String>();

	private ErrorDialogUtil(Context context) {
		this.context = context;
		map.put("1", "进水温度电路故障保护");
		map.put("16", "假火焰故障保护");
		map.put("17", "点火失败故障保护");
		map.put("18", "意外熄火故障保护");
		map.put("19", "温控电器故障保护");
		map.put("32", "热电偶动作保护");
		map.put("64", "风机电路故障保护");
		map.put("80", "出水超温保护");
		map.put("81", "进水超温保护");
		map.put("96", "出水温度电路故障保护");

		map.put("112", "拔码开关选择错误故障保护");

		map.put("226", "热水器未注满水直接通电，发生干烧。切断电源，热水器注满水后，再通电。");
		map.put("227", "中层发热管处传感器故障，请联系客服");
		map.put("228", "加热水温失控超过设定值，请联系客服");
		map.put("229", "下层发热管处传感器故障，请联系客服");
		
	
		map.put("E0","卫浴水输入水温度传感器故障或过热" );
		map.put("E1", "水压故障、缺水/变频水泵故障");
		map.put("E2","点火失败或伪火故障" );
		map.put("E3","取暖水温度传感器故障或过热" );
		map.put("E4","卫浴水温度传感器故障或过热" );
		map.put("E6","风压/风机故障，可调速风机风速故障，烟道温度探头故障" );
		map.put("E7","机械温控器过热保护" );
		map.put("E8","AD采样/12V电压故障");
		map.put("E9","主阀驱动继电器驱动电路故障" );

		dialogmap.put("226", "干烧故障");
		dialogmap.put("227", "传感器故障");
		dialogmap.put("228", "超温故障");
		// dialogmap.put("229", "传感器故障");

	}

	public static Map<String, String> getDialogmap() {
		return dialogmap;
	}

	public static void setDialogmap(Map<String, String> dialogmap) {
		ErrorDialogUtil.dialogmap = dialogmap;
	}

	NextButtonCall nextButtonCall, lastButtonCall;

	private SeekBar seekBar;
	RadioGroup peopleradioGroup;

	private RadioGroup radioGroup, peopleRadioGroup;

	public void setLastButtonCall(NextButtonCall lastButtonCall) {
		this.lastButtonCall = lastButtonCall;
	}

	public ErrorDialogUtil setNextButtonCall(NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
		return this;
	}

	public static ErrorDialogUtil instance(Context context) {
		if (model == null) {
			model = new ErrorDialogUtil(context);
		}
		ErrorDialogUtil.context = context;
		return model;
	}

	public void dissmiss() {
		if (setting != null && setting.isShowing()) {
			setting.dismiss();
		}
	}

	String name = "";

	public ErrorDialogUtil initName(String errorcode) {
		setting = new Dialog(context, R.style.custom_dialog);
		setting.setContentView(R.layout.dialog_device_error_tips);
		TextView title = (TextView) setting.findViewById(R.id.tv_order_title);
		TextView detail = (TextView) setting.findViewById(R.id.tv_detail);
		title.setText("机器故障(" + errorcode + ")");
		System.out.println(errorcode);

		detail.setText(dialogmap.get(Integer.parseInt(errorcode, 16) + ""));
		if (detail.getText().length() == 0) {
			detail.setText(map.get(Integer.parseInt(errorcode, 16) + ""));
		}
		return this;
	}
	
	public ErrorDialogUtil initName2(String errorcode) {
		setting = new Dialog(context, R.style.custom_dialog);
		setting.setContentView(R.layout.dialog_device_error_tips);
		TextView title = (TextView) setting.findViewById(R.id.tv_order_title);
		TextView detail = (TextView) setting.findViewById(R.id.tv_detail);
		title.setText("机器故障(" + errorcode + ")");
		System.out.println(errorcode);

		detail.setText(dialogmap.get(errorcode + ""));
		if (detail.getText().length() == 0) {
			detail.setText(map.get(errorcode));
		}
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
