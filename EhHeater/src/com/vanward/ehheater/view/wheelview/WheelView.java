/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 * 
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

package com.vanward.ehheater.view.wheelview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.vanward.ehheater.R;
import com.vanward.ehheater.util.PxUtil;
import com.vanward.ehheater.view.wheelview.adapters.AbstractWheelTextAdapter;
import com.vanward.ehheater.view.wheelview.adapters.WheelViewAdapter;

/**
 * Numeric wheel view.
 * 
 * @author Yuri Kanivets
 */
public class WheelView extends View {
	private static final String TAG = "WheelView";

	/** wheelview左上右下四边框的宽度,单位为dp */
	int stroke_width_dp = 2;

	/** wheelview左上右下四边框的宽度,单位为px */
	int stroke_width_px;

	/** 设置字体 */
	private Typeface tf;

	/** 高亮显示的文本右边的label字符串的画笔 */
	private Paint center_label_paint;

	/** 高亮显示的文本右边的label字符串 */
	private String center_label;

	/** 高亮显示的文本右边的label字符串相对于中点的位置, 单位dp */
	private int center_label_position = -1;

	/** 当前所hightlight的item的索引 */
	private int hightLightItemIndex = 0;

	/** label的大小,单位是sp,默认是20sp */
	private int label_size = 20;

	/** 不高亮时字体的大小 */
	private int normal_textSize = 22;

	/** 高亮时字体的大小 */
	private int highlight_textSize = 22;

	private int text_highlight_color_resId = R.color.white;

	private int text_normal_color_resId = R.color.wheelview_grey_label;

	private boolean hasLeftBorder = true;

	private boolean hasTopBorder = true;

	private boolean hasRightBorder = true;

	private boolean hasBottomBorder = true;

	private Context context;

	private int textHeight;

	/** Top and bottom shadows colors */
	// private int[] SHADOWS_COLORS = new int[] { 0x00FFFFFF, 0x00FFFFFF,
	// 0x00FFFFFF };
	private int[] SHADOWS_COLORS = new int[] { 0x00ffffff, 0x00FFFFFF, };
	private int[] SHADOWS_FONT_SCOLORS = new int[] { 0x00FFFFFF, 0x00FFFFFF };
	// private int[] SHADOWS_FONT_SCOLORS = new int[] { 0x80FFFFFF, 0x80FFFFFF,
	// 0x80FFFFFF };
	// private int[] SHADOWS_COLORS = new int[] { 0xFF111111, 0x00AAAAAA,
	// 0x00AAAAAA };

	/** Top and bottom items offset (to hide that) */
	private static final int ITEM_OFFSET_PERCENT = 0;

	/** Left and right padding value */
	private static final int PADDING = 10;

	/** Default count of visible items */
	private static final int DEF_VISIBLE_ITEMS = 5;

	// Wheel Values
	private int currentItem = 0;

	// Count of visible items
	private int visibleItems = DEF_VISIBLE_ITEMS;

	// Item height
	private int itemHeight = 0;

	// Center Line
	private Drawable centerDrawable;

	// Wheel drawables
	private int wheelBackground = R.drawable.layerlist_wheel_bg;
	private int wheelForeground = R.color.transparent;

	// Shadows drawables
	private GradientDrawable topShadow;
	private GradientDrawable bottomShadow;

	// Draw Shadows
	private boolean ifDrawShadows = true;

	// Scrolling
	private WheelScroller scroller;
	private boolean isScrollingPerformed;
	private int scrollingOffset;

	/** 滚轮是否可以滑动, 默认是true */
	private boolean isEnable = true;

	// Cyclic
	boolean isCyclic = false;

	// Items layout
	private LinearLayout itemsLayout;

	// The number of first item in layout
	private int firstItem;

	// View adapter
	private WheelViewAdapter viewAdapter;

	// Recycle
	private WheelRecycle recycle = new WheelRecycle(this);

