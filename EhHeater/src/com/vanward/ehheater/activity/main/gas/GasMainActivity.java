package com.vanward.ehheater.activity.main.gas;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.BaseBusinessActivity;
import com.vanward.ehheater.activity.appointment.AppointmentTimeActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.info.InfoErrorActivity;
import com.vanward.ehheater.activity.info.InfoTipActivity;
import com.vanward.ehheater.activity.info.InformationActivity;
import com.vanward.ehheater.application.EhHeaterApplication;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.ErrorUtils;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.SwitchDeviceUtil;
import com.vanward.ehheater.view.BaoCircleSlider;
import com.vanward.ehheater.view.BaoCircleSlider.BaoCircleSliderListener;
import com.vanward.ehheater.view.ChangeStuteView;
import com.vanward.ehheater.view.DeviceOffUtil;
import com.vanward.ehheater.view.ErrorDialogUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.xtremeprog.xpgconnect.generated.DeviceOnlineStateResp_t;
import com.xtremeprog.xpgconnect.generated.GasWaterHeaterStatusResp_t;
import com.xtremeprog.xpgconnect.generated.generated;

public class GasMainActivity extends BaseBusinessActivity implements
		OnClickListener, OnLongClickListener, BaoCircleSliderListener {

	private final String TAG = "GasMainActivity";

	// protected SlidingMenu mSlidingMenu;

	private TextView mTitleName, tv_mode, temptertitleTextView, sumwater,
			stute, shuiliuliangText;

	private View btn_power;
	private TextView tv_tempter, leavewater, target_tem, settemper;

	private ViewGroup stuteParent;
	private Button btn_appointment;

	private BaoCircleSlider circle_slider;

	private ImageView iv_wave, hotImgeImageView, modeimg;
	private AnimationDrawable animationDrawable;
	private RelativeLayout content;

	private Dialog deviceSwitchSuccessDialog;

	private boolean switchHintShowed;

	private View openView;

	private Button rightButton;

	private Animation operatingAnim;

	private boolean firstShowSwitchSuccess = true;

	private Dialog fullWaterDialog;

	private int circle_max_value = 65;

	private boolean isHeating = false;

	/** 指令正在发送中,三秒内不能改变CircleSlider滑动圆圈的位置 */
	private boolean isSendingCommand = false;

	private TextView powerTv;
	private ImageView tipsimg;
	private Button btn_info;
	private Button mode;

	private CountDownTimer mCountDownTimer;

	private BroadcastReceiver heaterNameChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			L.e(this, "heaterNameChangeReceiver()");
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
		setSlidingView(R.layout.activity_gas_main);
		initView(savedInstanceState);
		init();

		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				heaterNameChangeReceiver,
				new IntentFilter(Consts.INTENT_FILTER_HEATER_NAME_CHANGED));

		if (getIntent().getBooleanExtra("newActivity", false)) {
			String gasMac = getIntent().getStringExtra("mac");
			Log.e("notification传过来的gasMac是", gasMac);
			// connectDevice("", gasMac);
		} else {
			connectToDevice();
		}

		switchHintShowed = false;

		deviceSwitchSuccessDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithOneButton(R.string.switch_success,
						R.string.confirm, null);
		
		changeToOfflineUI();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ErrorUtils.isGasMainActivityActive = true;
		ErrorUtils.isMainActivityActive = false;
		ErrorUtils.isFurnaceMainActivityActive = false;

		L.e(this, "GasMainActivity的onResume调用了");

		String did = getIntent().getStringExtra("did");
		if (!TextUtils.isEmpty(did)
				&& !getIntent().getBooleanExtra("newActivity", false)) {
			SwitchDeviceUtil.switchDevice(did, this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		L.e(this, "onPause()");
		deviceSwitchSuccessDialog.dismiss();
	}

	private void init() {
		GasHeaterSendCommandService.getInstance().setBeforeSendCommandCallBack(
				this);

		fullWaterDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.full_water,
						R.string.I_know, R.string.soft_mode, null,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								GasHeaterSendCommandService.getInstance()
										.setToSolfMode();
								fullWaterDialog.dismiss();
							}
						});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		L.e(this, "重连之后onActivityResult调用了");

		if (requestCode == Consts.REQUESTCODE_CONNECT_ACTIVITY
				&& resultCode == RESULT_OK) {

			int connId = data.getIntExtra(Consts.INTENT_EXTRA_CONNID, -1);
			boolean isOnline = data.getBooleanExtra(
					Consts.INTENT_EXTRA_ISONLINE, true);
			String did = data.getStringExtra(Consts.INTENT_EXTRA_DID);
			String passcode = data.getStringExtra(Consts.INTENT_EXTRA_PASSCODE);
			String conntext = data
					.getStringExtra(Consts.INTENT_EXTRA_CONNECT_TEXT);

			final HeaterInfoService hser = new HeaterInfoService(
					getBaseContext());
			HeaterInfo curHeater = hser.getCurrentSelectedHeater();

			if (curHeater == null) {
				return;
			}

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
					isBinding = true;
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							isBinding = false;
						}
					}, 20000);
				} else {
					queryState();
				}

				if (ison) {
					rightButton
							.setBackgroundResource(R.drawable.icon_shut_enable);
				} else {
					rightButton
							.setBackgroundResource(R.drawable.icon_shut_disable);
				}

				if (getIntent().getBooleanExtra("switchSuccess", false)
						&& firstShowSwitchSuccess) {
					// 12月16日需求:去掉切换成功的提示
					/* appointmentSwitchSuccessDialog.show(); */
					firstShowSwitchSuccess = false;
				}

			} else {
				// 设备不在线
				Global.connectId = -1;
				Global.checkOnlineConnId = connId;
				changeToOfflineUI();
			}

			if (!conntext.contains("reconnect")) {
				mSlidingMenu.showContent();
			}

		}

		if (requestCode == Consts.REQUESTCODE_UPLOAD_BINDING) {
			HeaterInfoService hser = new HeaterInfoService(getBaseContext());
			HeaterInfo curHeater = hser.getCurrentSelectedHeater();

			isBinding = false;

			if (resultCode == RESULT_OK) {
				// binded
				new HeaterInfoService(getBaseContext()).updateBindedByUid(
						AccountService.getUserId(getApplicationContext()),
						curHeater.getMac(), true);
			}

			L.e(this, " Consts.REQUESTCODE_UPLOAD_BINDING 查询 queryState()");
			queryState();
		}
	}

	/**
	 * 多少秒后没有回调
	 */
	private long connectTime = 10000;

	@Override
	protected void queryState() {
		L.e(this, "queryState()");

		GasHeaterSendCommandService.getInstance()
				.SendGasWaterHeaterMobileRefreshReq();
		// rightButton.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// if (!stateQueried) {
		// if (isActived) {
		// dialog_reconnect.show();
		// }
		// dealDisConnect();
		// }
		// }
		// }, connectTime);
	}

	private boolean stateQueried;

	private void dealDisConnect() {
		isSendingCommand = false;
		tv_tempter.setText("--");
		tv_mode.setText("--模式");
		tv_mode.setVisibility(View.INVISIBLE);
		leavewater.setText("--");
		// powerTv.setText("--");
		target_tem.setText("--");
		settemper.setText("--");
		sumwater.setText(" ");
		circle_slider.setVisibility(View.GONE);
		// circularView.setOn(false);
		mode.setEnabled(false);
		stute.setText("不在线");
		tipsimg.setVisibility(View.GONE);
		if (hotImgeImageView != null) {
			hotImgeImageView.setVisibility(View.GONE);
			iv_wave.setVisibility(View.GONE);
			if (animationDrawable != null) {
				animationDrawable.stop();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		GasHeaterSendCommandService.getInstance().setBeforeSendCommandCallBack(
				null);
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				heaterNameChangeReceiver);
	}

	private void initView(Bundle savedInstanceState) {
		final Button btn_left = (Button) findViewById(R.id.ivTitleBtnLeft);

		((View) btn_left.getParent()).post(new Runnable() {
			@Override
			public void run() {
				Rect bounds = new Rect();
				btn_left.getHitRect(bounds);

				bounds.left -= 20;
				bounds.right += 30 * EhHeaterApplication.device_density + 0.5f;

				TouchDelegate touchDelegate = new TouchDelegate(bounds,
						btn_left);

				if (View.class.isInstance(btn_left.getParent())) {
					((View) btn_left.getParent())
							.setTouchDelegate(touchDelegate);
				}
			}
		});

		btn_left.setOnClickListener(this);
		rightButton = ((Button) findViewById(R.id.ivTitleBtnRigh));
		rightButton.setBackgroundResource(R.drawable.right_button_selector);
		rightButton.setOnClickListener(this);
		tipsimg = (ImageView) findViewById(R.id.infor_tip);
		tipsimg.setOnClickListener(this);
		btn_appointment = (Button) findViewById(R.id.appointment_btn);
		powerTv = (TextView) findViewById(R.id.power_tv);
		btn_power = findViewById(R.id.power);
		hotImgeImageView = (ImageView) findViewById(R.id.hotanimition);
		((AnimationDrawable) hotImgeImageView.getBackground()).start();
		// ((AnimationDrawable) hotImgeImageView.getDrawable()).start();
		temptertitleTextView = (TextView) findViewById(R.id.temptertext);
		target_tem = (TextView) findViewById(R.id.target_tem);
		settemper = (TextView) findViewById(R.id.settemper);
		btn_info = (Button) findViewById(R.id.btn_information);
		tv_mode = (TextView) findViewById(R.id.tv_mode);
		circle_slider = (BaoCircleSlider) findViewById(R.id.circle_slider);
		circle_slider.setCircleSliderListener(this);

		// stuteParent = (ViewGroup) findViewById(R.id.stute);
		mTitleName = (TextView) findViewById(R.id.ivTitleName);
		iv_wave = (ImageView) findViewById(R.id.wave_bg);
		modeimg = (ImageView) findViewById(R.id.modeimg);
		tv_tempter = (TextView) findViewById(R.id.tempter);
		leavewater = (TextView) findViewById(R.id.shuiliuliang);
		sumwater = (TextView) findViewById(R.id.zhushuiliang);
		shuiliuliangText = (TextView) findViewById(R.id.shuiliuliangText);
		content = (RelativeLayout) findViewById(R.id.content);
		mode = (Button) findViewById(R.id.pattern);
		stute = (TextView) findViewById(R.id.stute);
		btn_appointment.setOnClickListener(this);
		btn_power.setOnClickListener(this);
		initopenView();
		updateTitle(mTitleName);
		mode.setOnLongClickListener(this);
		circle_slider.setValue(35);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// circularView = new CircularView(GasMainActivity.this,
				// llt_circle, CircularView.CIRCULAR_SINGLE, 0);
				// circularView.setHeat(true);
				// circularView.setEndangle(65);
				// circularView.setAngle(35);
				// circularView.setOn(true);
				// circularView.setCircularListener(GasMainActivity.this);
				// llt_circle.addView(circularView);
				// circularView.setVisibility(View.VISIBLE);

				hotImgeImageView.setVisibility(View.GONE);
			}
		}, 50);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTitleBtnLeft:
			mSlidingMenu.showMenu(true);
			break;
		case R.id.ivTitleBtnRigh:

			if (tv_tempter.getText().toString().contains("--")) {
				// 以此判定为不在线
				dialog_reconnect.show();
				return;
			}

			if (ison) {
				DeviceOffUtil.instance(this)
						.nextButtonCall(new NextButtonCall() {
							@Override
							public void oncall(View v) {
								GasHeaterSendCommandService.getInstance()
										.closeDevice();
								DeviceOffUtil.instance(GasMainActivity.this)
										.dissmiss();
							}
						}).showDialog();
				return;
			} else {
				GasHeaterSendCommandService.getInstance().openDevice();
			}

			break;
		case R.id.appointment_btn:
			Intent intent = new Intent();
			intent.setClass(this, AppointmentTimeActivity.class);
			startActivity(intent);
			break;
		case R.id.pattern:
			Intent intent2 = new Intent();
			intent2.putExtra("name", tv_mode.getText());
			intent2.setClass(this, GasPatternActivity.class);
			startActivity(intent2);
			break;
		case R.id.btn_information:
			Intent intent3 = new Intent();
			intent3.putExtra("isgas", true);
			intent3.setClass(this, InformationActivity.class);
			startActivity(intent3);
			break;
		case R.id.infor_tip:
			break;
		}
	}

	/**
	 * 舒适模式
	 * 
	 * @param pResp
	 */
	private void changetoSofeMode(GasWaterHeaterStatusResp_t pResp) {
		tv_mode.setText("舒适模式");
		circle_slider.setVisibility(View.VISIBLE);
		// circularView.setOn(true);
		modeimg.setImageResource(R.drawable.gas_home_icon_comfort);
		// if (circularView != null) {
		// circularView.setVisibility(View.VISIBLE);
		// }
	}

	/**
	 * 厨房模式
	 * 
	 * @param pResp
	 */
	private void changetoKictienMode(GasWaterHeaterStatusResp_t pResp) {
		tv_mode.setText("厨房模式");
		modeimg.setImageResource(R.drawable.gas_home_icon_kitchen);
		circle_slider.setVisibility(View.GONE);
		// if (circularView != null) {
		// circularView.setVisibility(View.GONE);
		// }
	}

	/**
	 * 节能模式
	 * 
	 * @param pResp
	 */
	private void changetoEnergyMode(GasWaterHeaterStatusResp_t pResp) {
		tv_mode.setText("节能模式");
		modeimg.setImageResource(R.drawable.gas_home_icon_energy);
		circle_slider.setVisibility(View.GONE);
		// if (circularView != null) {
		// circularView.setVisibility(View.GONE);
		// }
	}

	/**
	 * 智能模式
	 * 
	 * @param pResp
	 */
	private void changetoIntelligenceMode(GasWaterHeaterStatusResp_t pResp) {
		tv_mode.setText("智能模式");
		modeimg.setImageResource(R.drawable.gas_home_icon_intelligence);
		circle_slider.setVisibility(View.GONE);
		// if (circularView != null) {
		// circularView.setVisibility(View.GONE);
		// }
	}

	/**
	 * 浴缸模式
	 * 
	 * @param pResp
	 */
	private void changetoBathtubMode(GasWaterHeaterStatusResp_t pResp) {
		tv_mode.setText("浴缸模式");
		circle_slider.setVisibility(View.VISIBLE);
		// circularView.setOn(true);
		((View) sumwater.getParent()).setVisibility(View.VISIBLE);
		modeimg.setImageResource(R.drawable.gas_home_icon_bathtub);
	}

	/**
	 * 自定义模式
	 * 
	 * @param pResp
	 */
	private void changetoDIYMode(GasWaterHeaterStatusResp_t pResp) {

		// circularView.setVisibility(View.VISIBLE);

		tv_mode.setText("自定义模式");
		circle_slider.setVisibility(View.VISIBLE);
		// circularView.setOn(true);
	}

	// 有没有预约? 这个是 预约按钮无效的
	private void setAppointmentButtonAble(boolean isAble) {
		btn_appointment.setEnabled(isAble);
		btn_power.setEnabled(isAble);
	}

	private void dealInHeat(GasWaterHeaterStatusResp_t pResp) {
		if (pResp.getFlame() == 1) {
			isHeating = true;
			stute.setText("加热中");
			// circularView.setIsheating(true);
			hotImgeImageView.setVisibility(View.VISIBLE);

			switch (pResp.getFirePower()) {
			case 1:
				iv_wave.setBackgroundResource(R.drawable.main_fire_level_1);
				break;
			case 2:
				iv_wave.setBackgroundResource(R.drawable.main_fire_level_2);
				break;
			case 3:
				iv_wave.setBackgroundResource(R.drawable.main_fire_level_3);
				break;
			case 4:
				iv_wave.setBackgroundResource(R.drawable.main_fire_level_4);
				break;
			case 5:
				iv_wave.setBackgroundResource(R.drawable.main_fire_level_5);
				break;
			}

			if (pResp.getFirePower() != 0) {
				animationDrawable = (AnimationDrawable) iv_wave.getBackground();
				// animationDrawable = (AnimationDrawable)
				// iv_wave.getDrawable();
				animationDrawable.start();
			}

			// animationDrawable = (AnimationDrawable) iv_wave.getDrawable();
			// animationDrawable.start();
			setViewsAble(false, pResp);
			/* rightButton.setEnabled(false); */// 所有模式在加热状态下, 都可以关机, 2014.11.7日
			mode.setEnabled(false);
			iv_wave.setVisibility(View.VISIBLE);
			if (pResp.getFunction_state() == 1
					|| pResp.getFunction_state() == 3
					|| pResp.getFunction_state() == 6) {
				rightButton.setEnabled(true);
				mode.setEnabled(false);
				circle_slider.setVisibility(View.VISIBLE);
				// circularView.setVisibility(View.VISIBLE);
				// circularView.setEndangle(48);
				circle_max_value = 48;
			} else {
				circle_slider.setVisibility(View.GONE);
				// circularView.setVisibility(View.GONE);
				// circularView.setEndangle(65);
				circle_max_value = 65;
			}
		} else {
			// circularView.setIsheating(false);
			isHeating = false;
			mode.setEnabled(true);
			hotImgeImageView.setVisibility(View.GONE);
			setViewsAble(true, pResp);
			rightButton.setEnabled(true);
			// if (pResp.getFunction_state() == 3) {
			// circularView.setEndangle(48);
			// } else {
			// circularView.setEndangle(65);
			// }
			// circularView.setEndangle(65);
			circle_max_value = 65;

			iv_wave.setVisibility(View.GONE);
		}
	}

	private void initopenView() {
		openView = LinearLayout.inflate(this, R.layout.activity_open, null);
		RelativeLayout.LayoutParams lParams = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		content.addView(openView, lParams);
		openView.setVisibility(View.GONE);
		openView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				GasHeaterSendCommandService.getInstance().openDevice();
			}
		});
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		if (connId == Global.connectId && event == -7) {
			// 连接断开
			L.e(this, "@@@@@@@@@@@@@@@");
			changeToOfflineUI();

			// 收到主动断开,重连一次
			connectToDevice();
		}
	}

	@Override
	public void OnGasWaterHeaterStatusResp(GasWaterHeaterStatusResp_t pResp,
			int nConnId) {
		super.OnGasWaterHeaterStatusResp(pResp, nConnId);

		if (nConnId != Global.connectId) {
			return;
		}

		stateQueried = true;

		dealMode(pResp);
		temptertureDeal(pResp);
		waterDeal(pResp);
		warningDeal(pResp);
		diyModeDeal(pResp);
		freezeProofing(pResp);
		flame(pResp);

		dealInHeat(pResp);
		onoffDeal(pResp);
		dealErrorWarnIcon(pResp);
		if (pResp.getCustomFunction() != 0) {
			circle_slider.setVisibility(View.VISIBLE);
			// circularView.setOn(true);
		}

		if (pResp.getOn_off() == 0) {
			tipsimg.setVisibility(View.GONE);
			circle_slider.setVisibility(View.GONE);
		}
	}

	@Override
	public void OnDeviceOnlineStateResp(DeviceOnlineStateResp_t pResp,
			int nConnId) {
		super.OnDeviceOnlineStateResp(pResp, nConnId);
//		L.e(this, "OnDeviceOnlineStateResp isOnline == " + pResp.getIsOnline());
//		if (pResp.getIsOnline() == 0) {
//			onConnectEvent(Global.connectId, -7);
//		}
	}

	/**
	 * 模式处理
	 * 
	 * @param pResp
	 */
	private void dealMode(GasWaterHeaterStatusResp_t pResp) {

		((View) sumwater.getParent()).setVisibility(View.GONE);

		// 系统模式：0x01（舒适模式）、0x02（厨房模式）、0x03（浴缸模式）、0x04（节能模式）、
		// 0x05（智能模式）、0x06（自定义模式）

		// 系统模式：0x01（舒适模式）、0x02（厨房模式）、0x03（浴缸模式）、0x04（节能模式）、
		// 0x05（智能模式）、0x06（自定义模式）

		currentModeCode = pResp.getFunction_state();

		L.e(this, "pResp.getFunction_state() == " + pResp.getFunction_state());
		L.e(this, "pResp.getCustomFunction() == " + pResp.getCustomFunction());

		switch (pResp.getFunction_state()) {
		case 1:
			changetoSofeMode(pResp);
			break;
		case 2:
			changetoKictienMode(pResp);
			break;
		case 3:
			changetoBathtubMode(pResp);
			break;
		case 4:
			changetoEnergyMode(pResp);
			break;
		case 5:
			changetoIntelligenceMode(pResp);
			break;
		case 6:
			changetoDIYMode(pResp);
			break;
		default:
			break;
		}
	}

	private int currentModeCode = -1;

	private void closeDevice() {
		openView.setVisibility(View.VISIBLE);
		// rightButton.setVisibility(View.GONE);
		ChangeStuteView.swichDeviceOff(stuteParent);
	}

	/**
	 * 开关处理
	 * 
	 * @param pResp
	 */
	private boolean ison = false;

	private void onoffDeal(GasWaterHeaterStatusResp_t pResp) {
		if (pResp.getOn_off() == 0) {
			setViewsAble(false, pResp);
			stute.setText(R.string.shutdown);
			circle_slider.setVisibility(View.GONE);
			// circularView.setOn(false);
			tv_mode.setVisibility(View.INVISIBLE);
			rightButton.setBackgroundResource(R.drawable.icon_shut_disable);
			ison = false;
		} else {
			// rightButton.setVisibility(View.VISIBLE);
			// openView.setVisibility(View.GONE);

			if (pResp.getFlame() != 1) {
				setViewsAble(true, pResp);
				stute.setText("待机中");
			}
			tv_mode.setVisibility(View.VISIBLE);
			ison = true;
			rightButton.setBackgroundResource(R.drawable.icon_shut_enable);
		}
	}

	/**
	 * 水温处理
	 * 
	 * @param pResp
	 */
	private void temptertureDeal(final GasWaterHeaterStatusResp_t pResp) {
		L.e(this, "返回的当前温度是 : " + pResp.getTargetTemperature());
		if (!circle_slider.isDraging() && !isSendingCommand) {
			circle_slider.setValue(pResp.getTargetTemperature());
			// circularView.setTargerdegree(pResp.getTargetTemperature());
		}
		target_tem.setText(pResp.getTargetTemperature() + "℃");
		if (pResp.getTargetTemperature() < 25) {
			if (!circle_slider.isDraging() && !isSendingCommand) {
				circle_slider.setValue(pResp.getTargetTemperature());
			}
			// circularView.setAngle(pResp.getTargetTemperature());
			Log.e("pResp.getOutputTemperature()", pResp.getOutputTemperature()
					+ "");
			tv_tempter.setText(pResp.getOutputTemperature() + "");
		} else {
			if (!circle_slider.isDraging() && !isSendingCommand) {
				circle_slider.setValue(pResp.getTargetTemperature());
			}
			// circularView.setAngle(pResp.getTargetTemperature());
			tv_tempter.setText(pResp.getTargetTemperature() + "");
		}

		// circularView.setTargerdegree(pResp.getTargetTemperature());
	}

	/**
	 * 水量处理
	 * 
	 * @param pResp
	 */
	private void waterDeal(GasWaterHeaterStatusResp_t pResp) {
		// 浴缸 设定注水量 累计注水量
		shuiliuliangText.setText("实时水流量");
		settemper.setText("/" + (pResp.getSetWater_power() * 10) + "L");
		leavewater.setText(pResp.getNowVolume() + "L");
		sumwater.setText(pResp.getSetWater_cumulative() + "L");

		if (pResp.getSetWater_cumulative() == (pResp.getSetWater_power() * 10)) {
			fullWaterDialog.show();

		} else {
			fullWaterDialog.dismiss();
		}

		// if (pResp.getFunction_state() == 3) {
		// ((View) sumwater.getParent()).setVisibility(View.VISIBLE);
		// sumwater.setText(pResp.getSetWater_power() + "0L");
		// shuiliuliangText.setText("累计注水量");
		// leavewater.setText(pResp.getSetWater_cumulative() + "L");
		// } else {
		// ((View) sumwater.getParent()).setVisibility(View.GONE);
		// shuiliuliangText.setText("实时水流量");
		// leavewater.setText(pResp.getNowVolume() + "L");
		// }

	}

	/**
	 * 警告处理
	 * 
	 * @param pResp
	 */
	private void warningDeal(GasWaterHeaterStatusResp_t pResp) {
	}

	private void diyModeDeal(GasWaterHeaterStatusResp_t pResp) {
		modeimg.setVisibility(View.VISIBLE);

		if (pResp.getCustomFunction() != 0) {

			List<GasCustomSetVo> list = new BaseDao(GasMainActivity.this)
					.getDb().findAllByWhere(
							GasCustomSetVo.class,
							" uid = '"
									+ AccountService
											.getUserId(GasMainActivity.this)
									+ "'");
			L.e(this, "GasCustomSetVo的大小是" + list.size());
			L.e(this,
					"pResp.getCustomFunction() == " + pResp.getCustomFunction());
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					GasCustomSetVo customSetVo = list.get(i);
					if (customSetVo.getSendId() == pResp.getCustomFunction()) {
						customSetVo.setTempter(pResp.getTargetTemperature());
						L.e(this,
								"getTargetTemperature : "
										+ pResp.getTargetTemperature());
						L.e(this,
								"customSetVo.getName : "
										+ customSetVo.getName());
						L.e(this,
								"customSetVo.getTempter : "
										+ customSetVo.getTempter());
						L.e(this,
								"customSetVo.getSendId : "
										+ customSetVo.getSendId());
						new BaseDao(this).getDb().update(customSetVo);
						String name = customSetVo.getName();
						tv_mode.setText(name);
						break;
					}
				}
			} else {
				tv_mode.setText("自定义模式");
			}

			// curGasCustomVo = (GasCustomSetVo) new BaseDao(this).getDb()
			// .findById(pResp.getCustomFunction(), GasCustomSetVo.class);
			//
			// if (curGasCustomVo != null) {
			// tv_mode.setText(curGasCustomVo.getName());
			// } else {
			// tv_mode.setText("自定义模式");
			// }

			modeimg.setVisibility(View.GONE);

		} else {

			modeimg.setVisibility(View.VISIBLE);

		}

	}

	private GasCustomSetVo curGasCustomVo = null;
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	private void dealErrorWarnIcon(final GasWaterHeaterStatusResp_t pResp) {

		freezeProofing(pResp);
		oxygenWarning(pResp);
		if (pResp.getErrorCode() != 0) {
			showErrorWarning(pResp.getErrorCode());
		} else {
			if (pResp.getFreezeProofingWarning() != 1
					&& pResp.getOxygenWarning() != 1) {
				tipsimg.setVisibility(View.GONE);
				ErrorDialogUtil.instance(this).dissmiss();
			}
		}
	}

	private void showErrorWarning(final short ErrorCode) {
		tipsimg.setVisibility(View.VISIBLE);
		tipsimg.setBackgroundResource(R.drawable.main_error);
		AnimationDrawable drawable = (AnimationDrawable) tipsimg
				.getBackground();
		// tipsimg.setImageResource(R.drawable.main_error);
		// AnimationDrawable drawable = (AnimationDrawable) tipsimg
		// .getDrawable();
		drawable.start();
		tipsimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ErrorDialogUtil.instance(GasMainActivity.this)
						.initName(Integer.toHexString(ErrorCode) + "")
						.setNextButtonCall(new NextButtonCall() {
							@Override
							public void oncall(View v) {
								Intent intent = new Intent();
								// intent.putExtra("data", inforVo);
								intent.setClass(GasMainActivity.this,
										InfoErrorActivity.class);
								intent.putExtra(
										"name",
										"机器故障("
												+ Integer
														.toHexString(ErrorCode)
												+ ")");
								intent.putExtra("time",
										simpleDateFormat.format(new Date()));
								intent.putExtra("detail",
										"请先暂关闭水龙头再打开，或关/开显示器，再操作1-2次仍然显示故障，请务必关闭水阀和气阀，拔掉电源插头，请与售后服务联系。");
								startActivity(intent);
							}
						}).showDialog();
			}
		});
	}

	/**
	 * 防冻报警提示：0（无）、1（有）
	 * 
	 * @param pResp
	 */
	private void freezeProofing(GasWaterHeaterStatusResp_t pResp) {
		if (pResp.getFreezeProofingWarning() == 1) {
			showFreezeProofing();
		}
	}

	private void showFreezeProofing() {
		tipsimg.setVisibility(View.VISIBLE);
		tipsimg.setBackgroundResource(R.drawable.main_tip);
		AnimationDrawable drawable = (AnimationDrawable) tipsimg
				.getBackground();
		// tipsimg.setImageResource(R.drawable.main_tip);
		// AnimationDrawable drawable = (AnimationDrawable) tipsimg
		// .getDrawable();
		drawable.start();
		tipsimg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 提醒
				// ErrorDialogUtil.instance(this).showDialog();
				Intent intent = new Intent();
				// intent.putExtra("data", inforVo);
				intent.setClass(GasMainActivity.this, InfoTipActivity.class);
				intent.putExtra("name", "防冻预警");
				intent.putExtra("time", simpleDateFormat.format(new Date()));
				intent.putExtra("detail",
						"亲，现检测到您热水器水箱已接近冰点，请旋下进水接头处的泄压排水阀进行排水，以免水箱冻裂。");
				startActivity(intent);

			}
		});
	}

	/**
	 * 氧护提示：0（无）、1（有）
	 * 
	 * @param pResp
	 */
	private void oxygenWarning(GasWaterHeaterStatusResp_t pResp) {
		if (pResp.getOxygenWarning() == 1) {
			showOxygenWarning();
		}
	}

	private void showOxygenWarning() {
		tipsimg.setVisibility(View.VISIBLE);
		tipsimg.setBackgroundResource(R.drawable.main_tip);
		AnimationDrawable drawable = (AnimationDrawable) tipsimg
				.getBackground();
		// tipsimg.setImageResource(R.drawable.main_tip);
		// AnimationDrawable drawable = (AnimationDrawable) tipsimg
		// .getDrawable();
		drawable.start();
		tipsimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 提醒
				Intent intent = new Intent();
				// intent.putExtra("data", inforVo);
				intent.setClass(GasMainActivity.this, InfoTipActivity.class);
				intent.putExtra("name", "智能氧护");
				intent.putExtra("time", simpleDateFormat.format(new Date()));
				intent.putExtra("detail",
						"亲，现检测到您热水器处于环境缺氧状态，机器已自动启动智能氧护措施，请确保您热水器处于通风环境下运行。");
				startActivity(intent);
			}
		});
	}

	/**
	 * 氧护提示：0（无）、1（有）
	 * 
	 * @param pResp
	 */
	private void flame(GasWaterHeaterStatusResp_t pResp) {
	}

	@Override
	public boolean onLongClick(View arg0) {
		GasHeaterSendCommandService.getInstance().setToSolfMode();
		return true;
	}

	private void sendToMsgAfterThreeSeconds(final int value) {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}

		isSendingCommand = true;
		mCountDownTimer = new CountDownTimer(3000, 1000) {

			@Override
			public void onTick(long arg0) {
			}

			@Override
			public void onFinish() {
				if (currentModeCode == 6 && curGasCustomVo != null) {
					GasHeaterSendCommandService.getInstance()
							.SendGasWaterHeaterDIYSettingReq(
									(short) curGasCustomVo.getSendId(),
									(short) value,
									(short) curGasCustomVo.getWaterval());
				} else {
					GasHeaterSendCommandService.getInstance().setTempter(value);
				}
				isSendingCommand = false;
				tv_tempter.setText(value + "");
			}
		};
		mCountDownTimer.start();
	}

	// @Override
	// public void levelListener(final int outlevel) {
	//
	// sentToMsgAfterSix(outlevel);
	// }

	// @Override
	// public void updateUIListener(int outlevel) {
	// temptertitleTextView.setText("设置水温");
	//
	// tv_tempter.setText(outlevel + "");
	// if (outlevel >= 50) {
	// // 变小了
	// if (circularView.getTargerdegree() > outlevel) {
	// if (outlevel == 50) {
	// circularView.setTargerdegree(outlevel - 1);
	// } else {
	// circularView.setTargerdegree(outlevel - 3);
	// }
	//
	// } else {
	// circularView.setTargerdegree(outlevel + 3);
	// }
	//
	// } else {
	// circularView.setTargerdegree(outlevel);
	// }
	// Insetting = true;
	// }

	// @Override
	// public void updateUIWhenAferSetListener(final int outlevel) {
	// temptertitleTextView.setText("设置水温");
	// Drawable img =
	// getBaseContext().getResources().getDrawable(R.drawable.icon_temperature);
	// int dp32 = PxUtil.dip2px(getBaseContext(), 32);
	// img.setBounds( 0, 0, dp32, dp32 );
	// temptertitleTextView.setCompoundDrawables(img, null, null, null);

	// Log.e("outlevel", outlevel + "");
	// tv_tempter.setText(outlevel + "");
	// }

	// @Override
	// public void updateLocalUIdifferent(int outlevel) {
	// }

	private void setViewsAble(boolean isAble, GasWaterHeaterStatusResp_t pResp) {
		if (isAble) {
			if (pResp.getFunction_state() == 3
					|| pResp.getFunction_state() == 1
					|| pResp.getFunction_state() == 6) {
				circle_slider.setVisibility(View.VISIBLE);
				// circularView.setVisibility(View.VISIBLE);
			} else {
				circle_slider.setVisibility(View.GONE);
				// circularView.setVisibility(View.GONE);
			}

		} else {
			circle_slider.setVisibility(View.GONE);
			// circularView.setVisibility(View.GONE);
		}
		mode.setEnabled(isAble);
		// rightButton.setEnabled(isAble);
	}

	@Override
	protected void changeToOfflineUI() {
		dealDisConnect();
		rightButton.setBackgroundResource(R.drawable.icon_shut_disable);
		stute.setText("不在线");
	}

	@Override
	public void didBeginTouchCircleSlider() {

	}

	@Override
	public void needChangeValue(int value, boolean isAdd) {
		boolean isLarger = value > circle_slider.getValue();
		if (value > circle_max_value) {
			if (isHeating) {
				circle_slider.setValue(circle_max_value);
				tv_tempter.setText(circle_max_value + "");
			}
		} else {
			if (value < 35) {
				value = 35;
			} else if (value > 65) {
				value = 65;
			}
			if (value >= 35 && value <= circle_max_value) {
				if (value == 49) {
					value = isAdd ? 50 : 48;
				} else if (value > 50 && value < 55) {
					value = isAdd ? 55 : 50;
				} else if (value > 55 && value < 60) {
					value = isAdd ? 60 : 55;
				} else if (value > 60 && value < 65) {
					value = isAdd ? 65 : 60;
				}
				tv_tempter.setText(value + "");
				circle_slider.setValue(value);
			}
		}
		// if (!isHeating) {
		//
		// } else {
		// if (value > circle_max_value) {
		// circle_slider.setValue(circle_max_value);
		// }
		// }
	}

	@Override
	public void didEndChangeValue() {
		sendToMsgAfterThreeSeconds(circle_slider.getValue());
	}

}
