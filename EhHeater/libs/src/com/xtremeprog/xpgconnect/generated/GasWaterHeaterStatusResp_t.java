/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class GasWaterHeaterStatusResp_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected GasWaterHeaterStatusResp_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(GasWaterHeaterStatusResp_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_GasWaterHeaterStatusResp_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setHeader(int value) {
    generatedJNI.GasWaterHeaterStatusResp_t_header_set(swigCPtr, this, value);
  }

  public int getHeader() {
    return generatedJNI.GasWaterHeaterStatusResp_t_header_get(swigCPtr, this);
  }

  public void setP0_version(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_p0_version_set(swigCPtr, this, value);
  }

  public short getP0_version() {
    return generatedJNI.GasWaterHeaterStatusResp_t_p0_version_get(swigCPtr, this);
  }

  public void setResp_address(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_resp_address_set(swigCPtr, this, value);
  }

  public short getResp_address() {
    return generatedJNI.GasWaterHeaterStatusResp_t_resp_address_get(swigCPtr, this);
  }

  public void setCommand(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.GasWaterHeaterStatusResp_t_command_get(swigCPtr, this);
  }

  public void setOn_off(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_on_off_set(swigCPtr, this, value);
  }

  public short getOn_off() {
    return generatedJNI.GasWaterHeaterStatusResp_t_on_off_get(swigCPtr, this);
  }

  public void setPriority(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_priority_set(swigCPtr, this, value);
  }

  public short getPriority() {
    return generatedJNI.GasWaterHeaterStatusResp_t_priority_get(swigCPtr, this);
  }

  public void setFunction_state(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_function_state_set(swigCPtr, this, value);
  }

  public short getFunction_state() {
    return generatedJNI.GasWaterHeaterStatusResp_t_function_state_get(swigCPtr, this);
  }

  public void setWater_function(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_water_function_set(swigCPtr, this, value);
  }

  public short getWater_function() {
    return generatedJNI.GasWaterHeaterStatusResp_t_water_function_get(swigCPtr, this);
  }

  public void setSetWater_power(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_setWater_power_set(swigCPtr, this, value);
  }

  public short getSetWater_power() {
    return generatedJNI.GasWaterHeaterStatusResp_t_setWater_power_get(swigCPtr, this);
  }

  public void setSetWater_cumulative(int value) {
    generatedJNI.GasWaterHeaterStatusResp_t_setWater_cumulative_set(swigCPtr, this, value);
  }

  public int getSetWater_cumulative() {
    return generatedJNI.GasWaterHeaterStatusResp_t_setWater_cumulative_get(swigCPtr, this);
  }

  public void setCustomFunction(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_CustomFunction_set(swigCPtr, this, value);
  }

  public short getCustomFunction() {
    return generatedJNI.GasWaterHeaterStatusResp_t_CustomFunction_get(swigCPtr, this);
  }

  public void setCustomWaterTemperture(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_CustomWaterTemperture_set(swigCPtr, this, value);
  }

  public short getCustomWaterTemperture() {
    return generatedJNI.GasWaterHeaterStatusResp_t_CustomWaterTemperture_get(swigCPtr, this);
  }

  public void setCustomWaterProportion(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_CustomWaterProportion_set(swigCPtr, this, value);
  }

  public short getCustomWaterProportion() {
    return generatedJNI.GasWaterHeaterStatusResp_t_CustomWaterProportion_get(swigCPtr, this);
  }

  public void setCallingDisp(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_callingDisp_set(swigCPtr, this, value);
  }

  public short getCallingDisp() {
    return generatedJNI.GasWaterHeaterStatusResp_t_callingDisp_get(swigCPtr, this);
  }

  public void setSprinkler(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_sprinkler_set(swigCPtr, this, value);
  }

  public short getSprinkler() {
    return generatedJNI.GasWaterHeaterStatusResp_t_sprinkler_get(swigCPtr, this);
  }

  public void setFlame(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_flame_set(swigCPtr, this, value);
  }

  public short getFlame() {
    return generatedJNI.GasWaterHeaterStatusResp_t_flame_get(swigCPtr, this);
  }

  public void setAirFan(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_airFan_set(swigCPtr, this, value);
  }

  public short getAirFan() {
    return generatedJNI.GasWaterHeaterStatusResp_t_airFan_get(swigCPtr, this);
  }

  public void setFirePower(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_firePower_set(swigCPtr, this, value);
  }

  public short getFirePower() {
    return generatedJNI.GasWaterHeaterStatusResp_t_firePower_get(swigCPtr, this);
  }

  public void setErrorCode(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_errorCode_set(swigCPtr, this, value);
  }

  public short getErrorCode() {
    return generatedJNI.GasWaterHeaterStatusResp_t_errorCode_get(swigCPtr, this);
  }

  public void setOxygenWarning(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_oxygenWarning_set(swigCPtr, this, value);
  }

  public short getOxygenWarning() {
    return generatedJNI.GasWaterHeaterStatusResp_t_oxygenWarning_get(swigCPtr, this);
  }

  public void setCoOverproofWarning(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_coOverproofWarning_set(swigCPtr, this, value);
  }

  public short getCoOverproofWarning() {
    return generatedJNI.GasWaterHeaterStatusResp_t_coOverproofWarning_get(swigCPtr, this);
  }

  public void setTargetTemperature(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_targetTemperature_set(swigCPtr, this, value);
  }

  public short getTargetTemperature() {
    return generatedJNI.GasWaterHeaterStatusResp_t_targetTemperature_get(swigCPtr, this);
  }

  public void setIncomeTemperature(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_incomeTemperature_set(swigCPtr, this, value);
  }

  public short getIncomeTemperature() {
    return generatedJNI.GasWaterHeaterStatusResp_t_incomeTemperature_get(swigCPtr, this);
  }

  public void setOutputTemperature(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_outputTemperature_set(swigCPtr, this, value);
  }

  public short getOutputTemperature() {
    return generatedJNI.GasWaterHeaterStatusResp_t_outputTemperature_get(swigCPtr, this);
  }

  public void setNowVolume(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_nowVolume_set(swigCPtr, this, value);
  }

  public short getNowVolume() {
    return generatedJNI.GasWaterHeaterStatusResp_t_nowVolume_get(swigCPtr, this);
  }

  public void setCumulativeVolume(long value) {
    generatedJNI.GasWaterHeaterStatusResp_t_cumulativeVolume_set(swigCPtr, this, value);
  }

  public long getCumulativeVolume() {
    return generatedJNI.GasWaterHeaterStatusResp_t_cumulativeVolume_get(swigCPtr, this);
  }

  public void setCumulativeGas(int value) {
    generatedJNI.GasWaterHeaterStatusResp_t_cumulativeGas_set(swigCPtr, this, value);
  }

  public int getCumulativeGas() {
    return generatedJNI.GasWaterHeaterStatusResp_t_cumulativeGas_get(swigCPtr, this);
  }

  public void setCumulativeUseTime(long value) {
    generatedJNI.GasWaterHeaterStatusResp_t_cumulativeUseTime_set(swigCPtr, this, value);
  }

  public long getCumulativeUseTime() {
    return generatedJNI.GasWaterHeaterStatusResp_t_cumulativeUseTime_get(swigCPtr, this);
  }

  public void setCumulativeOpenValveTimes(int value) {
    generatedJNI.GasWaterHeaterStatusResp_t_cumulativeOpenValveTimes_set(swigCPtr, this, value);
  }

  public int getCumulativeOpenValveTimes() {
    return generatedJNI.GasWaterHeaterStatusResp_t_cumulativeOpenValveTimes_get(swigCPtr, this);
  }

  public void setNow_efficiency(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_now_efficiency_set(swigCPtr, this, value);
  }

  public short getNow_efficiency() {
    return generatedJNI.GasWaterHeaterStatusResp_t_now_efficiency_get(swigCPtr, this);
  }

  public void setPreheatingModel(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_preheatingModel_set(swigCPtr, this, value);
  }

  public short getPreheatingModel() {
    return generatedJNI.GasWaterHeaterStatusResp_t_preheatingModel_get(swigCPtr, this);
  }

  public void setPresetTemperature(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_presetTemperature_set(swigCPtr, this, value);
  }

  public short getPresetTemperature() {
    return generatedJNI.GasWaterHeaterStatusResp_t_presetTemperature_get(swigCPtr, this);
  }

  public void setPreheatingOneHour(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_preheatingOneHour_set(swigCPtr, this, value);
  }

  public short getPreheatingOneHour() {
    return generatedJNI.GasWaterHeaterStatusResp_t_preheatingOneHour_get(swigCPtr, this);
  }

  public void setPreheatingOneMin(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_preheatingOneMin_set(swigCPtr, this, value);
  }

  public short getPreheatingOneMin() {
    return generatedJNI.GasWaterHeaterStatusResp_t_preheatingOneMin_get(swigCPtr, this);
  }

  public void setPreheatingTwoHour(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_preheatingTwoHour_set(swigCPtr, this, value);
  }

  public short getPreheatingTwoHour() {
    return generatedJNI.GasWaterHeaterStatusResp_t_preheatingTwoHour_get(swigCPtr, this);
  }

  public void setPreheatingTwoMin(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_preheatingTwoMin_set(swigCPtr, this, value);
  }

  public short getPreheatingTwoMin() {
    return generatedJNI.GasWaterHeaterStatusResp_t_preheatingTwoMin_get(swigCPtr, this);
  }

  public void setFreezeProofingWarning(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_freezeProofingWarning_set(swigCPtr, this, value);
  }

  public short getFreezeProofingWarning() {
    return generatedJNI.GasWaterHeaterStatusResp_t_freezeProofingWarning_get(swigCPtr, this);
  }

  public void setMercurycontent(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_mercurycontent_set(swigCPtr, this, value);
  }

  public short getMercurycontent() {
    return generatedJNI.GasWaterHeaterStatusResp_t_mercurycontent_get(swigCPtr, this);
  }

  public void setReturn_water_temperature(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_return_water_temperature_set(swigCPtr, this, value);
  }

  public short getReturn_water_temperature() {
    return generatedJNI.GasWaterHeaterStatusResp_t_return_water_temperature_get(swigCPtr, this);
  }

  public void setReservation_one(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_reservation_one_set(swigCPtr, this, value);
  }

  public short getReservation_one() {
    return generatedJNI.GasWaterHeaterStatusResp_t_reservation_one_get(swigCPtr, this);
  }

  public void setReservation_two(short value) {
    generatedJNI.GasWaterHeaterStatusResp_t_reservation_two_set(swigCPtr, this, value);
  }

  public short getReservation_two() {
    return generatedJNI.GasWaterHeaterStatusResp_t_reservation_two_get(swigCPtr, this);
  }

  public void setChecksum(int value) {
    generatedJNI.GasWaterHeaterStatusResp_t_checksum_set(swigCPtr, this, value);
  }

  public int getChecksum() {
    return generatedJNI.GasWaterHeaterStatusResp_t_checksum_get(swigCPtr, this);
  }

  public GasWaterHeaterStatusResp_t() {
    this(generatedJNI.new_GasWaterHeaterStatusResp_t(), true);
  }

}
