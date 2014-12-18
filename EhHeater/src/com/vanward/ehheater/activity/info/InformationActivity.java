package com.vanward.ehheater.activity.info;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.service.HeaterInfoService;

public class InformationActivity extends EhHeaterBaseActivity implements
		OnPageChangeListener, OnClickListener {

	private ImageView[] imageViews;
	private LinearLayout mViewPoints;
	private ViewPager mViewPager;
	private Button leftbutton, rightbButton;
	TextView title;
	TextView heatxiaolv, taptv, heattv;
	McuVo mcuVo;
	private boolean isGas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		initView(this);
	}

	ArrayList<View> pageViews = new ArrayList<View>();
	private TextView sumwater;
	private TextView sumgas;

	private void initView(Context context) {

		leftbutton = ((Button) findViewById(R.id.ivTitleBtnLeft));
		leftbutton.setOnClickListener(this);
		rightbButton = ((Button) findViewById(R.id.ivTitleBtnRigh));
		rightbButton.setVisibility(View.GONE);
		leftbutton.setBackgroundResource(R.drawable.icon_back);
		title = (TextView) findViewById(R.id.ivTitleName);

		View view3 = LinearLayout.inflate(this, R.layout.information_3, null);
		title.setText("信息");
		InforChartView inforChartView = new InforChartView(this);
		InforElChartView inforElChartView = new InforElChartView(this);
		InfoElcChartView inforElcChartView = new InfoElcChartView(this);
		isGas = getIntent().getBooleanExtra("isgas", false);
		if (isGas) {
			pageViews.add(inforChartView);
			pageViews.add(inforElChartView);
		} else {
			pageViews.add(inforElcChartView);
		}

		pageViews.add(view3);
		pageViews.add(new InforHistoryView(this));
		heattv = (TextView) view3.findViewById(R.id.heattv);

		sumwater = (TextView) inforChartView.findViewById(R.id.sumwater);
		sumgas = (TextView) inforElChartView.findViewById(R.id.sumgas);
		taptv = (TextView) view3.findViewById(R.id.taptv);
		heatxiaolv = (TextView) view3.findViewById(R.id.heatxiaolv);

		LinearLayout llt_gas_heater = (LinearLayout) view3
				.findViewById(R.id.llt_gas_heater);

		if (isGas) {
			llt_gas_heater.setVisibility(View.VISIBLE);
		} else {
			llt_gas_heater.setVisibility(View.GONE);
		}

		// 创建imageviews数组，大小是要显示的图片的数量
		imageViews = new ImageView[pageViews.size()];
		// 实例化小圆点的linearLayout和viewpager
		mViewPoints = (LinearLayout) findViewById(R.id.viewGroup);
		mViewPager = (ViewPager) findViewById(R.id.navigation_page);

		// 添加小圆点的图片
		for (int i = 0; i < pageViews.size(); i++) {
			ImageView imageView = new ImageView(context);
			// 设置小圆点imageview的参数
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					15, 15);
			layoutParams.setMargins(5, 0, 5, 0);
			imageView.setLayoutParams(layoutParams);// 创建一个宽高均为20 的布局
			// 将小圆点layout添加到数组中
			imageViews[i] = imageView;
			// 默认选中的是第一张图片，此时第一个小圆点是选中状态，其他不是
			if (i == 0) {
				imageViews[i].setBackgroundResource(R.drawable.dian1);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.dian2);
			}

			// 将imageviews添加到小圆点视图组
			mViewPoints.addView(imageViews[i]);
		}
		// 设置viewpager的适配器和监听事件
		mViewPager.setAdapter(new NavigationPageAdapter());
		mViewPager.setOnPageChangeListener(InformationActivity.this);
	}

	// 设置要显示的pageradapter类
	private class NavigationPageAdapter extends PagerAdapter {

		// 销毁position位置的界面
		@Override
		public void destroyItem(View v, int position, Object arg2) {
			((ViewPager) v).removeView((View) arg2);
		}

		// 获取当前窗体界面数
		@Override
		public int getCount() {
			return pageViews.size();
		}

		// 初始化position位置的界面
		@Override
		public Object instantiateItem(View v, int position) {
			View contentView = pageViews.get(position);
			/**
			 * 显示页面的相关操作
			 **/

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					android.widget.LinearLayout.LayoutParams.FILL_PARENT,
					android.widget.LinearLayout.LayoutParams.FILL_PARENT);
			((ViewPager) v).addView(contentView, 0, lp);
			return pageViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View v, Object arg1) {
			return v == arg1;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[position].setBackgroundResource(R.drawable.dian1);
			// 不是当前选中的page，其小圆点设置为未选中的状态
			if (position != i) {
				imageViews[i].setBackgroundResource(R.drawable.dian2);
			}
		}

		if (position == 1) {
			if (isGas) {
				try {
					((InforElChartView) pageViews.get(1)).selectDefault();
				} catch (Exception e) {
				}
			} else {
				getdata();
			}
		}
		if (position == 2 && isGas) {
			getdata();
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;

		default:
			break;
		}

	}

	public void getdata() {
		FinalHttp finalHttp = new FinalHttp();
		HeaterInfoService heaterInfoService = new HeaterInfoService(this);

		String url = "";
		if (isGas) {
			url = "GasInfo/getNewestData?did="
					+ heaterInfoService.getCurrentSelectedHeater().getDid();
		} else {
			url = "GasInfo/getNewestElData?did="
					+ heaterInfoService.getCurrentSelectedHeater().getDid();
		}

		Log.e("信息请求的url是 : ", url);
		
		finalHttp.get(Consts.REQUEST_BASE_URL + url,
				new AjaxCallBack<String>() {

					@Override
					public void onStart() {
						super.onStart();
						showRequestDialog();
					}

					// 请求成功
					@Override
					public void onSuccess(String t) {
						super.onSuccess(t);
						Log.e("信息请求返回的json是 : ", t);
						try {
							JSONObject json = new JSONObject(t);
							String responseCode = json.getString("responseCode");
							if ("200".equals(responseCode)) {
								JSONObject result = json.getJSONObject("result");
								if (isGas) {
									int now_efficiency = result.getInt("now_efficiency");
									heatxiaolv.setText(now_efficiency + "%");
									
									int curcumulativeOpenValveTimes = result.getInt("curcumulativeOpenValveTimes");
									taptv.setText(curcumulativeOpenValveTimes + "");
									
									int cumulveUseTime = result.getInt("cumulveUseTime");
									heattv.setText(cumulveUseTime + "mins");
								} else {
									int heating_tube_time = result.getInt("heating_tube_time");
									int machine_not_heating_time = result.getInt("machine_not_heating_time");
									int sum = heating_tube_time + machine_not_heating_time;
									heattv.setText(sum + "mins");
								}
							}
							dismissRequestDialog();
						} catch (JSONException e) {
							e.printStackTrace();
							dismissRequestDialog();
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						dismissRequestDialog();
					}
				});
	}

	public void setViewData() {
		 System.out.println(mcuVo.getCumulativeGas());
		 // heatxiaolv.setText(mcuVo.get);
		 taptv.setText(mcuVo.getCumulativeOpenValveTimes() + "");
		 if (mcuVo != null && mcuVo.getCumulatUseTime() != null) {
		 heattv.setText(mcuVo.getCumulatUseTime() + "mins");
		 }
		 if (mcuVo!=null&&mcuVo.getCumulativeVolume()!=null) {
		 sumwater.setText(mcuVo.getCumulativeVolume() + "L");
		 }
		 if (mcuVo!=null&&mcuVo.getCumulativeGas()!=null) {
		 sumgas.setText(mcuVo.getCumulativeGas() + "L");
		 }

		heattv.setText("0mins");
		sumwater.setText("0L");
		sumgas.setText("0㎥");

	}
}