	// Listeners
	private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
	private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();
	private List<OnWheelClickedListener> clickingListeners = new LinkedList<OnWheelClickedListener>();

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context, attrs);
	}

	public int getTextHeight() {
		return textHeight;
	}

	public void setTextHeight(int textHeight) {
		this.textHeight = textHeight;
	}

	/**
	 * Initializes class data
	 * 
	 * @param context
	 *            the context
	 */
	private void initData(Context context, AttributeSet attrs) {
		TypedArray type_array = context.obtainStyledAttributes(attrs,
				R.styleable.WheelView);
		wheelBackground = (int) type_array.getResourceId(
				R.styleable.WheelView_wheelBackground,
				R.drawable.layerlist_wheel_bg);
		wheelForeground = (int) type_array.getResourceId(
				R.styleable.WheelView_wheelForeground,
				R.drawable.shape_wheel_fg_orange);
		center_label = type_array
				.getString(R.styleable.WheelView_wheelCenterLabel);
		center_label_position = type_array.getInteger(
				R.styleable.WheelView_centerLabelPosition, -1);
		hasLeftBorder = type_array.getBoolean(
				R.styleable.WheelView_hasLeftBorder, false);
		hasTopBorder = type_array.getBoolean(
				R.styleable.WheelView_hasTopBorder, false);
		hasRightBorder = type_array.getBoolean(
				R.styleable.WheelView_hasRightBorder, false);
		hasBottomBorder = type_array.getBoolean(
				R.styleable.WheelView_hasBottomBorder, false);
		text_highlight_color_resId = type_array.getResourceId(
				R.styleable.WheelView_textHighlightColor, R.color.white);
		text_normal_color_resId = type_array.getResourceId(
				R.styleable.WheelView_textNormalColor,
				R.color.wheelview_grey_label);
		normal_textSize = (int) type_array.getDimension(
				R.styleable.WheelView_normalTextSize, 22);
		highlight_textSize = (int) type_array.getDimension(
				R.styleable.WheelView_highlightTextSize, 25);
		textHeight = (int) type_array.getDimension(
				R.styleable.WheelView_TextHeight, 40);
		isEnable = type_array.getBoolean(R.styleable.WheelView_enabled, true);
		super.setEnabled(isEnable);

		type_array.recycle();

		this.scroller = new WheelScroller(getContext(), scrollingListener);
		this.context = context;

		center_label_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		center_label_paint.setColor(Color.WHITE);
		stroke_width_px = PxUtil.dip2px(context, stroke_width_dp);
	}

	// Scrolling listener
	WheelScroller.ScrollingListener scrollingListener = new WheelScroller.ScrollingListener() {
		@Override
		public void onStarted() {
			isScrollingPerformed = true;
			notifyScrollingListenersAboutStart();
		}

		@Override
		public void onScroll(int distance) {
			doScroll(distance);

			int height = getHeight();
			if (scrollingOffset > height) {
				scrollingOffset = height;
				scroller.stopScrolling();
			} else if (scrollingOffset < -height) {
				scrollingOffset = -height;
				scroller.stopScrolling();
			}
		}

		@Override
		public void onFinished() {
			if (isScrollingPerformed) {
				notifyScrollingListenersAboutEnd();
				isScrollingPerformed = false;
			}

			scrollingOffset = 0;
			invalidate();
		}

		@Override
		public void onJustify() {
			if (Math.abs(scrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
				scroller.scroll(scrollingOffset, 0);
			}
		}
	};

	/**
	 * Set the the specified scrolling interpolator
	 * 
	 * @param interpolator
	 *            the interpolator
	 */
	public void setInterpolator(Interpolator interpolator) {
		scroller.setInterpolator(interpolator);
	}

	/**
	 * Gets count of visible items
	 * 
	 * @return the count of visible items
	 */
	public int getVisibleItems() {
		return visibleItems;
	}

	/**
	 * Sets the desired count of visible items. Actual amount of visible items
	 * depends on wheel layout parameters. To apply changes and rebuild view
	 * call measure().
	 * 
	 * @param count
	 *            the desired count for visible items
	 */
	public void setVisibleItems(int count) {
		visibleItems = count;
	}

	/**
	 * Gets view adapter
	 * 
	 * @return the view adapter
	 */
	public WheelViewAdapter getViewAdapter() {
		return viewAdapter;
	}

	// Adapter listener
	private DataSetObserver dataObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			invalidateWheel(false);
		}

		@Override
		public void onInvalidated() {
			invalidateWheel(true);
		}
	};

	/**
	 * Sets view adapter. Usually new adapters contain different views, so it
	 * needs to rebuild view by calling measure().
	 * 
	 * @param viewAdapter
	 *            the view adapter
	 */
	public void setViewAdapter(WheelViewAdapter viewAdapter) {
		stopScrolling();
		if (this.viewAdapter != null) {
			this.viewAdapter.unregisterDataSetObserver(dataObserver);
		}
		this.viewAdapter = viewAdapter;
		if (this.viewAdapter != null) {
			this.viewAdapter.registerDataSetObserver(dataObserver);
		}

		// 如果是ArrayWheelAdapter或者NumbericWheelAdapter的话,则添加监听
		if (viewAdapter instanceof AbstractWheelTextAdapter) {
			if (viewAdapter.getItemsCount() == 1) {
				// setCurrentItem(0); // 将index = 0 的item高亮
			}
			firstItem = 0;
			setCurrentItem(0); // 将index = 0 的item高亮

			addScrollingListener(new OnWheelScrollListener() {

				@Override
				public void onScrollingStarted(WheelView wheel) {
					WheelViewAdapter adapter = WheelView.this.getViewAdapter();
					// if (adapter instanceof AbstractWheelTextAdapter) {
					// ((AbstractWheelTextAdapter) adapter)
					// .setHightLightItemIndex(-1);
					// }
					hightLightItemIndex = -1;
					invalidateWheel(true);
				}

				@Override
				public void onScrollingFinished(WheelView wheel) {

					WheelViewAdapter adapter = WheelView.this.getViewAdapter();
					// if (adapter instanceof AbstractWheelTextAdapter) {
					// ((AbstractWheelTextAdapter) adapter)
					// .setHightLightItemIndex(getCurrentItem());
					// }
					hightLightItemIndex = getCurrentItem();
					invalidateWheel(true);
				}
			});

		}

		// 告诉adapter我是否是enable的
		// WheelViewAdapter adapter = WheelView.this.getViewAdapter();
		// if (adapter instanceof AbstractWheelTextAdapter) {
		// // 如果enable是false,则adapter里面的全部view颜色设成灰色
		// ((AbstractWheelTextAdapter) adapter).setEnable(this.isEnabled());
		// }

		invalidateWheel(true);
	}

	/**
	 * Adds wheel changing listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addChangingListener(OnWheelChangedListener listener) {
		changingListeners.add(listener);
	}

	/**
	 * Removes wheel changing listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeChangingListener(OnWheelChangedListener listener) {
		changingListeners.remove(listener);
	}

	public void removeAllChangingListener() {
		changingListeners.clear();
	}

	/**
	 * Notifies changing listeners
	 * 
	 * @param oldValue
	 *            the old wheel value
	 * @param newValue
	 *            the new wheel value
	 */
	protected void notifyChangingListeners(int oldValue, int newValue) {
		for (OnWheelChangedListener listener : changingListeners) {
			listener.onChanged(this, oldValue, newValue);
		}
	}

	/**
	 * Adds wheel scrolling listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.add(listener);
	}

	/**
	 * Removes wheel scrolling listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.remove(listener);
	}

	public void removeAllScrollingListener() {
		scrollingListeners.clear();
	}

	/**
	 * Notifies listeners about starting scrolling
	 */
	protected void notifyScrollingListenersAboutStart() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingStarted(this);
		}
	}

	/**
	 * Notifies listeners about ending scrolling
	 */
	protected void notifyScrollingListenersAboutEnd() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingFinished(this);
		}
	}

	/**
	 * Adds wheel clicking listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addClickingListener(OnWheelClickedListener listener) {
		clickingListeners.add(listener);
	}

	/**
	 * Removes wheel clicking listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeClickingListener(OnWheelClickedListener listener) {
		clickingListeners.remove(listener);
	}

	/**
	 * Notifies listeners about clicking
	 */
	protected void notifyClickListenersAboutClick(int item) {
		for (OnWheelClickedListener listener : clickingListeners) {
			listener.onItemClicked(this, item);
		}
	}

	/**
	 * Gets current value
	 * 
	 * @return the current value
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	public int getText_highlight_color_resId() {
		return text_highlight_color_resId;
	}

	public void setText_highlight_color_resId(int text_highlight_color_resId) {
		this.text_highlight_color_resId = text_highlight_color_resId;
	}

	public int getText_normal_color_resId() {
		return text_normal_color_resId;
	}

	public void setText_normal_color_resId(int text_normal_color_resId) {
		this.text_normal_color_resId = text_normal_color_resId;
	}

	public int getNormal_textSize() {
		return normal_textSize;
	}

	public void setNormal_textSize(int normal_textSize) {
		this.normal_textSize = normal_textSize;
	}

	public int getHighlight_textSize() {
		return highlight_textSize;
	}

	public void setHightlight_textSize(int hightlight_textSize) {
		this.highlight_textSize = hightlight_textSize;
	}

	public int getHightLightItemIndex() {
		return hightLightItemIndex;
	}

	public void setHightLightItemIndex(int hightLightItemIndex) {
		this.hightLightItemIndex = hightLightItemIndex;
	}

	/**
	 * Sets the current item. Does nothing when index is wrong.
	 * 
	 * @param index
	 *            the item index
	 * @param animated
	 *            the animation flag
	 */
	public void setCurrentItem(int index, boolean animated) {
		WheelViewAdapter adapter = getViewAdapter();
		// 当停止滚动的时候
		if (!isScrollingPerformed
				&& adapter instanceof AbstractWheelTextAdapter) {
			hightLightItemIndex = index;
			// ((AbstractWheelTextAdapter)
			// adapter).setHightLightItemIndex(index);
		}
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0
				|| this.getCurrentItem() == index) {
			invalidateWheel(true);
			return; // throw?
		}

		int itemCount = viewAdapter.getItemsCount();
		if (index < 0 || index >= itemCount) {
			if (isCyclic) {
				while (index < 0) {
					index += itemCount;
				}
				index %= itemCount;
			} else {
				return; // throw?
			}
		}
		if (index != currentItem) {
			if (animated) {
				int itemsToScroll = index - currentItem;
				if (isCyclic) {
					int scroll = itemCount + Math.min(index, currentItem)
							- Math.max(index, currentItem);
					if (scroll < Math.abs(itemsToScroll)) {
						itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
					}
				}
				scroll(itemsToScroll, 0);
			} else {
				scrollingOffset = 0;

				int old = currentItem;
				currentItem = index;

				notifyChangingListeners(old, currentItem);

				invalidateWheel(true);
			}
		}
	}

	/**
	 * Sets the current item w/o animation. Does nothing when index is wrong.
	 * 
	 * @param index
	 *            the item index
	 */
	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}

	/**
	 * Tests if wheel is cyclic. That means before the 1st item there is shown
	 * the last one
	 * 
	 * @return true if wheel is cyclic
	 */
	public boolean isCyclic() {
		return isCyclic;
	}

	/**
	 * Set wheel cyclic flag
	 * 
	 * @param isCyclic
	 *            the flag to set
	 */
	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;
		invalidateWheel(false);
	}

	/**
	 * Determine whether shadows are drawn
	 * 
	 * @return true is shadows are drawn
	 */
	public boolean drawShadows() {
		return ifDrawShadows;
	}

	/**
	 * Set whether shadows should be drawn
	 * 
	 * @param drawShadows
	 *            flag as true or false
	 */
	public void setDrawShadows(boolean drawShadows) {
		this.ifDrawShadows = drawShadows;
	}

	/**
	 * Set the shadow gradient color
	 * 
	 * @param start
	 * @param middle
	 * @param end
	 */
	public void setShadowColor(int start, int middle, int end) {
		SHADOWS_COLORS = new int[] { start, middle, end };
	}

	/**
	 * Sets the drawable for the wheel background
	 * 
	 * @param resource
	 */
	public void setWheelBackground(int resource) {
		wheelBackground = resource;
		setBackgroundResource(wheelBackground);
	}

	/**
	 * Sets the drawable for the wheel foreground
	 * 
	 * @param resource
	 */
	public void setWheelForeground(int resource) {
		wheelForeground = resource;
		centerDrawable = getContext().getResources().getDrawable(
				wheelForeground);
	}

	/**
	 * Invalidates wheel
	 * 
	 * @param clearCaches
	 *            if true then cached views will be clear
	 */
	public void invalidateWheel(boolean clearCaches) {
		if (clearCaches) {
			recycle.clearAll();
			if (itemsLayout != null) {
				itemsLayout.removeAllViews();
			}
			scrollingOffset = 0;
		} else if (itemsLayout != null) {
			// cache all items
			recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
		}

		invalidate();
	}

	/**
	 * Initializes resources
	 */
	private void initResourcesIfNecessary() {
		if (centerDrawable == null) {
			centerDrawable = getContext().getResources().getDrawable(
					wheelForeground);
		}

		if (topShadow == null) {
			topShadow = new GradientDrawable(Orientation.TOP_BOTTOM,
					SHADOWS_COLORS);
		}

		if (bottomShadow == null) {
			bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP,
					SHADOWS_COLORS);
		}

		setBackgroundResource(wheelBackground);
	}

	/**
	 * Calculates desired height for layout
	 * 
	 * @param layout
	 *            the source layout
	 * @return the desired layout height
	 */
	private int getDesiredHeight(LinearLayout layout) {
		if (layout != null && layout.getChildAt(0) != null) {
			itemHeight = layout.getChildAt(0).getMeasuredHeight();
		}

		int desired = itemHeight * visibleItems - itemHeight
				* ITEM_OFFSET_PERCENT / 50;

		return Math.max(desired, getSuggestedMinimumHeight());
	}

	/**
	 * Returns height of wheel item
	 * 
	 * @return the item height
	 */
	private int getItemHeight() {
		if (itemHeight != 0) {
			return itemHeight;
		}

		if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
			itemHeight = itemsLayout.getChildAt(0).getHeight();
			return itemHeight;
		}

		return getHeight() / visibleItems;
	}

	/**
	 * Calculates control width and creates text layouts
	 * 
	 * @param widthSize
	 *            the input layout width
	 * @param mode
	 *            the layout mode
	 * @return the calculated control width
	 */
	private int calculateLayoutWidth(int widthSize, int mode) {
		initResourcesIfNecessary();

		// TODO: make it static
		itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		itemsLayout
				.measure(MeasureSpec.makeMeasureSpec(widthSize,
						MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
						0, MeasureSpec.UNSPECIFIED));
		int width = itemsLayout.getMeasuredWidth();

		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width += 2 * PADDING;

			// Check against our minimum width
			width = Math.max(width, getSuggestedMinimumWidth());

			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
				width = widthSize;
			}
		}

		itemsLayout.measure(MeasureSpec.makeMeasureSpec(width - 2 * PADDING,
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED));

		return width;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Log.e(TAG, "onMeasure被调用了");
		// LogOnMeasureInfo(widthMeasureSpec, heightMeasureSpec);

		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		buildViewForMeasuring();

		int setWidth = calculateLayoutWidth(widthSpecSize, widthSpecMode);

		int setHeight;

		// MeasureSpec.EXACTLY是精确尺寸，当我们将控件的layout_width或layout_height指定为具体数值时如andorid:layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
		if (heightSpecMode == MeasureSpec.EXACTLY) {
			setHeight = heightSpecSize;
		} else {
			setHeight = getDesiredHeight(itemsLayout);

			// MeasureSpec.AT_MOST是最大尺寸，当控件的layout_width或layout_height指定为WRAP_CONTENT时，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
			if (heightSpecMode == MeasureSpec.AT_MOST) {
				setHeight = Math.min(setHeight, heightSpecSize);
			}
			
			// MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，通过measure方法传入的模式。
		}

		setMeasuredDimension(setWidth, setHeight);
	}

	/** 后台打印onMeasure参数 */
	private void LogOnMeasureInfo(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		String heightSpecModeString = null;
		String widthSpecModelString = null;
		switch (heightSpecMode) {
		case MeasureSpec.AT_MOST:
			heightSpecModeString = "MeasureSpec.AT_MOST";
			break;
		case MeasureSpec.EXACTLY:
			heightSpecModeString = "MeasureSpec.EXACTLY";
			break;
		case MeasureSpec.UNSPECIFIED:
			heightSpecModeString = "MeasureSpec.EXACTLY";
			break;
		}

		switch (widthSpecMode) {
		case MeasureSpec.AT_MOST:
			widthSpecModelString = "MeasureSpec.AT_MOST";
			break;
		case MeasureSpec.EXACTLY:
			widthSpecModelString = "MeasureSpec.EXACTLY";
			break;
		case MeasureSpec.UNSPECIFIED:
			widthSpecModelString = "MeasureSpec.EXACTLY";
			break;
		}

		int widthSpecSize2Dp = PxUtil.px2dip(getContext(), widthSpecSize);
		int heightSpecSize2Dp = PxUtil.px2dip(getContext(), heightSpecSize);

		// Log.e(TAG, widthSpecModelString);
		// Log.e"TAG, heightSpecModeString);
		// Log.e(TAG, widthSpecSize + "px");
		// Log.e(TAG, heightSpecSize + "px");
		// Log.e(TAG, widthSpecSize2Dp + "dp");
		// Log.e(TAG, heightSpecSize2Dp + "dp");
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layout(r - l, b - t);
	}

	/**
	 * Sets layouts width and height
	 * 
	 * @param width
	 *            the layout width
	 * @param height
	 *            the layout height
	 */
	private void layout(int width, int height) {
		int itemsWidth = width - 2 * PADDING;

		itemsLayout.layout(0, 0, itemsWidth, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Log.e(TAG, "onDraw被调用了");
//		drawCenterRect(canvas);

		if (viewAdapter != null && viewAdapter.getItemsCount() > 0) {
			updateView();
			drawItems(canvas);
		}

		if (ifDrawShadows && isEnabled())
			drawShadows(canvas);
	}

	/**
	 * Draws shadows on top and bottom of control
	 * 
	 * @param canvas
	 *            the canvas for drawing
	 */
	private void drawShadows(Canvas canvas) {
		int left_position = 0;
		int right_position = getWidth();
		int top_position = 0;
		int bottom_position = getHeight();
		if (hasLeftBorder) {
			left_position = stroke_width_px;
		}
		if (hasTopBorder) {
			top_position = stroke_width_px;
		}
		if (hasRightBorder) {
			right_position = getWidth() - stroke_width_px;
		}
		if (hasBottomBorder) {
			bottom_position = getHeight() - stroke_width_px;
		}

		// if (wheelBackground ==
		// R.drawable.layerlist_wheel_bg_without_right_border) {
		// left_position = stroke_width_px;
		// right_position = getWidth();
		// } else if (wheelBackground ==
		// R.drawable.layerlist_wheel_bg_without_left_border) {
		// left_position = 0;
		// right_position = getWidth() - stroke_width_px;
		// } else if (wheelBackground ==
		// R.drawable.layerlist_wheel_bg_without_border) {
		// left_position = 0;
		// right_position = getWidth();
		// top_position = 0;
		// bottom_position = getHeight();
		// } else if (wheelBackground ==
		// R.drawable.layerlist_wheel_bg_without_left_right_border) {
		// left_position = 0;
		// right_position = getWidth();
		// } else {
		// left_position = stroke_width_px;
		// right_position = getWidth() - stroke_width_px;
		// }

		int height = (int) getItemHeight() * 2 + getItemHeight() / 2;
		// topShadow.setColors(SHADOWS_FONT_SCOLORS); // 字体变灰(要4.1以上才能用)
		topShadow = new GradientDrawable(Orientation.TOP_BOTTOM,
				SHADOWS_FONT_SCOLORS);
		topShadow.setBounds(left_position, top_position, right_position, height
				+ stroke_width_px);
		topShadow.draw(canvas);
		// topShadow.setColors(SHADOWS_COLORS);
		// topShadow.draw(canvas);

		// bottomShadow.setColors(SHADOWS_FONT_SCOLORS); // (要4.1以上才能用)
		bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP,
				SHADOWS_FONT_SCOLORS);
		bottomShadow.setBounds(left_position, getHeight() - height
				- stroke_width_px, right_position, bottom_position);
		bottomShadow.draw(canvas);
		// bottomShadow.setColors(SHADOWS_COLORS);
		// bottomShadow.draw(canvas);

		// topShadow = new GradientDrawable(Orientation.TOP_BOTTOM,
		// new int[] { 0x00000000, 0x00000000,
		// 0x60F2F2F2});
		//
		// topShadow.setBounds(stroke_width_px, stroke_width_px, getWidth()
		// - stroke_width_px, getHeight() - stroke_width_px);
		// topShadow.draw(canvas);
	}

	/**
	 * Draws items
	 * 
	 * @param canvas
	 *            the canvas for drawing
	 */
	private void drawItems(Canvas canvas) {
		canvas.save();

		int top = (currentItem - firstItem) * getItemHeight()
				+ (getItemHeight() - getHeight()) / 2;
		canvas.translate(PADDING, -top + scrollingOffset);

		itemsLayout.draw(canvas);

		canvas.restore();
	}

	/**
	 * Draws rect for current value
	 * 
	 * @param canvas
	 *            the canvas for drawing
	 */
	private void drawCenterRect(Canvas canvas) {

		float scale = this.getResources().getDisplayMetrics().density;

		int center = getHeight() / 2;
		// int offset = (int) (getItemHeight() / 2 * 1.2);
		// int offset = (int) (getItemHeight() / 2);
		int offset = (int) (getHeight() / visibleItems / 2);
		// int offset = (int) (getItemHeight() / 2 * 0.8);
		// Log.e(TAG, (getItemHeight() / 2) + "");
		float plus_offset = 3 * scale + 0.5f;
		int left_offset = 0;
		int top_offset = (int) (center - offset + plus_offset);
		int right_offset = getWidth();
		int bottom_offset = (int) (center + offset + plus_offset);
		if (hasLeftBorder) {
			left_offset = stroke_width_px;
		}
		if (hasRightBorder) {
			right_offset = getWidth() - stroke_width_px;
		}

		centerDrawable.setBounds(left_offset, top_offset, right_offset,
				bottom_offset);

		// if (wheelBackground ==
		// R.drawable.layerlist_wheel_bg_without_right_border) {
		// centerDrawable.setBounds(stroke_width_px, center - offset,
		// getWidth(), center + offset);
		// } else if (wheelBackground ==
		// R.drawable.layerlist_wheel_bg_without_left_border) {
		// centerDrawable.setBounds(0, center - offset, getWidth()
		// - stroke_width_px, center + offset);
		// } else if (wheelBackground ==
		// R.drawable.layerlist_wheel_bg_without_border) {
		// centerDrawable.setBounds(0, center - offset, getWidth(), center
		// + offset);
		// } else if (wheelBackground ==
		// R.drawable.layerlist_wheel_bg_without_left_right_border) {
		// centerDrawable.setBounds(0, center - offset, getWidth(), center
		// + offset);
		// } else {
		// centerDrawable.setBounds(stroke_width_px, center - offset,
		// getWidth() - stroke_width_px, center + offset);
		// }
		// Rect centerRect = new Rect(stroke_width_px, center - offset,
		// getWidth() - stroke_width_px, center + offset);
		// Paint paint = new Paint();
		// paint.setColor(Color.parseColor("#FF091A40"));
		// canvas.drawRect(centerRect, paint);
		centerDrawable.draw(canvas);

		// 如果label设置了,则画出label
		if (null != center_label && !center_label.equals("")) {
			// dp转px(公式 : px = dp * scale + 0.5f)
			float px = label_size * scale + 0.5f;
			center_label_paint.setTextSize(px);
			// 距离10dp
			float margin_right = 10 * scale + 0.5f;
			float baseX = 0;
			// 如果设置了center_label的位置
			if (center_label_position > 0) {
				baseX = getWidth() / 2 + center_label_position * scale + 0.5f;
			} else {
				baseX = getWidth()
						- center_label_paint.measureText(center_label)
						- margin_right;
			}
			Rect bounds = new Rect();
			center_label_paint.getTextBounds(center_label, 0,
					center_label.length(), bounds);

			// float baseY = (float) (getHeight() / 2 + getItemHeight() / 4);
			// float baseY =
			// (float)(getHeight()-bounds.height())/2+bounds.height() -6 ; //
			// 也可以正确居中
			float baseY = (float) ((getHeight() / 2) - ((center_label_paint
					.descent() + center_label_paint.ascent()) / 2));

			// 在中间的最右边显示label, y是指定这个字符baseline在屏幕上的位置
			canvas.drawText(center_label, baseX, baseY, center_label_paint);
		}
	}

	private void drawTopAndBottom(Canvas canvas) {
		// Drawable alpha = getContext().getResources().getDrawable(
		// R.drawable.shape_wheel_top_and_bottom_bg);
		// alpha.setBounds(0, 0, getWidth(), getItemHeight());
		// alpha.setBounds(0, getHeight() - getItemHeight(), getWidth(),
		// getItemHeight());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled() || getViewAdapter() == null
				|| getViewAdapter().getItemsCount() == 1) {
			return false;
		}

		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (getParent() != null) {
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (!isScrollingPerformed) {
				int distance = (int) event.getY() - getHeight() / 2;
				if (distance > 0) {
					distance += getItemHeight() / 2;
				} else {
					distance -= getItemHeight() / 2;
				}
				int items = distance / getItemHeight();
				if (items != 0 && isValidItemIndex(currentItem + items)) {
					notifyClickListenersAboutClick(currentItem + items);
				}
			}
			break;
		}

		return scroller.onTouchEvent(event);
	}

	/**
	 * Scrolls the wheel
	 * 
	 * @param delta
	 *            the scrolling value
	 */
	private void doScroll(int delta) {
		scrollingOffset += delta;

		int itemHeight = getItemHeight();
		int count = scrollingOffset / itemHeight;

		int pos = currentItem - count;
		int itemCount = viewAdapter.getItemsCount();

		int fixPos = scrollingOffset % itemHeight;
		if (Math.abs(fixPos) <= itemHeight / 2) {
			fixPos = 0;
		}
		if (isCyclic && itemCount > 0) {
			if (fixPos > 0) {
				pos--;
				count++;
			} else if (fixPos < 0) {
				pos++;
				count--;
			}
			// fix position by rotating
			while (pos < 0) {
				pos += itemCount;
			}
			pos %= itemCount;
		} else {
			//
			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= itemCount) {
				count = currentItem - itemCount + 1;
				pos = itemCount - 1;
			} else if (pos > 0 && fixPos > 0) {
				pos--;
				count++;
			} else if (pos < itemCount - 1 && fixPos < 0) {
				pos++;
				count--;
			}
		}

		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}

		// update offset
		scrollingOffset = offset - count * itemHeight;
		if (scrollingOffset > getHeight()) {
			scrollingOffset = scrollingOffset % getHeight() + getHeight();
		}
	}

	/**
	 * Scroll the wheel
	 * 
	 * @param itemsToSkip
	 *            items to scroll
	 * @param time
	 *            scrolling duration
	 */
	public void scroll(int itemsToScroll, int time) {
		int distance = itemsToScroll * getItemHeight() - scrollingOffset;
		scroller.scroll(distance, time);
	}

	/**
	 * Calculates range for wheel items
	 * 
	 * @return the items range
	 */
	private ItemsRange getItemsRange() {
		if (getItemHeight() == 0) {
			return null;
		}

		int first = currentItem;
		int count = 1;

		while (count * getItemHeight() < getHeight()) {
			first--;
			count += 2; // top + bottom items
		}

		if (scrollingOffset != 0) {
			if (scrollingOffset > 0) {
				first--;
			}
			count++;

			// process empty items above the first or below the second
			int emptyItems = scrollingOffset / getItemHeight();
			first -= emptyItems;
			count += Math.asin(emptyItems);
		}
		return new ItemsRange(first, count);
	}

	/**
	 * Rebuilds wheel items if necessary. Caches all unused items.
	 * 
	 * @return true if items are rebuilt
	 */
	private boolean rebuildItems() {
		// Log.e(TAG, "rebuildItems被调用了");
		boolean updated = false;
		ItemsRange range = getItemsRange();
		if (itemsLayout != null) {
			int first = recycle.recycleItems(itemsLayout, firstItem, range);
			updated = firstItem != first;
			firstItem = first;
		} else {
			createItemsLayout();
			updated = true;
		}

		if (!updated) {
			updated = firstItem != range.getFirst()
					|| itemsLayout.getChildCount() != range.getCount();
		}

		if (firstItem > range.getFirst() && firstItem <= range.getLast()) {
			for (int i = firstItem - 1; i >= range.getFirst(); i--) {
				if (!addViewItem(i, true)) {
					break;
				}
				firstItem = i;
			}
		} else {
			firstItem = range.getFirst();
		}

		int first = firstItem;
		for (int i = itemsLayout.getChildCount(); i < range.getCount(); i++) {
			if (!addViewItem(firstItem + i, false)
					&& itemsLayout.getChildCount() == 0) {
				first++;
			}
		}
		firstItem = first;

		return updated;
	}

	/**
	 * Updates view. Rebuilds items and label if necessary, recalculate items
	 * sizes.
	 */
	private void updateView() {
		if (rebuildItems()) {
			calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
			layout(getWidth(), getHeight());
		}
	}

	/**
	 * Creates item layouts if necessary
	 */
	private void createItemsLayout() {
		if (itemsLayout == null) {
			itemsLayout = new LinearLayout(getContext());
			// itemsLayout.setBackgroundResource(R.color.black);
			itemsLayout.setOrientation(LinearLayout.VERTICAL);
		}
	}

	/**
	 * Builds view for measuring
	 */
	private void buildViewForMeasuring() { // 初始化一次,起码调用四次以上,因为onMeasure()被调用四次以上
		// Log.e(TAG, "buildViewForMeasuring被调用了");
		// clear all items
		if (itemsLayout != null) {
			recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
		} else {
			createItemsLayout();

			// add views
			int addItems = visibleItems / 2;
			for (int i = currentItem + addItems; i >= currentItem - addItems; i--) {
				if (addViewItem(i, true)) {
					firstItem = i;
				}
			}
		}
	}

	/**
	 * Adds view for item to items layout
	 * 
	 * @param index
	 *            the item index
	 * @param first
	 *            the flag indicates if view should be first
	 * @return true if corresponding item exists and is added
	 */
	private boolean addViewItem(int index, boolean first) {
		// Log.e(TAG, "addViewItem被调用了");
		View view = getItemView(index);

		// Camera mCamera = new Camera();
		// mCamera.rotate(20, 0, 0);
		// Matrix mMatrix = new Matrix();
		// mCamera.getMatrix(mMatrix);
		// view.getMatrix().preConcat(mMatrix);

		// view.setRotationX(60);

		// Log.e(TAG, "是否是Identity : " + view.getMatrix().isIdentity());

		if (view != null) {
			if (first) {
				itemsLayout.addView(view, 0);
			} else {
				itemsLayout.addView(view);
			}

			return true;
		}

		return false;
	}

	/**
	 * Checks whether intem index is valid
	 * 
	 * @param index
	 *            the item index
	 * @return true if item index is not out of bounds or the wheel is cyclic
	 */
	private boolean isValidItemIndex(int index) {
		return viewAdapter != null
				&& viewAdapter.getItemsCount() > 0
				&& (isCyclic || index >= 0
						&& index < viewAdapter.getItemsCount());
	}

	/**
	 * Returns view for specified item
	 * 
	 * @param index
	 *            the item index
	 * @return item view or empty view if index is out of bounds
	 */
	private View getItemView(int index) {
		// Log.e(TAG, "getItemView被调用了");
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return null;
		}
		int count = viewAdapter.getItemsCount();
		if (!isValidItemIndex(index)) {
			return viewAdapter
					.getEmptyItem(recycle.getEmptyItem(), itemsLayout);
		} else {
			while (index < 0) {
				index = count + index;
			}
		}

		index %= count;
		return viewAdapter.getItem(index, recycle.getItem(), itemsLayout);
	}

	/**
	 * Stops scrolling
	 */
	public void stopScrolling() {
		scroller.stopScrolling();
	}

	public String getLabel() {
		return center_label;
	}

	/**
	 * 设置label字符串
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.center_label = label;
	}

	public int getLabel_size() {
		return label_size;
	}

	/**
	 * 设置label的大小(单位是sp),默认是20sp
	 * 
	 * @param label_size
	 */
	public void setLabel_size(int label_size) {
		this.label_size = label_size;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		isEnable = enabled;
		if (enabled) {
			center_label_paint.setColor(Color.WHITE);
			centerDrawable = getContext().getResources().getDrawable(
					wheelForeground);
		} else {
			// wheelview里面的全部view都设成灰色
			// center_label_paint.setColor(context.getResources().getColor(
			// R.color.wheelview_grey_label));
			// centerDrawable = getContext().getResources().getDrawable(
			// R.drawable.shape_wheel_fg_grey);
		}
		// 告诉adapter我是否是enable的
		// WheelViewAdapter adapter = WheelView.this.getViewAdapter();
		// if (adapter instanceof AbstractWheelTextAdapter) {
		// // 如果enable是false,则adapter里面的全部view颜色设成灰色
		// ((AbstractWheelTextAdapter) adapter).setEnable(enabled);
		// }
		invalidateWheel(true);
	}

	public boolean getEnabled() {
		return isEnable;
	}
}
