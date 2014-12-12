package com.vanward.ehheater.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.vanward.ehheater.activity.global.Global;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;
import com.xtremeprog.xpgconnect.generated.generated;

/**
 * 每隔一定时间检查一下设备是否已上线
 */
public class CheckOnlineUtil {
	
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
		this.curmac = mac;
		paused = false;
		bindList.clear();
		
		if (checkOnlineTimer != null) {
			checkOnlineTimer.cancel();
		}
	}
	
	public void start(final Context context) {
		
		if (checkOnlineTimer != null) {
			checkOnlineTimer.cancel();
		}
		
		checkOnlineTimer = new Timer();

		bindList.clear();
		
		checkOnlineTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				
				Log.d("emmm", "checking online...");
				
				if (paused) {
					Log.d("emmm", "checking online returned due to paused");
					return;
				}
				
				if (Global.checkOnlineConnId < 0) {
					// 已处理了上线, 或者该连接已断开
					Log.d("emmm", "checking online returned due to already dealed");
					checkOnlineTimer.cancel();
					return;
				}
				
				for (XpgEndpoint end : bindList) {
					
					if (end.getSzMac().equals(curmac) && end.getIsOnline() == 1) {

						Log.d("emmm", "checking online: device online!");
						checkOnlineTimer.cancel();
						
						// ConnectActivity.connectToDevice(act, curmac, userId, userPsw);
						Intent intent = new Intent(ACTION_DEVICE_ONLINE);
						LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
						
						return;
					}
				}
				
				bindList.clear();
				Log.d("emmm", "checking online: continue fetch binding");
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
