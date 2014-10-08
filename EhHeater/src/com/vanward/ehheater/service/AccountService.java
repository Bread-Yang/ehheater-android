package com.vanward.ehheater.service;

import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;

import android.content.Context;
import android.text.TextUtils;

public class AccountService {
	
	public static boolean isLogged(Context context) {
		String uid = getUserId(context);
		String psw = getUserPsw(context);
		return !TextUtils.isEmpty(psw) && !TextUtils.isEmpty(uid);
	}
	
	public static String getUserId(Context context) {
		return new SharedPreferUtils(context).get(ShareKey.UserId, "");
	}
	
	public static String getUserPsw(Context context) {
		return new SharedPreferUtils(context).get(ShareKey.UserPsw, "");
	}
	
	public static void setUser(Context context, String uid, String psw) {
		SharedPreferUtils spu = new SharedPreferUtils(context);
		spu.put(ShareKey.UserId, uid);
		spu.put(ShareKey.UserPsw, psw);
	}
	
	public static String getPendingUserId(Context context) {
		return new SharedPreferUtils(context).get(ShareKey.PendingUserId, "");
	}
	
	public static String getPendingUserPsw(Context context) {
		return new SharedPreferUtils(context).get(ShareKey.PendingUserPsw, "");
	}
	
	public static void setPendingUser(Context context, String pendingUid, String pendingPsw) {
		SharedPreferUtils spu = new SharedPreferUtils(context);
		spu.put(ShareKey.PendingUserId, pendingUid);
		spu.put(ShareKey.PendingUserPsw, pendingPsw);
	}

}
