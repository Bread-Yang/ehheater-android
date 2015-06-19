package com.vanward.ehheater.util;

import net.tsz.afinal.http.AjaxCallBack;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class BaoDialogShowUtil {

	public static int DEFAULT_RESID = -1;

	private static BaoDialogShowUtil instance;

	private Context mContext;

	public static BaoDialogShowUtil getInstance(Context context) {
		if (instance == null) {
			synchronized (BaoDialogShowUtil.class) {
				if (instance == null) {
					instance = new BaoDialogShowUtil(context);
				}
			}
		} else {
			instance.mContext = context;
		}
		return instance;
	}

	private BaoDialogShowUtil(Context context) {
		this.mContext = context;
	}

	public BaoTimeoutDailog createLoadingDialog() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		RelativeLayout relativeLayout = (RelativeLayout) layoutInflater
				.inflate(R.layout.loadingdialog, null);

		TextView tvContent = (TextView) relativeLayout
				.findViewById(R.id.tv_loading);
		tvContent.setText("");
		tvContent.setVisibility(View.VISIBLE);
		BaoTimeoutDailog dialog = new BaoTimeoutDailog(mContext,
				R.style.dialogStyle);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.setContentView(relativeLayout);
		return dialog;
	}

	public BaoTimeoutDailog createDialogWithTwoButton(int contentResId,
			int leftButtonResId, int rightButtonResId,
			OnClickListener leftButtonClickListener,
			OnClickListener rightButtonClickListener) {
		final BaoTimeoutDailog dialog = new BaoTimeoutDailog(mContext,
				R.style.custom_dialog);
		dialog.setContentView(R.layout.dialog_common_two_button);

		TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
		tv_content.setText(contentResId);

		Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
		if (leftButtonResId != DEFAULT_RESID) {
			btn_cancel.setText(leftButtonResId);
		}
		if (leftButtonClickListener != null) {
			btn_cancel.setOnClickListener(leftButtonClickListener);
		} else {
			btn_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}

		Button btn_confirm = (Button) dialog.findViewById(R.id.btn_confirm);
		if (rightButtonResId != DEFAULT_RESID) {
			btn_confirm.setText(rightButtonResId);
		}
		if (rightButtonClickListener != null) {
			btn_confirm.setOnClickListener(rightButtonClickListener);
		} else {
			btn_confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
		return dialog;
	}

	public BaoTimeoutDailog createDialogWithOneButton(int contentResId,
			int buttonResId, OnClickListener buttonClickListener) {
		final BaoTimeoutDailog dialog = new BaoTimeoutDailog(mContext,
				R.style.custom_dialog);
		dialog.setContentView(R.layout.dialog_common_one_button);

		TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
		tv_content.setText(contentResId);

		Button btn_close = (Button) dialog.findViewById(R.id.btn_close);
		if (buttonResId != DEFAULT_RESID) {
			btn_close.setText(buttonResId);
		}
		if (buttonClickListener != null) {
			btn_close.setOnClickListener(buttonClickListener);
		} else {
			btn_close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}

		return dialog;
	}

	static class BaoTimeoutDailog extends Dialog {

		private CountDownTimer countDown;

		private boolean isDetach = false;

		public BaoTimeoutDailog(Context context) {
			super(context);
		}

		public BaoTimeoutDailog(Context context, boolean cancelable,
				OnCancelListener cancelListener) {
			super(context, cancelable, cancelListener);
		}

		public BaoTimeoutDailog(Context context, int theme) {
			super(context, theme);
		}

		@Override
		public void show() {
			countDown = new CountDownTimer(30000, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {

				}

				@Override
				public void onFinish() {
					if (!isDetach) {
						dismiss();
					}
					countDown = null;
				}
			};
			countDown.start();
			super.show();
		}

		public void show(final AjaxCallBack callBack) {
			countDown = new CountDownTimer(30000, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {

				}

				@Override
				public void onFinish() {
					if (!isDetach) {
						if (isShowing()) {
							dismiss();
						}
						callBack.onTimeout();
					}
					countDown = null;
				}
			};
			countDown.start();
			super.show();
		}

		@Override
		public void dismiss() {
			if (countDown != null) {
				countDown.cancel();
				countDown = null;
			}
			super.dismiss();
		}

		@Override
		public void onDetachedFromWindow() {
			super.onDetachedFromWindow();
			isDetach = true;
		};
	}
}
