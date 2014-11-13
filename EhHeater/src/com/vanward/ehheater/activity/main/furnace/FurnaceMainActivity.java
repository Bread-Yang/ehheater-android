package com.vanward.ehheater.activity.main.furnace;

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
import android.util.DisplayMetrics;
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
import com.vanward.ehheater.activity.configure.ConnectActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.view.CircleListener;
import com.vanward.ehheater.view.CircularView;
import com.vanward.ehheater.view.DeviceOffUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.vanward.ehheater.view.fragment.BaseSlidingFragmentActivity;
import com.vanward.ehheater.view.fragment.SlidingMenu;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.DERYStatusResp_t;
import com.xtremeprog.xpgconnect.generated.generated;

public class FurnaceMainActivity extends BaseSlidingFragmentActivity implements
		OnClickListener, OnLongClickListener, CircleListener {

	protected SlidingMenu mSlidingMenu;

	private Button btn_top_right, btn_appointment, btn_setting,
			btn_intellectual;

	private TextView mTitleName, tv_mode_tips, tv_status, tv_temperature,
			tv_current_or_setting_temperature_tips, tv_gas_consumption,
			tv_gas_unit;

	private LinearLayout llt_circle;

	private ImageView iv_fire_wave_animation, iv_rotate_animation,
			iv_season_mode;

	private RelativeLayout rlt_content, rlt_open;

	private RadioGroup rg_winner;

	private RadioButton rb_summer, rb_supply_heating, rb_bath;

	private CircularView circularView;

	private boolean isOn = false;

	private CountDownTimer mCountDownTimer;

	private TextView tv_sliding_menu_season_mode;

	private DERYStatusResp_t statusResp = null;

	private boolean Insetting = false;

	private BroadcastReceiver heaterNameChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateTitle();
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
		updateTitle();

		IntentFilter filter = new IntentFilter(
				Consts.INTENT_FILTER_HEATER_NAME_CHANGED);
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				heaterNameChangeReceiver, filter);
		registerSuicideReceiver();

		btn_top_right.post(new Runnable() {

			@Override
			public void run() {
				btn_top_right.setWidth(btn_top_right.getWidth());
				btn_top_right.setHeight(btn_top_right.getHeight());
				generated.SendDERYRefreshReq(Global.connectId);
			}
		});

		HeaterInfo curHeater = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeater();
		String mac = curHeater.getMac();
		String passcode = curHeater.getPasscode();
		String userId = AccountService.getUserId(getBaseContext());
		String userPsw = AccountService.getUserPsw(getBaseContext());

		ConnectActivity.connectToDevice(this, mac, userId, userPsw);
	}

	private void initSlidingMenu() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int mScreenWidth = dm.widthPixels;
		setBehindContentView(R.layout.main_left_fragment);
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setShadowWidth(mScreenWidth / 40);
		mSlidingMenu.setBehindOffset(mScreenWidth / 4);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		mSlidingMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);
		mSlidingMenu.setFadeEnabled(true);
		mSlidingMenu.setBehindScrollScale(0.333f);
	}

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
		llt_circle = (LinearLayout) findViewById(R.id.llt_circle);
		iv_fire_wave_animation = (ImageView) findViewById(R.id.iv_fire_wave_animation);
		iv_rotate_animation = (ImageView) findViewById(R.id.iv_rotate_animation);
		iv_season_mode = (ImageView) findViewById(R.id.iv_season_mode);
	}

	private void setListener() {
		((Button) findViewById(R.id.ivTitleBtnLeft)).setOnClickListener(this);
		btn_top_right.setOnClickListener(this);
		btn_setting.setOnClickListener(this);

		rg_winner.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				switch (checkedId) {
				case R.id.rb_supply_heating:

					rg_winner.setBackgroundResource(R.drawable.home_xuan_bg1);

					OnDERYStatusResp(statusResp, Global.connectId);

					circularView.setAngle(statusResp.getHeatingTemTarget());
					circularView.setTargerdegree(statusResp
							.getHeatingTemTarget());

					break;
				case R.id.rb_bath:

					rg_winner.setBackgroundResource(R.drawable.home_xuan_bg2);

					OnDERYStatusResp(statusResp, Global.connectId);

					circularView.setAngle(statusResp.getBathTemTarget());
					circularView.setTargerdegree(statusResp.getBathTemTarget());
					break;
				}
			}
		});
	}

	private void updateTitle() {
		HeaterInfo heaterInfo = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeater();
		if (heaterInfo != null) {
			mTitleName.setText(Consts.getHeaterName(heaterInfo));
		}
	}

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
		btn_top_right.setBackgroundResource(R.drawable.icon_shut_enable);

		llt_circle.post(new Runnable() {

			@Override
			public void run() {
				circularView = new CircularView(FurnaceMainActivity.this,
						llt_circle, CircularView.CIRCULAR_SINGLE, 0);
				// circularView.setHeat(true);
				circularView.setEndangle(65);
				circularView.setOn(true);
				circularView.setCircularListener(FurnaceMainActivity.this);

				// iv_rotate_animation.setVisibility(View.GONE);
				llt_circle.addView(circularView);
				// circularView.setVisibility(View.GONE);
			}
		});

		((AnimationDrawable) iv_rotate_animation.getDrawable()).start();
		((AnimationDrawable) iv_fire_wave_animation.getDrawable()).start();
	}

	@Override
	public void OnDERYStatusResp(DERYStatusResp_t pResp, int nConnId) {
		if (tv_sliding_menu_season_mode != null) {
			changeSlidingSeasonModeItem(pResp.getSeasonState() == 0 ? FurnaceSeasonActivity.SET_SUMMER_MODE
					: FurnaceSeasonActivity.SET_WINNER_MODE);
		}
		if (nConnId != Global.connectId) {
			return;
		} else {
			statusResp = pResp;
		}

		seasonAndModeDeal(pResp); // switch season and mode
		onOffDeal(pResp);
		gasConsumptionDeal(pResp);

		super.OnDERYStatusResp(pResp, nConnId);
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		if (connId == Global.connectId && event == -7) { // -7:offline, 0 :
															// online
			tv_status.setText(R.string.offline);
			rb_summer.setText(R.string.no_set);
			rb_supply_heating.setText(R.string.no_set);
			rb_bath.setText(R.string.no_set);
			tv_temperature.setText(R.string.no_set);
			tv_gas_consumption.setText(R.string.no_set);
			circularView.setVisibility(View.GONE);
			iv_fire_wave_animation.setVisibility(View.INVISIBLE);
			iv_rotate_animation.setVisibility(View.INVISIBLE);
			btn_setting.setEnabled(false);
			btn_top_right.setBackgroundResource(R.drawable.icon_shut_enable);
			isOn = false;
		} else if (connId == Global.connectId && event == 0) {
			generated.SendDERYRefreshReq(Global.connectId);
		}
	}

	private void onOffDeal(DERYStatusResp_t pResp) {
		if (pResp.getOnOff() == 0) { // shutdown
			setCircularViewEnable(false, pResp);
			tv_status.setText(R.string.shutdown);
			btn_setting.setEnabled(false);
			btn_top_right.setBackgroundResource(R.drawable.icon_shut_enable);
			iv_fire_wave_animation.setVisibility(View.INVISIBLE);
			iv_rotate_animation.setVisibility(View.INVISIBLE);
			isOn = false;
		} else if (pResp.getOnOff() == 1) { // standby
			btn_top_right.setBackgroundResource(R.drawable.icon_shut);
			btn_setting.setEnabled(true);
			isOn = true;
		}
	}

	private void seasonAndModeDeal(DERYStatusResp_t pResp) {
		if (pResp.getSeasonState() == 0) { // summer

			circularView.setEndangle(45); // demo版: 35° - 45°
			circularView.setBeginangle(35);

			rg_winner.setVisibility(View.GONE);
			rb_summer.setVisibility(View.VISIBLE);
			iv_season_mode.setImageResource(R.drawable.mode_icon_summer);

			rb_summer.setText(getResources().getString(R.string.setting)
					+ pResp.getBathTemTarget() + "°");

			if (!Insetting) {
				circularView.setAngle(pResp.getBathTemTarget());
			}
			if (!Insetting) {
				circularView.setTargerdegree(pResp.getBathTemTarget());
			}

			tv_temperature.setText(pResp.getBathTemNow() + "");

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

			if (pResp.getBathWater() == 0 && pResp.getOnOff() == 1) { // 0 :
																		// have
																		// bath
																		// current
				circularView.setVisibility(View.GONE);
				iv_rotate_animation.setVisibility(View.VISIBLE);
				tv_status.setText(R.string.bathing);
				// tv_temperature.setText(pResp.getBathTemNow() + "");
				tv_current_or_setting_temperature_tips
						.setText(R.string.outlet_temperature);
			} else {
				tv_status.setText(R.string.standby);
				circularView.setVisibility(View.VISIBLE);
				tv_current_or_setting_temperature_tips
						.setText(R.string.setting_temperature);
			}

			if (pResp.getFireState() == 0) { // 0 : no flame
				iv_fire_wave_animation.setVisibility(View.INVISIBLE);
				// tv_temperature.setText(pResp.getBathTemTarget() + "");
				// tv_current_or_setting_temperature_tips
				// .setText(R.string.setting_temperature);
			} else if (pResp.getFireState() == 1) { // 1 : have flame
				iv_fire_wave_animation.setVisibility(View.VISIBLE);
			}
		} else if (pResp.getSeasonState() == 1) { // winner

			if (rb_supply_heating.isChecked()) {
				circularView.setBeginangle(30); // demo版: 30° - 80°
				circularView.setEndangle(80);
			} else {
				circularView.setBeginangle(35);
				circularView.setEndangle(45); // demo版: 35° - 45°
			}

			if (pResp.getOnOff() == 1) {
				circularView.setVisibility(View.VISIBLE);
			}

			if (rb_supply_heating.isChecked()) {
				tv_current_or_setting_temperature_tips
						.setText(R.string.heating_temperature);
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

			if (pResp.getHeatingMode() == 0xA0) { // 0xA0 - normal mode
				tv_mode_tips
						.setCompoundDrawablesWithIntrinsicBounds(getResources()
								.getDrawable(R.drawable.mode_icon_normal),
								null, null, null);
				tv_mode_tips.setText(R.string.mode_default);
			} else if (pResp.getHeatingMode() == 0xA1) { // 0xA1 - night mode
				// tv_mode_tips.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
				// R.drawable.mode_icon_comfort), null, null, null);
				tv_mode_tips.setText(R.string.mode_night);
			} else if (pResp.getHeatingMode() == 0xA2) { // 0xA2 - outdoor mode
				tv_mode_tips.setCompoundDrawablesWithIntrinsicBounds(
						getResources()
								.getDrawable(R.drawable.mode_icon_outdoor),
						null, null, null);
				tv_mode_tips.setText(R.string.mode_outdoor);
			}

			if (rg_winner.getVisibility() == View.VISIBLE) {
				if (rb_supply_heating.isChecked()) {
					if (!Insetting) {
						circularView.setAngle(pResp.getHeatingTemTarget());
						circularView.setTargerdegree(pResp
								.getHeatingTemTarget());
					}

					tv_temperature.setText(pResp.getBothTemTarget() + "");
				} else {
					if (!Insetting) {
						circularView.setAngle(pResp.getBathTemTarget());
						circularView.setTargerdegree(pResp.getBathTemTarget());
					}

					if (statusResp.getBathWater() == 0) {
						tv_temperature.setText(pResp.getBathTemNow() + "");
					} else {
						tv_temperature.setText(pResp.getBathTemTarget() + "");
					}
				}
			}

			if (pResp.getFireState() == 0) { // 0 : no flame
				iv_rotate_animation.setVisibility(View.INVISIBLE);
				iv_fire_wave_animation.setVisibility(View.INVISIBLE);
				tv_status.setText(R.string.standby);
			} else if (pResp.getFireState() == 1) { // 1 : have flame
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
					circularView.setVisibility(View.GONE);
				}
				tv_status.setText(R.string.bathing);

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
									R.drawable.mode_icon_comfort), null, null,
							null);
					tv_mode_tips.setText(R.string.mode_comfort);
				}
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
					circularView.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void gasConsumptionDeal(DERYStatusResp_t pResp) {
		if (pResp.getOnOff() == 1) {
			tv_gas_unit.setVisibility(View.VISIBLE);
			tv_gas_consumption.setText(String.valueOf(pResp.getGasCountNow()));
		} else {
			tv_gas_unit.setVisibility(View.GONE);
			tv_gas_consumption.setText(R.string.no_set);
		}
	}

	private void setCircularViewEnable(boolean enable, DERYStatusResp_t pResp) {
		if (enable) {
			circularView.setVisibility(View.VISIBLE);
		} else {
			circularView.setVisibility(View.GONE);
		}
	}

	private void sendToMsgAfterThreeSeconds(final int value,
			final boolean isBathMode) {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
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
				Insetting = false;
			}
		};
		mCountDownTimer.start();
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

		switch (view.getId()) {
		case R.id.ivTitleBtnLeft:
			mSlidingMenu.showMenu(true);
			break;

		case R.id.ivTitleBtnRigh:
			if (isOn) {
				DeviceOffUtil.instance(this)
						.nextButtonCall(new NextButtonCall() {
							@Override
							public void oncall(View v) {
								FurnaceSendMsgModel.closeDevice();
								DeviceOffUtil
										.instance(FurnaceMainActivity.this)
										.dissmiss();
							}
						}).showDialog();
			} else {
				FurnaceSendMsgModel.openDevice();
			}
			break;

		case R.id.btn_setting:
			// Intent intent = new Intent(FurnaceMainActivity.this,
			// FurnacePatternActivity.class);
			// startActivity(intent);
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

	// 转圈停止拖动的时候执行
	@Override
	public void levelListener(int outlevel) {
		// Log.e("levelListener执行了", "outlevel : " + outlevel);
		boolean isBathMode = true;
		if (rg_winner.getCheckedRadioButtonId() == R.id.rb_supply_heating) {
			isBathMode = false;
		}
		sendToMsgAfterThreeSeconds(outlevel, isBathMode);
	}

	// 转圈拖动的时候执行
	@Override
	public void updateUIListener(int outlevel) {
		Log.e("updateUIListener执行了", "outlevel : " + outlevel);

		tv_temperature.setText(outlevel + "");
		tv_current_or_setting_temperature_tips
				.setText(R.string.setting_temperature);
		circularView.setTargerdegree(outlevel);

		// 变小了
		if (circularView.getTargerdegree() > outlevel) {
			// circularView.setTargerdegree(outlevel - 1);

		} else {
			// circularView.setTargerdegree(outlevel + 1);
		}
		Insetting = true;
	}

	// 第一次setListener的时候执行
	@Override
	public void updateUIWhenAferSetListener(int outlevel) {
		// Log.e("updateUIWhenAferSetListener执行了", "outlevel : " + outlevel);
	}

	@Override
	public void updateLocalUIdifferent(int outlevel) {
		// Log.e("updateLocalUIdifferent执行了", "outlevel : " + outlevel);
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

			Global.connectId = connId;

			if (isOnline) {

				boolean shouldExecuteBinding = HeaterInfoService
						.shouldExecuteBinding(curHeater);

				if (shouldExecuteBinding) {
					HeaterInfoService.setBinding(this, did, passcode);
				} else {
					queryState();
				}

				// mSlidingMenu.post(new Runnable() {
				//
				// @Override
				// public void run() {
				// generated.SendDERYRefreshReq(Global.connectId);
				// }
				// });

			} else {
				// TODO 设备不在线
				// stute.setText("设备不在线");
			}

			updateTitle(); // connect回调可能是由于切换了热水器, 需更新title
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

	private void queryState() {

		mSlidingMenu.post(new Runnable() {

			@Override
			public void run() {
				generated.SendDERYRefreshReq(Global.connectId);
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
        XPGConnectClient.RemoveActivity(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        XPGConnectClient.AddActivity(this);
	}
	
	private void registerSuicideReceiver() {

		IntentFilter filter = new IntentFilter(Consts.INTENT_FILTER_KILL_MAIN_ACTIVITY);
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, filter);
	}

}
