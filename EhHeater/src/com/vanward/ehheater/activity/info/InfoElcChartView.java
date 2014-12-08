package com.vanward.ehheater.activity.info;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import u.aly.w;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.info.ChartVo.Datavo;
import com.vanward.ehheater.activity.info.ChartVo.Xvo;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.HttpConnectUtil;

public class InfoElcChartView extends LinearLayout implements OnClickListener,
		OnCheckedChangeListener {

	private ViewGroup layout;
	Context context;
	LinearLayout.LayoutParams lParams;
	private SimpleDateFormat simpleDateFormat;
	WebView webView;
	ArrayList<Datavo> datalist = new ArrayList<Datavo>();
	ArrayList<Xvo> namelist = new ArrayList<Xvo>();

	String datalistjson = "";
	String namelistjson = "";
	
	private TextView last;
	private TextView next,sumwater;
	
	//long dates;
	
	//上一年，下一年，等等
	private ImageView image1;
	private ImageView image2;
	public static Electricity electricity;

	public InfoElcChartView(Context context) {
		super(context);
		this.context = context;
		layout = (ViewGroup) inflate(context, R.layout.infor_elc_chart, null);
		RadioGroup radioGroup = (RadioGroup) layout
				.findViewById(R.id.radioGroup1);
		radioGroup.setOnCheckedChangeListener(this);
		last = (TextView) layout.findViewById(R.id.last);
		next = (TextView) layout.findViewById(R.id.next);
		
		sumwater= (TextView)layout.findViewById(R.id.sumwater);
		((View) last.getParent()).setOnClickListener(this);
		((View) next.getParent()).setOnClickListener(this);
		webView = (WebView) layout.findViewById(R.id.webView1);
		webView.addJavascriptInterface(new Initobject(), "init");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setVisibility(View.GONE);
		webView.loadUrl("file:///android_asset/chart.html");
		
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				webView.setVisibility(view.VISIBLE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				webView.setVisibility(view.GONE);
			}

		});
		webView.setClickable(false);
		webView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return true;
			}
		});
		// chart4week();
		// webView.reload();
		lParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		addView(layout, lParams);
		// initItemView(new InforVo("设备故障", new Date(2014, 10, 10, 11, 11), 1));
		// initItemView(new InforVo("氧护提示", new Date(2014, 10, 10, 11, 11), 0));

		radioGroup.check(R.id.radio0);
	}
	
	
