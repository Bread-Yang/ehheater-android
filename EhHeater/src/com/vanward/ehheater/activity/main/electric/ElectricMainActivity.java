package com.vanward.ehheater.activity.main.electric;

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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.BaseBusinessActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.info.InfoErrorActivity;
import com.vanward.ehheater.activity.info.InfoTipActivity;
import com.vanward.ehheater.activity.info.InformationActivity;
import com.vanward.ehheater.activity.main.common.LeftFragment;
import com.vanward.ehheater.activity.main.furnace.FurnaceAppointmentListActivity;
import com.vanward.ehheater.application.EhHeaterApplication;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.statedata.EhState;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.ErrorUtils;
import com.vanward.ehheater.util.IntelligentPatternUtil;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.SwitchDeviceUtil;
import com.vanward.ehheater.util.TcpPacketCheckUtil;
import com.vanward.ehheater.view.BaoCircleSlider;
import com.vanward.ehheater.view.BaoCircleSlider.BaoCircleSliderListener;
import com.vanward.ehheater.view.ChangeStuteView;
import com.vanward.ehheater.view.DeviceOffUtil;
import com.vanward.ehheater.view.ErrorDialogUtil;
import com.vanward.ehheater.view.PowerSettingDialogUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.xtremeprog.xpgconnect.generated.DeviceOnlineStateResp_t;
import com.xtremeprog.xpgconnect.generated.StateResp_t;

