package com.vanward.ehheater.notification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.vanward.ehheater.activity.WelcomeActivity;

public class NotificationActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// If this activity is the root activity of the task, the app is not
		// running
		if (isTaskRoot()) {
			// Start the app before finishing
			Intent startAppIntent = new Intent(getApplicationContext(),
					WelcomeActivity.class);
			startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startAppIntent);
		}

		finish();
	}
}