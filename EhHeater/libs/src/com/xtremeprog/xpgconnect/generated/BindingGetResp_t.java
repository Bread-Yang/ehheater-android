/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class BindingGetResp_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected BindingGetResp_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(BindingGetResp_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_BindingGetResp_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setDid(XpgDataField value) {
    generatedJNI.BindingGetResp_t_did_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getDid() {
    long cPtr = generatedJNI.BindingGetResp_t_did_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setMac(XpgDataField value) {
    generatedJNI.BindingGetResp_t_mac_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getMac() {
    long cPtr = generatedJNI.BindingGetResp_t_mac_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setPasscode(XpgDataField value) {
    generatedJNI.BindingGetResp_t_passcode_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getPasscode() {
    long cPtr = generatedJNI.BindingGetResp_t_passcode_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setIsOnline(short value) {
    generatedJNI.BindingGetResp_t_isOnline_set(swigCPtr, this, value);
  }

  public short getIsOnline() {
    return generatedJNI.BindingGetResp_t_isOnline_get(swigCPtr, this);
  }

  public void setPiVersion(long value) {
    generatedJNI.BindingGetResp_t_piVersion_set(swigCPtr, this, value);
  }

  public long getPiVersion() {
    return generatedJNI.BindingGetResp_t_piVersion_get(swigCPtr, this);
  }

  public void setP0Version(XpgDataField value) {
    generatedJNI.BindingGetResp_t_p0Version_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getP0Version() {
    long cPtr = generatedJNI.BindingGetResp_t_p0Version_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public BindingGetResp_t() {
    this(generatedJNI.new_BindingGetResp_t(), true);
  }

}