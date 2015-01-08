package com.vanward.ehheater.activity.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.login.LoginActivity;
import com.xtremeprog.xpgconnect.generated.BindingDelResp_t;
import com.xtremeprog.xpgconnect.generated.BindingGetResp_t;
import com.xtremeprog.xpgconnect.generated.BindingSetResp_t;
import com.xtremeprog.xpgconnect.generated.BootstrapResp_t;
import com.xtremeprog.xpgconnect.generated.DeviceOnlineStateResp_t;
import com.xtremeprog.xpgconnect.generated.DiscoveryV1Resp_t;
import com.xtremeprog.xpgconnect.generated.DiscoveryV3Resp_t;
import com.xtremeprog.xpgconnect.generated.EasylinkResp_t;
import com.xtremeprog.xpgconnect.generated.HeartbeatResp_t;
import com.xtremeprog.xpgconnect.generated.LanLoginResp_t;
import com.xtremeprog.xpgconnect.generated.ModuleVersionResp_t;
import com.xtremeprog.xpgconnect.generated.OnboardingSetResp_t;
import com.xtremeprog.xpgconnect.generated.PasscodeResp_t;
import com.xtremeprog.xpgconnect.generated.ReadWifiConfigResp_t;
import com.xtremeprog.xpgconnect.generated.SerialPortConfigResp_t;
import com.xtremeprog.xpgconnect.generated.StateResp_t;
import com.xtremeprog.xpgconnect.generated.UserPwdChangeResp_t;
import com.xtremeprog.xpgconnect.generated.UserRegisterResp_t;
import com.xtremeprog.xpgconnect.generated.WifiListResp_t;
import com.xtremeprog.xpgconnect.generated.WriteWifiConfigResp_t;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

public class FeedbackActivity extends EhHeaterBaseActivity {

	private static final String TAG = "FeedbackActivity";

