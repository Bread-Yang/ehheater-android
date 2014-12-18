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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.UserPwdChangeResp_t;
import com.xtremeprog.xpgconnect.generated.XPG_RESULT;
import com.xtremeprog.xpgconnect.generated.generated;

public class ChangePasswordActivity2 extends EhHeaterBaseActivity {

	private CheckBox cb_show_pwd;
	private Button btn_confirm;
	private List<EditText> editList;

	private int tempConnId = -2;
	private String newPsw;

	private EditText et_password, et_new_password, et_new_password_confirm;

	private Dialog changePswSuccessdialog;

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
					// DialogUtil.instance().showLoadingDialog(ChangePasswordActivity2.this,
					// "");
					// XpgDataField uid =
					// generated.String2XpgData(AccountService.getUserId(getBaseContext()));
					// XpgDataField upd =
					// generated.String2XpgData(editList.get(1).getEditableText().toString());
					// generated.SendUserPwdChangeReq(connId, uid ,
					// upd);
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

		changePswSuccessdialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithOneButton(R.string.change_success_relogin,
						R.string.ok, new OnClickListener() {

							@Override
							public void onClick(View v) {
								changePswSuccessdialog.dismiss();
								setResult(RESULT_OK);
								finish();
							}
						});
	}

	private void requestChangePsw() {
		DialogUtil.instance().showLoadingDialog(this, "");
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (/* 失败 */tempConnId == -2) {
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
		XPGConnShortCuts.connect2big();
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		Log.d("emmm", "onConnectEvent@ChangePswd: " + connId + "-" + event);

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
		Log.d("emmm", "onLoginCloudResp@ChangePswd: " + result);

		generated.SendUserPwdChangeReq(tempConnId, generated
				.String2XpgData(AccountService.getUserId(getBaseContext())),
				generated.String2XpgData(newPsw));
	}

	@Override
	public void OnUserPwdChangeResp(UserPwdChangeResp_t pResp, int nConnId) {
		super.OnUserPwdChangeResp(pResp, nConnId);
		Log.d("emmm", "OnUserPwdChangeResp@ChangePswd: " + pResp.getResult());
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
