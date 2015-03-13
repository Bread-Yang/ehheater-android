package com.vanward.ehheater.activity.user;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.GizwitsErrorMsg;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.xtremeprog.xpgconnect.XPGConnectClient;

public class FindPasswordActivity extends EhHeaterBaseActivity implements
		OnClickListener {

	private EditText et_phone, et_new_passcode, et_captcha;

	private Button btn_acquire_captcha, btn_confirm;

	private CheckBox cb_show_psw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_find_passcode);
		setRightButton(View.INVISIBLE);
		setLeftButtonBackground(R.drawable.icon_back);

		et_phone = (EditText) findViewById(R.id.et_phone);
		et_new_passcode = (EditText) findViewById(R.id.et_new_passcode);
		et_captcha = (EditText) findViewById(R.id.et_captcha);
		btn_acquire_captcha = (Button) findViewById(R.id.btn_acquire_captcha);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		cb_show_psw = (CheckBox) findViewById(R.id.cb_show_psw);

		cb_show_psw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					et_new_passcode.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					et_new_passcode.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		btn_acquire_captcha.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!NetworkStatusUtil.isConnected(FindPasswordActivity.this)) {
					Toast.makeText(getBaseContext(), R.string.check_network,
							500).show();
					return;
				}
				if (isInputValid()) {
					XPGConnectClient.xpgc4GetMobileAuthCode(
							Consts.VANWARD_APP_ID, et_phone.getText()
									.toString());
					btn_acquire_captcha.setEnabled(false);

					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(FindPasswordActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});

		btn_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!NetworkStatusUtil.isConnected(FindPasswordActivity.this)) {
					Toast.makeText(getBaseContext(), R.string.check_network,
							500).show();
					return;
				}

				if (isInputValid()) {
					if (TextUtils.isEmpty(et_captcha.getText().toString())) {
						Toast.makeText(getBaseContext(), "请输入验证码", 1000).show();
						return;
					}
					DialogUtil.instance().showLoadingDialog(
							FindPasswordActivity.this, "正在修改密码,请稍后...");

					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							DialogUtil.dismissDialog();
						}
					}, 9000);

					XPGConnectClient.xpgc4RecoverPwdByPhone(
							Consts.VANWARD_APP_ID, et_phone.getText()
									.toString(), et_captcha.getText()
									.toString(), et_new_passcode.getText()
									.toString());
				}
			}
		});
	}

	private boolean isInputValid() {

		boolean phoneNotEmpty = !TextUtils.isEmpty(et_phone.getText()
				.toString());
		boolean newPswNotEmpty = !TextUtils.isEmpty(et_new_passcode.getText()
				.toString());
		boolean lengthGt6 = et_new_passcode.getText().toString().length() >= 6;
		boolean lengthGt18 = et_new_passcode.getText().toString().length() <= 18;

		if (!phoneNotEmpty) {
			Toast.makeText(getBaseContext(), "请输入手机号码", 1000).show();
			return false;
		}
		if (et_phone.getText().toString().length() != 11) {
			Toast.makeText(getBaseContext(), "请输入11位手机号码", 1000).show();
			return false;
		}

		if (!newPswNotEmpty) {
			Toast.makeText(getBaseContext(), R.string.please_input_new_psw,
					1000).show();
			return false;
		}

		if (!lengthGt6 || !lengthGt18) {
			Toast.makeText(getBaseContext(), R.string.psw_6_to_18, 1000).show();
			return false;
		}

		return true;
	}

	@Override
	public void onV4RecoverPwdByPhone(int errorCode) {
		L.e(this, "onV4RecoverPwdByPhone()");

		super.onV4RecoverPwdByPhone(errorCode);

		DialogUtil.dismissDialog();
		if (errorCode == 0) {
			Toast.makeText(getBaseContext(), "修改密码成功", 3000).show();
			Intent intent = new Intent();
			intent.putExtra("account", et_phone.getText().toString());
			setResult(RESULT_OK, intent);
			finish();
		} else {
			Toast.makeText(getBaseContext(),
					GizwitsErrorMsg.getEqual(errorCode).getCHNDescript(), 3000)
					.show();
			btn_acquire_captcha.setEnabled(true);
			et_captcha.setText("");
		}
	}
}
