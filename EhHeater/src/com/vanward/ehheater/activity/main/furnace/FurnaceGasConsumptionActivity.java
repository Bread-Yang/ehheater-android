package com.vanward.ehheater.activity.main.furnace;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.gson.Gson;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.TextUtil;

public class FurnaceGasConsumptionActivity extends EhHeaterBaseActivity {

	private static WebView wv_chart;
	private RadioGroup rg_tab;
	private RadioButton rb_realtime_consumption, rb_accumulated_consumption;

	private static final String TAG = "FurnaceGasConsumptionActivity";

	private SimpleDateFormat realTimeTitleDateFormat = new SimpleDateFormat(
			"yyyy/MM/dd");
	private SimpleDateFormat AccumulatedDateFormat = new SimpleDateFormat(
			"yyyy年");
	private SimpleDateFormat realTimeDateFormat = new SimpleDateFormat("HH:mm");

	private ArrayList<String> realTimeXCategories = new ArrayList<String>();
	private ArrayList<Double> realTimeYDatas = new ArrayList<Double>();

	private double[] defaultRealTimeYDatas = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

	/** 不在线或关机中实时耗量图表显示零数据。 */
	private String offline_data = "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]";

	private String online_data = "[1, 3.5, 4, 5, 4.5, 2, 2.5, 3, 3.4, 4.1, 4.2, 4.4, 3]";

	String result;

	private String did = "";

	private String uid = "";

	private boolean isPowerOffOrOffline;

	private Gson gson = new Gson();
	private String dummyAccumulatedData;
	private String accumulatedData;

