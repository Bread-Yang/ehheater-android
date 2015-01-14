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
    public void onAirLinkResp(XpgEndpoint endpoint) { }

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
    public void onWanLoginResp(int result, int connId) { }

    @Override
    public void onV4Login(int errorCode, String uid,String token,String expire_at) { }

    @Override
    public void onV4GetMyBindings(int errorCode, XpgEndpoint endpoint) { }

    @Override
    public void onV4QueryDevice(int errorCode, String did,String passcode) { }

    @Override
    public void onV4CreateUserByAnonymity(int errorCode, String uid,String token,String expire_at) { }

    @Override
    public void onV4CreateUserByName(int errorCode, String uid,String token,String expire_at) { }

    @Override
    public void onV4CreateUserByPhone(int errorCode, String uid,String token,String expire_at) { }

    @Override
    public void onV4CreateUserByMail(int errorCode, String uid,String token,String expire_at) { }

    @Override
    public void onV4UpdateUserName(int errorCode, String updatedAt) { }

    @Override
    public void onV4UpdatePhone(int errorCode, String updatedAt) { }

    @Override
    public void onV4ChangeUserPwd(int errorCode, String updatedAt) { }

    @Override
    public void onV4ChangeUserPhone(int errorCode, String updatedAt) { }

    @Override
    public void onV4ChangeUserMail(int errorCode, String updatedAt) { }

    @Override
    public void onV4GetDeviceInfo(int errorCode,XpgEndpoint endpoint) { }

    @Override
    public void onV4GetMobileAuthCode(int errorCode) { }

    @Override
    public void onV4VerifyMobileAuthCode(int errorCode) { }

    @Override
    public void onV4RecoverPwdByPhone(int errorCode) { }

    @Override
    public void onV4RecoverPwdByMail(int errorCode) { }

    @Override
    public void onV4BindDevce(int errorCode ,String successString,String failString) { }

    @Override
    public void onV4UnbindDevice(int errorCode ,String successString,String failString) { }

    /**
    * @param pResp
    *            可调用 generated.DumpWriteWifiConfigResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnWriteWifiConfigResp(WriteWifiConfigResp_t pResp, int nConnId) {
        // generated.DumpWriteWifiConfigResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpOnboardingSetResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnOnboardingSetResp(OnboardingSetResp_t pResp) {
        // generated.DumpOnboardingSetResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpDiscoveryV1Resp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnDiscoveryV1Resp(DiscoveryV1Resp_t pResp) {
        // generated.DumpDiscoveryV1Resp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpDiscoveryV3Resp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnDiscoveryV3Resp(DiscoveryV3Resp_t pResp) {
        // generated.DumpDiscoveryV3Resp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpEasylinkResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnEasylinkResp(EasylinkResp_t pResp) {
        // generated.DumpEasylinkResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpBootstrapResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnBootstrapResp(BootstrapResp_t pResp) {
        // generated.DumpBootstrapResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpAirLinkResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnAirLinkResp(AirLinkResp_t pResp) {
        // generated.DumpAirLinkResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpPasscodeResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnPasscodeResp(PasscodeResp_t pResp, int nConnId) {
        // generated.DumpPasscodeResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpLanLoginResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnLanLoginResp(LanLoginResp_t pResp, int nConnId) {
        // generated.DumpLanLoginResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpModuleVersionResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnModuleVersionResp(ModuleVersionResp_t pResp, int nConnId) {
        // generated.DumpModuleVersionResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpWifiListResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnWifiListResp(WifiListResp_t pResp, int nConnId) {
        // generated.DumpWifiListResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpSerialPortConfigResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnSerialPortConfigResp(SerialPortConfigResp_t pResp, int nConnId) {
        // generated.DumpSerialPortConfigResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpUserRegisterResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnUserRegisterResp(UserRegisterResp_t pResp, int nConnId) {
        // generated.DumpUserRegisterResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpBindingSetResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnBindingSetResp(BindingSetResp_t pResp, int nConnId) {
        // generated.DumpBindingSetResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpBindingGetResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnBindingGetResp(BindingGetResp_t pResp, int nConnId) {
        // generated.DumpBindingGetResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpBindingGetV2Resp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnBindingGetV2Resp(BindingGetV2Resp_t pResp, int nConnId) {
        // generated.DumpBindingGetV2Resp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpUserPwdChangeResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnUserPwdChangeResp(UserPwdChangeResp_t pResp, int nConnId) {
        // generated.DumpUserPwdChangeResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpBindingDelResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnBindingDelResp(BindingDelResp_t pResp, int nConnId) {
        // generated.DumpBindingDelResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpReadWifiConfigResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnReadWifiConfigResp(ReadWifiConfigResp_t pResp, int nConnId) {
        // generated.DumpReadWifiConfigResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpDeviceOnlineStateResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnDeviceOnlineStateResp(DeviceOnlineStateResp_t pResp, int nConnId) {
        // generated.DumpDeviceOnlineStateResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpStateResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnStateResp(StateResp_t pResp, int nConnId) {
        // generated.DumpStateResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpGasWaterHeaterStatusResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnGasWaterHeaterStatusResp(GasWaterHeaterStatusResp_t pResp, int nConnId) {
        // generated.DumpGasWaterHeaterStatusResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpDERYStatusResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnDERYStatusResp(DERYStatusResp_t pResp, int nConnId) {
        // generated.DumpDERYStatusResp(pResp);
    }

    /**
    * @param pResp
    *            可调用 generated.DumpHeartbeatResp(pResp) 方便地输出 pResp 的各个值到 logcat，不须手写代码逐个获取。
    * */
    @Override
    public void OnHeartbeatResp(HeartbeatResp_t pResp, int nConnId) {
        // generated.DumpHeartbeatResp(pResp);
    }

}
