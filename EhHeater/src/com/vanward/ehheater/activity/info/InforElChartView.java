package com.vanward.ehheater.activity.info;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import u.aly.x;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.info.ChartVo.Datavo;
import com.vanward.ehheater.activity.info.ChartVo.Xvo;
import com.vanward.ehheater.activity.info.InforChartView.Initobject;
import com.vanward.ehheater.activity.info.InforChartView.LoadDataTask;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.HttpConnectUtil;

public class InforElChartView extends LinearLayout implements OnClickListener,
		OnCheckedChangeListener {

	private ViewGroup layout;
	Context context;
	LinearLayout.LayoutParams lParams;
	private SimpleDateFormat simpleDateFormat;
	private WebView webView;
	ArrayList<Datavo> datalist = new ArrayList<Datavo>();
	ArrayList<Xvo> namelist = new ArrayList<Xvo>();

	String datalistjson = "";
	String namelistjson = "";

	TextView last, next;

	public InforElChartView(Context context) {
		super(context);
		this.context = context;
		layout = (ViewGroup) inflate(context, R.layout.infor_el_chart, null);
		RadioGroup radioGroup = (RadioGroup) layout
				.findViewById(R.id.radioGroup1);
		radioGroup.setOnCheckedChangeListener(this);
		webView = (WebView) layout.findViewById(R.id.webView1);
		webView.addJavascriptInterface(new Initobject(), "init");
		last = (TextView) layout.findViewById(R.id.last);
		next = (TextView) layout.findViewById(R.id.next);
		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return true;
			}
		});
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_asset/chart.html");
		lParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		addView(layout, lParams);
//		chart4week();
//		webView.reload();
	}

	@Override
	public void onClick(View arg0) {

	}

	class Initobject {
		@JavascriptInterface
		public String getdata() {
			return datalistjson;
		}

		@JavascriptInterface
		public String getx() {
			return namelistjson;
		}
	}

	public void chart4week() {
		last.setText("上一周");
		next.setText("下一周");
//		datalist.clear();
//		namelist.clear();
//		namelist.clear();
//		for (int i = 0; i < 7; i++) {
//			Xvo xvo = new Xvo();
//			xvo.setName("int" + i);
//			namelist.add(xvo);
//		}
//		Gson gson = new Gson();
//		namelistjson = gson.toJson(namelist);
//		System.out.println(namelistjson);
//
//		for (int i = 0; i < 7; i++) {
//			Datavo datavo = new Datavo();
//			datavo.setData(i * 10);
//			datalist.add(datavo);
//
//		}
//		datalistjson = gson.toJson(datalist);
//		System.out.println(datalistjson);
	}

	public void chart4Month() {
		last.setText("上一月");
		next.setText("下一月");
//		datalist.clear();
//		namelist.clear();
//		namelist.clear();
//		for (int i = 0; i < 4; i++) {
//			Xvo xvo = new Xvo();
//			xvo.setName("int" + i);
//			namelist.add(xvo);
//		}
//		Gson gson = new Gson();
//		namelistjson = gson.toJson(namelist);
//		System.out.println(namelistjson);
//
//		for (int i = 0; i < 4; i++) {
//			Datavo datavo = new Datavo();
//			datavo.setData(i * 10);
//			datalist.add(datavo);
//
//		}
//		datalistjson = gson.toJson(datalist);
//		System.out.println(datalistjson);
	}

	public void chart4Year() {
		last.setText("上一年");
		next.setText("下一年");
//		datalist.clear();
//		namelist.clear();
//		namelist.clear();
//		for (int i = 0; i < 12; i++) {
//			Xvo xvo = new Xvo();
//			xvo.setName("int" + i);
//			namelist.add(xvo);
//		}
//		Gson gson = new Gson();
//		namelistjson = gson.toJson(namelist);
//		System.out.println(namelistjson);
//
//		for (int i = 0; i < 12; i++) {
//			Datavo datavo = new Datavo();
//			datavo.setData(i * 10);
//			datalist.add(datavo);
//
//		}
//		datalistjson = gson.toJson(datalist);
//		System.out.println(datalistjson);
	}

//	@Override
//	public void onCheckedChanged(RadioGroup arg0, int arg1) {
//		if (arg1 == R.id.radio0) {
//			chart4week();
//		} else if (arg1 == R.id.radio1) {
//			chart4Month();
//		} else if (arg1 == R.id.radio2) {
//			chart4Year();
//		}
//		webView.reload();
//	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, final int arg1) {

		currentShowingTime = Calendar.getInstance().getTimeInMillis();
		
		if (arg1 == R.id.radio0) {
			currentShowingPeriodType = "1";
		} else if (arg1 == R.id.radio1) {
			currentShowingPeriodType = "2";
		} else if (arg1 == R.id.radio2) {
			currentShowingPeriodType = "3";
		}
		
		
		new LoadDataTask(currentShowingTime, currentShowingPeriodType, "3").execute();
		
		
//		webView.reload();
		
	}
	long currentShowingTime;
	String currentShowingPeriodType = "1";
	
	public void selectDefault() {
		currentShowingTime = Calendar.getInstance().getTimeInMillis();
		new LoadDataTask(currentShowingTime, currentShowingPeriodType, "3").execute();
	}
	
	class LoadDataTask extends AsyncTask<Void, Void, String> {
		
		String did;
		long dateTime2query;
		String resultType;
		String expendType;
		
		public LoadDataTask(long dateTime2query, String resultType, String expendType) {
			this.did = new HeaterInfoService(context).getCurrentSelectedHeater().getDid();
			this.dateTime2query = dateTime2query;
			this.resultType = resultType;
			this.expendType = expendType;
		}
		
		@Override
		protected void onPreExecute() {
			DialogUtil.instance().showLoadingDialog(context, "");
		}

		@Override
		protected String doInBackground(Void... params) {
			return HttpConnectUtil.getGasDatas(did, dateTime2query, resultType, expendType);
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			// use result to form namelist and datalist
			
			Log.d("emmm", "theString: " + result);
			
			try {
				dododo(resultType, result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			if (resultType.equals("1")) {
				chart4week();
			}
			
			if (resultType.equals("2")) {
				chart4Month();
			}
			
			if (resultType.equals("3")) {
				chart4Year();
			}

			webView.reload();
			
			DialogUtil.dismissDialog();
			
		}
		
		
		private void dododo(String resultType, String input) throws JSONException {
			
			JSONArray jr = new JSONArray(input);
			List<Xvo> nameLi = new ArrayList<Xvo>();
			List<Datavo> dataLi = new ArrayList<Datavo>();
			
			for (int i = 0; i<jr.length(); i++) {
				JSONObject jo = jr.getJSONObject(i);
				
				long timeStamp = jo.getLong("time");
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(timeStamp);
				String name = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.CHINA);
				
				if (!resultType.equals("3")) {
					name += cal.get(Calendar.DATE);
				}
				
				Xvo xvo = new Xvo();
				xvo.setName(name);
				nameLi.add(xvo);
				
				Datavo dvo = new Datavo();
				try {
					dvo.setData(Integer.parseInt(jo.getString("amount")));
				} catch (NumberFormatException e) {
					dvo.setData(0);
				}
				dataLi.add(dvo);
				
			}
			
			Gson gson = new Gson();
			namelistjson = gson.toJson(nameLi);
			datalistjson = gson.toJson(dataLi);

			Log.d("emmm", "namelistjson:" + namelistjson);
			Log.d("emmm", "datalistjson:" + datalistjson);
			
		}
		
	}
}
