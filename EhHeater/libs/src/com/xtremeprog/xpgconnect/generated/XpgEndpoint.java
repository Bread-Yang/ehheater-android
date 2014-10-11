/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class XpgEndpoint {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected XpgEndpoint(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(XpgEndpoint obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_XpgEndpoint(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setId(int value) {
    generatedJNI.XpgEndpoint_id_set(swigCPtr, this, value);
  }

  public int getId() {
    return generatedJNI.XpgEndpoint_id_get(swigCPtr, this);
  }

  public void setAddr(String value) {
    generatedJNI.XpgEndpoint_addr_set(swigCPtr, this, value);
  }

  public String getAddr() {
    return generatedJNI.XpgEndpoint_addr_get(swigCPtr, this);
  }

  public void setPort(int value) {
    generatedJNI.XpgEndpoint_port_set(swigCPtr, this, value);
  }

  public int getPort() {
    return generatedJNI.XpgEndpoint_port_get(swigCPtr, this);
  }

  public void setMode(XPG_WAN_LAN value) {
    generatedJNI.XpgEndpoint_mode_set(swigCPtr, this, value.swigValue());
  }

  public XPG_WAN_LAN getMode() {
    return XPG_WAN_LAN.swigToEnum(generatedJNI.XpgEndpoint_mode_get(swigCPtr, this));
  }

  public void setConnType(XPG_CONN_TYPE value) {
    generatedJNI.XpgEndpoint_connType_set(swigCPtr, this, value.swigValue());
  }

  public XPG_CONN_TYPE getConnType() {
    return XPG_CONN_TYPE.swigToEnum(generatedJNI.XpgEndpoint_connType_get(swigCPtr, this));
  }

  public void setSzMac(String value) {
    generatedJNI.XpgEndpoint_szMac_set(swigCPtr, this, value);
  }

  public String getSzMac() {
    return generatedJNI.XpgEndpoint_szMac_get(swigCPtr, this);
  }

  public void setSzWriteTopic(String value) {
    generatedJNI.XpgEndpoint_szWriteTopic_set(swigCPtr, this, value);
  }

  public String getSzWriteTopic() {
    return generatedJNI.XpgEndpoint_szWriteTopic_get(swigCPtr, this);
  }

  public void setSzClientId(String value) {
    generatedJNI.XpgEndpoint_szClientId_set(swigCPtr, this, value);
  }

  public String getSzClientId() {
    return generatedJNI.XpgEndpoint_szClientId_get(swigCPtr, this);
  }

  public void setSzDid(String value) {
    generatedJNI.XpgEndpoint_szDid_set(swigCPtr, this, value);
  }

  public String getSzDid() {
    return generatedJNI.XpgEndpoint_szDid_get(swigCPtr, this);
  }

  public void setSzFwVer(String value) {
    generatedJNI.XpgEndpoint_szFwVer_set(swigCPtr, this, value);
  }

  public String getSzFwVer() {
    return generatedJNI.XpgEndpoint_szFwVer_get(swigCPtr, this);
  }

  public void setSzProductKey(String value) {
    generatedJNI.XpgEndpoint_szProductKey_set(swigCPtr, this, value);
  }

  public String getSzProductKey() {
    return generatedJNI.XpgEndpoint_szProductKey_get(swigCPtr, this);
  }

  public void setSzPasscode(String value) {
    generatedJNI.XpgEndpoint_szPasscode_set(swigCPtr, this, value);
  }

  public String getSzPasscode() {
    return generatedJNI.XpgEndpoint_szPasscode_get(swigCPtr, this);
  }

  public void setPiVersion(long value) {
    generatedJNI.XpgEndpoint_piVersion_set(swigCPtr, this, value);
  }

  public long getPiVersion() {
    return generatedJNI.XpgEndpoint_piVersion_get(swigCPtr, this);
  }

  public void setP0Version(long value) {
    generatedJNI.XpgEndpoint_p0Version_set(swigCPtr, this, value);
  }

  public long getP0Version() {
    return generatedJNI.XpgEndpoint_p0Version_get(swigCPtr, this);
  }

  public void setIsOnline(short value) {
    generatedJNI.XpgEndpoint_isOnline_set(swigCPtr, this, value);
  }

  public short getIsOnline() {
    return generatedJNI.XpgEndpoint_isOnline_get(swigCPtr, this);
  }

  public void setNConnectRetry(long value) {
    generatedJNI.XpgEndpoint_nConnectRetry_set(swigCPtr, this, value);
  }

  public long getNConnectRetry() {
    return generatedJNI.XpgEndpoint_nConnectRetry_get(swigCPtr, this);
  }

  public void setIsSdkInternal(short value) {
    generatedJNI.XpgEndpoint_isSdkInternal_set(swigCPtr, this, value);
  }

  public short getIsSdkInternal() {
    return generatedJNI.XpgEndpoint_isSdkInternal_get(swigCPtr, this);
  }

  public XpgEndpoint() {
    this(generatedJNI.new_XpgEndpoint(), true);
  }

}