/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public final class XPG_WAN_LAN {
  public final static XPG_WAN_LAN LAN = new XPG_WAN_LAN("LAN", generatedJNI.LAN_get());
  public final static XPG_WAN_LAN WAN = new XPG_WAN_LAN("WAN", generatedJNI.WAN_get());
  public final static XPG_WAN_LAN MQTT = new XPG_WAN_LAN("MQTT", generatedJNI.MQTT_get());

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static XPG_WAN_LAN swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + XPG_WAN_LAN.class + " with value " + swigValue);
  }

  private XPG_WAN_LAN(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private XPG_WAN_LAN(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private XPG_WAN_LAN(String swigName, XPG_WAN_LAN swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static XPG_WAN_LAN[] swigValues = { LAN, WAN, MQTT };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

