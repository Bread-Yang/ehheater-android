package com.vanward.ehheater.activity.main.gas;

import com.vanward.ehheater.activity.global.Global;
import com.xtremeprog.xpgconnect.generated.generated;

public class SendMsgModel {

	// 晨浴模式
	public static void changeToMorningWash(int num) {
		generated.SendPatternSettingReq(Global.connectId, (short) 3);

		try {
			Thread.sleep(500);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (num == 1) {
			generated.SendPatternSettingReq(Global.connectId, (short) 5);
		} else if (num == 2) {
			generated.SendPatternSettingReq(Global.connectId, (short) 6);
		} else if (num == 3) {
			generated.SendPatternSettingReq(Global.connectId, (short) 7);
		}

	}

	// 发送预约 时间人数
	public static void sentAppolitionment(int hour, int min, int num) {
		// generated.SendPatternSettingReq(Global.connectId, (short) 3);
		generated.SendSettingOrderReq(Global.connectId, (short) hour,
				(short) min);

		try {
			Thread.sleep(500);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (num == 1) {
			generated.SendPatternSettingReq(Global.connectId, (short) 5);
		} else if (num == 2) {
			generated.SendPatternSettingReq(Global.connectId, (short) 6);
		} else if (num == 3) {
			generated.SendPatternSettingReq(Global.connectId, (short) 7);
		}

	}

	// 智能模式
	public static void changeToIntelligenceModeWash() {
		generated.SendPatternSettingReq(Global.connectId, (short) 2);
	}

	// 自定义
	public static void changeToZidingyiMode() {
		generated.SendPatternSettingReq(Global.connectId, (short) 1);
	}

	public static void setPower(int i) {
		generated.SendSettingPowerReq(Global.connectId, (short) (0x00 + i));
	}

	public static void setTempter(int i) {
		generated.SendSettingWaterTempReq(Global.connectId, (short) i);
	}

	// 夜电模式
	public static void changeNightMode() {
		generated.SendPatternSettingReq(Global.connectId, (short) 4);
	}

	// 打开设备
	public static void openDevice() {
		generated.SendGasWaterHeaterOnOffReq(Global.connectId, (short) 1);
	} // 打开设备

	public static void closeDevice() {
		generated.SendGasWaterHeaterOnOffReq(Global.connectId, (short) 0);
	}
}
