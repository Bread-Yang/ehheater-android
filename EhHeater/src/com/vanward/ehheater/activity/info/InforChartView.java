package com.vanward.ehheater.activity.info;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import u.aly.w;
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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.info.ChartVo.Datavo;
import com.vanward.ehheater.activity.info.ChartVo.Xvo;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.HttpConnectUtil;

public class InforChartView extends LinearLayout implements OnClickListener,
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

	public InforChartView(Context context) {
		super(context);
		this.context = context;
		layout = (ViewGroup) inflate(context, R.layout.inforchart, null);
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
		webView.loadUrl("file:///android_asset/chart.html");
		webView.setClickable(false);
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
//
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
//
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
//			return HttpConnectUtil.getGasDatas(did, dateTime2query, resultType,
//					expendType);
			return "";
		}

		@Override
		protected void onPostExecute(String result) {

			// use result to form namelist and datalist

			Log.d("emmm", "theString: " + result);

//			try {
//				dododo(resultType, result);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}

			if (resultType.equals("1")) {
				namelistjson="[{name:'10.1'},{name:'10.2'},{name:'10.3'},{name:'10.4'},{name:'10.5'},{name:'10.6'},{name:'10.7'}] ";
				datalistjson="[{data:55},{data:55},{data:65},{data:60},{data:70},{data:55},{data:55},] ";
				sumwater.setText("300L");
				chart4week();
			}

			if (resultType.equals("2")) {
				namelistjson="[{name:'10.1-10.7'},{name:'10.8-10.14'},{name:'10.15-10.21'},{name:'10.22-10.28'},{name:'10.29-10.30'}] ";
				datalistjson="[{data:415},{data:440},{data:380},{data:330},{data:110}] ";
				sumwater.setText("2230L");
				chart4Month();
			}

			if (resultType.equals("3")) {
				namelistjson="[{name:'01'},{name:'02'},{name:'03'},{name:'04'},{name:'05'},{name:'06'},{name:'07'},{name:'08'},{name:'09'},{name:'10'},{name:'11'},{name:'12'}]";
				datalistjson="[{data:2100},{data:2100},{data:2130},{data:2345},{data:2367},{data:2354},{data:2456},{data:2309},{data:2357},{data:2451},{data:0},{data:0}] ";
				sumwater.setText("24330L");
				chart4Year();
			}

			webView.reload();

			DialogUtil.dismissDialog();

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

}
