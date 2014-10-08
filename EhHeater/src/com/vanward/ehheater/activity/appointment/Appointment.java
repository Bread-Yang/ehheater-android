package com.vanward.ehheater.activity.appointment;

public class Appointment {

	String hour;
	String minute;
	// 星期
	String days;

	public Appointment(String hour, String minute, String days) {
		super();
		this.hour = hour;
		this.minute = minute;
		this.days = days;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}
}
