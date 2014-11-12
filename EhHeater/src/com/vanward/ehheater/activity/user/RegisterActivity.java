package com.vanward.ehheater.activity.user;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.configure.ShitActivity;
import com.vanward.ehheater.activity.info.SelectDeviceActivity;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.UserRegisterResp_t;

public class RegisterActivity extends EhHeaterBaseActivity {

	private EditText mEtPhone, mEtPsw, mEtPsw2;
	private Button mBtnConfirm;
	private CheckBox mCbShowPsw;

	@Override
	public void initUI() {
		super.initUI();
		setCenterView(R.layout.activity_register);
		setTopText(R.string.register);
		setRightButton(View.INVISIBLE);
		setLeftButtonBackground(R.drawable.icon_back);

		mEtPhone = (EditText) findViewById(R.id.ar_et_phone);
		mEtPsw = (EditText) findViewById(R.id.ar_et_psw);
		mEtPsw2 = (EditText) findViewById(R.id.ar_et_confirm_psw);
		mBtnConfirm = (Button) findViewById(R.id.ar_btn_confirm);
		mCbShowPsw = (CheckBox) findViewById(R.id.ar_chkbx_showpsw);

		mEtPhone.setOnClickListener(this);
		mEtPsw.setOnClickListener(this);
		mEtPsw2.setOnClickListener(this);
		mBtnConfirm.setOnClickListener(this);

		mCbShowPsw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mEtPsw.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					mEtPsw2.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					mEtPsw.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mEtPsw2.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.ar_btn_confirm:
			if (!NetworkStatusUtil.isConnected(this)) {
				Toast.makeText(getBaseContext(), "无网络连接", 500).show();
				return;
			}
			if (isInputValid()) {
				DialogUtil.instance().showLoadingDialog(this, "正在验证，请稍后...");
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						DialogUtil.dismissDialog();
					}
				}, 9000);

				// XPGConnectClient.xpgcRegister(mEtPhone.getText().toString(),
				// mEtPsw.getText().toString());
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						// 发现这个方法会卡ui线程
						XPGConnectClient.xpgcRegister(mEtPhone.getText()
								.toString(), mEtPsw.getText().toString());
						return null;
					}
				}.execute();
			}

			break;
		case R.id.btn_left:
			onBackPressed();
			break;
		}

	}

	@Override
	public void OnUserRegisterResp(UserRegisterResp_t pResp, int nConnId) {
		super.OnUserRegisterResp(pResp, nConnId);
		Log.d("emmm", "OnUserRegisterResp:" + pResp.getResult());
		DialogUtil.dismissDialog();

		if (pResp.getResult() == 0) {
			Toast.makeText(getBaseContext(), "注册成功", 1000).show();
			AccountService.setPendingUser(getBaseContext(), mEtPhone.getText().toString(), mEtPsw.getText().toString());
			startActivity(new Intent(getBaseContext(), SelectDeviceActivity.class));
			finish();
		} else {
			if (!isChangingConfigurations()) {
				
			}
			Toast.makeText(getBaseContext(), "该号码已注册", 1000).show();
		}

	}

	private boolean isInputValid() {

		boolean phoneNotEmpty = !TextUtils.isEmpty(mEtPhone.getText()
				.toString());
		boolean lengthGt6 = mEtPsw.getText().toString().length() >= 6;
		boolean lengthGt18 = mEtPsw.getText().toString().length() <= 18;
		boolean pswMatch = mEtPsw.getText().toString()
				.equals(mEtPsw2.getText().toString());

		if (!phoneNotEmpty) {
			Toast.makeText(getBaseContext(), "请输入账号和密码", 1000).show();
			return false;
		}

		if (!lengthGt6||!lengthGt18) {
			Toast.makeText(getBaseContext(), "密码长度需为6-18位", 1000).show();
			return false;
		}

		if (!pswMatch) {
			Toast.makeText(getBaseContext(), "请输入确认密码", 1000).show();
			return false;
		}

		return lengthGt6 && pswMatch;
	}

}
