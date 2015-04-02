package com.vanward.ehheater.util;

import android.util.Log;

public class L {

	// 发布打包时需要设置位false
	private static final boolean DEBUG = false;
	
	public static void e(Object object, String msg) {
		if (DEBUG) {
			String className = object.getClass().getSimpleName();
			Log.e(className, msg);
		}
	}
}
