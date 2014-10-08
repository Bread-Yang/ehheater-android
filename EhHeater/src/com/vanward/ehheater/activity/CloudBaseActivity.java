package com.vanward.ehheater.activity;

import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.XPG_RESULT;

/**
 * 自动连接大循环 activity 基类
 * 
 * @author ads
 * 
 */
public class CloudBaseActivity extends EhHeaterBaseActivity {

	private int connId = -2;

	static {
		XPGConnShortCuts.connect2big();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);

		if (event == XPG_RESULT.ERROR_NONE.swigValue()) {
			this.connId = connId;
			XPGConnectClient.xpgcLogin(connId,
					AccountService.getUserId(getBaseContext()),
					AccountService.getUserPsw(getBaseContext())); // login to
																	// server
		}
	}

	public int getConnId() {
		return connId;
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
