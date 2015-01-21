package com.vanward.ehheater.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vanward.ehheater.activity.main.MainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceMainActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;

public class ErrorUtils extends Activity {

	private static final String TAG = "ErrorUtils";

	public static boolean isGasMainActivityActive, isMainActivityActive,
			isFurnaceMainActivityActive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "ErrorUtils的onCreate()执行了");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "ErrorUtils的onResume()执行了");
		Intent originalIntent = getIntent();
		HeaterType device_type = (HeaterType) originalIntent
				.getSerializableExtra("device_type");
		boolean isGas = originalIntent.getBooleanExtra("isGas", false);

		Log.e(TAG, "device_type : " + device_type);
		Log.e(TAG, "isGas : " + isGas);
		Log.e(TAG, "isGasMainActivityActive : " + isGasMainActivityActive);
		Log.e(TAG, "isMainActivityActive : " + isMainActivityActive);
		Log.e(TAG, "isFurnaceMainActivityActive : " + isFurnaceMainActivityActive);

		switch (device_type) {
		case Eh: // 电热
			if (isGasMainActivityActive) { // 如何当前是处于燃热或壁挂炉的主控界面,则从燃热或壁挂炉主控界面切换到电热主控界面
				originalIntent.setClass(this, GasMainActivity.class);
			} else if (isFurnaceMainActivityActive) {
				originalIntent.setClass(this, FurnaceMainActivity.class);
			} else {
				originalIntent.setClass(this, MainActivity.class);
				if (!isMainActivityActive) {
					originalIntent.putExtra("newActivity", true);
				}
			}
			break;

		case ST: // 燃热
			if (isMainActivityActive) { // 如何当前是处于电热或壁挂炉的主控界面,则从电热或壁挂炉主控界面切换到燃热主控界面
				originalIntent.setClass(this, MainActivity.class);
			} else if (isFurnaceMainActivityActive) {
				originalIntent.setClass(this, FurnaceMainActivity.class);
			} else {
				originalIntent.setClass(this, GasMainActivity.class);
				if (!isGasMainActivityActive) {
					originalIntent.putExtra("newActivity", true);
				}
			}
			break;

		case EH_FURNACE: // 壁挂炉
			if (isMainActivityActive) {
				originalIntent.setClass(this, MainActivity.class);
			} else if (isGasMainActivityActive) {
				originalIntent.setClass(this, GasMainActivity.class);
			} else {
				originalIntent.setClass(this, FurnaceMainActivity.class);
				if (!isFurnaceMainActivityActive) {
					originalIntent.putExtra("newActivity", true);
				}
			}
			break;

		}

		startActivity(originalIntent);
		finish();

	}
}
