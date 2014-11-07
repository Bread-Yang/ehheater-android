package com.vanward.ehheater.activity.appointment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.main.SendMsgModel;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.view.wheelview.WheelView;
import com.vanward.ehheater.view.wheelview.adapters.AbstractWheelTextAdapter;
import com.vanward.ehheater.view.wheelview.adapters.NumericWheelAdapter;

public class AppointmentTimeActivity extends EhHeaterBaseActivity implements
		OnSeekBarChangeListener, OnClickListener {

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
	@ViewInject(id = R.id.radioGroup1, click = "onClick")
	RadioGroup peopleGroup;
	@ViewInject(id = R.id.seekBar1, click = "onClick")
	SeekBar seekBar1;
	@ViewInject(id = R.id.textView2, click = "onClick")
	TextView textView2;
	@ViewInject(id = R.id.switch1, click = "onClick")
	ImageButton switch1;
	@ViewInject(id = R.id.rlt_number, click = "onClick")
	RelativeLayout fenrenxiLayout;
	@ViewInject(id = R.id.RelativeLayout01, click = "onClick")
	RelativeLayout temLayout;
	@ViewInject(id = R.id.RelativeLayout02, click = "onClick")
	RelativeLayout powerLayout;

	@ViewInject(id = R.id.RelativeLayout04, click = "onClick")
	RelativeLayout replaceweek;

	@ViewInject(id = R.id.select_week, click = "onClick")
	TextView select_week;
	int power = 1, temper, peoplenum;

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
		fenrenxiLayout.setVisibility(View.GONE);
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
		switch1.setOnClickListener(this);
		switch1.setTag(1);
	}

	private void init() {
		wheelView1.setCyclic(true);
		wheelView2.setCyclic(true);
		seekBar1.setOnSeekBarChangeListener(this);
		wheelView1.setViewAdapter(new NumericWheelAdapter(this, 0, 23, "%02d",
				wheelView1));
		wheelView2.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d",
				wheelView2));
		ivTitleName.setText("添加");
		ivTitleBtnLeft.setBackgroundResource(R.drawable.icon_back);
		ivTitleBtnRigh.setBackgroundResource(R.drawable.menu_icon_ye);

		AppointMentVo appointMentVo = new BaseDao(this).getDb().findById(1,
				AppointMentVo.class);
		if (appointMentVo != null) {
			wheelView1.setCurrentItem(appointMentVo.getHour());
			wheelView2.setCurrentItem(appointMentVo.getMin());

		}

		peopleGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio00:
					// unlock
					unLockTempAndPower();
					break;
				case R.id.radio0:
					// lock
					// set args
					// lockTempAndPower();
					setArgsForTempAndPower(45, 3);
					break;
				case R.id.radio1:
					// lockTempAndPower();
					setArgsForTempAndPower(65, 3);
					break;
				case R.id.radio2:
					// lockTempAndPower();
					setArgsForTempAndPower(75, 3);
					break;
				}

			}
		});

		peopleGroup.check(R.id.radio0);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				System.out.println("dsadas");
				switch (arg1) {
				case R.id.RadioButton03:
					power = 1;
					break;
				case R.id.RadioButton01:
					power = 2;
					break;
				case R.id.RadioButton02:
					power = 3;
					break;

				default:
					break;
				}
			}
		});

	}

	private void lockTempAndPower() {
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			((RadioButton) radioGroup.getChildAt(i)).setEnabled(false);
		}
	}

	private void unLockTempAndPower() {
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			((RadioButton) radioGroup.getChildAt(i)).setEnabled(true);
		}
	}

	private void setArgsForTempAndPower(int temp, int power) {
		seekBar1.setProgress(temp - 35);
		switch (power) {
		case 1:
			radioGroup.check(R.id.RadioButton03);
			break;
		case 2:
			radioGroup.check(R.id.RadioButton01);
			break;
		case 3:
			radioGroup.check(R.id.RadioButton02);
			break;
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

	String[] dateString = { "一", "二", "三", "四", "五", "六", "日" };
	String weektext = "";

	@Override
	protected void onResume() {
		weektext = "";
		boolean isallcheck = true;
		if (AppointmentModel.getInstance(this).getDays() != null) {
			for (int i = 0; i < AppointmentModel.getInstance(this).getDays().length; i++) {
				if (AppointmentModel.getInstance(this).getDays()[i] == 1) {
					weektext = weektext + "星期" + dateString[i] + ",";
				} else {
					isallcheck = false;
				}
			}
			select_week.setText(weektext);
			if (isallcheck) {
				select_week.setText("每天");
				weektext = "每天";
			}
		} else {
			select_week.setText("永不");
		}

		super.onResume();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		super.onClick(arg0);
		switch (arg0.getId()) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;

		case R.id.RelativeLayout04:
			System.out.println("dasdas");
			// Intent intent = new Intent();
			// intent.setClass(AppointmentTimeActivity.this,
			// AppointmentDaysActivity.class);
			// startActivity(intent);
			break;
		case R.id.switch1:

			if ((Integer) switch1.getTag() == 1) {
				fenrenxiLayout.setVisibility(View.VISIBLE);
				temLayout.setVisibility(View.GONE);
				powerLayout.setVisibility(View.GONE);
				switch1.setImageResource(R.drawable.on);
				switch1.setTag(0);
			} else {
				fenrenxiLayout.setVisibility(View.GONE);
				temLayout.setVisibility(View.VISIBLE);
				powerLayout.setVisibility(View.VISIBLE);
				switch1.setImageResource(R.drawable.off);
				switch1.setTag(1);
			}
			break;

		case R.id.ivTitleBtnRigh:
			String hour = ((AbstractWheelTextAdapter) wheelView1
					.getViewAdapter()).getItemText(wheelView1.getCurrentItem())
					.toString();
			String minute = ((AbstractWheelTextAdapter) wheelView2
					.getViewAdapter()).getItemText(wheelView2.getCurrentItem())
					.toString();
			int num = 0;
			for (int i = 0; i < peopleGroup.getChildCount(); i++) {
				if (peopleGroup.getCheckedRadioButtonId() == peopleGroup
						.getChildAt(i).getId()) {
					num = i;
				}
			}

			Date date = new Date();

			if (date.getHours() > Integer.parseInt(hour)) {
				Toast.makeText(this, "预约时间不能早于当前时间", Toast.LENGTH_SHORT).show();
				return;
			}else if (date.getHours() == Integer.parseInt(hour)&&date.getMinutes() > Integer.parseInt(minute))  {
				Toast.makeText(this, "预约时间不能早于当前时间", Toast.LENGTH_SHORT).show();
		       return;
			}
			SendMsgModel.sentAppolitionment(Integer.parseInt(hour),
					Integer.parseInt(minute), peoplenum);
			// int hour, int minute, int looper, int[] days, int power,int
			// peopleNum, int temper
			System.out.println("weektext: " + weektext);
			Appointment appointMent = new Appointment(Integer.parseInt(hour),
					Integer.parseInt(minute), 1, weektext, power, peoplenum,
					temper);
			new BaseDao(this).getDb().save(appointMent);

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
		temper = arg1 + 35;
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
