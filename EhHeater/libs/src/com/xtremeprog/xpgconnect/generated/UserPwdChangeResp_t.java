/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class UserPwdChangeResp_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected UserPwdChangeResp_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(UserPwdChangeResp_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_UserPwdChangeResp_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setResult(short value) {
    generatedJNI.UserPwdChangeResp_t_result_set(swigCPtr, this, value);
  }

  public short getResult() {
    return generatedJNI.UserPwdChangeResp_t_result_get(swigCPtr, this);
  }

  public UserPwdChangeResp_t() {
    this(generatedJNI.new_UserPwdChangeResp_t(), true);
  }

}
