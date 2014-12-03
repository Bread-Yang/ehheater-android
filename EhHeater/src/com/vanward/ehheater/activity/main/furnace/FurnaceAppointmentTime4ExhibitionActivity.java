package com.vanward.ehheater.activity.main.furnace;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.model.AppointmentVo4Exhibition;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.view.SeekBarHint;
import com.vanward.ehheater.view.SeekBarHint.OnSeekBarHintProgressChangeListener;
import com.vanward.ehheater.view.wheelview.WheelView;
import com.vanward.ehheater.view.wheelview.adapters.NumericWheelAdapter;

public class FurnaceAppointmentTime4ExhibitionActivity extends
		EhHeaterBaseActivity {

	private final String TAG = "FurnaceAppointmentTimeActivity";

	private WheelView wheelView1, wheelView2;

	private ToggleButton tb_switch;

	private RadioGroup rg_mode;

	private SeekBarHint seekBar;

	private CheckBox cb_Monday, cb_Thuesday, cb_Wednesday, cb_Thursday,
			cb_Friday, cb_Saturday, cb_Sunday;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

	private AppointmentVo4Exhibition editModel;

	private Dialog appointmentConflictDialog, appointmentFullDialog;

	private boolean isEdit, isOverride;

	private String nickName = "用户1";

	private int position = -1;

	private Handler tipsHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (!isFinishing()) {
					appointmentConflictDialog.show();
				}
				break;

			case 1:
				if (!isFinishing()) {
					appointmentFullDialog.show();
				}
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_appointment_time);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButtonBackground(R.drawable.menu_icon_ye);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		wheelView1 = (WheelView) findViewById(R.id.wheelView1);
		wheelView2 = (WheelView) findViewById(R.id.wheelView2);
		tb_switch = (ToggleButton) findViewById(R.id.tb_switch);
		seekBar = (SeekBarHint) findViewById(R.id.seekbar);
		rg_mode = (RadioGroup) findViewById(R.id.rg_mode);
		cb_Monday = (CheckBox) findViewById(R.id.cb_Monday);
		cb_Thuesday = (CheckBox) findViewById(R.id.cb_Thuesday);
		cb_Wednesday = (CheckBox) findViewById(R.id.cb_Wednesday);
		cb_Thursday = (CheckBox) findViewById(R.id.cb_Thursday);
		cb_Friday = (CheckBox) findViewById(R.id.cb_Friday);
		cb_Saturday = (CheckBox) findViewById(R.id.cb_Saturday);
		cb_Sunday = (CheckBox) findViewById(R.id.cb_Sunday);
	}

	private void init() {
		appointmentConflictDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.appointment_conflict,
						BaoDialogShowUtil.DEFAULT_RESID, R.string.override,
						null, new OnClickListener() {

							@Override
							public void onClick(View v) {
								isOverride = true;
								btn_right.performClick();
							}
						});

		appointmentFullDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithOneButton(R.string.furnace_appointment_full,
						BaoDialogShowUtil.DEFAULT_RESID, null);

		wheelView1.setCyclic(true);
		wheelView2.setCyclic(true);
		wheelView1.setViewAdapter(new NumericWheelAdapter(this, 0, 23, "%02d",
				wheelView1));
		wheelView2.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d",
				wheelView2));

		editModel = (AppointmentVo4Exhibition) getIntent()
				.getSerializableExtra("editAppointment");
		position = getIntent().getIntExtra("position", -1);
		if (editModel != null) {
			setTopText(R.string.edit_appointment);
			isEdit = true;

			String time = dateFormat.format(new Date(editModel.getDateTime()));
			String[] times = time.split(":");

			Log.e("getDateTime", editModel.getDateTime() + "");
			Log.e("time : ", time);

			wheelView1.setCurrentItem(Integer.valueOf(times[0]));
			wheelView2.setCurrentItem(Integer.valueOf(times[1]));

			seekBar.setProgress(Integer.valueOf(editModel.getTemper()) - 30);

			if (editModel.getIsDeviceOn() == 1) {
				tb_switch.setChecked(true);
			}

			switch (editModel.getWorkMode()) {

			case 0:
				rg_mode.check(R.id.rb_mode_default);
				break;
			case 1:
				rg_mode.check(R.id.rb_mode_outdoor);
				break;
			case 2:
				rg_mode.check(R.id.rb_mode_night);
				break;

			}

			if (editModel.getLoopflag() == 0) {
				editModel.setWeek("0000000");
			} else if (editModel.getLoopflag() == 1) {
				cb_Monday.setChecked(true);
				cb_Thuesday.setChecked(true);
				cb_Wednesday.setChecked(true);
				cb_Thursday.setChecked(true);
				cb_Friday.setChecked(true);
				cb_Saturday.setChecked(true);
				cb_Sunday.setChecked(true);
			} else if (editModel.getLoopflag() == 2) {
				for (int i = 0; i < editModel.getWeek().length(); i++) {
					int flag = editModel.getWeek().charAt(i);
					if (flag == '1') {
						switch (i) {

						case 0:
							cb_Monday.setChecked(true);
							break;
						case 1:
							cb_Thuesday.setChecked(true);
							break;
						case 2:
							cb_Wednesday.setChecked(true);
							break;
						case 3:
							cb_Thursday.setChecked(true);
							break;
						case 4:
							cb_Friday.setChecked(true);
							break;
						case 5:
							cb_Saturday.setChecked(true);
							break;
						case 6:
							cb_Sunday.setChecked(true);
							break;
						}
					}
				}
			}
		} else {
			setTopText(R.string.add);
			editModel = new AppointmentVo4Exhibition();
			editModel.setWeek("0000000");
			
			Calendar c = Calendar.getInstance();
			int currentHour = c.get(Calendar.HOUR_OF_DAY);
			int currentMinute = c.get(Calendar.MINUTE);
			
			wheelView1.setCurrentItem(currentHour);
			wheelView2.setCurrentItem(currentMinute);
		}
	}

	private void setListener() {

		tb_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				editModel.setIsDeviceOn(isChecked ? 1 : 0);
			}
		});

		rg_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {

				case R.id.rb_mode_default:
					editModel.setWorkMode(0);
					seekBar.setEnabled(true);
					break;

				case R.id.rb_mode_outdoor:
					editModel.setWorkMode(1);
					seekBar.setProgress(0);
					seekBar.setEnabled(false);
					break;

				case R.id.rb_mode_night:
					editModel.setWorkMode(2);
					seekBar.setEnabled(true);
					break;
				}
			}
		});

		seekBar.setOnProgressChangeListener(new OnSeekBarHintProgressChangeListener() {

			@Override
			public String onHintTextChanged(SeekBarHint seekBarHint,
					int progress) {
				return String.format("%s℃", progress + 30);
			}
		});

		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				editModel.setIsAppointmentOn(1); // 添加或者编辑完后,默认打开

				int setHour = wheelView1.getCurrentItem();

				int setMinute = wheelView2.getCurrentItem();

				// Log.e(TAG, "hour是 : " + hour);
				// Log.e(TAG, "minute是 : " + minute);

				Calendar c = Calendar.getInstance();
				c.setTime(new Date());

				int currentHour = c.get(Calendar.HOUR_OF_DAY);
				int currentMinute = c.get(Calendar.MINUTE);

				// 如果小于或等于当前系统时间,则加一天时间
				if (setHour < currentHour
						|| ((setHour == currentHour) && (setMinute <= currentMinute))) {
					c.add(Calendar.DAY_OF_MONTH, 1);
				}

				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DAY_OF_MONTH);
				int second = c.get(Calendar.SECOND);

				Log.e("year : ", year + "");
				Log.e("month : ", month + "");
				Log.e("day : ", day + "");
				Log.e("hour : ", currentHour + "");
				Log.e("minute : ", currentMinute + "");
				Log.e("second : ", second + "");
				Log.e("timestamp: ", c.getTimeInMillis() + "");

				c.set(year, month, day, setHour, setMinute);

				// Date date = new Date();
				// date.setHours(setHour);
				// date.setMinutes(setMinute);
				// long timestamp = date.getTime();

				long timestamp = c.getTimeInMillis();

				editModel.setDateTime(timestamp);

				// Log.e(TAG, "timestamp是 : " + timestamp);
				//
				String time = dateFormat.format(new Date(timestamp));
				//
				// Log.e(TAG, "time 是 : " + time);

				editModel.setTemper(String.valueOf(seekBar.getProgress() + 30));

				StringBuilder week = new StringBuilder(editModel.getWeek());

				week.setCharAt(0, cb_Monday.isChecked() ? '1' : '0'); // Monday
				week.setCharAt(1, cb_Thuesday.isChecked() ? '1' : '0'); // Thuesday
				week.setCharAt(2, cb_Wednesday.isChecked() ? '1' : '0'); // Wednesday
				week.setCharAt(3, cb_Thursday.isChecked() ? '1' : '0'); // Thursday
				week.setCharAt(4, cb_Friday.isChecked() ? '1' : '0'); // Friday
				week.setCharAt(5, cb_Saturday.isChecked() ? '1' : '0'); // Saturday
				week.setCharAt(6, cb_Sunday.isChecked() ? '1' : '0'); // Sunday

				editModel.setWeek(week.toString());

				if ("0000000".equals(editModel.getWeek())) {
					editModel.setLoopflag(0);
				} else if ("1111111".equals(editModel.getWeek())) {
					editModel.setLoopflag(1);
				} else {
					editModel.setLoopflag(2);
				}

				// for test

				editModel.setName(nickName);

				editModel.setDid("SkWEsvSxX68zd6s63jhw6i");

				editModel.setUid("13527718111");

				editModel.setPasscode("SFKWDAWADS");

				editModel.setDeviceType(1);

				// 热水器多余的数据
				editModel.setPeopleNum("0");

				editModel.setPower("0");

				Intent intent = new Intent();
				intent.putExtra("editAppointment", editModel);
				intent.putExtra("position", position);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
}
