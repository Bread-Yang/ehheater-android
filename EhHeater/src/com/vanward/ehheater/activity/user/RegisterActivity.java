package com.vanward.ehheater.activity.user;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.info.SelectDeviceActivity;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.GizwitsErrorMsg;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.UserRegisterResp_t;

public class RegisterActivity extends EhHeaterBaseActivity {

	private EditText et_nickname, et_phone, et_psw, et_psw2, et_captcha;
	private Button mBtnConfirm, btn_acquire_captcha;
	private CheckBox mCbShowPsw;
	private HttpFriend mHttpFriend;

	@Override
	public void initUI() {
		super.initUI();
		setCenterView(R.layout.activity_register);
		setTopText(R.string.register);
		setRightButton(View.INVISIBLE);
		setLeftButtonBackground(R.drawable.icon_back);

		et_nickname = (EditText) findViewById(R.id.et_nickname);
		et_captcha = (EditText) findViewById(R.id.et_captcha);
		et_phone = (EditText) findViewById(R.id.ar_et_phone);
		et_psw = (EditText) findViewById(R.id.ar_et_psw);
		et_psw2 = (EditText) findViewById(R.id.ar_et_confirm_psw);
		mBtnConfirm = (Button) findViewById(R.id.ar_btn_confirm);
		btn_acquire_captcha = (Button) findViewById(R.id.btn_acquire_captcha);
		mCbShowPsw = (CheckBox) findViewById(R.id.ar_chkbx_showpsw);

		btn_acquire_captcha.setOnClickListener(this);
		et_phone.setOnClickListener(this);
		et_psw.setOnClickListener(this);
		et_psw2.setOnClickListener(this);
		mBtnConfirm.setOnClickListener(this);

		findViewById(R.id.llt_root).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

		findViewById(R.id.llt_root).setOnFocusChangeListener(
				new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View view, boolean hasFocus) {
						if (hasFocus) {
							((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
									.hideSoftInputFromWindow(
											RegisterActivity.this
													.getCurrentFocus()
													.getWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);
						} else {
						}
					}
				});

		mCbShowPsw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					et_psw.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					et_psw2.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					et_psw.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					et_psw2.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		mHttpFriend = HttpFriend.create(this);
	}

