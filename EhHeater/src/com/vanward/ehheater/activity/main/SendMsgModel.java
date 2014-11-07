package com.vanward.ehheater.activity.main;

import com.vanward.ehheater.activity.global.Global;
import com.xtremeprog.xpgconnect.generated.generated;

public class SendMsgModel {

	// 晨浴模式
	public static void changeToMorningWash(int num) {
		generated.SendPatternSettingReq(Global.connectId, (short) 3);

		try {
			Thread.sleep(1500);
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
	public static void sentAppolitionment(final int hour, final int min,
			final int num) {
		// generated.SendPatternSettingReq(Global.connectId, (short) 3);

		new Thread(new Runnable() {

			@Override
			public void run() {
				generated.SendSettingOrderReq(Global.connectId, (short) hour,
						(short) min);

				try {
					Thread.sleep(500);
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (num == 1) {
					generated
							.SendPatternSettingReq(Global.connectId, (short) 5);
				} else if (num == 2) {
					generated
							.SendPatternSettingReq(Global.connectId, (short) 6);
				} else if (num == 3) {
					generated
							.SendPatternSettingReq(Global.connectId, (short) 7);
				}

			}
		}).start();

	}

	// 智能模式
	public static void changeToIntelligenceModeWash() {
		generated.SendPatternSettingReq(Global.connectId, (short) 8);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		generated.SendSettingWaterTempReq(Global.connectId, (short) 45);
		
	}

	// 自定义
	public static void changeToZidingyiMode() {
		generated.SendPatternSettingReq(Global.connectId, (short) 1);
	}

	// 自定义
	public static void changeToJishiMode() {
		generated.SendPatternSettingReq(Global.connectId, (short) 2);
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
		generated.SendOnOffReq(Global.connectId, (short) 1);
	} // 打开设备

	public static void closeDevice() {
		generated.SendOnOffReq(Global.connectId, (short) 0);
	}
}
