package com.vanward.ehheater.activity.main;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.dao.BaseDao;

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

	@ViewInject(id = R.id.RadioGroup01, click = "")
	RadioGroup peopleGroup;

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
		peopleGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.RadioButton07:
					// unlock
					unLockTempAndPower();
					break;
				case R.id.RadioButton05:
					// lock
					// set args
					lockTempAndPower();
					setArgsForTempAndPower(45, 3);
					break;
				case R.id.RadioButton04:
					lockTempAndPower();
					setArgsForTempAndPower(65, 3);
					break;
				case R.id.RadioButton06:
					lockTempAndPower();
					setArgsForTempAndPower(75, 3);
					break;
				}
			}
		});
	}
	
	private void lockTempAndPower() {
		seekBar.setEnabled(false);
		for(int i = 0; i < powerGroup.getChildCount(); i++){
		    ((RadioButton)powerGroup.getChildAt(i)).setEnabled(false);
		}
	}
	
	private void unLockTempAndPower() {
		seekBar.setEnabled(true);
		for(int i = 0; i < powerGroup.getChildCount(); i++){
		    ((RadioButton)powerGroup.getChildAt(i)).setEnabled(true);
		}
	}
	
	private void setArgsForTempAndPower(int temp, int power) {
		seekBar.setProgress(temp-35);
		switch (power) {
		case 1:
			powerGroup.check(R.id.RadioButton03);
			break;
		case 2:
			powerGroup.check(R.id.RadioButton01);
			break;
		case 3:
			powerGroup.check(R.id.RadioButton02);
			break;
		}
		
	}

	public CustomSetVo getData() {
		CustomSetVo customSetVo = new CustomSetVo();
		customSetVo.setConnid(Global.connectId);
		if (nameedittext.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入名字", Toast.LENGTH_SHORT).show();
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