public class ElectricMainActivity extends BaseBusinessActivity implements
		OnClickListener, BaoCircleSliderListener {

	private final String TAG = "MainActivity";

	private LeftFragment mLeftFragment;

	private RightFragment mRightFragment;

	private TextView mTitleName, tv_mode, powerTv, temptertitleTextView;

	private Button btn_info;
	private View btn_power;
	private TextView tv_tempter, leavewater, target_tem;

	private BaoCircleSlider circle_slider;

	private ViewGroup llt_statusParent;
	private Button btn_appointment;
	private ImageView iv_wave, hotImgeImageView;
	private AnimationDrawable animationDrawable;
	private RelativeLayout content;

	private View openView;

	private LinearLayout llt_power;

	/** 指令正在发送中,三秒内不能改变CircleSlider滑动圆圈的位置 */
	private boolean isSendingCommand = false;

	// 主界面错误图标
	private ImageView iv_error;

	private Button rightButton;

	private static String customPatternName = "";

	private boolean firstShowSwitchSuccess = true;

	private boolean isError;

	private Dialog deviceSwitchSuccessDialog;

	private CountDownTimer mCountDownTimer;

	private boolean canupdateView = false;

	private int currentTemp;

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
		setSlidingView(R.layout.activity_electric_main);
		initView();

		ElectricHeaterSendCommandService.getInstance()
				.setBeforeSendCommandCallBack(this);

		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				heaterNameChangeReceiver,
				new IntentFilter(Consts.INTENT_FILTER_HEATER_NAME_CHANGED));

		canupdateView = false;

		if (getIntent().getBooleanExtra("newActivity", false)) {
			String electicMac = getIntent().getStringExtra("mac");
			// connectDevice("", electicMac);
		} else {
			connectToDevice();
		}

		// connectCurDevice();

		deviceSwitchSuccessDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithOneButton(R.string.switch_success,
						R.string.confirm, null);

		ChangeStuteView.swichdisconnect(llt_statusParent);
	}

	public void changeToOfflineUI() {
		ChangeStuteView.swichdisconnect(llt_statusParent);
		dealDisConnect();
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

				if (getIntent().getBooleanExtra("switchSuccess", false)
						&& firstShowSwitchSuccess) {
					// 12月16日需求:去掉切换成功的提示
					/* appointmentSwitchSuccessDialog.show(); */
					firstShowSwitchSuccess = false;
				}

			} else {
				// 设备不在线, 需检测上线
				Global.connectId = -1;
				Global.checkOnlineConnId = connId;
				try {
					changeToOfflineUI();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (isActived) {
					dialog_reconnect.show();
				}
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

			queryState();

		}

	}

	/**
	 * 多少秒后没有回调
	 */
	private static long connectTime = 10000;

	@Override
	protected void queryState() {
		ElectricHeaterSendCommandService.getInstance().SendStateReq();
		// rightButton.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// if (!stateQueried) {
		// changeToOfflineUI();
		// if (isActived) {
		// dialog_reconnect.show();
		// }
		// }
		// }
		// }, connectTime);
	}

	private boolean stateQueried;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		L.e(this, "onNewIntent()");
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		L.e(this, "onResume()");
		ErrorUtils.isMainActivityActive = true;
		ErrorUtils.isGasMainActivityActive = false;
		ErrorUtils.isFurnaceMainActivityActive = false;
		canupdateView = true;

		String did = getIntent().getStringExtra("did");
		L.e(this, "did : " + did);

		if (did != null && !"".equals(did)
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

	@Override
	protected void onStop() {
		L.e(this, "onStop()");
		super.onStop();
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		L.e(this, "onDestroy()");
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		ElectricHeaterSendCommandService.getInstance()
				.setBeforeSendCommandCallBack(null);

		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				heaterNameChangeReceiver);
	}

	private void initView() {
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
		rightButton.setOnClickListener(this);
		btn_appointment = (Button) findViewById(R.id.appointment_btn);
		powerTv = (TextView) findViewById(R.id.power_tv);
		btn_power = findViewById(R.id.power);
		hotImgeImageView = (ImageView) findViewById(R.id.hotanimition);
		((AnimationDrawable) hotImgeImageView.getBackground()).start();
		// ((AnimationDrawable) hotImgeImageView.getDrawable()).start();
		circle_slider = (BaoCircleSlider) findViewById(R.id.circle_slider);
		circle_slider.setCircleSliderListener(this);

		// 主界面错误图标
		iv_error = (ImageView) findViewById(R.id.infor_tip);
		temptertitleTextView = (TextView) findViewById(R.id.temptertext);
		target_tem = (TextView) findViewById(R.id.target_tem);
		btn_info = (Button) findViewById(R.id.btn_information);
		tv_mode = (TextView) findViewById(R.id.mode_tv);
		llt_statusParent = (ViewGroup) findViewById(R.id.stute);
		mTitleName = (TextView) findViewById(R.id.ivTitleName);
		iv_wave = (ImageView) findViewById(R.id.wave_bg);
		tv_tempter = (TextView) findViewById(R.id.tempter);
		leavewater = (TextView) findViewById(R.id.leavewater);
		content = (RelativeLayout) findViewById(R.id.content);
		btn_appointment.setOnClickListener(this);
		btn_power.setOnClickListener(this);
		llt_power = (LinearLayout) findViewById(R.id.llt_power);
		llt_power.setOnClickListener(this);

		initopenView();
		updateTitle(mTitleName);
		boolean InSetting = false;
		circle_slider.setValue(35);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// circularView = new CircularView(MainActivity.this,
				// llt_circle,
				// CircularView.CIRCULAR_SINGLE, 0);
				// circularView.setEndangle(75);
				// circularView.setAngle(35);
				// circularView.setOn(false);
				hotImgeImageView.setVisibility(View.GONE);
				animationDrawable = (AnimationDrawable) iv_wave.getDrawable();
				animationDrawable.start();
				// circularView.setCircularListener(MainActivity.this);
				// llt_circle.addView(circularView);
			}
		}, 100);
	}

	private void sendToMsgAfterThreeSeconds(final int value) {
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
				ElectricHeaterSendCommandService.getInstance()
						.setTempter(value);
				tv_tempter.setText(value + "");
				if (currentModeCode == 7) { // 智能模式
					IntelligentPatternUtil.addLastTemperature(
							ElectricMainActivity.this, value);
				}
				isSendingCommand = false;
			}
		};
		mCountDownTimer.start();
	}

	boolean ison = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTitleBtnLeft:
			mSlidingMenu.showMenu(true);
			break;
		case R.id.ivTitleBtnRigh:

			if (target_tem.getText().toString().contains("--")) {
				// 以此判定为不在线
				dialog_reconnect.show();
				return;
			}

			if (ison) {

				DeviceOffUtil.instance(this)
						.nextButtonCall(new NextButtonCall() {
							@Override
							public void oncall(View v) {
								ElectricHeaterSendCommandService.getInstance()
										.closeDevice();
								DeviceOffUtil.instance(
										ElectricMainActivity.this).dissmiss();
							}
						}).showDialog();
			} else {
				ElectricHeaterSendCommandService.getInstance().openDevice();
			}

			break;
		case R.id.appointment_btn:
			Intent intent = new Intent();
			intent.setClass(this, FurnaceAppointmentListActivity.class);
			startActivity(intent);
			break;
		case R.id.pattern:
			Intent intent2 = new Intent();
			intent2.putExtra("name", tv_mode.getText());
			intent2.setClass(this, EIPatternActivity.class);
			startActivity(intent2);
			break;
		case R.id.power:
			if (v.isSelected()) {

			} else {
				v.setSelected(true);
				Tojishi();
			}

			break;
		case R.id.btn_information:

			Intent intent3 = new Intent();
			intent3.putExtra("isgas", false);
			intent3.setClass(this, InformationActivity.class);
			startActivity(intent3);

			break;

		case R.id.llt_power:

			if (currentModeCode == 7 || currentModeCode == 4
					|| currentModeCode == 6) {
				if (ison) {
					setPower();
				}
			}
			break;

		}
	}

	private void changeToIntelligenceModeUpdateUI(byte[] data) {
		tv_mode.setText("智能模式");
		circle_slider.setVisibility(View.VISIBLE);
		// circularView.setOn(true);
		setAppointmentButtonAble(true);
		ChangeStuteView.swichLeaveMinView(llt_statusParent, 10);
		btn_power.setOnClickListener(this);
		int i = new EhState(data).getRemainingHeatingTime();
		if (i == 0 || i == -1) {
			ChangeStuteView.swichKeep(llt_statusParent);
		} else {
			ChangeStuteView.swichLeaveMinView(llt_statusParent, i);
		}

	}

	private void changeTojishiModeUpdateUI(byte[] data) {
		tv_mode.setText("即时加热");
		circle_slider.setVisibility(View.GONE);
		// circularView.setOn(false);
		setAppointmentButtonAble(true);
		ChangeStuteView.swichLeaveMinView(llt_statusParent, 10);
		btn_power.setOnClickListener(this);
		btn_power.setSelected(true);
		int i = new EhState(data).getRemainingHeatingTime();

		if (i == 0 || i == -1) {
			ChangeStuteView.swichKeep(llt_statusParent);
		} else {
			ChangeStuteView.swichLeaveMinView(llt_statusParent, i);
		}

	}

	private void changeToNightModeUpdateUI(byte[] data) {
		tv_mode.setText("夜电模式");
		circle_slider.setVisibility(View.GONE);
		// circularView.setOn(false);
		setAppointmentButtonAble(false);
		ChangeStuteView.swichNight(llt_statusParent);
		int i = new EhState(data).getSystemRunningState();
		int remainingTime = new EhState(data).getRemainingHeatingTime();
		if (i == 0) {
			ChangeStuteView.swichNight(llt_statusParent);
		} else {
			ChangeStuteView.swichLeaveMinView(llt_statusParent, remainingTime);
		}

	}

	private void changeToCustomModeUpdateUI(byte[] data) {
		// modeTv.setText("自定义模式");
		EhState ehState = new EhState(data);
		setAppointmentButtonAble(true);
		final int targetTemperature = ehState.getTargetTemperature();
		final int power = ehState.getPower();
		tv_mode.post(new Runnable() {

			@Override
			public void run() {
				List<CustomSetVo> list = new BaseDao(ElectricMainActivity.this)
						.getDb()
						.findAllByWhere(
								CustomSetVo.class,
								" uid = '"
										+ AccountService
												.getUserId(ElectricMainActivity.this)
										+ "'");

				if (list.size() > 0) {
					L.e(this, "设置自定义名字");
					for (int i = 0; i < list.size(); i++) {
						CustomSetVo customSetVo = list.get(i);

						if (customSetVo.getPower() == power
								&& customSetVo.getTempter() == targetTemperature) {

							if (customSetVo.isSet()) {
								tv_mode.setText(customSetVo.getName());
								break;
							}
						}
						if (i == list.size() - 1) {
							tv_mode.setText("自定义模式");
						}
					}
				} else {
					L.e(this, "自定义模式");
					tv_mode.setText("自定义模式");
				}

			}
		});

		circle_slider.setVisibility(View.VISIBLE);

		ChangeStuteView.swichLeaveMinView(llt_statusParent, 10);
		// powerTv.setText(3 + "kw");
		btn_power.setOnClickListener(this);
		int i = new EhState(data).getRemainingHeatingTime();
		if (i == 0 || i == -1) {
			ChangeStuteView.swichKeep(llt_statusParent);
		} else {
			ChangeStuteView.swichLeaveMinView(llt_statusParent, i);
		}

	}

	private void Tojishi() {
		ElectricHeaterSendCommandService.getInstance().changeToJishiMode();
	}

	private void setPower() {
		int oldvalue = Integer.parseInt((String) powerTv.getText().subSequence(
				0, 1));

		PowerSettingDialogUtil.instance(this)
				.setNextButtonCall(new NextButtonCall() {

					@Override
					public void oncall(View v) {
						int i = PowerSettingDialogUtil.instance(
								ElectricMainActivity.this).getPower();
						System.out.println("0x00: " + (short) (0x00 + i));
						ElectricHeaterSendCommandService.getInstance()
								.setPower(i);
						if (currentModeCode == 7) { // 智能模式
							IntelligentPatternUtil.addLastPower(
									ElectricMainActivity.this, i);
						}
						PowerSettingDialogUtil.instance(
								ElectricMainActivity.this).dissmiss();
					}
				}).showDialog(oldvalue);
	}

	/**
	 * 根据 时间人数更新界面
	 * 
	 * @param data
	 * 
	 * @param time
	 * @param peopleNum
	 */
	private void changeToMorningWashUpdateUI(byte[] data) {
		circle_slider.setVisibility(View.GONE);
		// circularView.setOn(false);
		setAppointmentButtonAble(false);
		tv_mode.setText("晨浴模式");
		ChangeStuteView.swichMorningWash(llt_statusParent);
		int i = new EhState(data).getRemainingHeatingTime();
		System.out.println("测试晨浴i: " + i);
		if (i == 0) {
			ChangeStuteView.swichMorningWash(llt_statusParent);
		} else if (new EhState(data).getSystemRunningState() == 1) {
			ChangeStuteView.swichLeaveMinView(llt_statusParent, i);
		}

	}

	/**
	 * 防冻报警提示：0（无）、1（有）
	 * 
	 * @param pResp
	 */
	private void freezeProofing(StateResp_t date) {
		L.e(this, "防冻报警 : " + date.getError());
		if (date.getError() == 160) {
			iv_error.setVisibility(View.VISIBLE);
			iv_error.setBackgroundResource(R.drawable.main_tip);
			AnimationDrawable drawable = (AnimationDrawable) iv_error
					.getBackground();
			// iv_error.setImageResource(R.drawable.main_tip);
			// AnimationDrawable drawable = (AnimationDrawable) iv_error
			// .getDrawable();
			drawable.start();
			iv_error.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 提醒
					// ErrorDialogUtil.instance(this).showDialog();
					Intent intent = new Intent();
					// intent.putExtra("data", inforVo);
					intent.setClass(ElectricMainActivity.this,
							InfoTipActivity.class);
					intent.putExtra("name", "防冻预警");
					intent.putExtra("time", simpleDateFormat.format(new Date()));
					intent.putExtra("detail",
							"亲，现检测到您热水器水箱已接近冰点，请旋下进水接头处的泄压排水阀进行排水，以免水箱冻裂。");
					startActivity(intent);

				}
			});
		}
	}

	/**
	 * 镁棒提示
	 * 
	 * @param pResp
	 */
	private void meibangWran(final StateResp_t date) {
		L.e(this, "镁棒提示 : " + date.getHeating_tube_time());
		if (date.getHeating_tube_time() > 800 * 60) {
			iv_error.setVisibility(View.VISIBLE);
			iv_error.setBackgroundResource(R.drawable.main_tip);
			AnimationDrawable drawable = (AnimationDrawable) iv_error
					.getBackground();
			// iv_error.setImageResource(R.drawable.main_tip);
			// AnimationDrawable drawable = (AnimationDrawable) iv_error
			// .getDrawable();
			drawable.start();
			iv_error.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 提醒
					// ErrorDialogUtil.instance(this).showDialog();
					Intent intent = new Intent();
					// intent.putExtra("data", inforVo);
					intent.setClass(ElectricMainActivity.this,
							InfoTipActivity.class);
					intent.putExtra("name", "镁棒更换提醒");
					intent.putExtra("time", simpleDateFormat.format(new Date()));
					intent.putExtra(
							"detail",
							"亲，距离上次更换镁棒，您的热水器已经累计加热"
									+ date.getHeating_tube_time() / 60
									+ "个小时，为保证加热管能长期有效工作，建议您联系客服更换镁棒。");
					startActivity(intent);
				}
			});
		}
	}

	/**
	 * 水质
	 * 
	 * @param pResp
	 */
	private void waterWarn(final StateResp_t date) {
		L.e(this, "水质提醒：" + date.getMachine_not_heating_time());
		if (date.getMachine_not_heating_time() > 9 * 24 * 60) {
			iv_error.setVisibility(View.VISIBLE);
			iv_error.setBackgroundResource(R.drawable.main_tip);
			AnimationDrawable drawable = (AnimationDrawable) iv_error
					.getBackground();
			// iv_error.setImageResource(R.drawable.main_tip);
			// AnimationDrawable drawable = (AnimationDrawable) iv_error
			// .getDrawable();
			drawable.start();
			iv_error.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 提醒
					// ErrorDialogUtil.instance(this).showDialog();
					Intent intent = new Intent();
					// intent.putExtra("data", inforVo);
					intent.setClass(ElectricMainActivity.this,
							InfoTipActivity.class);
					intent.putExtra("name", "水质提醒");
					intent.putExtra("time", simpleDateFormat.format(new Date()));
					intent.putExtra("detail",
							"亲，我们发现您的热水器长时间没用了，为了您的健康，建议您排空污水后再使用。");
					startActivity(intent);
				}
			});
		}
	}

	// 错误图标

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	private void dealErrorWarnIcon(final StateResp_t date) {
		freezeProofing(date);
		meibangWran(date);
		waterWarn(date);
		// oxygenWarning(pResp);
		System.out.println("错误码：" + date.getError() + "  ");
		L.e(this, "错误码：" + date.getError());
		if (date.getError() != 0 && date.getError() != 160) { // 不是防冻
			isError = true;
			iv_error.setVisibility(View.VISIBLE);
			iv_error.setBackgroundResource(R.drawable.main_error);
			AnimationDrawable drawable = (AnimationDrawable) iv_error
					.getBackground();
			// iv_error.setImageResource(R.drawable.main_error);
			// AnimationDrawable drawable = (AnimationDrawable) iv_error
			// .getDrawable();
			drawable.start();
			iv_error.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ErrorDialogUtil
							.instance(ElectricMainActivity.this)
							.initName(Integer.toHexString(date.getError()) + "")
							.setNextButtonCall(new NextButtonCall() {
								@Override
								public void oncall(View v) {
									Intent intent = new Intent();
									intent.setClass(ElectricMainActivity.this,
											InfoErrorActivity.class);
									intent.putExtra(
											"name",
											"机器故障("
													+ Integer.toHexString(date
															.getError()) + ")");
									intent.putExtra("time",
											simpleDateFormat.format(new Date()));
									intent.putExtra(
											// new EhState(data).getErrorCode()
											// errors
											"detail",
											ErrorDialogUtil
													.instance(
															ElectricMainActivity.this)
													.getMap()
													.get(date.getError() + ""));
									startActivity(intent);
								}
							}).showDialog();
				}
			});
		} else {
			if (date.getError() != 160
					&& date.getHeating_tube_time() <= 800 * 60) {
				isError = false;
				iv_error.setVisibility(View.GONE);
				ErrorDialogUtil.instance(this).dissmiss();
			}
		}
	}

	@Override
	public void OnStateResp(StateResp_t pResp, int nConnId) {
		super.OnStateResp(pResp, nConnId);
		L.e(this, "OnStateResp()");
		pResp.getError();

		dealErrorWarnIcon(pResp);

		// pResp.delete();
	}

	@Override
	public void OnDeviceOnlineStateResp(DeviceOnlineStateResp_t pResp,
			int nConnId) {
		super.OnDeviceOnlineStateResp(pResp, nConnId);
		L.e(this, "OnDeviceOnlineStateResp isOnline == " + pResp.getIsOnline());
		if (pResp.getIsOnline() == 0) {
			onConnectEvent(Global.connectId, -7);
		}
	}

	@Override
	public void onTcpPacket(byte[] data, int connId) {
		super.onTcpPacket(data, connId);
		L.e(this, "电热的onTcpPacket()");
		L.e(this, "剩余热水百分比" + new EhState(data).getRemainingHotWaterAmount());
		if (connId != Global.connectId) {
			return;
		}
		/**
		 * <!--警告代码：0xa0-0xaf（警告A0-AF）,故障代码：0xe0-0xef（故障E0-EF）--> <field
		 * name="error" type="byte" value="{INPUT}" range="160-239" /> new
		 * EhState(data).getErrorCode()
		 */

		System.out.println("回调onTcpPacket");
		System.out.println("MainActivity.onTcpPacket()： "
				+ new EhState(data).getRemainingHotWaterAmount());

		// freezeProofing(data);
		// meibangWran(data);

		if (!canupdateView) {
			return;
		}

		if (TcpPacketCheckUtil.isEhStateData(data)) {
			stateQueried = true;
			// llt_power.setEnabled(true);
			btn_power.setSelected(false);
			setTempture(data);
			setLeaveWater(data);
			setPower(data);
			setTargerTempertureUI(data);
			setHotAnimition(data);
			// 调试返回2 夜电模式\ 1智能模式 \3晨浴模式\ 4自定义模式
			System.out.println("当前模式：" + new EhState(data).getFunctionState());
			// 非常奇怪 智能模式设置成功，可是返回值 确实1 跟p0 文档不符合。设置进去的时候是2 ，晨浴模式成功。
			int mode = new EhState(data).getFunctionState();
			currentModeCode = mode;
			L.e(this, "打印前");
			L.e(this, "断线之后返回的mode是 : " + mode);
			L.e(this, "打印后");

			if (mode == 1) {
				changeTojishiModeUpdateUI(data);
			} else if (mode == 3) {
				changeToMorningWashUpdateUI(data);
			} else if (mode == 4 || mode == 6) {
				changeToCustomModeUpdateUI(data);
			} else if (mode == 2) {
				changeToNightModeUpdateUI(data);
			} else if (mode == 7) { // 智能模式
				changeToIntelligenceModeUpdateUI(data);
			}

			if (!new EhState(data).isPoweredOn()) {
				System.out.println("关机了");
				circle_slider.setVisibility(View.GONE);
				tv_mode.setVisibility(View.INVISIBLE);
				powerTv.setText("--");
				rightButton.setVisibility(View.VISIBLE);
				btn_power.setSelected(false);
				rightButton.setBackgroundResource(R.drawable.icon_shut_disable);
				findViewById(R.id.pattern).setEnabled(false);
				findViewById(R.id.power).setEnabled(false);
				ChangeStuteView.swichDeviceOff(llt_statusParent);
				ison = false;
			} else {
				tv_mode.setVisibility(View.VISIBLE);
				findViewById(R.id.pattern).setEnabled(true);
				findViewById(R.id.power).setEnabled(true);
				rightButton.setBackgroundResource(R.drawable.icon_shut_enable);

				// circularView.setOn(true);
				ison = true;
			}
		}
	}

	private int currentModeCode = -1;

	private void setTempture(final byte[] b) {
		System.out.println("当前水温：" + new EhState(b).getInnerTemp1() + "   "
				+ new EhState(b).getInnerTemp2() + "   "
				+ new EhState(b).getInnerTemp3());
		// tempter.setText(new EhState(b).getInnerTemp2() + "");
		currentTemp = new EhState(b).getInnerTemp1();
		if (!circle_slider.isDraging() && !isSendingCommand) {
			// circularView.setAngle(new EhState(b).getInnerTemp1());
			circle_slider.setValue(new EhState(b).getTargetTemperature());
			// circularView.setAngle(new EhState(b).getTargetTemperature());
			Drawable img = getBaseContext().getResources().getDrawable(
					R.drawable.icon_temperature);
			// int dp32 = PxUtil.dip2px(getBaseContext(), 32);
			// img.setBounds(0, 0, dp32, dp32);
			temptertitleTextView.setCompoundDrawablesWithIntrinsicBounds(img,
					null, null, null);
			temptertitleTextView.setTag(false);
			temptertitleTextView.setText("当前水温");
			if (isError) {
				tv_tempter.setText("--");
			} else {
				tv_tempter.setText(new EhState(b).getInnerTemp1() + "");
			}
		}

		System.out.println("当前设置温度：" + new EhState(b).getTargetTemperature());
	}

	private void setHotAnimition(byte[] b) {
		if (new EhState(b).getSystemRunningState() == 0) {
			// 未加热
			hotImgeImageView.setVisibility(View.GONE);
		} else if (new EhState(b).getSystemRunningState() == 1) {
			// 加热
			hotImgeImageView.setVisibility(View.VISIBLE);
			if (new EhState(b).getPower() == 1) {
				hotImgeImageView
						.setBackgroundResource(R.drawable.anim_furmace_main_heating_power_1);
				// operatingAnim = AnimationUtils.loadAnimation(this,
				// R.anim.tip_4500);
			} else if (new EhState(b).getPower() == 2) {
				hotImgeImageView
						.setBackgroundResource(R.drawable.anim_furmace_main_heating_power_2);
				// operatingAnim = AnimationUtils.loadAnimation(this,
				// R.anim.tip_3500);
			} else if (new EhState(b).getPower() == 3) {
				hotImgeImageView
						.setBackgroundResource(R.drawable.anim_furmace_main_heating_power_3);
				// operatingAnim = AnimationUtils.loadAnimation(this,
				// R.anim.tip_2500);
			}
			((AnimationDrawable) hotImgeImageView.getBackground()).start();
			// ((AnimationDrawable) hotImgeImageView.getDrawable()).start();
		}
	}

	private void setLeaveWater(byte[] b) {
		System.out.println("当前水量："
				+ new EhState(b).getRemainingHotWaterAmount());
		leavewater.setText(new EhState(b).getRemainingHotWaterAmount() + "%");
	}

	private void setPower(byte[] b) {
		System.out.println("当前功率：" + new EhState(b).getPower());
		powerTv.setText(new EhState(b).getPower() + "kw");
	}

	private void setTargerTempertureUI(byte[] b) {
		if (!circle_slider.isDraging() && !isSendingCommand) {
			circle_slider.setValue(new EhState(b).getTargetTemperature());
			target_tem.setText(new EhState(b).getTargetTemperature() + "℃");
		}
	}

	private void setAppointmentButtonAble(boolean isAble) {
		// btn_appointment.setEnabled(isAble);
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
				ElectricHeaterSendCommandService.getInstance().openDevice();
			}
		});
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		L.e(this, "onConnectEvent@MainActivity:" + connId + "-" + event);

		if (connId == Global.connectId && event == -7) {
			// 连接断开
			changeToOfflineUI();

			// 收到主动断开,重连一次
			connectToDevice();
		}

	}

	private void dealDisConnect() {
		ison = false;
		currentModeCode = 0;
		isSendingCommand = false;
		tv_tempter.setText("--");
		tv_mode.setText("--模式");
		tv_mode.setVisibility(View.INVISIBLE);
		leavewater.setText("--%");
		powerTv.setText("--");
		target_tem.setText("--");
		btn_power.setSelected(false);
		btn_power.setEnabled(false);
		// llt_power.setEnabled(false);
		findViewById(R.id.pattern).setEnabled(false);
		rightButton.setBackgroundResource(R.drawable.icon_shut_disable);
		circle_slider.setVisibility(View.GONE);
		// circularView.setOn(false);
		iv_error.setVisibility(View.GONE);
		hotImgeImageView.setVisibility(View.GONE);
	}

	// @Override
	// public void levelListener(final int outlevel) {
	// sentToMsgAfterSix(outlevel);
	// hotImgeImageView.setVisibility(View.GONE);
	// }

	// @Override
	// public void updateUIListener(int outlevel) {
	// temptertitleTextView.setText("设置温度");
	// Drawable img = getBaseContext().getResources().getDrawable(
	// R.drawable.menu_icon_setting);
	// int dp32 = PxUtil.dip2px(getBaseContext(), 32);
	// img.setBounds(0, 0, dp32, dp32);
	// temptertitleTextView.setCompoundDrawables(img, null, null, null);
	//
	// tv_tempter.setText(outlevel + "");
	// circle_slider.setValue(outlevel);
	// Insetting = true;
	// }

	// @Override
	// public void updateUIWhenAferSetListener(final int outlevel) {
	// temptertitleTextView.setText("当前水温");
	// Drawable img = getBaseContext().getResources().getDrawable(
	// R.drawable.icon_temperature);
	// int dp32 = PxUtil.dip2px(getBaseContext(), 32);
	// img.setBounds(0, 0, dp32, dp32);
	// temptertitleTextView.setCompoundDrawables(img, null, null, null);
	//
	// if (isError) {
	// tv_tempter.setText("--");
	// } else {
	// tv_tempter.setText(currentTemp + "");
	// }
	// }

	// @Override
	// public void updateLocalUIdifferent(int outlevel) {
	//
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/** only for test */
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void didBeginTouchCircleSlider() {

	}

	private boolean isSettingIcon = false;

	@Override
	public void needChangeValue(int value, boolean isAdd) {
		if (value < 35) {
			value = 35;
		} else if (value > 75) {
			value = 75;
		}
		if (value >= 35 && value <= 75) {
			circle_slider.setValue(value);

			temptertitleTextView.setText("设置温度");
			if (temptertitleTextView.getTag() == null) {
				temptertitleTextView.setTag(false);
			}
			if (!(Boolean) temptertitleTextView.getTag()) {
				Drawable img = getBaseContext().getResources().getDrawable(
						R.drawable.menu_icon_setting);
				// int dp32 = PxUtil.dip2px(getBaseContext(), 32);
				// img.setBounds(0, 0, dp32, dp32);
				temptertitleTextView.setCompoundDrawablesWithIntrinsicBounds(
						img, null, null, null);
				temptertitleTextView.setTag(true);
			}

			tv_tempter.setText(value + "");
		}
	}

	@Override
	public void didEndChangeValue() {
		sendToMsgAfterThreeSeconds(circle_slider.getValue());
	}
}