	// private void setListener() {
	// et_nickname.setOnFocusChangeListener(new OnFocusChangeListener() {
	//
	// @Override
	// public void onFocusChange(View view, boolean hasFocus) {
	// if (!hasFocus) {
	//
	// }
	// }
	// });
	// }

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.btn_acquire_captcha:
			if (!NetworkStatusUtil.isConnected(this)) {
				Toast.makeText(getBaseContext(), R.string.check_network, 500)
						.show();
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

			XPGConnectClient.xpgc4GetMobileAuthCode(Consts.VANWARD_APP_ID,
					et_phone.getText().toString());

			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(RegisterActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		case R.id.ar_btn_confirm:
			if (!NetworkStatusUtil.isConnected(this)) {
				Toast.makeText(getBaseContext(), R.string.check_network, 500)
						.show();
				return;
			}
			if (isInputValid()) {
				if (TextUtils.isEmpty(et_captcha.getText().toString())) {
					Toast.makeText(getBaseContext(), "请输入验证码！", 1000).show();
					return;
				}
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
						// XPGConnectClient.xpgcRegister(mEtPhone.getText()
						// .toString(), mEtPsw.getText().toString());
						XPGConnectClient.xpgc4CreateUserByPhone(
								Consts.VANWARD_APP_ID, et_phone.getText()
										.toString(), et_psw.getText()
										.toString(), et_captcha.getText()
										.toString());
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
	public void onV4CreateUserByPhone(int errorCode, String uid, String token,
			String expire_at) {
		L.e(this, "onV4CreateUserByPhone()");
		super.onV4CreateUserByPhone(errorCode, uid, token, expire_at);
		if (errorCode == 0) {

			Global.uid = uid;
			Global.token = token;

			Toast.makeText(getBaseContext(), "注册成功", 1000).show();

			AccountService.setPendingUser(getBaseContext(), et_phone.getText()
					.toString(), et_psw.getText().toString());
			AccountService.setUser(getBaseContext(), et_phone.getText()
					.toString(), et_psw.getText().toString());

			String requestURL = "userinfo/saveMemberInfo";

			AjaxParams params = new AjaxParams();
			params.put("uid", et_phone.getText().toString());
			params.put("userName", et_nickname.getText().toString());

//			Set<String> tagSet = new LinkedHashSet<String>();
//			tagSet.add(et_phone.getText().toString().trim());
//			JPushInterface.setTags(getApplicationContext(), tagSet,
//					mAliasCallback);

			mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					params, new AjaxCallBack<String>() {
						public void onSuccess(String jsonString) {
							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									startActivity(new Intent(getBaseContext(),
											SelectDeviceActivity.class));
									new SharedPreferUtils(getBaseContext())
											.put(ShareKey.UserNickname,
													et_nickname.getText()
															.toString().trim());
									finish();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

							DialogUtil.dismissDialog();
						}

						;

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Log.e("注册账号的时候请求昵称失败", "注册账号的时候请求昵称失败");
							startActivity(new Intent(getBaseContext(),
									SelectDeviceActivity.class));
							new SharedPreferUtils(getBaseContext()).put(
									ShareKey.UserNickname, "");
							finish();
							DialogUtil.dismissDialog();
						}
					});

		} else {
			DialogUtil.dismissDialog();
			Toast.makeText(getBaseContext(),
					GizwitsErrorMsg.getEqual(errorCode).getCHNDescript(), 3000)
					.show();
			btn_acquire_captcha.setEnabled(true);
			et_captcha.setText("");
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void OnUserRegisterResp(UserRegisterResp_t pResp, int nConnId) {
		super.OnUserRegisterResp(pResp, nConnId);
		L.e(this, "OnUserRegisterResp()");

		if (pResp.getResult() == 0) {
			Toast.makeText(getBaseContext(), "注册成功", 1000).show();
			AccountService.setPendingUser(getBaseContext(), et_phone.getText()
					.toString(), et_psw.getText().toString());
			AccountService.setUser(getBaseContext(), et_phone.getText()
					.toString(), et_psw.getText().toString());

			String requestURL = "userinfo/saveMemberInfo";

			AjaxParams params = new AjaxParams();
			params.put("uid", et_phone.getText().toString());
			params.put("userName", et_nickname.getText().toString());

			mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					params, new AjaxCallBack<String>() {
						public void onSuccess(String jsonString) {
							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									startActivity(new Intent(getBaseContext(),
											SelectDeviceActivity.class));
									new SharedPreferUtils(getBaseContext())
											.put(ShareKey.UserNickname,
													et_nickname.getText()
															.toString().trim());
									finish();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

							DialogUtil.dismissDialog();
						}

						;

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Log.e("注册账号的时候请求昵称失败", "注册账号的时候请求昵称失败");
							startActivity(new Intent(getBaseContext(),
									SelectDeviceActivity.class));
							new SharedPreferUtils(getBaseContext()).put(
									ShareKey.UserNickname, "");
							finish();
							DialogUtil.dismissDialog();
						}
					});
		} else {
			if (!isChangingConfigurations()) {

			}
			Toast.makeText(getBaseContext(), "该号码已注册", 1000).show();
			DialogUtil.dismissDialog();
		}

	}

	private boolean isInputValid() {

		boolean nicknameNotEmpty = !TextUtils.isEmpty(et_nickname.getText()
				.toString());
		boolean phoneNotEmpty = !TextUtils.isEmpty(et_phone.getText()
				.toString());
		boolean pswNotEmpty = !TextUtils.isEmpty(et_psw.getText().toString());
		boolean confirmPswNotEmpty = !TextUtils.isEmpty(et_psw2.getText()
				.toString());
		boolean lengthGt6 = et_psw.getText().toString().length() >= 6;
		boolean lengthGt18 = et_psw.getText().toString().length() <= 18;
		boolean pswMatch = et_psw.getText().toString()
				.equals(et_psw2.getText().toString());

		if (!nicknameNotEmpty) {
			Toast.makeText(getBaseContext(), "请输入用户名", 1000).show();
			return false;
		}
		if (!phoneNotEmpty) {
			Toast.makeText(getBaseContext(), "请输入手机号码", 1000).show();
			return false;
		}
		if (et_phone.getText().toString().length() != 11) {
			Toast.makeText(getBaseContext(), "请输入11位手机号码", 1000).show();
			return false;
		}
		if (!pswNotEmpty) {
			Toast.makeText(getBaseContext(), "请输入密码", 1000).show();
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

		// if (6 > mEtPsw2.getText().length() || 18 <
		// mEtPsw2.getText().length()) {
		// Toast.makeText(getBaseContext(), R.string.psw_6_to_18,
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }

		if (!pswMatch) {
			Toast.makeText(getBaseContext(), R.string.new_pwd_error, 1000)
					.show();
			return false;
		}

		return lengthGt6 && pswMatch;
	}

	private static final int MSG_SET_ALIAS = 1001;

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				// mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS,
				// alias), 1000 * 60);
				break;

			default:
				logs = "Failed with errorCode = " + code;
			}
		}

		private final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_SET_ALIAS:
					JPushInterface.setAliasAndTags(getApplicationContext(),
							(String) msg.obj, null, mAliasCallback);
					break;
				}
			}
		};
	};
}
