package com.vanward.ehheater.util;

import android.app.Dialog;
import android.content.Context;
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
	
	public Dialog createLoadingDialog() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		RelativeLayout relativeLayout = (RelativeLayout) layoutInflater
				.inflate(R.layout.loadingdialog, null);

		TextView tvContent = (TextView) relativeLayout
				.findViewById(R.id.tv_loading);
		tvContent.setText("");
		tvContent.setVisibility(View.VISIBLE);
		Dialog dialog = new Dialog(mContext, R.style.dialogStyle);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.setContentView(relativeLayout);
		return dialog;
	}

	public Dialog createDialogWithTwoButton(int contentResId,
			int leftButtonResId, int rightButtonResId,
			OnClickListener leftButtonClickListener,
			OnClickListener rightButtonClickListener) {
		final Dialog dialog = new Dialog(mContext, R.style.custom_dialog);
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

	public Dialog createDialogWithOneButton(int contentResId, int buttonResId,
			OnClickListener buttonClickListener) {
		final Dialog dialog = new Dialog(mContext, R.style.custom_dialog);
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
}
