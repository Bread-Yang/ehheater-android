package com.vanward.ehheater.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferUtils {

	private static final String SHARED_PREFERENCES_KEY = "ehheater";

	SharedPreferences share;

	public enum ShareKey {
		UserNickname, UserId, UserPsw, PendingUserId, PendingUserPsw, PollingElectricHeaterDid, PollingElectricHeaterMac, PollingGasHeaterDid, PollingGasHeaterMac, PollingFurnaceDid, PollingFurnaceMac, CurDeviceMac, CurDeviceDid, CurDeviceAddress, PendingSsid;
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
	 * 
	 * @param context
	 */
	public void clear() {
		put(ShareKey.UserNickname, "");
		put(ShareKey.UserPsw, "");
		put(ShareKey.PendingUserId, "");
		put(ShareKey.PendingUserPsw, "");
		// share.edit().clear().commit();
	}

	public static void saveUsername(Context context, String username) {

		SharedPreferences share = context.getSharedPreferences(
				SHARED_PREFERENCES_KEY + "_2", Context.MODE_PRIVATE);
		share.edit().putString("username", username);
		share.edit().commit();
	}

	public static String getUsername(Context context) {
		SharedPreferences share = context.getSharedPreferences(
				SHARED_PREFERENCES_KEY + "_2", Context.MODE_PRIVATE);
		return share.getString("username", "");
	}

}
