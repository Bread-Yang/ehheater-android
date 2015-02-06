package com.vanward.ehheater.activity.main.gas;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.view.SeekBarHint;
import com.vanward.ehheater.view.SeekBarHint.OnSeekBarHintProgressChangeListener;

public class GasAddPatternActivity extends EhHeaterBaseActivity implements
		OnClickListener, OnSeekBarChangeListener {

	private static final String TAG = "GasAddPatternActivity";

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

	@ViewInject(id = R.id.water_radiogroup, click = "")
	RadioGroup waterGroup;
	
	GasCustomSetVo oldGasCustomSetVo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gas_add_pattern);
		FinalActivity.initInjectedView(this);
		ivTitleBtnLeft.setBackgroundResource(R.drawable.icon_back);
		ivTitleBtnRigh.setBackgroundResource(R.drawable.menu_icon_ye);
		seekBar.setOnSeekBarChangeListener(this);
		int voId = getIntent().getIntExtra("gasCusVoId", -1);
		if (voId != -1) {
			oldGasCustomSetVo = new BaseDao(this).getDb().findById(voId,
					GasCustomSetVo.class);
			setData(oldGasCustomSetVo);
			ivTitleName.setText("编辑模式");
		} else {
			ivTitleName.setText("添加模式");
		}

		seekBar.setOnProgressChangeListener(new OnSeekBarHintProgressChangeListener() {

			@Override
			public String onHintTextChanged(SeekBarHint seekBarHint,
					int progress) {
				int position = seekBar.getProgress();
				int temp = position + 35;

				if (48 >= temp)
					;
				else if (49 == temp)
					temp = 48;
				else if (50 <= temp && 53 > temp)
					temp = 50;
				else if (53 <= temp && 58 > temp)
					temp = 55;
				else if (58 <= temp && 63 > temp)
					temp = 60;
				else if (63 <= temp && 65 >= temp)
					temp = 65;

				return String.format("%s℃", temp);
			}
		});
	}

	private void setData(GasCustomSetVo gasVo) {
		nameedittext.setText(gasVo.getName());
		seekBar.setProgress(gasVo.getTempter() - 35);

		int water = gasVo.getWaterval();

		switch (water) {
		case 40:
			waterGroup.check(R.id.RadioButton02);
			break;
		case 60:
			waterGroup.check(R.id.RadioButton01);
			break;
		case 100:
			waterGroup.check(R.id.RadioButton03);
			break;
		}

	}

	public GasCustomSetVo getData() {
		GasCustomSetVo customSetVo = new GasCustomSetVo();
		if (oldGasCustomSetVo != null) {
			customSetVo = oldGasCustomSetVo;
		}
		customSetVo
				.setUid(AccountService.getUserId(GasAddPatternActivity.this));
		customSetVo.setConnid(Global.connectId);
		String modeName = nameedittext.getText().toString().trim();
		if ("".equals(modeName)) {
			Toast.makeText(this, R.string.input_mode_name, Toast.LENGTH_SHORT)
					.show();
			return null;
		}
		
		boolean isSameWithLastName = false;
		if (oldGasCustomSetVo != null) {
			if (modeName.equals(oldGasCustomSetVo.getName())) {
				isSameWithLastName = true;
			}
		}
		
		if (!isSameWithLastName) {
			List items = new BaseDao(this).getDb().findAllByWhere(
					GasCustomSetVo.class, " name = '" + modeName + "'");

			if (items.size() != 0) {
				Toast.makeText(this, R.string.mode_name_exist, Toast.LENGTH_SHORT)
						.show();
				return null;
			}
		}
		
		if (modeName.equals("舒适") || modeName.equals("厨房")
				|| modeName.equals("浴缸") || modeName.equals("节能")
				|| modeName.equals("智能")) {
			Toast.makeText(this, R.string.mode_name_exist, Toast.LENGTH_SHORT)
					.show();
			return null;
		}

		customSetVo.setName(nameedittext.getText().toString());
		customSetVo.setTempter(seekBar.getProgress() + 35);
		List<GasCustomSetVo> list = new BaseDao(this).getDb().findAllByWhere(
				GasCustomSetVo.class,
				" uid = '"
						+ AccountService.getUserId(GasAddPatternActivity.this)
						+ "'");

		if (oldGasCustomSetVo == null) {
			for (int i = 0; i < list.size(); i++) {
				if (nameedittext.getText().toString()
						.equals(list.get(i).getName())) {
					Toast.makeText(this, "请输入有效名字！", Toast.LENGTH_SHORT).show();
					return null;
				}
			}
			customSetVo.setId(list.size() + 1);
		}

		View view = null;
		for (int i = 0; i < waterGroup.getChildCount(); i++) {
			if (waterGroup.getCheckedRadioButtonId() == waterGroup
					.getChildAt(i).getId()) {
				view = waterGroup.getChildAt(i);
			}
		}
		System.out.println("waterval:　" + view.getTag());
		customSetVo.setWaterval(Integer.parseInt((String) view.getTag()));
		return customSetVo;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		case R.id.ivTitleBtnRigh:
			final GasCustomSetVo customSetVo = getData();
			Log.e(TAG,
					"提交的customSetVo.getWaterval() : "
							+ customSetVo.getWaterval());
			Log.e(TAG,
					"提交的customSetVo.getTempter() : " + customSetVo.getTempter());
			if (customSetVo != null) {
				if (oldGasCustomSetVo != null) {
					new BaseDao(this).getDb().delete(oldGasCustomSetVo);
				}

				boolean isCheck = getIntent().getBooleanExtra("ischeck", false);
				customSetVo.setSet(isCheck);

				new BaseDao(this).getDb().replace(customSetVo);

				if (isCheck) {
					new Thread(new Runnable() {

						@Override
						public void run() {

							SendMsgModel.setDIYModel(customSetVo.getId(),
									customSetVo);

						}
					}).start();

				}
				finish();
			}

			break;

		default:
			break;
		}
		super.onClick(view);
	}

	public int heatmakeRange(int value) {
		int index = 0;
		if (value == 49) {
			index = 50;
		} else if (value > 49) {
			int[] rangs = { 50, 55, 60, 65 };
			for (int i = 0; i < rangs.length; i++) {
				if (Math.abs(value - rangs[i]) <= 3) {
					index = rangs[i];
					break;
				}
			}
		} else {
			index = value;
		}
		return index;
	}

	@Override
	public void onProgressChanged(SeekBar seekbar1, int arg1, boolean arg2) {

//		int position = seekbar1.getProgress();
//		int temp = position + 35;
//
//		if (48 >= temp)
//			;
//		else if (49 == temp)
//			temp = 48;
//		else if (50 <= temp && 53 > temp)
//			temp = 50;
//		else if (53 <= temp && 58 > temp)
//			temp = 55;
//		else if (58 <= temp && 63 > temp)
//			temp = 60;
//		else if (63 <= temp && 65 >= temp)
//			temp = 65;
//
//		float x = seekbar1.getWidth();
//		float seekbarWidth = seekbar1.getX();
//		float y = seekbar1.getY();
//		float width = (position * x) / 100 + seekbarWidth;
//		System.out.println("width: " + width);
//		System.out.println("x: " + x);
//		System.out.println("position: " + position);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
//		seekBar.setProgress(heatmakeRange(arg0.getProgress() + 35) - 35);
	}
}
