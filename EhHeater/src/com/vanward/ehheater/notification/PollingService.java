package com.vanward.ehheater.notification;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import net.tsz.afinal.http.AjaxCallBack;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.HttpFriend;

public class PollingService extends Service {

	public static final String ACTION = "com.vanward.service.PollingService";

	private Notification mNotification;
	private NotificationManager mManager;
	private HttpFriend mHttpFriend;
	private String uid;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		initNotifiManager();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		new PollingThread().start();
	}

	private void initNotifiManager() {
		mHttpFriend = HttpFriend.create(this);

		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.icon58;
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = "New Message";
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	private void checkAppointment() {
		uid = AccountService.getUserId(getBaseContext());
		if (null != uid && !"".equals(uid)) { // 有uid的时候才请求
			String requestURL = "userinfo/checkAppointmentStatue?uid=" + uid;
			// String requestURL =
			// "userinfo/checkAppointmentStatue?uid=13528297235";
			mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String jsonString) {
							super.onSuccess(jsonString);

							Log.e("请求返回来的数据是 : ", jsonString);

							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("601".equals(responseCode)) {
									JSONArray result = json
											.getJSONArray("result");
									showNotification();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							Log.e("New message!", "New message!");

						}
					});
		}
	}

	private void showNotification() {
		mNotification.when = System.currentTimeMillis();
		// Navigator to the new activity when click the notification title
		Intent i = new Intent(this, MessageActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.setLatestEventInfo(this,
				getResources().getString(R.string.app_name), "需要增加功率!",
				pendingIntent);
		mManager.notify(0, mNotification);
	}

	/**
	 * Polling thread
	 */
	class PollingThread extends Thread {
		@Override
		public void run() {
			Log.e("Polling...", "Polling...");
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {

				@Override
				public void run() {
					checkAppointment();
				}
			});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("Service:onDestroy", "Service:onDestroy");
	}

}
