/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class BindingGetV2Req_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected BindingGetV2Req_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(BindingGetV2Req_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_BindingGetV2Req_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public BindingGetV2Req_t() {
    this(generatedJNI.new_BindingGetV2Req_t(), true);
  }

}
