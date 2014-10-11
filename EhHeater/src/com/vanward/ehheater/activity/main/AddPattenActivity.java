package com.vanward.ehheater.activity.main;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.view.CircularSeekBar;
import com.vanward.ehheater.view.CircularSeekBar.OnSeekChangeListener;

public class AddPattenActivity extends EhHeaterBaseActivity implements
		OnClickListener, OnSeekBarChangeListener {

	@ViewInject(id = R.id.ivTitleName, click = "onClick")
	TextView ivTitleName;
	@ViewInject(id = R.id.ivTitleBtnLeft, click = "onClick")
	Button ivTitleBtnLeft;
	@ViewInject(id = R.id.ivTitleBtnRigh, click = "onClick")
	Button ivTitleBtnRigh;

	@ViewInject(id = R.id.nameedittext, click = "")
	EditText nameedittext;

	@ViewInject(id = R.id.seekBar1, click = "")
	SeekBar seekBar;

	@ViewInject(id = R.id.power, click = "")
	RadioGroup powerGroup;

	@ViewInject(id = R.id.textView2, click = "")
	TextView degree;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_pattern);
		FinalActivity.initInjectedView(this);
		ivTitleName.setText("添加模式");
		ivTitleBtnLeft.setBackgroundResource(R.drawable.icon_back);
		ivTitleBtnRigh.setBackgroundResource(R.drawable.menu_icon_ye);
		seekBar.setOnSeekBarChangeListener(this);
		degree.setText("35℃");
	}

	public CustomSetVo getData() {
		CustomSetVo customSetVo = new CustomSetVo();
		customSetVo.setConnid(Global.connectId);
		if (nameedittext.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
			return null;
		}
		customSetVo.setName(nameedittext.getText().toString());
		customSetVo.setTempter(seekBar.getProgress() + 35);
		List list = new BaseDao(this).getDb().findAll(CustomSetVo.class);
		View view = null;
		for (int i = 0; i < powerGroup.getChildCount(); i++) {
			if (powerGroup.getCheckedRadioButtonId() == powerGroup
					.getChildAt(i).getId()) {
				view = powerGroup.getChildAt(i);
			}
		}
		System.out.println("power:　" + view.getTag());
		customSetVo.setPower(Integer.parseInt((String) view.getTag()));
		return customSetVo;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		case R.id.ivTitleBtnRigh:
			CustomSetVo customSetVo = getData();
			if (customSetVo != null) {
				new BaseDao(this).getDb().save(customSetVo);
				finish();
			}

			break;

		default:
			break;
		}
		super.onClick(view);
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		degree.setText(arg1 + 35 + "℃");
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}
}
