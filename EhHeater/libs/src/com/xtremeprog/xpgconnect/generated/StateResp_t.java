/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class StateResp_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected StateResp_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(StateResp_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_StateResp_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setHeader(int value) {
    generatedJNI.StateResp_t_header_set(swigCPtr, this, value);
  }

  public int getHeader() {
    return generatedJNI.StateResp_t_header_get(swigCPtr, this);
  }

  public void setP0_version(short value) {
    generatedJNI.StateResp_t_p0_version_set(swigCPtr, this, value);
  }

  public short getP0_version() {
    return generatedJNI.StateResp_t_p0_version_get(swigCPtr, this);
  }

  public void setResp_address(short value) {
    generatedJNI.StateResp_t_resp_address_set(swigCPtr, this, value);
  }

  public short getResp_address() {
    return generatedJNI.StateResp_t_resp_address_get(swigCPtr, this);
  }

  public void setCommand(short value) {
    generatedJNI.StateResp_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.StateResp_t_command_get(swigCPtr, this);
  }

  public void setOn_off(short value) {
    generatedJNI.StateResp_t_on_off_set(swigCPtr, this, value);
  }

  public short getOn_off() {
    return generatedJNI.StateResp_t_on_off_get(swigCPtr, this);
  }

  public void setSystem_running_state(short value) {
    generatedJNI.StateResp_t_system_running_state_set(swigCPtr, this, value);
  }

  public short getSystem_running_state() {
    return generatedJNI.StateResp_t_system_running_state_get(swigCPtr, this);
  }

  public void setFunction_state(short value) {
    generatedJNI.StateResp_t_function_state_set(swigCPtr, this, value);
  }

  public short getFunction_state() {
    return generatedJNI.StateResp_t_function_state_get(swigCPtr, this);
  }

  public void setOrder_state(short value) {
    generatedJNI.StateResp_t_order_state_set(swigCPtr, this, value);
  }

  public short getOrder_state() {
    return generatedJNI.StateResp_t_order_state_get(swigCPtr, this);
  }

  public void setInner1_temp(short value) {
    generatedJNI.StateResp_t_inner1_temp_set(swigCPtr, this, value);
  }

  public short getInner1_temp() {
    return generatedJNI.StateResp_t_inner1_temp_get(swigCPtr, this);
  }

  public void setInner2_temp(short value) {
    generatedJNI.StateResp_t_inner2_temp_set(swigCPtr, this, value);
  }

  public short getInner2_temp() {
    return generatedJNI.StateResp_t_inner2_temp_get(swigCPtr, this);
  }

  public void setInner3_temp(short value) {
    generatedJNI.StateResp_t_inner3_temp_set(swigCPtr, this, value);
  }

  public short getInner3_temp() {
    return generatedJNI.StateResp_t_inner3_temp_get(swigCPtr, this);
  }

  public void setSetting_temp(short value) {
    generatedJNI.StateResp_t_setting_temp_set(swigCPtr, this, value);
  }

  public short getSetting_temp() {
    return generatedJNI.StateResp_t_setting_temp_get(swigCPtr, this);
  }

  public void setSetting_power(short value) {
    generatedJNI.StateResp_t_setting_power_set(swigCPtr, this, value);
  }

  public short getSetting_power() {
    return generatedJNI.StateResp_t_setting_power_get(swigCPtr, this);
  }

  public void setRemaining_heating_time(short value) {
    generatedJNI.StateResp_t_remaining_heating_time_set(swigCPtr, this, value);
  }

  public short getRemaining_heating_time() {
    return generatedJNI.StateResp_t_remaining_heating_time_get(swigCPtr, this);
  }

  public void setRemaining_hot_water(short value) {
    generatedJNI.StateResp_t_remaining_hot_water_set(swigCPtr, this, value);
  }

  public short getRemaining_hot_water() {
    return generatedJNI.StateResp_t_remaining_hot_water_get(swigCPtr, this);
  }

  public void setError(short value) {
    generatedJNI.StateResp_t_error_set(swigCPtr, this, value);
  }

  public short getError() {
    return generatedJNI.StateResp_t_error_get(swigCPtr, this);
  }

  public void setPower_consumption(int value) {
    generatedJNI.StateResp_t_power_consumption_set(swigCPtr, this, value);
  }

  public int getPower_consumption() {
    return generatedJNI.StateResp_t_power_consumption_get(swigCPtr, this);
  }

  public void setHeating_tube_time(int value) {
    generatedJNI.StateResp_t_heating_tube_time_set(swigCPtr, this, value);
  }

  public int getHeating_tube_time() {
    return generatedJNI.StateResp_t_heating_tube_time_get(swigCPtr, this);
  }

  public void setMachine_not_heating_time(int value) {
    generatedJNI.StateResp_t_machine_not_heating_time_set(swigCPtr, this, value);
  }

  public int getMachine_not_heating_time() {
    return generatedJNI.StateResp_t_machine_not_heating_time_get(swigCPtr, this);
  }

  public void setChecksum(int value) {
    generatedJNI.StateResp_t_checksum_set(swigCPtr, this, value);
  }

  public int getChecksum() {
    return generatedJNI.StateResp_t_checksum_get(swigCPtr, this);
  }

  public StateResp_t() {
    this(generatedJNI.new_StateResp_t(), true);
  }

}