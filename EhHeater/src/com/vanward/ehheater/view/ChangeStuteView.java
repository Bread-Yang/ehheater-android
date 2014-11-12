package com.vanward.ehheater.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class ChangeStuteView {

	public static void swichLeaveMinView(ViewGroup parent, int min) {
		parent.removeAllViews();
		View view = LinearLayout.inflate(parent.getContext(),
				R.layout.comment_statue_layout, null);
		TextView textView = (TextView) view.findViewById(R.id.min);
		textView.setText(min + "mins");
		parent.addView(view);
	}

	public static void swichNight(ViewGroup parent) {
		parent.removeAllViews();
		View view = LinearLayout.inflate(parent.getContext(),
				R.layout.comment_statue_layout, null);
		TextView text = (TextView) view.findViewById(R.id.text);
		TextView min = (TextView) view.findViewById(R.id.min);
		text.setText("加热时间");
		min.setText("00:00-06:00am");
		parent.addView(view);
	}

	public static void swichMorning(ViewGroup parent) {
		parent.removeAllViews();
		View view = LinearLayout.inflate(parent.getContext(),
				R.layout.comment_statue_layout, null);
		TextView text = (TextView) view.findViewById(R.id.text);
		TextView min = (TextView) view.findViewById(R.id.min);
		text.setText("加热时段");
		min.setText("06:00-09:00am");
		parent.addView(view);
	}

	public static void swichNoAppoient(ViewGroup parent) {
		parent.removeAllViews();
		View view = LinearLayout.inflate(parent.getContext(),
				R.layout.comment_statue_layout, null);
		TextView min = (TextView) view.findViewById(R.id.min);
		TextView textView = (TextView) view.findViewById(R.id.text);
		textView.setText("暂无预约");
		min.setVisibility(View.GONE);
		parent.addView(view);
	}

	public static void swichDeviceOff(ViewGroup parent) {
		parent.removeAllViews();
		View view = LinearLayout.inflate(parent.getContext(),
				R.layout.comment_statue_layout, null);
		TextView min = (TextView) view.findViewById(R.id.min);
		TextView textView = (TextView) view.findViewById(R.id.text);
		textView.setText("关机中");
		min.setVisibility(View.GONE);
		parent.addView(view);
	}
	public static void swichdisconnect(ViewGroup parent) {
		parent.removeAllViews();
		View view = LinearLayout.inflate(parent.getContext(),
				R.layout.comment_statue_layout, null);
		TextView min = (TextView) view.findViewById(R.id.min);
		TextView textView = (TextView) view.findViewById(R.id.text);
		textView.setText("不在线");
		min.setVisibility(View.GONE);
		parent.addView(view);
	}

	

	public static void swichKeep(ViewGroup parent) {
		parent.removeAllViews();
		View view = LinearLayout.inflate(parent.getContext(),
				R.layout.comment_statue_layout, null);
		TextView min = (TextView) view.findViewById(R.id.min);
		TextView textView = (TextView) view.findViewById(R.id.text);
		textView.setText("保温中");
		min.setVisibility(View.GONE);
		parent.addView(view);
	}

	public static void swichMorningWash(ViewGroup parent) {
		parent.removeAllViews();
		View view = LinearLayout.inflate(parent.getContext(),
				R.layout.morning_wash_statue_layout, parent);
		TextView time = (TextView) view.findViewById(R.id.time);
		time.setText("06:00-09:00am");
	}

}
