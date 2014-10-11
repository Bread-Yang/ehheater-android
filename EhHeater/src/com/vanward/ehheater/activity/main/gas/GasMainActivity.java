package com.vanward.ehheater.activity.main.gas;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.vanward.ehheater.activity.appointment.AppointmentTimeActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.LeftFragment;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.statedata.EhState;
import com.vanward.ehheater.util.CommonDialogUtil;
import com.vanward.ehheater.util.TcpPacketCheckUtil;
import com.vanward.ehheater.view.ChangeStuteView;
import com.vanward.ehheater.view.CircleListener;
import com.vanward.ehheater.view.CircularView;
import com.vanward.ehheater.view.PowerSettingDialogUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.vanward.ehheater.view.fragment.BaseSlidingFragmentActivity;
import com.vanward.ehheater.view.fragment.SlidingMenu;
import com.vanward.ehheater.view.wheelview.WheelView;
import com.xtremeprog.xpgconnect.generated.GasWaterHeaterStatusResp_t;
import com.xtremeprog.xpgconnect.generated.StateResp_t;
import com.xtremeprog.xpgconnect.generated.generated;

public class GasMainActivity extends BaseSlidingFragmentActivity implements
		OnClickListener {

	protected SlidingMenu mSlidingMenu;

	private TextView mTitleName, modeTv, temptertitleTextView;

	View btn_power;
	TextView tempter, leavewater, target_tem;

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

	private TextView powerTv;

	private Button btn_info;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSlidingMenu();
		setContentView(R.layout.main_gas_center_layout);
		initView(savedInstanceState);
		initData();
		IntentFilter filter = new IntentFilter(
				Consts.INTENT_FILTER_HEATER_NAME_CHANGED);
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				heaterNameChangeReceiver, filter);
		generated.SendStateReq(Global.connectId);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String tempname = modeTv.getText().toString();
		// if (tempname.equals("夜电模式") && tempname.equals("晨浴模式")
		// && tempname.equals("智能模式") && tempname.equals("自定义模式")) {
		// return;
		// }
		boolean flag = false;
		List<GasCustomSetVo> list = new BaseDao(this).getDb().findAll(
				GasCustomSetVo.class);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (tempname.equals(list.get(i).getName())) {
					flag = true;
				}
			}
			if (!flag) {
				modeTv.setText("自定义模式");
			}
		} else {
			modeTv.setText("自定义模式");
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private void initView(Bundle savedInstanceState) {
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

		initopenView();
		updateTitle();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				circularView = new CircularView(GasMainActivity.this,
						llt_circle, CircularView.CIRCULAR_SINGLE, 0);
				circularView.setEndangle(65);
				circularView.setAngle(35);
				circularView.setOn(true);
				operatingAnim = AnimationUtils.loadAnimation(
						GasMainActivity.this, R.anim.tip_4500);
				LinearInterpolator lin = new LinearInterpolator();
				operatingAnim.setInterpolator(lin);
				hotImgeImageView.startAnimation(operatingAnim);
				animationDrawable = (AnimationDrawable) iv_wave.getDrawable();
				animationDrawable.start();
				circularView.setCircularListener(new CircleListener() {
					@Override
					public void levelListener(final int outlevel) {
						SendMsgModel.setTempter(outlevel);
						tempter.setText(outlevel + "");
						// temptertitleTextView.setText("当前温度");
						hotImgeImageView.setVisibility(View.GONE);
						hotImgeImageView.clearAnimation();
					}

					@Override
					public void updateUIListener(int outlevel) {
						// TODO Auto-generated method stub
						temptertitleTextView.setText("设定温度");
						tempter.setText(outlevel + "");
					}

					@Override
					public void updateUIWhenAferSetListener(final int outlevel) {
						temptertitleTextView.setText("出水温度");
						tempter.setText(outlevel + "");
					}
				});
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

	private void updateTitle() {
		HeaterInfo heaterInfo = new HeaterInfoService(getBaseContext())
				.getCurrentSelectedHeater();
		if (heaterInfo != null) {
			mTitleName.setText(Consts.getHeaterName(heaterInfo));
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
			SendMsgModel.closeDevice();
			break;
		case R.id.appointment_btn:
			Intent intent = new Intent();
			intent.setClass(this, AppointmentTimeActivity.class);
			startActivity(intent);
			break;
		case R.id.pattern:
			Intent intent2 = new Intent();
			intent2.putExtra("name", modeTv.getText());
			intent2.setClass(this, PatternActivity.class);
			startActivity(intent2);
			break;

		case R.id.btn_information:
			break;
		}
	}

	/**
	 * 舒适模式
	 * 
	 * @param pResp
	 */
	public void changetoSofeMode(GasWaterHeaterStatusResp_t pResp) {
		modeTv.setText("舒适模式");
		circularView.setOn(true);
	}

	/**
	 * 厨房模式
	 * 
	 * @param pResp
	 */
	public void changetoKictienMode(GasWaterHeaterStatusResp_t pResp) {
		modeTv.setText("厨房模式");
		circularView.setOn(false);
	}

	/**
	 * 节能模式
	 * 
	 * @param pResp
	 */
	public void changetoEnergyMode(GasWaterHeaterStatusResp_t pResp) {
		modeTv.setText(" 节能模式");
		circularView.setOn(false);
	}

	/**
	 * 智能模式
	 * 
	 * @param pResp
	 */
	public void changetoligenceMode(GasWaterHeaterStatusResp_t pResp) {
		modeTv.setText("智能模式");
		circularView.setOn(false);
	}

	/**
	 * 浴缸模式
	 * 
	 * @param pResp
	 */
	public void changetoBathtubMode(GasWaterHeaterStatusResp_t pResp) {
		modeTv.setText("浴缸模式");
		circularView.setOn(true);
	}

	/**
	 * 自定义模式
	 * 
	 * @param pResp
	 */

	public void changetoDIYMode(GasWaterHeaterStatusResp_t pResp) {

		modeTv.setText("自定义模式");
		circularView.setOn(true);
		List<GasCustomSetVo> list = new BaseDao(this).getDb().findAll(
				GasCustomSetVo.class);

		// 自定义模式的名字怎么显示。
		// for (int i = 0; i < list.size(); i++) {
		// CustomSetVo customSetVo = list.get(i);
		// if (customSetVo.getPower() == power
		// && customSetVo.getTempter() == targetTemperature) {
		// modeTv.setText(customSetVo.getName());
		// break;
		// }
		// }
		circularView.setOn(true);
		// 剩余加热时间 好像燃热没有这个状态

	}

	// 有没有预约? 这个是 预约按钮无效的
	public void setAppointmentButtonAble(boolean isAble) {
		btn_appointment.setEnabled(isAble);
		btn_power.setEnabled(isAble);
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

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		if (connId == Global.connectId && event == -7) {
			// 连接断开
			CommonDialogUtil.showReconnectDialog(this);
		}
	}

	@Override
	public void OnGasWaterHeaterStatusResp(GasWaterHeaterStatusResp_t pResp,
			int nConnId) {
		modeDeal(pResp);
		temptertureDeal(pResp);
		waterDeal(pResp);
		onoffDeal(pResp);
		warningDeal(pResp);
		super.OnGasWaterHeaterStatusResp(pResp, nConnId);
	}

	/**
	 * 模式处理
	 * 
	 * @param pResp
	 */
	public void modeDeal(GasWaterHeaterStatusResp_t pResp) {

		// 模式切换的api 跟 那个需求有出入
		System.out.println("当前模式： " + pResp.getFunction_state());
		// 系统模式：0x01（舒适模式）、0x02（厨房模式）、0x03（浴缸模式）、0x04（节能模式）、
		// 0x05（智能模式）、0x06（自定义模式）

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
			changetoligenceMode(pResp);
			break;
		case 6:
			changetoDIYMode(pResp);
			break;
		default:
			break;
		}
	}

	/**
	 * 开关处理
	 * 
	 * @param pResp
	 */
	public void onoffDeal(GasWaterHeaterStatusResp_t pResp) {
		System.out.println("开关： " + pResp.getOn_off());// 1为开机0为关机
		if (pResp.getOn_off() == 0) {
			openView.setVisibility(View.VISIBLE);
			rightButton.setVisibility(View.GONE);
			ChangeStuteView.swichDeviceOff(stuteParent);
		} else {
			rightButton.setVisibility(View.VISIBLE);
			openView.setVisibility(View.GONE);
		}
	}

	/**
	 * 水温处理
	 * 
	 * @param pResp
	 */
	public void temptertureDeal(final GasWaterHeaterStatusResp_t pResp) {
		System.out.println("设置温度： " + pResp.getTargetTemperature());
		System.out.println("进水温度： " + pResp.getIncomeTemperature());
		System.out.println("出水温度： " + pResp.getOutputTemperature());
		target_tem.setText(pResp.getTargetTemperature() + "℃");
		circularView.setAngle(pResp.getOutputTemperature());
		circularView.setTargerdegree(pResp.getTargetTemperature());
		// tempter.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// // 设置初始化设定温度
		// tempter.setText(pResp.getOutputTemperature() + "℃");
		// }
		// }, 2000);
	}

	/**
	 * 水量处理
	 * 
	 * @param pResp
	 */
	public void waterDeal(GasWaterHeaterStatusResp_t pResp) {
		System.out.println("当前水流量： " + pResp.getNowVolume());
		System.out.println("当前设置水流量： " + pResp.getCustomWaterProportion());
		leavewater.setText(pResp.getNowVolume() + "L");
	}

	/**
	 * 警告处理
	 * 
	 * @param pResp
	 */
	public void warningDeal(GasWaterHeaterStatusResp_t pResp) {
		System.out.println("防冻警告：" + pResp.getFreezeProofingWarning());
		System.out.println("氧护提示：" + pResp.getOxygenWarning());
	}

}