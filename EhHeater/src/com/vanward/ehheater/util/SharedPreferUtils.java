package com.vanward.ehheater.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferUtils {

	private final String SHARED_PREFERENCES_KEY = "ehheater";

	SharedPreferences share;

	public enum ShareKey {
		UserId, UserPsw, PendingUserId, PendingUserPsw, CurDeviceMac, PendingSsid;
	}

	public SharedPreferUtils(Context context) {
		share = context.getSharedPreferences(SHARED_PREFERENCES_KEY,
				Context.MODE_PRIVATE);
	}

	public void put(ShareKey key, String value) {
		share.edit().putString(key.name(), value).commit();
	}

	public String get(ShareKey key, String defaultValue) {
		return share.getString(key.name(), defaultValue);
	}

	public void put(ShareKey key, Boolean value) {
		share.edit().putBoolean(key.name(), value).commit();
	}

	public Boolean get(ShareKey key, Boolean defaultValue) {
		return share.getBoolean(key.name(), defaultValue);
	}
	
	/**
	 * 清空
	 * @param context
	 */
	public void clear(){
		share.edit().clear().commit();
	}
}
