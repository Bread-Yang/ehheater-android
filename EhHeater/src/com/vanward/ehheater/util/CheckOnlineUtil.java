package com.vanward.ehheater.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.vanward.ehheater.activity.global.Global;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

/**
 * 每隔一定时间检查一下设备是否已上线
 */
public class CheckOnlineUtil {
	
	private static final String TAG = "CheckOnlineUtil";
	
	public static final String ACTION_DEVICE_ONLINE = "device_online";
	
	private Timer checkOnlineTimer;
	private String curmac;
	private boolean paused = false;
	private List<XpgEndpoint> bindList = new ArrayList<XpgEndpoint>();
	
	
	
	private static CheckOnlineUtil ins = null;
	
	private CheckOnlineUtil() {
	}
	
	public static CheckOnlineUtil ins() {
		if (ins == null) {
			ins = new CheckOnlineUtil();
		}
		
		return ins;
	}
	
	public void reset(String mac) {
		L.e(this, "reset()");
		this.curmac = mac;
		paused = false;
		for (XpgEndpoint item : bindList) {
//			item.delete();
		}
		bindList.clear();
		
		if (checkOnlineTimer != null) {
			checkOnlineTimer.cancel();
		}
	}
	
	public void start(Context context) {
		L.e(this, "start()");
		start(context, curmac);
	}
	
	public void start(final Context context, final String checkMac) {
		L.e(this, "start()");
		if (checkOnlineTimer != null) {
			checkOnlineTimer.cancel();
		}
		
		checkOnlineTimer = new Timer();

		for (XpgEndpoint item : bindList) {
//			item.delete();
		}
		bindList.clear();
		
		checkOnlineTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				
				L.e(this, "checking online...");
				
				if (paused) {
					L.e(this, "checking online returned due to paused");
					return;
				}
				
				if (Global.checkOnlineConnId < 0) {
					// 已处理了上线, 或者该连接已断开
					L.e(this, "checking online returned due to already dealed");
					checkOnlineTimer.cancel();
					return;
				} 
				
				for (XpgEndpoint end : bindList) {
					
					if (end.getSzMac().equals(checkMac) && end.getIsOnline() == 1) {

						L.e(this, "checking online: device online!");
						checkOnlineTimer.cancel();
						
						// ConnectActivity.connectToDevice(act, curmac, userId, userPsw);
						Intent intent = new Intent(ACTION_DEVICE_ONLINE);
						LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
						
						return;
					}
				}
				
				for (XpgEndpoint item : bindList) {
//					item.delete();
				}
				bindList.clear();
				L.e(this, "checking online: continue fetch binding");
				generated.SendBindingGetV2Req(Global.checkOnlineConnId);
				
			}
		}, 100, 5000);
	}

	public void pause() {
		paused = true;
	}
	
	public void resume() {
		paused = false;
	}
	
	public void stop() {

//		if (checkOnlineTimer != null) {
//			checkOnlineTimer.cancel();
//		}
	}
	
	public void receiveEndpoint(XpgEndpoint endpoint) {
		bindList.add(endpoint);
	}
	
}
