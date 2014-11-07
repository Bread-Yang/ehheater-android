package com.vanward.ehheater.activity.main.furnace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.xtremeprog.xpgconnect.generated.GasWaterHeaterStatusResp_t;
import com.xtremeprog.xpgconnect.generated.generated;

public class FurnaceMainActivity extends BaseSlidingFragmentActivity implements
		OnClickListener, OnLongClickListener, CircleListener {

	protected SlidingMenu mSlidingMenu;

	private Button btn_top_right, btn_appointment, btn_setting,
			btn_intellectual;

	private TextView mTitleName, tv_mode, tv_status;

	private LinearLayout llt_circle;

	private ImageView iv_rotate_animation;

	private RelativeLayout rlt_content, rlt_open;

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
		tv_mode = (TextView) findViewById(R.id.tv_mode);
		tv_status = (TextView) findViewById(R.id.tv_status);
		rlt_content = (RelativeLayout) findViewById(R.id.rlt_content);
		llt_circle = (LinearLayout) findViewById(R.id.llt_circle);
		iv_rotate_animation = (ImageView) findViewById(R.id.iv_rotate_animation);
	}

	private void setListener() {
		((Button) findViewById(R.id.ivTitleBtnLeft)).setOnClickListener(this);
		btn_top_right.setOnClickListener(this);
		btn_setting.setOnClickListener(this);
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

				iv_rotate_animation.setVisibility(View.GONE);
				llt_circle.addView(circularView);
				// circularView.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void OnGasWaterHeaterStatusResp(GasWaterHeaterStatusResp_t pResp,
			int nConnId) {
		onOffDeal(pResp);

		super.OnGasWaterHeaterStatusResp(pResp, nConnId);
	}

	private void onOffDeal(GasWaterHeaterStatusResp_t pResp) {
		if (pResp.getOn_off() == 0) {
			setCircularViewEnable(false, pResp);
			tv_status.setText(R.string.shutdown); // 关机
			btn_setting.setEnabled(false);
			isOn = false;
		} else {
			// rightButton.setVisibility(View.VISIBLE);
			// openView.setVisibility(View.GONE);
			if (pResp.getFlame() != 1) {
				setCircularViewEnable(true, pResp);
				tv_status.setText(R.string.standby);
				btn_setting.setEnabled(true);
				isOn = true;
			}

		}
	}

	private void setCircularViewEnable(boolean enable,
			GasWaterHeaterStatusResp_t pResp) {
		if (enable) {
			if (pResp.getFunction_state() == 3
					|| pResp.getFunction_state() == 1) {
				circularView.setVisibility(View.VISIBLE);
			} else {
				circularView.setVisibility(View.GONE);
			}
		} else {
			circularView.setVisibility(View.GONE);
		}
		tv_mode.setEnabled(enable);
	}

	private void sendToMsgAfterThreeSeconds(final int value) {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		mCountDownTimer = new CountDownTimer(3000, 1000) {

			@Override
			public void onTick(long arg0) {
				// TODO Auto-generated method stub
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
