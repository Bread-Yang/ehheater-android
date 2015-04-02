package com.vanward.ehheater.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.vanward.ehheater.R;
import com.vanward.ehheater.util.L;

public class BaoBarView extends View {

	private static final String TAG = "BaoBarView";

	public interface BaoBarViewAdapter {
		/** 有多少柱子 */
		public int numberOfBarInBarView(BaoBarView barView);

		/** 返回index所在柱子的值.BarView将根据最大值和最小值计算高度 */
		public float valueOfIndex(BaoBarView barView, int index);

		/** 横坐标刻度 */
		public String xAxisTitleOfIndex(BaoBarView barView, int index);
	}

	public static enum BaoTouchArea {
		BaoTouch_None, BaoTouch_SetValue, BaoTouch_Scroll
	};

	private Context context;

	private float limitMinValue;
	private float limitMaxValue;
	private float xOffset;
	private BaoBarViewAdapter adapter;

	private UIEdgeInsets plotArea;
	private float yAxisWidth;
	private float xAxisWidth;
	private float yGridPad; // Y轴网格间隔
	private float xGridPad; // X轴网格间隔

	private int barCount; // 每次重画, 就向adapter重新拿

	private Paint xAxisPaint;
	private Paint yAxisPaint;
	private Paint gridPaint;

	private int xAxisColorResId = R.color.orange;
	private int yAxisColorResId = R.color.orange;

	private Paint xLabelPaint;
	private Paint yLabelPaint;

	private int xLabelColorResId = R.color.appointment_text_color;
	private int yLabelColorResId = R.color.appointment_text_color;

	private Paint barPaint;

	private int barColorResId = R.color.orange;

	// pragma mark -- init

	public BaoBarView(Context context) {
		super(context);
		this.context = context;
		configureDefaultProperty();
	}

