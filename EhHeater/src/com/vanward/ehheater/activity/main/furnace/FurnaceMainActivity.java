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
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.gas.SendMsgModel;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.view.CircleListener;
import com.vanward.ehheater.view.CircularView;
import com.vanward.ehheater.view.DeviceOffUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.vanward.ehheater.view.fragment.BaseSlidingFragmentActivity;
import com.vanward.ehheater.view.fragment.SlidingMenu;
import com.xtremeprog.xpgconnect.generated.DERYStatusResp_t;
import com.xtremeprog.xpgconnect.generated.GasWaterHeaterStatusResp_t;
import com.xtremeprog.xpgconnect.generated.generated;

public class FurnaceMainActivity extends BaseSlidingFragmentActivity implements
		OnClickListener, OnLongClickListener, CircleListener {

	protected SlidingMenu mSlidingMenu;

	private Button btn_top_right, btn_appointment, btn_setting,
			btn_intellectual;

	private TextView mTitleName, tv_mode_tips, tv_status,
			tv_current_or_setting_temperature;

	private LinearLayout llt_circle;

	private ImageView iv_fire_wave_animation, iv_rotate_animation, iv_mode;

	private RelativeLayout rlt_content, rlt_open;

	private RadioGroup rg_winner;

	private RadioButton rb_summer;

	private CircularView circularView;

	private boolean isOn = false;

	private CountDownTimer mCountDownTimer;

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
		tv_current_or_setting_temperature = (TextView) findViewById(R.id.tv_current_or_setting_temperature);
		rlt_content = (RelativeLayout) findViewById(R.id.rlt_content);
		llt_circle = (LinearLayout) findViewById(R.id.llt_circle);
		iv_fire_wave_animation = (ImageView) findViewById(R.id.iv_fire_wave_animation);
		iv_rotate_animation = (ImageView) findViewById(R.id.iv_rotate_animation);
		iv_mode = (ImageView) findViewById(R.id.iv_mode);
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
					break;
				case R.id.rb_bath:
					rg_winner.setBackgroundResource(R.drawable.home_xuan_bg2);
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
				SendMsgModel.openDevice();
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

//				iv_rotate_animation.setVisibility(View.GONE);
				llt_circle.addView(circularView);
				// circularView.setVisibility(View.GONE);
			}
		});
		
		((AnimationDrawable)iv_rotate_animation.getDrawable()).start();
		((AnimationDrawable)iv_fire_wave_animation.getDrawable()).start();
	}

	@Override
	public void OnDERYStatusResp(DERYStatusResp_t pResp, int nConnId) {

		if (nConnId != Global.connectId) {
			return;
		}

		onOffDeal(pResp);
		seasonAndModeDeal(pResp); // switch season

		super.OnDERYStatusResp(pResp, nConnId);
	}

	private void onOffDeal(DERYStatusResp_t pResp) {
		if (pResp.getOnOff() == 0) { // shutdown
			setCircularViewEnable(false, pResp);
			tv_status.setText(R.string.shutdown);
			btn_setting.setEnabled(false);
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
			iv_mode.setImageResource(R.drawable.mode_icon_summer);

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
			
			if (pResp.getFireState() == 0) { // 0 : no flame
				iv_fire_wave_animation.setVisibility(View.GONE);
			} else if (pResp.getFireState() == 1) { // 1 : have flame
				iv_fire_wave_animation.setVisibility(View.VISIBLE);
				if (pResp.getBathWater() == 0) { // 0 : have bath current
					tv_status.setText(R.string.bathing);
				}
			}
		} else if (pResp.getSeasonState() == 1) { // winner
			rg_winner.setVisibility(View.VISIBLE);
			rb_summer.setVisibility(View.GONE);
			iv_mode.setImageResource(R.drawable.mode_icon_winner);

			if (pResp.getHeatingMode() == 0xA0) { // 0xA0 - normal mode
				tv_mode_tips
						.setCompoundDrawablesWithIntrinsicBounds(getResources()
								.getDrawable(R.drawable.mode_icon_normal),
								null, null, null);
				tv_mode_tips.setText(R.string.mode_outdoor);
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
				if (pResp.getBathWater() == 0) { // 0 : have bath current
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
					tv_status.setText(R.string.supplying_heat);
				}
			}
		}
	}

	private void temperatureDeal(DERYStatusResp_t pResp) {
		if (circularView == null) {
			return;
		}
		// tv_current_or_setting_temperature.setText(pResp.get)
	}

	private void setCircularViewEnable(boolean enable, DERYStatusResp_t pResp) {
		if (enable) {
			circularView.setVisibility(View.VISIBLE);
		} else {
			circularView.setVisibility(View.GONE);
		}
	}

	private void sendToMsgAfterThreeSeconds(final int value) {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		mCountDownTimer = new CountDownTimer(3000, 1000) {

			@Override
			public void onTick(long arg0) {
			}

			@Override
			public void onFinish() {
				SendMsgModel.setTempter(value);
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
								SendMsgModel.closeDevice();
								DeviceOffUtil
										.instance(FurnaceMainActivity.this)
										.dissmiss();
							}
						}).showDialog();
			} else {
				SendMsgModel.openDevice();
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

	// 转圈停止拖动的时候执行
	@Override
	public void levelListener(int outlevel) {
		Log.e("levelListener执行了", "outlevel : " + outlevel);
	}

	// 转圈拖动的时候执行
	@Override
	public void updateUIListener(int outlevel) {
		Log.e("updateUIListener执行了", "outlevel : " + outlevel);
	}

	// 第一次setListener的时候执行
	@Override
	public void updateUIWhenAferSetListener(int outlevel) {
		Log.e("updateUIWhenAferSetListener执行了", "outlevel : " + outlevel);
	}

	@Override
	public void updateLocalUIdifferent(int outlevel) {
		Log.e("updateLocalUIdifferent执行了", "outlevel : " + outlevel);
	}

}