	private Handler h3;
	private TakeDataThread threadFor5minute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_gas_consumption);
		setTopText(R.string.gas_consumption_title);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButton(View.GONE);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		wv_chart = (WebView) findViewById(R.id.wv_chart);
		rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
		rb_realtime_consumption = (RadioButton) rg_tab
				.findViewById(R.id.rb_realtime_consumption);
		rb_accumulated_consumption = (RadioButton) rg_tab
				.findViewById(R.id.rb_accumulated_consumption);
	}

	private void setListener() {

		rg_tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkResId) {
				if (checkResId == R.id.rb_realtime_consumption) {
					realTimeYDatas.clear();
					realTimeXCategories.clear();
					L.e(FurnaceGasConsumptionActivity.this,
							"rb_realtime_consumption.setOnClickListener()");
					wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");
					getDataForRealtime(3);
				} else if (checkResId == R.id.rb_accumulated_consumption) {
					accumulatedData = null;
					L.e(FurnaceGasConsumptionActivity.this,
							"rb_accumulated_consumption.setOnClickListener()");
					wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_accumulated_gas_consumption.html");
					getDataForAccumulated();
				}
			}
		});

		// rb_realtime_consumption.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// realTimeYDatas.clear();
		// realTimeXCategories.clear();
		// L.e(FurnaceGasConsumptionActivity.this,
		// "rb_realtime_consumption.setOnClickListener()");
		// wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");
		// getDataForRealtime(3);
		// }
		// });
		//
		// rb_accumulated_consumption.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// accumulatedData = null;
		// L.e(FurnaceGasConsumptionActivity.this,
		// "rb_accumulated_consumption.setOnClickListener()");
		// wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_accumulated_gas_consumption.html");
		// getDataForAccumulated();
		// }
		// });

		// findViewById(R.id.btn_click).setOnClickListener(new OnClickListener()
		// {
		//
		// @Override
		// public void onClick(View v) {
		// // wv_chart.loadUrl("javascript:updateRealTimeChart()");
		// getDataForShiShi(2);
		// wv_chart.loadUrl("javascript:getRealtimeConsumptionTitle()");
		// // wv_chart.reload();
		// // ("javascript:javacalljswithargs(" + "'hello world'" + ")")
		// // getJson(testJSON());
		// }
		// });
	}

	private void init() {
		try {
			InputStream inputStream;
			inputStream = getAssets().open(
					"furnace_chart/accumulated_dummy_data.json");
			dummyAccumulatedData = TextUtil.readTextFile(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		did = new HeaterInfoService(this).getCurrentSelectedHeater().getDid();
		uid = AccountService.getUserId(getBaseContext());
		// did = "Twv7ZQwEafRUqgJvC9YEZH";

		isPowerOffOrOffline = getIntent().getBooleanExtra(
				"isPowerOffOrOffline", false);

		wv_chart.setBackgroundColor(0xF3F3F3);
		wv_chart.getSettings().setJavaScriptEnabled(true);
		wv_chart.addJavascriptInterface(new HighChartsJavaScriptInterface(),
				"highChartsJavaScriptInterface");
		// wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");

		mHttpFriend.delaySeconds = 0;

		L.e(this, "init() : wv_chart.loadUrl()");
		wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");

		// 直接显示累计耗量
		rb_accumulated_consumption.setChecked(true);

		// 屏蔽实时燃气耗量曲线图的功能；
		// getDataForRealtime(3);
		//
		// h3 = new Handler() {
		// public void handleMessage(Message msg) {
		// };
		// };
		//
		// threadFor5minute = new TakeDataThread();
		// threadFor5minute.start();

	}

	class HighChartsJavaScriptInterface {

		@JavascriptInterface
		public String getRealtimeConsumptionTitle() {
			return realTimeTitleDateFormat.format(new Date());
		}

		@JavascriptInterface
		public String getAccumulatedConsumptionTitle() {
			L.e(FurnaceGasConsumptionActivity.this,
					"getAccumulatedConsumptionTitle()");
			return AccumulatedDateFormat.format(new Date());

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
		public String realTimeXCategories() {
			L.e(FurnaceGasConsumptionActivity.this, "realTimeXCategories()");
			if (realTimeXCategories.size() == 0) {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());

				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DAY_OF_MONTH);
				int currentHour = c.get(Calendar.HOUR_OF_DAY);
				int currentMinute = c.get(Calendar.MINUTE) / 5 * 5;

				c.set(year, month, day, currentHour, currentMinute, 0);

				long timestamp = c.getTimeInMillis() / 1000 * 1000; // 将毫秒统一设为000

				L.e(this,
						"当前时间是 : "
								+ realTimeDateFormat
										.format(new Date(timestamp)));
				realTimeXCategories.add(realTimeDateFormat.format(new Date(
						timestamp)));

				for (int i = 0; i < 11; i++) {
					timestamp -= 5 * 60 * 1000;
					L.e(this,
							"当前时间是 : "
									+ realTimeDateFormat.format(new Date(
											timestamp)));
					realTimeXCategories.add(realTimeDateFormat.format(new Date(
							timestamp)));
				}
				Collections.reverse(realTimeXCategories);
			}
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < realTimeXCategories.size(); i++) {
				sb.append("'").append(realTimeXCategories.get(i)).append("',");
			}
			sb.append("]");
			L.e(FurnaceGasConsumptionActivity.this,
					"X_Categories : " + sb.toString());
			return sb.toString();
		}

		@JavascriptInterface
		public double realTimeMaxValue() {
			L.e(FurnaceGasConsumptionActivity.this, "realTimeMaxValue()");
			double max = 10;
			if (realTimeYDatas.size() != 0) {
				max = realTimeYDatas.get(0);
				for (int i = 1; i < realTimeYDatas.size(); i++) {
					if (max < realTimeYDatas.get(i)) {
						max = realTimeYDatas.get(i);
					}
				}
				if (max == 0) {
					max = 10;
				}
			}
			return max;
		}

		@JavascriptInterface
		public String realTimeDatas() {
			L.e(FurnaceGasConsumptionActivity.this, "realTimeDatas()");
			if (realTimeYDatas.size() == 0) {
				for (int i = 0; i < defaultRealTimeYDatas.length; i++) {
					realTimeYDatas.add(defaultRealTimeYDatas[i]);
				}
			}
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < realTimeYDatas.size(); i++) {
				sb.append(realTimeYDatas.get(i)).append(",");
			}
			sb.append("]");
			L.e(FurnaceGasConsumptionActivity.this,
					"realTimeDatas : " + sb.toString());
			return sb.toString();
		}

		@JavascriptInterface
		public String getAccumulatedData() {
			L.e(FurnaceGasConsumptionActivity.this, "getAccumulatedData()");

			if (accumulatedData == null || "".equals(accumulatedData)) {
				accumulatedData = dummyAccumulatedData;
			}
			JSONObject joTemp, joResult;
			JSONArray ja = new JSONArray();
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			try {
				ja = new JSONArray(accumulatedData);
				for (int i = 0; i < ja.length(); i++) {
					joTemp = ja.getJSONObject(i);
					sb.append("{data:");
					L.e(FurnaceGasConsumptionActivity.this,
							"joTemp.getString('amount') : "
									+ joTemp.getString("amount"));
					if ("".equals(joTemp.getString("amount"))
							|| "0".equals(joTemp.getString("amount"))) {
						sb.append("\"\"");
					} else {
						BigDecimal dividend = new BigDecimal(10);
						// BigDecimal amount = new BigDecimal(
						// joTemp.getString("amount")).divide(dividend);
						BigDecimal amount = new BigDecimal(
								joTemp.getString("amount"));  // 改成了不除10
						L.e(FurnaceGasConsumptionActivity.this, "amount : "
								+ amount);
						if (amount.compareTo(new BigDecimal(0)) == 0) {
							sb.append("\"\"");
						} else {
							sb.append(amount.toString());
						}
					}
					sb.append("},");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			sb.append("]");

			L.e(FurnaceGasConsumptionActivity.this,
					"sb.toString() : " + sb.toString());

			return sb.toString();
		}
	}

	private JSONObject testJSON() {
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
			e.printStackTrace();
		}

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
			e.printStackTrace();
		}
	}

	private String json2StringLeiji() {
		return null;
	}

	private void getDataForAccumulated() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);

		// String requestURL =
		// "getFurnaceYearGas?did=dVfu4XXcUCbE93Z2mu4PyZ&year=2014";
		String requestURL = "getFurnaceYearGas?did=" + did + "&year=" + year;

		L.e(this, "请求累计耗量的URL是 : " + Consts.REQUEST_BASE_URL + requestURL);

		executeRequest(Consts.REQUEST_BASE_URL + requestURL, null,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String jsonString) {
						super.onSuccess(jsonString);
						L.e(FurnaceGasConsumptionActivity.this,
								"请求getFurnaceYearGas数据是 : " + jsonString);

						try {
							JSONObject json = new JSONObject(jsonString);

							String result = json.getString("result");
							accumulatedData = result; // 存放返回结果
							// rb_accumulated_consumption.setChecked(true);
							wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_accumulated_gas_consumption.html");
							if ("success".equals("result")) {
								finish();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						L.e(FurnaceGasConsumptionActivity.this, "请求失败");
						// rb_accumulated_consumption.setChecked(true);
						// wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_accumulated_gas_consumption.html");
					}

					@Override
					public void onTimeout() {
						super.onTimeout();
						// rb_accumulated_consumption.setChecked(true);
						// wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_accumulated_gas_consumption.html");
					}
				});
	}

	private String getDataForRealtime(final int ir) {
		L.e(this, "getDataForRealtime()");
		// String requestURL =
		// "http://vanward.xtremeprog.com/EhHeaterWeb/getFurnaceHourGas?did=dVfu4XXcUCbE93Z2mu4PyZ";
		String requestURL = "getFurnaceHourGas?did=" + did;

		L.e(this, "请求实时耗量的URL是 : " + Consts.REQUEST_BASE_URL + requestURL);

		executeRequest(Consts.REQUEST_BASE_URL + requestURL, null,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String jsonString) {
						super.onSuccess(jsonString);
						L.e(FurnaceGasConsumptionActivity.this,
								"请求getFurnaceHourGas后返回的数据是 : " + jsonString);

						try {
							JSONObject json = new JSONObject(jsonString);

							realTimeYDatas.clear();
							realTimeXCategories.clear();

							JSONArray result = json.getJSONArray("result");
							for (int i = 0; i < result.length(); i++) {
								JSONObject item = result.getJSONObject(i);
								String time = realTimeDateFormat
										.format(new Date(item.getLong("time")));
								realTimeXCategories.add(time);
								if (!item.getString("amount").equals("")) {
									realTimeYDatas.add(Double.parseDouble(item
											.getString("amount")) / 10);
								} else {
									realTimeYDatas.add(0.0);
								}
							}
							if (ir == 3) {
								handlerForRealtime.sendEmptyMessage(3);
							} else {
								handlerForRealtime.sendEmptyMessage(2);
							}

							if ("success".equals("result")) {
								finish();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						realTimeXCategories.clear();
						realTimeYDatas.clear();
						handlerForRealtime.sendEmptyMessage(3);
					}

					@Override
					public void onTimeout() {
						super.onTimeout();
						// realTimeXCategories.clear();
						realTimeYDatas.clear();
						handlerForRealtime.sendEmptyMessage(3);
					}
				});

		// mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executePost(
		// null, new AjaxCallBack<String>() {
		// @Override
		// public void onSuccess(String jsonString) {
		// super.onSuccess(jsonString);
		// L.e(this, "请求getFurnaceHourGas后返回的数据是 : " + jsonString);
		//
		// try {
		// JSONObject json = new JSONObject(jsonString);
		//
		// realTimeYDatas.clear();
		// realTimeXCategories.clear();
		//
		// JSONArray result = json.getJSONArray("result");
		// for (int i = 0; i < result.length(); i++) {
		// JSONObject item = result.getJSONObject(i);
		// String time = realTimeDateFormat
		// .format(new Date(item.getLong("time")));
		// realTimeXCategories.add(time);
		// if (!item.getString("amount").equals("")) {
		// realTimeYDatas.add(Double.parseDouble(item
		// .getString("amount")) / 10);
		// } else {
		// realTimeYDatas.add(0.0);
		// }
		// }
		// if (ir == 3) {
		// handlerForRealtime.sendEmptyMessage(3);
		// } else {
		// handlerForRealtime.sendEmptyMessage(2);
		// }
		//
		// if ("success".equals("result")) {
		// finish();
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// @Override
		// public void onFailure(Throwable t, int errorNo,
		// String strMsg) {
		// super.onFailure(t, errorNo, strMsg);
		// realTimeXCategories.clear();
		// realTimeYDatas.clear();
		// handlerForRealtime.sendEmptyMessage(3);
		// }
		//
		// @Override
		// public void onTimeout() {
		// super.onTimeout();
		// // realTimeXCategories.clear();
		// realTimeYDatas.clear();
		// handlerForRealtime.sendEmptyMessage(3);
		// }
		// });

		L.e(this, " getDataForShiShi()执行完了");
		return accumulatedData;
	}

	private Handler handlerForRealtime = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == 2) {
				// 啦数据，更新到webview对应的js

				wv_chart.loadUrl("javascript:updateChart()");
			} else if (msg.what == 3) {
				// rb_realtime_consumption.setChecked(true);
				wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_realtime_gas_consumption.html");
			} else if (msg.what == 4) {
				getDataForRealtime(2);
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
			while (!isFinishing()) {
				// 睡眠5分钟
				try {
					sleep(5 * 1000 * 60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				L.e(FurnaceGasConsumptionActivity.this, "5分钟后再请求多次实时数据");
				handlerForRealtime.sendEmptyMessage(4);
			}

		}
	}
}
