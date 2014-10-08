package com.vanward.ehheater.activity.appointment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.provider.MediaStore.Video;

public class AppointmentModel {
	private Context context;
	private static AppointmentModel model;
	private List<Appointment> adapter_date = new ArrayList<Appointment>();

	private AppointmentModel(Context context) {
		this.context = context;
		init();
	}

	public static AppointmentModel getInstance(Context context) {
		if (model == null) {
			model = new AppointmentModel(context);
		}
		return model;
	}

	public List<Appointment> getAdapter_date() {
		return adapter_date;
	}

	public void setAdapter_date(List<Appointment> adapter_date) {
		this.adapter_date = adapter_date;
	}

	private void init() {
		Appointment appointment;
		appointment = new Appointment("09", "10", "星期一");
		adapter_date.add(appointment);
		appointment = new Appointment("10", "10", "星期二");
		adapter_date.add(appointment);
	}

	public void addAppointment() {

	}

	public void getAppointment() {

	}
}
