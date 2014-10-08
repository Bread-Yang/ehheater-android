package com.vanward.ehheater.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.view.wheelview.WheelView;
import com.vanward.ehheater.view.wheelview.adapters.AbstractWheelTextAdapter;
import com.vanward.ehheater.view.wheelview.adapters.NumericWheelAdapter;

public class TimeDialogUtil {

	String hour, minute;

	public String getHour() {
		return hour;
	}

	public int limitStart = 0, limitend = 23;

	public String getMinute() {
		return minute;
	}

	private static Context context;
	private Dialog dialog_morning_wash_time_setting;
	private static TimeDialogUtil model;

	private TimeDialogUtil(Context context) {
		this.context = context;
	}

	NextButtonCall nextButtonCall;

	public void setNextButtonCall(NextButtonCall nextButtonCall) {
		this.nextButtonCall = nextButtonCall;
	}

	public interface NextButtonCall {
		void oncall(View v);
	}

	public static TimeDialogUtil instance(Context context) {
		if (model == null) {
			model = new TimeDialogUtil(context);
		}
		TimeDialogUtil.context = context;
		return model;
	}

	public TimeDialogUtil nextButtonCall(NextButtonCall nextButtonCall) {
		setNextButtonCall(nextButtonCall);
		return this;
	}

	public void dissmiss() {
		if (dialog_morning_wash_time_setting != null
				&& dialog_morning_wash_time_setting.isShowing()) {
			dialog_morning_wash_time_setting.dismiss();
		}

	}

	public TimeDialogUtil setLimitStart(int limitStart) {
		this.limitStart = limitStart;
		return this;
	}

	public TimeDialogUtil setLimitend(int limitend) {
		this.limitend = limitend;
		return this;
	}

	public void showDialog() {
		dialog_morning_wash_time_setting = new Dialog(context,
				R.style.custom_dialog);
		dialog_morning_wash_time_setting
				.setContentView(R.layout.dialog_morning_wash_time_setting);

		WheelView wheelView1 = (WheelView) dialog_morning_wash_time_setting
				.findViewById(R.id.wheelView1);
		WheelView wheelView2 = (WheelView) dialog_morning_wash_time_setting
				.findViewById(R.id.wheelView2);

		wheelView1.setCyclic(true);
		wheelView2.setCyclic(true);
		wheelView1.setViewAdapter(new NumericWheelAdapter(context, limitStart,
				limitend, "%02d", wheelView1));
		wheelView2.setViewAdapter(new NumericWheelAdapter(context, 0, 59,
				"%02d", wheelView2));
		dialog_morning_wash_time_setting.findViewById(R.id.btn_cancel)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog_morning_wash_time_setting.dismiss();
					}
				});

		dialog_morning_wash_time_setting.findViewById(R.id.btn_next_step)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						WheelView wheelViewhour = (WheelView) dialog_morning_wash_time_setting
								.findViewById(R.id.wheelView1);
						WheelView wheelViewMin = (WheelView) dialog_morning_wash_time_setting
								.findViewById(R.id.wheelView2);
						hour = ((AbstractWheelTextAdapter) wheelViewhour
								.getViewAdapter()).getItemText(
								wheelViewhour.getCurrentItem()).toString();
						minute = ((AbstractWheelTextAdapter) wheelViewMin
								.getViewAdapter()).getItemText(
								wheelViewMin.getCurrentItem()).toString();
						System.out.println("hour: " + hour + "minute:  "
								+ minute);
						nextButtonCall.oncall(v);
					}
				});
		dialog_morning_wash_time_setting.show();
	}
}
