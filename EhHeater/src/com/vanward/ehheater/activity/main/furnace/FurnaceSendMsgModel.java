package com.vanward.ehheater.activity.main.furnace;

import com.vanward.ehheater.activity.global.Global;
import com.xtremeprog.xpgconnect.generated.generated;

public class FurnaceSendMsgModel {

	public static void openDevice() {
		generated.SendDERYOnOrOffReq(Global.connectId, (short) 1);
	}

	public static void closeDevice() {
		generated.SendDERYOnOrOffReq(Global.connectId, (short) 0);
	}

	public static void setToSummerMode() {
		generated.SendDERYSeasonStateReq(Global.connectId, (short) 0);
	}

	public static void setToWinnerMode() {
		generated.SendDERYSeasonStateReq(Global.connectId, (short) 1);
	}

	public static void setToNormalBath() {
		generated.SendDERYBathModeReq(Global.connectId, (short) 0);
	}

	public static void setToComfortBath() {
		generated.SendDERYBathModeReq(Global.connectId, (short) 1);
	}

	public static void setToNormalHeating() {
		generated.SendDERYHeatingModeReq(Global.connectId, (short) 0xA0);
	}

	public static void setToNightHeating() {
		generated.SendDERYHeatingModeReq(Global.connectId, (short) 0xA1);
	}

	public static void setToOutdoorHeating() {
		generated.SendDERYHeatingModeReq(Global.connectId, (short) 0xA2);
	}

	public static void setBathTemperature(int i) {
		generated.SendDERYBathTemReq(Global.connectId, (short) i);
	}

	public static void setHeatingTemperature(int i) {
		generated.SendDERYHeatingTemReq(Global.connectId, (short) i);
	}

	public static void errorReset() {
		generated.SendDERYResetErrorReq(Global.connectId);
	}

	public static void refreshStatus() {
		generated.SendDERYRefreshReq(Global.connectId);
	}
	
}
