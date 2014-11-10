package com.vanward.ehheater.activity.main.furnace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
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
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.view.CircleListener;
import com.vanward.ehheater.view.CircularView;
import com.vanward.ehheater.view.DeviceOffUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.vanward.ehheater.view.fragment.BaseSlidingFragmentActivity;
import com.vanward.ehheater.view.fragment.SlidingMenu;
import com.xtremeprog.xpgconnect.generated.DERYStatusResp_t;
import com.xtremeprog.xpgconnect.generated.generated;

public class FurnaceMainActivity extends BaseSlidingFragmentActivity implements
		OnClickListener, OnLongClickListener, CircleListener {

	protected SlidingMenu mSlidingMenu;

	private Button btn_top_right, btn_appointment, btn_setting,
			btn_intellectual;

	private TextView mTitleName, tv_mode_tips, tv_status, tv_temperature,
			tv_current_or_setting_temperature_tips, tv_gas_consumption;

	private LinearLayout llt_circle;

	private ImageView iv_fire_wave_animation, iv_rotate_animation,
			iv_season_mode;

	private RelativeLayout rlt_content, rlt_open;

	private RadioGroup rg_winner;

	private RadioButton rb_summer;

	private CircularView circularView;

	private boolean isOn = false;

	private CountDownTimer mCountDownTimer;

	private TextView tv_sliding_menu_season_mode;

	private boolean isBathingInWinnerMode = false;

	private DERYStatusResp_t statusResp = null;

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

		btn_top_right.post(new Runnable() {

			@Override
			public void run() {
				generated.SendStateReq(Global.connectId);
			}
		});
		
		HeaterInfo curHeater = new HeaterInfoService(getBaseContext()).getCurrentSelectedHeater();
		String mac = curHeater.getMac();
		String passcode = curHeater.getPasscode();
		String userId = AccountService.getUserId(getBaseContext());
		String userPsw = AccountService.getUserPsw(getBaseContext());
		
		ConnectActivity.connectToDevice(this, mac, passcode, userId, userPsw);
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
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_mode_tips = (TextView) findViewById(R.id.tv_mode_tips);
		tv_temperature = (TextView) findViewById(R.id.tv_temperature);
		tv_current_or_setting_temperature_tips = (TextView) findViewById(R.id.tv_current_or_setting_temperature_tips);
		tv_gas_consumption = (TextView) findViewById(R.id.tv_gas_consumption);
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
				case R.id.rb_bath:
					rg_winner.setBackgroundResource(R.drawable.home_xuan_bg2);
					if (isBathingInWinnerMode) {
						circularView.setVisibility(View.GONE);
					}
					if (statusResp != null) {
						tv_temperature.setText(statusResp.getBathTemNow());
					}
					tv_current_or_setting_temperature_tips
							.setText(R.string.outlet_temperature);
					break;
				case R.id.rb_supply_heating:
					rg_winner.setBackgroundResource(R.drawable.home_xuan_bg1);
					circularView.setVisibility(View.VISIBLE);
					if (statusResp != null) {
						tv_temperature.setText(statusResp.getHeatingTemTarget());
					}
					tv_current_or_setting_temperature_tips
							.setText(R.string.heating_temperature);
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
		llt_circle.post(new Runnable() {

			@Override
			public void run() {
				circularView = new CircularView(FurnaceMainActivity.this,
						llt_circle, CircularView.CIRCULAR_SINGLE, 0);
				circularView.setHeat(true);
				circularView.setEndangle(65);
				circularView.setAngle(35);
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

		if (nConnId != Global.connectId) {
			return;
		} else {
			statusResp = pResp;
		}

		onOffDeal(pResp);
		if (pResp.getOnOff() == 1) {
			seasonAndModeDeal(pResp); // switch season and mode
		}
		gasConsumptionDeal(pResp);

		super.OnDERYStatusResp(pResp, nConnId);
	}

	private void onOffDeal(DERYStatusResp_t pResp) {
		if (pResp.getOnOff() == 0) { // shutdown
			setCircularViewEnable(false, pResp);
			tv_status.setText(R.string.shutdown);
			btn_setting.setEnabled(false);
			iv_fire_wave_animation.setVisibility(View.GONE);
			iv_rotate_animation.setVisibility(View.GONE);
			isOn = false;
		} else if (pResp.getOnOff() == 1) { // standby
			setCircularViewEnable(true, pResp);
			tv_status.setText(R.string.standby);
			btn_setting.setEnabled(true);
			isOn = true;
		}
	}

	private void seasonAndModeDeal(DERYStatusResp_t pResp) {
		if (pResp.getSeasonState() == 0) { // summer
			rg_winner.setVisibility(View.GONE);
			rb_summer.setVisibility(View.VISIBLE);
			iv_season_mode.setImageResource(R.drawable.mode_icon_summer);

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

			if (pResp.getBathWater() == 0) { // 0 : have bath current
				circularView.setVisibility(View.GONE);
				iv_rotate_animation.setVisibility(View.VISIBLE);
				tv_status.setText(R.string.bathing);
				tv_temperature.setText(pResp.getBathTemNow());
				tv_current_or_setting_temperature_tips
						.setText(R.string.outlet_temperature);
			} else {
				circularView.setVisibility(View.VISIBLE);
			}

			if (pResp.getFireState() == 0) { // 0 : no flame
				iv_fire_wave_animation.setVisibility(View.GONE);
				tv_temperature.setText(pResp.getBathTemTarget());
				tv_current_or_setting_temperature_tips
						.setText(R.string.setting_temperature);
			} else if (pResp.getFireState() == 1) { // 1 : have flame
				iv_fire_wave_animation.setVisibility(View.VISIBLE);
			}
		} else if (pResp.getSeasonState() == 1) { // winner
			rg_winner.setVisibility(View.VISIBLE);
			rb_summer.setVisibility(View.GONE);
			iv_season_mode.setImageResource(R.drawable.mode_icon_winner);

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

			if (pResp.getFireState() == 0) { // 0 : no flame
				iv_fire_wave_animation.setVisibility(View.GONE);
			} else if (pResp.getFireState() == 1) { // 1 : have flame
				iv_fire_wave_animation.setVisibility(View.VISIBLE);
				if (pResp.getBathWater() == 0) { // 0 : have bath current
					isBathingInWinnerMode = true;
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
										R.drawable.mode_icon_comfort), null,
								null, null);
						tv_mode_tips.setText(R.string.mode_comfort);
					}
				} else { // 1 : no bath current
					isBathingInWinnerMode = false;
					if (rg_winner.getCheckedRadioButtonId() == R.id.rb_bath) {
						circularView.setVisibility(View.VISIBLE);
					}
					tv_status.setText(R.string.supplying_heat);
				}
			}
		}
	}

	private void gasConsumptionDeal(DERYStatusResp_t pResp) {
		tv_gas_consumption.setText(String.valueOf(pResp.getGasCountNow()));
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
			Intent intent = new Intent(FurnaceMainActivity.this,
					FurnacePatternActivity.class);
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
		// Log.e("updateUIListener执行了", "outlevel : " + outlevel);
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
							.getDrawable(R.drawable.mode_icon_summer), null,
							null, null);

		} else {
			tv_sliding_menu_season_mode.setText(R.string.winner_mode);
			tv_sliding_menu_season_mode
					.setCompoundDrawablesWithIntrinsicBounds(getResources()
							.getDrawable(R.drawable.mode_icon_winner), null,
							null, null);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (tv_sliding_menu_season_mode != null && resultCode == RESULT_OK
				&& data != null) {
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
	}

}
