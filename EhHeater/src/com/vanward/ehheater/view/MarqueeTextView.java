package com.vanward.ehheater.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context, final ViewGroup parent) {
		super(context);
		mParent = parent;
	}

	private ViewGroup mParent;
	/** 是否停止滚动 */
	private boolean mStopMarquee;
	private String mText;
	private float mCoordinateX;
	private float mTextWidth;
	int y;

	public void setY(int y) {
		this.y = y;
	}

	public void setText(String text) {
		this.mText = text;
		mTextWidth = getPaint().measureText(mText);
		if (mHandler.hasMessages(0))
			mHandler.removeMessages(0);
		
		this.post(new Runnable() {

			@Override
			public void run() {
				if (mTextWidth > mParent.getWidth()) {
					mHandler.sendEmptyMessageDelayed(0, 1000);
				}
				
				Log.e("RUUNABLE得到的parent长度是:", mParent.getWidth() + "");
				Log.e("RUUNABLE得到的长度是:", getWidth() + "");
				Log.e("mTextWidth得到的长度是:", mTextWidth + "");
			}
		});
	}

	/**该方法没有被调用到*/
	@Override
	protected void onAttachedToWindow() {
		mStopMarquee = false;
		// if (!StringUtils.isEmpty(mText))
		mHandler.sendEmptyMessageDelayed(0, 1000);
		super.onAttachedToWindow();
	}

	public void startMove() {
		mStopMarquee = false;
		mHandler.sendEmptyMessageDelayed(0, 1000);

	}

	/**该方法没有被调用到*/
	@Override
	protected void onDetachedFromWindow() {
		mStopMarquee = true;
		if (mHandler.hasMessages(0))
			mHandler.removeMessages(0);
		super.onDetachedFromWindow();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// if (!StringUtils.isEmpty(mText))
		if (mText != null) {
			canvas.drawText(mText, mCoordinateX, y, getPaint());
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (Math.abs(mCoordinateX) > (mTextWidth + 100)) {
					mCoordinateX = 0;
					invalidate();
					if (!mStopMarquee) {
						sendEmptyMessageDelayed(0, 2000);
					}
				} else {
					mCoordinateX -= 5;
					// Log.e("move", mCoordinateX + "");
					invalidate();
					if (!mStopMarquee) {
						sendEmptyMessageDelayed(0, 30);
					}
				}

				break;
			}
			super.handleMessage(msg);
		}
	};

}