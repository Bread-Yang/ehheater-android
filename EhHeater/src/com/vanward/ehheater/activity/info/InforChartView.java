package com.vanward.ehheater.activity.info;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import com.vanward.ehheater.activity.info.InforElChartView.Initobject;

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
	private TextView next;

	public InforChartView(Context context) {
		super(context);
		this.context = context;
		layout = (ViewGroup) inflate(context, R.layout.inforchart, null);
		RadioGroup radioGroup = (RadioGroup) layout
				.findViewById(R.id.radioGroup1);
		radioGroup.setOnCheckedChangeListener(this);
		last = (TextView) layout.findViewById(R.id.last);
		next = (TextView) layout.findViewById(R.id.next);
		webView = (WebView) layout.findViewById(R.id.webView1);
		webView.addJavascriptInterface(new Initobject(), "init");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_asset/chart.html");
		webView.setClickable(false);
		webView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return true;
			}
		});
		chart4week();
		webView.reload();
		lParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		addView(layout, lParams);
		// initItemView(new InforVo("设备故障", new Date(2014, 10, 10, 11, 11), 1));
		// initItemView(new InforVo("氧护提示", new Date(2014, 10, 10, 11, 11), 0));
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
		datalist.clear();
		namelist.clear();
		namelist.clear();
		for (int i = 0; i < 7; i++) {
			Xvo xvo = new Xvo();
			xvo.setName("int" + i);
			namelist.add(xvo);
		}
		Gson gson = new Gson();
		namelistjson = gson.toJson(namelist);
		System.out.println(namelistjson);

		for (int i = 0; i < 7; i++) {
			Datavo datavo = new Datavo();
			datavo.setData(i * 10);
			datalist.add(datavo);

		}
		datalistjson = gson.toJson(datalist);
		System.out.println(datalistjson);
	}

	public void chart4Month() {
		last.setText("上一月");
		next.setText("下一月");
		datalist.clear();
		namelist.clear();
		namelist.clear();
		for (int i = 0; i < 4; i++) {
			Xvo xvo = new Xvo();
			xvo.setName("int" + i);
			namelist.add(xvo);
		}
		Gson gson = new Gson();
		namelistjson = gson.toJson(namelist);
		System.out.println(namelistjson);

		for (int i = 0; i < 4; i++) {
			Datavo datavo = new Datavo();
			datavo.setData(i * 10);
			datalist.add(datavo);

		}
		datalistjson = gson.toJson(datalist);
		System.out.println(datalistjson);
	}

	public void chart4Year() {
		last.setText("上一年");
		next.setText("下一年");
		datalist.clear();
		namelist.clear();
		namelist.clear();
		for (int i = 0; i < 12; i++) {
			Xvo xvo = new Xvo();
			xvo.setName("int" + i);
			namelist.add(xvo);
		}
		Gson gson = new Gson();
		namelistjson = gson.toJson(namelist);
		System.out.println(namelistjson);

		for (int i = 0; i < 12; i++) {
			Datavo datavo = new Datavo();
			datavo.setData(i * 10);
			datalist.add(datavo);

		}
		datalistjson = gson.toJson(datalist);
		System.out.println(datalistjson);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		if (arg1 == R.id.radio0) {
			chart4week();
		} else if (arg1 == R.id.radio1) {
			chart4Month();
		} else if (arg1 == R.id.radio2) {
			chart4Year();
		}
		webView.reload();
	}

}
