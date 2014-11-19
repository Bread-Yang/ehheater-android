package com.vanward.ehheater.activity.main.furnace;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;

public class FurnaceIntelligentControlActivity extends EhHeaterBaseActivity {

	private WebView wb_chart;

	private ImageButton ib_switch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_intelligent_control);
		setTopText(R.string.intelligent_control);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.GONE);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		wb_chart = (WebView) findViewById(R.id.wb_chart);
		ib_switch = (ImageButton) findViewById(R.id.ib_switch);
	}

	private void setListener() {
		ib_switch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	private void init() {
		wb_chart.addJavascriptInterface(new HighChartsJavaScriptInterface(),
				"highChartsJavaScriptInterface");
		wb_chart.getSettings().setJavaScriptEnabled(true);
		wb_chart.loadUrl("file:///android_asset/furnace_chart/chart.html");

	}

	class HighChartsJavaScriptInterface {
		@JavascriptInterface
		public void updateYValue(int x, int newY) {
			Log.e("X值", "X的值是 : " + x);
			Log.e("更新了Y值", "Y的最新值是 : " + newY);
		}
	}
}
