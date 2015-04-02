package com.vanward.ehheater.activity.configure;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.PxUtil;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.BindingSetResp_t;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;
import com.xtremeprog.xpgconnect.generated.XPG_RESULT;
import com.xtremeprog.xpgconnect.generated.generated;

public class DummySendBindingReqActivity extends GeneratedActivity {
	
	private static final String TAG = "DummySendBindingReqActivity";
	
	int tempConnId;
	
	private String username;
	private String userpsw;
	private String did2bind;
	private String passcode2bind;
	
//	TextView mTvInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		L.e(this, "onCreate");
		
//		setContentView(initContentView());
		
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
		
//		XPGConnectClient.xpgcLogin2Wan(username, userpsw, "", "");
//		XPGConnShortCuts.connect2big();
		
		if ("".equals(Global.token) || "".equals(Global.uid)) {
			XPGConnectClient.xpgc4Login(Consts.VANWARD_APP_ID, username, userpsw);
		} else {
			XPGConnectClient.xpgc4BindDevice(Consts.VANWARD_APP_ID, Global.token, did2bind, passcode2bind, "");
		}
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				setResult(RESULT_CANCELED); 
				XPGConnectClient.RemoveActivity(DummySendBindingReqActivity.this);
				finish();
			}
		}, 5000);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		XPGConnectClient.xpgcDisconnectAsync(tempConnId);
		XPGConnectClient.RemoveActivity(this);
	}
	
//	private View initContentView() {
		
//		mTvInfo = new TextView(this);
//		mTvInfo.setGravity(Gravity.CENTER);
//		mTvInfo.setText("正在上传绑定关系...");
//		mTvInfo.setMinHeight(PxUtil.dip2px(this, 40));
//		mTvInfo.setMinWidth(PxUtil.dip2px(this, 200));
//		
//		return mTvInfo;
//	}
	
	@Override
	public void onV4Login(int errorCode, String uid, String token,
			String expire_at) {
		L.e(this, "onV4Login()");
		if (errorCode == 0) {
			Global.uid = uid;
			Global.token = token;
			
			L.e(this, "token : " + token);
			L.e(this, "did2bind : " + did2bind);
			L.e(this, "passcode2bind : " + passcode2bind);
			
			XPGConnectClient.xpgc4BindDevice(Consts.VANWARD_APP_ID, token, did2bind, passcode2bind, "");
		}
	}
	
	@Override
	public void onWanLoginResp(int result, int connId) {
		super.onWanLoginResp(result, connId);
		L.e(this, "onV4Login()");
		L.e(this, "result : " + result);
		L.e(this, "connId : " + connId);
		
		tempConnId = connId;
		
		L.e(this, "did2bind : " + did2bind);
		L.e(this, "passcode2bind : " + passcode2bind);
		
		generated.SendBindingSetReq(connId, generated.String2XpgData(did2bind), 
				generated.String2XpgData(passcode2bind));
	}
	
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
//		L.e(this, "onConnectEvent@DummySendBinding:" + connId + "-" + event);
//		
//		if (event == XPG_RESULT.ERROR_NONE.swigValue()) {
//			tempConnId = connId;
//			XPGConnectClient.xpgcLogin(tempConnId, username, userpsw);	// login to server
//		}
	};
	
	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
//		L.e(this, "onLoginCloudResp@DummySendBinding:" + result);
//		
//		generated.SendBindingSetReq(tempConnId, generated.String2XpgData(did2bind), 
//				generated.String2XpgData(passcode2bind));
//		L.e(this, "sendingBinding@DummySendBinding: " + username + "-" + did2bind + "-" + passcode2bind);
	}
	
	@Override
	public void onV4BindDevce(int errorCode, String successString,
			String failString) {
		L.e(this, "onV4BindDevce()");
		super.onV4BindDevce(errorCode, successString, failString);
		L.e(this, "errorCode : " + errorCode);
		if (errorCode == 0) {
			setResult(RESULT_OK);
		} else {
			setResult(RESULT_CANCELED);
		}
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				XPGConnectClient.RemoveActivity(DummySendBindingReqActivity.this);
				finish();
			}
		}, 300);
	}
	
	public void OnBindingSetResp(BindingSetResp_t pResp, int nConnId) {
		super.OnBindingSetResp(pResp, nConnId);
		L.e(this, "OnBindingSetResp@DummySendBinding:" + pResp.getResult());
		
		if (pResp.getResult() == 0) {
			setResult(RESULT_OK);
//			mTvInfo.setText("绑定成功");
		} else {
			setResult(RESULT_CANCELED);
//			mTvInfo.setText("绑定失败");
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
