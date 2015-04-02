package com.vanward.ehheater.activity.more;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.GizwitsErrorMsg;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.UserPwdChangeResp_t;
import com.xtremeprog.xpgconnect.generated.XPG_RESULT;
import com.xtremeprog.xpgconnect.generated.generated;

public class ChangePasswordActivity extends EhHeaterBaseActivity {
	private static final String TAG = "ChangePasswordActivity";

	private CheckBox cb_show_pwd;
	private Button btn_confirm;
	private List<EditText> editList;

	private int tempConnId = -2;
	private String newPsw;

	private EditText et_password, et_new_password, et_new_password_confirm;

	private Dialog changePswSuccessdialog;
	
	private boolean isFailure;

	@Override
	public void initUI() {
		super.initUI();
		setCenterView(R.layout.activity_change_password);
		findViewById();
		setListener();
		init();
	};

	private void findViewById() {
		cb_show_pwd = (CheckBox) findViewById(R.id.cb_show_pwd);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		et_password = (EditText) findViewById(R.id.password);
		et_new_password = (EditText) findViewById(R.id.new_password);
		et_new_password_confirm = (EditText) findViewById(R.id.new_password_confirm);
	}

	private void setListener() {
		cb_show_pwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				for (EditText editLists : editList) {
					editLists
							.setInputType(isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
									: InputType.TYPE_CLASS_TEXT
											| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					editLists.setSelection(editLists.getText().length());
				}
			}
		});

		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean isNull = false;

				// for (EditText et : editList) {
				// if (et.getText().length() < 6) {
				// isNull = true;
				// Toast.makeText(getBaseContext(), R.string.not_null,
				// Toast.LENGTH_SHORT).show();
				// break;
				// }
				// }

				// 请输入旧密码！
				if ("".equals(et_password.getText().toString())) {
					Toast.makeText(getBaseContext(),
							R.string.please_input_old_psw, Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (6 > et_password.getText().length()
						|| 18 < et_password.getText().length()) {
					Toast.makeText(getBaseContext(), R.string.psw_6_to_18,
							Toast.LENGTH_SHORT).show();
					return;
				}

				// 原密码不正确，请重新输入！
				if (!AccountService.getUserPsw(getBaseContext()).equals(
						editList.get(0).getEditableText().toString())) {

					Toast.makeText(getBaseContext(), R.string.old_pwd_error,
							Toast.LENGTH_SHORT).show();

					return;
				}

				// 请输入新密码！
				if ("".equals(et_new_password.getText().toString())) {
					Toast.makeText(getBaseContext(),
							R.string.please_input_new_psw, Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (6 > et_new_password.getText().length()
						|| 18 < et_new_password.getText().length()) {
					Toast.makeText(getBaseContext(), R.string.psw_6_to_18,
							Toast.LENGTH_SHORT).show();
					return;
				}

				// 请输入确认密码！
				if ("".equals(et_new_password_confirm.getText().toString())) {
					Toast.makeText(getBaseContext(),
							R.string.please_input_confirm_psw,
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (6 > et_new_password_confirm.getText().length()
						|| 18 < et_new_password_confirm.getText().length()) {
					Toast.makeText(getBaseContext(), R.string.psw_6_to_18,
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (!editList.get(1).getEditableText().toString()
						.equals(editList.get(2).getEditableText().toString())) {

					Toast.makeText(getBaseContext(), R.string.new_pwd_error,
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					newPsw = editList.get(1).getEditableText().toString();
					requestChangePsw();
				}

			}
		});
	}

	private void init() {
		setTopText(R.string.change_password);
		setRightButton(View.GONE);
		editList = new ArrayList<EditText>();
		editList.add((EditText) findViewById(R.id.password));
		editList.add((EditText) findViewById(R.id.new_password));
		editList.add((EditText) findViewById(R.id.new_password_confirm));
		setLeftButtonBackground(R.drawable.icon_back);

		final Dialog dialog = new Dialog(this, R.style.custom_dialog);
		dialog.setContentView(R.layout.dialog_common_one_button);

		TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
		tv_content.setText(R.string.change_success_relogin);

		Button btn_close = (Button) dialog.findViewById(R.id.btn_close);
		btn_close.setText(R.string.ok);
		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changePswSuccessdialog.dismiss();
				setResult(RESULT_OK);
				finish();
			}
		});

		changePswSuccessdialog = dialog;
	}

	private void requestChangePsw() {
		DialogUtil.instance().showLoadingDialog(this, "");
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (!isFailure) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							DialogUtil.dismissDialog();
							Toast.makeText(getBaseContext(), "修改失败", 3000)
									.show();
						}
					});
				}
			}
		}, 5000);
		// XPGConnectClient.xpgcLogin2Wan(
		// AccountService.getUserId(getBaseContext()),
		// AccountService.getUserPsw(getBaseContext()), "", "");
