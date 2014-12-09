package com.vanward.ehheater.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.vanward.ehheater.activity.configure.ConnectActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.main.MainActivity;
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
	
	public static Boolean typeChanged;
	
	public static Activity hostActivity;
	
	public static HeaterType newHeaterType;
	
	
	
	
	public static void alterDevice() {
		alterDevice(hostActivity, typeChanged, newHeaterType);
	}
	
	public static void alterDevice(Activity hostActivity, boolean typeChanged, HeaterType newHeaterType) {

		String userId = AccountService.getUserId(hostActivity);
		String userPsw = AccountService.getUserPsw(hostActivity);

		HeaterInfoService hser = new HeaterInfoService(hostActivity);

		if (!typeChanged) {
			
			ConnectActivity.connectToDevice(hostActivity, 
					hser.getCurrentSelectedHeater().getMac(), userId, userPsw);

			LocalBroadcastManager.getInstance(hostActivity.getBaseContext()).
				sendBroadcast(new Intent(Consts.INTENT_FILTER_HEATER_NAME_CHANGED));
			
		} else {
			// 使前个activity停止接收回调
			XPGConnectClient.RemoveActivity((GeneratedActivity) hostActivity);
			
			switch (newHeaterType) {
			case Eh:
				hostActivity.startActivity(new Intent(hostActivity, MainActivity.class));
				hostActivity.finish();
				break;
			case ST:
				hostActivity.startActivity(new Intent(hostActivity, GasMainActivity.class));
				hostActivity.finish();
				break;
			case EH_FURNACE:
				hostActivity.startActivity(new Intent(hostActivity, FurnaceMainActivity.class));
				hostActivity.finish();
				break;
			default:
				Toast.makeText(hostActivity, "无法识别该设备", Toast.LENGTH_LONG).show();
				break;
			}

		}

		AlterDeviceHelper.hostActivity = null;
		AlterDeviceHelper.typeChanged = null;
		AlterDeviceHelper.newHeaterType = null;
		
	}
}
