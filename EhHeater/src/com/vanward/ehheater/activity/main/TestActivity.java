package com.vanward.ehheater.activity.main;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;

import com.vanward.ehheater.R;

public class TestActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_test);
		View iv_animation = findViewById(R.id.iv_animation);
		((AnimationDrawable) iv_animation.getBackground()).start();
	}
}
