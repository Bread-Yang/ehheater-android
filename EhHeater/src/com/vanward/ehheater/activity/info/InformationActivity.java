package com.vanward.ehheater.activity.info;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

import android.app.Activity;
import android.os.Bundle;

public class InformationActivity extends EhHeaterBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_information);
	}
}
