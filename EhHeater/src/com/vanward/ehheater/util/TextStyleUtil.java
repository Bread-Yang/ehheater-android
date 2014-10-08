package com.vanward.ehheater.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextStyleUtil {


	/**
	 * Set contains text color
	 * @param textview
	 * @param color
	 * @param string
	 */
	public static void setColorStringInTextView(TextView textview, int color,
			String string[]) {
		String text = textview.getText().toString();
		SpannableStringBuilder builder = new SpannableStringBuilder(text);

		for (int i = 0; i < string.length; i++) {
			if (text.contains(string[i])) {
				String firstChar = (String) string[i].subSequence(0, 1);
				int position = text.indexOf(firstChar);
				builder.setSpan(new ForegroundColorSpan(color), position,
						position + string[i].length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		textview.setText(builder);
	}
}
