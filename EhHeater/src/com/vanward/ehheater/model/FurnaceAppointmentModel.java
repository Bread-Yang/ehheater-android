package com.vanward.ehheater.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FurnaceAppointmentModel implements Parcelable {

	private int appointmentId;
	private String appointmentName;
	private int dateTime;
	private int did;
	private int userId;
	private int loopflag;  // 0:不循环,1:每天都循环,2:指定星期几循环
	private String week;   // 在loopflag == 2时生效,如"1100011"表示在星期一,星期二,星期六,星期日循环
	private int workMode;  // 壁挂炉工作模式,0:默认  1:外出  2:夜间
	private int isAppointmentOn; // 预约是否打开, 0:关闭,1:打开
	private int isDeviceOn;      // 壁挂炉是否打开, 0:关闭,1:打开
	private int deviceType; 	 // 设备类型, 0:热水器, 1:壁挂炉
	private int temperature;    // 预约温度

	public int getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getAppointmentName() {
		return appointmentName;
	}

	public void setAppointmentName(String appointmentName) {
		this.appointmentName = appointmentName;
	}

	public int getDateTime() {
		return dateTime;
	}

	public void setDateTime(int dateTime) {
		this.dateTime = dateTime;
	}

	public int getDid() {
		return did;
	}

	public void setDid(int did) {
		this.did = did;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

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
	
	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(appointmentName);
		dest.writeInt(appointmentId);
		dest.writeInt(dateTime);
		dest.writeInt(did);
		dest.writeInt(userId);
		dest.writeInt(loopflag);
		dest.writeString(week);
		dest.writeInt(workMode);
		dest.writeInt(isAppointmentOn);
		dest.writeInt(isDeviceOn);
		dest.writeInt(deviceType);
		dest.writeInt(temperature);
	}

	public static final Parcelable.Creator<FurnaceAppointmentModel> CREATOR = new Parcelable.Creator<FurnaceAppointmentModel>() {

		@Override
		public FurnaceAppointmentModel createFromParcel(Parcel source) {
			FurnaceAppointmentModel model = new FurnaceAppointmentModel();
			model.setAppointmentName(source.readString());
			model.setAppointmentId(source.readInt());
			model.setDateTime(source.readInt());
			model.setDid(source.readInt());
			model.setUserId(source.readInt());
			model.setLoopflag(source.readInt());
			model.setWeek(source.readString());
			model.setWorkMode(source.readInt());
			model.setIsAppointmentOn(source.readInt());
			model.setIsDeviceOn(source.readInt());
			model.setDeviceType(source.readInt());
			model.setTemperature(source.readInt());
			return model;
		}

		@Override
		public FurnaceAppointmentModel[] newArray(int size) {
			return new FurnaceAppointmentModel[size];
		}
	};
}
