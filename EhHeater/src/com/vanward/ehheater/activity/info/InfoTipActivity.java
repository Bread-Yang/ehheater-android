package com.vanward.ehheater.activity.info;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class InfoTipActivity extends Activity implements OnClickListener {
	private Button leftbutton;
	private Button rightbButton;
	TextView name, time, detail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infor_error);
		initview();
	}

	public void initview() {
		leftbutton = ((Button) findViewById(R.id.ivTitleBtnLeft));
		leftbutton.setOnClickListener(this);
		rightbButton = ((Button) findViewById(R.id.ivTitleBtnRigh));
		rightbButton.setVisibility(View.GONE);
		leftbutton.setBackgroundResource(R.drawable.icon_back);
		TextView title = (TextView) findViewById(R.id.ivTitleName);
		title.setText("信息");
		Button button1 = ((Button) findViewById(R.id.btn_contact));
		button1.setVisibility(View.GONE);
		name = (TextView) findViewById(R.id.name);
		name.setText(getIntent().getStringExtra("name"));
		detail = (TextView) findViewById(R.id.detail);
		detail.setText(getIntent().getStringExtra("detail"));
		time = (TextView) findViewById(R.id.time);
		time.setText(getIntent().getStringExtra("time"));

	}

	@Override
	public void onClick(View arg0) {
		finish();
	}

}
