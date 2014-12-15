package com.vanward.ehheater.activity.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class InfoErrorActivity extends Activity implements OnClickListener {
	private Button leftbutton;
	private Button rightbButton;
	TextView name, time, detail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
		

		name = (TextView) findViewById(R.id.name); 
		name.setText(getIntent().getStringExtra("name"));
		detail = (TextView) findViewById(R.id.detail);
		detail.setText(getIntent().getStringExtra("detail"));
		String errors=getIntent().getStringExtra("detail");
		switch (errors) {
		case "E2":
			detail.setText("切掉电源，热水器先注满水后，在通电");
			break;
			
		case "E3":
			detail.setText("请与客服联系");
			break;
			
		case "E4":
			detail.setText("请与客服联系");
			break;
			
		case "E5":
			detail.setText("请与客服联系");
			break;
		default:
			break;
		}
		time = (TextView) findViewById(R.id.time);
		time.setText(getIntent().getStringExtra("time"));
	}

	@Override
	public void onClick(View arg0) {
		finish();
	}

}
