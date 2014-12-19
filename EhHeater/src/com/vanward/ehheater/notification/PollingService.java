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
import com.vanward.ehheater.activity.main.MainActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;
import com.vanward.ehheater.bean.HeaterInfo;
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
	private short errorCode = 0;
	private boolean gasIsOxygenWarning, gasIsFreezeProofingWarning;
	private HeaterInfo electicHeaterInfo, gasHeaterInfo;

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
									// showNotification("需要增加功率", "需要增加功率");
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
		List<HeaterInfo> allDevices = new HeaterInfoDao(getBaseContext())
				.getAllDeviceOfType(HeaterType.Eh);
		if (allDevices != null) {
			Log.e("electicHeaterDids的大小是 : ", allDevices.size() + "");
		} else {
			Log.e("electicHeaterDids为null : ", "electicHeaterDids为null");
		}
		if (allDevices != null && allDevices.size() > 0) {
			// String requestURL =
			// "GasInfo/getNewestData?did=dVfu4XXcUCbE93Z2mu4PyZ";
			String requestURL = "uGasInfo/getNewestElData?did="
					+ allDevices.get(0).getDid();
			Log.e("checkElecticHeaterInfo的URL", requestURL);
			mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String jsonString) {
							super.onSuccess(jsonString);

							// Log.e("checkElecticHeaterInfo请求返回来的数据是 : ",
							// jsonString);

							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									JSONObject result = json
											.getJSONObject("result");
									int error = result.getInt("error");
									showNotification(0,
											R.string.freezeProof_warn,
											R.string.freezeProof_tips);
								}
							} catch (Exception e) {
								e.printStackTrace();
								Log.e("checkElecticHeaterInfo解析json出错",
										"checkElecticHeaterInfo解析json出错");
								showNotification(0, R.string.freezeProof_warn,
										R.string.freezeProof_tips);
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Log.e("checkElecticHeaterInfo请求故障接口出错",
									"checkElecticHeaterInfo请求故障接口出错");
							showNotification(0, R.string.freezeProof_warn,
									R.string.freezeProof_tips);
						}
					});
		}
	}

	/** 燃热水器故障 */
	private void checkGasHeaterInfo() {
		List<HeaterInfo> allDevices = new HeaterInfoDao(getBaseContext())
				.getAllDeviceOfType(HeaterType.ST);
		if (allDevices != null) {
			Log.e("gasHeaterDids的大小是 : ", allDevices.size() + "");
		} else {
			Log.e("gasHeaterDids为null : ", "gasHeaterDids为null");
		}
		if (allDevices != null && allDevices.size() > 0) {
			// String requestURL =
			// "GasInfo/getNewestData?did=dVfu4XXcUCbE93Z2mu4PyZ";
			String requestURL = "uGasInfo/getNewestData?did="
					+ allDevices.get(0).getDid();
			Log.e("checkGasHeaterInfo的URL", requestURL);
			mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String jsonString) {
							super.onSuccess(jsonString);

							// Log.e("checkGasHeaterInfo请求返回来的数据是 : ",
							// jsonString);

							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									JSONObject result = json
											.getJSONObject("result");

									// 故障
									try {
										errorCode = (short)result.getInt("errorCode");
									} catch (Exception e) {
										errorCode = 0;
									}

									// 防冻警报
									int freezeProofingWarning = result
											.getInt("freezeProofingWarning");
									if (freezeProofingWarning == 1) {
										gasIsFreezeProofingWarning = true;
									} else {
										gasIsFreezeProofingWarning = false;
									}

									// 氧护警报
									int oxygenWarning = result
											.getInt("oxygenWarning");
									if (oxygenWarning == 1) {
										gasIsOxygenWarning = true;
									} else {
										gasIsOxygenWarning = false;
									}

									if (errorCode != 0) {
										showNotification(
												1,
												"机器故障("
														+ Integer
																.toHexString(errorCode)
														+ ")",
												"机器故障("
														+ Integer
																.toHexString(errorCode)
														+ ")");
									}

									if (gasIsFreezeProofingWarning) {
										showNotification(1,
												R.string.freezeProof_warn,
												R.string.freezeProof_tips);
									}

									if (gasIsOxygenWarning) {
										showNotification(1,
												R.string.oxygen_warn,
												R.string.oxygen_tips);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								Log.e("checkGasHeaterInfo解析json出错",
										"checkGasHeaterInfo解析json出错");
							}
						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Log.e("请求故障接口出错", "请求故障接口出错");
							showNotification(1, R.string.oxygen_warn,
									R.string.oxygen_tips);
						}
					});
		}
	}

	private void showNotification(int notifyId, int titleResId, int contentResId) {
		String title = getResources().getString(titleResId);
		String content = getResources().getString(contentResId);
		showNotification(notifyId, title, content);
	}

	private void showNotification(int notifyId, String title, String content) {
		mNotification = new Notification();
		int icon = R.drawable.icon58;
		mNotification.icon = icon;
		mNotification.tickerText = title;
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotification.when = System.currentTimeMillis();
		// Navigator to the new activity when click the notification title
		Intent intent = new Intent();
		if (notifyId == 0) { // 电热水器
			intent.setClass(this, MainActivity.class);
		} else {
			intent.setClass(this, GasMainActivity.class);
			intent.putExtra("gasIsFreezeProofingWarning",
					gasIsFreezeProofingWarning);
			intent.putExtra("gasIsOxygenWarning", gasIsOxygenWarning);
			intent.putExtra("errorCode", errorCode);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification.setLatestEventInfo(this,
				getResources().getString(R.string.app_name), content,
				pendingIntent);
		mManager.notify(notifyId, mNotification);
	}

	/**
	 * Polling thread
	 */
	class PollingThread extends Thread {
		@Override
		public void run() {
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {

				@Override
				public void run() {
					// checkAppointment();
					// checkElecticHeaterInfo();
					checkGasHeaterInfo();
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
