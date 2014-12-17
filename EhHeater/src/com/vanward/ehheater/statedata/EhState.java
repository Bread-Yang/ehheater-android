package com.vanward.ehheater.statedata;

import java.util.Arrays;

public class EhState {

	byte[] header;
	byte p0Version;
	byte respAddress;
	byte commandCode;
	boolean poweredOn;
	byte systemRunningState;
	byte functionState;
	byte appointmentState;
	byte innerTemp1;
	byte innerTemp2;
	byte innerTemp3;
	byte targetTemperature;
	byte power;
	byte remainingHeatingTime;
	byte remainingHotWaterAmount;
	short errorCode;
	short electricityConsumption;
	short heating_tube_time;
	short machine_not_heating_time;

	public short getMachine_not_heating_time() {
		return machine_not_heating_time;
	}

	public void setMachine_not_heating_time(short machine_not_heating_time) {
		this.machine_not_heating_time = machine_not_heating_time;
	}

	short checkSum;

	public EhState(byte[] data) {
		try {
			setHeader(new byte[] { data[0], data[1] });
			setP0Version(data[2]);
			setRespAddress(data[3]);
			setCommandCode(data[4]);
			setPoweredOn(data[5] == 0 ? false : true);
			setSystemRunningState(data[6]);
			setFunctionState(data[7]);
			setAppointmentState(data[8]);
			setInnerTemp1(data[9]);
			setInnerTemp2(data[10]);
			setInnerTemp3(data[11]);
			setTargetTemperature(data[12]);
			setPower(data[13]);
			setRemainingHeatingTime(data[14]);
			setRemainingHotWaterAmount(data[15]);
			setErrorCode(data[16]);
			int highElec = data[17] * 256;
			int lowElec = data[18];
			setElectricityConsumption((short) (highElec + lowElec));

			int highheating_tube_time = data[19] * 256;
			int low_tube_time = data[20];
			setHeating_tube_time((short) (highheating_tube_time + low_tube_time));

			int highmachine_not_heating_time = data[21] * 256;
			int lowmachine_not_heating_time = data[22];
			setMachine_not_heating_time((short) (highmachine_not_heating_time + lowmachine_not_heating_time));
			
			setCheckSum((short) 0);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public short getHeating_tube_time() {
		return heating_tube_time;
	}

	public void setHeating_tube_time(short heating_tube_time) {
		this.heating_tube_time = heating_tube_time;
	}

	@Override
	public String toString() {
		return "EhState [header=" + Arrays.toString(header) + ", p0Version="
				+ p0Version + ", respAddress=" + respAddress + ", commandCode="
				+ commandCode + ", poweredOn=" + poweredOn
				+ ", systemRunningState=" + systemRunningState
				+ ", functionState=" + functionState + ", appointmentState="
				+ appointmentState + ", innerTemp1=" + innerTemp1
				+ ", innerTemp2=" + innerTemp2 + ", innerTemp3=" + innerTemp3
				+ ", targetTemperature=" + targetTemperature + ", power="
				+ power + ", remainingHeatingTime=" + remainingHeatingTime
				+ ", remainingHotWaterAmount=" + remainingHotWaterAmount
				+ ", errorCode=" + errorCode + ", electricityConsumption="
				+ electricityConsumption + ", checkSum=" + checkSum + "]";
	}

	public byte[] getHeader() {
		return header;
	}

	public void setHeader(byte[] header) {
		this.header = header;
	}

	public byte getP0Version() {
		return p0Version;
	}

	public void setP0Version(byte p0Version) {
		this.p0Version = p0Version;
	}

	public byte getRespAddress() {
		return respAddress;
	}

	public void setRespAddress(byte respAddress) {
		this.respAddress = respAddress;
	}

	public byte getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(byte commandCode) {
		this.commandCode = commandCode;
	}

	public boolean isPoweredOn() {
		return poweredOn;
	}

	public void setPoweredOn(boolean poweredOn) {
		this.poweredOn = poweredOn;
	}

	public byte getSystemRunningState() {
		return systemRunningState;
	}

	public void setSystemRunningState(byte systemRunningState) {
		this.systemRunningState = systemRunningState;
	}

	public byte getFunctionState() {
		return functionState;
	}

	public void setFunctionState(byte functionState) {
		this.functionState = functionState;
	}

	public byte getAppointmentState() {
		return appointmentState;
	}

	public void setAppointmentState(byte appointmentState) {
		this.appointmentState = appointmentState;
	}

	public byte getInnerTemp1() {
		return innerTemp1;
	}

	public void setInnerTemp1(byte innerTemp1) {
		this.innerTemp1 = innerTemp1;
	}

	public byte getInnerTemp2() {
		return innerTemp2;
	}

	public void setInnerTemp2(byte innerTemp2) {
		this.innerTemp2 = innerTemp2;
	}

	public byte getInnerTemp3() {
		return innerTemp3;
	}

	public void setInnerTemp3(byte innerTemp3) {
		this.innerTemp3 = innerTemp3;
	}

	public byte getTargetTemperature() {
		return targetTemperature;
	}

	public void setTargetTemperature(byte targetTemperature) {
		this.targetTemperature = targetTemperature;
	}

	public byte getPower() {
		return power;
	}

	public void setPower(byte power) {
		this.power = power;
	}

	public byte getRemainingHeatingTime() {
		return remainingHeatingTime;
	}

	public void setRemainingHeatingTime(byte remainingHeatingTime) {
		this.remainingHeatingTime = remainingHeatingTime;
	}

	public byte getRemainingHotWaterAmount() {
		return remainingHotWaterAmount;
	}

	public void setRemainingHotWaterAmount(byte remainingHotWaterAmount) {
		this.remainingHotWaterAmount = remainingHotWaterAmount;
	}



	public void setErrorCode(byte errorCode) {
		this.errorCode = errorCode;
	}

	public short getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(short errorCode) {
		this.errorCode = errorCode;
	}

	public short getElectricityConsumption() {
		return electricityConsumption;
	}

	public void setElectricityConsumption(short electricityConsumption) {
		this.electricityConsumption = electricityConsumption;
	}

	public short getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(short checkSum) {
		this.checkSum = checkSum;
	}

}
