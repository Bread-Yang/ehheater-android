/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.vanward.ehheater.view.wheelview.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.application.EhHeaterApplication;
import com.vanward.ehheater.util.PxUtil;
import com.vanward.ehheater.view.MarqueeTextView;
import com.vanward.ehheater.view.wheelview.WheelView;

/**
 * Abstract wheel adapter provides common functionality for adapters.
 */
public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {

	/** Text view resource. Used as a default view for adapter. */
	public static final int TEXT_VIEW_ITEM_RESOURCE = -1;

	/** No resource constant. */
	protected static final int NO_RESOURCE = 0;

	/** Default text color */
	public static final int DEFAULT_TEXT_COLOR = 0xFF101010;

	/** Default text color */
	public static final int LABEL_COLOR = 0xFF700070;

	// Text settings
	private int textColor = DEFAULT_TEXT_COLOR;

	// Current context
	protected Context context;
	// Layout inflater
	protected LayoutInflater inflater;

	/** 布局 : R.layout.XXX */
	protected int itemResourceId;
	/** 布局中的TextView : R.id.XXX */
	protected int itemTextResourceId;

	// Empty items resources
	protected int emptyItemResourceId;

	/** 默认字体 */
	private Typeface normal_text_typeface = EhHeaterApplication.number_tf;

	/** 高亮字体 */
	private Typeface highlight_text_typeface = EhHeaterApplication.number_tf;

	private WheelView wheelView;

	public static final Paint measurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	static {
		measurePaint
				.setTextSize(22 * EhHeaterApplication.device_density + 0.5f);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 */
	protected AbstractWheelTextAdapter(Context context, WheelView wheelView) {
		this(context, TEXT_VIEW_ITEM_RESOURCE, wheelView);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param itemResource
	 *            the resource ID for a layout file containing a TextView to use
	 *            when instantiating items views
	 */
	protected AbstractWheelTextAdapter(Context context, int itemResource,
			WheelView wheelView) {
		this(context, itemResource, NO_RESOURCE, wheelView);
		// this(context, R.layout.wheel_text_item, R.id.text);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param itemResource
	 *            the resource ID for a layout file containing a TextView to use
	 *            when instantiating items views
	 * @param itemTextResource
	 *            the resource ID for a text view in the item layout
	 */
	protected AbstractWheelTextAdapter(Context context, int itemResource,
			int itemTextResource, WheelView wheelView) {
		this.context = context;
		this.wheelView = wheelView;
		itemResourceId = itemResource;
		itemTextResourceId = itemTextResource;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Gets text color
	 * 
	 * @return the text color
	 */
	public int getTextColor() {
		return textColor;
	}

	/**
	 * Sets text color
	 * 
	 * @param textColor
	 *            the text color to set
	 */
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public Typeface getText_typeface() {
		return normal_text_typeface;
	}

	public void setText_typeface(Typeface text_typeface) {
		this.normal_text_typeface = text_typeface;
	}

	/**
	 * Gets resource Id for items views
	 * 
	 * @return the item resource Id
	 */
	public int getItemResource() {
		return itemResourceId;
	}

	/**
	 * Sets resource Id for items views
	 * 
	 * @param itemResourceId
	 *            the resource Id to set
	 */
	public void setItemResource(int itemResourceId) {
		this.itemResourceId = itemResourceId;
	}

	/**
	 * Gets resource Id for text view in item layout
	 * 
	 * @return the item text resource Id
	 */
	public int getItemTextResource() {
		return itemTextResourceId;
	}

	/**
	 * Sets resource Id for text view in item layout
	 * 
	 * @param itemTextResourceId
	 *            the item text resource Id to set
	 */
	public void setItemTextResource(int itemTextResourceId) {
		this.itemTextResourceId = itemTextResourceId;
	}

	/**
	 * Gets resource Id for empty items views
	 * 
	 * @return the empty item resource Id
	 */
	public int getEmptyItemResource() {
		return emptyItemResourceId;
	}

	/**
	 * Sets resource Id for empty items views
	 * 
	 * @param emptyItemResourceId
	 *            the empty item resource Id to set
	 */
	public void setEmptyItemResource(int emptyItemResourceId) {
		this.emptyItemResourceId = emptyItemResourceId;
	}

	/**
	 * Returns text for specified item
	 * 
	 * @param index
	 *            the item index
	 * @return the text of specified items
	 */
	public abstract CharSequence getItemText(int index);

	@Override
	public View getItem(int index, View convertView, ViewGroup parent) {
		if (index >= 0 && index < getItemsCount()) {
			String text = (String) getItemText(index);
			if (convertView == null) {
				// Log.e("parent的宽度", parent.getWidth() + "");
				// if (measurePaint.measureText(text) > parent.getWidth()) {
				// convertView = getMarqueeView(itemResourceId, index, parent);
				// } else {
				// convertView = getView(itemResourceId, parent);
				// }
				if (text.length() > 10) {
					convertView = getMarqueeView(itemResourceId, index, parent);
				} else {
					convertView = getView(itemResourceId, parent);
				}
			}
			TextView textView = (TextView) convertView;
			// getTextView(convertView, itemTextResourceId);
			if (textView != null) {

				if (text == null) {
					text = "";
				}
				// if (!text.startsWith("智感杀菌")) {
				textView.setText(text);
				if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
					configureTextView(textView);
				}
				// }
			}
			// textView.setTypeface(MyConstant.number_tf);
			if (wheelView.getHightLightItemIndex() == index) {
				if (wheelView.getEnabled()) {
					textView.setTextColor(context.getResources().getColor(
							wheelView.getText_highlight_color_resId())); // hightlight的item的颜色
				} else {
					textView.setTextColor(context.getResources().getColor(
							wheelView.getText_normal_color_resId()));
				}
				textView.setTextSize(wheelView.getHighlight_textSize());
				textView.setPadding(0, 3, 0, 3);
				// textView.setSingleLine(true);
				// textView.setEllipsize(TruncateAt.MARQUEE);
//				if (textView.getText().toString().length() < 7) {
//					textView.setTypeface(normal_text_typeface, Typeface.BOLD);
//				}
			}else{
				textView.setTextColor(context.getResources().getColor(
						wheelView.getText_normal_color_resId()));
			}
			return convertView;
		}
		return null;
	}

	@Override
	public View getEmptyItem(View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getView(emptyItemResourceId, parent);
		}
		if (emptyItemResourceId == TEXT_VIEW_ITEM_RESOURCE
				&& convertView instanceof TextView) {
			configureTextView((TextView) convertView);
		}

		return convertView;
	}

	/**
	 * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
	 * 
	 * @param view
	 *            the text view to be configured
	 */
	protected void configureTextView(TextView view) {
		if (wheelView.getEnabled()) {
			view.setTextColor(textColor);
		} else {
			view.setTextColor(context.getResources().getColor(
					R.color.wheelview_disable_item));
		}

		// view.getPaint().setFakeBoldText(true);//加粗
		view.getPaint().setTypeface(highlight_text_typeface);
		// view.setTextColor(context.getResources().getColor(R.color.black));

		// view.getPaint().setTypeface(text_typeface);
		view.setGravity(Gravity.CENTER);
		view.setTextSize(wheelView.getNormal_textSize());
		view.setLines(1);
		// Log.e("打印wheelview的text", view.getText().toString() + "string");
	}

	/**
	 * Loads a text view from view
	 * 
	 * @param view
	 *            the text view or layout containing it
	 * @param textResource
	 *            the text resource Id in layout
	 * @return the loaded text view
	 */
	private TextView getTextView(View view, int textResource) {
		TextView text = null;
		try {
			if (textResource == NO_RESOURCE) {
				text = (TextView) view;
			} else if (textResource != NO_RESOURCE) {
				text = (TextView) view.findViewById(textResource);
			}
			// if (view instanceof MarqueeTextView) {
			// text = (MarqueeTextView) view;
			// }
		} catch (ClassCastException e) {
			Log.e("AbstractWheelAdapter",
					"You must supply a resource ID for a TextView");
			throw new IllegalStateException(
					"AbstractWheelAdapter requires the resource ID to be a TextView",
					e);
		}
		text.getPaint().setTypeface(normal_text_typeface);
		return text;
	}

	/**
	 * Loads view from resources
	 * 
	 * @param resource
	 *            the resource Id
	 * @return the loaded view or null if resource is not set
	 */
	private View getView(int resource, ViewGroup parent) {
		switch (resource) {
		case NO_RESOURCE:
			return null;
		case TEXT_VIEW_ITEM_RESOURCE:
			// TextView textView = (TextView) inflater.inflate(
			// R.layout.item_wheel_textview, parent, false);
			TextView textView = new TextView(context);
			int height = PxUtil.dip2px(context, wheelView.getTextHeight());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, height);
			params.gravity = Gravity.CENTER;
			textView.setLayoutParams(params);
			textView.getPaint().setTypeface(normal_text_typeface);
			textView.setTextColor(context.getResources()
					.getColor(R.color.black));
			int padding = PxUtil.dip2px(context, 5);
			textView.setPadding(0, padding, 0, padding);

			// textView.setSingleLine(true);
			// textView.setEllipsize(TruncateAt.MARQUEE); // 走马灯
			// textView.setSelected(true);

			return textView;
		default:
			return inflater.inflate(resource, parent, false);
		}
	}

	public View getMarqueeView(int resource, int index, ViewGroup parent) {
		switch (resource) {
		case NO_RESOURCE:
			return null;
		case TEXT_VIEW_ITEM_RESOURCE:
			// TextView textView = (TextView) inflater.inflate(
			// R.layout.item_wheel_textview, parent, false);
			MarqueeTextView textView = new MarqueeTextView(context, parent);
			int height = PxUtil.dip2px(context, wheelView.getTextHeight());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, height);
			params.gravity = Gravity.CENTER;
			textView.setTextColor(context.getResources()
					.getColor(R.color.black));
			textView.setLayoutParams(params);
			textView.getPaint().setTypeface(normal_text_typeface);

			textView.setY(height / 2 + height / 5);
			// int padding = com.midea.sk.chart.PxUtil.dip2px(context, 5);
			// textView.setPadding(0, padding, 0);

			textView.setGravity(Gravity.CENTER);
			textView.setTextSize(wheelView.getNormal_textSize());
			textView.setLines(1);
			textView.setSingleLine(true);
			textView.setEllipsize(TruncateAt.MARQUEE); // 走马灯
			textView.setText((String) getItemText(index));
			// textView.setText(context.getResources().getStringArray(
			// R.array.upper_data)[index]);
			// textView.setSelected(true);

			return textView;
		default:
			return inflater.inflate(resource, parent, false);
		}

	}
}
