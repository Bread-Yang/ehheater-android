package com.vanward.ehheater.activity.configure;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easylink.android.EasyLinkWifiManager;
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
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

public class ManualConfStep2Activity extends EhHeaterBaseActivity {
	
	private TextView mTvSsid;
	private EditText mEtPsw;
	private Button mBtnNext;
	private Dialog dialog_easylink;
	private EasyLinkTimeoutCounter counter = new EasyLinkTimeoutCounter(60000, 1000);
	
	@Override
	public void initUI() {
		super.initUI();
		setTopText(R.string.manual_conf_guide);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.INVISIBLE);
		setCenterView(R.layout.activity_manual_conf_2);
		
		mTvSsid = (TextView) findViewById(R.id.amc2_tv_ssid);
		mEtPsw = (EditText) findViewById(R.id.amc2_et_psw);
		mBtnNext = (Button) findViewById(R.id.amc2_btn_next);
		
		mTvSsid.setText(new SharedPreferUtils(getBaseContext()).get(ShareKey.PendingSsid, ""));
		mBtnNext.setOnClickListener(this);
		
		dialog_easylink = EasyLinkDialogUtil.initEasyLinkDialog(this, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// cancel listener
				dialog_easylink.dismiss();
				counter.cancel();
			}
		});
		
	}
	
	
	@Override
	public void onClick(View view) {
		super.onClick(view);
		
		switch (view.getId()) {
		
		case R.id.amc2_btn_next:
			
			if (TextUtils.isEmpty(mEtPsw.getText())) {
				Toast.makeText(getBaseContext(), "请输入wifi密码", 500).show();
			} 
			
			dialog_easylink.show();
			counter.start();
			generated.SendOnboardingSetReq(generated.String2XpgData(mTvSsid.getText().toString()), 
					generated.String2XpgData(mEtPsw.getText().toString()));
			
			break;
		}
		
	}
	
	
	@Override
	public void onEasyLinkResp(XpgEndpoint endpoint) {
		if (easyLkRespCount++ == 0) {
			// 配置成功, 保存设备(此时密码为空), 跳转回welcome
			tempEndpoint = endpoint;
			finishingConfig();
		}
	}
	XpgEndpoint tempEndpoint;
	int easyLkRespCount;
	
	private void finishingConfig() {

		counter.cancel();
		dialog_easylink.dismiss();
		Toast.makeText(getBaseContext(), "配置成功!", 1000).show();

		Log.e("打印productKey前", "打印productKey前");
		HeaterInfo hinfo = new HeaterInfo(tempEndpoint);
		Log.e("productKey是 : ", hinfo.getProductKey());
//		hinfo.setPasscode(generated.XpgData2String(pResp.getPasscode()));
		new HeaterInfoService(getBaseContext()).addNewHeater(hinfo);
		Log.d("emmm", "finishingConfig:new heater saved!" + hinfo.getMac() + "-" + hinfo.getPasscode());
		
		String username = AccountService.getPendingUserId(getBaseContext());
		String userpsw = AccountService.getPendingUserPsw(getBaseContext());
		if (!AccountService.isLogged(getBaseContext()) 
				&& !TextUtils.isEmpty(username) 
				&& !TextUtils.isEmpty(userpsw)) {
			AccountService.setUser(getBaseContext(), username, userpsw);
		}

		// send broadcasts to finish previous activities
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				new Intent(Consts.INTENT_FILTER_KILL_LOGIN_ACTIVITY));
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				new Intent(Consts.INTENT_FILTER_KILL_CONFIGURE_ACTIVITY));
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				new Intent(Consts.INTENT_FILTER_KILL_AUTO_CONFIGURE_FAIL_ACTIVITY));
		
//		Intent intent = new Intent();
//		intent.setClass(getBaseContext(), WelcomeActivity.class);
//		intent.putExtra(Consts.INTENT_EXTRA_FLAG_REENTER, true);
//		startActivity(intent);
//		
//		XPGConnectClient.RemoveActivity(this);
//		finish();
		waitWifiChange();
	}
	
	private void waitWifiChange() {
		Dialog waitDialog = EasyLinkDialogUtil.initWaitWifiDialog(this, null);
		waitDialog.show();
		new CountDownTimer(120000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				String curSsid = new EasyLinkWifiManager(getBaseContext()).getCurrentSSID();
				String pendingSsid = new SharedPreferUtils(getBaseContext()).get(ShareKey.PendingSsid, "");
				if (pendingSsid.equals(curSsid)) {
					Intent intent = new Intent();
					intent.setClass(getBaseContext(), WelcomeActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_FLAG_REENTER, true);
					startActivity(intent);
					
					XPGConnectClient.RemoveActivity(ManualConfStep2Activity.this);
					finish();
				}
			}
			
			@Override
			public void onFinish() {
				
			}
			
		}.start();
	}
	
	
	private class EasyLinkTimeoutCounter extends CountDownTimer {
		
		public EasyLinkTimeoutCounter(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			dialog_easylink.dismiss();
			// fail
			startActivity(new Intent(getBaseContext(), ManualConfigFailActivity.class));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			((TextView)dialog_easylink.findViewById(R.id.easylink_time_tv)).setText(millisUntilFinished / 1000 + "");
		}
	}
	 
	
}
