package com.vanward.ehheater.activity.main.furnace;

import java.util.logging.SimpleFormatter;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.BaseBusinessActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.MainActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.CheckOnlineUtil;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.view.BaoCircleSlider;
import com.vanward.ehheater.view.BaoCircleSlider.BaoCircleSliderListener;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.DERYStatusResp_t;
import com.xtremeprog.xpgconnect.generated.generated;

public class FurnaceMainActivity extends BaseBusinessActivity implements
		OnClickListener, OnLongClickListener, BaoCircleSliderListener {

	private static final String TAG = "FurnaceMainActivity";

	// protected SlidingMenu mSlidingMenu;

	private Button btn_top_right, btn_appointment, btn_setting,
			btn_intellectual;

	private TextView mTitleName, tv_mode_tips, tv_status, tv_temperature,
			tv_current_or_setting_temperature_tips, tv_gas_consumption,
			tv_gas_unit;

	private ImageView iv_fire_wave_animation, iv_rotate_animation,
			iv_season_mode;

	private Dialog appointmentSwitchSuccessDialog;

	private RelativeLayout rlt_content, rlt_open;

	private RadioGroup rg_winner;

	private RadioButton rb_summer, rb_supply_heating, rb_bath;

	private LinearLayout llt_gas_consumption;

	private boolean isOn = false;

	private CountDownTimer mCountDownTimer;

	private TextView tv_sliding_menu_season_mode;

	public static DERYStatusResp_t statusResp = null;

	private BaoCircleSlider circle_slider;

	/** 指令正在发送中,三秒内不能改变CircleSlider滑动圆圈的位置 */
	private boolean isSendingCommand = false;

	private Dialog turnOffInWinnerDialog;

	private boolean isPowerOffOrOffline;
	
	private boolean firstShowSwitchSuccess = true;

	private BroadcastReceiver heaterNameChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (isFinishing()) {
				return;
			}
			updateTitle(mTitleName);
			initSlidingMenu();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSlidingMenu();
		setContentView(R.layout.activity_furnace_main);
		findViewById();
		setListener();
		init();
		initOpenView();
		updateTitle(mTitleName);

		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				heaterNameChangeReceiver,
				new IntentFilter(Consts.INTENT_FILTER_HEATER_NAME_CHANGED));

		// btn_top_right.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// btn_top_right.setWidth(btn_top_right.getWidth());
		// btn_top_right.setHeight(btn_top_right.getHeight());
		// generated.SendDERYRefreshReq(Global.connectId);
		// }
		// });

		connectCurDevice();
	}

	// private void initSlidingMenu() {
	// DisplayMetrics dm = new DisplayMetrics();
	// getWindowManager().getDefaultDisplay().getMetrics(dm);
	// int mScreenWidth = dm.widthPixels;
	// setBehindContentView(R.layout.main_left_fragment);
	// mSlidingMenu = getSlidingMenu();
	// mSlidingMenu.setMode(SlidingMenu.LEFT);
	// mSlidingMenu.setShadowWidth(mScreenWidth / 40);
	// mSlidingMenu.setBehindOffset(mScreenWidth / 4);
	// mSlidingMenu.setFadeDegree(0.35f);
	// mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	// mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
	// mSlidingMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);
	// mSlidingMenu.setFadeEnabled(true);
	// mSlidingMenu.setBehindScrollScale(0.333f);
	// }

	private void findViewById() {
		btn_top_right = ((Button) findViewById(R.id.ivTitleBtnRigh));
		btn_appointment = ((Button) findViewById(R.id.btn_appointment));
		btn_setting = ((Button) findViewById(R.id.btn_setting));
		btn_intellectual = ((Button) findViewById(R.id.btn_intellectual));
		mTitleName = (TextView) findViewById(R.id.ivTitleName);
		rg_winner = (RadioGroup) findViewById(R.id.rg_winner);
		rb_summer = (RadioButton) findViewById(R.id.rb_summer);
		rb_supply_heating = (RadioButton) findViewById(R.id.rb_supply_heating);
		rb_bath = (RadioButton) findViewById(R.id.rb_bath);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_mode_tips = (TextView) findViewById(R.id.tv_mode_tips);
		tv_temperature = (TextView) findViewById(R.id.tv_temperature);
		tv_current_or_setting_temperature_tips = (TextView) findViewById(R.id.tv_current_or_setting_temperature_tips);
		tv_gas_consumption = (TextView) findViewById(R.id.tv_gas_consumption);
		tv_gas_unit = (TextView) findViewById(R.id.tv_gas_unit);
		rlt_content = (RelativeLayout) findViewById(R.id.rlt_content);
		iv_fire_wave_animation = (ImageView) findViewById(R.id.iv_fire_wave_animation);
		iv_rotate_animation = (ImageView) findViewById(R.id.iv_rotate_animation);
		iv_season_mode = (ImageView) findViewById(R.id.iv_season_mode);
		circle_slider = (BaoCircleSlider) findViewById(R.id.circle_slider);
		llt_gas_consumption = (LinearLayout) findViewById(R.id.llt_gas_consumption);
	}

	private void setListener() {
		((Button) findViewById(R.id.ivTitleBtnLeft)).setOnClickListener(this);
		btn_top_right.setOnClickListener(this);
		btn_appointment.setOnClickListener(this);
		llt_gas_consumption.setOnClickListener(this);
		btn_setting.setOnClickListener(this);
		circle_slider.setCircleSliderListener(this);

		rg_winner.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				switch (checkedId) {
				case R.id.rb_supply_heating:

					rg_winner.setBackgroundResource(R.drawable.home_xuan_bg1);

					if (statusResp != null) {
						OnDERYStatusResp(statusResp, Global.connectId);

						circle_slider.setValue(statusResp.getHeatingTemTarget());
					}

					break;
				case R.id.rb_bath:

					rg_winner.setBackgroundResource(R.drawable.home_xuan_bg2);

					if (statusResp != null) {
						OnDERYStatusResp(statusResp, Global.connectId);

						circle_slider.setValue(statusResp.getBathTemTarget());
					}

					break;
				}
			}
		});
	}

	// private void updateTitle() {
	// HeaterInfo heaterInfo = new HeaterInfoService(getBaseContext())
	// .getCurrentSelectedHeater();
	// if (heaterInfo != null) {
	// mTitleName.setText(Consts.getHeaterName(heaterInfo));
	// }
	// }

	private void initOpenView() {
		rlt_open = (RelativeLayout) LinearLayout.inflate(this,
				R.layout.activity_open, null);
		RelativeLayout.LayoutParams lParams = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		rlt_content.addView(rlt_open, lParams);
		rlt_open.setVisibility(View.GONE);
		rlt_open.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FurnaceSendMsgModel.openDevice();
			}
		});
	}

	private void init() {
		turnOffInWinnerDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.turn_off_in_winter,
						R.string.cancel, R.string.turn_off, null,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								FurnaceSendMsgModel.closeDevice();
								turnOffInWinnerDialog.dismiss();
							}
						});

		appointmentSwitchSuccessDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithOneButton(R.string.switch_success,
						R.string.confirm, null);

		btn_top_right.setBackgroundResource(R.drawable.icon_shut_enable);

		circle_slider.setVisibility(View.GONE);

		((AnimationDrawable) iv_rotate_animation.getDrawable()).start();
		((AnimationDrawable) iv_fire_wave_animation.getDrawable()).start();
	}

	@Override
	public void OnDERYStatusResp(DERYStatusResp_t pResp, int nConnId) {
		super.OnDERYStatusResp(pResp, nConnId);
		DialogUtil.dismissDialog();

		if (tv_sliding_menu_season_mode != null) {
			changeSlidingSeasonModeItem(pResp.getSeasonState() == 0 ? FurnaceSeasonActivity.SET_SUMMER_MODE
					: FurnaceSeasonActivity.SET_WINNER_MODE);
		}

		if (nConnId != Global.connectId) {
			return;
		} else {
			statusResp = pResp;
		}
		stateQueried = true;
		seasonAndModeDeal(pResp); // switch season and mode
		onOffDeal(pResp);
		gasConsumptionDeal(pResp);

	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		if (connId == Global.connectId && event == -7) { // -7:offline, 0 :
															// online
			isPowerOffOrOffline = true;

			statusResp = null;

			tv_status.setText(R.string.offline);
			rb_summer.setText(R.string.no_set);
			rb_supply_heating.setText(R.string.no_set);
			rb_bath.setText(R.string.no_set);
			tv_temperature.setText(R.string.no_set);
			tv_gas_consumption.setText(R.string.no_set);
			circle_slider.setVisibility(View.GONE);
			iv_fire_wave_animation.setVisibility(View.INVISIBLE);
			iv_rotate_animation.setVisibility(View.INVISIBLE);
			btn_appointment.setEnabled(false);
			btn_setting.setEnabled(false);
			btn_intellectual.setEnabled(false);
			btn_top_right.setBackgroundResource(R.drawable.icon_shut_enable);
			isOn = false;

		} /*
		 * else if (connId == Global.connectId && event == 0) {
		 * generated.SendDERYRefreshReq(Global.connectId); }
		 */
	}

	private void onOffDeal(DERYStatusResp_t pResp) {
		if (pResp.getOnOff() == 0) { // shutdown
			isPowerOffOrOffline = true;
			setCircularViewEnable(false, pResp);
			tv_status.setText(R.string.shutdown);
			btn_appointment.setEnabled(false);
			btn_setting.setEnabled(false);
			btn_intellectual.setEnabled(false);
			btn_top_right.setBackgroundResource(R.drawable.icon_shut_enable);
			iv_fire_wave_animation.setVisibility(View.INVISIBLE);
			iv_rotate_animation.setVisibility(View.INVISIBLE);
			isOn = false;
		} else if (pResp.getOnOff() == 1) { // standby
			isPowerOffOrOffline = false;
			btn_top_right.setBackgroundResource(R.drawable.icon_shut);
			if (pResp.getSeasonState() == 1) {
				btn_appointment.setEnabled(true);
				btn_intellectual.setEnabled(true);
			}
			btn_setting.setEnabled(true);
			isOn = true;
		}
	}

	private void seasonAndModeDeal(DERYStatusResp_t pResp) {
		if (pResp.getSeasonState() == 0) { // summer

			btn_appointment.setEnabled(false);
			btn_intellectual.setEnabled(false);

			rg_winner.setVisibility(View.GONE);
			rb_summer.setVisibility(View.VISIBLE);
			iv_season_mode.setImageResource(R.drawable.mode_icon_summer);

			rb_summer.setText(getResources().getString(R.string.setting)
					+ pResp.getBathTemTarget() + "°");

			if (!circle_slider.isDraging() && !isSendingCommand) {
				circle_slider.setValue(pResp.getBathTemTarget());
			}

			if (pResp.getBathMode() == 0) { // 0 - normal bath(temp : 30 - 60)
				tv_mode_tips.setCompoundDrawablesWithIntrinsicBounds(
						getResources().getDrawable(R.drawable.mode_icon_bath),
						null, null, null);
				tv_mode_tips.setText(R.string.mode_bath);
			} else if (pResp.getBathMode() == 1) { // 1 - comfort bath(temp : 35
													// - 45)
				tv_mode_tips.setCompoundDrawablesWithIntrinsicBounds(
						getResources()
								.getDrawable(R.drawable.mode_icon_comfort),
						null, null, null);
				tv_mode_tips.setText(R.string.mode_comfort);
			}

			circle_slider.setVisibility(View.VISIBLE);

			if (pResp.getBathWater() == 0 && pResp.getOnOff() == 1) { // 0 :
																		// have
																		// bath
																		// current
																		// circle_slider.setVisibility(View.GONE);
				iv_rotate_animation.setVisibility(View.VISIBLE);
				tv_status.setText(R.string.bathing);
				// tv_temperature.setText(pResp.getBathTemNow() + "");
				tv_current_or_setting_temperature_tips
						.setText(R.string.outlet_temperature);
				tv_temperature.setText(pResp.getBathTemNow() + "");
			} else {
				tv_status.setText(R.string.standby);
				// circle_slider.setVisibility(View.VISIBLE);
				tv_current_or_setting_temperature_tips
						.setText(R.string.setting_temperature);
				tv_temperature.setText(pResp.getBathTemTarget() + "");
			}

			if (pResp.getFireState() == 0) { // 0 : no flame
				iv_fire_wave_animation.setVisibility(View.INVISIBLE);
				// tv_temperature.setText(pResp.getBathTemTarget() + "");
				// tv_current_or_setting_temperature_tips
				// .setText(R.string.setting_temperature);
			} else if (pResp.getFireState() == 1 && pResp.getOnOff() == 1) { // 1
																				// :
																				// have
																				// flame
				iv_fire_wave_animation.setVisibility(View.VISIBLE);
			}
		} else if (pResp.getSeasonState() == 1) { // winner

			// Log.e("冬季返回来的温度是 : ", pResp.getHeatingTemTarget() + "");

			if (pResp.getHeatingSend() == 0) { // 0 : 散热器
				rb_supply_heating
						.setCompoundDrawablesWithIntrinsicBounds(
								null,
								getResources()
										.getDrawable(
												R.drawable.selector_bg_furnace_main_supply_heating_sanre),
								null, null);
			} else {
				rb_supply_heating
						.setCompoundDrawablesWithIntrinsicBounds(
								null,
								getResources()
										.getDrawable(
												R.drawable.selector_bg_furnace_main_supply_heating_dinuan),
								null, null);
			}

			if (pResp.getOnOff() == 1) {
				circle_slider.setVisibility(View.VISIBLE);
			}

			if (rb_supply_heating.isChecked()) {
				tv_current_or_setting_temperature_tips
						.setText(R.string.heating_temperature);
			} else {
			}

			rg_winner.setVisibility(View.VISIBLE);
			rb_summer.setVisibility(View.GONE);
			iv_season_mode.setImageResource(R.drawable.mode_icon_winner);
			rb_supply_heating.setText(getResources()
					.getString(R.string.setting)
					+ pResp.getHeatingTemTarget()
					+ "°");
			rb_bath.setText(getResources().getString(R.string.setting)
					+ pResp.getBathTemTarget() + "°");

			if (rg_winner.getVisibility() == View.VISIBLE) {
				if (rb_supply_heating.isChecked()) {
					if (!circle_slider.isDraging() && !isSendingCommand) {
						circle_slider.setValue(pResp.getHeatingTemTarget());
					}

					tv_temperature.setText(pResp.getBothTemTarget() + "");

					if (pResp.getHeatingMode() == 0xA0) { // 0xA0 - normal mode
						tv_mode_tips.setCompoundDrawablesWithIntrinsicBounds(
								getResources().getDrawable(
										R.drawable.mode_icon_normal), null,
								null, null);
						tv_mode_tips.setText(R.string.mode_default);
					} else if (pResp.getHeatingMode() == 0xA1) { // 0xA1 - night
																	// mode
						tv_mode_tips.setCompoundDrawablesWithIntrinsicBounds(
								getResources().getDrawable(
										R.drawable.mode_icon_night), null,
								null, null);
						tv_mode_tips.setText(R.string.mode_night);
					} else if (pResp.getHeatingMode() == 0xA2) { // 0xA2 -
																	// outdoor
																	// mode
						tv_mode_tips.setCompoundDrawablesWithIntrinsicBounds(
								getResources().getDrawable(
										R.drawable.mode_icon_outdoor), null,
								null, null);
						tv_mode_tips.setText(R.string.mode_outdoor);
						circle_slider.setVisibility(View.INVISIBLE);
					}
				} else {
					if (!circle_slider.isDraging() && !isSendingCommand) {
						circle_slider.setValue(pResp.getBathTemTarget());
					}

					if (statusResp.getBathWater() == 0) {
						tv_temperature.setText(pResp.getBathTemNow() + "");
					} else {
						tv_temperature.setText(pResp.getBathTemTarget() + "");
					}

					if (pResp.getBathMode() == 0) { // 0 - normal bath(temp : 30
						// - 60)
						tv_mode_tips.setCompoundDrawablesWithIntrinsicBounds(
								getResources().getDrawable(
										R.drawable.mode_icon_bath), null, null,
								null);
						tv_mode_tips.setText(R.string.mode_bath);
					} else if (pResp.getBathMode() == 1) { // 1 - comfort
						// bath(temp : 35 -
						// 45)
						tv_mode_tips.setCompoundDrawablesWithIntrinsicBounds(
								getResources().getDrawable(
										R.drawable.mode_icon_comfort), null,
								null, null);
						tv_mode_tips.setText(R.string.mode_comfort);
					}
				}
			}

			if (pResp.getFireState() == 0) { // 0 : no flame
				iv_rotate_animation.setVisibility(View.INVISIBLE);
				iv_fire_wave_animation.setVisibility(View.INVISIBLE);
				tv_status.setText(R.string.standby);
			} else if (pResp.getFireState() == 1 && pResp.getOnOff() == 1) { // 1
																				// :
																				// have
																				// flame
				if ((rb_bath.isChecked() && pResp.getBathWater() == 0)
						|| rb_supply_heating.isChecked()) {
					iv_fire_wave_animation.setVisibility(View.VISIBLE);
				}
				if (rb_supply_heating.isChecked()) {
					if (pResp.getOnOff() == 1) {
						tv_status.setText(R.string.supplying_heat);
					}
				}
			}

			if (pResp.getBathWater() == 0 && pResp.getOnOff() == 1) { // 0 :
																		// have
																		// bath
																		// current
				iv_rotate_animation.setVisibility(View.VISIBLE);
				tv_current_or_setting_temperature_tips
						.setText(R.string.outlet_temperature);
				// when bathing, furnace bathing temperature can't be set.
				if (rg_winner.getCheckedRadioButtonId() == R.id.rb_bath) {
					// circle_slider.setVisibility(View.GONE);
					circle_slider.setVisibility(View.VISIBLE);
				}
				tv_status.setText(R.string.bathing);
			} else { // 1 : no bath current
				iv_rotate_animation.setVisibility(View.INVISIBLE);
				if (rb_bath.isChecked()) {
					iv_fire_wave_animation.setVisibility(View.INVISIBLE);
				}
				if (rb_bath.isChecked()) {
					tv_current_or_setting_temperature_tips
							.setText(R.string.setting_temperature);
				}
				if (rg_winner.getCheckedRadioButtonId() == R.id.rb_bath) {
					circle_slider.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void gasConsumptionDeal(DERYStatusResp_t pResp) {
		if (pResp.getOnOff() == 1) {
			tv_gas_unit.setVisibility(View.VISIBLE);
			tv_gas_consumption.setText(String.valueOf(pResp.getGasCountNow()));
			Log.e("实时燃气量是 : ", String.valueOf(pResp.getGasCountNow()));
			Log.e("累计燃气量是 : ", String.valueOf(pResp.getGasCount()));
		} else {
			tv_gas_unit.setVisibility(View.GONE);
			tv_gas_consumption.setText(R.string.no_set);
		}
	}

	private void setCircularViewEnable(boolean enable, DERYStatusResp_t pResp) {
		if (enable) {
			circle_slider.setVisibility(View.VISIBLE);
		} else {
			circle_slider.setVisibility(View.GONE);
		}
	}

	private void sendToMsgAfterThreeSeconds(final int value,
			final boolean isBathMode) {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		isSendingCommand = true;
		mCountDownTimer = new CountDownTimer(3000, 1000) {

			@Override
			public void onTick(long arg0) {
			}

			@Override
			public void onFinish() {
				if (isBathMode) {
					FurnaceSendMsgModel.setBathTemperature(value);
				} else {
					FurnaceSendMsgModel.setHeatingTemperature(value);
				}
				isSendingCommand = false;
			}
		};
		mCountDownTimer.start();
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		Intent intent;
		switch (view.getId()) {
		case R.id.ivTitleBtnLeft:
			mSlidingMenu.showMenu(true);
			break;

		case R.id.ivTitleBtnRigh:
			if (statusResp == null) {
				DialogUtil.instance().showReconnectDialog(this);
			} else {
				if (isOn) {
					// if (statusResp.getSeasonState() == 0) {
					// DeviceOffUtil.instance(this)
					// .nextButtonCall(new NextButtonCall() {
					// @Override
					// public void oncall(View v) {
					// FurnaceSendMsgModel.closeDevice();
					// DeviceOffUtil.instance(
					// FurnaceMainActivity.this)
					// .dissmiss();
					// }
					// }).showDialog();
					// } else {
					// turnOffInWinnerDialog.show();
					// }
					turnOffInWinnerDialog.show();
				} else {
					FurnaceSendMsgModel.openDevice();
				}
			}
			break;

		case R.id.btn_appointment:
			if (Global.isForExhibition) {
				intent = new Intent(FurnaceMainActivity.this,
						FurnaceAppointmentList4ExhibitionActivity.class);
			} else {
				intent = new Intent(FurnaceMainActivity.this,
						FurnaceAppointmentListActivity.class);
			}
			startActivity(intent);
			break;

		case R.id.llt_gas_consumption:
			intent = new Intent(FurnaceMainActivity.this,
					FurnaceGasConsumptionActivity.class);
			intent.putExtra("isPowerOffOrOffline", isPowerOffOrOffline);
			startActivity(intent);
			break;

		case R.id.btn_setting:
			intent = new Intent(FurnaceMainActivity.this,
					FurnacePatternActivity.class);
			if (statusResp.getBathMode() == 0) { // 0 - normal bath
				intent.putExtra("bathMode", "normal");
			} else {
				intent.putExtra("bathMode", "comfort");
			}
			if (statusResp.getHeatingMode() == 0xA0) { // 0xA0 - normal mode
				intent.putExtra("heatingMode", "normal");
			} else if (statusResp.getHeatingMode() == 0xA1) { // 0xA1 - night
																// mode
				intent.putExtra("heatingMode", "night");
			} else if (statusResp.getHeatingMode() == 0xA2) { // 0xA2 - outdoor
																// mode
				intent.putExtra("heatingMode", "outdoor");
			}
			intent.putExtra(
					"seasonMode",
					statusResp.getSeasonState() == 0 ? FurnaceSeasonActivity.SET_SUMMER_MODE
							: FurnaceSeasonActivity.SET_WINNER_MODE);
			startActivity(intent);
			break;

		case R.id.btn_intellectual:
			intent = new Intent(FurnaceMainActivity.this,
					FurnaceIntelligentControlActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onLongClick(View arg0) {
		return false;
	}

	public TextView getTv_sliding_menu_season_mode() {
		return tv_sliding_menu_season_mode;
	}

	public void setTv_sliding_menu_season_mode(
			TextView tv_sliding_menu_season_mode) {
		this.tv_sliding_menu_season_mode = tv_sliding_menu_season_mode;

		tv_sliding_menu_season_mode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mSlidingMenu.toggle();
					}
				}, 500);

			}
		});
	}

	private void changeSlidingSeasonModeItem(int seasonMode) {
		if (seasonMode == FurnaceSeasonActivity.SET_SUMMER_MODE) {
			tv_sliding_menu_season_mode.setText(R.string.summer_mode);
			tv_sliding_menu_season_mode
					.setCompoundDrawablesWithIntrinsicBounds(getResources()
							.getDrawable(R.drawable.setting_icon_xiatian),
							null, null, null);
			tv_sliding_menu_season_mode
					.setTag(FurnaceSeasonActivity.SET_SUMMER_MODE);

		} else {
			tv_sliding_menu_season_mode.setText(R.string.winner_mode);
			tv_sliding_menu_season_mode
					.setCompoundDrawablesWithIntrinsicBounds(getResources()
							.getDrawable(R.drawable.setting_icon_dongtian),
							null, null, null);
			tv_sliding_menu_season_mode
					.setTag(FurnaceSeasonActivity.SET_WINNER_MODE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Consts.REQUESTCODE_CONNECT_ACTIVITY
				&& resultCode == RESULT_OK) {

			int connId = data.getIntExtra(Consts.INTENT_EXTRA_CONNID, -1);
			boolean isOnline = data.getBooleanExtra(
					Consts.INTENT_EXTRA_ISONLINE, true);
			String did = data.getStringExtra(Consts.INTENT_EXTRA_DID);
			String passcode = data.getStringExtra(Consts.INTENT_EXTRA_PASSCODE);

			HeaterInfoService hser = new HeaterInfoService(getBaseContext());
			HeaterInfo curHeater = hser.getCurrentSelectedHeater();

			if (!TextUtils.isEmpty(passcode)) {
				curHeater.setPasscode(passcode);
			}
			if (!TextUtils.isEmpty(did)) {
				curHeater.setDid(did);
			}

			new HeaterInfoDao(getBaseContext()).save(curHeater);

			if (isOnline) {
				Global.connectId = connId;
				Global.checkOnlineConnId = -1;

				boolean shouldExecuteBinding = HeaterInfoService
						.shouldExecuteBinding(curHeater);

				if (shouldExecuteBinding) {
					HeaterInfoService.setBinding(this, did, passcode);
				} else {
					queryState();
				}

				if (getIntent().getBooleanExtra("switchSuccess", false) && firstShowSwitchSuccess) {
					appointmentSwitchSuccessDialog.show();
					firstShowSwitchSuccess = false;
				}

			} else {

				Global.connectId = -1;
				Global.checkOnlineConnId = connId;

				changeToOfflineUI();

				DialogUtil.instance().showReconnectDialog(new Runnable() {
					@Override
					public void run() {
						CheckOnlineUtil.ins().start(getBaseContext());
					}
				}, this);
			}

			mSlidingMenu.showContent();

		} else if (tv_sliding_menu_season_mode != null
				&& resultCode == RESULT_OK && data != null) {
			int seasonMode = data.getIntExtra("seasonMode",
					FurnaceSeasonActivity.SET_SUMMER_MODE);
			changeSlidingSeasonModeItem(seasonMode);
			if (seasonMode == FurnaceSeasonActivity.SET_SUMMER_MODE) {
				rb_summer.setVisibility(View.VISIBLE);
				rg_winner.setVisibility(View.GONE);
				iv_season_mode.setImageResource(R.drawable.mode_icon_summer);
			} else {
				rb_summer.setVisibility(View.GONE);
				rg_winner.setVisibility(View.VISIBLE);
				iv_season_mode.setImageResource(R.drawable.mode_icon_winner);
			}
		}

		if (requestCode == Consts.REQUESTCODE_UPLOAD_BINDING) {

			HeaterInfoService hser = new HeaterInfoService(getBaseContext());
			HeaterInfo curHeater = hser.getCurrentSelectedHeater();

			if (resultCode == RESULT_OK) {
				// binded
				new HeaterInfoService(getBaseContext()).updateBinded(
						curHeater.getMac(), true);
			}

			queryState();

		}
	}

	private boolean stateQueried;

	private void queryState() {
		// DialogUtil.instance().showQueryingDialog(this);
		DialogUtil.instance().showLoadingDialog(this, "");
		stateQueried = false;
		generated.SendDERYRefreshReq(Global.connectId);
		mSlidingMenu.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!stateQueried) {
					changeToOfflineUI();
					DialogUtil.instance().showReconnectDialog(
							FurnaceMainActivity.this);
				}

			}
		}, MainActivity.connectTime);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				heaterNameChangeReceiver);
	}

	@Override
	public void didBeginTouchCircleSlider() {
		// Log.e(TAG, "didBeginTouchCircleSlider");
	}

	@Override
	public void needChangeValue(int value, boolean isAdd) {
		if (rg_winner.getVisibility() == View.VISIBLE
				&& rb_supply_heating.isChecked()) {
			// 若是在散热器供暖下：温度调节范围30~80℃，温度可在此范围内调节
			// 若是在地暖供暖下 ： 温度调节范围30~55℃，温度可在此范围内调节

			if (statusResp.getHeatingSend() == 0) { // 0 : 散热器
				if (80 >= value && value >= 30) {
					tv_temperature.setText(value + "");
					tv_current_or_setting_temperature_tips
							.setText(R.string.setting_temperature);
					circle_slider.setValue(value);
				}
			} else {
				if (55 >= value && value >= 30) {
					tv_temperature.setText(value + "");
					tv_current_or_setting_temperature_tips
							.setText(R.string.setting_temperature);
					circle_slider.setValue(value);
				}
			}
		} else {
			if (statusResp.getBathWater() == 0 && statusResp.getOnOff() == 1) {
				if (statusResp.getBathMode() == 0) { // 0 - normal bath
					if (48 < value || value < 30) {
						return;
					}
				} else {
					if (35 > value || value > 45) {
						return;
					}
				}
			} else {
				if (statusResp.getBathMode() == 0) { // 0 - normal bath
					if (60 < value || value < 30) {
						return;
					}
				} else {
					if (35 > value || value > 45) {
						return;
					}
				}
			}
			tv_temperature.setText(value + "");
			tv_current_or_setting_temperature_tips
					.setText(R.string.setting_temperature);
			circle_slider.setValue(value);
		}
	}

	@Override
	public void didEndChangeValue() {
		// Log.e(TAG, "didEndChangeValue");
		boolean isBathMode = true;
		if (rg_winner.getVisibility() == View.VISIBLE
				&& rg_winner.getCheckedRadioButtonId() == R.id.rb_supply_heating) {
			isBathMode = false;
		}
		sendToMsgAfterThreeSeconds(circle_slider.getValue(), isBathMode);
	}

	@Override
	protected void changeToOfflineUI() {

		try {
			tv_status.setText(R.string.offline);
			rb_summer.setText(R.string.no_set);
			rb_supply_heating.setText(R.string.no_set);
			rb_bath.setText(R.string.no_set);
			tv_temperature.setText(R.string.no_set);
			tv_gas_consumption.setText(R.string.no_set);
			circle_slider.setVisibility(View.GONE);
			iv_fire_wave_animation.setVisibility(View.INVISIBLE);
			iv_rotate_animation.setVisibility(View.INVISIBLE);
			btn_appointment.setEnabled(false);
			btn_setting.setEnabled(false);
			btn_intellectual.setEnabled(false);
			btn_top_right.setBackgroundResource(R.drawable.icon_shut_enable);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
