package com.vanward.ehheater.activity.appointment;

import net.tsz.afinal.annotation.sqlite.Id;

public class AppointMentVo {
	@Id
	String nid = "";
	int hour;
	int min;

	public String getNid() {
		return nid;
	}

	public AppointMentVo() {
	}

	public AppointMentVo(String nid, int hour, int min, int num) {
		super();
		this.nid = nid;
		this.hour = hour;
		this.min = min;
		this.num = num;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	int num;

}
