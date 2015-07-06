package com.vanward.ehheater.activity.main.gas;

import android.content.Context;

import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.common.BaseSendCommandService;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.view.BathSettingDialogUtil.BathSettingVo;
import com.xtremeprog.xpgconnect.generated.generated;

public class GasHeaterSendCommandService extends BaseSendCommandService {

	private static final GasHeaterSendCommandService single = new GasHeaterSendCommandService();

	private GasHeaterSendCommandService() {
	}

	public static GasHeaterSendCommandService getInstance() {
		return single;
	}

	public void SendGasWaterHeaterMobileRefreshReq() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendGasWaterHeaterMobileRefreshReq(Global.connectId);
	}

	public void setTempter(int i) {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendGasWaterHeaterTargetTemperatureReq(Global.connectId,
				(short) i);
	}

	/**
	 * 系统模式：0x01（舒适模式）、0x02（厨房模式）、0x03（浴缸模式）、0x04（节能模式）、 0x05（智能模式）、0x06（自定义模式）
	 */

	// 设置到智能模式
	public void setToIntelligenceMode() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 5);
	}

	// 设置到自定义模式
	public void setToDIYMode() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 6);
	}

	// 厨房模式
	public void setToKictionMode() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 2);
	}

	// 舒适模式
	public void setToSolfMode() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 1);
	}

	// 节能模式
	public void setToEnergyMode() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated
				.SendGasWaterHeaterModelCommandReq(Global.connectId, (short) 4);
	}

	// 浴缸模式
	public void setToBathtubMode(final Context context) {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {

				generated.SendGasWaterHeaterModelCommandReq(Global.connectId,
						(short) 3);

				BathSettingVo bathSettingVo = new BaseDao(context).getDb()
						.findById("1", BathSettingVo.class);

				if (bathSettingVo == null) {
					bathSettingVo = new BathSettingVo("1", 50, 48);
				}

				try {
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}

				generated.SendGasWaterHeaterTargetTemperatureReq(
						Global.connectId, (short) (bathSettingVo.getTem()));

				try {
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}

				generated.SendGasWaterHeaterSetWaterInjectionReq(
						Global.connectId, (short) (bathSettingVo.getWater()));

			}
		}).start();

	}

	public void SendGasWaterHeaterDIYSettingReq(short sendId, short value,
			short waterValue) {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendGasWaterHeaterDIYSettingReq(Global.connectId, sendId,
				value, waterValue);
	}

	// 打开设备
	public void openDevice() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendGasWaterHeaterOnOffReq(Global.connectId, (short) 1);
	}

	// 关闭设备
	public void closeDevice() {
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		generated.SendGasWaterHeaterOnOffReq(Global.connectId, (short) 0);
	}

	// DIY设置指令下发

	public void setDIYModel(int i, final GasCustomSetVo gasCustomSetVo) {
		L.e(GasHeaterSendCommandService.class,
				"SendMsgModel.setDIYModel() : " + gasCustomSetVo.getSendId()
						+ " :  " + gasCustomSetVo.getTempter() + " :  "
						+ gasCustomSetVo.getWaterval());
		if (beforeSendCommandCallBack != null) {
			beforeSendCommandCallBack.beforeSendCommand();
		}
		// generated.SendGasWaterHeaterDIYSettingReq(arg0, arg1, arg2, arg3,
		// arg4)
		new Thread(new Runnable() {

			@Override
			public void run() {
				setToDIYMode();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				generated.SendGasWaterHeaterDIYSettingReq(Global.connectId,
						(short) gasCustomSetVo.getSendId(),
						(short) gasCustomSetVo.getTempter(),
						(short) gasCustomSetVo.getWaterval());
			}
		}).start();

	}
}
