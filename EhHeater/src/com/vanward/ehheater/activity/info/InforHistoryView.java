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

public class InforHistoryView extends LinearLayout implements OnClickListener {

	private ViewGroup layout;
	Context context;
	LinearLayout.LayoutParams lParams;
	private SimpleDateFormat simpleDateFormat;

	public InforHistoryView(Context context) {
		super(context);
		this.context = context;
		layout = (ViewGroup) inflate(context, R.layout.inforhistory, null);
		lParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		addView(layout, lParams);
		initItemView(new InforVo("设备故障", new Date(2014, 10, 10, 11, 11), 1));
		initItemView(new InforVo("氧护提示", new Date(2014, 10, 10, 11, 11), 0));
	}

	public void initItemView(InforVo inforVo) {
		View itemview = inflate(context, R.layout.inforhistory_item, null);

		ImageView imageView = (ImageView) itemview.findViewById(R.id.icon);
		TextView name = (TextView) itemview.findViewById(R.id.inforname);
		TextView time = (TextView) itemview.findViewById(R.id.time);
		if (inforVo.getStyle() == 1) {
			imageView.setImageResource(R.drawable.information_warning);

		} else {
			imageView.setImageResource(R.drawable.information_tip);
		}
		itemview.setOnClickListener(this);
		name.setText(inforVo.getName());
		simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
		time.setText("2014/11/11 11:11");
		layout.addView(itemview, lParams);
		itemview.setTag(inforVo);

	}

	public class InforVo implements Serializable {
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public int getStyle() {
			return style;
		}

		public void setStyle(int style) {
			this.style = style;
		}

		public InforVo(String name, Date date, int style) {
			super();
			this.name = name;
			this.date = date;
			this.style = style;
		}

		String name;
		Date date;
		int style; // 1为故障 0为信息
	}

	@Override
	public void onClick(View arg0) {
		InforVo inforVo = (InforVo) arg0.getTag();
		Intent intent = new Intent();
		// intent.putExtra("data", inforVo);
		intent.putExtra("name", inforVo.getName());
		intent.putExtra("time", "2014/11/11 11:11");
		if (inforVo.getStyle() == 1) {
			intent.setClass(context, InfoErrorActivity.class);
			intent.putExtra("detail", "电器故障");
		} else {
			intent.setClass(context, InfoTipActivity.class);
			intent.putExtra("detail", "电器提示");
		}
		context.startActivity(intent);
	}
}
