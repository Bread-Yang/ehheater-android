package com.vanward.ehheater.activity.info;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class InformationActivity extends Activity implements
		OnPageChangeListener, OnClickListener {

	private ImageView[] imageViews;
	private LinearLayout mViewPoints;
	private ViewPager mViewPager;
	private Button leftbutton, rightbButton;
	TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		initView(this);
	}

	ArrayList<View> pageViews = new ArrayList<View>();

	private void initView(Context context) {

		leftbutton = ((Button) findViewById(R.id.ivTitleBtnLeft));
		leftbutton.setOnClickListener(this);
		rightbButton = ((Button) findViewById(R.id.ivTitleBtnRigh));
		rightbButton.setVisibility(View.GONE);
		leftbutton.setBackgroundResource(R.drawable.icon_back);
		title = (TextView) findViewById(R.id.ivTitleName);
		title.setText("信息");
		pageViews.add(new InforChartView(this));
		pageViews.add(new InforElChartView(this));
		pageViews.add(LinearLayout.inflate(this, R.layout.information_3, null));
		pageViews.add(new InforHistoryView(this));

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
}
