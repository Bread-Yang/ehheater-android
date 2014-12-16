package com.xtremeprog.xpgconnect;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

import android.os.Handler;
import android.os.Message;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	
	static {
		System.loadLibrary("xpgconnectcli");
	}

	public static native int xpgcInit(byte[] clientId);
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
	public static native void xpgcGetVerifyCodes(String appId,String phone);
	public static native void xpgcCreatUser(String appId,String userID,String pwd,String code);
	public static native void xpgcLoginV4(String appId,String userID,String pwd);
	public static native void xpgcRequestPasswordReset(String appId,String userID,String code,String newPwd);
	public static native void xpgcLogin2Wan(String uid,String password,String did,String passcode);

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
		Log.i(TAG, String.format("%s", Utils.GetFileLineMethod()));
		// TODO: check if memory leak of the endpoint
        for (GeneratedActivity activity : lstActivities)
        {
			XpgEndpoint res = GeneratedJniJava.GetXpgEndpoint(cPtr, false);
            Message msg = activity.handler.obtainMessage(ON_DEVICE_FOUND, 0, 0, res);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
		{
			XpgEndpoint res = GeneratedJniJava.GetXpgEndpoint(cPtr, false);
			listener.onDeviceFound(res);
		}
	}

	public static void onEasyLinkResp(long cPtr) {
		Log.i(TAG, String.format("%s", Utils.GetFileLineMethod()));
		// TODO: check if memory leak of the endpoint
        for (GeneratedActivity activity : lstActivities)
        {
			XpgEndpoint res = GeneratedJniJava.GetXpgEndpoint(cPtr, false);
            Message msg = activity.handler.obtainMessage(ON_EASYLINK_RESP, 0, 0, res);
            activity.handler.sendMessage(msg);
        }
		for (ClientListener listener : lstClientListeners)
		{
			XpgEndpoint res = GeneratedJniJava.GetXpgEndpoint(cPtr, false);
			listener.onEasyLinkResp(res);
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
}
