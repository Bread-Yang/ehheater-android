package com.vanward.ehheater.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.util.BitmapThumbUtil;

public class BaoCircleSlider extends View {

	private static final String TAG = "BaoCircleSlider";

	private float minValue; // 起始值 : 0
	private float maxValue;
	private int value; // slider对外的值,即圆点的位置代表的值

	private ImageView ctrlIconView;
	private ImageView backImageView;
	private ImageView addImageView;
	private ImageView minusImageView;

	private int ctrlIconViewWidth;

	private Point ctrlIconViewCenterPoint = new Point(100, 100);

	/** 用于在Graphical Layout中显示 */
	private boolean isFirstDisplay = true;

	private Point circleCenter = new Point();

	/** 用于记录拖动时，手指位置代表的值，值域为minValue~maxValue */
	private int currentValue;

	/** 移动时,记录上一个角度 */
	private float preAngle;

	private float minus_or_add_imageView_rotate_angle = 0;

	private boolean showCtrlIcon = true;

	private boolean draging;

	private boolean changingValue;

	private boolean isAdd;

	private boolean isOnline;

	private boolean isTouchCtrlIcon;

	private BaoCircleSliderListener circleSliderListener;

	private boolean showAddImageView = false;

	private boolean showMinusImageView = false;
	
	private boolean showCtrlIconView = true;

	private CountDownTimer countDownTimer;

	private int offset = 0;

	public interface BaoCircleSliderListener {

		public void didBeginTouchCircleSlider();

		public void needChangeValue(int value, boolean isAdd);

		public void didEndChangeValue();

	}

	public BaoCircleSlider(Context context) {
		super(context);
		initRoutine();
	}

	public BaoCircleSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		initRoutine();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		circleCenter = new Point(widthSpecSize / 2, heightSpecSize / 2);

		ctrlIconViewWidth = widthSpecSize / 6;

		ctrlIconView.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		if (isFirstDisplay) {
			ctrlIconViewCenterPoint = new Point(widthSpecSize / 2,
					heightSpecSize - ctrlIconViewWidth / 2);
			ctrlIconView.layout(widthSpecSize / 2 - ctrlIconViewWidth / 2,
					heightSpecSize - ctrlIconViewWidth, widthSpecSize / 2
							+ ctrlIconViewWidth / 2, heightSpecSize);
		} else {
			ctrlIconView.layout(ctrlIconViewCenterPoint.x - ctrlIconViewWidth
					/ 2, ctrlIconViewCenterPoint.y - ctrlIconViewWidth / 2,
					ctrlIconViewCenterPoint.x + ctrlIconViewWidth / 2,
					ctrlIconViewCenterPoint.y + ctrlIconViewWidth / 2);
		}

		float scale = this.getResources().getDisplayMetrics().density;
		offset = (int) (8 * scale + 0.5f);
		// int offset = 0;

		addImageView.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addImageView.layout(offset, offset, widthSpecSize - offset,
				widthSpecSize - offset);

		minusImageView.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		minusImageView.layout(offset, offset, widthSpecSize - offset,
				widthSpecSize - offset);

