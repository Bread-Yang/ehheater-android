package com.vanward.ehheater.activity.more;

import android.os.Bundle;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class HelpActivity extends EhHeaterBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_help);
		setTopText(R.string.help);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
	}

	private void setListener() {
	}

	private void init() {
	}

}
