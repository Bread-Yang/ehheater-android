package com.xtremeprog.xpgconnect.generated;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.os.Handler;
import android.os.Message;
import com.xtremeprog.xpgconnect.generated.*;

public class GeneratedJniJava {
    private static List<GeneratedJniListener> lstListeners = new LinkedList<GeneratedJniListener>();
    private static List<Handler> lstHandlers = new LinkedList<Handler>();

    public static XpgEndpoint GetXpgEndpoint(long cPtr, boolean cMemoryOwn) {
        return (cPtr == 0) ? null : new XpgEndpoint(cPtr, cMemoryOwn);
    }

    public static void OnWriteWifiConfigResp(long cPtr, int nConnId) {
        WriteWifiConfigResp_t obj = new WriteWifiConfigResp_t(cPtr, false);
        WriteWifiConfigResp_t resp = new WriteWifiConfigResp_t();
        resp.setCommand(obj.getCommand());
        resp.setResult(obj.getResult());
        resp.setChecksum(obj.getChecksum());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(0, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnWriteWifiConfigResp(resp, nConnId);
        }
    }

    public static void OnOnboardingSetResp(long cPtr) {
        OnboardingSetResp_t obj = new OnboardingSetResp_t(cPtr, false);
        OnboardingSetResp_t resp = new OnboardingSetResp_t();
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(1, 0, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnOnboardingSetResp(resp);
        }
    }

    public static void OnDiscoveryV1Resp(long cPtr) {
        DiscoveryV1Resp_t obj = new DiscoveryV1Resp_t(cPtr, false);
        DiscoveryV1Resp_t resp = new DiscoveryV1Resp_t();
        resp.setCommand(obj.getCommand());
        resp.setMAC(obj.getMAC());
        resp.setVersion(obj.getVersion());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(2, 0, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnDiscoveryV1Resp(resp);
        }
    }

    public static void OnDiscoveryV3Resp(long cPtr) {
        DiscoveryV3Resp_t obj = new DiscoveryV3Resp_t(cPtr, false);
        DiscoveryV3Resp_t resp = new DiscoveryV3Resp_t();
        resp.setDid(obj.getDid());
        resp.setMac(obj.getMac());
        resp.setFwVer(obj.getFwVer());
        resp.setProductKey(obj.getProductKey());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(3, 0, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnDiscoveryV3Resp(resp);
        }
    }

    public static void OnEasylinkResp(long cPtr) {
        EasylinkResp_t obj = new EasylinkResp_t(cPtr, false);
        EasylinkResp_t resp = new EasylinkResp_t();
        resp.setCommand(obj.getCommand());
        resp.setMAC(obj.getMAC());
        resp.setVersion(obj.getVersion());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(4, 0, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnEasylinkResp(resp);
        }
    }

    public static void OnBootstrapResp(long cPtr) {
        BootstrapResp_t obj = new BootstrapResp_t(cPtr, false);
        BootstrapResp_t resp = new BootstrapResp_t();
        resp.setDid(obj.getDid());
        resp.setMac(obj.getMac());
        resp.setFwVer(obj.getFwVer());
        resp.setProductKey(obj.getProductKey());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(5, 0, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnBootstrapResp(resp);
        }
    }

    public static void OnPasscodeResp(long cPtr, int nConnId) {
        PasscodeResp_t obj = new PasscodeResp_t(cPtr, false);
        PasscodeResp_t resp = new PasscodeResp_t();
        resp.setPasscode(obj.getPasscode());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(6, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnPasscodeResp(resp, nConnId);
        }
    }

    public static void OnLanLoginResp(long cPtr, int nConnId) {
        LanLoginResp_t obj = new LanLoginResp_t(cPtr, false);
        LanLoginResp_t resp = new LanLoginResp_t();
        resp.setResult(obj.getResult());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(7, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnLanLoginResp(resp, nConnId);
        }
    }

    public static void OnModuleVersionResp(long cPtr, int nConnId) {
        ModuleVersionResp_t obj = new ModuleVersionResp_t(cPtr, false);
        ModuleVersionResp_t resp = new ModuleVersionResp_t();
        resp.setPiVersion(obj.getPiVersion());
        resp.setP0Version(obj.getP0Version());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(8, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnModuleVersionResp(resp, nConnId);
        }
    }

    public static void OnWifiListResp(long cPtr, int nConnId) {
        WifiListResp_t obj = new WifiListResp_t(cPtr, false);
        WifiListResp_t resp = new WifiListResp_t();
        resp.setSsid(obj.getSsid());
        resp.setSignalIntensity(obj.getSignalIntensity());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(9, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnWifiListResp(resp, nConnId);
        }
    }

    public static void OnSerialPortConfigResp(long cPtr, int nConnId) {
        SerialPortConfigResp_t obj = new SerialPortConfigResp_t(cPtr, false);
        SerialPortConfigResp_t resp = new SerialPortConfigResp_t();
        resp.setResult(obj.getResult());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(10, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnSerialPortConfigResp(resp, nConnId);
        }
    }

