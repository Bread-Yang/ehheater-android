/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class UserRegisterReq_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected UserRegisterReq_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(UserRegisterReq_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_UserRegisterReq_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setUid(XpgDataField value) {
    generatedJNI.UserRegisterReq_t_uid_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getUid() {
    long cPtr = generatedJNI.UserRegisterReq_t_uid_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setPwd(XpgDataField value) {
    generatedJNI.UserRegisterReq_t_pwd_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getPwd() {
    long cPtr = generatedJNI.UserRegisterReq_t_pwd_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public UserRegisterReq_t() {
    this(generatedJNI.new_UserRegisterReq_t(), true);
  }

}
