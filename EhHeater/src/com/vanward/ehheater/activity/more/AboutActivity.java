package com.vanward.ehheater.activity.more;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class AboutActivity extends EhHeaterBaseActivity {

	private Button btn_check_update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_about);
		setTopText(R.string.about);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		btn_check_update = (Button) findViewById(R.id.btn_check_update);
	}

	private void setListener() {
		btn_check_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	private void init() {
	}

}
