package com.vanward.ehheater.util;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapDecodeCacheUtil {
	
	private static Map<Integer, Bitmap> cacheMap = new HashMap<Integer, Bitmap>();
	
	
	/**
	 * 缓存已decode过的图片,  防止outOfMemory错误
	 * 
	 * @param res
	 * @param resId
	 * @return
	 */
	public static Bitmap getBitmapFromRes(Resources res, int resId) {
		
		if (cacheMap.get(resId) == null) {
			Bitmap bm = BitmapFactory.decodeResource(res, resId);
			cacheMap.put(resId, bm);
		}
		
		return cacheMap.get(resId);
	}

}
