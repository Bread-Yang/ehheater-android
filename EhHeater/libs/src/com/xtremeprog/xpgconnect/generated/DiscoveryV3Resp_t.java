/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class DiscoveryV3Resp_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected DiscoveryV3Resp_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(DiscoveryV3Resp_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_DiscoveryV3Resp_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setDid(XpgDataField value) {
    generatedJNI.DiscoveryV3Resp_t_did_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getDid() {
    long cPtr = generatedJNI.DiscoveryV3Resp_t_did_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setMac(XpgDataField value) {
    generatedJNI.DiscoveryV3Resp_t_mac_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getMac() {
    long cPtr = generatedJNI.DiscoveryV3Resp_t_mac_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setFwVer(XpgDataField value) {
    generatedJNI.DiscoveryV3Resp_t_fwVer_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getFwVer() {
    long cPtr = generatedJNI.DiscoveryV3Resp_t_fwVer_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setProductKey(XpgDataField value) {
    generatedJNI.DiscoveryV3Resp_t_productKey_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getProductKey() {
    long cPtr = generatedJNI.DiscoveryV3Resp_t_productKey_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public void setSzIp(String value) {
    generatedJNI.DiscoveryV3Resp_t_szIp_set(swigCPtr, this, value);
  }

  public String getSzIp() {
    return generatedJNI.DiscoveryV3Resp_t_szIp_get(swigCPtr, this);
  }

  public DiscoveryV3Resp_t() {
    this(generatedJNI.new_DiscoveryV3Resp_t(), true);
  }

}