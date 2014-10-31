package com.vanward.ehheater.util;

import java.io.InputStream;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpConnectUtil {

	
	public static String getGasDatas(String did2query, long dateTime2query, String resultType, String expendType) {

		try {
			
			String para = "?did=" + did2query + "&dateTime=" + dateTime2query + "&expendType=" + 
					expendType + "&resultType=" + resultType;

//	        HttpGet httpGet = new HttpGet("http://172.16.1.130:8080/EhHeaterWeb/userinfo/getgasdata2?did=EohJ73eV37ABqVPm4jZcNT&dateTime=1414743947000&expendType=1&resultType=1");
	        HttpGet httpGet = new HttpGet("http://122.10.94.216:8080/EhHeaterWeb/userinfo/getgasdata2" + para);
	        
		    HttpParams paras = new BasicHttpParams();
		    
//			paras.setParameter("did", did2query);
//			paras.setParameter("dateTime", dateTime2query);
//			paras.setParameter("resultType", resultType);
//			paras.setParameter("expendType", expendType);
			

			Log.d("emmm", "queryParas: " + did2query + "-" + dateTime2query + "-" + resultType + "-" + expendType);
			
//			paras.setParameter("did", "EohJ73eV37ABqVPm4jZcNT");
//			paras.setParameter("dateTime", Calendar.getInstance().getTimeInMillis());
//			paras.setParameter("resultType", "1");
//			paras.setParameter("expendType", "1");
		    
		    
			HttpConnectionParams.setConnectionTimeout(paras, 10000);
			HttpConnectionParams.setSoTimeout(paras, 10000);
			
			DefaultHttpClient httpClient = new DefaultHttpClient(paras);
			HttpResponse response = httpClient.execute(httpGet);
			InputStream content = response.getEntity().getContent(); 
			
			String theString = IOUtils.toString(content, "UTF-8");
//			Log.d("emmm", "theString: " + theString);
//			testJson(theString);
			
			return theString;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
	
	private static void testJson(String input) throws JSONException {
		
		JSONArray jr = new JSONArray(input);
		for (int i = 0; i<jr.length(); i++) {
			JSONObject jo = jr.getJSONObject(i);
			Log.d("emmm", "theString: " + jo.getString("amount") + "-" + jo.getString("time"));
		}
		
	}
}
