package com.vanward.ehheater.activity.more;

import net.tsz.afinal.http.AjaxCallBack;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.util.HttpFriend;

public class ChangeNicknameActivity extends EhHeaterBaseActivity {

	private EditText et_change_nickname;

	private HttpFriend mHttpFriend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_change_nickname);
		setTopText(R.string.account);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButtonBackground(R.drawable.menu_icon_ye);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		et_change_nickname = (EditText) findViewById(R.id.et_change_nickname);
	}

	private void setListener() {
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String requestURL = "";

				showRequestDialog();
				mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL)
						.executePost(new AjaxCallBack<String>() {

							@Override
							public void onSuccess(String t) {
								super.onSuccess(t);

								dismissRequestDialog();
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
