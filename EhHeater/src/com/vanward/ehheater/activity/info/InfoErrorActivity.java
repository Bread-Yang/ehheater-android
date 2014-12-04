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
		detail.setText("请先暂时关闭热水龙头再打开,或关/开显示器，再操作1-2次仍然显示故障代码,请务必关闭水阀和气阀，拔掉电源插头，请与售后服务联系。");
		time = (TextView) findViewById(R.id.time);
		time.setText(getIntent().getStringExtra("time"));
	}

	@Override
	public void onClick(View arg0) {
		finish();
	}

}
