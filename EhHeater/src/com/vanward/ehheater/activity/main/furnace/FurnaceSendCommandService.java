package com.vanward.ehheater.activity.main.furnace;

import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.common.BaseSendCommandService;
import com.xtremeprog.xpgconnect.generated.generated;

public class FurnaceSendCommandService extends BaseSendCommandService {

	private static final FurnaceSendCommandService single = new FurnaceSendCommandService();

	private FurnaceSendCommandService() {
	}

	public static FurnaceSendCommandService getInstance() {
		return single;
	}
	
	public void SendDERYRefreshReq() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYRefreshReq(Global.connectId);
	}

	public void SendDERYResetErrorReq() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYResetErrorReq(Global.connectId);
	}

	public void openDevice() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYOnOrOffReq(Global.connectId, (short) 1);
	}

	public void closeDevice() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYOnOrOffReq(Global.connectId, (short) 0);
	}

	public void setToSummerMode() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYSeasonStateReq(Global.connectId, (short) 0);
	}

	public void setToWinnerMode() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYSeasonStateReq(Global.connectId, (short) 1);
	}

	public void setToNormalBath() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYBathModeReq(Global.connectId, (short) 0);
	}

	public void setToComfortBath() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYBathModeReq(Global.connectId, (short) 1);
	}

	public void setToNormalHeating() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYHeatingModeReq(Global.connectId, (short) 0xA0);
	}

	public void setToNightHeating() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYHeatingModeReq(Global.connectId, (short) 0xA1);
	}

	public void setToOutdoorHeating() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYHeatingModeReq(Global.connectId, (short) 0xA2);
	}

	public void setBathTemperature(int i) {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYBathTemReq(Global.connectId, (short) i);
	}

	public void setHeatingTemperature(int i) {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYHeatingTemReq(Global.connectId, (short) i);
	}

	public void errorReset() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYResetErrorReq(Global.connectId);
	}

	public void refreshStatus() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendDERYRefreshReq(Global.connectId);
	}

}
