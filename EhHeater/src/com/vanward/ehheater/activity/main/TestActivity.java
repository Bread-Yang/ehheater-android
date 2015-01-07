package com.vanward.ehheater.activity.main;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.vanward.ehheater.R;
import com.vanward.ehheater.view.BaoCircleSlider;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_test);
		ImageView iv_animation = (ImageView) findViewById(R.id.iv_animation);
		((AnimationDrawable) iv_animation.getDrawable()).start();

		ImageView wave_bg = (ImageView) findViewById(R.id.wave_bg);
		((AnimationDrawable) wave_bg.getDrawable()).start();

//		BaoCircleSlider circleSlider = new BaoCircleSlider(this);
//		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
//				LayoutParams.FILL_PARENT);
//		circleSlider.setLayoutParams(params);
//		
//		RelativeLayout rlt_test = (RelativeLayout) findViewById(R.id.rlt_test);
//		rlt_test.addView(circleSlider);
	}
}