//		XPGConnShortCuts.connect2big();

		if ("".equals(Global.token) || "".equals(Global.uid)) {
			XPGConnectClient.xpgc4Login(Consts.VANWARD_APP_ID,
					AccountService.getUserId(getBaseContext()),
					AccountService.getUserPsw(getBaseContext()));
		} else {
			L.e(this, "uid : " + Global.uid);
			L.e(this, "token : " + Global.token);
			XPGConnectClient.xpgc4ChangeUserPwd(Consts.VANWARD_APP_ID,
					Global.token, et_password.getText().toString(),
					et_new_password.getText().toString());
		}

	}  

	@Override
	public void onV4Login(int errorCode, String uid, String token,
			String expire_at) {
		L.e(this, "onV4Login()");
		L.e(this, "errorCode : " + errorCode);
		if (errorCode == 0) { 
			Global.uid = uid;
			Global.token = token;
			
			L.e(this, "uid : " + Global.uid);
			L.e(this, "token : " + Global.token);
			L.e(this, "app_id : " + Consts.VANWARD_APP_ID);
			L.e(this, "old psw : " + et_password.getText().toString());
			L.e(this, "new psw : " + et_new_password.getText().toString());

			XPGConnectClient.xpgc4ChangeUserPwd(Consts.VANWARD_APP_ID,
					Global.token, et_password.getText().toString(),
					et_new_password.getText().toString());
		}
	}

	@Override
	public void onWanLoginResp(int result, int connId) {
		super.onWanLoginResp(result, connId);
		tempConnId = connId;
		generated.SendUserPwdChangeReq(tempConnId, generated
				.String2XpgData(AccountService.getUserId(getBaseContext())),
				generated.String2XpgData(newPsw));
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		L.e(this, "onConnectEvent@ChangePswd: " + connId + "-" + event);

		if (event == XPG_RESULT.ERROR_NONE.swigValue()) {
			// 连接成功
			tempConnId = connId;
			XPGConnectClient.xpgcLogin(tempConnId,
					AccountService.getUserId(getBaseContext()),
					AccountService.getUserPsw(getBaseContext())); // login to
																	// server
		}
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
		L.e(this, "onLoginCloudResp@ChangePswd: " + result);

		generated.SendUserPwdChangeReq(tempConnId, generated
				.String2XpgData(AccountService.getUserId(getBaseContext())),
				generated.String2XpgData(newPsw));
	}
	
	@Override
	public void onV4ChangeUserPwd(int errorCode, String updatedAt) {
		super.onV4ChangeUserPwd(errorCode, updatedAt);
		L.e(this, "onV4ChangeUserPwd()");
		DialogUtil.dismissDialog();
		if (errorCode == 0) {
			isFailure = true;
			AccountService.setUser(getBaseContext(),
					AccountService.getUserId(getBaseContext()), newPsw);
			changePswSuccessdialog.show();
		} else {
			Toast.makeText(getBaseContext(),
					GizwitsErrorMsg.getEqual(errorCode).getCHNDescript(), 3000)
					.show();
		}
	}
	
	@Override
	public void OnUserPwdChangeResp(UserPwdChangeResp_t pResp, int nConnId) {
		super.OnUserPwdChangeResp(pResp, nConnId);
		L.e(this, "OnUserPwdChangeResp@ChangePswd: " + pResp.getResult());
		DialogUtil.dismissDialog();

		if (pResp.getResult() == 0) {
			AccountService.setUser(getBaseContext(),
					AccountService.getUserId(getBaseContext()), newPsw);
			changePswSuccessdialog.show();
		} else {
			Toast.makeText(getBaseContext(), R.string.failure,
					Toast.LENGTH_SHORT).show();
		}
	}
}
