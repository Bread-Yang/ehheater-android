package com.vanward.ehheater.activity.main.furnace;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.AccountDao;
import com.vanward.ehheater.model.AppointmentVo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.view.SeekBarHint;
import com.vanward.ehheater.view.SeekBarHint.OnSeekBarHintProgressChangeListener;
import com.vanward.ehheater.view.wheelview.WheelView;
import com.vanward.ehheater.view.wheelview.adapters.NumericWheelAdapter;

public class FurnaceAppointmentTimeActivity extends EhHeaterBaseActivity {

	private WheelView wheelView1, wheelView2;

	private ToggleButton tb_switch;

	private RadioGroup rg_mode;

	private SeekBarHint seekBar;

	private CheckBox cb_Monday, cb_Thuesday, cb_Wednesday, cb_Thursday,
			cb_Friday, cb_Saturday, cb_Sunday;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

	private AppointmentVo editModel;

	private HeaterInfo heaterInfo;

	private Dialog appointmentConflictDialog, appointmentFullDialog,
			appointmentExecuteTomorrowDialog;

	private boolean isEdit, isOverride;

	private String conflictName = "";

	private long conflictTime;

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
		}

		;
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

		// IntelligentPatternUtil.addLastTemperature(this, 11);
		// IntelligentPatternUtil.addLastTemperature(this, 21);
		// IntelligentPatternUtil.addLastTemperature(this, 21);
		// IntelligentPatternUtil.addLastTemperature(this, 11);
		// IntelligentPatternUtil.addLastTemperature(this, 11);
		// IntelligentPatternUtil.addLastTemperature(this, 11);
		// IntelligentPatternUtil.addLastTemperature(this, 21);
		//
		// IntelligentPatternUtil.addLastPower(this, 3);
		// IntelligentPatternUtil.addLastPower(this, 3);
		// IntelligentPatternUtil.addLastPower(this, 3);
		// IntelligentPatternUtil.addLastPower(this, 3);
		// IntelligentPatternUtil.addLastPower(this, 3);
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
		boolean isFloorHeating = getIntent().getBooleanExtra("floor_heating",
				false);
		L.e(this, "isfloorHeating : " + isFloorHeating);
		// 若是在散热器供暖下：温度调节范围30~80℃，温度可在此范围内调节
		// 若是在地暖供暖下 ： 温度调节范围30~55℃，温度可在此范围内调节
		if (isFloorHeating) {
			seekBar.setMax(25);
			((TextView)findViewById(R.id.tv_max_value)).setText("55℃");
		} else {
			seekBar.setMax(50);
		}
		
		appointmentConflictDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.appointment_conflict,
						BaoDialogShowUtil.DEFAULT_RESID, R.string.override,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								finish();
							}
						}, new OnClickListener() {

							@Override
							public void onClick(View v) {
								isOverride = true;
								btn_right.performClick();
							}
						});

		appointmentFullDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithOneButton(R.string.furnace_appointment_full,
						BaoDialogShowUtil.DEFAULT_RESID, new OnClickListener() {

							@Override
							public void onClick(View v) {
								appointmentFullDialog.dismiss();
								finish();
							}
						});
		appointmentExecuteTomorrowDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithOneButton(
						R.string.appointment_execute_tomorrow,
						BaoDialogShowUtil.DEFAULT_RESID, new OnClickListener() {

							@Override
							public void onClick(View v) {
								appointmentExecuteTomorrowDialog.dismiss();
								finish();
							}
						});

		heaterInfo = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeater();

		String requestURL = "userinfo/getUsageInformation?uid="
				+ AccountService.getUserId(getBaseContext());

		// executeRequest(Consts.REQUEST_BASE_URL + requestURL, null, new
		// AjaxCallBack<String>() {
		// public void onSuccess(String jsonString) {
		// JSONObject json;
		// try {
		// json = new JSONObject(jsonString);
		// String responseCode = json
		// .getString("responseCode");
		// if ("200".equals(responseCode)) {
		// JSONObject result = json
		// .getJSONObject("result");
		// nickName = result.getString("userName");
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// });

		// mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
		// null, new AjaxCallBack<String>() {
		// public void onSuccess(String jsonString) {
		// JSONObject json;
		// try {
		// json = new JSONObject(jsonString);
		// String responseCode = json
		// .getString("responseCode");
		// if ("200".equals(responseCode)) {
		// JSONObject result = json
		// .getJSONObject("result");
		// nickName = result.getString("userName");
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// });

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

			L.e(this, "getDateTime : " + editModel.getDateTime());
			L.e(this, "time : " + time);

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
							cb_Sunday.setChecked(true);
							break;
						case 1:
							cb_Monday.setChecked(true);
							break;
						case 2:
							cb_Thuesday.setChecked(true);
							break;
						case 3:
							cb_Wednesday.setChecked(true);
							break;
						case 4:
							cb_Thursday.setChecked(true);
							break;
						case 5:
							cb_Friday.setChecked(true);
							break;
						case 6:
							cb_Saturday.setChecked(true);
							break;
						}
					}
				}

			}
		} else {
			setTopText(R.string.add);
			editModel = new AppointmentVo();
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

				int setHour = wheelView1.getCurrentItem();

				int setMinute = wheelView2.getCurrentItem();

				// L.e(this, "hour是 : " + hour);
				// L.e(this, "minute是 : " + minute);

				Calendar c = Calendar.getInstance();
				c.setTime(new Date());

				int currentHour = c.get(Calendar.HOUR_OF_DAY);
				int currentMinute = c.get(Calendar.MINUTE);

				L.e(this, "currentHour : " + currentHour);
				L.e(this, "currentMinute : " + currentMinute);

				L.e(this, "setHour : " + setHour);
				L.e(this, "setMinute : " + setMinute);

				final boolean isBeforeCurrentTime = setHour < currentHour
						|| ((setHour == currentHour) && (setMinute <= currentMinute));

				// 如果小于或等于当前系统时间,则加一天时间
				if (isBeforeCurrentTime) {
					c.add(Calendar.DAY_OF_MONTH, 1);
				}

				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DAY_OF_MONTH);
				int second = c.get(Calendar.SECOND);

				c.set(year, month, day, setHour, setMinute, 0);

				// Date date = new Date();
				// date.setHours(setHour);
				// date.setMinutes(setMinute);
				// long timestamp = date.getTime();

				long timestamp = c.getTimeInMillis() / 1000 * 1000; // 将毫秒统一设为000

				editModel.setDateTime(timestamp);

				// L.e(this, "timestamp是 : " + timestamp);
				//
				String time = dateFormat.format(new Date(timestamp));
				//
				// L.e(this, "time 是 : " + time);

				editModel.setTemper(String.valueOf(seekBar.getProgress() + 30));

				StringBuilder week = new StringBuilder(editModel.getWeek());

				week.setCharAt(1, cb_Monday.isChecked() ? '1' : '0'); // Monday
				week.setCharAt(2, cb_Thuesday.isChecked() ? '1' : '0'); // Thuesday
				week.setCharAt(3, cb_Wednesday.isChecked() ? '1' : '0'); // Wednesday
				week.setCharAt(4, cb_Thursday.isChecked() ? '1' : '0'); // Thursday
				week.setCharAt(5, cb_Friday.isChecked() ? '1' : '0'); // Friday
				week.setCharAt(6, cb_Saturday.isChecked() ? '1' : '0'); // Saturday
				week.setCharAt(0, cb_Sunday.isChecked() ? '1' : '0'); // Sunday

				editModel.setWeek(week.toString());

				if ("0000000".equals(editModel.getWeek())) {
					editModel.setLoopflag(0);
				} else if ("1111111".equals(editModel.getWeek())) {
					editModel.setLoopflag(1);
				} else {
					editModel.setLoopflag(2);
				}

				// for test

				// editModel.setName(nickName);

				editModel.setName(new AccountDao(getApplicationContext())
						.getNicknameByUid((AccountService
								.getUserId(getBaseContext()))));

				editModel.setDid(heaterInfo.getDid());

				editModel.setUid(AccountService.getUserId(getBaseContext()));

				editModel.setPasscode(heaterInfo.getPasscode());

				editModel.setDeviceType(1);

				// 热水器多余的数据
				editModel.setPeopleNum("0");

				editModel.setPower("0");

				// String url = "userinfo/updateAppointment";

				String requestURL = null;
				if (isEdit) {
					requestURL = "userinfo/updateAppointment";
				} else {
					editModel.setIsAppointmentOn(1); // 添加完后,默认打开
					requestURL = "userinfo/saveAppointment";
				}

				Gson gson = new Gson();
				String json = gson.toJson(editModel);

				AjaxParams params = new AjaxParams();
				params.put("data", json);
				if (isOverride) {
					params.put("ignoreConflict", "true");
				}
				
				L.e(FurnaceAppointmentTimeActivity.this, "data : " + params);
				
				L.e(FurnaceAppointmentTimeActivity.this, "请求的URL : " + Consts.REQUEST_BASE_URL + requestURL);

				executeRequest(Consts.REQUEST_BASE_URL + requestURL, params,
						new AjaxCallBack<String>() {

							@Override
							public void onSuccess(String jsonString) {
								super.onSuccess(jsonString);
								isOverride = false;
								try {
									JSONObject json = new JSONObject(jsonString);
									String responseCode = json
											.getString("responseCode");
									if ("200".equals(responseCode)) {
										boolean isAllUncheck = !cb_Monday
												.isChecked()
												&& !cb_Thuesday.isChecked()
												&& !cb_Wednesday.isChecked()
												&& !cb_Thursday.isChecked()
												&& !cb_Friday.isChecked()
												&& !cb_Saturday.isChecked()
												&& !cb_Sunday.isChecked();
										// 如果小于或等于当前系统时间,则加一天时间
										if (isBeforeCurrentTime) {
											if (isAllUncheck) { // 没有选择循环的时候,点击确认后,弹出提示框
												appointmentExecuteTomorrowDialog
														.show();
											} else {
												finish();
											}
										} else {
											finish();
										}
									} else if ("501".equals(responseCode)) {
										JSONObject result = json
												.getJSONObject("result");
										conflictName = result.getString("name");
										conflictTime = result
												.getLong("dateTime");
										tipsHandler.sendEmptyMessage(0);
									} else if ("403".equals(responseCode)) { // 预约满了
										tipsHandler.sendEmptyMessage(1);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(Throwable t, int errorNo,
									String strMsg) {
								super.onFailure(t, errorNo, strMsg);
								isOverride = false;
							}
						});

				// mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL)
				// .executePost(params, new AjaxCallBack<String>() {
				//
				// @Override
				// public void onSuccess(String jsonString) {
				// super.onSuccess(jsonString);
				// isOverride = false;
				// try {
				// JSONObject json = new JSONObject(jsonString);
				// String responseCode = json
				// .getString("responseCode");
				// if ("200".equals(responseCode)) {
				// boolean isAllUncheck = !cb_Monday
				// .isChecked()
				// && !cb_Thuesday.isChecked()
				// && !cb_Wednesday.isChecked()
				// && !cb_Thursday.isChecked()
				// && !cb_Friday.isChecked()
				// && !cb_Saturday.isChecked()
				// && !cb_Sunday.isChecked();
				// // 如果小于或等于当前系统时间,则加一天时间
				// if (isBeforeCurrentTime) {
				// if (isAllUncheck) { // 没有选择循环的时候,点击确认后,弹出提示框
				// appointmentExecuteTomorrowDialog
				// .show();
				// } else {
				// finish();
				// }
				// } else {
				// finish();
				// }
				// } else if ("501".equals(responseCode)) {
				// JSONObject result = json
				// .getJSONObject("result");
				// conflictName = result.getString("name");
				// conflictTime = result
				// .getLong("dateTime");
				// tipsHandler.sendEmptyMessage(0);
				// } else if ("403".equals(responseCode)) { // 预约满了
				// tipsHandler.sendEmptyMessage(1);
				// }
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
				// }
				//
				// @Override
				// public void onFailure(Throwable t, int errorNo,
				// String strMsg) {
				// super.onFailure(t, errorNo, strMsg);
				// isOverride = false;
				// }
				// });
			}
		});
	}
}
