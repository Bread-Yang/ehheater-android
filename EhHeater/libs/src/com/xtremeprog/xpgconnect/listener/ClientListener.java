package com.xtremeprog.xpgconnect.listener;

import com.xtremeprog.xpgconnect.generated.*;

/**
 * General listener for client.
 */
public interface ClientListener {

	/**
	 * SDK inited.
	 * 
	 * @param result
	 *            0 = succeed
	 */
	public void onInited(int result);

	/**
	 * Device found.
	 * 
	 * @see generated#SendDiscoveryReq
	 * @see XPGConnectClient#xpgcBroadcast
	 * @see XPGConnectClient#xpgcFindDevice
	 * 
	 * @param endpoint
	 *            {@link XpgEndpoint}
	 */
	public void onDeviceFound(XpgEndpoint endpoint);

	/**
	 * Received a Easy Link response.
	 * 
	 * @see XPGConnectClient#xpgcBroadcast
	 * 
	 * @param endpoint
	 *            {@link XpgEndpoint}
	 */
	public void onEasyLinkResp(XpgEndpoint endpoint);

	/**
	 * connect event.
	 * 
	 * @param connId
	 *            connection id
	 * @param event
	 *            {@link XPG_RESULT}
	 */
	public void onConnectEvent(int connId, int event);

	/**
	 * login to cloud response, with result and mac
	 * 
	 * @param result
	 *            0 = succeed
	 * @param mac
	 *            string, in format like "E0B20037002E"
	 */
	public void onLoginCloudResp(int result, String mac);

	/**
	 * write event.
	 * 
	 * @param result
	 *            number of sent bytes if succeed
	 *            less than 0 if fails, see also error codes like XPG_RESULT enum
	 * @param connId
	 *            connection id
	 */
	public void onWriteEvent(int result, int connId);

	public void onVersionEvent(int key, int value, int connId);

	public void onTcpPacket(byte data[], int connId);
	
	public void onSendPacket(byte data[], int connId);

	public void onHTTPResp(int result, String buffer);
}
