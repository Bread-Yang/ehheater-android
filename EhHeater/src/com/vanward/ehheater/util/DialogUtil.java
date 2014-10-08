package com.vanward.ehheater.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class DialogUtil {
	private static DialogUtil dialogUtil;
	private static Dialog dialog;
	
	private DialogUtil() {
		
	}

	public static DialogUtil instance() {// 实例化
		if (dialogUtil == null) {
			dialogUtil = new DialogUtil();
		}
		return dialogUtil;
	}

	/**
	 * 等待对话框
	 * 
	 * @param context
	 */
	public void showLoadingDialog(Context context, int content) {
		dismissDialog();
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		RelativeLayout relativeLayout = (RelativeLayout) layoutInflater
				.inflate(R.layout.loadingdialog, null);

		TextView tvContent = (TextView) relativeLayout
				.findViewById(R.id.tv_loading);
		tvContent.setText(content);
		tvContent.setVisibility(View.VISIBLE);
		dialog = new Dialog(context, R.style.dialogStyle);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(relativeLayout);
		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void showLoadingDialog(Context context, String content) {
		dismissDialog();
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		RelativeLayout relativeLayout = (RelativeLayout) layoutInflater
				.inflate(R.layout.loadingdialog, null);

		TextView tvContent = (TextView) relativeLayout
				.findViewById(R.id.tv_loading);
		tvContent.setText(content);
		tvContent.setVisibility(View.VISIBLE);
		dialog = new Dialog(context, R.style.dialogStyle);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(relativeLayout);
		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void showDialog() {
		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			try {
				dialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Dialog getDialog() {
		return dialog;
	}

	public static boolean getIsShowing() {
		if (dialog != null) {
			return dialog.isShowing();
		}
		return false;
	}

}