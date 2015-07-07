package com.vanward.ehheater.activity.main.electric;

import android.app.Activity;
import android.os.Bundle;

import com.vanward.ehheater.R;
import com.vanward.ehheater.view.BaoCircleSlider;
import com.vanward.ehheater.view.BaoCircleSlider.BaoCircleSliderListener;

public class TestActivity extends Activity implements BaoCircleSliderListener{
	
	private BaoCircleSlider circle_slider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		circle_slider = (BaoCircleSlider)findViewById(R.id.circle_slider);
		
		circle_slider.setCircleSliderListener(this);
		
//		ImageView iv_animation = (ImageView) findViewById(R.id.iv_animation);
//		((AnimationDrawable) iv_animation.getDrawable()).start();
//
//		ImageView wave_bg = (ImageView) findViewById(R.id.wave_bg);
//		((AnimationDrawable) wave_bg.getDrawable()).start();

//		BaoCircleSlider circleSlider = new BaoCircleSlider(this);
//		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
//				LayoutParams.FILL_PARENT);
//		circleSlider.setLayoutParams(params);
//		
//		RelativeLayout rlt_test = (RelativeLayout) findViewById(R.id.rlt_test);
//		rlt_test.addView(circleSlider);
	}

	@Override
	public void didBeginTouchCircleSlider() {
		
	}

	@Override
	public void needChangeValue(int value, boolean isAdd) {
		circle_slider.setValue(value);
	}

	@Override
	public void didEndChangeValue() {
		// TODO Auto-generated method stub
		
	}
}
