package com.vanward.ehheater.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vanward.ehheater.activity.main.MainActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;

public class ErrorUtils extends Activity {

	private static final String TAG = "ErrorUtils";

	public static boolean isGasMainActivityActive, isMainActivityActive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG,  "ErrorUtils的onCreate()执行了");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "ErrorUtils的onResume()执行了");
		Intent originalIntent = getIntent();
		boolean isGas = originalIntent.getBooleanExtra("isGas", false);

		Log.e(TAG, "isGas : " + isGas);
		Log.e(TAG, "isGasMainActivityActive : " + isGasMainActivityActive);
		Log.e(TAG, "isMainActivityActive : " + isMainActivityActive);

		if (isGas) { // 燃热
			if (isMainActivityActive) { // 如何当前是出于电热界面,则从电热界面切换到燃热界面
				originalIntent.setClass(this, MainActivity.class);
			} else {
				originalIntent.setClass(this, GasMainActivity.class);
				if (!isGasMainActivityActive) {
					originalIntent.putExtra("newActivity", true);
				}
			}

		} else { // 电热
			if (isGasMainActivityActive) { // 如何当前是出于燃热界面,则从燃热界面切换到电热界面
				originalIntent.setClass(this, GasMainActivity.class);
			} else {
				originalIntent.setClass(this, MainActivity.class);
				if (!isMainActivityActive) {
					originalIntent.putExtra("newActivity", true);
				}
			}
		}
		startActivity(originalIntent);
		finish();

	}
}
