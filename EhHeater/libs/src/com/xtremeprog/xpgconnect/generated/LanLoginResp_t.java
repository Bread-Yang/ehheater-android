/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class LanLoginResp_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected LanLoginResp_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(LanLoginResp_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_LanLoginResp_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setResult(short value) {
    generatedJNI.LanLoginResp_t_result_set(swigCPtr, this, value);
  }

  public short getResult() {
    return generatedJNI.LanLoginResp_t_result_get(swigCPtr, this);
  }

  public LanLoginResp_t() {
    this(generatedJNI.new_LanLoginResp_t(), true);
  }

}
