package com.vanward.ehheater.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class SeekBarHint extends SeekBar implements
		SeekBar.OnSeekBarChangeListener {

	private int mPopupWidth;
	private int mPopupStyle;
	public static final int POPUP_FIXED = 1;
	public static final int POPUP_FOLLOW = 0;

	private Paint label_paint;

	private PopupWindow mPopup;
	private TextView mPopupTextView;
	private int mYLocationOffset;
	private int minValueTips;
	/** 默认是"℃" */
	private String tipsUnit;

	private OnSeekBarChangeListener mInternalListener;
	private OnSeekBarChangeListener mExternalListener;

	private OnSeekBarHintProgressChangeListener mProgressChangeListener;

	public interface OnSeekBarHintProgressChangeListener {
		public String onHintTextChanged(SeekBarHint seekBarHint, int progress);
	}

	public SeekBarHint(Context context) {
		super(context);
		init(context, null);
	}

	public SeekBarHint(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public SeekBarHint(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

		setOnSeekBarChangeListener(this);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SeekBarHint);

		mPopupWidth = (int) a.getDimension(R.styleable.SeekBarHint_popupWidth,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		mYLocationOffset = (int) a.getDimension(
				R.styleable.SeekBarHint_yOffset, 0);
		minValueTips = (int) a.getInt(R.styleable.SeekBarHint_minValueTips, 0);
		tipsUnit = (String) a.getString(R.styleable.SeekBarHint_tipsUnit);
		if (tipsUnit == null) {
			tipsUnit = "℃";
		}
		mPopupStyle = a
				.getInt(R.styleable.SeekBarHint_popupStyle, POPUP_FOLLOW);

		label_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		label_paint.setColor(Color.parseColor("#fff76805"));
		label_paint
				.setTextSize(15 * getResources().getDisplayMetrics().density + 0.5f);

		a.recycle();
		initHintPopup();
	}

	public void setPopupStyle(int style) {
		mPopupStyle = style;
	}

	public int getPopupStyle() {
		return mPopupStyle;
	}

	private void initHintPopup() {
		String popupText = "30℃";

		if (mProgressChangeListener != null) {
			popupText = mProgressChangeListener.onHintTextChanged(this,
					getProgress());
		}

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View undoView = inflater.inflate(R.layout.popup, null);
		mPopupTextView = (TextView) undoView.findViewById(R.id.text);
		mPopupTextView.setText(popupText != null ? popupText : String
				.valueOf(getProgress()));

		mPopup = new PopupWindow(undoView, mPopupWidth,
				ViewGroup.LayoutParams.WRAP_CONTENT, false);

		mPopup.setAnimationStyle(R.style.fade_animation);

		this.post(new Runnable() {

			@Override
			public void run() {
				// showPopup();
			}
		});
	}

	private void showPopup() {

		if (mPopupStyle == POPUP_FOLLOW) {
			int[] location = new int[2];
			getLocationOnScreen(location);
			mPopup.showAtLocation(this, Gravity.NO_GRAVITY,
					(int) (this.getX() + (int) getXPosition(this)),
					(int) (location[1] + mYLocationOffset));
		}
		if (mPopupStyle == POPUP_FIXED) {
			mPopup.showAtLocation(this, Gravity.CENTER | Gravity.BOTTOM, 0,
					(int) (this.getY() + mYLocationOffset + this.getHeight()));
		}
	}

	private void hidePopup() {
		if (mPopup.isShowing()) {
			mPopup.dismiss();
		}
	}

	public void setHintView(View view) {
		// TODO
		// initHintPopup();
	}

	@Override
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
		if (mInternalListener == null) {
			mInternalListener = l;
			super.setOnSeekBarChangeListener(l);
		} else {
			mExternalListener = l;
		}
	}

	public void setOnProgressChangeListener(
			OnSeekBarHintProgressChangeListener l) {
		mProgressChangeListener = l;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
		String popupText = null;
		if (mProgressChangeListener != null) {
			popupText = mProgressChangeListener.onHintTextChanged(this,
					getProgress());
		}

		if (mExternalListener != null) {
			mExternalListener.onProgressChanged(seekBar, progress, b);
		}

		mPopupTextView.setText(popupText != null ? popupText : String
				.valueOf(progress));

		if (mPopupStyle == POPUP_FOLLOW) {
			int[] location = new int[2];
			getLocationOnScreen(location);
			mPopup.update((int) (this.getX() + (int) getXPosition(seekBar)),
					(int) (location[1] + mYLocationOffset), -1, -1);
		}

	}

	public int getSnapProgress() {
		String hint = mProgressChangeListener.onHintTextChanged(this,
				getProgress());
		int snapProgress = Integer
				.valueOf(hint.substring(0, hint.length() - 1));
		return snapProgress;
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// switch (event.getAction()) {
	//
	// case MotionEvent.ACTION_DOWN:
	// setThumb(getResources().getDrawable(R.drawable.tiao_down));
	// break;
	//
	// case MotionEvent.ACTION_UP:
	// setThumb(getResources().getDrawable(R.drawable.tiao));
	// break;
	// }
	//
	// return super.onTouchEvent(event);
	// }

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		if (mExternalListener != null) {
			mExternalListener.onStartTrackingTouch(seekBar);
		}

		// showPopup();
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float baseX = getXPosition(this);
		float baseY = mYLocationOffset;

		float val = (((float) getProgress() * (float) (getWidth() - 2 * getThumbOffset())) / getMax());

		String popupText = minValueTips + tipsUnit;

		if (mProgressChangeListener != null) {
			popupText = mProgressChangeListener.onHintTextChanged(this,
					getProgress());
		}

		float textWidth = label_paint.measureText(popupText);
		if (val + textWidth > getWidth()) {
			val = getWidth() - textWidth;
		}

		canvas.drawText(popupText, val, getPaddingTop(), label_paint);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (mExternalListener != null) {
			mExternalListener.onStopTrackingTouch(seekBar);
		}

		// hidePopup();
	}

	private float getXPosition(SeekBar seekBar) {
		float val = (((float) seekBar.getProgress() * (float) (seekBar
				.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax());
		float offset = seekBar.getThumbOffset();

		int textWidth = mPopupWidth;
		float textCenter = (textWidth / 2.0f);

		float newX = val + offset - textCenter;

		return newX;
	}
}
