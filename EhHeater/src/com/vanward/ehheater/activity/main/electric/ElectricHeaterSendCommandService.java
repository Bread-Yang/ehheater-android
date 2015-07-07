package com.vanward.ehheater.activity.main.electric;

import android.content.Context;

import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.common.BaseSendCommandService;
import com.vanward.ehheater.util.IntelligentPatternUtil;
import com.xtremeprog.xpgconnect.generated.generated;

public class ElectricHeaterSendCommandService extends BaseSendCommandService {

	private static final ElectricHeaterSendCommandService single = new ElectricHeaterSendCommandService();

	private ElectricHeaterSendCommandService() {
	}

	public static ElectricHeaterSendCommandService getInstance() {
		return single;
	}
	
	public void SendStateReq() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendStateReq(Global.connectId);
	}

	// 晨浴模式
	public void changeToMorningWash(int num) {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
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
	// public static void sentAppolitionment(final int hour, final int min,
	// final int num) {
	// // generated.SendPatternSettingReq(Global.connectId, (short) 3);
	//
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// generated.SendSettingOrderReq(Global.connectId, (short) hour,
	// (short) min);
	//
	// try {
	// Thread.sleep(500);
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// if (num == 1) {
	// generated
	// .SendPatternSettingReq(Global.connectId, (short) 5);
	// } else if (num == 2) {
	// generated
	// .SendPatternSettingReq(Global.connectId, (short) 6);
	// } else if (num == 3) {
	// generated
	// .SendPatternSettingReq(Global.connectId, (short) 7);
	// }
	//
	// }
	// }).start();
	//
	// }

	// 智能模式
	public void changeToIntelligenceModeWash(Context context) {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendPatternSettingReq(Global.connectId, (short) 8);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		generated.SendSettingWaterTempReq(Global.connectId,
				(short) IntelligentPatternUtil.getMostSetTemperature(context));

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		setPower(IntelligentPatternUtil.getMostSetPower(context));

	}

	// 自定义
	public void changeToZidingyiMode() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendPatternSettingReq(Global.connectId, (short) 1);
	}

	// 自定义
	public void changeToJishiMode() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendPatternSettingReq(Global.connectId, (short) 2);
	}

	public void setPower(int i) {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendSettingPowerReq(Global.connectId, (short) (0x00 + i));
	}

	public void setTempter(int i) {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendSettingWaterTempReq(Global.connectId, (short) i);
	}

	// 夜电模式
	public void changeNightMode() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendPatternSettingReq(Global.connectId, (short) 4);
	}

	// 打开设备
	public void openDevice() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendOnOffReq(Global.connectId, (short) 1);
	}

	public void closeDevice() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendOnOffReq(Global.connectId, (short) 0);
	}
}
