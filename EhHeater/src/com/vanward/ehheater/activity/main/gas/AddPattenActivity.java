package com.vanward.ehheater.activity.main.gas;

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
import com.vanward.ehheater.view.SeekBarHint;
import com.vanward.ehheater.view.SeekBarHint.OnSeekBarHintProgressChangeListener;

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

	@ViewInject(id = R.id.seekBar, click = "")
	SeekBarHint seekBar;

	@ViewInject(id = R.id.water_radiogroup, click = "")
	RadioGroup waterGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_gas_pattern);
		FinalActivity.initInjectedView(this);
		ivTitleName.setText("添加模式");
		ivTitleBtnLeft.setBackgroundResource(R.drawable.icon_back);
		ivTitleBtnRigh.setBackgroundResource(R.drawable.menu_icon_ye);
		seekBar.setOnSeekBarChangeListener(this);
		int voId = getIntent().getIntExtra("gasCusVoId", -1);
		if (voId != -1) {
			gasVo = new BaseDao(this).getDb().findById(voId,
					GasCustomSetVo.class);
			setData(gasVo);
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
				
				return String.format("%s℃",temp);
			}
		});
	}

	GasCustomSetVo gasVo;

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
		if (gasVo != null) {
			customSetVo = gasVo;
		}
		customSetVo.setConnid(Global.connectId);
		if (nameedittext.getText().toString().length() <= 0) {
			Toast.makeText(this, "请输入名称", Toast.LENGTH_SHORT).show();
			return null;
		}

		customSetVo.setName(nameedittext.getText().toString());
		customSetVo.setTempter(seekBar.getProgress() + 35);
		List<GasCustomSetVo> list = new BaseDao(this).getDb().findAll(
				GasCustomSetVo.class);

		if (gasVo == null) {
			for (int i = 0; i < list.size(); i++) {
				if (nameedittext.getText().toString()
						.equals(list.get(i).getName())) {
					Toast.makeText(this, "请输入有效名字！", Toast.LENGTH_SHORT)
							.show();
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
			if (customSetVo != null) {
				if (gasVo != null) {
					new BaseDao(this).getDb().delete(gasVo);
				}
				new BaseDao(this).getDb().replace(customSetVo);

				if (getIntent().getBooleanExtra("ischeck", false)) {
					new Thread(new Runnable() {

						@Override
						public void run() {

							SendMsgModel
							.setDIYModel(
									customSetVo
											.getId(),
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

		int position = seekbar1.getProgress();
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

		float x = seekbar1.getWidth();
		float seekbarWidth = seekbar1.getX();
		float y = seekbar1.getY();
		float width = (position * x) / 100 + seekbarWidth;
		System.out.println("width: " + width);
		System.out.println("x: " + x);
		System.out.println("position: " + position);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		seekBar.setProgress(heatmakeRange(arg0.getProgress() + 35) - 35);
	}
}
