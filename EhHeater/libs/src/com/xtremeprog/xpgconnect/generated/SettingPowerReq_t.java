/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class SettingPowerReq_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected SettingPowerReq_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SettingPowerReq_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_SettingPowerReq_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setHeader(int value) {
    generatedJNI.SettingPowerReq_t_header_set(swigCPtr, this, value);
  }

  public int getHeader() {
    return generatedJNI.SettingPowerReq_t_header_get(swigCPtr, this);
  }

  public void setP0_version(short value) {
    generatedJNI.SettingPowerReq_t_p0_version_set(swigCPtr, this, value);
  }

  public short getP0_version() {
    return generatedJNI.SettingPowerReq_t_p0_version_get(swigCPtr, this);
  }

  public void setResp_address(short value) {
    generatedJNI.SettingPowerReq_t_resp_address_set(swigCPtr, this, value);
  }

  public short getResp_address() {
    return generatedJNI.SettingPowerReq_t_resp_address_get(swigCPtr, this);
  }

  public void setCommand(short value) {
    generatedJNI.SettingPowerReq_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.SettingPowerReq_t_command_get(swigCPtr, this);
  }

  public void setSetting_power(short value) {
    generatedJNI.SettingPowerReq_t_setting_power_set(swigCPtr, this, value);
  }

  public short getSetting_power() {
    return generatedJNI.SettingPowerReq_t_setting_power_get(swigCPtr, this);
  }

  public void setChecksum(int value) {
    generatedJNI.SettingPowerReq_t_checksum_set(swigCPtr, this, value);
  }

  public int getChecksum() {
    return generatedJNI.SettingPowerReq_t_checksum_get(swigCPtr, this);
  }

  public SettingPowerReq_t() {
    this(generatedJNI.new_SettingPowerReq_t(), true);
  }

}
