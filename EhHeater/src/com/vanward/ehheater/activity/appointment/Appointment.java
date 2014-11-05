package com.vanward.ehheater.activity.appointment;

import net.tsz.afinal.annotation.sqlite.Id;

public class Appointment {

	@Id
	int id;
	int hour;
	int minute;
	int looper;
	int[] days;
	int power;
	int peopleNum;
	String dates;

	public String getDates() {
		return dates;
	}

	public void setDates(String dates) {
		this.dates = dates;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Appointment() {

	}

	public Appointment(int hour, int minute, int looper, String datestring,
			int power, int peopleNum, int temper) {
		super();
		this.hour = hour;
		this.minute = minute;
		this.looper = looper;
		this.dates = datestring;
		this.power = power;
		this.peopleNum = peopleNum;
		this.temper = temper;
	}

	int temper;

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getLooper() {
		return looper;
	}

	public void setLooper(int looper) {
		this.looper = looper;
	}

	public int[] getDays() {
		return days;
	}

	public void setDays(int[] days) {
		this.days = days;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(int peopleNum) {
		this.peopleNum = peopleNum;
	}

	public int getTemper() {
		return temper;
	}

	public void setTemper(int temper) {
		this.temper = temper;
	}

}
