package com.vanward.ehheater.activity.main.furnace;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.TextUtil;
import com.vanward.ehheater.view.BaoBarView;
import com.vanward.ehheater.view.BaoBarView.BaoBarViewAdapter;
import com.vanward.ehheater.view.BaoBarView.BaoTouchArea;
import com.vanward.ehheater.view.BaoBarView.CGPoint;

public class FurnaceIntelligentControlActivity extends EhHeaterBaseActivity
		implements BaoBarViewAdapter {

	private final String TAG = "FurnaceIntelligentControlActivity";

	private WebView wv_chart;

	private ToggleButton tb_switch;

	private CheckBox cb_Monday, cb_Thuesday, cb_Wednesday, cb_Thursday,
			cb_Friday, cb_Saturday, cb_Sunday;

	private BaoBarView bbv;
	private int[] data = new int[48];

	private CGPoint touchPoint;
	private float tempOffset;
	private BaoTouchArea touchArea;

	private ArrayList<int[]> highChar_data;

	private Gson gson = new Gson();

	private String loop = "";

	private String did = "";

	private String uid = "";

	private String passcode = "";

	private Dialog saveDialog;

	private boolean isAdd = false;

	private int warmId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_intelligent_control);
		setTopText(R.string.intelligent_control);
		setLeftButtonBackground(R.drawable.icon_back);
		// setRightButton(View.GONE);
		setRightButtonBackground(R.drawable.menu_icon_ye);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		bbv = (BaoBarView) findViewById(R.id.bbv);
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
				// saveDialog.show();
				finish();
			}
		});

		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveChartData();
			}
		});

		bbv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					touchPoint = new CGPoint(event.getX(), event.getY());
					tempOffset = bbv.getxOffset();
					BaoTouchArea area = bbv.touchAreaOfPoint(touchPoint);
					touchArea = area;
					break;
				case MotionEvent.ACTION_MOVE:

					CGPoint point = new CGPoint(event.getX(), event.getY());
					if (touchArea == BaoTouchArea.BaoTouch_Scroll) {
						Float offset = (float) Math.abs(point.x - touchPoint.x);
						boolean toLeft = (point.x <= touchPoint.x);
						if (!toLeft) {
							offset = -offset;
						}
						float tempOffset = FurnaceIntelligentControlActivity.this.tempOffset;// self.xOffset;
						tempOffset += offset;
						tempOffset = (tempOffset < 0) ? 0 : tempOffset;
						bbv.setxOffset(tempOffset);
					} else if (touchArea == BaoTouchArea.BaoTouch_SetValue) {
						int index = bbv.indexOfTouchPoint(point);
						int newValue = (int) bbv.valueOfTouchPoint(point);
						if (newValue < 30) {
							newValue = 30;
						}
						if (index < data.length) {
							int value = data[index];
							data[index] = newValue;
							bbv.invalidate();
							L.e(this, "data : " + Arrays.toString(data));
						}
					}
					break;
				case MotionEvent.ACTION_UP:

					break;

				}
				return true;
			}
		});
	}

	JSONObject json2;

	private void saveChartData() {

		String requestURL = null;

		if (isAdd) {
			requestURL = "furnace/saveWarm";
		} else {
			requestURL = "furnace/updateWarm";
		}

		/*
		 * json用的数据
		 * 
		 * cb_Monday, cb_Thuesday, cb_Wednesday, cb_Thursday, cb_Friday,
		 * cb_Saturday, cb_Sunday
		 */
		boolean isWarmOn = tb_switch.isChecked();
		String bbvHeight = new String();
		// 星期循环,第0位是星期日
		loop = (cb_Sunday.isChecked() ? "1" : "0")
				+ (cb_Monday.isChecked() ? "1" : "0")
				+ (cb_Thuesday.isChecked() ? "1" : "0")
				+ (cb_Wednesday.isChecked() ? "1" : "0")
				+ (cb_Thursday.isChecked() ? "1" : "0")
				+ (cb_Friday.isChecked() ? "1" : "0")
				+ (cb_Saturday.isChecked() ? "1" : "0");

		for (int i = 0; i < data.length; i++) {
			if (data[i] < 30) {
				data[i] = 30;
			}
			bbvHeight += data[i];
			if (i < data.length - 1) {
				bbvHeight += ",";
			}
		}
		L.e(this,
				bbvHeight.length() + "bbvHeight里面的数据是： " + bbvHeight.toString());

		final JSONObject json2 = new JSONObject();
		try {
			if (!isAdd) {
				json2.put("warmId", warmId);
			}
			json2.put("dateTime", 1418092441000d);
			// json2.put("dateTime", System.currentTimeMillis());
			json2.put("name", "测试555");
			// json2.put("did", "LWFDwtEcFWJ5hSBPXrVXFS");
			json2.put("did", did);
			// json2.put("uid", "q1231");
			json2.put("uid", uid);
			// json2.put("passcode", "123");
			json2.put("passcode", passcode);
			L.e(this, "passcode : " + passcode);
			// json2.put("loopflag", 1);
			if ("0000000".equals(loop)) {
				json2.put("loopflag", 0);
			} else if ("1111111".equals(loop)) {
				json2.put("loopflag", 1);
			} else {
				json2.put("loopflag", 2);
			}
			json2.put("week", loop);
			json2.put("isWarmOn", isWarmOn ? "1" : "0");
			// json2.put("temp","1,1,2,3,4,6,7,8,60,10,1,2,3,84,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8");
			json2.put("temp", bbvHeight);

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		L.e(FurnaceIntelligentControlActivity.this, json2.toString());

		AjaxParams params = new AjaxParams();

		params.put("data", json2.toString());

		L.e(this, "请求的URL是 : " + Consts.REQUEST_BASE_URL + requestURL);

		executeRequest(Consts.REQUEST_BASE_URL + requestURL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String jsonString) {
				super.onSuccess(jsonString);
				L.e(this, "请求成功后返回的数据是 : " + jsonString);

				try {
					JSONObject json = new JSONObject(jsonString);

					String result = json.getString("responseCode");
					saveToSharedPreferences(json2.toString());
					if ("200".equals(result)) {
						finish();
					} else {
						// 弹出对话框,提示是否重新提交
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo,
					String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				String str = null;
				// showDialog2(2);
			}
		});
		
//		mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executePost(
//				params, new AjaxCallBack<String>() {
//					@Override
//					public void onSuccess(String jsonString) {
//						super.onSuccess(jsonString);
//						L.e(this, "请求成功后返回的数据是 : " + jsonString);
//
//						try {
//							JSONObject json = new JSONObject(jsonString);
//
//							String result = json.getString("responseCode");
//							saveToSharedPreferences(json2.toString());
//							if ("200".equals(result)) {
//								finish();
//							} else {
//								// 弹出对话框,提示是否重新提交
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//
//					@Override
//					public void onFailure(Throwable t, int errorNo,
//							String strMsg) {
//						super.onFailure(t, errorNo, strMsg);
//						String str = null;
//						// showDialog2(2);
//					}
//				});

	}

	String forResult;

	private String getData() {

		// String requestURL =
		// "furnace/getWarm?did=LWFDwtEcFWJ5hSBPXrVXFS&uid=q1231";

		String requestURL = "furnace/getWarm?did=" + did + "&uid=" + uid;

		AjaxParams params = new AjaxParams();
		params.put("data", gson.toJson(highChar_data));

		L.e(this, "智能温控请求: requestURL : " + requestURL);

		executeRequest(Consts.REQUEST_BASE_URL + requestURL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String jsonString) {
				super.onSuccess(jsonString);
				L.e(this, "请求成功后返回的数据是 : " + jsonString);
				L.e(this, "Sharedpreferences返回的数据是 : "
						+ getFromSharedPreferences());

				try {
					JSONObject json = new JSONObject(jsonString);

					String result = json.getString("result");
					forResult = result; // 存放返回结果
					// extractDataFromJson(getFromSharedPreferences());
					// extractDataFromJson(result); // 加载数据放到这里了
					saveToSharedPreferences(result);
					extractDataFromJson(getFromSharedPreferences());
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
				extractDataFromJson(getFromSharedPreferences());
				// showDialog2(1);
			}
		});
		
//		mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executePost(
//				params, new AjaxCallBack<String>() {
//					@Override
//					public void onSuccess(String jsonString) {
//						super.onSuccess(jsonString);
//						L.e(this, "请求成功后返回的数据是 : " + jsonString);
//						L.e(this, "Sharedpreferences返回的数据是 : "
//								+ getFromSharedPreferences());
//
//						try {
//							JSONObject json = new JSONObject(jsonString);
//
//							String result = json.getString("result");
//							forResult = result; // 存放返回结果
//							// extractDataFromJson(getFromSharedPreferences());
//							// extractDataFromJson(result); // 加载数据放到这里了
//							saveToSharedPreferences(result);
//							extractDataFromJson(getFromSharedPreferences());
//							if ("success".equals("result")) {
//								finish();
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//
//					@Override
//					public void onFailure(Throwable t, int errorNo,
//							String strMsg) {
//						super.onFailure(t, errorNo, strMsg);
//						extractDataFromJson(getFromSharedPreferences());
//						// showDialog2(1);
//					}
//				});
		return forResult;
	}

	private void extractDataFromJson(String jsonString) {

		L.e(this, "进入extractDataFromJson方法");
		try {
			JSONArray jsonArray = new JSONArray(jsonString);

			JSONObject json = jsonArray.getJSONObject(0);

			// 右上角开关
			boolean enable = json.getString("isWarmOn").equals("1") ? true
					: false;
			tb_switch.setChecked(enable);

			// String data1 = json.getJSONArray("data").toString();
			// highChar_data = gson.fromJson(data1,
			// new TypeToken<ArrayList<int[]>>() {
			// }.getType());

			warmId = json.getInt("warmId");

			loop = json.getString("week");

			// 获取所有高度值存到数组
			String temp48 = json.getString("temp");
			Log.d(TAG, temp48.length() + "");

			// int[] fordot = new int [47] ;
			// int j = 1; fordot[1] = 0;
			// for (int i = 0; i < temp48.length(); i++) {
			// if(temp48.substring(i, i + 1 ).equals(","))
			// fordot[j++] = i;
			// }
			// for(int i = 0; i < 47; i++){
			//
			// }

			String[] str = temp48.split(",");
			int j = 0;
			for (int i = 0; i < str.length; i++) {

				data[i] = Integer.parseInt(str[i]);

				if (data[i] < 30) {
					data[i] = 30;
				}
			}

			L.e(this, "解析json方法里面的--loop的数据是 : " + loop);

			if (!"null".equals(loop)) {
				for (int i = 0; i < loop.length(); i++) {
					int flag = loop.charAt(i);
					if (flag == '1') {
						switch (i) {

						case 0:
							cb_Sunday.setChecked(true);
							break;
						case 1:
							cb_Monday.setChecked(true);
							break;
						case 2:
							cb_Thuesday.setChecked(true);
							break;
						case 3:
							cb_Wednesday.setChecked(true);
							break;
						case 4:
							cb_Thursday.setChecked(true);
							break;
						case 5:
							cb_Friday.setChecked(true);
							break;
						case 6:
							cb_Saturday.setChecked(true);
							break;
						}
					}
				}
			} else {
				Calendar c = Calendar.getInstance();
				int currentDay = c.get(Calendar.DAY_OF_WEEK); // 1: 星期日, 7 : 星期六

				switch (currentDay) {

				case 1:
					cb_Sunday.setChecked(true);
					break;
				case 2:
					cb_Monday.setChecked(true);
					break;
				case 3:
					cb_Thuesday.setChecked(true);
					break;
				case 4:
					cb_Wednesday.setChecked(true);
					break;
				case 5:
					cb_Thursday.setChecked(true);
					break;
				case 6:
					cb_Friday.setChecked(true);
					break;
				case 7:
					cb_Saturday.setChecked(true);
					break;
				}
			}
			
			bbv.invalidate();
		} catch (Exception e) {
			L.e(this, "没有数据返回");
			isAdd = true;
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
		did = new HeaterInfoService(this).getCurrentSelectedHeater().getDid();
		uid = AccountService.getUserId(getBaseContext());
		passcode = new HeaterInfoService(this).getCurrentSelectedHeater()
				.getPasscode();

		sp = getSharedPreferences("results", 0);
		editor = sp.edit();

		boolean isFloorHeating = getIntent().getBooleanExtra("floor_heating",
				false);
		// 若是在散热器供暖下：温度调节范围30~80℃，温度可在此范围内调节
		// 若是在地暖供暖下 ： 温度调节范围30~55℃，温度可在此范围内调节
		if (isFloorHeating) {
			bbv.setLimitMaxValue(55);
		} else {
			bbv.setLimitMaxValue(80);
		}
		bbv.setAdapter(this);

		saveDialog = BaoDialogShowUtil.getInstance(getApplicationContext())
				.createDialogWithTwoButton(R.string.confirm_save,
						BaoDialogShowUtil.DEFAULT_RESID,
						BaoDialogShowUtil.DEFAULT_RESID, new OnClickListener() {

							@Override
							public void onClick(View v) {
								saveDialog.dismiss();
								finish();
							}
						}, new OnClickListener() {

							@Override
							public void onClick(View v) {
								saveDialog.dismiss();
								saveChartData();
							}
						});

		// requestHttpData();

		// extractDataFromJson(getTestData()); // for test
		getData();

		// wv_chart.addJavascriptInterface(new HighChartsJavaScriptInterface(),
		// "highChartsJavaScriptInterface");
		// wv_chart.getSettings().setJavaScriptEnabled(true);
		// wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_intelligent_control.html");
		// wv_chart.loadUrl("file:///android_asset/furnace_chart/chart_intelligent_control_new.html");
		// wv_chart.setBackgroundColor(0xF3F3F3);
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		// wv_chart.getSettings().setAllowUniversalAccessFromFileURLs(true);
		// wv_chart.getSettings().setAllowFileAccessFromFileURLs(true);
		// }
	}

	private void requestHttpData() {

		String requestURL = "";

		executeRequest(Consts.REQUEST_BASE_URL + requestURL, null, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String jsonString) {
				super.onSuccess(jsonString);
				L.e(this, "请求成功后返回的数据是 : " + jsonString);

				extractDataFromJson(jsonString);

			}
		});
		
