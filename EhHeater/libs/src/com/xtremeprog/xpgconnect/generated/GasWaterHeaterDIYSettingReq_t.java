/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class GasWaterHeaterDIYSettingReq_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected GasWaterHeaterDIYSettingReq_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(GasWaterHeaterDIYSettingReq_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_GasWaterHeaterDIYSettingReq_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setHeader(int value) {
    generatedJNI.GasWaterHeaterDIYSettingReq_t_header_set(swigCPtr, this, value);
  }

  public int getHeader() {
    return generatedJNI.GasWaterHeaterDIYSettingReq_t_header_get(swigCPtr, this);
  }

  public void setP0_version(short value) {
    generatedJNI.GasWaterHeaterDIYSettingReq_t_p0_version_set(swigCPtr, this, value);
  }

  public short getP0_version() {
    return generatedJNI.GasWaterHeaterDIYSettingReq_t_p0_version_get(swigCPtr, this);
  }

  public void setResp_address(short value) {
    generatedJNI.GasWaterHeaterDIYSettingReq_t_resp_address_set(swigCPtr, this, value);
  }

  public short getResp_address() {
    return generatedJNI.GasWaterHeaterDIYSettingReq_t_resp_address_get(swigCPtr, this);
  }

  public void setCommand(short value) {
    generatedJNI.GasWaterHeaterDIYSettingReq_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.GasWaterHeaterDIYSettingReq_t_command_get(swigCPtr, this);
  }

  public void setDIYCommend(short value) {
    generatedJNI.GasWaterHeaterDIYSettingReq_t_DIYCommend_set(swigCPtr, this, value);
  }

  public short getDIYCommend() {
    return generatedJNI.GasWaterHeaterDIYSettingReq_t_DIYCommend_get(swigCPtr, this);
  }

  public void setDIYWaterTemperature(short value) {
    generatedJNI.GasWaterHeaterDIYSettingReq_t_DIYWaterTemperature_set(swigCPtr, this, value);
  }

  public short getDIYWaterTemperature() {
    return generatedJNI.GasWaterHeaterDIYSettingReq_t_DIYWaterTemperature_get(swigCPtr, this);
  }

  public void setDIYVolume(short value) {
    generatedJNI.GasWaterHeaterDIYSettingReq_t_DIYVolume_set(swigCPtr, this, value);
  }

  public short getDIYVolume() {
    return generatedJNI.GasWaterHeaterDIYSettingReq_t_DIYVolume_get(swigCPtr, this);
  }

  public void setChecksum(int value) {
    generatedJNI.GasWaterHeaterDIYSettingReq_t_checksum_set(swigCPtr, this, value);
  }

  public int getChecksum() {
    return generatedJNI.GasWaterHeaterDIYSettingReq_t_checksum_get(swigCPtr, this);
  }

  public GasWaterHeaterDIYSettingReq_t() {
    this(generatedJNI.new_GasWaterHeaterDIYSettingReq_t(), true);
  }

}
