package com.vanward.ehheater.model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class AppointmentVo4Exhibition implements Serializable {

	/**
	 * 
	 "appointmentId":"201410211120", //预约唯一标识 "name":"预约1", //预约名称
	 * "time":"2014-10-21 10:01", //预约时间 "loop":0 //不循环-0，每天-1，每周-2，每月-3
	 */

	int appointmentId;
	long dateTime;

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	String name;
	String did;
	String uid;
	// 分人洗
	String peopleNum;
	public String temper;
	String passcode;
	int isSend;// 1已发送，0未发送
	private int loopflag; // 0:不循环,1:每天都循环,2:指定星期几循环
	private String week; // 在loopflag == 2时生效,如"1100011"表示在星期一,星期二,星期六,星期日循环
	private int workMode; // 壁挂炉工作模式,0:默认 1:外出 2:夜间
	private int isAppointmentOn; // 预约是否打开, 0:关闭,1:打开
	private int isDeviceOn; // 壁挂炉是否打开, 0:关闭,1:打开
	private int deviceType; // 设备类型, 0:热水器, 1:壁挂炉

	public int getLoopflag() {
		return loopflag;
	}

	public void setLoopflag(int loopflag) {
		this.loopflag = loopflag;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public int getWorkMode() {
		return workMode;
	}

	public void setWorkMode(int workMode) {
		this.workMode = workMode;
	}

	public int getIsAppointmentOn() {
		return isAppointmentOn;
	}

	public void setIsAppointmentOn(int isAppointmentOn) {
		this.isAppointmentOn = isAppointmentOn;
	}

	public int getIsDeviceOn() {
		return isDeviceOn;
	}

	public void setIsDeviceOn(int isDeviceOn) {
		this.isDeviceOn = isDeviceOn;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public String getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(String peopleNum) {
		this.peopleNum = peopleNum;
	}

	public String getTemper() {
		return temper;
	}

	public void setTemper(String temper) {
		this.temper = temper;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	String power;

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public int getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}

	public int getIsSend() {
		return isSend;
	}

	public void setIsSend(int isSend) {
		this.isSend = isSend;
	}

}
