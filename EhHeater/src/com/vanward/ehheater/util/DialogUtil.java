package com.vanward.ehheater.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
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
		showReconnectDialog(null, act);
	}

	public void showReconnectDialog(final Runnable onCancel, final Activity act) {
		dismissDialog();

		if (!NetworkStatusUtil.isConnected(act)) { // 没有wifi和没有蜂窝网络的时候
			dialog = BaoDialogShowUtil.getInstance(act)
					.createDialogWithOneButton(R.string.check_network,
							R.string.I_know, null);
		} else {
			dialog = new Dialog(act, R.style.custom_dialog);
			dialog.setContentView(R.layout.dialog_reconnect);
			dialog.findViewById(R.id.dr_btn_cancel).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (onCancel != null) {
								onCancel.run();
							}
							dialog.dismiss();
//							HeaterInfo curHeater = new HeaterInfoService(act
//									.getBaseContext())
//									.getCurrentSelectedHeater();
//							if (curHeater == null) {
//								return;
//							}
//							switch (new HeaterInfoService(act)
//									.getHeaterType(curHeater)) {
//							case ELECTRIC_HEATER:
//								generated.SendStateReq(Global.connectId);
//								break;
//							case GAS_HEATER:
//								generated.SendGasWaterHeaterMobileRefreshReq(Global.connectId);
//								break;
//							case FURNACE:
//								generated.SendDERYRefreshReq(Global.connectId);
//								break;
//
//							}
						}
					});
			dialog.findViewById(R.id.dr_btn_reconnect).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							HeaterInfo curHeater = new HeaterInfoService(act
									.getBaseContext())
									.getCurrentSelectedHeater();
							if (curHeater == null) {
								return;
							}
							String mac = curHeater.getMac();
							String passcode = curHeater.getPasscode();

							String userId = AccountService.getUserId(act);
							String userPsw = AccountService.getUserPsw(act);

							dialog.dismiss();
							ConnectActivity.connectToDevice(act, mac, passcode,
									userId, userPsw);

						}
					});
		}

		try {
			if (!act.isFinishing()) {
				dialog.show();
			}
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
		// if (dialog != null && dialog.isShowing()) {
		if (dialog != null) {
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