	private HeaterDevice device = Global.device;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startActivity(new Intent(this, LoginActivity.class));
	}

	@Override
	public void onInited(int result) {
	}

	@Override
	public void onDeviceFound(XpgEndpoint endpoint) {
	}

	@Override
	public void onEasyLinkResp(XpgEndpoint endpoint) {
	}

	@Override
	public void onVersionEvent(int key, int value, int connId) {
	}

	@Override
	public void onConnectEvent(int connId, int event) {
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
	}

	@Override
	public void onWriteEvent(int result, int connId) {
	}

	@Override
	public void onTcpPacket(byte[] data, int connId) {
		// 第5位 : "开关机状态"
		if (data[5] == 0x00) {
			device.setPowerOn(false);
		} else {
			device.setPowerOn(true);
		}

		// 第6位 : "加热状态,未加热状态"
		if (data[6] == 0x00) {
			device.setHeating(false);
		} else {
			device.setHeating(true);
		}

		// 第7位 : "工作模式"
		if (data[6] == 0x01) {
		} else if (data[6] == 0x02) {
		} else if (data[6] == 0x03) {
		} else if (data[6] == 0x04) {
		} else if (data[6] == 0x05) {
		} else if (data[6] == 0x06) {
		} else if (data[6] == 0x07) {
		}

		// 第8位 : "预约状态"
		if (data[8] == 0x00) {
			device.setAppointment(false);
		} else {
			device.setAppointment(true);
		}

		// 第9位 : "内胆水温1"
		device.setInnerTemp(data[9]);

		// 第10位 : "内胆水温2"

		// 第11位 : "内胆水温3"

		// 第12位 : "设置水温"
		device.setSetupTemp(data[12]);

		// 第13位 : "设置功率"
		device.setSetupPower(data[13]);

		// 第14位 : "剩余加热时间"
		device.setResidualHeatTime(data[14]);

		// 第15位 : "剩余热水量"
		device.setResidualHotWater(data[15]);

		Log.e(TAG, "开关机状态 : " + (device.isPowerOn() ? "开机" : "关机"));
		Log.e(TAG, "加热状态 : " + (device.isHeating() ? "加热" : "未加热"));
		Log.e(TAG, "预约状态 : " + (device.isAppointment() ? "预约" : "未预约"));
		Log.e(TAG, "内胆水温 : " + (device.getInnerTemp() + ""));
		Log.e(TAG, "设置水温 : " + (device.getSetupTemp() + ""));
		Log.e(TAG, "设置功率 : " + (device.getSetupPower() + ""));
		Log.e(TAG, "剩余加热时间 : " + (device.getResidualHeatTime() + ""));
		Log.e(TAG, "剩余热水量 : " + (device.getResidualHotWater() + ""));
	}

	@Override
	public void OnWriteWifiConfigResp(WriteWifiConfigResp_t pResp, int nConnId) {
		generated.DumpWriteWifiConfigResp(pResp);
	}

	@Override
	public void OnOnboardingSetResp(OnboardingSetResp_t pResp) {
		generated.DumpOnboardingSetResp(pResp);
	}

	@Override
	public void OnDiscoveryV1Resp(DiscoveryV1Resp_t pResp) {
		generated.DumpDiscoveryV1Resp(pResp);
	}

	@Override
	public void OnDiscoveryV3Resp(DiscoveryV3Resp_t pResp) {
		generated.DumpDiscoveryV3Resp(pResp);
	}

	@Override
	public void OnEasylinkResp(EasylinkResp_t pResp) {
		generated.DumpEasylinkResp(pResp);
	}

	@Override
	public void OnBootstrapResp(BootstrapResp_t pResp) {
		generated.DumpBootstrapResp(pResp);
	}

	@Override
	public void OnPasscodeResp(PasscodeResp_t pResp, int nConnId) {
		generated.DumpPasscodeResp(pResp);
	}

	@Override
	public void OnLanLoginResp(LanLoginResp_t pResp, int nConnId) {
		generated.DumpLanLoginResp(pResp);
	}

	@Override
	public void OnModuleVersionResp(ModuleVersionResp_t pResp, int nConnId) {
		generated.DumpModuleVersionResp(pResp);
	}

	@Override
	public void OnWifiListResp(WifiListResp_t pResp, int nConnId) {
		generated.DumpWifiListResp(pResp);
	}

	@Override
	public void OnSerialPortConfigResp(SerialPortConfigResp_t pResp, int nConnId) {
		generated.DumpSerialPortConfigResp(pResp);
	}

	@Override
	public void OnUserRegisterResp(UserRegisterResp_t pResp, int nConnId) {
		generated.DumpUserRegisterResp(pResp);
	}

	@Override
	public void OnBindingSetResp(BindingSetResp_t pResp, int nConnId) {
		generated.DumpBindingSetResp(pResp);
	}

	@Override
	public void OnBindingGetResp(BindingGetResp_t pResp, int nConnId) {
		generated.DumpBindingGetResp(pResp);
	}

	@Override
	public void OnUserPwdChangeResp(UserPwdChangeResp_t pResp, int nConnId) {
		generated.DumpUserPwdChangeResp(pResp);
	}

	@Override
	public void OnBindingDelResp(BindingDelResp_t pResp, int nConnId) {
		generated.DumpBindingDelResp(pResp);
	}

	@Override
	public void OnReadWifiConfigResp(ReadWifiConfigResp_t pResp, int nConnId) {
		generated.DumpReadWifiConfigResp(pResp);
	}

	@Override
	public void OnDeviceOnlineStateResp(DeviceOnlineStateResp_t pResp,
			int nConnId) {
		generated.DumpDeviceOnlineStateResp(pResp);
	}

	@Override
	public void OnStateResp(StateResp_t pResp, int nConnId) {
		generated.DumpStateResp(pResp);
	}

	@Override
	public void OnHeartbeatResp(HeartbeatResp_t pResp, int nConnId) {
		generated.DumpHeartbeatResp(pResp);
	}
}
