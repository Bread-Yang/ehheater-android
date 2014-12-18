package com.vanward.ehheater.notification;

import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import u.aly.cn;

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
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
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
									showNotification("需要增加功率", "需要增加功率");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		}
	}

	/** 电热水器故障 */
	private void checkElecticHeaterInfo() {
		List<String> ehDids = new HeaterInfoDao(getBaseContext())
				.getAllDeviceDidOfType(HeaterType.Eh);
		if (ehDids != null && ehDids.size() > 0) {
			// String requestURL =
			// "GasInfo/getNewestData?did=dVfu4XXcUCbE93Z2mu4PyZ";
			String requestURL = "uGasInfo/getNewestElData?did=" + ehDids.get(0);
			mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String jsonString) {
							super.onSuccess(jsonString);

							Log.e("checkElecticHeaterInfo请求返回来的数据是 : ",
									jsonString);

							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									JSONObject result = json
											.getJSONObject("result");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});
		}
	}

	/** 燃热水器故障 */
	private void checkGasHeaterInfo() {
		Log.e("checkGasHeaterInfo被调用了", "checkGasHeaterInfo被调用了");
		List<String> ehDids = new HeaterInfoDao(getBaseContext())
				.getAllDeviceDidOfType(HeaterType.ST);
		if (ehDids != null && ehDids.size() > 0) {
			// String requestURL =
			// "GasInfo/getNewestData?did=dVfu4XXcUCbE93Z2mu4PyZ";
			String requestURL = "uGasInfo/getNewestData?did=" + ehDids.get(0);
			Log.e("请求的URL是 : ", "请求的URL是");
			mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String jsonString) {
							super.onSuccess(jsonString);

							Log.e("checkGasHeaterInfo请求返回来的数据是 : ", jsonString);

							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									JSONObject result = json
											.getJSONObject("result");

									// 防冻警报
									int coOverproofWarning = result
											.getInt("coOverproofWarning");
									if (coOverproofWarning == 0) {
										showNotification(
												R.string.freezeProof_warn,
												R.string.freezeProof_tips);
									}

									// 氧护警报
									int oxygenWarning = result
											.getInt("oxygenWarning");
									if (oxygenWarning == 0) {
										showNotification(R.string.oxygen_warn,
												R.string.oxygen_tips);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		}
	}

	private void showNotification(int titleResId, int contentResId) {
		String title = getResources().getString(titleResId);
		String content = getResources().getString(contentResId);
		showNotification(title, content);
	}

	private void showNotification(String title, String content) {
		mNotification = new Notification();
		int icon = R.drawable.icon58;
		mNotification.icon = icon;
		mNotification.tickerText = title;
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotification.when = System.currentTimeMillis();
		// Navigator to the new activity when click the notification title
		Intent i = new Intent(this, MessageActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.setLatestEventInfo(this,
				getResources().getString(R.string.app_name), content,
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
					// checkAppointment();
//					checkElecticHeaterInfo();
//					checkGasHeaterInfo();
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