		backImageView.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		backImageView.layout(offset, offset, widthSpecSize - offset,
				widthSpecSize - offset);
	}

	private void initRoutine() {
		isOnline = false;
		isTouchCtrlIcon = false;
		preAngle = 0;
		minValue = 20;
		maxValue = 90;
		if (ctrlIconView == null) {
			ctrlIconView = new ImageView(getContext());
			ctrlIconView.setImageBitmap(BitmapThumbUtil
					.decodeSampledBitmapFromResource(getResources(),
							R.drawable.home_yuan_tiao_intact, 100, 100));
		}
		if (addImageView == null) {
			addImageView = new ImageView(getContext());
			addImageView.setImageBitmap(BitmapThumbUtil
					.decodeSampledBitmapFromResource(getResources(),
							R.drawable.home_yuan_add, 300, 300));
		}
		if (minusImageView == null) {
			minusImageView = new ImageView(getContext());
			minusImageView.setImageBitmap(BitmapThumbUtil
					.decodeSampledBitmapFromResource(getResources(),
							R.drawable.home_yuan_reduction, 300, 300));
		}
		if (backImageView == null) {
			backImageView = new ImageView(getContext());
			backImageView.setImageBitmap(BitmapThumbUtil
					.decodeSampledBitmapFromResource(getResources(),
							R.drawable.home_yuan_bg, 300, 300));
		}
	}

	/** 是否在拖动 */
	public boolean isDraging() {
		return draging;
	}

	public float getMinValue() {
		return minValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

	public BaoCircleSliderListener getCircleSliderListener() {
		return circleSliderListener;
	}

	public void setCircleSliderListener(
			BaoCircleSliderListener circleSliderListener) {
		this.circleSliderListener = circleSliderListener;
	}

	public float getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

	public boolean isShowAddImageView() {
		return showAddImageView;
	}

	public void setShowAddImageView(boolean showAddImageView) {
		this.showAddImageView = showAddImageView;
		invalidate();
	}

	public boolean isShowMinusImageView() {
		return showMinusImageView;
	}

	public void setShowMinusImageView(boolean showMinusImageView) {
		this.showMinusImageView = showMinusImageView;
		invalidate();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		this.post(new Runnable() {

			@Override
			public void run() {
				updateCtrlIconPositionOfValue(BaoCircleSlider.this.value);
			}
		});
		isFirstDisplay = false;
	}

	public boolean isShowCtrlIcon() {
		return showCtrlIcon;
	}

	public void setShowCtrlIcon(boolean showCtrlIcon) {
		this.showCtrlIcon = showCtrlIcon;
		ctrlIconView
				.setVisibility(showCtrlIcon ? View.VISIBLE : View.INVISIBLE);
	}

	private void updateCtrlIconPositionOfValue(int value) {
		float angle = 360.0f * (value - this.minValue)
				/ (this.maxValue - this.minValue); // 当前值所偏移的度数
		ctrlIconViewCenterPoint = iconCenterOfAngle(angle);
		// L.e(this, "ctrlIconViewCenterPoint.x : " +
		// ctrlIconViewCenterPoint.x);
		// L.e(this, "ctrlIconViewCenterPoint.y : " +
		// ctrlIconViewCenterPoint.y);
		updateDragingTipViewRotateOfValue(value);
		ctrlIconView.layout(ctrlIconViewCenterPoint.x - ctrlIconViewWidth / 2,
				ctrlIconViewCenterPoint.y - ctrlIconViewWidth / 2,
				ctrlIconViewCenterPoint.x + ctrlIconViewWidth / 2,
				ctrlIconViewCenterPoint.y + ctrlIconViewWidth / 2);
		// ctrlIconView.layout(ctrlIconViewCenterPoint.x - ctrlIconViewWidth /
		// 2,
		// ctrlIconViewCenterPoint.y - ctrlIconViewWidth / 2,
		// ctrlIconViewCenterPoint.x + ctrlIconViewWidth / 2,
		// ctrlIconViewCenterPoint.y + ctrlIconViewWidth / 2);
		invalidate();
	}

	private void updateDragingTipViewRotateOfValue(int value) {
		minus_or_add_imageView_rotate_angle = 360.0f * (value - this.minValue)
				/ (this.maxValue - this.minValue); // 当前值所偏移的度数

	}

	private void dealWithTouchPoint(Point touchPoint) {
		float angle = angleOfTouchPoint(touchPoint);
		int value = valueOfAngle(angle);
		// L.e(this, "dealWithTouchPoint : " + "angle : " + angle);
		// L.e(this, "dealWithTouchPoint : " + "value : " + value);
		setCurrentValue(value);
	}

	private void hideDragingTipView() {
		showAddImageView = false;
		showMinusImageView = false;
		invalidate();
	}

	/**
	 * 点击的点是否在圆环线上
	 * 
	 * @param point
	 * @return
	 */
	private boolean isOnLine(Point point) {
		float radius = getWidth() / 2;
		float length = lengthOfTwoPoint(point, circleCenter);
		boolean isOnline = (length < (radius + ctrlIconViewWidth * 3 / 2))
				&& (length > (radius - ctrlIconViewWidth * 3 / 2));
		// L.e(this, "是否在圆环线上 : " + isOnline);
		return isOnline;
	}

	private boolean isTouchInCtrlIconView(Point point) {
		Rect hitRect = new Rect();
		ctrlIconView.getHitRect(hitRect);
		// L.e(this, "hitRect.left : " + hitRect.left);
		// L.e(this, "hitRect.top : " + hitRect.top);
		// L.e(this, "hitRect.right : " + hitRect.right);
		// L.e(this, "hitRect.bottom : " + hitRect.bottom);
		// L.e(this, "是否在Rect中 : " + hitRect.contains(point.x, point.y));
		return hitRect.contains(point.x, point.y);
	}

	private int valueOfAngle(float angle) {
		float valueRange = (this.maxValue - this.minValue);
		int value = (int) (Math.ceil(angle * valueRange / 360.0) + this.minValue);
		// L.e(this, "valueOfAngle : " + value);
		return value;
	}

	private float lengthOfTwoPoint(Point p1, Point p2) {
		float a = Math.abs(p1.x - p2.x);
		float b = Math.abs(p1.y - p2.y);
		return (float) Math.sqrt(a * a + b * b);
	}

	private float angleOfTouchPoint(Point point) {
		// 根据公式 c^2 = a^2 + b^2 - 2abCos(a); cos(a) = (a^2 + b^2 - c^2) / 2ab
		float radius = getWidth() / 2;
		Point southPoint = new Point(getWidth() / 2, getHeight());
		float a = lengthOfTwoPoint(circleCenter, southPoint);
		float b = lengthOfTwoPoint(circleCenter, point);
		float c = lengthOfTwoPoint(point, southPoint);
		float cosValue = ((a * a) + (b * b) - (c * c)) / (2 * a * b);
		double radian = Math.acos(cosValue);
		float angle = (float) Math.toDegrees(radian);
		if (point.x > circleCenter.x) {
			angle = 360 - angle;
		}
		// L.e(this, "点击处的角度是 : " + angle);
		return angle;

	}

	private Point iconCenterOfAngle(float angle) {
		float normalAngle = 90 + angle; // 以最南端为起点，顺时针偏移角度
		int radius = getWidth() / 2 - this.ctrlIconViewWidth / 3 * 2; // 半径,
																		// 可以改这里,但是圆圈会被切
		// 已知圆心(x0, y0)和半径(r)、角度(a)，可求圆上一点(x1, y1), 公式: x1 = x0 + r * cos(a); y1
		// = y0 + r * sin(a); (PS:计算机中的正余弦函数的参数为弧度，需要注意装换)
		double radian = Math.toRadians(normalAngle);
		int x1 = (int) (circleCenter.x + radius * Math.cos(radian));
		int y1 = (int) (circleCenter.y + radius * Math.sin(radian));
		// L.e(this, "getWidth() : " + getWidth());
		// L.e(this, "circleCenter.x : " + circleCenter.x +
		// );
		// L.e(this, "circleCenter.x : " + circleCenter.x +
		// "");
		// L.e(this, "circleCenter.y : " + circleCenter.y +
		// "");
		Point point = new Point(x1, y1);
		return point;
	}

	private float angle2Radian(float angle) {
		return (float) (angle * Math.PI / 180);
	}

	private float radian2Angle(float radian) {
		return (float) (180 * (radian) / Math.PI);
	}

	boolean isInCtrlIcon;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		Point touchPoint = new Point((int) event.getX(), (int) event.getY());
		
		if (!showCtrlIconView) {
			return false; 
		}

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			// L.e(this, "1event : ACTION_DOWN");

			isInCtrlIcon = isTouchInCtrlIconView(touchPoint);
			isOnline = isOnLine(touchPoint);
			if (isInCtrlIcon && showCtrlIcon) {
				isTouchCtrlIcon = true;
				draging = true;
				changingValue = true;
				preAngle = angleOfTouchPoint(touchPoint);
				dealWithTouchPoint(touchPoint);
				if (circleSliderListener != null) {
					circleSliderListener.didBeginTouchCircleSlider();
				}
			}
			break;

		case MotionEvent.ACTION_MOVE:

			// L.e(this, "22event : ACTION_MOVE");

			getParent().requestDisallowInterceptTouchEvent(true);
			if (isTouchCtrlIcon) {
				draging = true;
				changingValue = true;
				float angle = angleOfTouchPoint(touchPoint);
				float offset = Math.abs(angle - preAngle);
				if (offset > 40) {
					return true; // 控制值的变化，避免从最大值跳到最小值等情况
				}
				// L.e(this, "angle - preAngle = " + (angle - preAngle));
				// if (Math.abs(angle - preAngle) >= 4) {
				isAdd = (angle > preAngle);
				// }

				preAngle = angle;
				setShowAddImageView(isAdd);
				setShowMinusImageView(!isAdd);
				dealWithTouchPoint(touchPoint);
				// setValue(currentValue); // 使ctrlIconView滚动,for test
				// L.e(this, "currentValue : " + currentValue);
				if (circleSliderListener != null) {
					circleSliderListener.needChangeValue(
							(int) this.currentValue, isAdd);
				}
			}
			break;

		case MotionEvent.ACTION_UP:

			// L.e(this, "333event : ACTION_UP");

			if (isTouchCtrlIcon) {
				float angle = angleOfTouchPoint(touchPoint);
				float offset = Math.abs(angle - preAngle);
				if (offset > 30) {
					currentValue = value;
				} else {
					dealWithTouchPoint(touchPoint);
				}
				if (circleSliderListener != null) {
					circleSliderListener.needChangeValue(currentValue, isAdd);
				}
			} else if (isOnline) {
				float angle = angleOfTouchPoint(touchPoint);
				Rect hitRect = new Rect();
				ctrlIconView.getHitRect(hitRect);
				float currentAngle = angleOfTouchPoint(new Point(
						hitRect.centerX(), hitRect.centerY()));
				// L.e(this, "点击处的angle : " + angle);
				// L.e(this, "当前圆点的currentAngle : " + currentAngle);
				boolean isAdd = angle > currentAngle;
				int pad = isAdd ? +1 : -1;
				// L.e(this, "ANGLE是 : " + isAdd);
				currentValue = value + pad;
				// setValue(currentValue); // 使ctrlIconView滚动, for test
				changingValue = true;
				if (circleSliderListener != null) {
					circleSliderListener.needChangeValue(currentValue, isAdd);
				}
			}
			if (isTouchCtrlIcon || isOnline) {
				currentValue = value;
				// resetDelaySendTimer();
				if (circleSliderListener != null) {
					circleSliderListener.didEndChangeValue();
				}
			}
			changingValue = false;
			draging = false;
			isTouchCtrlIcon = false;
			isOnline = false;
			hideDragingTipView();
			isInCtrlIcon = false;
			break;
		}
		return true;
	}

	private void resetDelaySendTimer() {
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
		countDownTimer = new CountDownTimer(3000, 1000) {

			@Override
			public void onTick(long arg0) {
			}

			@Override
			public void onFinish() {
				if (circleSliderListener != null) {
					circleSliderListener.didEndChangeValue();
				}
			}
		};
		countDownTimer.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();
		canvas.translate(offset, offset);
		backImageView.draw(canvas);
		canvas.restore();

		if (showAddImageView) {
			canvas.save();
			canvas.rotate(minus_or_add_imageView_rotate_angle, getWidth() / 2,
					getHeight() / 2);
			canvas.translate(offset, offset);
			addImageView.draw(canvas);
			canvas.restore();
		}

		

		if (showMinusImageView) {
			canvas.save();
			canvas.rotate(minus_or_add_imageView_rotate_angle, getWidth() / 2,
					getHeight() / 2);
			canvas.translate(offset, offset);
			minusImageView.draw(canvas);
			canvas.restore();
		}

		canvas.save();
		// canvas.rotate(270, getWidth() / 2, getHeight() / 2);
		if (showCtrlIconView) {
			if (isInCtrlIcon) {
				ctrlIconView.layout(ctrlIconViewCenterPoint.x - ctrlIconViewWidth
						/ 3 * 2, ctrlIconViewCenterPoint.y - ctrlIconViewWidth / 3
						* 2, ctrlIconViewCenterPoint.x + ctrlIconViewWidth / 3 * 2,
						ctrlIconViewCenterPoint.y + ctrlIconViewWidth / 3 * 2);
				canvas.translate(ctrlIconViewCenterPoint.x - ctrlIconViewWidth / 3
						* 2, ctrlIconViewCenterPoint.y - ctrlIconViewWidth / 3 * 2);
			} else {
				ctrlIconView.layout(ctrlIconViewCenterPoint.x - ctrlIconViewWidth
						/ 2, ctrlIconViewCenterPoint.y - ctrlIconViewWidth / 2,
						ctrlIconViewCenterPoint.x + ctrlIconViewWidth / 2,
						ctrlIconViewCenterPoint.y + ctrlIconViewWidth / 2);
				canvas.translate(ctrlIconViewCenterPoint.x - ctrlIconViewWidth / 2,
						ctrlIconViewCenterPoint.y - ctrlIconViewWidth / 2);
			}
			ctrlIconView.draw(canvas);
		}
		canvas.restore();
	}

	@Override
	public void setVisibility(int visibility) {
//		super.setVisibility(visibility);
		if (visibility == 0) {
			showCtrlIconView = true;
		} else {
			showCtrlIconView = false;
		}
		invalidate();
		// L.e(this, "setVisibility调用了 : " + (visibility == View.VISIBLE));
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		// L.e(this, "onDetachedFromWindow()执行了");

		((BitmapDrawable) ctrlIconView.getDrawable()).getBitmap().recycle();
		((BitmapDrawable) addImageView.getDrawable()).getBitmap().recycle();
		((BitmapDrawable) minusImageView.getDrawable()).getBitmap().recycle();

		ctrlIconView.setImageBitmap(null);
		addImageView.setImageBitmap(null);
		minusImageView.setImageBitmap(null);
	}
}
