/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class UserPwdChangeReq_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected UserPwdChangeReq_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(UserPwdChangeReq_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_UserPwdChangeReq_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setUid(XpgDataField value) {
    generatedJNI.UserPwdChangeReq_t_uid_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getUid() {
    long cPtr = generatedJNI.UserPwdChangeReq_t_uid_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setPwd(XpgDataField value) {
    generatedJNI.UserPwdChangeReq_t_pwd_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getPwd() {
    long cPtr = generatedJNI.UserPwdChangeReq_t_pwd_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public UserPwdChangeReq_t() {
    this(generatedJNI.new_UserPwdChangeReq_t(), true);
  }

}
