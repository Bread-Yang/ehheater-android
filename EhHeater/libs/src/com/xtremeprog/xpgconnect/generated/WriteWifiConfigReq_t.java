/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class WriteWifiConfigReq_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected WriteWifiConfigReq_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(WriteWifiConfigReq_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_WriteWifiConfigReq_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setCommand(short value) {
    generatedJNI.WriteWifiConfigReq_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.WriteWifiConfigReq_t_command_get(swigCPtr, this);
  }

  public void setSsid(String value) {
    generatedJNI.WriteWifiConfigReq_t_ssid_set(swigCPtr, this, value);
  }

  public String getSsid() {
    return generatedJNI.WriteWifiConfigReq_t_ssid_get(swigCPtr, this);
  }

  public void setKey(String value) {
    generatedJNI.WriteWifiConfigReq_t_key_set(swigCPtr, this, value);
  }

  public String getKey() {
    return generatedJNI.WriteWifiConfigReq_t_key_get(swigCPtr, this);
  }

  public void setChecksum(short value) {
    generatedJNI.WriteWifiConfigReq_t_checksum_set(swigCPtr, this, value);
  }

  public short getChecksum() {
    return generatedJNI.WriteWifiConfigReq_t_checksum_get(swigCPtr, this);
  }

  public WriteWifiConfigReq_t() {
    this(generatedJNI.new_WriteWifiConfigReq_t(), true);
  }

}
