package com.vanward.ehheater.activity.info;

import android.app.Activity;
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
		detail.setText("故障十五、漏电保护指示灯不亮\n原因1：电源杆座无电或接触不良。处理：用电笔检查是否有电以及漏电保护插头与插座的接触是否良好。\n原因2：热水器内无水（或未灌满），电热管处于干烧状态，漏电保护插头的复位按钮跳起，切断电源。处理：先按使用说明将热水器灌满水，再将按下漏电保护插头的复位按钮，即使可恢复正常。\n原因3：热水器的水温太高，漏 保护插头的复位按钮跳起， 切断电源。处理：温控器失灵，更换温控器。\n热水器漏电，漏电保护插头的复位按钮跳起，切断电源。处理：用电笔检查是否漏电，如漏电用电笔查明漏电部位，设法消除漏电。");
		time = (TextView) findViewById(R.id.time);
		time.setText(getIntent().getStringExtra("time"));
	}

	@Override
	public void onClick(View arg0) {
		finish();
	}

}
