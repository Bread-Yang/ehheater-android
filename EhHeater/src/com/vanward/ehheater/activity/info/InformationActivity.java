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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.DialogUtil;

public class InformationActivity extends Activity implements
		OnPageChangeListener, OnClickListener {

	private ImageView[] imageViews;
	private LinearLayout mViewPoints;
	private ViewPager mViewPager;
	private Button leftbutton, rightbButton;
	TextView title;
	TextView heatxiaolv, taptv, heattv;
	McuVo mcuVo;

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
		boolean isgas = getIntent().getBooleanExtra("isgas", false);
		if (isgas) {
			pageViews.add(inforChartView);
			pageViews.add(inforElChartView);
		} else {
			
			pageViews.add(inforElcChartView);
		}

		//pageViews.add(view3);
		pageViews.add(new InforHistoryView(this));
		heattv = (TextView) view3.findViewById(R.id.heattv);
	

		sumwater = (TextView) inforChartView.findViewById(R.id.sumwater);
		sumgas = (TextView) inforElChartView.findViewById(R.id.sumgas);
		taptv = (TextView) view3.findViewById(R.id.taptv);
		heatxiaolv = (TextView) view3.findViewById(R.id.heatxiaolv);
		
		if (isgas) {
			((View) heatxiaolv.getParent()).setVisibility(View.VISIBLE);
		} else {
			((View) heatxiaolv.getParent()).setVisibility(View.GONE);
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

		// getdata();

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
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

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

		if (position == 1 && tempToken++ == 0) {
			// load
			try {
				((InforElChartView) pageViews.get(1)).selectDefault();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		if (position == 2) {
			setViewData();
		}

	}

	private int tempToken = 0;

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

		System.out
				.println("http://122.10.94.216:8080/EhHeaterWeb/userinfo/getNewData?did="
						+ heaterInfoService.getCurrentSelectedHeater().getDid());

		finalHttp
				.get("http://122.10.94.216:8080/EhHeaterWeb/userinfo/getNewData?did="
						+ heaterInfoService.getCurrentSelectedHeater().getDid(),
						new AjaxCallBack<String>() {

							@Override
							public void onStart() {
								DialogUtil.instance().showDialog();
								super.onStart();
							}
									//请求成功
							@Override
							public void onSuccess(String t) {

								try {
									JSONObject jsonObject = new JSONObject(t);
									t = jsonObject.getJSONObject("result")
											.toString();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Gson gson = new Gson();
								System.out.println("tt:" + t);
								mcuVo = gson.fromJson(t, McuVo.class);
								setViewData();
								DialogUtil.instance().dismissDialog();
								super.onSuccess(t);
							}
							
							@Override
							public void onFailure(Throwable t, int errorNo,
									String strMsg) {
								// TODO Auto-generated method stub
								DialogUtil.instance().dismissDialog();
								super.onFailure(t, errorNo, strMsg);
							}
						});
	}
	
	
	
	
	public void setViewData() {
		// System.out.println(mcuVo.getCumulativeGas());
		// // heatxiaolv.setText(mcuVo.get);
		// taptv.setText(mcuVo.getCumulativeOpenValveTimes() + "");
		// if (mcuVo != null && mcuVo.getCumulatUseTime() != null) {
		// heattv.setText(mcuVo.getCumulatUseTime() + "mins");
		// }
		// if (mcuVo!=null&&mcuVo.getCumulativeVolume()!=null) {
		// sumwater.setText(mcuVo.getCumulativeVolume() + "L");
		// }
		// if (mcuVo!=null&&mcuVo.getCumulativeGas()!=null) {
		// sumgas.setText(mcuVo.getCumulativeGas() + "L");
		// }

		heattv.setText("1000mins");
		sumwater.setText("120000L");
		sumgas.setText("140000L");

	}
}
