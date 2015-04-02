package com.vanward.ehheater.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class AboutActivity extends Activity {

	private Button btn_check_update;
	private Button rightbButton;
	private TextView tv_vanward_site;
	private View leftbutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		btn_check_update = (Button) findViewById(R.id.btn_check_update);
		leftbutton = ((Button) findViewById(R.id.ivTitleBtnLeft));
		rightbButton = ((Button) findViewById(R.id.ivTitleBtnRigh));
		tv_vanward_site = ((TextView) findViewById(R.id.tv_vanward_site));
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
	}

	private void init() {
		TextView tv_about = (TextView) findViewById(R.id.tv_about);
		tv_about.setText(Html.fromHtml(getString(R.string.vanward_profile)));
	}

}
