package com.vanward.ehheater.util.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.util.L;

public class ConnectChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "ConnectChangeReceiver";

	public static final String CONNECTED = "CONNECTED";

	public void onReceive(Context context, Intent intent) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting(); // 有wifi或者有蜂窝网络

		L.e(this, "isConnected : " + isConnected);
		
		if (!isConnected) {
			Toast.makeText(context, R.string.check_network, Toast.LENGTH_SHORT)
			.show();
		}
		
		intent = new Intent(CONNECTED);
		intent.putExtra("isConnected", isConnected);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}
