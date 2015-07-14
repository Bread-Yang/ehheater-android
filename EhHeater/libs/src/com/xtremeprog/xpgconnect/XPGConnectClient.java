package com.xtremeprog.xpgconnect;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.security.Security;

import com.xtremeprog.xpgconnect.generated.*;
import com.xtremeprog.xpgconnect.listener.ClientListener;
import com.xtremeprog.xpgconnect.listener.DoneListener;

/**
 * XPGConnectClient. <br/>
 * {@link Entity} can be a {@link Device} or a {@link Room}. <br/>
 * Entity has many {@link EntityAttr}.
 */
public class XPGConnectClient {
	public static final String TAG = "xpgcActivity";

	public static final int ON_INITED = -8;
	public static final int ON_DEVICE_FOUND = -1;
	public static final int ON_EASYLINK_RESP = -2;
	public static final int ON_LOGIN_CLOUD_RESP = -3;
	public static final int ON_TCP_PACKET = -4;
	public static final int ON_CONNECT_EVENT = -5;
	public static final int ON_WRITE_EVENT = -6;
	public static final int ON_VERSION_EVENT = -7;
	public static final int ON_SEND_PACKET = -9;
	public static final int ON_HTTP_RESP = -10;

	//for V4 SDK
	public static final int ON_V4_LOGIN = -11;
	public static final int ON_V4_GET_MY_BINDINGS = -12;
	public static final int ON_V4_QUERY_DEVICE = -13;
	public static final int ON_V4_CREATE_USER_BY_ANONYMITY = -14;
	public static final int ON_V4_CREATE_USER_BY_NAME = -15;
	public static final int ON_V4_CREATE_USER_BY_PHONE = -16;
	public static final int ON_V4_CREATE_USER_BY_MAIL = -17;
	public static final int ON_V4_UPDATE_USER_NAME = -18;
	public static final int ON_V4_UPDATE_PHONE = -19;
	public static final int ON_V4_CHANGE_USER_PWD = -20;
	public static final int ON_V4_CHANGE_USER_PHONE = -21;
	public static final int ON_V4_CHANGE_USER_MAIL = -22;
	public static final int ON_V4_GET_DEVICE_INFO = -23;
	public static final int ON_V4_GET_MOBILE_AUTH_CODE = -24;
	public static final int ON_V4_VERIFY_MOBILE_AUTH_CODE = -25;
	public static final int ON_V4_RECOVER_PWD_BY_PHONE = -26;
	public static final int ON_V4_RECOVER_PWD_BY_MAIL = -27;

	public static final int ON_WAN_LOGIN_RESP = -28;

	public static final int ON_V4_BIND_DEVICE = -29;
	public static final int ON_V4_UNBIND_DEVICE = -30;
	

	public static final int ON_AIRLINK_RESP = -31;
	static {
		System.loadLibrary("xpgconnectcli");
	}

