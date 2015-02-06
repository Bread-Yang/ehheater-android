package com.vanward.ehheater.activity.main.furnace;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.gson.Gson;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.HttpFriend;

public class FurnaceGasConsumptionActivity extends EhHeaterBaseActivity {

	private static WebView wv_chart;
	private RadioGroup rg_tab;

	private static final String TAG = "FurnaceGasConsumptionActivity";

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	/** 不在线或关机中实时耗量图表显示零数据。 */
	private String offline_data = "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]";

	private String online_data = "[1, 3.5, 4, 5, 4.5, 2, 2.5, 3, 3.4, 4.1, 4.2, 4.4, 3]";

	String result;

	private String did = "";

	private String uid = "";

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

	Handler rgtabHandler;

	private void setListener() {

		rg_tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_realtime_consumption:
					// wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");
					getDataForShiShi(3);

					break;

				case R.id.rb_accumulated_consumption:

					getData();
					rgtabHandler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							// TODO Auto-generated method stub
							super.handleMessage(msg);
							Log.d(TAG, "加载中。。");
							wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_accumulated_gas_consumption.html");
						}
					};
					break;
				}
			}
		});

		findViewById(R.id.btn_click).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// wv_chart.loadUrl("javascript:updateRealTimeChart()");
				getDataForShiShi(2);
				wv_chart.loadUrl("javascript:getRealtimeConsumptionTitle()");
				// wv_chart.reload();
				// ("javascript:javacalljswithargs(" + "'hello world'" + ")")
				// getJson(testJSON());
			}
		});
	}

	private Handler h3;
	private TakeDataThread threadFor5minute;

	private void init() {
		did = new HeaterInfoService(this).getCurrentSelectedHeater().getDid();
		uid = AccountService.getUserId(getBaseContext());

		isPowerOffOrOffline = getIntent().getBooleanExtra(
				"isPowerOffOrOffline", false);

		wv_chart.setBackgroundColor(0xF3F3F3);
		wv_chart.getSettings().setJavaScriptEnabled(true);
		wv_chart.addJavascriptInterface(new HighChartsJavaScriptInterface(),
				"highChartsJavaScriptInterface");
		// wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");
		// wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_update_gas_consumption.html");

		getDataForShiShi(3);

		h3 = new Handler() {
			public void handleMessage(Message msg) {
			};
		};

		threadFor5minute = new TakeDataThread();
		threadFor5minute.start();

	}

	class HighChartsJavaScriptInterface {

		@JavascriptInterface
		public String getRealtimeConsumptionTitle() {
			Log.d(TAG, "getRealtime方法执行了");
			return dateFormat.format(new Date());
		}

		@JavascriptInterface
		public String getAccumulatedConsumptionTitle() {
			Log.d(TAG, "getAccumulatedConsumptionTitle方法执行了");
			return "";
		}

		@JavascriptInterface
		public String getHighChartData() {
			Log.d(TAG, "getHighChartData方法执行了");
			if (isPowerOffOrOffline) {
				return offline_data;
			} else {
				return online_data;
			}

		}

		@JavascriptInterface
		public String getx() {
			Log.e(TAG, "getx方法执行了");
			return "";
		}

		@JavascriptInterface
		public String fromJsResult(String data) {
			Log.e(TAG, "fromJsResult方法参数data的数据是" + data);
			return "";
		}

		@JavascriptInterface
		public String getdata() {

			Log.e(TAG, "getx方法执行完了" + forResult);
			JSONObject joTemp, joResult;
			JSONArray ja = new JSONArray();
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			try {
				ja = new JSONArray(forResult);
				for (int i = 0; i < ja.length(); i++) {
					joTemp = ja.getJSONObject(i);
					sb.append("{data:");
					sb.append(joTemp.getString("amount"));
					sb.append("},");

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			sb.append("]");

			return sb.toString();
		}

		@JavascriptInterface
		public int gety(int i) {
			Log.e(TAG, "gety方法执行了" + i);
			for (int i2 = 0; i2 < 12; i2++) {
				Log.d(TAG, "数组的数据是" + intForShiShi[i2]);
			}
			if (100 == i) {// 取最新的那一个数据
				return intForShiShi[11];
			} else // 根据i取12个数据，i的范围是0~11
			{
				return intForShiShi[i];
			}

		}

	}

	private JSONObject testJSON() {
		Log.e("FurnaceGas", "testJSON方法执行了");
		JSONObject[] jbArray = new JSONObject[12];
		for (int i = 0; i < 12; i++) {
			jbArray[i] = new JSONObject();
		}
		JSONObject json = new JSONObject();
		JSONArray jsonResult = new JSONArray();
		try {
			jbArray[0].put("amount", "1");
			jbArray[0].put("time", "1422943200000");
			jbArray[1].put("amount", "2");
			jbArray[1].put("time", "1422943500000");
			jbArray[2].put("amount", "1.5");
			jbArray[2].put("time", "1422943800000");
			jbArray[3].put("amount", "3.5");
			jbArray[3].put("time", "1422944100000");
			jbArray[4].put("amount", "4");
			jbArray[4].put("time", "1422944400000");
			jbArray[5].put("amount", "1.8");
			jbArray[5].put("time", "1422944700000");
			jbArray[6].put("amount", "2.2");
			jbArray[6].put("time", "1422945300000");
			jbArray[7].put("amount", "2.7");
			jbArray[7].put("time", "1422945300000");
			jbArray[8].put("amount", "2.8");
			jbArray[8].put("time", "1422945600000");
			jbArray[9].put("amount", "3.0");
			jbArray[9].put("time", "1422945900000");
			jbArray[10].put("amount", "3.7");
			jbArray[10].put("time", "1422946200000");
			jbArray[11].put("amount", "4.5");
			jbArray[11].put("time", "1422946500000");

			for (int i = 0; i < 12; i++) {
				jsonResult.put(i, jbArray[i]);
			}

			json.put("responseCode", "200");
			json.put("responseMessage", "成功");
			json.putOpt("result", jsonResult);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.e("FurnaceGas", json.toString());

		return json;
	}

	private void getJson(JSONObject jo) {

		String jo1 = "", jo2 = "";
		JSONArray ja1;
		// jo1 = new JSONObject(); jo2 = new JSONObject();
		ja1 = new JSONArray();

		try {
			jo1 = jo.getString("responseCode");
			jo2 = jo.getString("responseMessage");
			ja1 = jo.getJSONArray("result");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("getJson执行了的数据是", jo1 + jo2 + ja1.toString());

	}

	private String json2StringLeiji() {
		return null;
	}

	private HttpFriend mHttpFriend;
	private ArrayList<int[]> highChar_data;
	private Gson gson = new Gson();
	String forResult;

	private String getData() {
		mHttpFriend = HttpFriend.create(this);
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);

		// String requestURL =
		// "getFurnaceYearGas?did=dVfu4XXcUCbE93Z2mu4PyZ&year=2014";
		String requestURL = "getFurnaceYearGas?did=" + did + "&year=" + year;

		AjaxParams params = new AjaxParams();
		params.put("data", gson.toJson(highChar_data));

		mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executePost(
				params, new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String jsonString) {
						super.onSuccess(jsonString);
						Log.e(TAG, "请求getFurnaceYearGas数据是 : " + jsonString);

						try {
							JSONObject json = new JSONObject(jsonString);

							String result = json.getString("result");
							forResult = result; // 存放返回结果
							rgtabHandler.sendEmptyMessage(1);
							if ("success".equals("result")) {
								finish();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
		Log.e(TAG, " getData()执行完了");
		return forResult;
	}

	int[] intForShiShi = new int[12];

	private String getDataForShiShi(final int ir) {
		mHttpFriend = HttpFriend.create(this);

		// String requestURL =
		// "http://vanward.xtremeprog.com/EhHeaterWeb/getFurnaceHourGas?did=dVfu4XXcUCbE93Z2mu4PyZ";
		String requestURL = "getFurnaceHourGas?did=" + did;

		AjaxParams params = new AjaxParams();
		params.put("data", gson.toJson(highChar_data));

		mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executePost(
				params, new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String jsonString) {
						super.onSuccess(jsonString);
						Log.e(TAG, "请求getFurnaceHourGas后返回的数据是 : " + jsonString);

						try {
							JSONObject json = new JSONObject(jsonString);

							String result = json.getString("result");
							JSONArray resultArray = new JSONArray(result);
							for (int i = 0; i < 12; i++) {
								if (!resultArray.getJSONObject(i)
										.getString("amount").equals("")) {
									intForShiShi[i] = Integer
											.parseInt(resultArray
													.getJSONObject(i)
													.getString("amount"));
								} else {
									intForShiShi[i] = 0;
								}
							}
							if (ir == 3) {
								handlerForShiShi.sendEmptyMessage(3);
							} else {
								handlerForShiShi.sendEmptyMessage(2);
							}

							if ("success".equals("result")) {
								finish();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
		Log.e(TAG, " getDataForShiShi()执行完了");
		return forResult;
	}

	private Handler handlerForShiShi = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == 2) {
				// 啦数据，更新到webview对应的js

				wv_chart.loadUrl("javascript:updateRealTimeChart()");
			} else if (msg.what == 3) {
				wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_update_gas_consumption.html");
			} else if (msg.what == 4) {
				getDataForShiShi(2);
			}
		};

	};

	/*
	 * 当线程启动后，每隔5分钟获取一次数据，然后通知webView 进行reload
	 * 
	 * 但为了考虑业务，所以当ToggleButton切换的时候，线程应该结束
	 * 
	 * 当ToggleButton切换回来的时候，显示之前已经加载好的数据，并获取新的数据，进行整合
	 */
	class TakeDataThread extends Thread {
		@Override
		public void run() {
			Log.d(TAG, "线程开始");
			while (true) {

				// 睡眠5分钟
				try {
					sleep(5 * 1000 * 60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handlerForShiShi.sendEmptyMessage(4);

			}

		}
	}
}
