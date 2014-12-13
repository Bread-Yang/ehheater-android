package com.vanward.ehheater.activity.more;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.WelcomeActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.UIUtil;
import com.vanward.ehheater.view.LogoutUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.xtremeprog.xpgconnect.XPGConnectClient;

public class AccountManagementActivity extends EhHeaterBaseActivity implements
		OnClickListener {

	private TextView tv_account, tv_nickname;
	private RelativeLayout rlt_change_nickname, rlt_change_password,
			rlt_change_bind_phone;
	private Button btn_logout;

	@Override
	public void initUI() {
		super.initUI();
		setCenterView(R.layout.activity_account_management);
		findViewById();
		init();
	}

	private void findViewById() {
		tv_account = (TextView) findViewById(R.id.tv_account);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		rlt_change_nickname = (RelativeLayout) findViewById(R.id.rlt_change_nickname);
		rlt_change_password = (RelativeLayout) findViewById(R.id.rlt_change_password);
		rlt_change_bind_phone = (RelativeLayout) findViewById(R.id.rlt_change_bind_phone);
		btn_logout = (Button) findViewById(R.id.btn_logout);

		UIUtil.setOnClick(this, rlt_change_nickname, rlt_change_password,
				btn_logout);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

		if (view == rlt_change_nickname) {
			intent.setClass(getBaseContext(), ChangeNicknameActivity.class);
			intent.putExtra("nickname", tv_nickname.getText().toString());
			startActivityForResult(intent, 0);
		}

		if (view == rlt_change_password) {
			intent.setClass(getBaseContext(), ChangePasswordActivity2.class);
			startActivityForResult(intent, 1);
		}

		if (view == btn_logout) {
			LogoutUtil.instance(AccountManagementActivity.this)
					.nextButtonCall(new NextButtonCall() {
						@Override
						public void oncall(View v) {
							LogoutUtil.instance(AccountManagementActivity.this)
									.dissmiss();
							logout();
						}
					}).showDialog();

		}
	}

	private void logout() {
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				new Intent(Consts.INTENT_ACTION_LOGOUT));
		
		if (Global.connectId > -1) {
			XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
			Global.connectId = -1;
		}
		
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				new Intent(Consts.INTENT_FILTER_KILL_MAIN_ACTIVITY));

		new SharedPreferUtils(getBaseContext()).clear();
		new HeaterInfoService(getBaseContext()).deleteAllHeaters();
		AccountService.setUser(this, null, null);

		startActivity(new Intent(getBaseContext(), WelcomeActivity.class));
		finish();
	}

	private void init() {
		setTopText(R.string.account_management);
		setRightButton(View.GONE);
		setLeftButtonBackground(R.drawable.icon_back);

		String nickName = new SharedPreferUtils(getBaseContext()).get(
				ShareKey.UserNickname, "");
		tv_nickname.setText(nickName);

		tv_account.setText(AccountService.getUserId(getBaseContext()));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK && data != null) {
				String newNickName = data.getStringExtra("newNickName");
				if (newNickName != null) {
					tv_nickname.setText(newNickName);
				}
			}
			break;
		case 1:
			if (resultCode == RESULT_OK) {
				logout();
			}
			break;

		}
	}

}