	public static native int xpgcInit(byte[] clientId);
	public static native String getVersion();
	public static native int xpgcFindDevice();
	public static native void xpgcStartDiscovery();
	public static native void xpgcStopDiscovery();
	public static native int xpgcRegister(String uid, String pwd);
	public static native int xpgcConnectDevice(String addr, int tcpPort);
	public static native int xpgcConnectDeviceAsync(String addr, int tcpPort);
	public static native int xpgcConnectCloud(String addr, int port);
	public static native int xpgcConnectCloudAsync(String addr, int port);
	public static native int xpgcConnectMqtt(String ip, int port,
		String mac, String did, String passcode, int piVersion);
	public static native int xpgcConnectMqttAsync(String ip, int port,
		String mac, String did, String passcode, int piVersion);
	public static native int xpgcConnectAddr(String ip, int port, int piVersion);
	public static native int xpgcConnectAddrAsync(String ip, int port, int piVersion);
	/**
	 * @param mode
	 *            {@link XPG_WAN_LAN}
	 * @param connType
	 *            {@link XPG_CONN_TYPE}
	 */
	public static native int xpgcConnect2(String addr, int port,
		int mode,
	    int connType,
	    String mac, String did, String passcode,
	    int piVersion);
	public static native int xpgcConnect2Async(String addr, int port,
		int mode,
	    int connType,
	    String mac, String did, String passcode,
	    int piVersion);
	public static native int xpgcLogin(int connId, String szUid, String szPass);
	public static native int xpgcEnableCtrl(int connId, String szDid, String szPass);
	public static native int xpgcDisconnectAsync(int connId);
	public static native void xpgcIoctl(int key, int value);
	public static native String xpgcIoctlString(int key, String value);
	public static native int xpgcBroadcast(byte[] data, int port);
	public static native int xpgcWrite(byte[] data, int connId);
	public static native int xpgcWriteAsync(byte[] data, int connId);
	public static native int xpgcReset();
	/**
	 *  获取手机验证码
	 *
	 * @param appId
	 *           
	 * @param phone
	 *           手机号码
	 */
	public static native void xpgc4GetMobileAuthCode(String appId,String phone);
	/**
	 *  检验手机验证码
	 *
	 * @param appId
	 *           
	 * @param phone
	 *           手机号码
	 * @param code
	 *           验证码
	 */
	public static native void xpgc4VerifyMobileAuthCode(String appId,String phone,String code);
	/**
	 *  创建匿名帐户
	 *
	 * @param appId
	 *           
	 * @param phoneID
	 *           手机唯一识别码
	 */
	public static native void xpgc4CreateUserByAnonymity(String appId,String phoneID);
	/**
	 *  创建普通帐户
	 *
	 * @param appId
	 *           
	 * @param userID
	 *           用户名
	 * @param pwd
	 *           密码
	 */
	public static native void xpgc4CreateUserByName(String appId,String userID,String pwd);
	/**
	 *  创建手机绑定帐户
	 *
	 * @param appId
	 *           
	 * @param userID
	 *           用户名
	 * @param pwd
	 *           密码
	 * @param code
	 *           验证码
	 */
	public static native void xpgc4CreateUserByPhone(String appId,String userID,String pwd,String code);
	/**
	 *  创建邮件绑定帐户
	 *
	 * @param appId
	 *           
	 * @param mail
	 *           邮箱
	 * @param pwd
	 *           密码
	 */
	public static native void xpgc4CreateUserByMail(String appId,String mail,String pwd);
	/**
	 *  登录机智云
	 *
	 * @param appId
	 *           
	 * @param userID
	 *           用户名
	 * @param pwd
	 *           密码
	 */
	public static native void xpgc4Login(String appId,String userID,String pwd);
	/**
	 *  用手机重置密码
	 *
	 * @param appId
	 *           
	 * @param userID
	 *           用户名
	 * @param code
	 *           验证码
	 * @param newPwd
	 *           新密码
	 */
	public static native void xpgc4RecoverPwdByPhone(String appId,String userID,String code,String newPwd);
	/**
	 *  用邮箱重置密码
	 *
	 * @param appId
	 *           
	 * @param mail
	 *           邮箱
	 */
	public static native void xpgc4RecoverPwdByMail(String appId,String mail);

	public static native void xpgc4UpdateUsername(String appId,String token,String userID,String pwd);
	public static native void xpgc4UpdatePhone(String appId,String token,String phone,String pwd,String code);
	public static native void xpgc4ChangeUserPwd(String appId,String token,String pwd,String newPwd);

    public static native void xpgc4ChangeUserMail(String appId,String token,String mail);
    public static native void xpgc4ChangeUserPhone(String appId,String token,String code,String newPhone);
    public static native void xpgc4QueryDevice(String appId,String token,String productKey,String mac);
    public static native void xpgc4BindDevice(String appId,String token,String did,String passcode,String remark);
    public static native void xpgc4GetMyBindings(String appId,String token,int limit,int skip);
    public static native void xpgc4GetDeviceInfo(String appId,String token,String did);
    public static native void xpgc4UnbindDevice(String appId,String token,String did);


	public static native void xpgcLogin2Wan(String uid,String password,String did,String passcode);
	public static native void xpgcLogin2Lan(String ip,String passcode);

	private static native void xpgcSetLogPath(String filePath);
	private static native void xpgcSetIpPath(String filePath);


