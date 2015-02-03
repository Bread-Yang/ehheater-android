package com.vanward.ehheater.activity.main.furnace;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class FurnaceGasConsumptionActivity extends EhHeaterBaseActivity {

	private WebView wv_chart;
	private RadioGroup rg_tab;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	/** 不在线或关机中实时耗量图表显示零数据。 */
	private String offline_data = "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]";

	private String online_data = "[1, 3.5, 4, 5, 4.5, 2, 2.5, 3, 3.4, 4.1, 4.2, 4.4, 3]";

	private boolean isPowerOffOrOffline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_gas_consumption);
		setTopText(R.string.gas_consumption);
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
//					wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");
					wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_update_gas_consumption.html");
					break;

				case R.id.rb_accumulated_consumption:
					wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_accumulated_gas_consumption.html");
					break; 
				}
			}
		});
		
		findViewById(R.id.btn_click).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				wv_chart.loadUrl("javascript:updateRealTimeChart()");
			}
		});
	}

	private void init() {
		isPowerOffOrOffline = getIntent().getBooleanExtra(
				"isPowerOffOrOffline", false);

		wv_chart.setBackgroundColor(0xF3F3F3);
		wv_chart.getSettings().setJavaScriptEnabled(true);
		wv_chart.addJavascriptInterface(new HighChartsJavaScriptInterface(),
				"highChartsJavaScriptInterface");
//		wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");
		wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_update_gas_consumption.html");
	}

	class HighChartsJavaScriptInterface {

		@JavascriptInterface
		public String getRealtimeConsumptionTitle() {
			return dateFormat.format(new Date());
		}

		@JavascriptInterface
		public String getAccumulatedConsumptionTitle() {
			return "";
		}

		@JavascriptInterface
		public String getHighChartData() {
			if (isPowerOffOrOffline) {
				return offline_data;
			} else {
				return online_data;
			}

		}

		@JavascriptInterface
		public String getx() {
			return "";
		}
	}
}
