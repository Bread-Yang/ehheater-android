package com.vanward.ehheater.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class BaoCircleSlider extends View {

	private ImageView ctrlIconView;
	private ImageView addImageView;
	private ImageView minusImageView;

	/** 用于记录拖动时，手指位置代表的值，值域为minValue~maxValue */
	private float currentValue;

	/** 移动时,记录上一个角度 */
	private float preAngle;

	private boolean isAdd;

	private boolean isOnline;
	private boolean isTouchCtrlIcon;

	private Context context;

	public BaoCircleSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		// this.context = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		if (ctrlIconView == null) {
			ctrlIconView = new ImageView(getContext());
			ctrlIconView.setImageResource(R.drawable.home_yuan_tiao_intact);
		}
		ctrlIconView.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//		ctrlIconView.layout(0, 0, widthSpecSize, widthSpecSize);

		if (addImageView == null) {
			addImageView = new ImageView(getContext());
			addImageView.setImageResource(R.drawable.home_yuan_add);
		}
		addImageView.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addImageView.layout(0, 0, widthSpecSize, widthSpecSize);

		if (minusImageView == null) {
			minusImageView = new ImageView(getContext());
			minusImageView.setImageResource(R.drawable.home_yuan_reduction);
		}
		minusImageView.setLayoutParams(new LayoutParams(100, 100));
		minusImageView.layout(0, 0, widthSpecSize, heightSpecSize);

	}

	private double angle2Radian(double angle) {
		return (angle * Math.PI / 180);
	}

	private double radian2Angle(double radian) {
		return (180 * (radian) / Math.PI);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		ctrlIconView.draw(canvas);
		addImageView.draw(canvas);
		minusImageView.draw(canvas);
		
	}
}
