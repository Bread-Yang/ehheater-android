/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class GasWaterHeaterOnOffReq_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected GasWaterHeaterOnOffReq_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(GasWaterHeaterOnOffReq_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_GasWaterHeaterOnOffReq_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setHeader(int value) {
    generatedJNI.GasWaterHeaterOnOffReq_t_header_set(swigCPtr, this, value);
  }

  public int getHeader() {
    return generatedJNI.GasWaterHeaterOnOffReq_t_header_get(swigCPtr, this);
  }

  public void setP0_version(short value) {
    generatedJNI.GasWaterHeaterOnOffReq_t_p0_version_set(swigCPtr, this, value);
  }

  public short getP0_version() {
    return generatedJNI.GasWaterHeaterOnOffReq_t_p0_version_get(swigCPtr, this);
  }

  public void setResp_address(short value) {
    generatedJNI.GasWaterHeaterOnOffReq_t_resp_address_set(swigCPtr, this, value);
  }

  public short getResp_address() {
    return generatedJNI.GasWaterHeaterOnOffReq_t_resp_address_get(swigCPtr, this);
  }

  public void setCommand(short value) {
    generatedJNI.GasWaterHeaterOnOffReq_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.GasWaterHeaterOnOffReq_t_command_get(swigCPtr, this);
  }

  public void setOn_off(short value) {
    generatedJNI.GasWaterHeaterOnOffReq_t_on_off_set(swigCPtr, this, value);
  }

  public short getOn_off() {
    return generatedJNI.GasWaterHeaterOnOffReq_t_on_off_get(swigCPtr, this);
  }

  public void setChecksum(int value) {
    generatedJNI.GasWaterHeaterOnOffReq_t_checksum_set(swigCPtr, this, value);
  }

  public int getChecksum() {
    return generatedJNI.GasWaterHeaterOnOffReq_t_checksum_get(swigCPtr, this);
  }

  public GasWaterHeaterOnOffReq_t() {
    this(generatedJNI.new_GasWaterHeaterOnOffReq_t(), true);
  }

}
