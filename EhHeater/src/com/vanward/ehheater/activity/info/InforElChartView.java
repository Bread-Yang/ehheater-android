package com.vanward.ehheater.activity.info;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class InforElChartView extends LinearLayout implements OnClickListener {

	private ViewGroup layout;
	Context context;
	LinearLayout.LayoutParams lParams;
	private SimpleDateFormat simpleDateFormat;

	public InforElChartView(Context context) {
		super(context);
		this.context = context;
		layout = (ViewGroup) inflate(context, R.layout.infor_el_chart, null);
		lParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		addView(layout, lParams);
//		initItemView(new InforVo("设备故障", new Date(2014, 10, 10, 11, 11), 1));
//		initItemView(new InforVo("氧护提示", new Date(2014, 10, 10, 11, 11), 0));
	}

	@Override
	public void onClick(View arg0) {
		
	}

}
