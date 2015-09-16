package com.vanward.ehheater.activity.info;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.info.ChartVo.Datavo;
import com.vanward.ehheater.activity.info.ChartVo.Xvo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.L;

public class InfoAccumulatedGasChartView extends LinearLayout implements
		OnClickListener, OnCheckedChangeListener {

	private final String TAG = "InfoOfAccumulatedGasChartView";

	private ViewGroup layout;
	private Context context;
	private LinearLayout.LayoutParams llt_Params;
	private WebView webView;
	ArrayList<Datavo> datalist = new ArrayList<Datavo>();
	ArrayList<Xvo> namelist = new ArrayList<Xvo>();

	String datalistjson = "";
	String namelistjson = "";

	long dates, dtime;

	private ImageView iv_last, iv_next;

	private TextView tv_last, tv_next, tv_sumgas, tv_lqtime;
	private HttpFriend mHttpFriend;

	public InfoAccumulatedGasChartView(Context context) {
		super(context);
		this.context = context;
		mHttpFriend = HttpFriend.create(context);
		layout = (ViewGroup) inflate(context,
				R.layout.activity_info_accumulated_gas, null);
		RadioGroup radioGroup = (RadioGroup) layout
				.findViewById(R.id.radioGroup1);
		radioGroup.setOnCheckedChangeListener(this);

		webView = (WebView) layout.findViewById(R.id.webView1);
		webView.setBackgroundColor(0); // 设置背景色
		webView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
		webView.addJavascriptInterface(new Initobject(), "init");
		tv_last = (TextView) layout.findViewById(R.id.last);
		((View) tv_last.getParent()).setOnClickListener(this);
		tv_next = (TextView) layout.findViewById(R.id.next);
		tv_sumgas = (TextView) layout.findViewById(R.id.sumgas);

		dates = getTodayTime();
		iv_last = (ImageView) layout.findViewById(R.id.imageView1);
		iv_last.setOnClickListener(this);
		iv_next = (ImageView) layout.findViewById(R.id.imageView2);
		iv_next.setOnClickListener(this);
		tv_lqtime = (TextView) layout.findViewById(R.id.messagetime);

		((View) tv_next.getParent()).setOnClickListener(this);

		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return true;
			}
		});

		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_asset/chart.html");
		llt_Params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		addView(layout, llt_Params);
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
		tv_last.setText("上一周");
		tv_next.setText("下一周");
	}

	public void chart4Month() {
		tv_last.setText("上一月");
		tv_next.setText("下一月");
	}

	public void chart4Year() {
		tv_last.setText("上一年");
		tv_next.setText("下一年");
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

		new LoadDataTask(currentShowingTime, currentShowingPeriodType, "3")
				.execute();
	}

	long currentShowingTime;
	String currentShowingPeriodType = "1";

	public void selectDefault() {
		currentShowingTime = Calendar.getInstance().getTimeInMillis();
		new LoadDataTask(currentShowingTime, currentShowingPeriodType, "3")
				.execute();
	}

	class LoadDataTask extends AsyncTask<Void, Void, String> {

		String did;
		long dateTime2query;
		String resultType;
		String expendType;

		public LoadDataTask(long dateTime2query, String resultType,
				String expendType) {
			this.did = new HeaterInfoService(context)
					.getCurrentSelectedHeater().getDid();
			this.dateTime2query = dateTime2query;
			this.resultType = resultType;
			this.expendType = expendType;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(Void... params) {
			// return HttpConnectUtil.getGasDatas(did, dateTime2query,
			// resultType,
			// expendType);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			if (resultType.equals("1")) {
				getmessageweek(dates);
			}

			if (resultType.equals("2")) {
				getmessagemonth(dates);
			}

			if (resultType.equals("3")) {
				getmessageyear(dates);
			}
		}
	}

	public void getmessageweek(final long da) {
		L.e(this, "getmessageweek返回的时间是 : " + da);
		String adid = new HeaterInfoService(context).getCurrentSelectedHeater()
				.getDid();
		String url = Consts.REQUEST_BASE_URL + "GasInfo/getgasdata?did=" + adid
				+ "&dateTime=" + da + "&resultType=1&expendType=1";
		L.e(this, "getmessageweek请求的url是 : " + url);

		mHttpFriend.toUrl(url).executeGet(null, new AjaxCallBack<String>() {

			// 请求成功
			SimpleDateFormat format = new SimpleDateFormat("MM/dd");

			@Override
			public void onSuccess(String t) {
				L.e(this, "getmessageweek返回的json数据是 : " + t);
				try {
					JSONObject jsonObject = new JSONObject(t);
					JSONArray array = jsonObject.getJSONArray("result");

					JSONObject jb = (JSONObject) array.get(3);
					dtime = Long.valueOf(jb.getString("time"));

					JSONArray jsonArray = new JSONArray();
					JSONArray jsonArray2 = new JSONArray();
					List<Electricity> li = new ArrayList<Electricity>();
					float a = 0;
					for (int i = 0; i < array.length(); i++) {
						float b = 0;
						JSONObject jsonObj = array.getJSONObject(i);
						String amount = jsonObj.getString("amount");
						String time = format.format(new Long(jsonObj
								.getString("time")));
						// 格式化日期
						Electricity electricity = new Electricity();
						electricity.setAmount(amount);
						electricity.setTime(time);
						li.add(electricity);
						JSONObject jsonOBJ = new JSONObject();
						JSONObject jsonOBJ2 = new JSONObject();
						b = Math.round(Float.parseFloat(li.get(i).getAmount()
								.equals("") ? "0" : li.get(i).getAmount()));
						L.e(this, "li.get(i).getAmount() : "
								+ li.get(i).getAmount());
						L.e(this, "b : " + b);
						a = a + b + 0f;
						jsonOBJ.put("name", li.get(i).getTime());
						if (li.get(i).getAmount().equals("")) {
							jsonOBJ2.put("data", "");
						} else {
							int round = Math.round(Float.parseFloat(li.get(i)
									.getAmount()));
							if (Float.valueOf(li.get(i).getAmount()) == 0
									|| round == 0) {
								jsonOBJ2.put("data", "");
							} else {
								jsonOBJ2.put("data", round);
							}
						}
						jsonArray.put(jsonOBJ);
						jsonArray2.put(jsonOBJ2);
					}
					namelistjson = jsonArray.toString();
					datalistjson = jsonArray2.toString();
					dtime = da;
					SimpleDateFormat sim = new SimpleDateFormat("yyyy年");
					Long l = new Long(dtime);
					Date da = new Date(l);
					tv_lqtime.setText(sim.format(da));
					tv_sumgas.setText(Math.round(a) + "㎥");
					chart4week();
					webView.reload();
					// 销毁等待
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}
		});
	}

	public void getmessagemonth(final long da2) {
		L.e(this, "getmessagemonth返回的时间是 : " + da2);
		String adid = new HeaterInfoService(context).getCurrentSelectedHeater()
				.getDid();

		mHttpFriend.toUrl(
				Consts.REQUEST_BASE_URL + "GasInfo/getgasdata?did=" + adid
						+ "&dateTime=" + da2 + "&resultType=2&expendType=1")
				.executeGet(null, new AjaxCallBack<String>() {

					// 请求成功
					SimpleDateFormat format = new SimpleDateFormat("MM/dd");
					SimpleDateFormat format2 = new SimpleDateFormat("-dd");

					@Override
					public void onSuccess(String t) {
						L.e(this, "getmessagemonth返回的json数据是 : " + t);
						try {
							JSONObject jsonObject = new JSONObject(t);
							JSONArray array = jsonObject.getJSONArray("result");

							JSONObject jb = (JSONObject) array.get(0);
							dtime = Long.valueOf(jb.getString("time"));

							JSONArray jsonArray = new JSONArray();
							JSONArray jsonArray2 = new JSONArray();
							List<Electricity> li = new ArrayList<Electricity>();
							float a = 0;
							for (int i = 0; i < array.length(); i++) {
								float b = 0;
								JSONObject jsonObj = array.getJSONObject(i);
								String amount = jsonObj.getString("amount");
								String time = format.format(new Long(jsonObj
										.getString("time")));
								// 格式化日期

								Long log = new Long(jsonObj.getString("time"));
								Date time2 = new Date(log);
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(time2);
								calendar.set(calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
								calendar.add(calendar.MONTH, 1);// 月增加1天
								calendar.add(calendar.DAY_OF_MONTH, -1);// 日期倒数一日,既得到本月最后一天

								Calendar ca = Calendar.getInstance();
								ca.setTime(time2);
								ca.set(ca.DAY_OF_WEEK, 7);

								String time4 = String.valueOf(format2.format(ca
										.getTime()));
								if (i == 4) {
									time4 = String.valueOf(format2
											.format(calendar.getTime()));
								}
								String time3 = time + time4;
								Electricity electricity = new Electricity();

								electricity.setAmount(amount);
								electricity.setTime(time3);
								li.add(electricity);
								JSONObject jsonOBJ = new JSONObject();
								JSONObject jsonOBJ2 = new JSONObject();
								b = Math.round(Float.parseFloat(li.get(i)
										.getAmount().equals("") ? "0" : li.get(
										i).getAmount()));
								L.e(this, "li.get(i).getAmount() : "
										+ li.get(i).getAmount());
								L.e(this, "b : " + b);
								a = a + b + 0f;
								jsonOBJ.put("name", li.get(i).getTime());
								if (li.get(i).getAmount().equals("")) {
									jsonOBJ2.put("data", "");
								} else {
									int round = Math.round(Float.parseFloat(li
											.get(i).getAmount()));
									if (Float.valueOf(li.get(i).getAmount()) == 0
											|| round == 0) {
										jsonOBJ2.put("data", "");
									} else {
										jsonOBJ2.put("data", round);
									}
								}
								jsonArray.put(jsonOBJ);
								jsonArray2.put(jsonOBJ2);
							}

							// 赋值name
							namelistjson = jsonArray.toString();
							// 赋值data
							datalistjson = jsonArray2.toString();
							dtime = da2;
							SimpleDateFormat sim = new SimpleDateFormat("yyyy年");
							Long l = new Long(dtime);
							Date da = new Date(l);
							tv_lqtime.setText(sim.format(da));
							// 设置使用的总电数
							tv_sumgas.setText(Math.round(a) + "㎥");
							// 更换下方按钮
							chart4Month();
							// 刷新数据展示
							webView.reload();
							// 销毁等待
						} catch (Exception e) {
							// TODO Auto-generated catch blocks
							e.printStackTrace();
						}
						super.onSuccess(t);
					}
				});
	}

	public void getmessageyear(final long da3) {
		String adid = new HeaterInfoService(context).getCurrentSelectedHeater()
				.getDid();

		String url = Consts.REQUEST_BASE_URL + "GasInfo/getgasdata?did=" + adid
				+ "&dateTime=" + da3 + "&resultType=3&expendType=1";

		mHttpFriend.toUrl(url).executeGet(null, new AjaxCallBack<String>() {

			// 请求成功
			SimpleDateFormat format = new SimpleDateFormat("MM");

			@Override
			public void onSuccess(String t) {
				L.e(this, "getmessageyear返回的json数据是 : " + t);
				try {
					JSONObject jsonObject = new JSONObject(t);
					JSONArray array = jsonObject.getJSONArray("result");

					JSONArray jsonArray = new JSONArray();
					JSONArray jsonArray2 = new JSONArray();
					List<Electricity> li = new ArrayList<Electricity>();
					float a = 0;
					for (int i = 0; i < array.length(); i++) {
						float b = 0;
						JSONObject jsonObj = array.getJSONObject(i);
						String amount = jsonObj.getString("amount");
						String time = format.format(new Long(jsonObj
								.getString("time")));
						Electricity electricity = new Electricity();
						electricity.setAmount(amount);
						electricity.setTime(time);
						li.add(electricity);
						JSONObject jsonOBJ = new JSONObject();
						JSONObject jsonOBJ2 = new JSONObject();
						b = Math.round(Float.parseFloat(li.get(i).getAmount()
								.equals("") ? "0" : li.get(i).getAmount()));
						a = a + b + 0f;
						jsonOBJ.put("name", li.get(i).getTime());
						if (li.get(i).getAmount().equals("")) {
							jsonOBJ2.put("data", "");
						} else {
							int round = Math.round(Float.parseFloat(li.get(i)
									.getAmount()));
							if (Float.valueOf(li.get(i).getAmount()) == 0
									|| round == 0) {
								jsonOBJ2.put("data", "");
							} else {
								jsonOBJ2.put("data", round);
							}
						}
						jsonArray.put(jsonOBJ);
						jsonArray2.put(jsonOBJ2);
						L.e(this, "JSON是 : " + jsonArray2.toString());
					}
					// 赋值name
					namelistjson = jsonArray.toString();
					// 赋值data
					datalistjson = jsonArray2.toString();
					dtime = da3;
					SimpleDateFormat sim = new SimpleDateFormat("yyyy年");
					Long l = new Long(dtime);
					Date da = new Date(l);
					tv_lqtime.setText(sim.format(da));
					// 设置使用的总电数
					tv_sumgas.setText(Math.round(a) + "㎥");
					// 更换下方按钮
					chart4Year();
					// 刷新数据展示
					webView.reload();
					// 销毁等待
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}
		});
	}

	private long getTodayTime() {
		Date date = new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		long times = ca.getTime().getTime() / 1000 * 1000;
		return times;
	}

	public long timechanged() {
		Long l = new Long(dtime);
		Date date = new Date(l);
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(ca.DATE, -7);
		long times = ca.getTime().getTime() / 1000 * 1000;
		return times;
	}

	public long timechanged2() {
		Long l = new Long(dtime);
		Date date = new Date(l);
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(ca.MONTH, -1);
		long times = ca.getTime().getTime() / 1000 * 1000;
		return times;
	}

	public long timechanged3() {
		Long l = new Long(dtime);
		Date date = new Date(l);
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(ca.YEAR, -1);
		long times = ca.getTime().getTime() / 1000 * 1000;
		return times;
	}

	public long timechanged4() {
		Long l2 = new Long(System.currentTimeMillis());
		Date t2 = new Date(l2);
		t2.setHours(0);
		t2.setMinutes(0);
		t2.setSeconds(0);
		Calendar ca2 = Calendar.getInstance();
		ca2.setTime(t2);

		Long l = new Long(dtime);
		Date date = new Date(l);
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		if (ca.get(ca.WEEK_OF_MONTH) != ca2.get(ca2.WEEK_OF_MONTH)) {
			ca.add(ca.DATE, 7);
		}
		long times = ca.getTime().getTime() / 1000 * 1000;
		return times;
	}

	public long timechanged5() {
		Long l2 = new Long(System.currentTimeMillis());
		Date t2 = new Date(l2);
		t2.setHours(0);
		t2.setMinutes(0);
		t2.setSeconds(0);
		Calendar ca2 = Calendar.getInstance();
		ca2.setTime(t2);

		Long l = new Long(dtime);
		Date date = new Date(l);
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		if (ca.get(ca.MONTH) + 1 != ca2.get(ca2.MONTH) + 1) {
			ca.add(ca.MONTH, 1);
		}
		long times = ca.getTime().getTime() / 1000 * 1000;
		return times;
	}

	public long timechanged6() {
		Long l2 = new Long(System.currentTimeMillis());
		Date t2 = new Date(l2);
		t2.setHours(0);
		t2.setMinutes(0);
		t2.setSeconds(0);
		Calendar ca2 = Calendar.getInstance();
		ca2.setTime(t2);

		Long l = new Long(dtime);
		Date date = new Date(l);
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		if (ca.get(ca.YEAR) != ca2.get(ca2.YEAR)) {
			ca.add(ca.YEAR, 1);
		}
		long times = ca.getTime().getTime() / 1000 * 1000;
		return times;
	}

	@Override
	public void onClick(View v) {
		
		int tagLast = 0;
		
		String textLast = tv_last.getText().toString().trim();
		
		if (textLast.equals("上一周")) {
			tagLast = 1;
		}else if (textLast.equals("上一月")) {
			tagLast = 2;
		}else if (textLast.equals("上一年")) {
			tagLast = 3;
		}
		
		int tagNext = 0;
		
		String textNext = tv_next.getText().toString().trim();
		
		if (textNext.equals("下一周")) {
			tagNext = 1;
		}else if (textNext.equals("下一月")) {
			tagNext = 2;
		}else if (textNext.equals("下一年")) {
			tagNext = 3;
		}
		
		switch (v.getId()) {
		case R.id.imageView1:
			switch (tagLast) {
			case 1:
				long dates5 = timechanged();
				getmessageweek(dates5);
				break;

			case 2:
				long dates6 = timechanged2();
				getmessagemonth(dates6);
				break;

			case 3:
				long dates7 = timechanged3();
				getmessageyear(dates7);
				break;

			default:
				break;
			}
			break;

		case R.id.imageView2:
			switch (tagNext) {
			case 1:
				long dates2 = timechanged4();
				if (timechanged4() != dtime) {
					getmessageweek(dates2);
				}
				break;

			case 2:
				long dates3 = timechanged5();
				if (timechanged5() != dtime) {
					getmessagemonth(dates3);
				}
				break;

			case 3:
				long dates4 = timechanged6();
				if (timechanged6() != dtime) {
					getmessageyear(dates4);
				}
				break;

			default:
				break;
			}
			break;
		default:
			break;
		}
	}
}