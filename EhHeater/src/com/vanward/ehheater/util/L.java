package com.vanward.ehheater.util;

import android.util.Log;

public class L {

	// 发布打包时需要设置位false
	private static final boolean DEBUG = false;

	public static void e(Object object, String msg) {
		if (DEBUG) {
			String className = object.getClass().getSimpleName();
			// Log.e(className, msg);

			StackTraceElement[] stackTraceElement = Thread.currentThread()
					.getStackTrace();
			int currentIndex = -1;
			for (int i = 0; i < stackTraceElement.length; i++) {
				if (stackTraceElement[i].getMethodName().compareTo("e") == 0) {
					currentIndex = i + 1;
					break;
				}
			}

			String fullClassName = stackTraceElement[currentIndex]
					.getClassName();

			String methodName = stackTraceElement[currentIndex].getMethodName();
			String lineNumber = String.valueOf(stackTraceElement[currentIndex]
					.getLineNumber());


			// Log.e(className, msg + "  Log at " + fullClassName + "." +
			// methodName
			// + "(" + className + ".java:" + lineNumber + ")");

			Log.e(className, msg + "                                 =====>  "
					+ className + ".java : " + lineNumber);
			// Log.e(className, msg + "(" + className + ".java:" + lineNumber +
			// ")");
		}
	}
}
