package com.vanward.ehheater.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.vanward.ehheater.activity.main.electric.ElectricMainActivity;
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
		L.e(this, "ErrorUtils的onCreate()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		L.e(this, "ErrorUtils的onResume()");
		Intent originalIntent = getIntent();
		HeaterType device_type = (HeaterType) originalIntent
				.getSerializableExtra("device_type");

		L.e(this, "device_type : " + device_type);
		L.e(this, "isGasMainActivityActive : " + isGasMainActivityActive);
		L.e(this, "isMainActivityActive : " + isMainActivityActive);
		L.e(this, "isFurnaceMainActivityActive : " + isFurnaceMainActivityActive);
		L.e(this, "did : " + getIntent().getStringExtra("did"));

		switch (device_type) {
		case ELECTRIC_HEATER: // 电热
			if (isGasMainActivityActive) { // 如何当前是处于燃热或壁挂炉的主控界面,则从燃热或壁挂炉主控界面切换到电热主控界面
				originalIntent.setClass(this, GasMainActivity.class);
			} else if (isFurnaceMainActivityActive) {
				originalIntent.setClass(this, FurnaceMainActivity.class);
			} else {
				originalIntent.setClass(this, ElectricMainActivity.class);
				if (!isMainActivityActive) {
					originalIntent.putExtra("newActivity", true);
				}
			}
			break;

		case GAS_HEATER: // 燃热
			L.e(this, "case GAS-HEATER");
			if (isMainActivityActive) { // 如何当前是处于电热或壁挂炉的主控界面,则从电热或壁挂炉主控界面切换到燃热主控界面
				originalIntent.setClass(this, ElectricMainActivity.class);
			} else if (isFurnaceMainActivityActive) {
				originalIntent.setClass(this, FurnaceMainActivity.class);
			} else {
				originalIntent.setClass(this, GasMainActivity.class);
				if (!isGasMainActivityActive) {
					originalIntent.putExtra("newActivity", true);
				}
			}
			break;

		case FURNACE: // 壁挂炉
			if (isMainActivityActive) {
				originalIntent.setClass(this, ElectricMainActivity.class);
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