//	public long timechanged(){
//	    Long l=new Long(System.currentTimeMillis());
//        Date date = new Date(l); 
//        Calendar ca=Calendar.getInstance();
//        ca.setTime(date);
//        ca.add(ca.YEAR,-1);
//        Date date2 = new Date();
//        date2=ca.getTime();
//        long times =date2.getTime() ;  
//		return times;
//	}
	@Override
	public void onClick(View arg0) {
//		switch (arg0.getId()) {
//		case R.id.lastparent:
//			if (currentShowingPeriodType.equals("1")) {
//				Calendar cal = Calendar.getInstance();
//				cal.setTimeInMillis(currentShowingTime);
//				cal.add(Calendar.DATE, -7);
//				currentShowingTime = cal.getTimeInMillis();
//			}
//
//			if (currentShowingPeriodType.equals("2")) {
//				Calendar cal = Calendar.getInstance();
//				cal.setTimeInMillis(currentShowingTime);
//				cal.add(Calendar.MONTH, -1);
//				currentShowingTime = cal.getTimeInMillis();
//			}
//
//			if (currentShowingPeriodType.equals("3")) {
//				Calendar cal = Calendar.getInstance();
//				cal.setTimeInMillis(currentShowingTime);
//				cal.add(Calendar.YEAR, -1);
//				currentShowingTime = cal.getTimeInMillis();
//			}
//
//			new LoadDataTask(currentShowingTime, currentShowingPeriodType, "1")
//					.execute();
//
//			break;
//		case R.id.nextparent:
//
//			if (currentShowingPeriodType.equals("1")) {
//				Calendar cal = Calendar.getInstance();
//				cal.setTimeInMillis(currentShowingTime);
//				cal.add(Calendar.DATE, 7);
//				currentShowingTime = cal.getTimeInMillis();
//			}
//
//			if (currentShowingPeriodType.equals("2")) {
//				Calendar cal = Calendar.getInstance();
//				cal.setTimeInMillis(currentShowingTime);
//				cal.add(Calendar.MONTH, 1);
//				currentShowingTime = cal.getTimeInMillis();
//			}
//
//			if (currentShowingPeriodType.equals("3")) {
//				Calendar cal = Calendar.getInstance();
//				cal.setTimeInMillis(currentShowingTime);
//				cal.add(Calendar.YEAR, -1);
//				currentShowingTime = cal.getTimeInMillis();
//			}
//
//			new LoadDataTask(currentShowingTime, currentShowingPeriodType, "1")
//					.execute();
//			break;
//		}
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
	}

	public void chart4Month() {
		last.setText("上一月");
		next.setText("下一月");
	}

	public void chart4Year() {
		last.setText("上一年");
		next.setText("下一年");
	}
	
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

		new LoadDataTask(currentShowingTime, currentShowingPeriodType, "1")
				.execute();

		// webView.reload();

	}

	long currentShowingTime;
	String currentShowingPeriodType = "1";

	class LoadDataTask extends AsyncTask<Void, Void, String> {

		String did;
		long dateTime2query;
		String resultType;
		String expendType;

		public LoadDataTask(long dateTime2query, String resultType,
				String expendType) {
			// this.did = "EohJ73eV37ABqVPm4jZcNT";
			this.did = new HeaterInfoService(context)
					.getCurrentSelectedHeater().getDid();
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
			// return HttpConnectUtil.getGasDatas(did, dateTime2query,
			// resultType,
			// expendType);
			return "";
		}
		@Override
		protected void onPostExecute(String result) {
			
			Log.d("emmm", "theString: " + result);
			
			if (resultType.equals("1")) {
				getmessageweek();
//				namelistjson = "[{name:'10.1'},{name:'10.2'},{name:'10.3'},{name:'10.4'},{name:'10.5'},{name:'10.6'},{name:'10.7'}] ";
//				datalistjson = "[{data:2},{data:2},{data:3},{data:3},{data:5},{data:5},{data:5},] ";
//				chart4week();
//				sumwater.setText("200度");
			}

			if (resultType.equals("2")) {
				getmessagemonth();
//				namelistjson = "[{name:'10.1-10.7'},{name:'10.8-10.14'},{name:'10.15-10.21'},{name:'10.22-10.28'},{name:'10.29-10.30'}] ";
//				datalistjson = "[{data:25},{data:30},{data:24},{data:26},{data:25}] ";
//				chart4Month();
//				sumwater.setText("80度");
			}
			
			if (resultType.equals("3")) {
				getmessageyear();
//				namelistjson = "[{name:'01'},{name:'02'},{name:'03'},{name:'04'},{name:'05'},{name:'06'},{name:'07'},{name:'08'},{name:'09'},{name:'10'},{name:'11'},{name:'12'}]";
//				datalistjson = "[{data:130},{data:140},{data:120},{data:110},{data:100},{data:150},{data:115},{data:125},{data:114},{data:115},{data:0},{data:0}] ";
//				chart4Year();
//				sumwater.setText("960度");
			}
			
//			webView.reload();
//
//			DialogUtil.dismissDialog();

		}

		private void dododo(String resultType, String input)
				throws JSONException {

			JSONObject jsonObject = new JSONObject(input);
			JSONArray jr = jsonObject.getJSONArray("result");
			List<Xvo> nameLi = new ArrayList<Xvo>();
			List<Datavo> dataLi = new ArrayList<Datavo>();

			for (int i = 0; i < jr.length(); i++) {
				JSONObject jo = jr.getJSONObject(i);

				long timeStamp = jo.getLong("time");
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(timeStamp);
				String name = cal.getDisplayName(Calendar.MONTH,
						Calendar.SHORT, Locale.CHINA);

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
	
	public void getmessageweek(){
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get("http://122.10.94.216:80/EhHeaterWeb/GasInfo/getgasdata?did="+Global.connectId+"&dateTime="+System.currentTimeMillis()+"&resultType=1&expendType=3", 
				new AjaxCallBack<String>(){
			//等待数据展示
			@Override
			public void onStart() {
				DialogUtil.instance().showDialog();
				super.onStart();
			}
			
			//请求成功
			 SimpleDateFormat format = new SimpleDateFormat("MM/dd");
					  
			@Override
					public void onSuccess(String t) {
				        try {
							JSONObject jsonObject = new JSONObject(t);
							JSONArray array = jsonObject.getJSONArray("result");
							JSONArray jsonArray = new JSONArray();
							JSONArray jsonArray2 = new JSONArray();
							List<Electricity> li=new ArrayList<Electricity>();
							for(int i=0;i<array.length();i++){
								JSONObject jsonObj = array.getJSONObject(i);
								String amount =jsonObj.getString("amount");
								String time = format.format(new Long(jsonObj.getString("time")));
								//格式化日期
								Electricity electricity=new Electricity();
								electricity.setAmount(amount);
								electricity.setTime(time);
								System.out.println("diandiandiandian2"+electricity.getAmount());
								li.add(electricity);  
								JSONObject jsonOBJ = new JSONObject();
								JSONObject jsonOBJ2 = new JSONObject();
								jsonOBJ.put("name", li.get(i).getTime());
								jsonOBJ2.put("data", li.get(i).getAmount());
								jsonArray.put(jsonOBJ);
								jsonArray2.put(jsonOBJ2);
							}
							//赋值name
							namelistjson = jsonArray.toString();
							//赋值data
							datalistjson = jsonArray2.toString();
							//设置使用的总电数
							getall();
							//更换下方按钮
							chart4week();
							//刷新数据展示
							webView.reload();
							//销毁等待
							DialogUtil.dismissDialog();

						} catch (Exception e) {
							// TODO Auto-generated catch blocks
							e.printStackTrace();
						}
						super.onSuccess(t);
					}
			//请求失败  
			@Override
			public void onFailure(Throwable t, int errorNo,
					String strMsg) {
				// TODO Auto-generated method stub  请求失败
				System.out.println("qingqiu");
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}
	
	public void getmessagemonth(){
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get("http://122.10.94.216:80/EhHeaterWeb/GasInfo/getgasdata?did="+Global.connectId+"&dateTime="+System.currentTimeMillis()+"&resultType=2&expendType=3", 
				new AjaxCallBack<String>(){
			//等待数据展示
			@Override
			public void onStart() {
				DialogUtil.instance().showDialog();
				super.onStart();
			}
			
			//请求成功
			 SimpleDateFormat format = new SimpleDateFormat("MM/dd");
					  
			@Override
					public void onSuccess(String t) {
				        try {
							JSONObject jsonObject = new JSONObject(t);
							JSONArray array = jsonObject.getJSONArray("result");
							JSONArray jsonArray = new JSONArray();
							JSONArray jsonArray2 = new JSONArray();
							List<Electricity> li=new ArrayList<Electricity>();
							for(int i=0;i<array.length();i++){
								JSONObject jsonObj = array.getJSONObject(i);
								String amount =jsonObj.getString("amount");
								String time = format.format(new Long(jsonObj.getString("time")));
								 Date dat=new Date();
								 dat=format.parse(time);
								 Calendar calendar = Calendar.getInstance();
								 calendar.setTime(dat);
							    if(calendar.get(calendar.DATE)==1){
							    	calendar.add(calendar.DATE,5);
							    }
							    else{
							    	calendar.add(calendar.DATE,6);
							    }
							    SimpleDateFormat format2 = new SimpleDateFormat("-dd");
							    String time2=format2.format(calendar.getTime());
							    String times=time+time2;
								//格式化日期
								Electricity electricity=new Electricity();
								electricity.setAmount(amount);
								electricity.setTime(times);
								li.add(electricity);  
								JSONObject jsonOBJ = new JSONObject();
								JSONObject jsonOBJ2 = new JSONObject();
								jsonOBJ.put("name", li.get(i).getTime());
								jsonOBJ2.put("data", li.get(i).getAmount());
								jsonArray.put(jsonOBJ);
								jsonArray2.put(jsonOBJ2);
							}
							//赋值name
							namelistjson = jsonArray.toString();
							//赋值data
							datalistjson = jsonArray2.toString();
							//datalistjson = "[{data:2},{data:2},{data:3},{data:3},{data:5},{data:5}] ";
							//设置使用的总电数
							getall();
							//更换下方按钮
							chart4Month();
							//刷新数据展示
							webView.reload();
							//销毁等待
							DialogUtil.dismissDialog();

						} catch (Exception e) {
							// TODO Auto-generated catch blocks
							e.printStackTrace();
						}
						super.onSuccess(t);
					}
			//请求失败  
			@Override
			public void onFailure(Throwable t, int errorNo,
					String strMsg) {
				// TODO Auto-generated method stub  请求失败
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}
	
	public void getmessageyear(){
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get("http://122.10.94.216:80/EhHeaterWeb/GasInfo/getgasdata?did="+Global.connectId+"&dateTime="+System.currentTimeMillis()+"&resultType=3&expendType=3", 
				new AjaxCallBack<String>(){
			//等待数据展示
			@Override
			public void onStart() {
				DialogUtil.instance().showDialog();
				super.onStart();
			}
			
			//请求成功
			 SimpleDateFormat format = new SimpleDateFormat("MM");
					  
			@Override
					public void onSuccess(String t) {
				        try {
							JSONObject jsonObject = new JSONObject(t);
							System.out.println("yuuyuyu"+t);
							JSONArray array = jsonObject.getJSONArray("result");
							JSONArray jsonArray = new JSONArray();
							JSONArray jsonArray2 = new JSONArray();
							List<Electricity> li=new ArrayList<Electricity>();
							for(int i=0;i<array.length();i++){
								JSONObject jsonObj = array.getJSONObject(i);
								String amount =jsonObj.getString("amount");
								String time = format.format(new Long(jsonObj.getString("time")));
								Electricity electricity=new Electricity();
								electricity.setAmount(amount);
								electricity.setTime(time);
								li.add(electricity);  
								JSONObject jsonOBJ = new JSONObject();
								JSONObject jsonOBJ2 = new JSONObject();
								jsonOBJ.put("name", li.get(i).getTime());
								jsonOBJ2.put("data", li.get(i).getAmount());
								jsonArray.put(jsonOBJ);
								jsonArray2.put(jsonOBJ2);
							}
							//赋值name
							namelistjson = jsonArray.toString();
							//赋值data
							datalistjson = jsonArray2.toString();
							
							//设置使用的总电数
							getall();
							//更换下方按钮
							chart4Year();
							//刷新数据展示
							webView.reload();
							//销毁等待
							DialogUtil.dismissDialog();

						} catch (Exception e) {
							// TODO Auto-generated catch blocks
							e.printStackTrace();
						}
						super.onSuccess(t);
					}
			//请求失败  
			@Override
			public void onFailure(Throwable t, int errorNo,
					String strMsg) {
				// TODO Auto-generated method stub  请求失败
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}
	
	//上下
	public void getall(){
		System.out.println("lail");
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get("http://122.10.94.216/EhHeaterWeb/GasInfo/getNewestElData?did="+Global.connectId+"", new AjaxCallBack<String>(){
			@Override
			public void onSuccess(String t) {
				try {
					JSONObject jsonObject = new JSONObject(t);
					if(jsonObject.get("result").equals(null)){
						sumwater.setText("0度");
					}
					else{
						sumwater.setText(jsonObject.get("result")+"度");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}
			
			//请求失败  
			@Override
			public void onFailure(Throwable t, int errorNo,
					String strMsg) {
				// TODO Auto-generated method stub  请求失败
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}
}
