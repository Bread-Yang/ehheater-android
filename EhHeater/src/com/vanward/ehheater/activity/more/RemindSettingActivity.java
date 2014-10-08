package com.vanward.ehheater.activity.more;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class RemindSettingActivity extends EhHeaterBaseActivity {

	private ToggleButton tb_switch;
	private ListView lv_listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_remind_setting);
		setTopText(R.string.remind_setting);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		tb_switch = (ToggleButton) findViewById(R.id.tb_switch);
		lv_listview = (ListView) findViewById(R.id.lv_listview);
	}

	private void setListener() {
		tb_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

			}
		});
	}

	private void init() {
	}

}
