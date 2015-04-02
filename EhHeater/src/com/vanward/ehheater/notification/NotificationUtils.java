package com.vanward.ehheater.notification;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.HttpFriend;

public class NotificationUtils {

	private static NotificationUtils instance;

	private Context mContext;

	private Notification mNotification;
	private NotificationManager mManager;
	private HttpFriend mHttpFriend;
	private String uid;

	public static NotificationUtils getInstance(Context context) {
		if (instance == null) {
			synchronized (NotificationUtils.class) {
				if (instance == null) {
					instance = new NotificationUtils(context);
				}
			}
		} else {
			instance.mContext = context;
		}
		return instance;
	}

	private NotificationUtils(Context context) {
		this.mContext = context;

		mHttpFriend = HttpFriend.create(context);

		mManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.icon58;
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = "需要增加功率!";
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	public void checkAppointment(final String conflictAppointmentId) {
		uid = AccountService.getUserId(mContext);
		if (null != uid && !"".equals(uid)) { // 有uid的时候才请求
			String requestURL = "userinfo/checkAppointmentStatue?uid=" + uid;
			// String requestURL =
			// "userinfo/checkAppointmentStatue?uid=13528297235";
			mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String jsonString) {
							super.onSuccess(jsonString);

							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("601".equals(responseCode)) {
									JSONArray result = json
											.getJSONArray("result");
									for (int i = 0; i < result.length(); i++) {
										JSONObject item = result.getJSONObject(i);
										boolean needNotify = item.getBoolean("needNotify");
										String appointmentId = item.getString("appointmentId");
										if (appointmentId.equals(conflictAppointmentId) && needNotify) {
											showNotification();
											break;
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});
		}
	}

	private void showNotification() {
		mNotification.when = System.currentTimeMillis();
		// Navigator to the new activity when click the notification title
		Intent i = new Intent(mContext, MessageActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.setLatestEventInfo(mContext, mContext.getResources()
				.getString(R.string.app_name), "亲，您需要增加功率才能保证您的用水。", pendingIntent);
		mManager.notify(0, mNotification);
	}
}