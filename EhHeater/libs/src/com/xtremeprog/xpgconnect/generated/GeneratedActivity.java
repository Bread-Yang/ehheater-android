package com.xtremeprog.xpgconnect.generated;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.listener.*;
import com.xtremeprog.xpgconnect.generated.*;

public class GeneratedActivity extends Activity
    implements ClientListener, GeneratedJniListener
{
    public XpgMsgHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new XpgMsgHandler(this);

        XPGConnectClient.AddActivity(this);
        GeneratedJniJava.AddHandler(handler);
    }

    @Override
    public void onDestroy()
    {
        GeneratedJniJava.RemoveHandler(handler);
        XPGConnectClient.RemoveActivity(this);
        super.onDestroy();
    }

    @Override
    public void onInited(int result) { }

    @Override
    public void onDeviceFound(XpgEndpoint endpoint) { }

    @Override
    public void onEasyLinkResp(XpgEndpoint endpoint) { }

    @Override
    public void onVersionEvent(int key, int value, int connId) { }

    @Override
    public void onConnectEvent(int connId, int event) { }

    @Override
    public void onLoginCloudResp(int result, String mac) { }

    @Override
    public void onWriteEvent(int result, int connId) { }

    @Override
    public void onTcpPacket(byte[] data, int connId) { }

    @Override
    public void onSendPacket(byte[] data, int connId) { }

    @Override
    public void onHTTPResp(int result, String buffer) { }

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
    public void OnBindingGetV2Resp(BindingGetV2Resp_t pResp, int nConnId) {
        generated.DumpBindingGetV2Resp(pResp);
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
    public void OnDeviceOnlineStateResp(DeviceOnlineStateResp_t pResp, int nConnId) {
        generated.DumpDeviceOnlineStateResp(pResp);
    }

    @Override
    public void OnStateResp(StateResp_t pResp, int nConnId) {
        generated.DumpStateResp(pResp);
    }

    @Override
    public void OnGasWaterHeaterStatusResp(GasWaterHeaterStatusResp_t pResp, int nConnId) {
        generated.DumpGasWaterHeaterStatusResp(pResp);
    }

    @Override
    public void OnDERYStatusResp(DERYStatusResp_t pResp, int nConnId) {
        generated.DumpDERYStatusResp(pResp);
    }

    @Override
    public void OnHeartbeatResp(HeartbeatResp_t pResp, int nConnId) {
        generated.DumpHeartbeatResp(pResp);
    }

}
