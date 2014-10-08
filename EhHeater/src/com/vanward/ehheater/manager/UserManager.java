package com.vanward.ehheater.manager;

import android.content.Context;

import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.BindingDelResp_t;
import com.xtremeprog.xpgconnect.generated.BindingGetResp_t;
import com.xtremeprog.xpgconnect.generated.BindingSetResp_t;
import com.xtremeprog.xpgconnect.generated.BootstrapResp_t;
import com.xtremeprog.xpgconnect.generated.DeviceOnlineStateResp_t;
import com.xtremeprog.xpgconnect.generated.DiscoveryV1Resp_t;
import com.xtremeprog.xpgconnect.generated.DiscoveryV3Resp_t;
import com.xtremeprog.xpgconnect.generated.EasylinkResp_t;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;
import com.xtremeprog.xpgconnect.generated.GeneratedJniJava;
import com.xtremeprog.xpgconnect.generated.GeneratedJniListener;
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
import com.xtremeprog.xpgconnect.generated.XpgMsgHandler;
import com.xtremeprog.xpgconnect.listener.ClientListener;

public class UserManager extends GeneratedActivity implements ClientListener, GeneratedJniListener {
	static UserManager userManager;
	public XpgMsgHandler handler;

	public static UserManager getInstance(Context context) {
		if (userManager == null) {
			synchronized (UserManager.class) {
				if (userManager == null) {
					userManager = new UserManager(context);
				}
			}
		}
		return userManager;
	}

	public UserManager(Context context) {
		handler = new XpgMsgHandler(this);
		XPGConnectClient.AddActivity(this);
		GeneratedJniJava.AddHandler(handler);
	}

	@Override
	public void OnBindingDelResp(BindingDelResp_t arg0, int arg1) {

	}

	@Override
	public void OnBindingGetResp(BindingGetResp_t arg0, int arg1) {

	}

	@Override
	public void OnBindingSetResp(BindingSetResp_t arg0, int arg1) {

	}

	@Override
	public void OnBootstrapResp(BootstrapResp_t arg0) {

	}

	@Override
	public void OnDeviceOnlineStateResp(DeviceOnlineStateResp_t arg0, int arg1) {

	}

	@Override
	public void OnDiscoveryV1Resp(DiscoveryV1Resp_t arg0) {

	}

	@Override
	public void OnDiscoveryV3Resp(DiscoveryV3Resp_t arg0) {

	}

	@Override
	public void OnEasylinkResp(EasylinkResp_t arg0) {

	}

	@Override
	public void OnHeartbeatResp(HeartbeatResp_t arg0, int arg1) {

	}

	@Override
	public void OnLanLoginResp(LanLoginResp_t arg0, int arg1) {

	}

	@Override
	public void OnModuleVersionResp(ModuleVersionResp_t arg0, int arg1) {

	}

	@Override
	public void OnOnboardingSetResp(OnboardingSetResp_t arg0) {

	}

	@Override
	public void OnPasscodeResp(PasscodeResp_t arg0, int arg1) {

	}

	@Override
	public void OnReadWifiConfigResp(ReadWifiConfigResp_t arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnSerialPortConfigResp(SerialPortConfigResp_t arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnStateResp(StateResp_t arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnUserPwdChangeResp(UserPwdChangeResp_t arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnUserRegisterResp(UserRegisterResp_t arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnWifiListResp(WifiListResp_t arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnWriteWifiConfigResp(WriteWifiConfigResp_t arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectEvent(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeviceFound(XpgEndpoint arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEasyLinkResp(XpgEndpoint arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInited(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoginCloudResp(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTcpPacket(byte[] arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVersionEvent(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWriteEvent(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