//		mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
//				null, new AjaxCallBack<String>() {
//					@Override
//					public void onSuccess(String jsonString) {
//						super.onSuccess(jsonString);
//						L.e(this, "请求成功后返回的数据是 : " + jsonString);
//
//						extractDataFromJson(jsonString);
//
//					}
//				});
	}

	class HighChartsJavaScriptInterface {

		@JavascriptInterface
		public String getHighChartData() {
			return gson.toJson(highChar_data);
		}

		@JavascriptInterface
		public void updateYValue(int x, int newY) {
			// x是从1开始,1到24小时
			highChar_data.set(x - 1, new int[] { x, newY });
		}
	}

	@Override
	public int numberOfBarInBarView(BaoBarView barView) {
		return data.length;
	}

	@Override
	public float valueOfIndex(BaoBarView barView, int index) {
		return data[index];
	}

	@Override
	public String xAxisTitleOfIndex(BaoBarView barView, int index) {
		int hour = index / 2;
		int minute = index % 2 * 30;
		return String.format("%02d", hour) + ":"
				+ String.format("%02d", minute);
	}

	private SharedPreferences sp;
	private Editor editor;

	private String getFromSharedPreferences() {

		return sp.getString("result", "");
	}

	private void saveToSharedPreferences(String strResult) {
		editor.putString("result", strResult);
		editor.commit();
	}

	public void showDialog2(int tv_content) {
		final Dialog dialog = new Dialog(this, R.style.custom_dialog);
		dialog.setContentView(R.layout.dialog_zhineng_error1);
		Button bt_Sure = (Button) dialog.findViewById(R.id.btn_sure);
		bt_Sure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		TextView tv = (TextView) dialog.findViewById(R.id.tv_content);
		if (!(tv_content == 1)) {

			tv.setText("保存数据超时");
		} else {
			tv.setText("请求数据超时");
		}
		dialog.show();
	}

}
