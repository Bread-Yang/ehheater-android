package com.vanward.ehheater.util;

import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.xtremeprog.xpgconnect.XPGConnectClient;

import android.app.Activity;

public class SwitchDeviceUtil {

	public static void switchDevice(String mac, Activity activity) {

		HeaterInfo newHeaterInfo = new HeaterInfoDao(activity)
				.getHeaterByMac(mac);
		HeaterInfo shareheaterInfo = new HeaterInfoService(activity)
				.getCurrentSelectedHeater();

		if (newHeaterInfo.getMac().equals(shareheaterInfo.getMac())) {
			return;
		} else {
			HeaterInfoService hser = new HeaterInfoService(activity);
			HeaterType oriHeaterType = hser.getCurHeaterType();
			HeaterType newHeaterType = hser.getHeaterType(newHeaterInfo);

			hser.setCurrentSelectedHeater(mac);

			AlterDeviceHelper.newHeaterType = newHeaterType;
			AlterDeviceHelper.typeChanged = !newHeaterType
					.equals(oriHeaterType);
			AlterDeviceHelper.hostActivity = activity;

			if (Global.connectId > 0) {
				// 触发BaseBusinessActivity里的断开连接回调, 具体的切换逻辑在该回调中处理
				XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
			} else {
				// 如果当前未建立连接, 直接调用此方法
				AlterDeviceHelper.alterDevice();
			}
		}
	}
}
