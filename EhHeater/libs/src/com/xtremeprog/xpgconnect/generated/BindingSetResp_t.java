/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class BindingSetResp_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected BindingSetResp_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(BindingSetResp_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_BindingSetResp_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setResult(short value) {
    generatedJNI.BindingSetResp_t_result_set(swigCPtr, this, value);
  }

  public short getResult() {
    return generatedJNI.BindingSetResp_t_result_get(swigCPtr, this);
  }

  public BindingSetResp_t() {
    this(generatedJNI.new_BindingSetResp_t(), true);
  }

}
