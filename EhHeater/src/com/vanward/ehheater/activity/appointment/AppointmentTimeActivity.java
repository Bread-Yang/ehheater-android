package com.vanward.ehheater.activity.appointment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.SendMsgModel;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.view.wheelview.WheelView;
import com.vanward.ehheater.view.wheelview.adapters.AbstractWheelTextAdapter;
import com.vanward.ehheater.view.wheelview.adapters.NumericWheelAdapter;

public class AppointmentTimeActivity extends EhHeaterBaseActivity implements
		OnSeekBarChangeListener {

	private WheelView wheelView1, wheelView2;

	private TextView tv_days, tv_number;

	private int number = 2;

	private int[] days = { 0, 0, 0, 0, 0, 0, 0 };

	private final int TO_APPOINTMENT_DAYS = 0;

	private final int TO_APPOINTMENT_NUMBER = 1;
	@ViewInject(id = R.id.ivTitleName, click = "onClick")
	TextView ivTitleName;
	@ViewInject(id = R.id.ivTitleBtnLeft, click = "onClick")
	Button ivTitleBtnLeft;
	@ViewInject(id = R.id.ivTitleBtnRigh, click = "onClick")
	Button ivTitleBtnRigh;
	@ViewInject(id = R.id.water_radiogroup, click = "onClick")
	RadioGroup radioGroup;
	@ViewInject(id = R.id.seekBar1, click = "onClick")
	SeekBar seekBar1;

	@ViewInject(id = R.id.textView2, click = "onClick")
	TextView textView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment_time);
		FinalActivity.initInjectedView(this);
		// setTopText(R.string.add);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		tv_days = (TextView) findViewById(R.id.tv_days);
		tv_number = (TextView) findViewById(R.id.tv_number);
		wheelView1 = (WheelView) findViewById(R.id.wheelView1);
		wheelView2 = (WheelView) findViewById(R.id.wheelView2);
	}

	private void setListener() {
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				String hour = ((AbstractWheelTextAdapter) wheelView1
						.getViewAdapter()).getItemText(
						wheelView1.getCurrentItem()).toString();
				String minute = ((AbstractWheelTextAdapter) wheelView2
						.getViewAdapter()).getItemText(
						wheelView2.getCurrentItem()).toString();
				intent.putExtra("hour", hour);
				intent.putExtra("minute", minute);
				intent.putExtra("days", days);
				intent.putExtra("temp", 18);
				intent.putExtra("number", number);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

	}

	private void init() {
		wheelView1.setCyclic(true);
		wheelView2.setCyclic(true);
		seekBar1.setOnSeekBarChangeListener(this);
		wheelView1.setViewAdapter(new NumericWheelAdapter(this, 0, 23, "%02d",
				wheelView1));
		wheelView2.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d",
				wheelView2));
		ivTitleName.setText("预约");
		ivTitleBtnLeft.setBackgroundResource(R.drawable.icon_back);

		ivTitleBtnRigh.setBackgroundResource(R.drawable.menu_icon_ye);

		AppointMentVo appointMentVo = new BaseDao(this).getDb().findById(1,
				AppointMentVo.class);
		if (appointMentVo != null) {
			wheelView1.setCurrentItem(appointMentVo.getHour());
			wheelView2.setCurrentItem(appointMentVo.getMin());
			((RadioButton) radioGroup.getChildAt(appointMentVo.getNum()))
					.setChecked(true);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TO_APPOINTMENT_DAYS:
				days = data.getIntArrayExtra("days");
				String text = "";
				boolean isNever = true; // never repeat
				boolean isEveryDay = true; // every
				for (int i = 0; i < days.length; i++) {
					if (days[i] == 1) {
						switch (i) {
						case 0:
							text += getResources().getString(R.string.Monday)
									+ " ";
							break;
						case 1:
							text += getResources().getString(R.string.Tuesday)
									+ " ";
							break;
						case 2:
							text += getResources()
									.getString(R.string.Wednesday) + " ";
							break;
						case 3:
							text += getResources().getString(R.string.Thursday)
									+ " ";
							break;
						case 4:
							text += getResources().getString(R.string.Friday)
									+ " ";
							break;
						case 5:
							text += getResources().getString(R.string.Saturday)
									+ " ";
							break;
						case 6:
							text += getResources().getString(R.string.Sunday)
									+ " ";
							break;
						}
						isNever = false;
					} else {
						isEveryDay = false;
					}
				}
				if (isEveryDay) {
					tv_days.setText(R.string.everyday);
				} else if (isNever) {
					tv_days.setText(R.string.never);
				} else {
					tv_days.setText(text);
				}
				break;
			case TO_APPOINTMENT_NUMBER:
				number = data.getIntExtra("number", 2);
				switch (number) {
				case 1:
					tv_number.setText(R.string.one_person);
					break;
				case 2:
					tv_number.setText(R.string.two_person);
					break;
				case 3:
					tv_number.setText(R.string.three_person);
					break;
				}
				break;
			}

		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		super.onClick(arg0);
		switch (arg0.getId()) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;

		case R.id.ivTitleBtnRigh:
			String hour = ((AbstractWheelTextAdapter) wheelView1
					.getViewAdapter()).getItemText(wheelView1.getCurrentItem())
					.toString();
			String minute = ((AbstractWheelTextAdapter) wheelView2
					.getViewAdapter()).getItemText(wheelView2.getCurrentItem())
					.toString();

			int num = 0;
			for (int i = 0; i < radioGroup.getChildCount(); i++) {
				if (radioGroup.getCheckedRadioButtonId() == radioGroup
						.getChildAt(i).getId()) {
					num = i;
				}
			}
			AppointMentVo appointMentVo = new AppointMentVo("1",
					Integer.parseInt(hour), Integer.parseInt(minute), num);
			new BaseDao(this).getDb().replace(appointMentVo);
			SendMsgModel.sentAppolitionment(Integer.parseInt(hour),
					Integer.parseInt(minute), num + 1);
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		textView2.setText(arg1 + 35 + "℃");
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}
}
