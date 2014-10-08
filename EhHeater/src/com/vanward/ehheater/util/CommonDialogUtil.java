package com.vanward.ehheater.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.WelcomeActivity;
import com.vanward.ehheater.activity.global.Consts;

public class CommonDialogUtil {
	
	private static Dialog reconnectDialog;

	public static Dialog showReconnectDialog(final Activity act) {
		reconnectDialog = new Dialog(act, R.style.custom_dialog);
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
				Intent intent = new Intent(act, WelcomeActivity.class);
				intent.putExtra(Consts.INTENT_EXTRA_FLAG_REENTER, true);
				intent.putExtra(Consts.INTENT_EXTRA_FLAG_AS_DIALOG, true);
				act.startActivity(intent);
			}
		});

		reconnectDialog.show();
		return reconnectDialog;
	}
	
	
	
}
