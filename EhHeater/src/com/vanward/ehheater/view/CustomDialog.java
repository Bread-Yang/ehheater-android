package com.vanward.ehheater.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.vanward.ehheater.R;

public class CustomDialog extends Dialog {
	private static int default_width = 230; // 默认宽度
	private static int default_height = 130;// 默认高度
	private static int MSG_DISMISS = 1;
	Context context;
	boolean isTimeOut = false;
	Handler handler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == MSG_DISMISS) {
				try {
					if (CustomDialog.this.isShowing()) {
						dismiss();
						Toast.makeText(getContext(), R.string.time_out, 1000)
								.show();
					}
				} catch (Exception e) {

				}
			}
		};
	};

	public CustomDialog(Context context, int style) {
		this(context, default_width, default_height, style, false);
		this.context = context;
	}
	
	public CustomDialog(Context context, int style, boolean isTimeOut) {
		this(context, default_width, default_height, style, isTimeOut);
		this.context = context;
	} 

	public CustomDialog(Context context, int width, int height, int style,
			boolean isTimeOut) {
		super(context, style);
		// set content
		// setContentView(layout);
		// set window params
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		// set width,height by density and gravity
		float density = getDensity(context);
		params.width = (int) (width * density);
		params.height = (int) (height * density);
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
		this.setCancelable(false);
		this.isTimeOut = isTimeOut;
	}

	private float getDensity(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
	}

	@Override
	public void show() {
		super.show();
		if (isTimeOut) {
			handler.sendEmptyMessageDelayed(MSG_DISMISS, 40000);
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		handler.removeMessages(MSG_DISMISS);
	}

}