/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public final class XPG_VERSION_EVENT_KEY {
  public final static XPG_VERSION_EVENT_KEY PI_VERSION = new XPG_VERSION_EVENT_KEY("PI_VERSION", generatedJNI.PI_VERSION_get());
  public final static XPG_VERSION_EVENT_KEY P0_VERSION = new XPG_VERSION_EVENT_KEY("P0_VERSION", generatedJNI.P0_VERSION_get());

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static XPG_VERSION_EVENT_KEY swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + XPG_VERSION_EVENT_KEY.class + " with value " + swigValue);
  }

  private XPG_VERSION_EVENT_KEY(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private XPG_VERSION_EVENT_KEY(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private XPG_VERSION_EVENT_KEY(String swigName, XPG_VERSION_EVENT_KEY swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static XPG_VERSION_EVENT_KEY[] swigValues = { PI_VERSION, P0_VERSION };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

