package com.vanward.ehheater.view;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import com.vanward.ehheater.R;

//@SuppressLint("HandlerLeak")
public class CircularView extends View {
	public boolean isHeat() {
		return isHeat;
	}

	public void setHeat(boolean isHeat) {
		this.isHeat = isHeat;
	}

	CircleListener circularListener;

	final static int MOVE_IN = 1;
	final static int MOVE_OUT = 2;
	boolean isFromMyView;

	final static int UpdateUIToSet = 0, UpdateUIToDefault = 2, SentToMsg = 1,
			UpdateUILocalNumDiff = 3;

	int moveCircular;
	boolean islock;
	float endX;
	Bitmap bgBmp;
	Bitmap outBmp, redoutBmp, blueoutBmp, dianbmp;
	private Paint mPaint;
	// private SurfaceHolder mHolder;
	View view;
	int rx, ry, r;
	double bmSize;
	int degree = 0, olddegree = 0;
	int targerdegree = 0;
	RectF rectF, inRectF;
	int type;
	int bmpOffset;
	int needleW;
	boolean isOn;
	public static final int CIRCULAR_SINGLE = 1;
	public static final int CIRCULAR_DOUBLE = 2;
	private Path mPath;

	public void setCircularListener(CircleListener circularListener) {
		this.circularListener = circularListener;
	}

	public int angleToDegree(float angle) {
		angle = angle - 25;
		return (int) (angle * 360 / angledegree);
	}

	public int perangle = 6;
	public int angledegree = 60;
	public int beginangle = 35;
	public int endangle = 75;

	public int getEndangle() {
		return endangle;
	}

	public void setEndangle(int endangle) {
		this.endangle = endangle;
	}

	private CircularView(Context context) {
		super(context);
	}

	public float degreeToAngle(int degree) {
		float f = (degree * angledegree / 360);
		int temp = (degree * angledegree / 360);
		if (f - temp > 0) {
			temp = temp + 1;
		}
		System.out.println("degreeToAngle : " + (degree * angledegree / 360)
				+ "temp： " + temp);
		temp = temp + 25;
		return temp;
	}

