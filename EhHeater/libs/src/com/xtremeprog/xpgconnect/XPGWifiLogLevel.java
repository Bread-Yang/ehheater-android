/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect;

public final class XPGWifiLogLevel {
  public final static XPGWifiLogLevel XPGWifiLogLevelError = new XPGWifiLogLevel("XPGWifiLogLevelError", XPGConnectJNI.XPGWifiLogLevelError_get());
  public final static XPGWifiLogLevel XPGWifiLogLevelWarning = new XPGWifiLogLevel("XPGWifiLogLevelWarning");
  public final static XPGWifiLogLevel XPGWifiLogLevelAll = new XPGWifiLogLevel("XPGWifiLogLevelAll");

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static XPGWifiLogLevel swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + XPGWifiLogLevel.class + " with value " + swigValue);
  }

  private XPGWifiLogLevel(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private XPGWifiLogLevel(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private XPGWifiLogLevel(String swigName, XPGWifiLogLevel swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static XPGWifiLogLevel[] swigValues = { XPGWifiLogLevelError, XPGWifiLogLevelWarning, XPGWifiLogLevelAll };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}
