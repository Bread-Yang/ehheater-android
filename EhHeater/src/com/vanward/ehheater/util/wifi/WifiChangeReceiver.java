package com.vanward.ehheater.util.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class WifiChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "WifiChangeReceiver";

	public static final String WIFI_CONNECTED = "WIFI_CONNECTED";

	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "onReceive执行了");
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		Log.e(TAG, "isConnected : " + isConnected);

		if (isConnected) { // 如果wifi连上,则自动重连
			intent = new Intent(WIFI_CONNECTED);
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		}
	}
}
