package com.vanward.ehheater.activity.configure;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easylink.android.EasyLinkWifiManager;
import com.easylink.android.FirstTimeConfig2;
import com.easylink.android.FirstTimeConfigListener;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.WelcomeActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.EasyLinkDialogUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.TextStyleUtil;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;

public class ShitActivity extends EhHeaterBaseActivity implements OnClickListener, FirstTimeConfigListener {

	private RelativeLayout mRlStepContainer;
	private Button mBtnNextStep;
	private TextView mTvWifiSsid;
	private EditText mEtWifiPsw;
	private Dialog dialog_easylink;
	
	private Map<Integer, View> mMapStepViews = new HashMap<Integer, View>();

	
	@Override
	public void initUI() {
		super.initUI();
		
		setCenterView(R.layout.activity_configure);
		setRightButton(View.INVISIBLE);
		setLeftButtonBackground(R.drawable.icon_back);
		setTopText(R.string.setting_new_device);
		
		mBtnNextStep = (Button) findViewById(R.id.ac_btn_next_step);
		mBtnNextStep.setOnClickListener(this);
		mRlStepContainer = (RelativeLayout) findViewById(R.id.ac_rl_step_container);
		mRlStepContainer.addView(getStepView(1));

//		initEasyLinkDialog();
		dialog_easylink = EasyLinkDialogUtil.initEasyLinkDialog(this, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mEasyLinkTimeoutTimer.cancel();
				dialog_easylink.dismiss();
				stopEasyLink();
			}
		});
		

		IntentFilter filter = new IntentFilter(Consts.INTENT_FILTER_KILL_CONFIGURE_ACTIVITY);
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, filter);
		
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
	
	@Override
	public void onBackPressed() {
		int childCount = mRlStepContainer.getChildCount();
	
		if (childCount > 1) {
			mRlStepContainer.removeViewAt(childCount - 1);
		} else {
			
			boolean shouldKillProcess = getIntent().getBooleanExtra(
					Consts.INTENT_EXTRA_CONFIGURE_ACTIVITY_SHOULD_KILL_PROCESS_WHEN_FINISH, false);
			if (shouldKillProcess) {
				android.os.Process.killProcess(android.os.Process.myPid());
			} else {
				super.onBackPressed();
			}
			
		}
	}
	
	private void nextStep() {
		int curStep = mRlStepContainer.getChildCount();
		View step = getStepView(curStep+1);
		if (step != null) {
			mRlStepContainer.addView(step);
		} else {
			// 到了最后一步
			easyLink();
		}
	}
	
	private View getStepView(int whichStep) {
		
		View v = mMapStepViews.get(whichStep);
		if (v == null) {
			v = initStepView(whichStep);
			mMapStepViews.put(whichStep, v);
		}
		
		if (v != null && v.getParent() != null) {
			((ViewGroup)v.getParent()).removeView(v);
		}
		
		return v;
	}
	
	private View initStepView(int whichStep) {
		
		View v = null;
		
		switch (whichStep) {
		
		case 1: 
			v = getLayoutInflater().inflate(R.layout.activity_configure_step1, mRlStepContainer, false);
			TextView s1tip = (TextView) v.findViewById(R.id.acs1_tv_tip);
			TextStyleUtil.setColorStringInTextView(s1tip, Color.parseColor("#ff5f00"), new String[] {"电源"});
			break;
		
		case 2:
			v = getLayoutInflater().inflate(R.layout.activity_configure_step2, mRlStepContainer, false);
			mTvWifiSsid = (TextView) v.findViewById(R.id.acs2_tv_ssid);
			mEtWifiPsw = (EditText) v.findViewById(R.id.acs2_et_psw);
			
			applyCurWifiSsid();
			break;
		case 3:
			v = getLayoutInflater().inflate(R.layout.activity_configure_step3, mRlStepContainer, false);
			TextView s3tip = (TextView) v.findViewById(R.id.acs3_tv_tip);
			TextStyleUtil.setColorStringInTextView(s3tip, Color.parseColor("#ff5f00"), new String[] {"3秒","三声"});
			break;
		
		}
		
		return v;
		
	}
	
	private void applyCurWifiSsid() {
		String curWifiSsid = new EasyLinkWifiManager(getBaseContext()).getCurrentSSID();
		mTvWifiSsid.setText(curWifiSsid);
		new SharedPreferUtils(getBaseContext()).put(ShareKey.PendingSsid, curWifiSsid);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ac_btn_next_step:
			
			if (mRlStepContainer.getChildCount() == 1 && mTvWifiSsid != null) {
				applyCurWifiSsid();
			}
			
			if (mRlStepContainer.getChildCount() == 2 && mTvWifiSsid != null
					&& "<unknown ssid>".equals(mTvWifiSsid.getText().toString())) {
				Toast.makeText(getBaseContext(), "请连接至wifi网络", 500).show();
				return;
			}
			
			if (mRlStepContainer.getChildCount() == 2 && mEtWifiPsw != null 
					&& TextUtils.isEmpty(mEtWifiPsw.getText())) {
				Toast.makeText(getBaseContext(), "请输入wifi密码", 500).show();
				return;
			}
			
			nextStep();
			break;
		case R.id.btn_left:
			onBackPressed();;
			break;
		}
	}
	
	private void easyLink() {

		EasyLinkWifiManager manager = new EasyLinkWifiManager(this);
		String ssid = manager.getCurrentSSID();
		String gateway = manager.getGatewayIpAddress();
		String pss = /*"go4xpggo4xpg"*/mEtWifiPsw.getText().toString();
		
		dialog_easylink.show();
		mEasyLinkTimeoutTimer.start();
		
		try {
			configer = new FirstTimeConfig2(this,
					pss,
					null,
					gateway,
					ssid);
			
			configer.transmitSettings();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	FirstTimeConfig2 configer;
	EasyLinkTimeoutCounter mEasyLinkTimeoutTimer = new EasyLinkTimeoutCounter(60000, 1000);
	
	private void stopEasyLink() {
		try {
			configer.stopTransmitting();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFirstTimeConfigEvent(FtcEvent paramFtcEvent,
			Exception paramException) {

	}
	
	@Override
	public void onEasyLinkResp(XpgEndpoint endpoint) {
		if (easyLkRespCount++ == 0) {
			// 配置成功, 保存设备(此时密码为空), 跳转回welcome
			stopEasyLink();
			tempEndpoint = endpoint;
			mEasyLinkTimeoutTimer.cancel();
			dialog_easylink.dismiss();
			XPGConnectClient.RemoveActivity(this);
			
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					finishingConfig();
				}
			}, 300);
		}
	}
	XpgEndpoint tempEndpoint;
	int easyLkRespCount;
	
	private void finishingConfig() {

		Toast.makeText(getBaseContext(), "配置成功!", 1000).show();

		HeaterInfo hinfo = new HeaterInfo(tempEndpoint);
		new HeaterInfoService(getBaseContext()).addNewHeater(hinfo);
		Log.d("emmm", "finishingConfig:new heater saved!" + hinfo.getMac() + "-" + hinfo.getPasscode());
		
		String username = AccountService.getPendingUserId(getBaseContext());
		String userpsw = AccountService.getPendingUserPsw(getBaseContext());
		if (!AccountService.isLogged(getBaseContext()) 
				&& !TextUtils.isEmpty(username) 
				&& !TextUtils.isEmpty(userpsw)) {
			AccountService.setUser(getBaseContext(), username, userpsw);
		}

		// send a broad cast to finish login activity
		Intent killerIntent = new Intent(Consts.INTENT_FILTER_KILL_LOGIN_ACTIVITY);
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(killerIntent);
		
		Intent intent = new Intent();
		intent.setClass(getBaseContext(), WelcomeActivity.class);
		intent.putExtra(Consts.INTENT_EXTRA_FLAG_REENTER, true);
		startActivity(intent);
		
		XPGConnectClient.RemoveActivity(this);
		finish();
	}

	private class EasyLinkTimeoutCounter extends CountDownTimer {
		
		public EasyLinkTimeoutCounter(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			dialog_easylink.dismiss();
			stopEasyLink();
			
			startActivity(new Intent(getBaseContext(), AutoConfigureFailActivity.class));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			((TextView)dialog_easylink.findViewById(R.id.easylink_time_tv)).setText(millisUntilFinished / 1000 + "");
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/**only for test*/
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

//			HeaterInfo hinfo = new HeaterInfo();
//			hinfo.setMac("C8934641B3B4");
//			hinfo.setDid("1");
//			hinfo.setPasscode("FKAIDJKART");
//			return hinfo;
//			
//			HeaterInfo hinfo = new HeaterInfo();
//			hinfo.setMac("C8934642E4C7");
//			hinfo.setDid("o4kvBWCq5QwcWuZZbm4w4Z");
//			hinfo.setPasscode("JPDRRIXEKX");
//			hinfo.setBinded(1);
//			return hinfo;
			
			
//			HeaterInfo hinfo1 = new HeaterInfo();
//			hinfo1.setMac("C8934642E4C7");
//			hinfo1.setPasscode("JPDRRIXEKX");
//			
//			HeaterInfo hinfo2 = new HeaterInfo();
//			hinfo2.setMac("C8934642F763");
//			hinfo2.setPasscode("UMBXIWTCEM");
//			
//			HeaterInfoService hser = new HeaterInfoService(getBaseContext());
//			hser.addNewHeater(hinfo2);
//			hser.addNewHeater(hinfo1);
			
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
