package com.vanward.ehheater.util;

import java.net.HttpURLConnection;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.content.Context;

public class PingUtil {

	public static boolean ping(Context context) {
		URL sourceUrl;
		try {
			sourceUrl = new URL("http://www.baidu.com");
	
//			HttpParams httpParameters = new BasicHttpParams();
//			HttpConnectionParams.setConnectionTimeout(httpParameters, 500);
//			HttpConnectionParams.setSoTimeout(httpParameters, 500);
//
//			HttpGet httpget = new HttpGet(sourceUrl.toURI());
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			httpClient.setParams(httpParameters);
//
//			HttpResponse response = httpClient.execute(httpget);
			
			
			
			L.e(PingUtil.class, "conn.getResponseCode() 获取前");
			HttpURLConnection conn = (HttpURLConnection) sourceUrl
					.openConnection();
			
			conn.setRequestMethod("HEAD");
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);  
			conn.connect();
			L.e(PingUtil.class, "conn.getResponseCode() : " + conn.getResponseCode());
			String code = String.valueOf(conn.getResponseCode());
			return code.startsWith("2");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
