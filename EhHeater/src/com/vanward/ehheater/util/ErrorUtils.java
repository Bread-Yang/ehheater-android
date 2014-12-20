package com.vanward.ehheater.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.vanward.ehheater.activity.main.MainActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;

public class ErrorUtils extends Activity {

	public static boolean isGasMainActivityActive, isMainActivityActive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent originalIntent = getIntent();
		Intent sendIntent = new Intent();
		sendIntent.putExtra("errorCode",
				originalIntent.getShortExtra("errorCode", (short) 0));
		boolean isGas = originalIntent.getBooleanExtra("isGas", false);
		if (isGas) { // 燃热
			sendIntent.putExtra("gasIsFreezeProofingWarning", originalIntent
					.getBooleanExtra("gasIsFreezeProofingWarning", false));
			sendIntent
					.putExtra("gasIsOxygenWarning", originalIntent
							.getBooleanExtra("gasIsOxygenWarning", false));
			sendIntent.putExtra("gasMac",
					originalIntent.getStringExtra("gasMac"));

			if (isMainActivityActive) {  // 如何当前是出于电热界面,则从电热界面切换到燃热界面
				sendIntent.setClass(this, MainActivity.class);
				sendIntent.putExtra("swithchToGas", true);
			} else {
				sendIntent.setClass(this, GasMainActivity.class);
				if (!isGasMainActivityActive) {
					sendIntent.putExtra("newActivity", true);
				}
			}
				
		} else { // 电热
			sendIntent.putExtra("electicMac",
					originalIntent.getStringExtra("electicMac"));
			if (isGasMainActivityActive) { // 如何当前是出于燃热界面,则从燃热界面切换到电热界面
				sendIntent.setClass(this, GasMainActivity.class);
				sendIntent.putExtra("swithchToElectic", true);
			} else {
				sendIntent.setClass(this, MainActivity.class);
				if (!isMainActivityActive) {
					sendIntent.putExtra("newActivity", true);
				}
			}
		}
		startActivity(sendIntent);
		finish();
	}
}
