package com.vanward.ehheater.util;

import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.util.Log;

public class PingUtil {

	public static boolean ping(Context context) {
		URL sourceUrl;
		try {
			sourceUrl = new URL("http://www.baidu.com");
			HttpURLConnection conn = (HttpURLConnection) sourceUrl
					.openConnection();
			conn.connect();
			String code = String.valueOf(conn.getResponseCode());
			return code.startsWith("2");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
