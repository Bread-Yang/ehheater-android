package com.vanward.ehheater.util;

import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.XPG_CONN_TYPE;
import com.xtremeprog.xpgconnect.generated.XPG_WAN_LAN;

public class XPGConnShortCuts {
	
	public static final String SERVER_ADDR = "china.xtremeprog.com";
	
	public static final int SERVER_PORT = 1883;

	public static void connect2small(String ip) {

		XPGConnectClient.xpgcConnect2Async(
				ip, 
				12416, 
				XPG_WAN_LAN.LAN.swigValue(), 
				XPG_CONN_TYPE.TCP.swigValue(), 
				null, null, null, 
				0x00000003);
		
	}
	
	public static void connect2big(String mac, String did, String passcode) {
		
		XPGConnectClient.xpgcConnect2Async(
				SERVER_ADDR, 
				SERVER_PORT,
				XPG_WAN_LAN.MQTT.swigValue(),
				XPG_CONN_TYPE.TCP.swigValue(), 
				mac, 
				did, 
				passcode,
				0x00000003);
		
	}
	
	public static void connect2big() {
		connect2big(null, null, null);
	}
	
}
