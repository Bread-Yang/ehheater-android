package com.vanward.ehheater.activity.info;

import java.io.Serializable;

public class McuVo {

	private Integer errorCode;
	private Integer cumulatUseTime;
	private Integer coOverproofWarning;
	private Integer cumulativeOpenValveTimes;
	private Integer oxygenWarning;
	private Integer cumulativeGas;
	private Integer cumulativeVolume;
	private Integer curcumulativeVolume;
	private Integer curcumulativeGas;
	private Integer curcumulativeOpenValveTimes;

	public Integer getCurcumulativeVolume() {
		return curcumulativeVolume;
	}

	public void setCurcumulativeVolume(Integer curcumulativeVolume) {
		this.curcumulativeVolume = curcumulativeVolume;
	}

	public Integer getCurcumulativeGas() {
		return curcumulativeGas;
	}

	public void setCurcumulativeGas(Integer curcumulativeGas) {
		this.curcumulativeGas = curcumulativeGas;
	}

	public Integer getCurcumulativeOpenValveTimes() {
		return curcumulativeOpenValveTimes;
	}

	public void setCurcumulativeOpenValveTimes(
			Integer curcumulativeOpenValveTimes) {
		this.curcumulativeOpenValveTimes = curcumulativeOpenValveTimes;
	}

	KeyClass keyClass;

	public KeyClass getKeyClass() {
		return keyClass;
	}

	public void setKeyClass(KeyClass keyClass) {
		this.keyClass = keyClass;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public Integer getCumulatUseTime() {
		return cumulatUseTime;
	}

	public void setCumulatUseTime(Integer cumulatUseTime) {
		this.cumulatUseTime = cumulatUseTime;
	}

	public Integer getCoOverproofWarning() {
		return coOverproofWarning;
	}

	public void setCoOverproofWarning(Integer coOverproofWarning) {
		this.coOverproofWarning = coOverproofWarning;
	}

	public Integer getCumulativeOpenValveTimes() {
		return cumulativeOpenValveTimes;
	}

	public void setCumulativeOpenValveTimes(Integer cumulativeOpenValveTimes) {
		this.cumulativeOpenValveTimes = cumulativeOpenValveTimes;
	}

	public Integer getOxygenWarning() {
		return oxygenWarning;
	}

	public void setOxygenWarning(Integer oxygenWarning) {
		this.oxygenWarning = oxygenWarning;
	}

	public Integer getCumulativeGas() {
		return cumulativeGas;
	}

	public void setCumulativeGas(Integer cumulativeGas) {
		this.cumulativeGas = cumulativeGas;
	}

	public Integer getCumulativeVolume() {
		return cumulativeVolume;
	}

	public void setCumulativeVolume(Integer cumulativeVolume) {
		this.cumulativeVolume = cumulativeVolume;
	}

	public static class KeyClass implements Serializable {
		long ts;
		String did;

		public long getTs() {
			return ts;
		}

		public void setTs(long ts) {
			this.ts = ts;
		}

		public String getDid() {
			return did;
		}

		public void setDid(String did) {
			this.did = did;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof KeyClass) {
				KeyClass pk = (KeyClass) o;
				if (this.ts == pk.getTs() && this.did == pk.getDid()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}

}
