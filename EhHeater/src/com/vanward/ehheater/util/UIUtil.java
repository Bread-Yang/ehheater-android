package com.vanward.ehheater.util;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * UI 界面工具类
 * @author ads
 *
 */
public class UIUtil {

	/**
	 * 批量绑定点击事件
	 * @param listener
	 * @param viewGroup
	 */
	public static void setOnClick(OnClickListener listener , View... viewGroup){
		if( listener != null && viewGroup != null && viewGroup.length > 0 ){
			for( View view : viewGroup ){
				view.setOnClickListener(listener);
			}
		}
	}
	
}
