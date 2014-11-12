package com.vanward.ehheater.activity.main;

import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.appointment.AppointmentListActivity;
import com.vanward.ehheater.activity.configure.ConnectActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.info.InformationActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.statedata.EhState;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.PxUtil;
import com.vanward.ehheater.util.TcpPacketCheckUtil;
import com.vanward.ehheater.view.ChangeStuteView;
import com.vanward.ehheater.view.CircleListener;
import com.vanward.ehheater.view.CircularView;
import com.vanward.ehheater.view.DeviceOffUtil;
import com.vanward.ehheater.view.PowerSettingDialogUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.vanward.ehheater.view.fragment.BaseSlidingFragmentActivity;
import com.vanward.ehheater.view.fragment.SlidingMenu;
import com.vanward.ehheater.view.wheelview.WheelView;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.StateResp_t;
import com.xtremeprog.xpgconnect.generated.generated;

public class MainActivity extends BaseSlidingFragmentActivity implements
		OnClickListener, CircleListener {

	protected SlidingMenu mSlidingMenu;

	private LeftFragment mLeftFragment;

	private RightFragment mRightFragment;

	private TextView mTitleName, modeTv, powerTv, temptertitleTextView;

	private Button btn_info;
	View btn_power;
	TextView tempter, leavewater, target_tem;

	private Dialog dialog_power_setting;

	private WheelView wheelView1, wheelView2;

	LinearLayout llt_circle;
	ViewGroup stuteParent;
	Button btn_appointment;
	CircularView circularView;
	ImageView iv_wave, hotImgeImageView;
	AnimationDrawable animationDrawable;
	RelativeLayout rlt_start_device, content;

	private View openView;

	private Button rightButton;

	private Animation operatingAnim;

	BroadcastReceiver heaterNameChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("emmm", "onReceive@mainac");
			updateTitle();
			initSlidingMenu();
		}
	};

	private CountDownTimer mCountDownTimer;

	boolean canupdateView = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSlidingMenu();
		setContentView(R.layout.main_center_layout);
		initView();
		initData();

		IntentFilter filter = new IntentFilter(
				Consts.INTENT_FILTER_HEATER_NAME_CHANGED);
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				heaterNameChangeReceiver, filter);
		canupdateView = false;

		HeaterInfo curHeater = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeater();
		String mac = curHeater.getMac();
		String passcode = curHeater.getPasscode();
		String userId = AccountService.getUserId(getBaseContext());
		String userPsw = AccountService.getUserPsw(getBaseContext());

		ConnectActivity.connectToDevice(this, mac, passcode, userId, userPsw);
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

			if (Global.connectId != -1) {
				XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
			}

			Global.connectId = connId;

			if (isOnline) {
				DialogUtil.instance().showQueryingDialog(this);
				generated.SendStateReq(Global.connectId);
			} else {
				// TODO 设备不在线

			}

			updateTitle(); // connect回调可能是由于切换了热水器, 需更新title
			mSlidingMenu.showContent();

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		canupdateView = true;
		// String tempname = modeTv.getText().toString();
		//
		// if (tempname.equals("夜电模式") || tempname.equals("晨浴模式")
		// || tempname.equals("智能模式") || tempname.equals("自定义模式")) {
		// return;
		// }
		// boolean flag = false;
		// List<CustomSetVo> list = new BaseDao(this).getDb().findAll(
		// CustomSetVo.class);
		// if (list != null && list.size() > 0) {
		// for (int i = 0; i < list.size(); i++) {
		// if (tempname.equals(list.get(i).getName())) {
		// flag = true;
		// }
		// }
		// if (!flag) {
		// modeTv.setText("智能模式");
		// }
		// } else {
		// modeTv.setText("智能模式");
		// }
		// 自动切换到智能模式
		// modeTv.post(new Runnable() {
		// @Override
		// public void run() {
		// SendMsgModel.changeToIntelligenceModeWash();
		// }
		// });
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		DialogUtil.dismissDialog();
	};

	@Override
	public void onBackPressed() {
		XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private void initView() {
		((Button) findViewById(R.id.ivTitleBtnLeft)).setOnClickListener(this);
		rightButton = ((Button) findViewById(R.id.ivTitleBtnRigh));
		rightButton.setOnClickListener(this);
		btn_appointment = (Button) findViewById(R.id.appointment_btn);
		powerTv = (TextView) findViewById(R.id.power_tv);
		btn_power = findViewById(R.id.power);
		hotImgeImageView = (ImageView) findViewById(R.id.hotanimition);

		hotImgeImageView.clearAnimation();

		temptertitleTextView = (TextView) findViewById(R.id.temptertext);
		target_tem = (TextView) findViewById(R.id.target_tem);
		rlt_start_device = (RelativeLayout) findViewById(R.id.start_device_rlt);
		btn_info = (Button) findViewById(R.id.btn_information);
		modeTv = (TextView) findViewById(R.id.mode_tv);
		llt_circle = (LinearLayout) findViewById(R.id.circle_llt);
		stuteParent = (ViewGroup) findViewById(R.id.stute);
		mTitleName = (TextView) findViewById(R.id.ivTitleName);
		iv_wave = (ImageView) findViewById(R.id.wave_bg);
		tempter = (TextView) findViewById(R.id.tempter);
		leavewater = (TextView) findViewById(R.id.leavewater);
		content = (RelativeLayout) findViewById(R.id.content);

		btn_appointment.setOnClickListener(this);
		btn_power.setOnClickListener(this);
		rlt_start_device.setOnClickListener(this);
		powerTv.setOnClickListener(this);

		initopenView();
		updateTitle();
		boolean InSetting = false;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				circularView = new CircularView(MainActivity.this, llt_circle,
						CircularView.CIRCULAR_SINGLE, 0);
				circularView.setEndangle(75);
				circularView.setAngle(35);
				circularView.setOn(false);
				operatingAnim = AnimationUtils.loadAnimation(MainActivity.this,
						R.anim.tip_4500);
				LinearInterpolator lin = new LinearInterpolator();
				operatingAnim.setInterpolator(lin);
				hotImgeImageView.startAnimation(operatingAnim);

				animationDrawable = (AnimationDrawable) iv_wave.getDrawable();
				animationDrawable.start();
				circularView.setCircularListener(MainActivity.this);
				llt_circle.addView(circularView);
			}
		}, 100);
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

	public void sentToMsgAfterSix(final int value) {
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
				tempter.setText(value + "");
				Insetting = false;
			}
		};
		mCountDownTimer.start();
	}

	private void updateTitle() {

		HeaterInfo heaterInfo = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeater();
		if (heaterInfo != null) {
			mTitleName.setText(Consts.getHeaterName(heaterInfo));
		}
	}

	boolean ison = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_device_rlt:
			/* generated.SendOnOffReq(Global.connectId, (short) 1); */
			rlt_start_device.setVisibility(View.GONE);
			break;
		case R.id.ivTitleBtnLeft:
			mSlidingMenu.showMenu(true);
			break;
		case R.id.ivTitleBtnRigh:
			/* generated.SendOnOffReq(Global.connectId, (short) 0); */

			if (ison) {

				DeviceOffUtil.instance(this)
						.nextButtonCall(new NextButtonCall() {
							@Override
							public void oncall(View v) {
								SendMsgModel.closeDevice();
								DeviceOffUtil.instance(MainActivity.this)
										.dissmiss();
							}
						}).showDialog();
			} else {
				SendMsgModel.openDevice();
			}

			break;
		case R.id.appointment_btn:
			Intent intent = new Intent();
			intent.setClass(this, AppointmentListActivity.class);
			startActivity(intent);
			break;
		case R.id.pattern:
			Intent intent2 = new Intent();
			intent2.putExtra("name", modeTv.getText());
			intent2.setClass(this, PatternActivity.class);
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
			intent3.setClass(this, InformationActivity.class);
			startActivity(intent3);

			break;

		case R.id.power_tv:
			if (currentModeCode == 7 || currentModeCode == 4) {
				setPower();
			}
			break;

		}
	}

	private void changeToIntelligenceModeUpdateUI(byte[] data) {
		modeTv.setText("智能模式");
		circularView.setOn(true);
		setAppointmentButtonAble(true);
		ChangeStuteView.swichLeaveMinView(stuteParent, 10);
		btn_power.setOnClickListener(this);
		int i = new EhState(data).getRemainingHeatingTime();
		if (i == 0 || i == -1) {
			ChangeStuteView.swichKeep(stuteParent);
		} else {
			ChangeStuteView.swichLeaveMinView(stuteParent, i);
		}

	}

	private void changeTojishiModeUpdateUI(byte[] data) {
		modeTv.setText("即时加热");
		circularView.setOn(false);
		setAppointmentButtonAble(true);
		ChangeStuteView.swichLeaveMinView(stuteParent, 10);
		btn_power.setOnClickListener(this);
		btn_power.setSelected(true);
		int i = new EhState(data).getRemainingHeatingTime();

		if (i == 0 || i == -1) {
			ChangeStuteView.swichKeep(stuteParent);
		} else {
			ChangeStuteView.swichLeaveMinView(stuteParent, i);
		}

	}

	private void changeToNightModeUpdateUI(byte[] data) {
		modeTv.setText("夜电模式");
		circularView.setOn(false);
		setAppointmentButtonAble(false);
		ChangeStuteView.swichNight(stuteParent);
		int i = new EhState(data).getRemainingHeatingTime();
		if (i == 0 || i == -1) {
			ChangeStuteView.swichNight(stuteParent);
		} else {
			ChangeStuteView.swichLeaveMinView(stuteParent, i);
		}

	}

	// public void setButtonCanUse(){
	// btn_power.
	//
	// }

	private void changeToCustomModeUpdateUI(byte[] data) {
		modeTv.setText("自定义模式");
		EhState ehState = new EhState(data);
		setAppointmentButtonAble(true);
		final int targetTemperature = ehState.getTargetTemperature();
		final int power = ehState.getPower();
		modeTv.post(new  Runnable() {
			
			@Override
			public void run() {
				List<CustomSetVo> list = new BaseDao(MainActivity.this).getDb().findAll(
						CustomSetVo.class);

				for (int i = 0; i < list.size(); i++) {
					CustomSetVo customSetVo = list.get(i);

					if (customSetVo.getPower() == power
							&& customSetVo.getTempter() == targetTemperature) {

						if (customSetVo.getName().length() > 6) {
							modeTv.setText(customSetVo.getName().substring(0, 6)
									+ "...");
						} else {
							modeTv.setText(customSetVo.getName());
						}

						break;
					}

				}
			}
		});
	
		circularView.setOn(true);
		ChangeStuteView.swichLeaveMinView(stuteParent, 10);
		// powerTv.setText(3 + "kw");
		btn_power.setOnClickListener(this);
		int i = new EhState(data).getRemainingHeatingTime();
		if (i == 0 || i == -1) {
			ChangeStuteView.swichKeep(stuteParent);
		} else {
			ChangeStuteView.swichLeaveMinView(stuteParent, i);
		}

	}

	public void Tojishi() {
		SendMsgModel.changeToJishiMode();
	}

	public void setPower() {
		int oldvalue = Integer.parseInt((String) powerTv.getText().subSequence(
				0, 1));

		PowerSettingDialogUtil.instance(this)
				.setNextButtonCall(new NextButtonCall() {

					@Override
					public void oncall(View v) {
						int i = PowerSettingDialogUtil.instance(
								MainActivity.this).getPower();
						System.out.println("0x00: " + (short) (0x00 + i));
						SendMsgModel.setPower(i);
						PowerSettingDialogUtil.instance(MainActivity.this)
								.dissmiss();
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
		circularView.setOn(false);
		setAppointmentButtonAble(false);
		modeTv.setText("晨浴模式");
		ChangeStuteView.swichMorningWash(stuteParent);
		int i = new EhState(data).getRemainingHeatingTime();
		System.out.println("测试晨浴i: " + i);
		if (i == 0) {
			ChangeStuteView.swichMorningWash(stuteParent);

		} else if (new EhState(data).getSystemRunningState() == 1) {
			ChangeStuteView.swichLeaveMinView(stuteParent, i);
		}

	}

	@Override
	public void OnStateResp(StateResp_t pResp, int nConnId) {
		System.out.println("回调");
		super.OnStateResp(pResp, nConnId);
	}

	@Override
	public void onTcpPacket(byte[] data, int connId) {
		super.onTcpPacket(data, connId);

		if (connId != Global.connectId) {
			return;
		}

		System.out.println("回调onTcpPacket");
		System.out.println("MainActivity.onTcpPacket()： "
				+ new EhState(data).getRemainingHotWaterAmount());

		if (!canupdateView) {
			return;
		}

		if (TcpPacketCheckUtil.isEhStateData(data)) {
			DialogUtil.dismissDialog();
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
			if (mode == 1) {
				changeTojishiModeUpdateUI(data);
			} else if (mode == 3) {
				changeToMorningWashUpdateUI(data);
			} else if (mode == 4 || mode == 6) {
				changeToCustomModeUpdateUI(data);
			} else if (mode == 2) {
				changeToNightModeUpdateUI(data);
			} else if (mode == 7) {
				changeToIntelligenceModeUpdateUI(data);
			}

			byte b = new EhState(data).getSystemRunningState();
			System.out.println("onTcpPacket: " + b);
			if (!new EhState(data).isPoweredOn()) {
				System.out.println("关机了");
				// openView.setVisibility(View.VISIBLE);
				circularView.setOn(false);
				powerTv.setText("--");
				rightButton.setVisibility(View.VISIBLE);
				btn_power.setSelected(false);
				rightButton.setBackgroundResource(R.drawable.icon_shut_enable);
				findViewById(R.id.pattern).setEnabled(false);
				findViewById(R.id.power).setEnabled(false);
				ChangeStuteView.swichDeviceOff(stuteParent);
				ison = false;
			} else {
				// rightButton.setVisibility(View.VISIBLE);F
				// openView.setVisibility(View.GONE);
				findViewById(R.id.pattern).setEnabled(true);
				findViewById(R.id.power).setEnabled(true);
				rightButton.setBackgroundResource(R.drawable.icon_shut_able);

				// circularView.setOn(true);
				ison = true;
			}
		}
	}

	private int currentModeCode = -1;

	public void setTempture(final byte[] b) {
		System.out.println("当前水温：" + new EhState(b).getInnerTemp1() + "   "
				+ new EhState(b).getInnerTemp2() + "   "
				+ new EhState(b).getInnerTemp3());
		// tempter.setText(new EhState(b).getInnerTemp2() + "");
		if (!Insetting && circularView != null) {
			circularView.setAngle(new EhState(b).getInnerTemp1());
			tempter.setText(new EhState(b).getInnerTemp1() + "");
		}

		System.out.println("当前设置温度：" + new EhState(b).getTargetTemperature());
	}

	public void setHotAnimition(byte[] b) {
		System.out.println("是否加热中：" + new EhState(b).getSystemRunningState());
		if (new EhState(b).getSystemRunningState() == 0) {
			// 未加热
			hotImgeImageView.setVisibility(View.GONE);
			hotImgeImageView.clearAnimation();
		} else if (new EhState(b).getSystemRunningState() == 1) {
			// 加热
			hotImgeImageView.setVisibility(View.VISIBLE);
			hotImgeImageView.clearAnimation();
			if (new EhState(b).getPower() == 1) {
				operatingAnim = AnimationUtils.loadAnimation(this,
						R.anim.tip_4500);
			} else if (new EhState(b).getPower() == 2) {
				operatingAnim = AnimationUtils.loadAnimation(this,
						R.anim.tip_3500);
			} else if (new EhState(b).getPower() == 3) {
				operatingAnim = AnimationUtils.loadAnimation(this,
						R.anim.tip_2500);
			}
			LinearInterpolator lin = new LinearInterpolator();
			operatingAnim.setInterpolator(lin);
			hotImgeImageView.startAnimation(operatingAnim);
		}
	}

	public void setLeaveWater(byte[] b) {
		System.out.println("当前水量："
				+ new EhState(b).getRemainingHotWaterAmount());
		leavewater.setText(new EhState(b).getRemainingHotWaterAmount() + "%");
	}

	public void setPower(byte[] b) {
		System.out.println("当前功率：" + new EhState(b).getPower());
		powerTv.setText(new EhState(b).getPower() + "kw");
	}

	public void setTargerTempertureUI(byte[] b) {
		circularView.setTargerdegree(new EhState(b).getTargetTemperature());
		target_tem.setText(new EhState(b).getTargetTemperature() + "℃");
	}

	public void setAppointmentButtonAble(boolean isAble) {
		// btn_appointment.setEnabled(isAble);
	}

	public void initopenView() {
		openView = LinearLayout.inflate(this, R.layout.activity_open, null);
		RelativeLayout.LayoutParams lParams = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		content.addView(openView, lParams);
		openView.setVisibility(View.GONE);
		openView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SendMsgModel.openDevice();
			}
		});
	}

	boolean isConnect = true;

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		Log.d("emmm", "onConnectEvent@MainActivity:" + connId + "-" + event);

		if (connId == Global.connectId && event == -7) {
			// 连接断开
			ChangeStuteView.swichdisconnect(stuteParent);
			dealDisConnect();
			isConnect = false;
			DialogUtil.instance().showReconnectDialog(this);
		}
	}

	public void dealDisConnect() {
		tempter.setText("--");
		modeTv.setText("----");
		leavewater.setText("--");
		powerTv.setText("--");
		target_tem.setText("--");
	}

	boolean Insetting = false;

	@Override
	public void levelListener(final int outlevel) {
		// SendMsgModel.setTempter(outlevel);
		// tempter.setText(outlevel + "");
		sentToMsgAfterSix(outlevel);
		// temptertitleTextView.setText("当前温度");
		hotImgeImageView.setVisibility(View.GONE);
		hotImgeImageView.clearAnimation();
	}

	@Override
	public void updateUIListener(int outlevel) {
		temptertitleTextView.setText("设置温度");
		Drawable img = getBaseContext().getResources().getDrawable(
				R.drawable.menu_icon_setting);
		int dp32 = PxUtil.dip2px(getBaseContext(), 32);
		img.setBounds(0, 0, dp32, dp32);
		temptertitleTextView.setCompoundDrawables(img, null, null, null);

		tempter.setText(outlevel + "");
		circularView.setTargerdegree(outlevel);
		Insetting = true;
	}

	@Override
	public void updateUIWhenAferSetListener(final int outlevel) {
		temptertitleTextView.setText("当前水温");
		Drawable img = getBaseContext().getResources().getDrawable(
				R.drawable.icon_temperature);
		int dp32 = PxUtil.dip2px(getBaseContext(), 32);
		img.setBounds(0, 0, dp32, dp32);
		temptertitleTextView.setCompoundDrawables(img, null, null, null);

		tempter.setText(outlevel + "");
	}

	@Override
	public void updateLocalUIdifferent(int outlevel) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/** only for test */
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
		}
		return super.onKeyDown(keyCode, event);
	}

}
