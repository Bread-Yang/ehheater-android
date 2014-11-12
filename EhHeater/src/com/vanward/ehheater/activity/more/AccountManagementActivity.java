package com.vanward.ehheater.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.CloudBaseActivity;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.WelcomeActivity;
import com.vanward.ehheater.activity.login.LoginActivity;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.UIUtil;
import com.vanward.ehheater.view.LogoutUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class AccountManagementActivity extends EhHeaterBaseActivity implements
		OnClickListener {

	private TextView tv_account;
	private RelativeLayout rlt_change_password;
	private RelativeLayout rlt_change_bind_phone;
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
		rlt_change_password = (RelativeLayout) findViewById(R.id.rlt_change_password);
		rlt_change_bind_phone = (RelativeLayout) findViewById(R.id.rlt_change_bind_phone);
		btn_logout = (Button) findViewById(R.id.btn_logout);

		UIUtil.setOnClick(this, rlt_change_password, btn_logout);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view == rlt_change_password) {
			intent.setClass(getBaseContext(), ChangePasswordActivity2.class);
			startActivity(intent);
		}

		if (view == btn_logout) {
			LogoutUtil.instance(AccountManagementActivity.this)
					.nextButtonCall(new NextButtonCall() {
						@Override
						public void oncall(View v) {
							new SharedPreferUtils(getBaseContext()).clear();
							new HeaterInfoService(getBaseContext())
									.deleteAllHeaters();
							intent.setClass(getBaseContext(),
									LoginActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}).showDialog();

		}
	
	}

	private void init() {
		setTopText(R.string.account_management);
		setRightButton(View.GONE);
		setLeftButtonBackground(R.drawable.icon_back);

		tv_account.setText(AccountService.getUserId(getBaseContext()));
	}

}
