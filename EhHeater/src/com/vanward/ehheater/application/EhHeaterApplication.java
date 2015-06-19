package com.vanward.ehheater.application;

import android.app.Application;
import android.graphics.Typeface;
import cn.jpush.android.api.JPushInterface;

import com.vanward.ehheater.statedata.EhState;
import com.vanward.ehheater.util.L;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.GasWaterHeaterStatusResp_t;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.listener.ClientListener;

public class EhHeaterApplication extends Application implements ClientListener {

	private static final String TAG = "EhHeaterApplication";

	public static Typeface number_tf;

	public static float device_density;

	public static EhState currentEhState;

	public static GasWaterHeaterStatusResp_t currentGasHeaterStatus;

	@Override
	public void onCreate() {
		super.onCreate();

		XPGConnectClient.initClient(this);
		
		JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush

		// 加这行显示log详情
		// XPGConnectClient.xpgcIoctl(XPG_CONFIG_KEY.LOG_LEVEL.swigValue(),3);

		// XPGConnectClient.xpgcIoctl(XPG_CONFIG_KEY.DEVICE_FOUND_TIMER.swigValue(),5);

		// LogcatFileManager.getInstance().startLogcatManager(getBaseContext());

		// EhHeaterApplication.number_tf = Typeface.createFromAsset(getAssets(),
		// "fonts/number.otf");
		//
		EhHeaterApplication.device_density = this.getResources()
				.getDisplayMetrics().density;

		L.e(this, "density : " + EhHeaterApplication.device_density);

		// L.e(this, "getResources().getConfiguration().screenWidthDp : " +
		// getResources().getConfiguration().screenWidthDp);
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
	public void onConnectEvent(int connId, int event) {
		L.e(this, "onConnectEvent@EhHeaterApplication@: " + connId + " - "
				+ event);
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {

	}

	@Override
	public void onWriteEvent(int result, int connId) {

	}

	@Override
	public void onVersionEvent(int key, int value, int connId) {

	}

	@Override
	public void onTcpPacket(byte[] data, int connId) {

	}

	@Override
	public void onSendPacket(byte[] data, int connId) {

	}

	@Override
	public void onHTTPResp(int result, String buffer) {

	}

	@Override
	public void onWanLoginResp(int result, int connId) {

	}

	@Override
	public void onV4Login(int errorCode, String uid, String token,
			String expire_at) {

	}

	@Override
	public void onV4GetMyBindings(int errorCode, XpgEndpoint endpoint) {

	}

	@Override
	public void onV4QueryDevice(int errorCode, String did, String passcode) {

	}

	@Override
	public void onV4CreateUserByAnonymity(int errorCode, String uid,
			String token, String expire_at) {

	}

	@Override
	public void onV4CreateUserByName(int errorCode, String uid, String token,
			String expire_at) {

	}

	@Override
	public void onV4CreateUserByPhone(int errorCode, String uid, String token,
			String expire_at) {

	}

	@Override
	public void onV4CreateUserByMail(int errorCode, String uid, String token,
			String expire_at) {

	}

	@Override
	public void onV4UpdateUserName(int errorCode, String updatedAt) {

	}

	@Override
	public void onV4UpdatePhone(int errorCode, String updatedAt) {

	}

	@Override
	public void onV4ChangeUserPwd(int errorCode, String updatedAt) {

	}

	@Override
	public void onV4ChangeUserPhone(int errorCode, String updatedAt) {

	}

	@Override
	public void onV4ChangeUserMail(int errorCode, String updatedAt) {

	}

	@Override
	public void onV4GetDeviceInfo(int errorCode, XpgEndpoint endpoint) {

	}

	@Override
	public void onV4GetMobileAuthCode(int errorCode) {

	}

	@Override
	public void onV4VerifyMobileAuthCode(int errorCode) {

	}

	@Override
	public void onV4RecoverPwdByPhone(int errorCode) {

	}

	@Override
	public void onV4RecoverPwdByMail(int errorCode) {

	}

	@Override
	public void onV4BindDevce(int errorCode, String successString,
			String failString) {

	}

	@Override
	public void onV4UnbindDevice(int errorCode, String successString,
			String failString) {

	}

	@Override
	public void onAirLinkResp(XpgEndpoint endpoint) {

	}

}
