package com.xtremeprog.xpgconnect.generated;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.listener.*;
import com.xtremeprog.xpgconnect.generated.*;

    public class XpgMsgHandler extends Handler
    {
        private GeneratedActivity myActivity = null;

        public XpgMsgHandler(GeneratedActivity activity) { myActivity = activity; }

        public void handleMessage(Message msg) {
            if (null == myActivity) return;
            switch (msg.what) {
                case XPGConnectClient.ON_INITED:
                     myActivity.onInited(msg.arg1);
                     break;
                case XPGConnectClient.ON_DEVICE_FOUND:
                     myActivity.onDeviceFound((XpgEndpoint) msg.obj);
                     break;
                case XPGConnectClient.ON_EASYLINK_RESP:
                     myActivity.onEasyLinkResp((XpgEndpoint) msg.obj);
                     break;
                case XPGConnectClient.ON_LOGIN_CLOUD_RESP:
                     myActivity.onLoginCloudResp(msg.arg1, (String) msg.obj);
                     break;
                case XPGConnectClient.ON_TCP_PACKET:
                     myActivity.onTcpPacket((byte[]) msg.obj, msg.arg1);
                     break;
                case XPGConnectClient.ON_CONNECT_EVENT:
                     myActivity.onConnectEvent(msg.arg1, msg.arg2);
                     break;
                case XPGConnectClient.ON_WRITE_EVENT:
                     myActivity.onWriteEvent(msg.arg1, msg.arg2);
                     break;
                case XPGConnectClient.ON_VERSION_EVENT:
                     break;
                case 0:
                     myActivity.OnWriteWifiConfigResp(
                         (WriteWifiConfigResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 1:
                     myActivity.OnOnboardingSetResp(
                         (OnboardingSetResp_t) msg.obj); // arg1 = nConnId
                     break;
                case 2:
                     myActivity.OnDiscoveryV1Resp(
                         (DiscoveryV1Resp_t) msg.obj); // arg1 = nConnId
                     break;
                case 3:
                     myActivity.OnDiscoveryV3Resp(
                         (DiscoveryV3Resp_t) msg.obj); // arg1 = nConnId
                     break;
                case 4:
                     myActivity.OnEasylinkResp(
                         (EasylinkResp_t) msg.obj); // arg1 = nConnId
                     break;
                case 5:
                     myActivity.OnBootstrapResp(
                         (BootstrapResp_t) msg.obj); // arg1 = nConnId
                     break;
                case 6:
                     myActivity.OnPasscodeResp(
                         (PasscodeResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 7:
                     myActivity.OnLanLoginResp(
                         (LanLoginResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 8:
                     myActivity.OnModuleVersionResp(
                         (ModuleVersionResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 9:
                     myActivity.OnWifiListResp(
                         (WifiListResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 10:
                     myActivity.OnSerialPortConfigResp(
                         (SerialPortConfigResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 11:
                     myActivity.OnUserRegisterResp(
                         (UserRegisterResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 12:
                     myActivity.OnBindingSetResp(
                         (BindingSetResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 13:
                     myActivity.OnBindingGetResp(
                         (BindingGetResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 14:
                     myActivity.OnBindingGetV2Resp(
                         (BindingGetV2Resp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 15:
                     myActivity.OnUserPwdChangeResp(
                         (UserPwdChangeResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 16:
                     myActivity.OnBindingDelResp(
                         (BindingDelResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 17:
                     myActivity.OnReadWifiConfigResp(
                         (ReadWifiConfigResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 18:
                     myActivity.OnDeviceOnlineStateResp(
                         (DeviceOnlineStateResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 19:
                     myActivity.OnStateResp(
                         (StateResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 20:
                     myActivity.OnGasWaterHeaterStatusResp(
                         (GasWaterHeaterStatusResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 21:
                     myActivity.OnHeartbeatResp(
                         (HeartbeatResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
            }
            super.handleMessage(msg);
        }
    }