    public static void OnUserRegisterResp(long cPtr, int nConnId) {
        UserRegisterResp_t obj = new UserRegisterResp_t(cPtr, false);
        UserRegisterResp_t resp = new UserRegisterResp_t();
        resp.setResult(obj.getResult());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(11, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnUserRegisterResp(resp, nConnId);
        }
    }

    public static void OnBindingSetResp(long cPtr, int nConnId) {
        BindingSetResp_t obj = new BindingSetResp_t(cPtr, false);
        BindingSetResp_t resp = new BindingSetResp_t();
        resp.setResult(obj.getResult());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(12, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnBindingSetResp(resp, nConnId);
        }
    }

    public static void OnBindingGetResp(long cPtr, int nConnId) {
        BindingGetResp_t obj = new BindingGetResp_t(cPtr, false);
        BindingGetResp_t resp = new BindingGetResp_t();
        resp.setDid(obj.getDid());
        resp.setMac(obj.getMac());
        resp.setPasscode(obj.getPasscode());
        resp.setIsOnline(obj.getIsOnline());
        resp.setPiVersion(obj.getPiVersion());
        resp.setP0Version(obj.getP0Version());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(13, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnBindingGetResp(resp, nConnId);
        }
    }

    public static void OnBindingGetV2Resp(long cPtr, int nConnId) {
        BindingGetV2Resp_t obj = new BindingGetV2Resp_t(cPtr, false);
        BindingGetV2Resp_t resp = new BindingGetV2Resp_t();
        resp.setDid(obj.getDid());
        resp.setMac(obj.getMac());
        resp.setPasscode(obj.getPasscode());
        resp.setProductKey(obj.getProductKey());
        resp.setIsOnline(obj.getIsOnline());
        resp.setPiVersion(obj.getPiVersion());
        resp.setP0Version(obj.getP0Version());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(14, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnBindingGetV2Resp(resp, nConnId);
        }
    }

    public static void OnUserPwdChangeResp(long cPtr, int nConnId) {
        UserPwdChangeResp_t obj = new UserPwdChangeResp_t(cPtr, false);
        UserPwdChangeResp_t resp = new UserPwdChangeResp_t();
        resp.setResult(obj.getResult());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(15, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnUserPwdChangeResp(resp, nConnId);
        }
    }

    public static void OnBindingDelResp(long cPtr, int nConnId) {
        BindingDelResp_t obj = new BindingDelResp_t(cPtr, false);
        BindingDelResp_t resp = new BindingDelResp_t();
        resp.setResult(obj.getResult());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(16, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnBindingDelResp(resp, nConnId);
        }
    }

    public static void OnReadWifiConfigResp(long cPtr, int nConnId) {
        ReadWifiConfigResp_t obj = new ReadWifiConfigResp_t(cPtr, false);
        ReadWifiConfigResp_t resp = new ReadWifiConfigResp_t();
        resp.setCommand(obj.getCommand());
        resp.setSsid(obj.getSsid());
        resp.setKey(obj.getKey());
        resp.setChecksum(obj.getChecksum());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(17, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnReadWifiConfigResp(resp, nConnId);
        }
    }

    public static void OnDeviceOnlineStateResp(long cPtr, int nConnId) {
        DeviceOnlineStateResp_t obj = new DeviceOnlineStateResp_t(cPtr, false);
        DeviceOnlineStateResp_t resp = new DeviceOnlineStateResp_t();
        resp.setMac(obj.getMac());
        resp.setPasscode(obj.getPasscode());
        resp.setIsOnline(obj.getIsOnline());
        resp.setPiVersion(obj.getPiVersion());
        resp.setP0Version(obj.getP0Version());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(18, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnDeviceOnlineStateResp(resp, nConnId);
        }
    }

    public static void OnStateResp(long cPtr, int nConnId) {
        StateResp_t obj = new StateResp_t(cPtr, false);
        StateResp_t resp = new StateResp_t();
        resp.setHeader(obj.getHeader());
        resp.setP0_version(obj.getP0_version());
        resp.setResp_address(obj.getResp_address());
        resp.setCommand(obj.getCommand());
        resp.setOn_off(obj.getOn_off());
        resp.setSystem_running_state(obj.getSystem_running_state());
        resp.setFunction_state(obj.getFunction_state());
        resp.setOrder_state(obj.getOrder_state());
        resp.setInner1_temp(obj.getInner1_temp());
        resp.setInner2_temp(obj.getInner2_temp());
        resp.setInner3_temp(obj.getInner3_temp());
        resp.setSetting_temp(obj.getSetting_temp());
        resp.setSetting_power(obj.getSetting_power());
        resp.setRemaining_heating_time(obj.getRemaining_heating_time());
        resp.setRemaining_hot_water(obj.getRemaining_hot_water());
        resp.setError(obj.getError());
        resp.setPower_consumption(obj.getPower_consumption());
        resp.setHeating_tube_time(obj.getHeating_tube_time());
        resp.setMachine_not_heating_time(obj.getMachine_not_heating_time());
        resp.setChecksum(obj.getChecksum());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(19, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnStateResp(resp, nConnId);
        }
    }

