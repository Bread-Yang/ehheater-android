package com.vanward.ehheater.activity.appointment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vanward.ehheater.util.db.DBService;

import android.content.Context;
import android.provider.MediaStore.Video;

public class AppointmentModel {
	private Context context;
	private static AppointmentModel model;
	private List<Appointment> adapter_date = new ArrayList<Appointment>();
	int[] days;

	public int[] getDays() {
		return days;
	}

	public void setDays(int[] days) {
		this.days = days;
	}

	private AppointmentModel(Context context) {
		this.context = context;
	}

	public static AppointmentModel getInstance(Context context) {
		if (model == null) {
			model = new AppointmentModel(context);
		}
		return model;
	}

	public List<Appointment> getAdapter_date() {
		adapter_date = DBService.getInstance(context)
				.findAll(Appointment.class);
		System.out.println("size:" + adapter_date.size());
		return adapter_date;
	}

	public void saveAppointment(Appointment appointment) {
		DBService.getInstance(context).save(appointment);
	}

}
