package com.vanward.ehheater.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.vanward.ehheater.activity.WelcomeActivity;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.xtremeprog.xpgconnect.XPGConnectClient;

public class SwitchDeviceUtil {

	public static void switchDevice(String did, Activity activity) {

//		HeaterInfo newHeaterInfo = new HeaterInfoDao(activity)
//				.getHeaterByMac(mac);
		HeaterInfo newHeaterInfo = new HeaterInfoDao(activity)
		.getHeaterByDid(did);
		HeaterInfo shareheaterInfo = new HeaterInfoService(activity)
				.getCurrentSelectedHeater();
		
		Log.e("newHeaterInfo == null", (newHeaterInfo == null) + "");
		Log.e("shareheaterInfo == null", (shareheaterInfo == null) + "");
		
		
		if (newHeaterInfo == null || shareheaterInfo == null) {
			Intent intent = new Intent(activity, WelcomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.startActivity(intent);
			activity.finish();
			return;
		}

		if (newHeaterInfo.getMac().equals(shareheaterInfo.getMac())) {
			return;
		} else {
			HeaterInfoService hser = new HeaterInfoService(activity);
			HeaterType oriHeaterType = hser.getCurHeaterType();
			HeaterType newHeaterType = hser.getHeaterType(newHeaterInfo);

			hser.setCurrentSelectedHeater(newHeaterInfo.getMac());

			AlterDeviceHelper.newHeaterType = newHeaterType;
			AlterDeviceHelper.typeChanged = !newHeaterType
					.equals(oriHeaterType);
			AlterDeviceHelper.hostActivity = activity;

			if (Global.connectId > -1) {
				// 触发BaseBusinessActivity里的断开连接回调, 具体的切换逻辑在该回调中处理
				XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
			} else {
				// 如果当前未建立连接, 直接调用此方法
				AlterDeviceHelper.alterDevice();
			}
		}
	}
}
