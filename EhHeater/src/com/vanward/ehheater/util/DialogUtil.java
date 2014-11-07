package com.vanward.ehheater.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.configure.ConnectActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;

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
	
	public void showQueryingDialog(Activity act) {
		dismissDialog();
		
		AlertDialog.Builder bder = new AlertDialog.Builder(act);
		bder.setMessage("正在查询热水器状态...");
		bder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// just do nothing
			}
		});
		
		dialog = bder.create();
		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showReconnectDialog(final Activity act) {
		final Dialog reconnectDialog = new Dialog(act, R.style.custom_dialog);
		reconnectDialog.setContentView(R.layout.dialog_reconnect);
		reconnectDialog.findViewById(R.id.dr_btn_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				reconnectDialog.dismiss();
			}
		});
		reconnectDialog.findViewById(R.id.dr_btn_reconnect).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				HeaterInfo curHeater = new HeaterInfoService(act.getBaseContext()).getCurrentSelectedHeater();
				String mac = curHeater.getMac();
				String passcode = curHeater.getPasscode();
				
				String userId = AccountService.getUserId(act);
				String userPsw = AccountService.getUserPsw(act);
				

				reconnectDialog.dismiss();
				ConnectActivity.connectToDevice(act, mac, passcode, userId, userPsw);
				
			}
		});

		
		dialog = reconnectDialog;
		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
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