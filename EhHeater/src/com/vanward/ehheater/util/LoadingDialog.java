package com.vanward.ehheater.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


/**
 * 加载loading框
 * 
 * @author genesis
 * 
 */

public class LoadingDialog {

	ProgressDialog mypDialog;
	Context context;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
//				Toast.makeText(context, context.getString(R.string.timeout),
//						1000).show();
//				if (mypDialog != null) {
//					mypDialog.dismiss();
//					mypDialog = null;
//				}
			}
		}
	};

	public boolean isShowing() {
		if (mypDialog == null)
			return false;
		return mypDialog.isShowing();
	}

	public void show(Context context, String text, boolean cancelable,
			boolean isButton,
			DialogInterface.OnClickListener buttonClickListener) {
		show(context, text, cancelable, isButton, buttonClickListener, null);
	}

	public void show(Context context, String text, boolean cancelable,
			boolean isButton,
			DialogInterface.OnClickListener buttonClickListener,
			DialogInterface.OnCancelListener oncancleListener) {
		this.dismiss();
		this.context = context;
		try {
			mypDialog = null;
			mypDialog = new ProgressDialog(context);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage(text);
			mypDialog.setIndeterminate(false);
			mypDialog.setCancelable(true);
			mypDialog.setOnCancelListener(oncancleListener);
			String s = "取消";
			if (isButton) {
				mypDialog.setButton(s, buttonClickListener);
			}
			mypDialog.show();

		} catch (Exception e) {
			// TODO: handle exception
		}
		handler.sendEmptyMessageDelayed(0, 50000);
	}

	public void dismiss() {

		if (mypDialog != null) {
			mypDialog.dismiss();
			handler.removeMessages(0);
			mypDialog = null;
		}
	}

}
