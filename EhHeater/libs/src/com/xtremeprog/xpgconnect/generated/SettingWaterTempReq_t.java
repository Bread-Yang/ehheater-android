/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class SettingWaterTempReq_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected SettingWaterTempReq_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SettingWaterTempReq_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_SettingWaterTempReq_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setHeader(int value) {
    generatedJNI.SettingWaterTempReq_t_header_set(swigCPtr, this, value);
  }

  public int getHeader() {
    return generatedJNI.SettingWaterTempReq_t_header_get(swigCPtr, this);
  }

  public void setP0_version(short value) {
    generatedJNI.SettingWaterTempReq_t_p0_version_set(swigCPtr, this, value);
  }

  public short getP0_version() {
    return generatedJNI.SettingWaterTempReq_t_p0_version_get(swigCPtr, this);
  }

  public void setResp_address(short value) {
    generatedJNI.SettingWaterTempReq_t_resp_address_set(swigCPtr, this, value);
  }

  public short getResp_address() {
    return generatedJNI.SettingWaterTempReq_t_resp_address_get(swigCPtr, this);
  }

  public void setCommand(short value) {
    generatedJNI.SettingWaterTempReq_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.SettingWaterTempReq_t_command_get(swigCPtr, this);
  }

  public void setSetting_water_temp(short value) {
    generatedJNI.SettingWaterTempReq_t_setting_water_temp_set(swigCPtr, this, value);
  }

  public short getSetting_water_temp() {
    return generatedJNI.SettingWaterTempReq_t_setting_water_temp_get(swigCPtr, this);
  }

  public void setChecksum(int value) {
    generatedJNI.SettingWaterTempReq_t_checksum_set(swigCPtr, this, value);
  }

  public int getChecksum() {
    return generatedJNI.SettingWaterTempReq_t_checksum_get(swigCPtr, this);
  }

  public SettingWaterTempReq_t() {
    this(generatedJNI.new_SettingWaterTempReq_t(), true);
  }

}
