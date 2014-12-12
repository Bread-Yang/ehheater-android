package com.vanward.ehheater.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.vanward.ehheater.R;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.HttpFriend;
public class PollingService extends Service {

	public static final String ACTION = "com.vanward.service.PollingService";
	
	private Notification mNotification;
	private NotificationManager mManager;
	private HttpFriend mHttpFriend;
	private ArrayList<String> allDeviceDid = new ArrayList<String>();

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
		
		// 获取到所有电热水器的did
		HeaterInfoService heaterService = new HeaterInfoService(getBaseContext());
		HeaterInfoDao heaterDao = new HeaterInfoDao(getBaseContext());
		List<HeaterInfo> allDeviceInfo = heaterDao.getAll();
		for (HeaterInfo item : allDeviceInfo) {
			if (heaterService.getHeaterType(item) == HeaterType.Eh) {
				allDeviceDid.add(item.getDid());
			}
		}
		
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = "New Message";
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	private void showNotification() {
		mNotification.when = System.currentTimeMillis();
		//Navigator to the new activity when click the notification title
		Intent i = new Intent(this, MessageActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.setLatestEventInfo(this,
				getResources().getString(R.string.app_name), "You have new message!" +  new Random().nextInt(), pendingIntent);
		mManager.notify(0, mNotification);
	}

	/**
	 * Polling thread
	 */
	int count = 0;
	class PollingThread extends Thread {
		@Override
		public void run() {
			Log.e("Polling...", "Polling...");
//			System.out.println("Polling...");
			count ++;
			if (count % 5 == 0) {
				showNotification();
				Log.e("New message!", "New message!");
//				System.out.println("New message!");
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("Service:onDestroy", "Service:onDestroy");
//		System.out.println("Service:onDestroy");
	}

}
