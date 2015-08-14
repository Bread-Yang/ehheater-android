package com.vanward.ehheater.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatusUtil {

	/**
	 * 能上网
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isConnected(Context ctx) {
		ConnectivityManager connMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
//		L.e(NetworkStatusUtil.class,
//				"可不可以上网 : "
//						+ (activeNetworkInfo != null && activeNetworkInfo
//								.isConnected()));
		 return activeNetworkInfo != null &&
				 activeNetworkInfo.isConnectedOrConnecting(); // 有wifi或者有蜂窝网络
		// NetworkInfo wifi = connMgr
		// .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		// NetworkInfo md = connMgr
		// .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
//		boolean networkStatus = ((wifi != null && wifi.isConnected()) || (md != null && md
//				.isConnected()));
//		L.e(NetworkStatusUtil.class,
//				"可不可以上网 networkStatus : "
//						+ networkStatus);
//		return networkStatus;  // 有wifi或者有蜂窝网络
	}

	/**
	 * wifi已连接
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isConnectedByWifi(Context ctx) {
		ConnectivityManager connMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifi.isConnected();
	}

	/**
	 * 移动数据已连接但wifi未连接
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isConnectedByMobileData(Context ctx) {
		ConnectivityManager connMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo md = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return !wifi.isConnected() && md.isConnected();
	}

}
