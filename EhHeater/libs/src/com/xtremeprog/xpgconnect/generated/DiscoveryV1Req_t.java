/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class DiscoveryV1Req_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected DiscoveryV1Req_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(DiscoveryV1Req_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_DiscoveryV1Req_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setCommand(int value) {
    generatedJNI.DiscoveryV1Req_t_command_set(swigCPtr, this, value);
  }

  public int getCommand() {
    return generatedJNI.DiscoveryV1Req_t_command_get(swigCPtr, this);
  }

  public void setSzIp(String value) {
    generatedJNI.DiscoveryV1Req_t_szIp_set(swigCPtr, this, value);
  }

  public String getSzIp() {
    return generatedJNI.DiscoveryV1Req_t_szIp_get(swigCPtr, this);
  }

  public DiscoveryV1Req_t() {
    this(generatedJNI.new_DiscoveryV1Req_t(), true);
  }

}