	public static void xpgcInitLogPath(Context ctx)
	{	

		String directory = android.os.Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/"
				+ XPGConnectClient.getApplicationName(ctx)
				+ "/logs/";
		File tempFile = new File( directory );
			if ( !tempFile.exists() )
				tempFile.mkdirs();

		directory=directory+XPGConnectClient.getStrDate()+"-log.txt";
		xpgcSetLogPath(directory);
	}

	public static void xpgcInitSaveDNS(Context ctx)
	{
		//change save time for DNS failure results to 0
		Security.setProperty("networkaddress.cache.negative.ttl", "0");

		//create saving path
		String directory = android.os.Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/"
				+ "Gizwits"
				+"/";
		File tempFile = new File( directory );
			if ( !tempFile.exists() )
				tempFile.mkdirs();

		xpgcSetIpPath(directory);
	}

	//获取应用程序的名称
	private static String getApplicationName(Context ctx) { 
		PackageManager packageManager = null; 
		ApplicationInfo applicationInfo = null; 
		try 
		{ 
		packageManager = ctx.getApplicationContext().getPackageManager(); 
		applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), 0); 
		} catch (PackageManager.NameNotFoundException e) { 
		applicationInfo = null; 
		}
		String applicationName = 
		(String) packageManager.getApplicationLabel(applicationInfo); 
		
		return applicationName; 
	} 

	/** 返回当前日期的格式化（yyyy-MM-dd）表示 */
	public static String getStrDate() {
		SimpleDateFormat dd = new SimpleDateFormat("yyyy-MM-dd");
		return dd.format(new Date());
	}

	private static native long GetEndpointPtr(int connId);
	public static XpgEndpoint GetEndpoint(int connId)
	{
		long cPtr = GetEndpointPtr(connId);
		if (0 == cPtr)
			return null;
		else
			return GeneratedJniJava.GetXpgEndpoint(cPtr, false);
	}

	// listeners
	private static List<ClientListener> lstClientListeners = new LinkedList<ClientListener>();
	private static List<GeneratedActivity> lstActivities = new LinkedList<GeneratedActivity>();
	
	public static void AddDelegate(ClientListener listener)
	{
		if (null == listener)
			return ;
		if (!lstClientListeners.contains(listener))
			lstClientListeners.add(listener);
		
	}
	
	public static void RemoveDelegate(ClientListener listener)
	{
		if (lstClientListeners.contains(listener))
			lstClientListeners.remove(listener);
	}
	
	public static void AddActivity(GeneratedActivity activity)
	{
		if (null == activity)
			return ;
		if (!lstActivities.contains(activity))
			lstActivities.add(activity);
			
	}
	
	public static void RemoveActivity(GeneratedActivity activity)
	{
		if (lstActivities.contains(activity))
			lstActivities.remove(activity);
	}
	
	/**
	 * Initialize XPG Connect Client. Should call this method before call any
	 * other client method.
	 * 
	 * @category API
	 * @param listener
	 *            {@link ClientListener}
	 * @return 0 if success, else return -1
	 */
	public static int initClient(ClientListener listener) {
		if (listener == null) return -1;

		AddDelegate(listener);


		byte[] clientId = Coding.EncodeUUID(UUID.randomUUID());

		return xpgcInit(clientId);
	}

	public static int initClient(GeneratedActivity activity) {
		if (activity == null) return -1;

		AddActivity(activity);

		byte[] clientId = Coding.EncodeUUID(UUID.randomUUID());

		return xpgcInit(clientId);
	}

	public static void onInited(int result) {
		Log.i(TAG, String.format("%s, result = %d",
			Utils.GetFileLineMethod(), result));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_INITED, result, 0, null);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onInited(result);
	}

	/**
	 * @category JNI Callback
	 * @param mac
	 */
	public static void onLoginCloudResp(int result, String mac) {
		Log.i(TAG, String.format("%s, result = %d, mac = %s",
			Utils.GetFileLineMethod(), result, mac));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_LOGIN_CLOUD_RESP, result, 0, mac);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onLoginCloudResp(result, mac);
	}

	/**
	 * @category JNI Callback
	 * @param packet
	 * @param connId
	 */
	public static void onTcpPacket(byte[] data, int connId) {
		Log.i(TAG, String.format("%s, data len = %d, connId = %d",
			Utils.GetFileLineMethod(), data.length, connId));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_TCP_PACKET, connId, 0, data);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onTcpPacket(data, connId);
	}

	/**
	 * @category JNI Callback
	 * @param packet
	 * @param connId
	 */
	public static void onSendPacket(byte[] data, int connId) {
		Log.i(TAG, String.format("%s, data len = %d, connId = %d",
			Utils.GetFileLineMethod(), data.length, connId));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_SEND_PACKET, connId, 0, data);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onSendPacket(data, connId);
	}
	
	/**
	 * @category JNI Callback
	 * @param ip
	 * @param ipStr
	 * @param macStr
	 */
	public static void onDeviceFound(long cPtr) {
		XpgEndpoint res = GeneratedJniJava.GetXpgEndpoint(cPtr, true);
		Log.i(TAG, String.format("%s, mac = %s", Utils.GetFileLineMethod(), res.getSzMac()));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_DEVICE_FOUND, 0, 0, res);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
		{
			listener.onDeviceFound(res);
		}
	}

	public static void onEasyLinkResp(long cPtr) {
		XpgEndpoint res = GeneratedJniJava.GetXpgEndpoint(cPtr, true);
		Log.i(TAG, String.format("%s, mac = %s", Utils.GetFileLineMethod(), res.getSzMac()));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_EASYLINK_RESP, 0, 0, res);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
		{
			listener.onEasyLinkResp(res);
		}
	}

	public static void onAirLinkResp(long cPtr) {
		XpgEndpoint res = GeneratedJniJava.GetXpgEndpoint(cPtr, true);
		Log.i(TAG, String.format("%s, mac = %s", Utils.GetFileLineMethod(), res.getSzMac()));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_AIRLINK_RESP, 0, 0, res);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
		{
			listener.onAirLinkResp(res);
		}
	}

	/**
	 * @category JNI Callback
	 * @param connId
	 */
	public static void onConnectEvent(int connId, int event) {
		Log.i(TAG, String.format("%s, connId = %d, event = %d",
			Utils.GetFileLineMethod(), connId, event));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_CONNECT_EVENT, connId, event);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onConnectEvent(connId, event);
	}

	/**
	 * @category JNI Callback
	 * @param result
	 * @param connId
	 */
	public static void onWriteEvent(int result, int connId) {
		Log.i(TAG, String.format("%s, result = %d, connId = %d",
			Utils.GetFileLineMethod(), result, connId));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_WRITE_EVENT, result, connId);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onWriteEvent(result, connId);
	}

	/**
	 * @category JNI Callback
	 * @param key
	 * @param value
	 * @param connId
	 */
	public static void onVersionEvent(int key, int value, int connId) {
		Log.i(TAG, String.format("%s, key = %d, value = %d, connId = %d",
			Utils.GetFileLineMethod(), key, value, connId));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_VERSION_EVENT, key, value, connId);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onVersionEvent(key, value, connId);
	}

	/**
	 * @category JNI Callback
	 * @param result
	 * @param buffer
	 */
	public static void onHTTPResp(int result, String buffer) {
		Log.i(TAG, String.format("%s, result = %d, buffer = %s",
			Utils.GetFileLineMethod(), result, buffer));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_HTTP_RESP, result, 0, buffer);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onHTTPResp(result, buffer);
	}

	/**
	 * @category JNI Callback
	 * @param result
	 * @param connId
	 */
	public static void onWanLoginResp(int result, int connId) {
		Log.i(TAG, String.format("%s, result = %d, connId = %d",
			Utils.GetFileLineMethod(), result, connId));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_WAN_LOGIN_RESP, result, connId, null);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onWanLoginResp(result, connId);
	}

	//for SDK respone
	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param uid
	 * @param token
	 * @param expire_at
	 */
	public static void onV4Login(int errorCode, String uid,String token,String expire_at) {
		Log.i(TAG, String.format("%s, errorCode = %d, uid = %s, token = %s, expire_at = %s",
			Utils.GetFileLineMethod(), errorCode, uid,token,expire_at));
		String [] mResult={uid,token,expire_at};
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_LOGIN, errorCode, 0, mResult);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4Login(errorCode, uid, token, expire_at);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param did
	 * @param passcode
	 */
	public static void onV4GetMyBindings(int errorCode, long cPtr) {
		Log.i(TAG, String.format("%s, errorCode = %d, cPtr = %d",
			Utils.GetFileLineMethod(), errorCode, cPtr));
		XpgEndpoint res = GeneratedJniJava.GetXpgEndpoint(cPtr, true);
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_GET_MY_BINDINGS, errorCode, 0, res);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4GetMyBindings(errorCode, res);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param did
	 * @param passcode
	 */
	public static void onV4QueryDevice(int errorCode, String did,String passcode) {
		Log.i(TAG, String.format("%s, errorCode = %d, did = %s, passcode = %s",
			Utils.GetFileLineMethod(), errorCode, did, passcode));
		String [] mResult={did,passcode};
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_QUERY_DEVICE, errorCode, 0, mResult);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4QueryDevice(errorCode, did, passcode);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param uid
	 * @param token
	 * @param expire_at
	 */
	public static void onV4CreateUserByAnonymity(int errorCode, String uid,String token,String expire_at) {
		Log.i(TAG, String.format("%s, errorCode = %d, uid = %s, token = %s, expire_at = %s",
			Utils.GetFileLineMethod(), errorCode, uid,token,expire_at));
		String [] mResult={uid,token,expire_at};
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_CREATE_USER_BY_ANONYMITY, errorCode, 0, mResult);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4CreateUserByAnonymity(errorCode, uid, token, expire_at);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param uid
	 * @param token
	 * @param expire_at
	 */
	public static void onV4CreateUserByName(int errorCode, String uid,String token,String expire_at) {
		Log.i(TAG, String.format("%s, errorCode = %d, uid = %s, token = %s, expire_at = %s",
			Utils.GetFileLineMethod(), errorCode, uid,token,expire_at));
		String [] mResult={uid,token,expire_at};
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_CREATE_USER_BY_NAME, errorCode, 0, mResult);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4CreateUserByName(errorCode, uid, token, expire_at);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param uid
	 * @param token
	 * @param expire_at
	 */
	public static void onV4CreateUserByPhone(int errorCode, String uid,String token,String expire_at) {
		Log.i(TAG, String.format("%s, errorCode = %d, uid = %s, token = %s, expire_at = %s",
			Utils.GetFileLineMethod(), errorCode, uid,token,expire_at));
		String [] mResult={uid,token,expire_at};
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_CREATE_USER_BY_PHONE, errorCode, 0, mResult);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4CreateUserByPhone(errorCode, uid, token, expire_at);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param uid
	 * @param token
	 * @param expire_at
	 */
	public static void onV4CreateUserByMail(int errorCode, String uid,String token,String expire_at) {
		Log.i(TAG, String.format("%s, errorCode = %d, uid = %s, token = %s, expire_at = %s",
			Utils.GetFileLineMethod(), errorCode, uid,token,expire_at));
		String [] mResult={uid,token,expire_at};
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_CREATE_USER_BY_MAIL, errorCode, 0, mResult);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4CreateUserByMail(errorCode, uid, token, expire_at);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param updatedAt
	 */
	public static void onV4UpdateUserName(int errorCode, String updatedAt) {
		Log.i(TAG, String.format("%s, errorCode = %d, updatedAt = %s",
			Utils.GetFileLineMethod(), errorCode,updatedAt));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_UPDATE_USER_NAME, errorCode, 0, updatedAt);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4UpdateUserName(errorCode, updatedAt);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param updatedAt
	 */
	public static void onV4UpdatePhone(int errorCode, String updatedAt) {
		Log.i(TAG, String.format("%s, errorCode = %d, updatedAt = %s",
			Utils.GetFileLineMethod(), errorCode,updatedAt));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_UPDATE_PHONE , errorCode, 0, updatedAt);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4UpdatePhone(errorCode, updatedAt);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param updatedAt
	 */
	public static void onV4ChangeUserPwd(int errorCode, String updatedAt) {
		Log.i(TAG, String.format("%s, errorCode = %d, updatedAt = %s",
			Utils.GetFileLineMethod(), errorCode,updatedAt));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_CHANGE_USER_PWD , errorCode, 0, updatedAt);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4ChangeUserPwd(errorCode, updatedAt);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param updatedAt
	 */
	public static void onV4ChangeUserPhone(int errorCode, String updatedAt) {
		Log.i(TAG, String.format("%s, errorCode = %d, updatedAt = %s",
			Utils.GetFileLineMethod(), errorCode,updatedAt));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_CHANGE_USER_PHONE , errorCode, 0, updatedAt);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4ChangeUserPhone(errorCode, updatedAt);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param updatedAt
	 */
	public static void onV4ChangeUserMail(int errorCode, String updatedAt) {
		Log.i(TAG, String.format("%s, errorCode = %d, updatedAt = %s",
			Utils.GetFileLineMethod(), errorCode,updatedAt));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_CHANGE_USER_MAIL , errorCode, 0, updatedAt);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4ChangeUserMail(errorCode, updatedAt);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 * @param productKey
	 * @param did
	 * @param mac
	 * @param isOnline
	 * @param passcode
	 * @param host
	 * @param port
	 */
	public static void onV4GetDeviceInfo(int errorCode, long cPtr) {
		Log.i(TAG, String.format("%s, errorCode = %d, cPtr = %d",
			Utils.GetFileLineMethod(), errorCode, cPtr));
		XpgEndpoint res = GeneratedJniJava.GetXpgEndpoint(cPtr, true);
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_GET_DEVICE_INFO , errorCode, 0, res);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4GetDeviceInfo(errorCode,res);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 */
	public static void onV4GetMobileAuthCode(int errorCode) {
		Log.i(TAG, String.format("%s, errorCode = %d",
			Utils.GetFileLineMethod(), errorCode));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_GET_MOBILE_AUTH_CODE , errorCode, 0, null);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4GetMobileAuthCode(errorCode);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 */
	public static void onV4VerifyMobileAuthCode(int errorCode) {
		Log.i(TAG, String.format("%s, errorCode = %d",
			Utils.GetFileLineMethod(), errorCode));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_VERIFY_MOBILE_AUTH_CODE  , errorCode, 0, null);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4VerifyMobileAuthCode(errorCode);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 */
	public static void onV4RecoverPwdByPhone(int errorCode) {
		Log.i(TAG, String.format("%s, errorCode = %d",
			Utils.GetFileLineMethod(), errorCode));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_RECOVER_PWD_BY_PHONE  , errorCode, 0, null);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4RecoverPwdByPhone(errorCode);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 */
	public static void onV4RecoverPwdByMail(int errorCode) {
		Log.i(TAG, String.format("%s, errorCode = %d",
			Utils.GetFileLineMethod(), errorCode));
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_RECOVER_PWD_BY_MAIL  , errorCode, 0, null);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4RecoverPwdByMail(errorCode);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 */
	public static void onV4BindDevce(int errorCode ,String successString,String failString) {
		Log.i(TAG, String.format("%s, errorCode = %d ,successString=%s ,failString=%s",
			Utils.GetFileLineMethod(), errorCode,successString,failString));
		String mResult[] ={successString,failString}; 
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_BIND_DEVICE , errorCode, 0, mResult);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4BindDevce(errorCode,successString,failString);
	}

	/**
	 * @category JNI Callback
	 * @param errorCode
	 */
	public static void onV4UnbindDevice(int errorCode ,String successString,String failString) {
		Log.i(TAG, String.format("%s, errorCode = %d ,successString=%s ,failString=%s",
			Utils.GetFileLineMethod(), errorCode,successString,failString));
		String mResult[] ={successString,failString}; 
        for (GeneratedActivity activity : lstActivities)
        {
            Message msg = activity.handler.obtainMessage(ON_V4_UNBIND_DEVICE , errorCode, 0, mResult);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
			listener.onV4UnbindDevice(errorCode,successString,failString);
	}
}
