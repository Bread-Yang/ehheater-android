package com.vanward.ehheater.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapThumbUtil {
	
	private static final String TAG = "BitmapThumbUtil";
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
//		L.e(this, "reqWidth : " + reqWidth);
//		L.e(this, "reqHeight : " + reqHeight);
//		L.e(this, "options.outHeight : " + options.outHeight);
//		L.e(this, "options.outWidth : " + options.outWidth);

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		// 另外我们可以设置图片的参数,例如设置为Bitmap.Config.RGB_565来减少内存开销。因为在android文档中描述Bitmap.Config.RGB_565每一个像素存在2个字节中，而默认的Bitmap.Config.ARGB_8888每一个像素则需要4个字节，理论上足足节省了一半空间。
		return BitmapFactory.decodeResource(res, resId, options).copy(Bitmap.Config.ARGB_8888, true);
	}

	public static BitmapFactory.Options getBitmapRawOptions(Resources res,
			int resId) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		return options;
	}
}
