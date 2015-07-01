package com.vanward.ehheater.activity.user;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

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

	private EditText et_phone, et_new_passcode, et_confirm_psw, et_captcha;

	private Button btn_acquire_captcha, btn_confirm;

	private CheckBox cb_show_psw;
	
	private Dialog changePswSuccessdialog;
	
	private Handler timeoutHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			DialogUtil.dismissDialog();
			Toast.makeText(getBaseContext(), "请求超时，请重试！",
					Toast.LENGTH_SHORT).show();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_find_passcode);
		setTopText(R.string.forget_pwd);
		setRightButton(View.INVISIBLE);
		setLeftButtonBackground(R.drawable.icon_back);

		et_phone = (EditText) findViewById(R.id.et_phone);
		et_new_passcode = (EditText) findViewById(R.id.et_new_passcode);
		et_confirm_psw = (EditText) findViewById(R.id.et_confirm_psw);
		et_captcha = (EditText) findViewById(R.id.et_captcha);
		btn_acquire_captcha = (Button) findViewById(R.id.btn_acquire_captcha);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		cb_show_psw = (CheckBox) findViewById(R.id.cb_show_psw);
		
		final Dialog dialog = new Dialog(this, R.style.custom_dialog);
		dialog.setContentView(R.layout.dialog_find_passcode_success);

		changePswSuccessdialog = dialog;
		
		changePswSuccessdialog.show();
		
		findViewById(R.id.llt_root).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

		findViewById(R.id.llt_root).setOnFocusChangeListener(
				new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View arg0, boolean hasFocus) {
						if (hasFocus) {
							((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
									.hideSoftInputFromWindow(
											FindPasswordActivity.this
													.getCurrentFocus()
													.getWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}
				});

		cb_show_psw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					et_new_passcode.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					et_confirm_psw.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					et_new_passcode.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					et_confirm_psw.setInputType(InputType.TYPE_CLASS_TEXT
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

				boolean phoneNotEmpty = !TextUtils.isEmpty(et_phone.getText()
						.toString());

				if (!phoneNotEmpty) {
					Toast.makeText(getBaseContext(), "请输入手机号码", 1000).show();
					return;
				}
				if (et_phone.getText().toString().length() != 11) {
					Toast.makeText(getBaseContext(), "请输入11位手机号码", 1000).show();
					return;
				}

				btn_acquire_captcha.setEnabled(false);

				new CountDownTimer(60000, 1000) {

					@Override
					public void onTick(long millisUntilFinished) {
						String acquire_again = getResources().getString(
								R.string.acquire_again);
						btn_acquire_captcha.setText(acquire_again + "("
								+ millisUntilFinished / 1000 + ")");
					}

					@Override
					public void onFinish() {
						btn_acquire_captcha.setEnabled(true);
						btn_acquire_captcha.setText(R.string.acquire_captcha);
					}
				}.start();

				L.e(this, "XPGConnectClient.xpgc4GetMobileAuthCode()前");
				XPGConnectClient.xpgc4GetMobileAuthCode(Consts.VANWARD_APP_ID,
						et_phone.getText().toString());
				L.e(this, "XPGConnectClient.xpgc4GetMobileAuthCode()后");

				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(FindPasswordActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
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
						Toast.makeText(getBaseContext(), "请输入验证码！", 1000).show();
						return;
					}
					DialogUtil.instance().showLoadingDialog(
							FindPasswordActivity.this, "正在修改密码,请稍后...");

					
					timeoutHandler.sendEmptyMessageDelayed(0, 9000);

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
		boolean confirmPswNotEmpty = !TextUtils.isEmpty(et_confirm_psw
				.getText().toString());
		boolean pswMatch = et_new_passcode.getText().toString()
				.equals(et_confirm_psw.getText().toString());

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

		if (!confirmPswNotEmpty) {
			Toast.makeText(getBaseContext(), R.string.please_input_confirm_psw,
					1000).show();
			return false;
		}

		if (!pswMatch) {
			Toast.makeText(getBaseContext(), R.string.new_pwd_error, 1000)
					.show();
			return false;
		}

		return true;
	}

	@Override
	public void onV4RecoverPwdByPhone(int errorCode) {
		L.e(this, "onV4RecoverPwdByPhone()");

		super.onV4RecoverPwdByPhone(errorCode);
		
		timeoutHandler.removeMessages(0);
		
		DialogUtil.dismissDialog();
		if (errorCode == 0) {
			Intent intent = new Intent();
			intent.putExtra("account", et_phone.getText().toString());
			setResult(RESULT_OK, intent);
			changePswSuccessdialog.show();
			
			new CountDownTimer(4000, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					((TextView) changePswSuccessdialog
							.findViewById(R.id.tv_countdown_time))
							.setText(millisUntilFinished / 1000 + "");
				}

				@Override
				public void onFinish() {
					changePswSuccessdialog.dismiss();
					finish();
				}
			}.start();
		} else {
			Toast.makeText(getBaseContext(),
					GizwitsErrorMsg.getEqual(errorCode).getCHNDescript(), 3000)
					.show();
			btn_acquire_captcha.setEnabled(true);
			et_captcha.setText("");
		}
	}
}
