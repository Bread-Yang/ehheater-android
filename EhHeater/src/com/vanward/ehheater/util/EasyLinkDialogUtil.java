package com.vanward.ehheater.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.vanward.ehheater.R;

public class EasyLinkDialogUtil {

	public static Dialog initEasyLinkDialog(Activity activity, View.OnClickListener canceler) {

		Dialog dialog_easylink = new Dialog(activity, R.style.custom_dialog);
		dialog_easylink.setContentView(R.layout.dialog_easylink_layout);
		dialog_easylink.setCancelable(false);
		dialog_easylink.setCanceledOnTouchOutside(false);
		dialog_easylink.findViewById(R.id.del_btn_cancel).setOnClickListener(canceler);
		
		return dialog_easylink;
	}

	public static Dialog initWaitWifiDialog(Activity activity, View.OnClickListener canceler) {

		Dialog dialog = new Dialog(activity, R.style.custom_dialog);
		dialog.setContentView(R.layout.dialog_easylink_layout);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.findViewById(R.id.easylink_time_tv).setVisibility(View.GONE);
		dialog.findViewById(R.id.del_tv_second).setVisibility(View.GONE);
		dialog.findViewById(R.id.del_btn_cancel).setOnClickListener(canceler);
		
		return dialog;
	}
}
