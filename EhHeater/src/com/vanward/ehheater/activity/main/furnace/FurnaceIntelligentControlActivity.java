package com.vanward.ehheater.activity.main.furnace;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.TextUtil;

public class FurnaceIntelligentControlActivity extends EhHeaterBaseActivity {

	private final String TAG = "FurnaceIntelligentControlActivity";

	private WebView wv_chart;

	private ToggleButton tb_switch;

	private HttpFriend mHttpFriend;

	private CheckBox cb_Monday, cb_Thuesday, cb_Wednesday, cb_Thursday,
			cb_Friday, cb_Saturday, cb_Sunday;

	private String highChar_data = "";

	private String loop = "";

	private String did = "";

	private String uid = "";

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
		wv_chart = (WebView) findViewById(R.id.wv_chart);
		tb_switch = (ToggleButton) findViewById(R.id.tb_switch);
		cb_Monday = (CheckBox) findViewById(R.id.cb_Monday);
		cb_Thuesday = (CheckBox) findViewById(R.id.cb_Thuesday);
		cb_Wednesday = (CheckBox) findViewById(R.id.cb_Wednesday);
		cb_Thursday = (CheckBox) findViewById(R.id.cb_Thursday);
		cb_Friday = (CheckBox) findViewById(R.id.cb_Friday);
		cb_Saturday = (CheckBox) findViewById(R.id.cb_Saturday);
		cb_Sunday = (CheckBox) findViewById(R.id.cb_Sunday);
	}

	private void setListener() {

		btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				wv_chart.loadUrl("javascript:getSeries()");

				String requestURL = "";

				HashMap<String, String> map = new HashMap<String, String>();

				map.put("data", highChar_data);

				showRequestDialog();
				mHttpFriend.clearParams()
						.toUrl(Consts.REQUEST_BASE_URL + requestURL)
						.executePost(new AjaxCallBack<String>() {
							@Override
							public void onSuccess(String jsonString) {
								super.onSuccess(jsonString);
								Log.e(TAG, "请求成功后返回的数据是 : " + jsonString);

								try {
									JSONObject json = new JSONObject(jsonString);

									String result = json.getString("result");

									if ("success".equals("result")) {
										finish();
									} else {
										// 弹出对话框,提示是否重新提交
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

								dismissRequestDialog();
							}
						});
			}
		});
	}

	private void extractDataFromJson(String jsonString) {
		Log.e("hello", "hello");
		try {
			JSONObject json = new JSONObject(jsonString);

			boolean enable = json.getBoolean("enable");
			tb_switch.setChecked(enable);

			highChar_data = json.getJSONArray("data").toString();

			Log.e("转换前打印出来的字符串 : ", highChar_data);

			Gson gson = new Gson();

			ArrayList<ArrayList<Integer>> map = gson.fromJson(highChar_data,
					new TypeToken<ArrayList<ArrayList<Integer>>>() {
					}.getType());  

			Log.e("map是 : ", map + "");

			Log.e("转换后打印出来的字符串 : ", gson.toJson(map));

			loop = json.getString("loop");

			for (int i = 0; i < loop.length(); i++) {
				int flag = loop.charAt(i);
				if (flag == '1') {
					switch (i) {

					case 0:
						cb_Monday.setChecked(true);
						break;
					case 1:
						cb_Thuesday.setChecked(true);
						break;
					case 2:
						cb_Wednesday.setChecked(true);
						break;
					case 3:
						cb_Thursday.setChecked(true);
						break;
					case 4:
						cb_Friday.setChecked(true);
						break;
					case 5:
						cb_Saturday.setChecked(true);
						break;
					case 6:
						cb_Sunday.setChecked(true);
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getTestData() {

		InputStream inputStream;
		try {
			inputStream = getAssets().open(
					"furnace_test_data/intelligent_control_data.txt");
			String json = new TextUtil(getApplication())
					.readTextFile(inputStream);
			return json;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void init() {
		// did = new
		// HeaterInfoService(this).getCurrentSelectedHeater().getDid();
		// uid = AccountService.getUserId(getBaseContext());

		mHttpFriend = HttpFriend.create(this);
		// requestHttpData();

		extractDataFromJson(getTestData()); // for test

		wv_chart.addJavascriptInterface(new HighChartsJavaScriptInterface(),
				"highChartsJavaScriptInterface");
		wv_chart.getSettings().setJavaScriptEnabled(true);
		wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_intelligent_control.html");
		wv_chart.setBackgroundColor(0xF3F3F3);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			wv_chart.getSettings().setAllowUniversalAccessFromFileURLs(true);
			wv_chart.getSettings().setAllowFileAccessFromFileURLs(true);
		}
	}

	private void requestHttpData() {

		String requestURL = "";

		showRequestDialog();
		mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String jsonString) {
						super.onSuccess(jsonString);
						Log.e(TAG, "请求成功后返回的数据是 : " + jsonString);

						extractDataFromJson(jsonString);

						dismissRequestDialog();
					}
				});
	}

	class HighChartsJavaScriptInterface {

		@JavascriptInterface
		public void updateHighChar_data() {
			// Log.e(TAG, "打印的data是 : " + data);
			Log.e(TAG, "打印的highChar_data是 : " + highChar_data);
			// highChar_data = data;
		}

		@JavascriptInterface
		public String getHighChartData() {
			return highChar_data;
		}

		@JavascriptInterface
		public void updateYValue(int x, int newY) {
			Log.e("X值", "X的值是 : " + x);
			Log.e("更新了Y值", "Y的最新值是 : " + newY);
		}
	}
}
