package com.vanward.ehheater.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private static String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		// 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			String packageName = intent.getDataString().substring(8);
			Log.e(TAG, "---------------" + packageName);
			Intent newIntent = new Intent();
			newIntent.setClassName(packageName, packageName + ".MainActivity");
			newIntent.setAction("android.intent.action.MAIN");
			newIntent.addCategory("android.intent.category.LAUNCHER");
			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(newIntent);
		}
		// 接收广播：设备上删除了一个应用程序包。
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			Log.e(TAG, "万和云智能被删除了");
		}
	}
}
