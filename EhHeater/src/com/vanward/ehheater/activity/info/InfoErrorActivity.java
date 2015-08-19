package com.vanward.ehheater.activity.info;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.furnace.FurnaceSendCommandService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.xtremeprog.xpgconnect.generated.generated;

public class InfoErrorActivity extends Activity implements OnClickListener {

	private Button leftbutton;
	private Button rightbButton;
	private TextView name, time, detail;
	private Dialog dialog_dial;
	private Button btn_reset, btn_contact;

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
		btn_contact = (Button) findViewById(R.id.btn_contact);
		btn_reset = (Button) findViewById(R.id.btn_reset);
		btn_contact.setOnClickListener(this);
		btn_reset.setOnClickListener(this);
		leftbutton.setBackgroundResource(R.drawable.icon_back);
		TextView title = (TextView) findViewById(R.id.ivTitleName);
		title.setText("信息");
		
		HeaterInfoService hser = new HeaterInfoService(getBaseContext());

		// 当前设备是壁挂炉才显示复位按钮
		if (!hser.getCurrentSelectedHeater().getProductKey()
				.equals(Consts.FURNACE_PRODUCT_KEY)) {
			btn_reset.setVisibility(View.INVISIBLE);
		}

		name = (TextView) findViewById(R.id.name);
		name.setText(getIntent().getStringExtra("name"));
		detail = (TextView) findViewById(R.id.detail);
		detail.setText(getIntent().getStringExtra("detail"));
		time = (TextView) findViewById(R.id.time);
		time.setText(getIntent().getStringExtra("time"));

		dialog_dial = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.hotline_num,
						BaoDialogShowUtil.DEFAULT_RESID, R.string.dial, null,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog_dial.dismiss();
								Intent intent = new Intent(Intent.ACTION_CALL,
										Uri.parse("tel:"
												+ getResources().getString(
														R.string.hotline_num)));
								startActivity(intent);
							}
						});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_contact:
			dialog_dial.show();
			break;
		case R.id.btn_reset:
			FurnaceSendCommandService.getInstance().SendDERYResetErrorReq();
			break;
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		}
	}

}
