/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class DeviceOnlineStateResp_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected DeviceOnlineStateResp_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(DeviceOnlineStateResp_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_DeviceOnlineStateResp_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setMac(XpgDataField value) {
    generatedJNI.DeviceOnlineStateResp_t_mac_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getMac() {
    long cPtr = generatedJNI.DeviceOnlineStateResp_t_mac_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setPasscode(XpgDataField value) {
    generatedJNI.DeviceOnlineStateResp_t_passcode_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getPasscode() {
    long cPtr = generatedJNI.DeviceOnlineStateResp_t_passcode_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setIsOnline(short value) {
    generatedJNI.DeviceOnlineStateResp_t_isOnline_set(swigCPtr, this, value);
  }

  public short getIsOnline() {
    return generatedJNI.DeviceOnlineStateResp_t_isOnline_get(swigCPtr, this);
  }

  public void setPiVersion(long value) {
    generatedJNI.DeviceOnlineStateResp_t_piVersion_set(swigCPtr, this, value);
  }

  public long getPiVersion() {
    return generatedJNI.DeviceOnlineStateResp_t_piVersion_get(swigCPtr, this);
  }

  public void setP0Version(XpgDataField value) {
    generatedJNI.DeviceOnlineStateResp_t_p0Version_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getP0Version() {
    long cPtr = generatedJNI.DeviceOnlineStateResp_t_p0Version_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public DeviceOnlineStateResp_t() {
    this(generatedJNI.new_DeviceOnlineStateResp_t(), true);
  }

}
