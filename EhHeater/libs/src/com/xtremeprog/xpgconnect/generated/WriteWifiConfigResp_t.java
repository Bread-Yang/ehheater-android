/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class WriteWifiConfigResp_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected WriteWifiConfigResp_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(WriteWifiConfigResp_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_WriteWifiConfigResp_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setCommand(short value) {
    generatedJNI.WriteWifiConfigResp_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.WriteWifiConfigResp_t_command_get(swigCPtr, this);
  }

  public void setResult(short value) {
    generatedJNI.WriteWifiConfigResp_t_result_set(swigCPtr, this, value);
  }

  public short getResult() {
    return generatedJNI.WriteWifiConfigResp_t_result_get(swigCPtr, this);
  }

  public void setChecksum(short value) {
    generatedJNI.WriteWifiConfigResp_t_checksum_set(swigCPtr, this, value);
  }

  public short getChecksum() {
    return generatedJNI.WriteWifiConfigResp_t_checksum_get(swigCPtr, this);
  }

  public WriteWifiConfigResp_t() {
    this(generatedJNI.new_WriteWifiConfigResp_t(), true);
  }

}
