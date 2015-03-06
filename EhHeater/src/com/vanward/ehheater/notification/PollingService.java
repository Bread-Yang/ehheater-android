package com.vanward.ehheater.notification;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.ErrorUtils;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;

public class PollingService extends Service {

	private final String TAG = "PollingService";

	public static final String ACTION = "com.vanward.service.PollingService";

	private Notification mNotification;
	private NotificationManager mManager;
	private HttpFriend mHttpFriend;
	private String uid;
	private String electicMac, gasMac, furnaceMac;

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
		mHttpFriend.showTips = false;

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

							// Log.e(TAG, "checkAppointment请求返回来的数据是 : "
							// + jsonString);

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

	private long magnesiumNotificationTimeStamp;
	private long waterQualityNotificationTimeStamp;
	private long electricErrorNotificationTimeStamp;
	private int electricErrorCode = -1;

	/** 电热水器故障 */
	private void checkElecticHeaterInfo() {
		if (null != uid && !"".equals(uid)) { // 有uid的时候才请求

		}
		// List<HeaterInfo> allDevices = new HeaterInfoDao(getBaseContext())
		// .getAllDeviceOfType(HeaterType.Eh);
		// if (allDevices != null) {
		// // Log.e(TAG, "electicHeaterDids的大小是 : " + allDevices.size() + "");
		// } else {
		// // Log.e(TAG, "electicHeaterDids为null : " +
		// // "electicHeaterDids为null");
		// }
		SharedPreferUtils spu = new SharedPreferUtils(this);
		String did = spu.get(ShareKey.PollingElectricHeaterDid, "");
		electicMac = spu.get(ShareKey.PollingElectricHeaterMac, "");
		Log.e(TAG, "EhHeaterDid : " + did);
		if (!"".equals(did)) {
			// if (allDevices != null && allDevices.size() > 0) {
			// String requestURL =
			// "GasInfo/getNewestData?did=dVfu4XXcUCbE93Z2mu4PyZ";
			// electicMac = allDevices.get(0).getMac();
			// Log.e(TAG, "electic mac是 : " + allDevices.get(0).getMac());
			// Log.e(TAG, "电热的did是 : " + allDevices.get(0).getDid());
			String requestURL = "GasInfo/getNewestElData?did=" + did;
			// Log.e(TAG, "checkElecticHeaterInfo的URL" + Consts.REQUEST_BASE_URL
			// + requestURL);
			mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String jsonString) {
							super.onSuccess(jsonString);

							Log.e(TAG, "checkElecticHeaterInfo请求返回来的数据是 : "
									+ jsonString);

							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									JSONObject result = json
											.getJSONObject("result");
									try {
										short errorCode = (short) result
												.getInt("error");

										if (errorCode == electricErrorCode
												&& !(electricErrorNotificationTimeStamp + 1000 * 60 * 60 <= System
														.currentTimeMillis())) {
											return;
										}
										if (errorCode != 0) {
											electricErrorCode = errorCode;
											electricErrorNotificationTimeStamp = System
													.currentTimeMillis();
											magnesiumNotificationTimeStamp = 0;
											waterQualityNotificationTimeStamp = 0;

											switch (errorCode) {
											case 160: // 防冻
												showNotification(
														0,
														R.string.freezeProof_warn_title,
														R.string.freezeProof_tips);
												break;
											case 226:
												showNotification(0, "干烧故障",
														"干烧故障");
												break;
											case 227:
												showNotification(0, "传感器故障",
														"传感器故障");
												break;
											case 228:
												showNotification(0, "超温故障",
														"超温故障");
												break;
											}
										}

										int heating_tube_time = result
												.getInt("heating_tube_time");
										if (heating_tube_time > 800 * 60
												&& (magnesiumNotificationTimeStamp + 1000 * 60 * 60 <= System
														.currentTimeMillis())) { // 镁棒
											magnesiumNotificationTimeStamp = System
													.currentTimeMillis();
											electricErrorNotificationTimeStamp = 0;
											waterQualityNotificationTimeStamp = 0;

											// 单位是分钟
											showNotification(
													0,
													"镁棒更换",
													"亲，距离上次更换镁棒，您的热水器已经累计加热"
															+ heating_tube_time
															+ "个小时，为保证加热管能长期有效工作，建议您联系客服更换镁棒。");
											return;
										}

										int machine_not_heating_time = result
												.getInt("machine_not_heating_time");
										if (machine_not_heating_time > 9 * 24 * 60
												&& (waterQualityNotificationTimeStamp + 1000 * 60 * 60 <= System
														.currentTimeMillis())) { // 单位是分钟
											waterQualityNotificationTimeStamp = System
													.currentTimeMillis();
											electricErrorNotificationTimeStamp = 0;
											waterQualityNotificationTimeStamp = 0;

											showNotification(0, "水质提醒",
													"亲，我们发现您的热水器长时间没用了，为了您的健康，建议您排空污水后再使用。");
											return;
										}

									} catch (Exception e) {
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								Log.e(TAG, "checkElecticHeaterInfo解析json出错"
										+ "checkElecticHeaterInfo解析json出错");
							}

						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Log.e(TAG, "checkElecticHeaterInfo请求故障接口出错"
									+ "checkElecticHeaterInfo请求故障接口出错");
						}
					});
		}
	}

	private long waterFullNotificationTimestamp;
	private long freezeProofNotificationTimeStamp;
	private long oxygenNotificationTimeStamp;
	private long gasErrorNotificationTimeStamp;
	private int gasErrorCode = -1;

	/** 燃热水器故障 */
	private void checkGasHeaterInfo() {
		// List<HeaterInfo> allDevices = new HeaterInfoDao(getBaseContext())
		// .getAllDeviceOfType(HeaterType.ST);
		// if (allDevices != null) {
		// // Log.e(TAG, "gasHeaterDids的大小是 : " + allDevices.size() + "");
		// } else {
		// // Log.e(TAG, "gasHeaterDids为null : " + "gasHeaterDids为null");
		// }
		SharedPreferUtils spu = new SharedPreferUtils(this);
		String did = spu.get(ShareKey.PollingGasHeaterDid, "");
		gasMac = spu.get(ShareKey.PollingGasHeaterMac, "");
		Log.e(TAG, "GasHeaterDid : " + did);
		if (!"".equals(did)) {
			// if (allDevices != null && allDevices.size() > 0) {
			// String requestURL =
			// "GasInfo/getNewestData?did=dVfu4XXcUCbE93Z2mu4PyZ";
			// gasMac = allDevices.get(0).getMac();
			// Log.e(TAG, "gasMac是 : " + gasMac);
			// Log.e(TAG, "燃热的did是 : " + allDevices.get(0).getDid());

			String requestURL = "GasInfo/getNewestData?did=" + did;
			// Log.e(TAG, "checkGasHeaterInfo的URL" + requestURL);

			mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
					null, new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String jsonString) {
							super.onSuccess(jsonString);

							Log.e(TAG, "checkGasHeaterInfo请求返回来的数据是 : "
									+ jsonString);

							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									JSONObject result = json
											.getJSONObject("result");

									// 防冻警报
									int freezeProofingWarning = result
											.getInt("freezeProofingWarning");

									if (freezeProofingWarning == 1
											&& (freezeProofNotificationTimeStamp + 1000 * 60 * 60 <= System
													.currentTimeMillis())) {
										freezeProofNotificationTimeStamp = System
												.currentTimeMillis();
										waterFullNotificationTimestamp = 0;
										oxygenNotificationTimeStamp = 0;
										gasErrorNotificationTimeStamp = 0;

										showNotification(
												1,
												R.string.freezeProof_warn_title,
												R.string.freezeProof_tips);

										return;
									}

									// 氧护警报
									int oxygenWarning = result
											.getInt("oxygenWarning");

									if (oxygenWarning == 1
											&& (oxygenNotificationTimeStamp + 1000 * 60 * 60 <= System
													.currentTimeMillis())) {
										oxygenNotificationTimeStamp = System
												.currentTimeMillis();
										freezeProofNotificationTimeStamp = 0;
										waterFullNotificationTimestamp = 0;
										gasErrorNotificationTimeStamp = 0;

										showNotification(1,
												R.string.oxygen_warn,
												R.string.oxygen_tips);

										return;
									}

									// 机器警报
									short errorCode = (short) result
											.getInt("errorCode");

									if (errorCode == gasErrorCode
											&& !(gasErrorNotificationTimeStamp + 1000 * 60 * 60 <= System
													.currentTimeMillis())) {
										return;
									}

									if (errorCode != 0) {
										gasErrorCode = errorCode;
										gasErrorNotificationTimeStamp = System
												.currentTimeMillis();
										freezeProofNotificationTimeStamp = 0;
										waterFullNotificationTimestamp = 0;
										oxygenNotificationTimeStamp = 0;

										showNotification(
												1,
												"机器故障("
														+ Integer
																.toHexString(gasErrorCode)
														+ ")",
												"机器故障("
														+ Integer
																.toHexString(gasErrorCode)
														+ ")");
										return;
									}

									// 满水警报
									int water_function = result
											.getInt("water_function");

									if (water_function == 2
											&& (waterFullNotificationTimestamp + 1000 * 60 * 60 <= System
													.currentTimeMillis())) {
										waterFullNotificationTimestamp = System
												.currentTimeMillis();
										freezeProofNotificationTimeStamp = 0;
										oxygenNotificationTimeStamp = 0;
										gasErrorNotificationTimeStamp = 0;

										showNotification(1, R.string.tips,
												R.string.full_water);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								Log.e(TAG, "checkGasHeaterInfo解析json出错"
										+ "checkGasHeaterInfo解析json出错");
							}
						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Log.e(TAG, "checkGasHeaterInfo请求故障接口出错"
									+ "checkGasHeaterInfo请求故障接口出错");
							// showNotification(1, R.string.oxygen_warn,
							// R.string.oxygen_tips);
						}
					});
		}
	}

	private long furnaceErrorNotificationTimeStamp;
	private int furnaceErrorCode = -1;

	/** 壁挂炉故障 */
	private void checkFurnaceInfo() {
		// List<HeaterInfo> allDevices = new HeaterInfoDao(getBaseContext())
		// .getAllDeviceOfType(HeaterType.ST);
		// if (allDevices != null) {
		// // Log.e(TAG, "gasHeaterDids的大小是 : " + allDevices.size() + "");
		// } else {
		// // Log.e(TAG, "gasHeaterDids为null : " + "gasHeaterDids为null");
		// }
		SharedPreferUtils spu = new SharedPreferUtils(this);
		String did = spu.get(ShareKey.PollingFurnaceDid, "");
		furnaceMac = spu.get(ShareKey.PollingFurnaceMac, "");
		Log.e(TAG, "FurnaceDid : " + did);
		if (!"".equals(did)) {
			// if (allDevices != null && allDevices.size() > 0) {
			// String requestURL =
			// "GasInfo/getNewestData?did=dVfu4XXcUCbE93Z2mu4PyZ";
			// gasMac = allDevices.get(0).getMac();
			// Log.e(TAG, "gasMac是 : " + gasMac);
			// Log.e(TAG, "燃热的did是 : " + allDevices.get(0).getDid());

			String requestURL = Consts.REQUEST_BASE_URL
					+ "getNewestFurnaceData?did=" + did;
			Log.e(TAG, "checkFurnaceInfo的URL" + requestURL);

			mHttpFriend.toUrl(requestURL).executeGet(null,
					new AjaxCallBack<String>() {
						@Override
						public void onSuccess(String jsonString) {
							super.onSuccess(jsonString);

							Log.e(TAG, "checkFurnaceInfo请求返回来的数据是 : "
									+ jsonString);

							try {
								JSONObject json = new JSONObject(jsonString);
								String responseCode = json
										.getString("responseCode");
								if ("200".equals(responseCode)) {
									JSONObject result = json
											.getJSONObject("result");

									int error = result.getInt("error");

									String[] errorStrings = getResources()
											.getStringArray(
													R.array.furnace_error_arrary);

									if (furnaceErrorCode == error
											&& !(furnaceErrorNotificationTimeStamp + 1000 * 60 * 60 <= System
													.currentTimeMillis())) {
										return;
									}
									if (error != 0) {

										furnaceErrorNotificationTimeStamp = System
												.currentTimeMillis();
										furnaceErrorCode = error;

										switch (error) {
										case 10:
											showNotification(2, "壁挂炉故障",
													errorStrings[0]);
											break;
										case 1:
											showNotification(2, "壁挂炉故障",
													errorStrings[1]);
											break;
										case 2:
											showNotification(2, "壁挂炉故障",
													errorStrings[2]);
											break;
										case 3:
											showNotification(2, "壁挂炉故障",
													errorStrings[3]);
											break;
										case 4:
											showNotification(2, "壁挂炉故障",
													errorStrings[4]);
											break;
										case 6:
											showNotification(2, "壁挂炉故障",
													errorStrings[5]);
											break;
										case 7:
											showNotification(2, "壁挂炉故障",
													errorStrings[6]);
											break;
										case 8:
											showNotification(2, "壁挂炉故障",
													errorStrings[7]);
											break;
										case 9:
											showNotification(2, "壁挂炉故障",
													errorStrings[8]);
											break;
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								Log.e(TAG, "checkFurnaceInfo解析json出错"
										+ "checkFurnaceInfo解析json出错");
							}
						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Log.e(TAG, "checkFurnaceInfo请求故障接口出错"
									+ "checkFurnaceInfo请求故障接口出错");
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
		intent.setClass(this, ErrorUtils.class);
		if (notifyId == 0) { // 电热水器
			intent.putExtra("mac", electicMac);
			intent.putExtra("device_type", HeaterType.ELECTRIC_HEATER);
		} else if (notifyId == 1) { // 燃热水器
			intent.putExtra("mac", gasMac);
			intent.putExtra("device_type", HeaterType.GAS_HEATER);
		} else if (notifyId == 2) { // 壁挂炉
			intent.putExtra("mac", furnaceMac);
			intent.putExtra("device_type", HeaterType.FURNACE);
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
					Log.e(TAG, "PollingThread执行了");
					// checkAppointment();
					checkElecticHeaterInfo();
					checkGasHeaterInfo();
					checkFurnaceInfo();
				}
			});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}