	public BaoBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		configureDefaultProperty();
	}

	public BaoBarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		configureDefaultProperty();
	}

	private void configureDefaultProperty() {
		xAxisPaint = new Paint();
		xAxisPaint.setAntiAlias(true);
		xAxisPaint.setStrokeWidth(dp2Pixel(1));
		// xAxisPaint.setColor(context.getResources().getColor(xAxisColorResId));

		yAxisPaint = new Paint();
		yAxisPaint.setAntiAlias(true);
		// yAxisPaint.setColor(context.getResources().getColor(yAxisColorResId));

		gridPaint = new Paint();
		gridPaint.setAntiAlias(true);
		gridPaint.setColor(context.getResources().getColor(R.color.gray));

		xLabelPaint = new Paint();
		xLabelPaint.setAntiAlias(true);
		xLabelPaint.setColor(context.getResources().getColor(xLabelColorResId));
		xLabelPaint.setTextSize(dp2Pixel(15));

		yLabelPaint = new Paint();
		yLabelPaint.setAntiAlias(true);
		yLabelPaint.setColor(context.getResources().getColor(yLabelColorResId));
		yLabelPaint.setTextSize(dp2Pixel(13));

		barPaint = new Paint();
		barPaint.setStrokeWidth(1);

		float top = dp2Pixel(40);
		float left = dp2Pixel(35);
		float bottom = dp2Pixel(45);
		float right = dp2Pixel(35);
		plotArea = new UIEdgeInsets(top, left, bottom, right);

		this.limitMaxValue = 80;
		this.limitMinValue = 0;
		this.xOffset = 0;

		setBackgroundColor(Color.WHITE);
	}

	// pragma mark -- private method

	private void updateGridPad() {
		float valuePad = this.limitMaxValue - this.limitMinValue;
		float pad = 5; // Y轴上5度为一格
		int yNum = (int) Math.ceil((valuePad / pad));
		CGSize plotSize = plotSize();
		float padH = plotSize.height / yNum;
		yGridPad = padH;

		int xNum = 20; // 二十个点 + 一个标题
		float padW = (plotSize.width) / xNum;
		xGridPad = padW;
	}

	private CGSize plotSize() {
		CGSize size = new CGSize(this.getWidth() - this.plotArea.left
				- this.plotArea.right, this.getHeight() - this.plotArea.top
				- this.plotArea.bottom);
		return size;
	}

	private void drawText(Canvas canvas, String text, float x, float y,
			Paint paint, float angle) {
		canvas.save();
		if (angle != 0) {
			canvas.rotate(angle, x, y);
		}
		canvas.drawText(text, x, y, paint);
		canvas.restore();
	}

	/**
	 * 根据参数point判断触摸的点属于柱状图中的哪个区域。point必须是BaoBarView里的坐标
	 * 
	 * @param point
	 * @return
	 */
	public BaoTouchArea touchAreaOfPoint(CGPoint point) {
		BaoTouchArea area = BaoTouchArea.BaoTouch_None;
		CGSize plotSize = plotSize();
		CGRect settingArea = new CGRect(this.plotArea.left, this.plotArea.top
				- dp2Pixel(5), plotSize.width, plotSize.height);
		CGRect scrollArea = new CGRect(this.plotArea.left, getHeight()
				- this.plotArea.bottom, plotSize.width, this.plotArea.bottom);

		if (scrollArea.toRect().contains((int) point.x, (int) point.y)) {
			area = BaoTouchArea.BaoTouch_Scroll;
		} else if (settingArea.toRect().contains((int) point.x, (int) point.y)) {
			area = BaoTouchArea.BaoTouch_SetValue;
		}
		return area;
	}

	/**
	 * 根据BaoBarView中的位置坐标point，计算出触摸点到横坐标的高度，从而计算出此高度的值。
	 * 
	 * @param point
	 * @return
	 */
	public float valueOfTouchPoint(CGPoint point) {
		float height = getHeight() - point.y - this.plotArea.bottom;
		height = (height < 0) ? 0 : height;
		float result = valueOfHeight(height);
		if (result < limitMinValue) {
			result = 30;
		} else if (result > limitMaxValue) {
			result = limitMaxValue;
		}
		return result;
	}

	/**
	 * 根据BaoBarView中的位置坐标point，计算出触摸点到纵坐标的距离，从而计算出此点在数据源的那个位置。index由0开始
	 * 
	 * @param point
	 * @return
	 */
	public int indexOfTouchPoint(CGPoint point) {
		float distance = point.x - this.plotArea.left;
		distance = (distance < 0) ? 0 : distance;

		float padW = this.xGridPad;
		float realDistance = distance + this.xOffset;
		int result = (int) Math.floor(realDistance / padW);
		return result;
	}

	/**
	 * 数值对应的高度
	 * 
	 * @param value
	 * @return
	 */
	public float heightOfValue(float value) {
		float pad = Math.abs(this.limitMaxValue - this.limitMinValue);
		float tValue = value - this.limitMinValue;
		CGSize plotSize = plotSize();
		float totalH = plotSize.height;
		float result = tValue / pad * totalH;
		return result;
	}

	/**
	 * 高度对应的值.height为柱状的高度。
	 * 
	 * @param height
	 * @return
	 */
	public float valueOfHeight(float height) {
		float pad = Math.abs(this.limitMaxValue - this.limitMinValue);
		CGSize plotSize = plotSize();
		float totalH = plotSize.height - 2 * this.yGridPad;
		float result = pad * height / totalH + this.limitMinValue;
		return result;
	}

	// pragma mark -- draw

	/**
	 * 画X轴
	 * 
	 * @param canvas
	 */
	private void drawXAxisOfContext(Canvas canvas) {
		float width = 1;

		CGPoint startPoint = new CGPoint(this.plotArea.left,
				(getHeight() - this.plotArea.bottom));
		CGPoint endPoint = new CGPoint((getWidth() - this.plotArea.right),
				startPoint.y);

		canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
				xAxisPaint);

		// 画箭头
		// canvas.drawLine(endPoint.x - dp2Pixel(7), endPoint.y - dp2Pixel(3),
		// endPoint.x, endPoint.y, xAxisPaint);
		//
		// canvas.drawLine(endPoint.x - dp2Pixel(7), endPoint.y + dp2Pixel(3),
		// endPoint.x, endPoint.y, xAxisPaint);
	}

	/**
	 * 画Y轴
	 * 
	 * @param canvas
	 */
	private void drawYAxisOfContext(Canvas canvas) {
		float width = 1;

		CGPoint startPoint = new CGPoint(this.plotArea.left, this.plotArea.top);
		CGPoint endPoint = new CGPoint(startPoint.x, getHeight()
				- this.plotArea.bottom);

		// canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
		// yAxisPaint);

		// 画箭头
		// canvas.drawLine(startPoint.x - dp2Pixel(3), startPoint.y +
		// dp2Pixel(7),
		// startPoint.x, startPoint.y, xAxisPaint);
		//
		// canvas.drawLine(startPoint.x + dp2Pixel(3), startPoint.y +
		// dp2Pixel(7),
		// startPoint.x, startPoint.y, xAxisPaint);
	}

	/**
	 * 画X轴方向的网格
	 * 
	 * @param canvs
	 */
	private void drawXAxisGrid(Canvas canvas) {
		float padW = this.xGridPad;
		int startIndex = (int) Math.floor(this.xOffset / padW);
		float pIndex = (float) Math.ceil(this.xOffset / padW);

		float startX = padW * pIndex - this.xOffset;

		float xValue = this.plotArea.left + startX;
		float endLie = (getWidth() - this.plotArea.right);
		int titleIndex = 0;
		float titleStartX = (pIndex < 1) ? this.plotArea.left - startX
				: this.plotArea.left - (padW - startX);
		int titleValue = startIndex;
		while (xValue < endLie + padW) {
			if (xValue > endLie) {
				xValue = endLie;
			}
			// 横坐标网格
			CGPoint startPoint = new CGPoint(xValue, this.plotArea.top);
			CGPoint endPoint = new CGPoint(xValue, getHeight()
					- this.plotArea.bottom);
			canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
					gridPaint);
			xValue += padW;

			// 横坐标刻度
			String string = "";
			if (adapter != null) {
				string = adapter.xAxisTitleOfIndex(this, titleValue);
				if (null == string) {
					string = "";
				}
			}
			CGRect rect = new CGRect(titleStartX + titleIndex * padW,
					getHeight() - this.plotArea.bottom, padW,
					this.plotArea.bottom);
			if (rect.x < endLie) {
				Rect bounds = new Rect();
				xLabelPaint.getTextBounds(string, 0, string.length(), bounds);
				CGPoint drawPoint = new CGPoint((titleStartX + titleIndex
						* padW)
						+ padW / 5.0f, (getHeight() - this.plotArea.bottom)
						+ dp2Pixel(5));
				if (drawPoint.x >= this.plotArea.left) {
					drawText(canvas, string, drawPoint.x, drawPoint.y,
							xLabelPaint, 90);
				}
			}
			titleIndex++;
			titleValue++;
		}

		// 横坐标右侧标题
		String string = "时间";
		CGPoint drawPoint = new CGPoint((endLie + 10.0f),
				(getHeight() - this.plotArea.bottom) + dp2Pixel(5));
		// drawText(canvas, string, drawPoint.x, drawPoint.y, xLabelPaint, 90);
	}

	/**
	 * 画Y轴方向的网格
	 * 
	 * @param canvas
	 */
	private void drawYAxisGrid(Canvas canvas) {
		float valuePad = this.limitMaxValue - this.limitMinValue;
		float pad = 5f;
		int num = (int) (Math.ceil(valuePad / pad));
		float padH = this.yGridPad;
		
		// 画y轴标题
		yLabelPaint.setColor(context.getResources().getColor(R.color.orange));
		drawText(canvas, "℃", dp2Pixel(10), getHeight() - this.plotArea.bottom - ((num + 1) * padH) + dp2Pixel(3),
				yLabelPaint, 0);
		
		yLabelPaint.setColor(context.getResources().getColor(yLabelColorResId));
		for (int i = 0; i <= num; i++) {
			float yValue = getHeight() - this.plotArea.bottom - (i * padH);
			CGPoint startPoint = new CGPoint(this.plotArea.left, yValue);
			CGPoint endPoint = new CGPoint(getWidth() - this.plotArea.right,
					yValue);
			canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
					gridPaint);

			if (i >= 0 && i <= num) {
				String string = (int)limitMinValue + i * 5 + "";

				CGRect rect = new CGRect(0, yValue, this.plotArea.left, padH);
				Rect bounds = new Rect();
				yLabelPaint.getTextBounds(string, 0, string.length(), bounds);
				Rect drawRect = new Rect(
						(int) ((rect.width - bounds.width()) / 2.0),
						(int) rect.y, (int) bounds.width(),
						(int) bounds.height());
				// NSString *string = [NSString
				// stringWithFormat:@"%i℃",(i-1)*5];

				FontMetricsInt fontMetrics = yLabelPaint.getFontMetricsInt();
				int baseline = drawRect.top
						+ (drawRect.bottom - drawRect.top - fontMetrics.bottom + fontMetrics.top)
						/ 2 - fontMetrics.top;
				// canvas.drawText(string, drawRect.centerX(), baseline,
				// yLabelPaint);
				drawText(canvas, string, dp2Pixel(10), yValue + dp2Pixel(3),
						yLabelPaint, 0);
				// UIFont *font = [UIFont systemFontOfSize:13];
				// CGSize strSize = [string sizeWithFont:font
				// constrainedToSize:rect.size
				// lineBreakMode:NSLineBreakByTruncatingTail];
				// CGRect drawRect = CGRectMak e((rect.size.width -
				// strSize.width) / 2.0, rect.origin.y, strSize.width,
				// strSize.height);
				// [string drawInRect:drawRect withFont:font];
			}
		}
		// [[UIColor lightGrayColor] set];
		// CGContextSetLineWidth(context, 1);
		// CGContextStrokePath(context);
		// CGContextRestoreGState(context);
	}

	private void drawBar(Canvas canvas) {
		float padW = this.xGridPad;
		int startIndex = (int) Math.floor(this.xOffset / padW);
		int pIndex = (int) Math.ceil(this.xOffset / padW);
		float padX = (pIndex < 1) ? padW : (padW * pIndex - this.xOffset);
		float endX = this.plotArea.left + padX;
		float startX = this.plotArea.left;
		float endLie = (getWidth() - this.plotArea.right);

		// CGContextSaveGState(context);
		// UIColor *fillColor = [UIColor cyanColor];
		// UIColor *borderColor = [UIColor lightGrayColor];
		// CGContextSetFillColorWithColor(context, fillColor.CGColor);
		// CGContextSetStrokeColorWithColor(context, borderColor.CGColor);
		while (startX < endLie) {
			float value = 0f;
			if (startIndex < this.barCount) {
				if (adapter != null) {
					value = adapter.valueOfIndex(this, startIndex);
				}
			}
			if (value < 30) {
				value = 30;
			} else if (value > limitMaxValue) {
				value = limitMaxValue;
			}
			float height = heightOfValue(value);
			float orignalY = getHeight() - this.plotArea.bottom - height;
			float width = endX - startX;
			float orignalX = startX;
			CGRect rect = new CGRect(orignalX, orignalY, width, height);
			barPaint.setStyle(Paint.Style.FILL);
			barPaint.setColor(context.getResources().getColor(barColorResId));
			canvas.drawRect(rect.x, rect.y, rect.x + rect.width, rect.y
					+ rect.height, barPaint);
			barPaint.setStyle(Paint.Style.STROKE);
			barPaint.setColor(Color.WHITE);
			canvas.drawRect(rect.x, rect.y, rect.x + rect.width, rect.y
					+ rect.height, barPaint);

			// CGContextFillRect(context, rect);
			// CGContextStrokeRect(context, rect);
			startX = endX;
			endX += padW;
			if (endX > endLie) {
				endX = endLie;
			}
			startIndex++;
		}
		// CGContextRestoreGState(context);
	}

	public void drawLine(Canvas canvas) {
		Paint linePaint = new Paint();
		linePaint.setColor(Color.argb(255, 128, 128, 128));
		canvas.drawRect(0, 0, getWidth(), getHeight(), linePaint);
		linePaint.setAntiAlias(true);
		linePaint.setColor(Color.argb(255, 154, 154, 154));
		for (int i = 0; i < getWidth() / 6; i++) { // draw y grid
			canvas.drawLine(6 * i, 0, 6 * i, getHeight(), linePaint);
		}
		for (int i = 0; i < getHeight() / 6; i++) { // draw x grid
			canvas.drawLine(0, 6 * i, getWidth(), 6 * i, linePaint);
		}
	}

	// pragma mark -- override method

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		updateGridPad();

		if (adapter == null) {
			this.barCount = 0;
		} else {
			this.barCount = this.adapter.numberOfBarInBarView(this);
		}

		// 画网格
		drawXAxisGrid(canvas);
		drawYAxisGrid(canvas);

		// 画柱状
		drawBar(canvas);

		// 画轴
		drawXAxisOfContext(canvas);
		drawYAxisOfContext(canvas);
	}

	// pragma mark -- getter and setter

	public UIEdgeInsets getPlotArea() {
		return plotArea;
	}

	public void setPlotArea(UIEdgeInsets plotArea) {
		this.plotArea = plotArea;
		updateGridPad();
		invalidate();
	}

	public float getLimitMinValue() {
		return limitMinValue;
	}

	public void setLimitMinValue(float limitMinValue) {
		this.limitMinValue = limitMinValue;
		invalidate();
	}

	public float getLimitMaxValue() {
		return limitMaxValue;
	}

	public void setLimitMaxValue(float limitMaxValue) {
		this.limitMaxValue = limitMaxValue;
		invalidate();
	}

	public float getxOffset() {
		return xOffset;
	}

	public void setxOffset(float xOffset) {
		L.e(this, "xoffset : " + xOffset);
		L.e(this, "xGridPad : " + xGridPad);
		L.e(this, "plotArea.right - plotArea.left : "
				+ (plotArea.right - plotArea.left));
		if (xOffset + getWidth() - plotArea.right - plotArea.left > xGridPad * 48) {
			return;
		}
		this.xOffset = xOffset;
		invalidate();
	}

	public BaoBarViewAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(BaoBarViewAdapter adapter) {
		this.adapter = adapter;
	}

	// pragma mark -- custom class

	public static class CGPoint {
		public float x, y;

		public CGPoint(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	public static class CGRect {
		private float x, y, width, height;

		public CGRect(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public Rect toRect() {
			return new Rect((int) x, (int) y, (int) (x + width),
					(int) (y + height));
		}
	}

	public static class CGSize {
		private float width, height;

		public CGSize(float width, float height) {
			this.width = width;
			this.height = height;
		}
	}

	public static class UIEdgeInsets {
		private float top, left, bottom, right;

		public UIEdgeInsets(float top, float left, float bottom, float right) {
			this.top = top;
			this.left = left;
			this.bottom = bottom;
			this.right = right;
		}
	}

	// pragma mark -- Utility

	/**
	 * dp转px(公式 : px = dp * scale + 0.5f)
	 * 
	 * @param dp
	 * @return
	 */
	private float dp2Pixel(int dp) {
		float scale = this.getResources().getDisplayMetrics().density;
		return dp * scale + 0.5f;
	}
}
