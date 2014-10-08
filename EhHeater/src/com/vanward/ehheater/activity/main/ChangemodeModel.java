package com.vanward.ehheater.activity.main;

import android.content.Context;

public class ChangemodeModel {

	private Context context;
	private static ChangemodeModel model;

	private ChangemodeModel(Context context) {
		this.context = context;
	}

	public static ChangemodeModel getInstance(Context context) {
		if (model == null) {
			model = new ChangemodeModel(context);
		}
		return model;
	}

	public void changeToMorningMode() {

	}
}
