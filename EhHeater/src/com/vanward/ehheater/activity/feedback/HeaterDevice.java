package com.vanward.ehheater.activity.feedback;

public class HeaterDevice {

	/** 工作模式 */
	private static enum WORK_MODE {
		GENERAL, INTELLIGENCE, MORNING_WASH, NIGHT_POWER, SEPERATE_WASH,
	};

	/** 开关机状态 */
	private boolean isPowerOn;

	/** 当前工作模式 */
	private WORK_MODE workMode;

	/** 是否正在加热 */
	private boolean isHeating;

	/** 是否预约 */
	private boolean isAppointment;

	/** 内胆温度 */
	private int innerTemp;

	/** 设置温度 */
	private int setupTemp;

	/** 设置功率 */
	private int setupPower;

	/** 剩余加热时间 */
	private int residualHeatTime;

	/** 剩余热水量 */
	private int residualHotWater;

	public boolean isPowerOn() {
		return isPowerOn;
	}

	public void setPowerOn(boolean isPowerOn) {
		this.isPowerOn = isPowerOn;
	}

	public WORK_MODE getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WORK_MODE workMode) {
		this.workMode = workMode;
	}

	public boolean isHeating() {
		return isHeating;
	}

	public void setHeating(boolean isHeating) {
		this.isHeating = isHeating;
	}

	public boolean isAppointment() {
		return isAppointment;
	}

	public void setAppointment(boolean isAppointment) {
		this.isAppointment = isAppointment;
	}

	public int getInnerTemp() {
		return innerTemp;
	}

	public void setInnerTemp(int innerTemp) {
		this.innerTemp = innerTemp;
	}

	public int getSetupTemp() {
		return setupTemp;
	}

	public void setSetupTemp(int setupTemp) {
		this.setupTemp = setupTemp;
	}

	public int getSetupPower() {
		return setupPower;
	}

	public void setSetupPower(int setupPower) {
		this.setupPower = setupPower;
	}

	public int getResidualHeatTime() {
		return residualHeatTime;
	}

	public void setResidualHeatTime(int residualHeatTime) {
		this.residualHeatTime = residualHeatTime;
	}

	public int getResidualHotWater() {
		return residualHotWater;
	}

	public void setResidualHotWater(int residualHotWater) {
		this.residualHotWater = residualHotWater;
	}

}
