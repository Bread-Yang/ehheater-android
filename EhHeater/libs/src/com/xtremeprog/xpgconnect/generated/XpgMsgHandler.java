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

        String [] mResult;
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
                case XPGConnectClient.ON_HTTP_RESP:
                     myActivity.onHTTPResp(msg.arg1,(String) msg.obj);
                     break;
                case XPGConnectClient.ON_WAN_LOGIN_RESP:
                     myActivity.onWanLoginResp(msg.arg1,msg.arg2);
                     break;
                case XPGConnectClient.ON_V4_LOGIN:
                     mResult = (String [])msg.obj;                     myActivity.onV4Login(msg.arg1,mResult[0],mResult[1],mResult[2]);
                     break;
                case XPGConnectClient.ON_V4_GET_MY_BINDINGS:
                     myActivity.onV4GetMyBindings(msg.arg1,(XpgEndpoint)msg.obj);
                     break;
                case XPGConnectClient.ON_V4_QUERY_DEVICE:
                     mResult = (String [])msg.obj;                     myActivity.onV4QueryDevice(msg.arg1,mResult[0],mResult[1]);
                     break;
                case XPGConnectClient.ON_V4_CREATE_USER_BY_ANONYMITY:
                     mResult = (String [])msg.obj;                     myActivity.onV4CreateUserByAnonymity(msg.arg1,mResult[0],mResult[1],mResult[2]);
                     break;
                case XPGConnectClient.ON_V4_CREATE_USER_BY_NAME:
                     mResult = (String [])msg.obj;                     myActivity.onV4CreateUserByName(msg.arg1,mResult[0],mResult[1],mResult[2]);
                     break;
                case XPGConnectClient.ON_V4_CREATE_USER_BY_PHONE:
                     mResult = (String [])msg.obj;                     myActivity.onV4CreateUserByPhone(msg.arg1,mResult[0],mResult[1],mResult[2]);
                     break;
                case XPGConnectClient.ON_V4_CREATE_USER_BY_MAIL:
                     mResult = (String [])msg.obj;                     myActivity.onV4CreateUserByMail(msg.arg1,mResult[0],mResult[1],mResult[2]);
                     break;
                case XPGConnectClient.ON_V4_UPDATE_USER_NAME:
                     myActivity.onV4UpdateUserName(msg.arg1,(String)msg.obj);
                     break;
                case XPGConnectClient.ON_V4_UPDATE_PHONE:
                     myActivity.onV4UpdatePhone(msg.arg1,(String)msg.obj);
                     break;
                case XPGConnectClient.ON_V4_CHANGE_USER_PWD:
                     myActivity.onV4ChangeUserPwd(msg.arg1,(String)msg.obj);
                     break;
                case XPGConnectClient.ON_V4_CHANGE_USER_PHONE:
                     myActivity.onV4ChangeUserPhone(msg.arg1,(String)msg.obj);
                     break;
                case XPGConnectClient.ON_V4_CHANGE_USER_MAIL:
                     myActivity.onV4ChangeUserMail(msg.arg1,(String)msg.obj);
                     break;
                case XPGConnectClient.ON_V4_GET_DEVICE_INFO:
                     myActivity.onV4GetDeviceInfo(msg.arg1,(XpgEndpoint)msg.obj);
                     break;
                case XPGConnectClient.ON_V4_GET_MOBILE_AUTH_CODE:
                     myActivity.onV4GetMobileAuthCode(msg.arg1);
                     break;
                case XPGConnectClient.ON_V4_VERIFY_MOBILE_AUTH_CODE:
                     myActivity.onV4VerifyMobileAuthCode(msg.arg1);
                     break;
                case XPGConnectClient.ON_V4_RECOVER_PWD_BY_PHONE:
                     myActivity.onV4RecoverPwdByPhone(msg.arg1);
                     break;
                case XPGConnectClient.ON_V4_RECOVER_PWD_BY_MAIL:
                     myActivity.onV4RecoverPwdByMail(msg.arg1);
                     break;
                case XPGConnectClient.ON_V4_BIND_DEVICE:
                     mResult = (String [])msg.obj;                     myActivity.onV4BindDevce(msg.arg1,mResult[0],mResult[1]);
                     break;
                case XPGConnectClient.ON_V4_UNBIND_DEVICE:
                     mResult = (String [])msg.obj;                     myActivity.onV4UnbindDevice(msg.arg1,mResult[0],mResult[1]);
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
                     myActivity.OnEasylinkV3Resp(
                         (EasylinkV3Resp_t) msg.obj); // arg1 = nConnId
                     break;
                case 6:
                     myActivity.OnBootstrapResp(
                         (BootstrapResp_t) msg.obj); // arg1 = nConnId
                     break;
                case 7:
                     myActivity.OnPasscodeResp(
                         (PasscodeResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 8:
                     myActivity.OnLanLoginResp(
                         (LanLoginResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 9:
                     myActivity.OnModuleVersionResp(
                         (ModuleVersionResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 10:
                     myActivity.OnWifiListResp(
                         (WifiListResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 11:
                     myActivity.OnSerialPortConfigResp(
                         (SerialPortConfigResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 12:
                     myActivity.OnUserRegisterResp(
                         (UserRegisterResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 13:
                     myActivity.OnBindingSetResp(
                         (BindingSetResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 14:
                     myActivity.OnBindingGetResp(
                         (BindingGetResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 15:
                     myActivity.OnBindingGetV2Resp(
                         (BindingGetV2Resp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 16:
                     myActivity.OnUserPwdChangeResp(
                         (UserPwdChangeResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 17:
                     myActivity.OnBindingDelResp(
                         (BindingDelResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 18:
                     myActivity.OnReadWifiConfigResp(
                         (ReadWifiConfigResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 19:
                     myActivity.OnDeviceOnlineStateResp(
                         (DeviceOnlineStateResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 20:
                     myActivity.OnStateResp(
                         (StateResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 21:
                     myActivity.OnGasWaterHeaterStatusResp(
                         (GasWaterHeaterStatusResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 22:
                     myActivity.OnDERYStatusResp(
                         (DERYStatusResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
                case 23:
                     myActivity.OnHeartbeatResp(
                         (HeartbeatResp_t) msg.obj, msg.arg1); // arg1 = nConnId
                     break;
            }
            super.handleMessage(msg);
        }
    }