	// Type
	public CircularView(Context context, View view, int Type, int startAngel) {
		super(context);
		this.degree = startAngel;
		mPaint = new Paint();
		mPaint.setStyle(Style.FILL);
		mPaint.setAntiAlias(true);// 抗锯齿
		mPaint.setColor(Color.WHITE);
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		bgBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.home_yuan_tiao3);
		dianbmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.home_yuan_tiao_dian);
		outBmp = dianbmp;
		redoutBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.home_yuan_tiao);
		blueoutBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.home_yuan_tiao_lan);
		// Shader shader = new BitmapShader(bgBmp, Shader.TileMode.CLAMP,
		// Shader.TileMode.CLAMP);
		needleW = outBmp.getWidth();
		bmSize = Math.sqrt(2) * needleW;
		mPaint.setStrokeWidth(needleW / 4);
		// mPaint.setShader(shader);
		this.view = view;
		this.type = Type;
		r = view.getHeight() > view.getWidth() ? view.getWidth() / 2 : view
				.getHeight() / 2;
		rx = view.getWidth() / 2;
		ry = view.getHeight() / 2;
		rectF = new RectF(needleW / 2, needleW / 2, view.getWidth() - needleW
				/ 2, view.getHeight() - needleW / 2);
		inRectF = new RectF(needleW * 2, needleW * 2, view.getWidth() - needleW
				* 2, view.getHeight() - needleW * 2);
		// mLinearGradient = new LinearGradient(rx, 0, rx, view.getHeight(),
		// new int[] {
		// context.getResources().getColor(R.color.title_color),
		// Color.parseColor("#FF8C69") }, null,
		// Shader.TileMode.REPEAT);
		mPath = new Path();

	}

	public boolean isOn() {
		return isOn;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			// 把界面更新成设定温度
			case UpdateUIToSet:
				postInvalidate();
				circularListener.updateUIListener((int) degreeToAngle(degree));
				break;

			// 发送指令。
			case SentToMsg:
				postInvalidate();
				circularListener.levelListener((int) degreeToAngle(degree));
				// postInvalidate();
				// circularListener.levelListener((int) degreeToAngle(degree));
				break;

			// 还原成当前温度
			case UpdateUIToDefault:

				postInvalidate();
				circularListener
						.updateUIWhenAferSetListener((int) degreeToAngle(degree));
				break;

			// 圆圈位置跟设定的温度一致时的回调.,把圆圈中的数字 跟 圆圈的位置分开。
			case UpdateUILocalNumDiff:
				postInvalidate();
				circularListener
						.updateLocalUIdifferent((int) degreeToAngle(degree));
				break;

			}
			super.handleMessage(msg);
		}

	};

	public boolean isCanUpadateAndSetMinMax(int degree) {
		if (degreeToAngle(degree) <= beginangle) {
			System.out.println("执行");
			setAngle(beginangle);
			return false;
		} else if (degreeToAngle(degree) >= endangle) {
			setAngle(endangle);
			return false;
		} else {
			return true;

		}
	}

	float downx = 0, downy = 0;
	boolean isclick = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isOn) {
			float x = 0;
			float y = 0;

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				System.out.println("test: down");
				isclick = true;
				downx = event.getX();
				downy = event.getY();
				x = event.getX();
				y = event.getY();

				if (isOnLine(x, y)) {
					moveCircular = MOVE_OUT;
					System.out.println("degree: " + degree);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				System.out.println("test: move");
				System.out.println("degree： " + degree);
				isCanUpadateAndSetMinMax(degree);
				x = event.getX();
				y = event.getY();
				if (Math.abs(downx - x) < 20 && Math.abs(downy - y) < 20) {
					// 这个是点击，move 全部不生效
					isclick = true;
					return true;
				} else {
					isclick = false;
				}
				if (islock) {
					if (degree == 355) {
						if (x > endX) {
							islock = false;
						}
					} else if (degree == 5) {
						if (x < endX) {
							islock = false;
						}
					}
				}

				if (!islock) {
					if (moveCircular == MOVE_OUT) {
						degree = (int) finalangel(x, y, angel(x, y));
						if (degree >= 355 || degree <= 5) {
							endX = x;
							islock = true;
						}
						if (isCanUpadateAndSetMinMax(degree)) {
							if (degree > olddegree) {
								outBmp = redoutBmp;
							} else if (degree < olddegree) {
								outBmp = blueoutBmp;
							}
							olddegree = degree;
							if (degree >= angleToDegree(beginangle)
									&& degree <= angleToDegree(endangle)) {
								heatmakeRange(degreeToAngle(degree));
								handler.sendEmptyMessage(UpdateUIToSet);
							}

						}
					}
				}

				break;
			case MotionEvent.ACTION_UP:
				System.out.println("test: up");
				islock = false;
				moveCircular = 0;
				outBmp = dianbmp;
				x = event.getX();
				y = event.getY();
				int tempdegree = (int) finalangel(x, y, angel(x, y));
				if (isclick) {
					if (tempdegree > 180 && degree < 360) {
						degree = angleToDegree(targerdegree) + perangle;
						// degree = degree + 6;
					} else if (tempdegree < 180 && degree > 0) {
						degree = angleToDegree(targerdegree) - perangle;
						// degree = degree - 6;
					} else {
						// degree = tempdegree;
					}
				}

				heatmakeRange(degreeToAngle(degree));
				if (degree >= angleToDegree(beginangle)
						&& degree <= angleToDegree(endangle)) {
					if (isclick) {
						handler.sendEmptyMessage(UpdateUIToSet);
					}
					handler.sendEmptyMessage(SentToMsg);
				}
			}
		}
		return true;
	}

	public int getTargerdegree() {
		return targerdegree;
	}

	public void setTargerdegree(int targerdegree) {
		this.targerdegree = targerdegree;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.save();
		setPath();
		canvas.clipPath(mPath); // if (degree != 0) {
		// canvas.drawBitmap(bgBmp, 0, 0, mPaint);

		canvas.restore();
		Matrix matrix = new Matrix();
		matrix.postTranslate(rx - needleW / 2, view.getHeight() - needleW
				+ bmpOffset);
		matrix.postRotate(degree, rx, ry);
		mPaint.setStrokeWidth(needleW / 2);
		canvas.drawBitmap(outBmp, matrix, mPaint);
		// RectF oval1=new RectF(,20,180,40);
	}

	public float angel(float x, float y) {
		float b = (float) Math.sqrt((rx - x) * (rx - x) + (ry - y) * (ry - y));
		float c = Math.abs(y - ry);
		float a = Math.abs(x - rx);
		float cos = (a * a + b * b - c * c) / (2 * a * b);
		float angel = (float) Math.acos(cos);

		return (float) (angel * 180 / Math.PI);
	}

	public float finalangel(float x, float y, float angel) {
		if (x < rx && y < ry) {
			return 90 + angel;
		} else if (x > rx && y < ry) {
			return 180 + (90 - angel);
		} else if (x > rx && y > ry) {
			return 270 + angel;
		} else {
			return 90 - angel;
		}
	}

	public boolean isOnLine(float x, float y) {
		double distance = Math.sqrt((x - rx) * (x - rx) + (y - ry) * (y - ry));
		if (distance > r - needleW + 10 && distance < r) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOnInLine(float x, float y) {
		double distance = Math.sqrt((x - rx) * (x - rx) + (y - ry) * (y - ry));
		if (distance > r - needleW * 2 && distance < r - needleW) {
			return true;
		} else {
			return false;
		}
	}

	public void setAngle(int level) {

		if (level == 0) {
			degree = 0;
		} else {
			degree = angleToDegree(level);
		}
		System.out.println("setAngle：  level=" + level + "degree=" + degree);
		if (level < 25) {
			degree = angleToDegree(25);
			handler.sendEmptyMessage(UpdateUILocalNumDiff);
		} else {
			handler.sendEmptyMessage(UpdateUIToDefault);
		}
	}

	boolean isHeat = false;

	public void heatmakeRange(float value) {
		if (isHeat) {
			if (value == 49) {
				degree = angleToDegree(50);
			}
			if (value > 49) {
				int[] rangs = { 50, 55, 60, 65 };
				for (int i = 0; i < rangs.length; i++) {
					if (Math.abs(value - rangs[i]) <= 3) {
						degree = angleToDegree(rangs[i]);
						break;
					}
				}
			}
		}

	}

	public void setPath() {
		mPath.reset();
		float eachCount = (float) r / 45;
		if (degree <= 45) {
			mPath.moveTo((float) (r - (degree * eachCount)), r * 2);
			mPath.lineTo(rx, ry);
			mPath.lineTo(rx, r * 2);
			mPath.close();
		}
		if (degree > 45 && degree <= 135) {
			mPath.moveTo(0, (float) (r * 2 - (degree - 45) * eachCount));
			mPath.lineTo(rx, ry);
			mPath.lineTo(rx, r * 2);
			mPath.lineTo(0, r * 2);
			mPath.close();
		}
		if (degree > 135 && degree <= 225) {
			mPath.moveTo((float) ((degree - 135) * eachCount), 0);
			mPath.lineTo(rx, ry);
			mPath.lineTo(rx, r * 2);
			mPath.lineTo(0, r * 2);
			mPath.lineTo(0, 0);
			mPath.close();
		}
		if (degree > 225 && degree <= 325) {
			mPath.moveTo(r * 2, (float) ((degree - 225) * eachCount));
			mPath.lineTo(rx, ry);
			mPath.lineTo(rx, r * 2);
			mPath.lineTo(0, r * 2);
			mPath.lineTo(0, 0);
			mPath.lineTo(r * 2, 0);
			mPath.close();
		}
		if (degree > 325 && degree <= 360) {
			mPath.moveTo((float) (r * 2 - (degree - 325) * eachCount - 20),
					r * 2);
			mPath.lineTo(rx, ry);
			mPath.lineTo(rx, r * 2);
			mPath.lineTo(0, r * 2);
			mPath.lineTo(0, 0);
			mPath.lineTo(r * 2, 0);
			mPath.lineTo(r * 2, r * 2);
			mPath.close();
		}
	}
}