    public static void OnGasWaterHeaterStatusResp(long cPtr, int nConnId) {
        GasWaterHeaterStatusResp_t obj = new GasWaterHeaterStatusResp_t(cPtr, false);
        GasWaterHeaterStatusResp_t resp = new GasWaterHeaterStatusResp_t();
        resp.setHeader(obj.getHeader());
        resp.setP0_version(obj.getP0_version());
        resp.setResp_address(obj.getResp_address());
        resp.setCommand(obj.getCommand());
        resp.setOn_off(obj.getOn_off());
        resp.setPriority(obj.getPriority());
        resp.setFunction_state(obj.getFunction_state());
        resp.setWater_function(obj.getWater_function());
        resp.setSetWater_power(obj.getSetWater_power());
        resp.setSetWater_cumulative(obj.getSetWater_cumulative());
        resp.setCustomFunction(obj.getCustomFunction());
        resp.setCustomWaterTemperture(obj.getCustomWaterTemperture());
        resp.setCustomWaterProportion(obj.getCustomWaterProportion());
        resp.setCallingDisp(obj.getCallingDisp());
        resp.setSprinkler(obj.getSprinkler());
        resp.setFlame(obj.getFlame());
        resp.setAirFan(obj.getAirFan());
        resp.setFirePower(obj.getFirePower());
        resp.setErrorCode(obj.getErrorCode());
        resp.setOxygenWarning(obj.getOxygenWarning());
        resp.setCoOverproofWarning(obj.getCoOverproofWarning());
        resp.setTargetTemperature(obj.getTargetTemperature());
        resp.setIncomeTemperature(obj.getIncomeTemperature());
        resp.setOutputTemperature(obj.getOutputTemperature());
        resp.setNowVolume(obj.getNowVolume());
        resp.setCumulativeVolume(obj.getCumulativeVolume());
        resp.setCumulativeGas(obj.getCumulativeGas());
        resp.setCumulativeUseTime(obj.getCumulativeUseTime());
        resp.setCumulativeOpenValveTimes(obj.getCumulativeOpenValveTimes());
        resp.setNow_efficiency(obj.getNow_efficiency());
        resp.setPreheatingModel(obj.getPreheatingModel());
        resp.setPresetTemperature(obj.getPresetTemperature());
        resp.setPreheatingOneHour(obj.getPreheatingOneHour());
        resp.setPreheatingOneMin(obj.getPreheatingOneMin());
        resp.setPreheatingTwoHour(obj.getPreheatingTwoHour());
        resp.setPreheatingTwoMin(obj.getPreheatingTwoMin());
        resp.setFreezeProofingWarning(obj.getFreezeProofingWarning());
        resp.setMercurycontent(obj.getMercurycontent());
        resp.setReturn_water_temperature(obj.getReturn_water_temperature());
        resp.setReservation_one(obj.getReservation_one());
        resp.setReservation_two(obj.getReservation_two());
        resp.setChecksum(obj.getChecksum());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(20, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnGasWaterHeaterStatusResp(resp, nConnId);
        }
    }

    public static void OnHeartbeatResp(long cPtr, int nConnId) {
        HeartbeatResp_t obj = new HeartbeatResp_t(cPtr, false);
        HeartbeatResp_t resp = new HeartbeatResp_t();
        resp.setHeader(obj.getHeader());
        resp.setP0_version(obj.getP0_version());
        resp.setResp_address(obj.getResp_address());
        resp.setCommand(obj.getCommand());
        resp.setChecksum(obj.getChecksum());
        for (Handler handler : lstHandlers)
        {
            Message msg = handler.obtainMessage(21, nConnId, 0, resp);
            handler.sendMessage(msg);
        }
        for (GeneratedJniListener listener : lstListeners)
        {
            listener.OnHeartbeatResp(resp, nConnId);
        }
    }

    public static void AddHandler(Handler handler)
    {
        if (null == handler)
            return;
        if (!lstHandlers.contains(handler))
            lstHandlers.add(handler);
    }

    public static void RemoveHandler(Handler handler)
    {
        if (lstHandlers.contains(handler))
            lstHandlers.remove(handler);
    }

    public static void AddDelegate(GeneratedJniListener listener)
    {
        if (null == listener)
            return;
        if (!lstListeners.contains(listener))
            lstListeners.add(listener);
    }

    public static void RemoveDelegate(GeneratedJniListener listener)
    {
        if (lstListeners.contains(listener))
            lstListeners.remove(listener);
    }
}
