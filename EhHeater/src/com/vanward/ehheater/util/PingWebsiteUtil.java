package com.vanward.ehheater.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.util.Log;

public class PingWebsiteUtil {

	private static boolean ping(String addr, int port) {
		boolean exists = false;
		Socket sock = null;

		try {
		    SocketAddress sockaddr = new InetSocketAddress(InetAddress.getByName(addr), port);
		    // Create an unbound socket
		    sock = new Socket();

		    // This method will block no more than timeoutMs.
		    // If the timeout occurs, SocketTimeoutException is thrown.
		    int timeoutMs = 5000;   // 2 seconds
		    sock.connect(sockaddr, timeoutMs);
		    exists = true;
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if (sock != null) {
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return exists;
	}
	
	/**
	 * onSuccess onFail运行于workerThread(非UI)
	 * @param addr
	 * @param onSuccess
	 * @param onFail
	 */
	public static void testWebsiteAvail(final String addr, final int port, 
			final Runnable onSuccess, final Runnable onFail) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean ret = ping(addr, port);
				if (ret) {
					// Log.e("emmm", "ping success");
					if (onSuccess != null) {
						onSuccess.run();
					}
				} else {
					// Log.e("emmm", "ping fail");
					if (onFail != null) {
						onFail.run();
					}
					
				}
			}
		}).start();
	}
	
	/**
	 * 测试大循环服务器是否连通, 
	 * onSuccess onFail运行于workerThread(非UI)
	 * @param onSuccess
	 * @param onFail
	 */
	public static void testGizwitsAvail(Runnable onSuccess, Runnable onFail) {
		testWebsiteAvail(XPGConnShortCuts.SERVER_ADDR, XPGConnShortCuts.SERVER_PORT, onSuccess, onFail);
	}
	
}
