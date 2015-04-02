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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.info.ChartVo.Datavo;
import com.vanward.ehheater.activity.info.ChartVo.Xvo;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.L;

public class InfoAccumulatedWaterChartView extends LinearLayout implements
		OnClickListener, OnCheckedChangeListener {

	private final String TAG = "InfoAccumulatedWaterChartView";

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
	private TextView next, sumwater;

	private TextView tv_lqtime;
	long dates, dtime;

	// 上一年，下一年，等等
	private ImageView imageView1;
	private ImageView imageView2;
	private HttpFriend mHttpFriend;

	public InfoAccumulatedWaterChartView(Context context) {
		super(context);
		this.context = context;
		mHttpFriend = HttpFriend.create(context);
		layout = (ViewGroup) inflate(context,
				R.layout.activity_info_accumulated_water, null);
		RadioGroup radioGroup = (RadioGroup) layout
				.findViewById(R.id.radioGroup1);
		RadioButton radiobutton = (RadioButton) radioGroup
				.findViewById(R.id.radio0);
		radioGroup.setOnCheckedChangeListener(this);

		last = (TextView) layout.findViewById(R.id.last);
		next = (TextView) layout.findViewById(R.id.next);
		sumwater = (TextView) layout.findViewById(R.id.sumwater);
		((View) last.getParent()).setOnClickListener(this);
		((View) next.getParent()).setOnClickListener(this);

		tv_lqtime = (TextView) layout.findViewById(R.id.messagetime);

		dates = getTodayTime();
		imageView1 = (ImageView) layout.findViewById(R.id.imageView1);
		imageView1.setOnClickListener(this);
		imageView2 = (ImageView) layout.findViewById(R.id.imageView2);
		imageView2.setOnClickListener(this);

		webView = (WebView) layout.findViewById(R.id.webView1);
		webView.setBackgroundColor(0); // 设置背景色
		webView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
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
		lParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		addView(layout, lParams);
		radiobutton.setChecked(true);
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
			return "";
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
		String adid = new HeaterInfoService(context).getCurrentSelectedHeater()
				.getDid();
		String url = Consts.REQUEST_BASE_URL + "GasInfo/getgasdata?did=" + adid
				+ "&dateTime=" + da + "&resultType=1&expendType=2";
		System.out.println("当前设备的url" + url);

		mHttpFriend.toUrl(url).executeGet(null, new AjaxCallBack<String>() {

			// 请求成功
			SimpleDateFormat format = new SimpleDateFormat("MM/dd");

			@Override
			public void onSuccess(String t) {
				System.out.println("燃气消耗量" + t);
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
					// 赋值name
					namelistjson = jsonArray.toString();
					// 赋值data
					datalistjson = jsonArray2.toString();
					dtime = da;
					// 设置使用的总电数
					SimpleDateFormat sim = new SimpleDateFormat("yyyy年");
					Long l = new Long(da);
					Date da = new Date(l);
					tv_lqtime.setText(sim.format(da));
					sumwater.setText(Math.round(a) + "L");
					// 更换下方按钮
					chart4week();
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

	public void getmessagemonth(final long da2) {
		String adid = new HeaterInfoService(context).getCurrentSelectedHeater()
				.getDid();
		String url = Consts.REQUEST_BASE_URL + "GasInfo/getgasdata?did=" + adid
				+ "&dateTime=" + da2 + "&resultType=2&expendType=2";

		mHttpFriend.toUrl(url).executeGet(null, new AjaxCallBack<String>() {

			// 请求成功
			SimpleDateFormat format = new SimpleDateFormat("MM/dd");
			SimpleDateFormat format2 = new SimpleDateFormat("-dd");

			@Override
			public void onSuccess(String t) {
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
							time4 = String.valueOf(format2.format(calendar
									.getTime()));
						}
						String time3 = time + time4;
						Electricity electricity = new Electricity();
						electricity.setAmount(amount);
						electricity.setTime(time3);
						li.add(electricity);
						JSONObject jsonOBJ = new JSONObject();
						JSONObject jsonOBJ2 = new JSONObject();
						b = Math.round(Float.parseFloat(li.get(i).getAmount()
								.equals("") ? "0" : li.get(i).getAmount()));
						;
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
					// 赋值name
					namelistjson = jsonArray.toString();
					// 赋值data
					datalistjson = jsonArray2.toString();
					
					dtime = da2;
					
					SimpleDateFormat sim = new SimpleDateFormat("yyyy年");
					Long l = new Long(da2);
					Date da = new Date(l);
					tv_lqtime.setText(sim.format(da));
					// 设置使用的总电数
					sumwater.setText(Math.round(a) + "L");
					// 更换下方按钮
					chart4Month();
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

	public void getmessageyear(final long da3) {
		String adid = new HeaterInfoService(context).getCurrentSelectedHeater()
				.getDid();

		String url = Consts.REQUEST_BASE_URL + "GasInfo/getgasdata?did=" + adid
				+ "&dateTime=" + da3 + "&resultType=3&expendType=2";

		mHttpFriend.toUrl(url).executeGet(null, new AjaxCallBack<String>() {

			// 请求成功
			SimpleDateFormat format = new SimpleDateFormat("MM");

			@Override
			public void onSuccess(String t) {
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
						;
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
					sumwater.setText(Math.round(a) + "L");
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

	// 上下
	public void getall() {
		String url = Consts.REQUEST_BASE_URL + "getNewestElData?did="
				+ Global.connectId;

		mHttpFriend.toUrl(url).executeGet(null, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				try {
					JSONObject jsonObject = new JSONObject(t);
					if (jsonObject.get("result").equals(null)) {
						sumwater.setText("0L");
					} else {
						sumwater.setText(jsonObject.get("result") + "L");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}

			// 请求失败
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
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
		switch (v.getId()) {
		case R.id.imageView1:
			switch (last.getText().toString().trim()) {
			case "上一周":
				long dates5 = timechanged();
				getmessageweek(dates5);
				break;

			case "上一月":
				long dates6 = timechanged2();
				getmessagemonth(dates6);
				break;

			case "上一年":
				long dates7 = timechanged3();
				getmessageyear(dates7);
				break;

			default:
				break;
			}
			break;

		case R.id.imageView2:
			switch (next.getText().toString().trim()) {
			case "下一周":
				long dates2 = timechanged4();
				if (timechanged4() != dtime) {
					getmessageweek(dates2);
				}
				break;

			case "下一月":
				long dates3 = timechanged5();
				if (timechanged5() != dtime) {
					getmessagemonth(dates3);
				}
				break;

			case "下一年":
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