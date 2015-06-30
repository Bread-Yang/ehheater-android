package com.vanward.ehheater.activity.more;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.L;

public class AboutActivity extends Activity {

	private Button btn_check_update;
	private Button rightbButton;
	private TextView tv_vanward_site, tv_model;
	private View leftbutton;
	private HttpFriend mHttpFriend;
	private Dialog updateTipsDialog;
	private String downloadAPKUrl = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		findViewById();
		setListener();
		init();

		HeaterInfoService hser = new HeaterInfoService(getBaseContext());

		// 当前设备是壁挂炉才显示型号文本
		if (!hser.getCurrentSelectedHeater().getProductKey()
				.equals(Consts.FURNACE_PRODUCT_KEY)) {
			tv_model.setVisibility(View.INVISIBLE);
		}
	}

	private void findViewById() {
		btn_check_update = (Button) findViewById(R.id.btn_check_update);
		leftbutton = ((Button) findViewById(R.id.ivTitleBtnLeft));
		rightbButton = ((Button) findViewById(R.id.ivTitleBtnRigh));
		tv_vanward_site = ((TextView) findViewById(R.id.tv_vanward_site));
		tv_model = ((TextView) findViewById(R.id.tv_model));
		rightbButton.setVisibility(View.GONE);
		leftbutton.setBackgroundResource(R.drawable.icon_back);
		TextView title = (TextView) findViewById(R.id.ivTitleName);
		title.setText("关于");
	}

	private void setListener() {
		leftbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		tv_vanward_site.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(getResources().getString(
						R.string.vanward_site)));
				startActivity(intent);
			}
		});

		btn_check_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					final int currentVersion = getPackageManager()
							.getPackageInfo("com.vanward.ehheater", 0).versionCode;

					String requestURL = "checkVersion";

//					mHttpFriend.toUrl("http://enaiter.xtremeprog.com/EnaiterWeb/checkVersion?versionCode=1")
					mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL)
							.executePost(null, new AjaxCallBack<String>() {
								@Override
								public void onSuccess(String jsonString) {
									JSONObject json;
									L.e(AboutActivity.this, "返回的json数据是 : "
											+ jsonString);
									try {
										json = new JSONObject(jsonString);
										String responseCode = json
												.getString("responseCode");
										L.e(AboutActivity.this,
												"responseCode : "
														+ responseCode);
										if ("200".equals(responseCode)) {
											JSONObject result = json
													.getJSONObject("result");

											int lastestVersionCode = result
													.getInt("versionCode");

											downloadAPKUrl = result
													.getString("path");
											if (lastestVersionCode > currentVersion) {
												updateTipsDialog.show();
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void init() {
		TextView tv_about = (TextView) findViewById(R.id.tv_about);
		tv_about.setText(Html.fromHtml(getString(R.string.vanward_profile)));

		mHttpFriend = HttpFriend.create(this);

		updateTipsDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.update_tips,
						BaoDialogShowUtil.DEFAULT_RESID, R.string.update,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								updateTipsDialog.dismiss();
							}
						}, new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (downloadAPKUrl != null) {
									updateTipsDialog.dismiss();
									Intent intent = new Intent(
											Intent.ACTION_VIEW);
									intent.setData(Uri.parse(downloadAPKUrl));
									startActivity(intent);
								}
							}
						});
	}

}
