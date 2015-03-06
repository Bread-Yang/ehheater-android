/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public final class XPG_CONN_TYPE {
  public final static XPG_CONN_TYPE TCP = new XPG_CONN_TYPE("TCP", generatedJNI.TCP_get());
  public final static XPG_CONN_TYPE STCP = new XPG_CONN_TYPE("STCP", generatedJNI.STCP_get());
  public final static XPG_CONN_TYPE BT = new XPG_CONN_TYPE("BT", generatedJNI.BT_get());
  public final static XPG_CONN_TYPE BLE = new XPG_CONN_TYPE("BLE", generatedJNI.BLE_get());

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static XPG_CONN_TYPE swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + XPG_CONN_TYPE.class + " with value " + swigValue);
  }

  private XPG_CONN_TYPE(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private XPG_CONN_TYPE(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private XPG_CONN_TYPE(String swigName, XPG_CONN_TYPE swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static XPG_CONN_TYPE[] swigValues = { TCP, STCP, BT, BLE };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

