/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class GasWaterHeaterTargetTemperatureReq_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected GasWaterHeaterTargetTemperatureReq_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(GasWaterHeaterTargetTemperatureReq_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_GasWaterHeaterTargetTemperatureReq_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setHeader(int value) {
    generatedJNI.GasWaterHeaterTargetTemperatureReq_t_header_set(swigCPtr, this, value);
  }

  public int getHeader() {
    return generatedJNI.GasWaterHeaterTargetTemperatureReq_t_header_get(swigCPtr, this);
  }

  public void setP0_version(short value) {
    generatedJNI.GasWaterHeaterTargetTemperatureReq_t_p0_version_set(swigCPtr, this, value);
  }

  public short getP0_version() {
    return generatedJNI.GasWaterHeaterTargetTemperatureReq_t_p0_version_get(swigCPtr, this);
  }

  public void setResp_address(short value) {
    generatedJNI.GasWaterHeaterTargetTemperatureReq_t_resp_address_set(swigCPtr, this, value);
  }

  public short getResp_address() {
    return generatedJNI.GasWaterHeaterTargetTemperatureReq_t_resp_address_get(swigCPtr, this);
  }

  public void setCommand(short value) {
    generatedJNI.GasWaterHeaterTargetTemperatureReq_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.GasWaterHeaterTargetTemperatureReq_t_command_get(swigCPtr, this);
  }

  public void setTargetTemperature(short value) {
    generatedJNI.GasWaterHeaterTargetTemperatureReq_t_targetTemperature_set(swigCPtr, this, value);
  }

  public short getTargetTemperature() {
    return generatedJNI.GasWaterHeaterTargetTemperatureReq_t_targetTemperature_get(swigCPtr, this);
  }

  public void setChecksum(int value) {
    generatedJNI.GasWaterHeaterTargetTemperatureReq_t_checksum_set(swigCPtr, this, value);
  }

  public int getChecksum() {
    return generatedJNI.GasWaterHeaterTargetTemperatureReq_t_checksum_get(swigCPtr, this);
  }

  public GasWaterHeaterTargetTemperatureReq_t() {
    this(generatedJNI.new_GasWaterHeaterTargetTemperatureReq_t(), true);
  }

}
