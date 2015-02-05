/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect;

public class XPGWifiSSIDList {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected XPGWifiSSIDList(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(XPGWifiSSIDList obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        XPGConnectJNI.delete_XPGWifiSSIDList(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public long GetCount() {
    return XPGConnectJNI.XPGWifiSSIDList_GetCount(swigCPtr, this);
  }

  public XPGWifiSSID GetItem(int index) {
    long cPtr = XPGConnectJNI.XPGWifiSSIDList_GetItem(swigCPtr, this, index);
    return (cPtr == 0) ? null : new XPGWifiSSID(cPtr, false);
  }

  public XPGWifiSSIDList() {
    this(XPGConnectJNI.new_XPGWifiSSIDList(), true);
  }

}
