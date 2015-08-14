package com.vanward.ehheater.application;

import android.app.Application;
import android.graphics.Typeface;
import cn.jpush.android.api.JPushInterface;

import com.vanward.ehheater.statedata.EhState;
import com.vanward.ehheater.util.L;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.GasWaterHeaterStatusResp_t;
import com.xtremeprog.xpgconnect.generated.XPG_CONFIG_KEY;
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
		
//		Locale locale = this.getResources().getConfiguration().locale;
//
//		StringBuffer sb = new StringBuffer();
//
//		sb.append("locale.toString():" + locale.toString());
//		sb.append("\nlocale.getCountry():" + locale.getCountry());
//		sb.append("\nlocale.getDisplayCountry():" + locale.getDisplayCountry());
//		sb.append("\nlocale.getDisplayLanguage():" + locale.getDisplayLanguage());
//		sb.append("\nlocale.getDisplayName():" + locale.getDisplayName());
//		sb.append("\nlocale.getDisplayVariant():" + locale.getDisplayVariant());
//		sb.append("\nlocale.getISO3Country():" + locale.getISO3Country());
//		sb.append("\nlocale.getISO3Language():" + locale.getISO3Language());
//		sb.append("\nlocale.getLanguage():" + locale.getLanguage());
//		sb.append("\nlocale.getVariant():" + locale.getVariant());
//
//		L.e(this, sb.toString());

		XPGConnectClient.initClient(this);
		
		JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush

		// 加这行显示log详情
		XPGConnectClient.xpgcIoctl(XPG_CONFIG_KEY.LOG_LEVEL.swigValue(),3);
		XPGConnectClient.xpgcInitSaveDNS(this);

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceFound(XpgEndpoint endpoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEasyLinkResp(XpgEndpoint endpoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAirLinkResp(XpgEndpoint endpoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWriteEvent(int result, int connId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVersionEvent(int key, int value, int connId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTcpPacket(byte[] data, int connId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendPacket(byte[] data, int connId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHTTPResp(int result, String buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWanLoginResp(int result, int connId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4Login(int errorCode, String uid, String token,
			String expire_at) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4GetMyBindings(int errorCode, XpgEndpoint endpoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4QueryDevice(int errorCode, String did, String passcode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4CreateUserByAnonymity(int errorCode, String uid,
			String token, String expire_at) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4CreateUserByName(int errorCode, String uid, String token,
			String expire_at) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4CreateUserByPhone(int errorCode, String uid, String token,
			String expire_at) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4CreateUserByMail(int errorCode, String uid, String token,
			String expire_at) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4UpdateUserName(int errorCode, String updatedAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4UpdatePhone(int errorCode, String updatedAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4ChangeUserPwd(int errorCode, String updatedAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4ChangeUserPhone(int errorCode, String updatedAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4ChangeUserMail(int errorCode, String updatedAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4GetDeviceInfo(int errorCode, XpgEndpoint endpoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4GetMobileAuthCode(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4VerifyMobileAuthCode(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4RecoverPwdByPhone(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4RecoverPwdByMail(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4BindDevce(int errorCode, String successString,
			String failString) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onV4UnbindDevice(int errorCode, String successString,
			String failString) {
		// TODO Auto-generated method stub
		
	}
}
