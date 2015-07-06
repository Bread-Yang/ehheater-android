package com.vanward.ehheater.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.vanward.ehheater.activity.BaseBusinessActivity;
import com.vanward.ehheater.activity.configure.ConnectActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.main.electric.ElectricMainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceMainActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;

/**
 * 切换设备的帮助类
 * 
 * 
 * @author Administrator
 * 
 */
public class AlterDeviceHelper {

	private static final String TAG = "AlterDeviceHelper";

	public static Boolean typeChanged;

	public static Activity hostActivity;

	public static HeaterType newHeaterType;

	public static void alterDevice() {
		alterDevice(hostActivity, typeChanged, newHeaterType);
	}

	public static void alterDevice(Activity hostActivity, boolean typeChanged,
			HeaterType newHeaterType) {

		String userId = AccountService.getUserId(hostActivity.getApplicationContext());
		String userPsw = AccountService.getUserPsw(hostActivity.getApplicationContext());

		HeaterInfoService hser = new HeaterInfoService(hostActivity.getApplicationContext());

		if (!typeChanged) {
			
//			ConnectActivity.connectToDevice(hostActivity, hser
//					.getCurrentSelectedHeater().getMac(), userId, userPsw);
			
			((BaseBusinessActivity)hostActivity).connectToDevice();

			LocalBroadcastManager
					.getInstance(hostActivity.getBaseContext())
					.sendBroadcast(
							new Intent(Consts.INTENT_FILTER_HEATER_NAME_CHANGED));

		} else {
			// 使前个activity停止接收回调
			XPGConnectClient.RemoveActivity((GeneratedActivity) hostActivity);
			Intent intent = new Intent();
			intent.putExtra("switchSuccess", true);
			switch (newHeaterType) {
			case ELECTRIC_HEATER:
				intent.setClass(hostActivity, ElectricMainActivity.class);
				hostActivity.startActivity(intent);
				L.e(AlterDeviceHelper.class, "调用了hostActivity.finish()");
				hostActivity.finish();
				break;
			case GAS_HEATER:
				intent.setClass(hostActivity, GasMainActivity.class);
				hostActivity.startActivity(intent);
				L.e(AlterDeviceHelper.class, "调用了hostActivity.finish()");
				hostActivity.finish();
				break;
			case FURNACE:
				intent.setClass(hostActivity, FurnaceMainActivity.class);
				hostActivity.startActivity(intent);
				L.e(AlterDeviceHelper.class, "调用了hostActivity.finish()");
				hostActivity.finish();
				break;
			default:
				Toast.makeText(hostActivity, "无法识别该设备", Toast.LENGTH_LONG)
						.show();
				break;
			}

		}

		AlterDeviceHelper.hostActivity = null;
		AlterDeviceHelper.typeChanged = null;
		AlterDeviceHelper.newHeaterType = null;

	}
}
