package com.vanward.ehheater.activity.main;

import net.tsz.afinal.annotation.sqlite.Id;
import android.R.integer;

public class CustomSetVo {

	int connid;

	public int getConnid() {
		return connid;
	}

	public void setConnid(int connid) {
		this.connid = connid;
	}

	public int getTempter() {
		return tempter;
	}

	public void setTempter(int tempter) {
		this.tempter = tempter;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	int tempter;
	int power;
	int peoplenum;

	public int getPeoplenum() {
		return peoplenum;
	}

	public void setPeoplenum(int peoplenum) {
		this.peoplenum = peoplenum;
	}

	@Id
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	boolean isSet;

	public boolean isSet() {
		return isSet;
	}

	public void setSet(boolean isSet) {
		this.isSet = isSet;
	}

}
