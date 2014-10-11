package com.vanward.ehheater.activity.main.gas;

import com.vanward.ehheater.activity.global.Global;
import com.xtremeprog.xpgconnect.generated.generated;

public class SendMsgModel {

	public static void setTempter(int i) {
		generated.SendGasWaterHeaterTargetTemperatureReq(Global.connectId,
				(short) i);
	}

	/**
	 * 系统模式：0x01（舒适模式）、0x02（厨房模式）、0x03（浴缸模式）、0x04（节能模式）、 0x05（智能模式）、0x06（自定义模式）
	 */

	// 设置到智能模式
	public static void setToIntelligenceMode() {
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 5);
	}

	// 设置到自定义模式
	public static void setToDIYMode() {
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 6);
	}

	// 厨房模式
	public static void setToKictionMode() {
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 2);
	}

	// 舒适模式
	public static void setToSolfMode() {
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 1);
	}

	// 节能模式
	public static void setToEnergyMode() {
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 4);
	}

	// 浴缸模式
	public static void setToBathtubMode() {
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 3);
	}

	// 打开设备
	public static void openDevice() {
		generated.SendGasWaterHeaterOnOffReq(Global.connectId, (short) 1);
	}

	// 打开设备
	public static void closeDevice() {
		generated.SendGasWaterHeaterOnOffReq(Global.connectId, (short) 0);
	}

	// DIY设置指令下发

	public static void setDIYModel(int i, GasCustomSetVo gasCustomSetVo) {
		System.out.println("SendMsgModel.setDIYModel()"
				+ gasCustomSetVo.getTempter() + " :  "
				+ gasCustomSetVo.getWaterval());
		// generated.SendGasWaterHeaterDIYSettingReq(arg0, arg1, arg2, arg3,
		// arg4)
		generated.SendGasWaterHeaterDIYSettingReq(Global.connectId, (short) 1,
				(short) 50, (short) 1);
	}

}
