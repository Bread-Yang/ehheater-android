package com.vanward.ehheater.activity.more;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.login.LoginActivity;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;

public class ChangeNicknameActivity extends EhHeaterBaseActivity {

	private EditText et_change_nickname;

	private HttpFriend mHttpFriend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_change_nickname);
		setTopText(R.string.edit_nickname);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButtonBackground(R.drawable.menu_icon_ye);
		findViewById();
		setListener();
		init();

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
											ChangeNicknameActivity.this
													.getCurrentFocus()
													.getWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}
				});

	}

	private void findViewById() {
		et_change_nickname = (EditText) findViewById(R.id.et_change_nickname);
	}

	private void setListener() {
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String requestURL = "userinfo/saveMemberInfo";

				final String nickName = et_change_nickname.getText().toString()
						.trim();

				if (TextUtils.isEmpty(nickName)) {
					Toast.makeText(getBaseContext(), "请输入用户名", 1000).show();
					return;
				}

				AjaxParams params = new AjaxParams();
				params.put("uid", AccountService.getUserId(getBaseContext()));
				params.put("userName", nickName);

				mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL)
						.executeGet(params, new AjaxCallBack<String>() {

							@Override
							public void onSuccess(String jsonString) {
								super.onSuccess(jsonString);

								try {
									JSONObject json = new JSONObject(jsonString);
									String responseCode = json
											.getString("responseCode");
									if ("200".equals(responseCode)) {
										new SharedPreferUtils(getBaseContext())
												.put(ShareKey.UserNickname,
														nickName);
										Intent intent = new Intent();
										intent.putExtra("newNickName", nickName);
										setResult(RESULT_OK, intent);
										finish();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
			}
		});
	}

	private void init() {
		mHttpFriend = HttpFriend.create(this);

		String nickName = getIntent().getStringExtra("nickname");
		if (nickName != null) {
			et_change_nickname.setText(nickName);
		}
	}

}
