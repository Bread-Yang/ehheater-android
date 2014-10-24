package com.vanward.ehheater.activity.more;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class AboutActivity extends Activity {

	private Button btn_check_update;
	private Button rightbButton;
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
	}

	private void init() {
	}

}
