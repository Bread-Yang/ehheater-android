package com.vanward.ehheater.activity.appointment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.model.AppointmentVo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.view.SeekBarHint;
import com.vanward.ehheater.view.SeekBarHint.OnSeekBarHintProgressChangeListener;
import com.vanward.ehheater.view.wheelview.WheelView;
import com.vanward.ehheater.view.wheelview.adapters.NumericWheelAdapter;

public class AppointmentTimeActivity extends EhHeaterBaseActivity implements
		OnClickListener {

	private final String TAG = "AppointmentTimeActivity";

	private WheelView wheelView1, wheelView2;

	private TextView tv_days, tv_number;

	private RadioGroup rg_power, rg_people;

	private SeekBarHint seekBar;

	private ToggleButton tb_switch;

	private RelativeLayout rlt_fenrenxi, rlt_temperature, rlt_power, rlt_week;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

	private AppointmentVo editModel;

	private HttpFriend mHttpFriend;

	private HeaterInfo heaterInfo;

	private int number = 2;

	private int[] days = { 0, 0, 0, 0, 0, 0, 0 };

	private final int TO_APPOINTMENT_DAYS = 0;

	private final int TO_APPOINTMENT_NUMBER = 1;

	int temper, peoplenum;

	private Dialog appointmentConflictDialog, appointmentFullDialog,
			appointmentAddPowerLess3Dailog, appointmentAddPowerLarger3Dailog;

	private boolean isEdit, isOverride;

	private String nickName = "";

	private String conflictName = "";

	private long conflictTime;

	private String saveOrUpdateAppointmentId = "";

	private Handler tipsHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (!isFinishing()) {
					TextView tv_tips = (TextView) appointmentConflictDialog
							.findViewById(R.id.tv_content);
					// tv_tips.setText(getResources().getString(
					// R.string.appointment_conflict)
					// + String.format("%2d", wheelView1.getCurrentItem())
					// + "："
					// + String.format("%2d", wheelView2.getCurrentItem())
					// + "？");
					String sFormat = getResources().getString(
							R.string.appointment_conflict);
					String sFinalAge = String.format(sFormat, conflictName,
							dateFormat.format(new Date(conflictTime)));
					tv_tips.setText(sFinalAge);

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
		setCenterView(R.layout.activity_appointment_time);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButtonBackground(R.drawable.menu_icon_ye);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		tv_days = (TextView) findViewById(R.id.tv_days);
		tv_number = (TextView) findViewById(R.id.tv_number);
		wheelView1 = (WheelView) findViewById(R.id.wheelView1);
		wheelView2 = (WheelView) findViewById(R.id.wheelView2);
		rg_power = (RadioGroup) findViewById(R.id.rg_power);
		rg_people = (RadioGroup) findViewById(R.id.rg_people);
		seekBar = (SeekBarHint) findViewById(R.id.seekBar);
		tb_switch = (ToggleButton) findViewById(R.id.tb_switch);
		rlt_fenrenxi = (RelativeLayout) findViewById(R.id.rlt_fenrenxi);
		rlt_fenrenxi.setVisibility(View.GONE);
		rlt_temperature = (RelativeLayout) findViewById(R.id.rlt_temperature);
		rlt_power = (RelativeLayout) findViewById(R.id.rlt_power);
		rlt_week = (RelativeLayout) findViewById(R.id.rlt_week);
	}

	private void setListener() {

		tb_switch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							rlt_fenrenxi.setVisibility(View.VISIBLE);
							rg_people.check(R.id.rb_people_1);
							rlt_temperature.setVisibility(View.GONE);
							rlt_power.setVisibility(View.GONE);
						} else {
							rlt_fenrenxi.setVisibility(View.GONE);
							editModel.setPeopleNum("0");
							rlt_temperature.setVisibility(View.VISIBLE);
							rlt_power.setVisibility(View.VISIBLE);
						}
					}
				});

		seekBar.setOnProgressChangeListener(new OnSeekBarHintProgressChangeListener() {

			@Override
			public String onHintTextChanged(SeekBarHint seekBarHint,
					int progress) {
				return String.format("%s℃", progress + 35);
			}
		});

		rg_people.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_people_1:
					// unLockTempAndPower();
					peopleForTempAndPower(45, 3);
					editModel.setPeopleNum("1");
					break;
				case R.id.rb_people_2:
					// lockTempAndPower();
					peopleForTempAndPower(65, 3);
					editModel.setPeopleNum("2");
					break;
				case R.id.rb_people_3:
					// lockTempAndPower();
					peopleForTempAndPower(75, 3);
					editModel.setPeopleNum("3");
					break;
				}

			}
		});

		rg_power.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.rb_power_1:
					editModel.setPower("1");
					break;
				case R.id.rb_power_2:
					editModel.setPower("2");
					break;
				case R.id.rb_power_3:
					editModel.setPower("3");
					break;

				default:
					break;
				}
			}
		});

		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int setHour = wheelView1.getCurrentItem();

				int setMinute = wheelView2.getCurrentItem();

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

				c.set(year, month, day, setHour, setMinute, 0);

				// Date date = new Date();
				// date.setHours(setHour);
				// date.setMinutes(setMinute);
				// long timestamp = date.getTime();

				long timestamp = c.getTimeInMillis() / 1000 * 1000; // 将毫秒统一设为000

				editModel.setDateTime(timestamp);

				// Log.e(TAG, "timestamp是 : " + timestamp);
				//
				String time = dateFormat.format(new Date(timestamp));
				//
				// Log.e(TAG, "time 是 : " + time);

				editModel.setTemper(String.valueOf(seekBar.getProgress() + 35));

				String week = "";

				for (int i = 0; i < days.length - 1; i++) {
					week += days[i];
				}

				week = days[6] + week;

				editModel.setWeek(week);

				if ("0000000".equals(editModel.getWeek())) {
					editModel.setLoopflag(0);
				} else if ("1111111".equals(editModel.getWeek())) {
					editModel.setLoopflag(1);
				} else {
					editModel.setLoopflag(2);
				}

				// for test

				editModel.setName(nickName);

				editModel.setDid(heaterInfo.getDid());

				editModel.setUid(AccountService.getUserId(getBaseContext()));

				editModel.setPasscode(heaterInfo.getPasscode());

				editModel.setDeviceType(0);

				String requestURL = null;
				if (isEdit) {
					requestURL = "userinfo/updateAppointment";
				} else {
					editModel.setIsAppointmentOn(1); // 添加完后,默认打开
					requestURL = "userinfo/saveAppointment";
				}

				showRequestDialog();

				Gson gson = new Gson();
				String json = gson.toJson(editModel);

				Log.e(TAG, "提交的json数据是 : " + json);

				AjaxParams params = new AjaxParams();
				params.put("data", json);
				Log.e("isOverride : ", isOverride + "");
				if (isOverride) {
					params.put("ignoreConflict", "true");
				}

				Log.e(TAG, "提交URL是 : " + Consts.REQUEST_BASE_URL + requestURL);
				mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL)
						.executePost(params, new AjaxCallBack<String>() {

							@Override
							public void onSuccess(String jsonString) {
								super.onSuccess(jsonString);
								Log.e(TAG, "编辑或者保存返回的json数据是 : " + jsonString);
								isOverride = false;
								try {
									JSONObject json = new JSONObject(jsonString);
									String responseCode = json
											.getString("responseCode");
									if ("200".equals(responseCode)) {
										if (isEdit) {
											saveOrUpdateAppointmentId = String.valueOf(editModel
													.getAppointmentId());
										} else {
											saveOrUpdateAppointmentId = json
													.getString("result");
										}

										String requestURL = "userinfo/checkAppointmentStatue?uid="
												+ AccountService
														.getUserId(getBaseContext());

										mHttpFriend
												.toUrl(Consts.REQUEST_BASE_URL
														+ requestURL)
												.executeGet(
														null,
														new AjaxCallBack<String>() {
															@Override
															public void onSuccess(
																	String jsonString) {
																super.onSuccess(jsonString);

																Log.e(TAG,
																		"功率需要增加的请求返回来的数据是 : "
																				+ jsonString);
																try {
																	JSONObject json = new JSONObject(
																			jsonString);
																	String responseCode = json
																			.getString("responseCode");
																	if ("601"
																			.equals(responseCode)) {
																		JSONArray result = json
																				.getJSONArray("result");
																		for (int i = 0; i < result
																				.length(); i++) {
																			JSONObject item = result
																					.getJSONObject(i);
																			boolean needNotify = item
																					.getBoolean("needNotify");
																			String appointmentId = item
																					.getString("appointmentId");
																			if (appointmentId
																					.equals(saveOrUpdateAppointmentId)
																					&& needNotify) {
																				if (!"3".equals(editModel
																						.getPower())) {
																					appointmentAddPowerLess3Dailog
																							.show();
																				} else {
																					appointmentAddPowerLarger3Dailog
																							.show();
																				}
																				editModel.setAppointmentId(Integer.valueOf(appointmentId));
																				isEdit = true;
																				break;
																			}
																		}
																	} else {
																		finish();
																	}
																} catch (Exception e) {
																	e.printStackTrace();
																}

															}
														});

									} else if ("501".equals(responseCode)) {
										JSONObject result = json
												.getJSONObject("result");
										conflictName = result.getString("name");
										conflictTime = result
												.getLong("dateTime");
										dismissRequestDialog();
										tipsHandler.sendEmptyMessage(0);
									} else if ("403".equals(responseCode)) { // 预约满了
										dismissRequestDialog();
										tipsHandler.sendEmptyMessage(1);
									}
								} catch (JSONException e) {
									e.printStackTrace();
									dismissRequestDialog();
								}
							}

							@Override
							public void onFailure(Throwable t, int errorNo,
									String strMsg) {
								super.onFailure(t, errorNo, strMsg);
								isOverride = false;
								Toast.makeText(AppointmentTimeActivity.this,
										"服务器错误", Toast.LENGTH_LONG).show();
								dismissRequestDialog();
							}
						});
			}
		});
	}

	private void init() {
		appointmentConflictDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.appointment_conflict,
						BaoDialogShowUtil.DEFAULT_RESID, R.string.override,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								appointmentConflictDialog.dismiss();
								finish();
							}
						}, new OnClickListener() {

							@Override
							public void onClick(View v) {
								appointmentConflictDialog.dismiss();
								isOverride = true;
								btn_right.performClick();
							}
						});

		appointmentFullDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithOneButton(R.string.heater_appointment_full,
						BaoDialogShowUtil.DEFAULT_RESID, new OnClickListener() {

							@Override
							public void onClick(View v) {
								finish();
							}
						});

		appointmentAddPowerLess3Dailog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithOneButton(R.string.add_power_less_than_3,
						R.string.ok, new OnClickListener() {

							@Override
							public void onClick(View v) {
								appointmentAddPowerLess3Dailog.dismiss();
								// finish();
							}
						});

		appointmentAddPowerLarger3Dailog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithOneButton(R.string.add_power_larger_than_3,
						R.string.ok, new OnClickListener() {

							@Override
							public void onClick(View v) {
								appointmentAddPowerLarger3Dailog.dismiss();
								// finish();
							}
						});

		heaterInfo = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeater();

		mHttpFriend = HttpFriend.create(this);

		String requestURL = "userinfo/getUsageInformation?uid="
				+ AccountService.getUserId(getBaseContext());

		mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
				null, new AjaxCallBack<String>() {
					public void onSuccess(String jsonString) {
						JSONObject json;
						try {
							json = new JSONObject(jsonString);
							String responseCode = json
									.getString("responseCode");
							if ("200".equals(responseCode)) {
								JSONObject result = json
										.getJSONObject("result");
								nickName = result.getString("userName");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					};

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						Toast.makeText(AppointmentTimeActivity.this, "服务器错误",
								Toast.LENGTH_LONG).show();
						dismissRequestDialog();
					}
				});

		wheelView1.setCyclic(true);
		wheelView2.setCyclic(true);
		wheelView1.setViewAdapter(new NumericWheelAdapter(this, 0, 23, "%02d",
				wheelView1));
		wheelView2.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d",
				wheelView2));

		editModel = getIntent().getParcelableExtra("editAppointment");
		if (editModel != null) {
			setTopText(R.string.edit_appointment);
			isEdit = true;

			String time = dateFormat.format(new Date(editModel.getDateTime()));
			String[] times = time.split(":");

			wheelView1.setCurrentItem(Integer.valueOf(times[0]));
			wheelView2.setCurrentItem(Integer.valueOf(times[1]));

			if (editModel.getLoopflag() == 1) {
				days = new int[] { 1, 1, 1, 1, 1, 1, 1 };
			} else if (editModel.getLoopflag() == 2) {
				for (int i = 1; i < editModel.getWeek().length(); i++) {
					int flag = Integer.valueOf(editModel.getWeek().substring(i,
							i + 1));
					Log.e("flag", flag + "");
					days[i - 1] = flag;
				}
				days[6] = Integer.valueOf(editModel.getWeek().substring(0, 1));
			}
			showRepeatDays();

			String peopleNum = editModel.getPeopleNum();

			if (!"0".equals(editModel.getPeopleNum())) {
				tb_switch.setChecked(true);
				editModel.setPeopleNum(peopleNum);
				if ("1".equals(editModel.getPeopleNum())) {
					rg_people.check(R.id.rb_people_1);
				} else if ("2".equals(editModel.getPeopleNum())) {
					rg_people.check(R.id.rb_people_2);
				} else if ("3".equals(editModel.getPeopleNum())) {
					rg_people.check(R.id.rb_people_3);
				}

			}

			if ("1".equals(editModel.getPower())) {
				rg_power.check(R.id.rb_power_1);
			} else if ("2".equals(editModel.getPower())) {
				rg_power.check(R.id.rb_power_2);
			} else if ("3".equals(editModel.getPower())) {
				rg_power.check(R.id.rb_power_3);
			}

			seekBar.setProgress(Integer.valueOf(editModel.getTemper()) - 35);

		} else {
			setTopText(R.string.add);
			editModel = new AppointmentVo();
			editModel.setWeek("0000000");
			editModel.setPeopleNum("0");
			editModel.setPower("2");

			Calendar c = Calendar.getInstance();
			int currentHour = c.get(Calendar.HOUR_OF_DAY);
			int currentMinute = c.get(Calendar.MINUTE);

			wheelView1.setCurrentItem(currentHour);
			wheelView2.setCurrentItem(currentMinute);
		}
	}

	private void lockTempAndPower() {
		for (int i = 0; i < rg_power.getChildCount(); i++) {
			((RadioButton) rg_power.getChildAt(i)).setEnabled(false);
		}
	}

	private void unLockTempAndPower() {
		for (int i = 0; i < rg_power.getChildCount(); i++) {
			((RadioButton) rg_power.getChildAt(i)).setEnabled(true);
		}
	}

	private void peopleForTempAndPower(int temp, int power) {
		seekBar.setProgress(temp - 35);
		switch (power) {
		case 1:
			rg_power.check(R.id.rb_power_1);
			break;
		case 2:
			rg_power.check(R.id.rb_power_2);
			break;
		case 3:
			rg_power.check(R.id.rb_power_3);
			break;
		}
	}

	private void showRepeatDays() {
		String text = "";
		boolean isNever = true; // never repeat
		boolean isEveryDay = true; // every
		for (int i = 0; i < days.length; i++) {
			if (days[i] == 1) {
				switch (i) {
				case 0:
					text += getResources().getString(R.string.Monday) + " ";
					break;
				case 1:
					text += getResources().getString(R.string.Tuesday) + " ";
					break;
				case 2:
					text += getResources().getString(R.string.Wednesday) + " ";
					break;
				case 3:
					text += getResources().getString(R.string.Thursday) + " ";
					break;
				case 4:
					text += getResources().getString(R.string.Friday) + " ";
					break;
				case 5:
					text += getResources().getString(R.string.Saturday) + " ";
					break;
				case 6:
					text += getResources().getString(R.string.Sunday) + " ";
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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("onActivityResult", "onActivityResult");
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TO_APPOINTMENT_DAYS:
				days = data.getIntArrayExtra("days");
				showRepeatDays();
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
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.rlt_week:
			Intent intent = new Intent();
			intent.putExtra("days", days);
			intent.setClass(AppointmentTimeActivity.this,
					AppointmentDaysActivity.class);
			startActivityForResult(intent, 0);
			break;
		}
	}

}