package com.xtremeprog.xpgconnect.generated;

import com.xtremeprog.xpgconnect.generated.WriteWifiConfigResp_t;
import com.xtremeprog.xpgconnect.generated.OnboardingSetResp_t;
import com.xtremeprog.xpgconnect.generated.DiscoveryV1Resp_t;
import com.xtremeprog.xpgconnect.generated.DiscoveryV3Resp_t;
import com.xtremeprog.xpgconnect.generated.EasylinkResp_t;
import com.xtremeprog.xpgconnect.generated.BootstrapResp_t;
import com.xtremeprog.xpgconnect.generated.PasscodeResp_t;
import com.xtremeprog.xpgconnect.generated.LanLoginResp_t;
import com.xtremeprog.xpgconnect.generated.ModuleVersionResp_t;
import com.xtremeprog.xpgconnect.generated.WifiListResp_t;
import com.xtremeprog.xpgconnect.generated.SerialPortConfigResp_t;
import com.xtremeprog.xpgconnect.generated.UserRegisterResp_t;
import com.xtremeprog.xpgconnect.generated.BindingSetResp_t;
import com.xtremeprog.xpgconnect.generated.BindingGetResp_t;
import com.xtremeprog.xpgconnect.generated.BindingGetV2Resp_t;
import com.xtremeprog.xpgconnect.generated.UserPwdChangeResp_t;
import com.xtremeprog.xpgconnect.generated.BindingDelResp_t;
import com.xtremeprog.xpgconnect.generated.ReadWifiConfigResp_t;
import com.xtremeprog.xpgconnect.generated.DeviceOnlineStateResp_t;
import com.xtremeprog.xpgconnect.generated.StateResp_t;
import com.xtremeprog.xpgconnect.generated.GasWaterHeaterStatusResp_t;
import com.xtremeprog.xpgconnect.generated.HeartbeatResp_t;

public interface GeneratedJniListener {
    public void OnWriteWifiConfigResp(WriteWifiConfigResp_t resp, int nConnId);
    public void OnOnboardingSetResp(OnboardingSetResp_t resp);
    public void OnDiscoveryV1Resp(DiscoveryV1Resp_t resp);
    public void OnDiscoveryV3Resp(DiscoveryV3Resp_t resp);
    public void OnEasylinkResp(EasylinkResp_t resp);
    public void OnBootstrapResp(BootstrapResp_t resp);
    public void OnPasscodeResp(PasscodeResp_t resp, int nConnId);
    public void OnLanLoginResp(LanLoginResp_t resp, int nConnId);
    public void OnModuleVersionResp(ModuleVersionResp_t resp, int nConnId);
    public void OnWifiListResp(WifiListResp_t resp, int nConnId);
    public void OnSerialPortConfigResp(SerialPortConfigResp_t resp, int nConnId);
    public void OnUserRegisterResp(UserRegisterResp_t resp, int nConnId);
    public void OnBindingSetResp(BindingSetResp_t resp, int nConnId);
    public void OnBindingGetResp(BindingGetResp_t resp, int nConnId);
    public void OnBindingGetV2Resp(BindingGetV2Resp_t resp, int nConnId);
    public void OnUserPwdChangeResp(UserPwdChangeResp_t resp, int nConnId);
    public void OnBindingDelResp(BindingDelResp_t resp, int nConnId);
    public void OnReadWifiConfigResp(ReadWifiConfigResp_t resp, int nConnId);
    public void OnDeviceOnlineStateResp(DeviceOnlineStateResp_t resp, int nConnId);
    public void OnStateResp(StateResp_t resp, int nConnId);
    public void OnGasWaterHeaterStatusResp(GasWaterHeaterStatusResp_t resp, int nConnId);
    public void OnHeartbeatResp(HeartbeatResp_t resp, int nConnId);
}