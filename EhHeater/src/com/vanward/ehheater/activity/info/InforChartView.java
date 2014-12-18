package com.vanward.ehheater.activity.info;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import u.aly.w;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.info.ChartVo.Datavo;
import com.vanward.ehheater.activity.info.ChartVo.Xvo;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.HttpConnectUtil;

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
	private TextView next, sumwater;

	private TextView lqtime;
	long dates, dtime;

	// 上一年，下一年，等等
	private ImageView imageView1;
	private ImageView imageView2;

	private Dialog loadingDialog;

	public InforChartView(Context context) {
		super(context);
		this.context = context;
		layout = (ViewGroup) inflate(context, R.layout.inforchart, null);
		RadioGroup radioGroup = (RadioGroup) layout
				.findViewById(R.id.radioGroup1);
		RadioButton radiobutton = (RadioButton) radioGroup
				.findViewById(R.id.radio0);
		radioGroup.setOnCheckedChangeListener(this);

		loadingDialog = BaoDialogShowUtil.getInstance(context)
				.createLoadingDialog();

		last = (TextView) layout.findViewById(R.id.last);
		next = (TextView) layout.findViewById(R.id.next);
		sumwater = (TextView) layout.findViewById(R.id.sumwater);
		((View) last.getParent()).setOnClickListener(this);
		((View) next.getParent()).setOnClickListener(this);

		lqtime = (TextView) layout.findViewById(R.id.messagetime);

		dates = System.currentTimeMillis();
		imageView1 = (ImageView) layout.findViewById(R.id.imageView1);
		imageView1.setOnClickListener(this);
		imageView2 = (ImageView) layout.findViewById(R.id.imageView2);
		imageView2.setOnClickListener(this);

		webView = (WebView) layout.findViewById(R.id.webView1);
		webView.addJavascriptInterface(new Initobject(), "init");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_asset/chart.html");
		webView.setClickable(false);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				webView.setVisibility(view.VISIBLE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				webView.setVisibility(view.GONE);
			}

		});
		webView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return true;
			}
		});
		// chart4week();
		// webView.reload();
		lParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		addView(layout, lParams);
		// initItemView(new InforVo("设备故障", new Date(2014, 10, 10, 11, 11), 1));
		// initItemView(new InforVo("氧护提示", new Date(2014, 10, 10, 11, 11), 0));

		// radioGroup.check(R.id.radio0);
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

		// webView.reload();

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
			// this.did = "EohJ73eV37ABqVPm4jZcNT";
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
			return "";
		}

		@Override
		protected void onPostExecute(String result) {

			// use result to form namelist and datalist

			Log.d("emmm", "theString: " + result);

			// try {
			// dododo(resultType, result);
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }
			if (resultType.equals("1")) {
				getmessageweek(dates);
				// namelistjson="[{name:'10.1'},{name:'10.2'},{name:'10.3'},{name:'10.4'},{name:'10.5'},{name:'10.6'},{name:'10.7'}] ";
				// datalistjson="[{data:55},{data:55},{data:65},{data:60},{data:70},{data:55},{data:55},] ";
				// sumwater.setText("300L");
				// chart4week();
			}

			if (resultType.equals("2")) {
				getmessagemonth(dates);
				// namelistjson="[{name:'10.1-10.7'},{name:'10.8-10.14'},{name:'10.15-10.21'},{name:'10.22-10.28'},{name:'10.29-10.30'}] ";
				// datalistjson="[{data:415},{data:440},{data:380},{data:330},{data:110}] ";
				// sumwater.setText("2230L");
				// chart4Month();
			}

			if (resultType.equals("3")) {
				getmessageyear(dates);
				// namelistjson="[{name:'01'},{name:'02'},{name:'03'},{name:'04'},{name:'05'},{name:'06'},{name:'07'},{name:'08'},{name:'09'},{name:'10'},{name:'11'},{name:'12'}]";
				// datalistjson="[{data:2100},{data:2100},{data:2130},{data:2345},{data:2367},{data:2354},{data:2456},{data:2309},{data:2357},{data:2451},{data:0},{data:0}] ";
				// sumwater.setText("24330L");
				// chart4Year();
			}

			// webView.reload();
			//
			// DialogUtil.dismissDialog();

		}

		// private void dododo(String resultType, String input)
		// throws JSONException {
		//
		// JSONObject jsonObject = new JSONObject(input);
		// JSONArray jr = jsonObject.getJSONArray("result");
		// List<Xvo> nameLi = new ArrayList<Xvo>();
		// List<Datavo> dataLi = new ArrayList<Datavo>();
		//
		// for (int i = 0; i < jr.length(); i++) {
		// JSONObject jo = jr.getJSONObject(i);
		//
		// long timeStamp = jo.getLong("time");
		// Calendar cal = Calendar.getInstance();
		// cal.setTimeInMillis(timeStamp);
		// String name = cal.getDisplayName(Calendar.MONTH,
		// Calendar.SHORT, Locale.CHINA);
		//
		// if (!resultType.equals("3")) {
		// name += cal.get(Calendar.DATE);
		// }
		//
		// Xvo xvo = new Xvo();
		// xvo.setName(name);
		// nameLi.add(xvo);
		//
		// Datavo dvo = new Datavo();
		// try {
		// dvo.setData(Integer.parseInt(jo.getString("amount")));
		// } catch (NumberFormatException e) {
		// dvo.setData(0);
		// }
		// dataLi.add(dvo);
		//
		// }
		//
		// Gson gson = new Gson();
		// namelistjson = gson.toJson(nameLi);
		// datalistjson = gson.toJson(dataLi);
		//
		// Log.d("emmm", "namelistjson:" + namelistjson);
		// Log.d("emmm", "datalistjson:" + datalistjson);
		//
		// }

	}

	public void getmessageweek(long da) {
		String adid = new HeaterInfoService(context).getCurrentSelectedHeater()
				.getDid();
		String url = "http://122.10.94.216:80/EhHeaterWeb/GasInfo/getgasdata?did="
				+ adid + "&dateTime=" + da + "&resultType=1&expendType=2";
		System.out.println("当前设备的url" + url);
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(url, new AjaxCallBack<String>() {
			// 等待数据展示
			@Override
			public void onStart() {
				loadingDialog.show();
				super.onStart();
			}

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
						;
						a = a + b + 0f;
						jsonOBJ.put("name", li.get(i).getTime());
						jsonOBJ2.put(
								"data",
								li.get(i).getAmount().equals("") ? 0 : Math
										.round(Float.parseFloat(li.get(i)
												.getAmount())));
						jsonArray.put(jsonOBJ);
						jsonArray2.put(jsonOBJ2);
					}
					// 赋值name
					namelistjson = jsonArray.toString();
					// 赋值data
					datalistjson = jsonArray2.toString();
					// 设置使用的总电数
					SimpleDateFormat sim = new SimpleDateFormat("yyyy年");
					Long l = new Long(System.currentTimeMillis());
					Date da = new Date(l);
					lqtime.setText(sim.format(da));
					sumwater.setText(Math.round(a) + "L");
					// 更换下方按钮
					chart4week();
					// 刷新数据展示
					webView.reload();
					// 销毁等待
					loadingDialog.dismiss();

				} catch (Exception e) {
					// TODO Auto-generated catch blocks
					e.printStackTrace();
				}
				super.onSuccess(t);
			}

			// 请求失败
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub 请求失败
				super.onFailure(t, errorNo, strMsg);
				Toast.makeText(context, "服务器错误", Toast.LENGTH_LONG).show();
				loadingDialog.dismiss();
			}
		});
	}

	public void getmessagemonth(long da2) {
		String adid = new HeaterInfoService(context).getCurrentSelectedHeater()
				.getDid();
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(
				"http://122.10.94.216:80/EhHeaterWeb/GasInfo/getgasdata?did="
						+ adid + "&dateTime=" + da2
						+ "&resultType=2&expendType=2",
				new AjaxCallBack<String>() {
					// 等待数据展示
					@Override
					public void onStart() {
						loadingDialog.show();
						super.onStart();
					}

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
								;
								a = a + b + 0f;
								jsonOBJ.put("name", li.get(i).getTime());
								jsonOBJ2.put(
										"data",
										li.get(i).getAmount().equals("") ? 0
												: Math.round(Float
														.parseFloat(li.get(i)
																.getAmount())));
								jsonArray.put(jsonOBJ);
								jsonArray2.put(jsonOBJ2);
							}
							// 赋值name
							namelistjson = jsonArray.toString();
							// 赋值data
							datalistjson = jsonArray2.toString();
							SimpleDateFormat sim = new SimpleDateFormat("yyyy年");
							Long l = new Long(System.currentTimeMillis());
							Date da = new Date(l);
							lqtime.setText(sim.format(da));
							// 设置使用的总电数
							sumwater.setText(Math.round(a) + "L");
							// 更换下方按钮
							chart4Month();
							// 刷新数据展示
							webView.reload();
							// 销毁等待
							loadingDialog.dismiss();

						} catch (Exception e) {
							// TODO Auto-generated catch blocks
							e.printStackTrace();
						}
						super.onSuccess(t);
					}

					// 请求失败
					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						Toast.makeText(context, "服务器错误", Toast.LENGTH_LONG)
								.show();
						loadingDialog.dismiss();
					}
				});
	}

	public void getmessageyear(long da3) {
		dtime = da3;
		String adid = new HeaterInfoService(context).getCurrentSelectedHeater()
				.getDid();
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(
				"http://122.10.94.216:80/EhHeaterWeb/GasInfo/getgasdata?did="
						+ adid + "&dateTime=" + da3
						+ "&resultType=3&expendType=2",
				new AjaxCallBack<String>() {
					// 等待数据展示
					@Override
					public void onStart() {
						loadingDialog.show();
						super.onStart();
					}

					// 请求成功
					SimpleDateFormat format = new SimpleDateFormat("MM");

					@Override
					public void onSuccess(String t) {
						try {

							JSONObject jsonObject = new JSONObject(t);
							JSONArray array = jsonObject.getJSONArray("result");

							// JSONObject jb=(JSONObject) array.get(0);
							// dtime=Long.valueOf(jb.getString("time"));
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
								b = Math.round(Float.parseFloat(li.get(i)
										.getAmount().equals("") ? "0" : li.get(
										i).getAmount()));
								;
								a = a + b + 0f;
								jsonOBJ.put("name", li.get(i).getTime());
								jsonOBJ2.put(
										"data",
										li.get(i).getAmount().equals("") ? 0
												: Math.round(Float
														.parseFloat(li.get(i)
																.getAmount())));
								jsonArray.put(jsonOBJ);
								jsonArray2.put(jsonOBJ2);
							}
							// 赋值name
							namelistjson = jsonArray.toString();
							// 赋值data
							datalistjson = jsonArray2.toString();
							SimpleDateFormat sim = new SimpleDateFormat("yyyy年");
							Long l = new Long(dtime);
							Date da = new Date(l);
							lqtime.setText(sim.format(da));
							// 设置使用的总电数
							sumwater.setText(Math.round(a) + "L");
							// 更换下方按钮
							chart4Year();
							// 刷新数据展示
							webView.reload();
							// 销毁等待
							loadingDialog.dismiss();

						} catch (Exception e) {
							// TODO Auto-generated catch blocks
							e.printStackTrace();
						}
						super.onSuccess(t);
					}

					// 请求失败
					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub 请求失败
						super.onFailure(t, errorNo, strMsg);
						Toast.makeText(context, "服务器错误", Toast.LENGTH_LONG)
								.show();
						loadingDialog.dismiss();
					}
				});
	}

	// 上下
	public void getall() {
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(
				"http://122.10.94.216/EhHeaterWeb/GasInfo/getNewestElData?did="
						+ Global.connectId + "", new AjaxCallBack<String>() {
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
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub 请求失败
						super.onFailure(t, errorNo, strMsg);
					}
				});
	}

	public long timechanged() {
		Long l = new Long(dtime);
		Date date = new Date(l);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(ca.DATE, -7);
		long times = ca.getTime().getTime();
		return times;
	}

	public long timechanged2() {
		Long l = new Long(dtime);
		Date date = new Date(l);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(ca.MONTH, -1);
		long times = ca.getTime().getTime();
		return times;
	}

	public long timechanged3() {
		Long l = new Long(dtime);
		Date date = new Date(l);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(ca.YEAR, -1);
		long times = ca.getTime().getTime();
		return times;
	}

	public long timechanged4() {
		Long l2 = new Long(System.currentTimeMillis());
		Date t2 = new Date(l2);
		Calendar ca2 = Calendar.getInstance();
		ca2.setTime(t2);

		Long l = new Long(dtime);
		Date date = new Date(l);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		if (ca.get(ca.WEEK_OF_MONTH) != ca2.get(ca2.WEEK_OF_MONTH)) {
			ca.add(ca.DATE, 7);
		}
		long times = ca.getTime().getTime();
		return times;
	}

	public long timechanged5() {
		Long l2 = new Long(System.currentTimeMillis());
		Date t2 = new Date(l2);
		Calendar ca2 = Calendar.getInstance();
		ca2.setTime(t2);

		Long l = new Long(dtime);
		Date date = new Date(l);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		if (ca.get(ca.MONTH) + 1 != ca2.get(ca2.MONTH) + 1) {
			ca.add(ca.MONTH, 1);
		}
		long times = ca.getTime().getTime();
		return times;
	}

	public long timechanged6() {
		Long l2 = new Long(System.currentTimeMillis());
		Date t2 = new Date(l2);
		Calendar ca2 = Calendar.getInstance();
		ca2.setTime(t2);

		Long l = new Long(dtime);
		Date date = new Date(l);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		if (ca.get(ca.YEAR) != ca2.get(ca2.YEAR)) {
			ca.add(ca.YEAR, 1);
		}
		long times = ca.getTime().getTime();
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
				SimpleDateFormat sim = new SimpleDateFormat("yyyy年");
				Long l = new Long(dates7);
				Date da = new Date(l);
				lqtime.setText(sim.format(da));
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
				SimpleDateFormat sim = new SimpleDateFormat("yyyy年");
				Long l = new Long(dates4);
				Date da = new Date(l);
				lqtime.setText(sim.format(da));
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