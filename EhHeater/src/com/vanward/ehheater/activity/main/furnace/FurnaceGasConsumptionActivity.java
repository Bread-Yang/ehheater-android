package com.vanward.ehheater.activity.main.furnace;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class FurnaceGasConsumptionActivity extends EhHeaterBaseActivity {

	private WebView wv_chart;
	private RadioGroup rg_tab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_gas_consumption);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.GONE);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		wv_chart = (WebView) findViewById(R.id.wv_chart);
		rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
	}

	private void setListener() {

		rg_tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_realtime_consumption:
					wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");
					break;

				case R.id.rb_accumulated_consumption:
					wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_accumulated_gas_consumption.html");
					break;

				}
				 wv_chart.reload();
			}
		});
	}

	private void init() {
		wv_chart.setBackgroundColor(0xF3F3F3);
		wv_chart.getSettings().setJavaScriptEnabled(true);
		wv_chart.addJavascriptInterface(new HighChartsJavaScriptInterface(),
				"highChartsJavaScriptInterface");
		wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");
	}

	class HighChartsJavaScriptInterface {

		@JavascriptInterface
		public String getRealtimeConsumptionTitle() {
			return "2014/10/31";
		}
		
		@JavascriptInterface
		public String getdata() {
			return "";
		}

		@JavascriptInterface
		public String getx() {
			return "";
		}
	}
}
