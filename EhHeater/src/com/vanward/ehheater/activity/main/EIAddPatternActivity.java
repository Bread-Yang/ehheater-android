package com.vanward.ehheater.activity.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.db.DBService;
import com.vanward.ehheater.view.SeekBarHint;
import com.vanward.ehheater.view.SeekBarHint.OnSeekBarHintProgressChangeListener;

public class EIAddPatternActivity extends EhHeaterBaseActivity implements
		OnClickListener {

	private static final String TAG = "EIAddPatternActivity";

	@ViewInject(id = R.id.ivTitleName, click = "onClick")
	TextView ivTitleName;
	@ViewInject(id = R.id.ivTitleBtnLeft, click = "onClick")
	Button ivTitleBtnLeft;
	@ViewInject(id = R.id.ivTitleBtnRigh, click = "onClick")
	Button ivTitleBtnRigh;

	@ViewInject(id = R.id.nameedittext, click = "")
	EditText nameedittext;

	@ViewInject(id = R.id.seekBar, click = "")
	SeekBarHint seekBar;

	@ViewInject(id = R.id.power, click = "")
	RadioGroup powerGroup;

	@ViewInject(id = R.id.RadioGroup01, click = "")
	RadioGroup peopleGroup;

	@ViewInject(id = R.id.tem_latout, click = "")
	RelativeLayout temlayout;

	@ViewInject(id = R.id.power_layout, click = "")
	RelativeLayout powerLayout;

	@ViewInject(id = R.id.imageButton1, click = "onClick")
	ImageButton swich;
	@ViewInject(id = R.id.RelativeLayout04, click = "onClick")
	RelativeLayout peopleNum;
	private CustomSetVo oldcustomSetVo;

	static Map<Integer, Integer> tempMap = new HashMap<Integer, Integer>();

	static {
		tempMap.put(1, 45);
		tempMap.put(2, 65);
		tempMap.put(3, 75);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_pattern);
		FinalActivity.initInjectedView(this);

		ivTitleBtnLeft.setBackgroundResource(R.drawable.icon_back);
		ivTitleBtnRigh.setBackgroundResource(R.drawable.menu_icon_ye);

		setData();

		peopleGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				// case R.id.RadioButton07:
				// // unlock
				// unLockTempAndPower();
				// break;
				case R.id.RadioButton05:
					// lock
					// set args
					// lockTempAndPower();
					setArgsForTempAndPower(45, 3);
					break;
				case R.id.RadioButton04:
					// lockTempAndPower();
					setArgsForTempAndPower(65, 3);
					break;
				case R.id.RadioButton06:
					// lockTempAndPower();
					setArgsForTempAndPower(75, 3);
					break;
				}
			}
		});

		seekBar.setOnProgressChangeListener(new OnSeekBarHintProgressChangeListener() {

			@Override
			public String onHintTextChanged(SeekBarHint seekBarHint,
					int progress) {
				return String.format("%s℃", progress + 35);
			}
		});
	}

	public void setData() {
		String name = getIntent().getStringExtra("index");
		if (name != null) {
			oldcustomSetVo = DBService.getInstance(this).findById(name,
					CustomSetVo.class);

			nameedittext.setText(oldcustomSetVo.getName());

			if (oldcustomSetVo.getPeoplenum() > 0) {
				for (int i = 0; i < peopleGroup.getChildCount(); i++) {
					if (Integer.parseInt(peopleGroup.getChildAt(i).getTag()
							.toString()) == oldcustomSetVo.getPeoplenum()) {
						((RadioButton) peopleGroup.getChildAt(i))
								.setChecked(true);
						break;
					}
				}
				setSwich(true);
			} else {
				setArgsForTempAndPower(oldcustomSetVo.getTempter(),
						oldcustomSetVo.getPower());
				setSwich(false);
			}

			ivTitleName.setText("编辑模式");

		} else {
			ivTitleName.setText("添加模式");
			swich.setTag(1);
			peopleNum.setVisibility(View.GONE);

		}

	}

	private void lockTempAndPower() {
		seekBar.setEnabled(false);
		for (int i = 0; i < powerGroup.getChildCount(); i++) {
			((RadioButton) powerGroup.getChildAt(i)).setEnabled(false);
		}
	}

	private void unLockTempAndPower() {
		seekBar.setEnabled(true);
		for (int i = 0; i < powerGroup.getChildCount(); i++) {
			((RadioButton) powerGroup.getChildAt(i)).setEnabled(true);
		}
	}

	private void setArgsForTempAndPower(int temp, int power) {
		seekBar.setProgress(temp - 35);
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
		customSetVo.setUid(AccountService.getUserId(EIAddPatternActivity.this));
		customSetVo.setConnid(Global.connectId);
		String modeName = nameedittext.getText().toString().trim();
		if ("".equals(modeName)) {
			Toast.makeText(this, R.string.input_mode_name, Toast.LENGTH_SHORT)
					.show();
			return null;
		}
		
		if (modeName.equals("晨浴") || modeName.equals("夜电")
				|| modeName.equals("智能")) {
			Toast.makeText(this, R.string.mode_name_exist, Toast.LENGTH_SHORT)
					.show();
			return null;
		}

		List items = new BaseDao(this).getDb().findAllByWhere(
				CustomSetVo.class, " name = '" + modeName + "'");

		if (items.size() != 0) {
			Toast.makeText(this, R.string.mode_name_exist, Toast.LENGTH_SHORT)
					.show();
			return null;
		}
		
		customSetVo.setName(modeName);

		if (swich.getTag() != null
				&& Integer.parseInt(swich.getTag().toString()) == 0) {

			View view = null;
			for (int i = 0; i < peopleGroup.getChildCount(); i++) {
				if (peopleGroup.getCheckedRadioButtonId() == peopleGroup
						.getChildAt(i).getId()) {
					view = peopleGroup.getChildAt(i);
				}
			}

			customSetVo
					.setPeoplenum(Integer.parseInt(view.getTag().toString()));
			customSetVo.setTempter(tempMap.get(customSetVo.getPeoplenum()));
			customSetVo.setPower(3);
		} else {
			customSetVo.setTempter(seekBar.getProgress() + 35);
			List list = new BaseDao(this)
					.getDb()
					.findAllByWhere(
							CustomSetVo.class,
							" uid = '"
									+ AccountService
											.getUserId(EIAddPatternActivity.this)
									+ "'");
			View view = null;
			for (int i = 0; i < powerGroup.getChildCount(); i++) {
				if (powerGroup.getCheckedRadioButtonId() == powerGroup
						.getChildAt(i).getId()) {
					view = powerGroup.getChildAt(i);
				}
			}
			Log.e(TAG, "power:　" + view.getTag());
			customSetVo.setPower(Integer.parseInt((String) view.getTag()));

		}
		return customSetVo;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		case R.id.ivTitleBtnRigh:
			final CustomSetVo customSetVo = getData();
			if (customSetVo != null) {
				if (oldcustomSetVo != null) {
					new BaseDao(this).getDb().delete(oldcustomSetVo);
				}

				boolean isCheck = getIntent().getBooleanExtra("ischeck", false);
				customSetVo.setSet(isCheck);

				new BaseDao(this).getDb().replace(customSetVo);
				if (isCheck) {
					new Thread(new Runnable() {

						@Override
						public void run() {

							Log.e(TAG, "自定义");
							SendMsgModel.changeToZidingyiMode();
							try {
								Thread.sleep(700);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							Log.e(TAG, "自定义 pow: " + customSetVo.getPower());
							SendMsgModel.setPower(customSetVo.getPower());
							try {
								Thread.sleep(700);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							Log.e(TAG, "自定义 Tem: " + customSetVo.getTempter());
							SendMsgModel.setTempter(customSetVo.getTempter());

						}
					}).start();

				}
				finish();
			}

			break;
		case R.id.imageButton1:
			setSwich((Integer) swich.getTag() == 1);
			break;
		default:
			break;
		}
		super.onClick(view);
	}

	public void setSwich(boolean plag) {
		if (plag) {
			peopleNum.setVisibility(View.VISIBLE);
			temlayout.setVisibility(View.GONE);
			powerLayout.setVisibility(View.GONE);
			swich.setImageResource(R.drawable.on);
			swich.setTag(0);
		} else {
			peopleNum.setVisibility(View.GONE);
			temlayout.setVisibility(View.VISIBLE);
			powerLayout.setVisibility(View.VISIBLE);
			swich.setImageResource(R.drawable.off);
			swich.setTag(1);
		}
	}

}
