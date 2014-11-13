package com.vanward.ehheater.activity.configure;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.util.PxUtil;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.BindingSetResp_t;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;
import com.xtremeprog.xpgconnect.generated.XPG_RESULT;
import com.xtremeprog.xpgconnect.generated.generated;

public class DummySendBindingReqActivity extends GeneratedActivity {
	
	int tempConnId;
	
	private String username;
	private String userpsw;
	private String did2bind;
	private String passcode2bind;
	
	@Override
	protected void onStop() {
		super.onStop();
		XPGConnectClient.xpgcDisconnectAsync(tempConnId);
		XPGConnectClient.RemoveActivity(this);
	}
	
	private View initContentView() {
		
		mTvInfo = new TextView(this);
		mTvInfo.setGravity(Gravity.CENTER);
		mTvInfo.setText("正在上传绑定关系...");
		mTvInfo.setMinHeight(PxUtil.dip2px(this, 40));
		mTvInfo.setMinWidth(PxUtil.dip2px(this, 200));
		
		return mTvInfo;
	}
	TextView mTvInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(initContentView());
		
		username = getIntent().getStringExtra(Consts.INTENT_EXTRA_USERNAME);
		userpsw = getIntent().getStringExtra(Consts.INTENT_EXTRA_USERPSW);
		did2bind = getIntent().getStringExtra(Consts.INTENT_EXTRA_DID2BIND);
		passcode2bind = getIntent().getStringExtra(Consts.INTENT_EXTRA_PASSCODE2BIND);
		
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(userpsw) || 
				TextUtils.isEmpty(did2bind) || TextUtils.isEmpty(passcode2bind)) {
			
			setResult(RESULT_CANCELED);
			XPGConnectClient.RemoveActivity(DummySendBindingReqActivity.this);
			finish();
			return;
		}
		
		XPGConnShortCuts.connect2big();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				setResult(RESULT_CANCELED);
				XPGConnectClient.RemoveActivity(DummySendBindingReqActivity.this);
				finish();
			}
		}, 5000);
	}
	
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		Log.d("emmm", "onConnectEvent@DummySendBinding:" + connId + "-" + event);
		
		if (event == XPG_RESULT.ERROR_NONE.swigValue()) {
			tempConnId = connId;
			XPGConnectClient.xpgcLogin(tempConnId, username, userpsw);	// login to server
		}
	};
	
	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
		Log.d("emmm", "onLoginCloudResp@DummySendBinding:" + result);
		
		generated.SendBindingSetReq(tempConnId, generated.String2XpgData(did2bind), 
				generated.String2XpgData(passcode2bind));
		Log.d("emmm", "sendingBinding@DummySendBinding: " + username + "-" + did2bind + "-" + passcode2bind);
	}
	
	public void OnBindingSetResp(BindingSetResp_t pResp, int nConnId) {
		super.OnBindingSetResp(pResp, nConnId);
		Log.d("emmm", "OnBindingSetResp@DummySendBinding:" + pResp.getResult());
		
		if (pResp.getResult() == 0) {
			setResult(RESULT_OK);
			mTvInfo.setText("绑定成功");
		} else {
			setResult(RESULT_CANCELED);
			mTvInfo.setText("绑定失败");
		}
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				XPGConnectClient.RemoveActivity(DummySendBindingReqActivity.this);
				finish();
			}
		}, 300);
		
	};
	
	
	
}
