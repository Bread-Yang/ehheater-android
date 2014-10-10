package com.vanward.ehheater.activity.main.gas;

import net.tsz.afinal.annotation.sqlite.Id;
import android.R.integer;

public class GasCustomSetVo {
	@Id
	int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	int connid;

	public int getConnid() {
		return connid;
	}

	public int getWaterval() {
		return waterval;
	}

	public void setWaterval(int waterval) {
		this.waterval = waterval;
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

	// 温度
	int tempter;
	// 水流量
	int waterval;

	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
