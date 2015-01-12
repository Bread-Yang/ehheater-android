/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public final class XPG_CONFIG_KEY {
  public final static XPG_CONFIG_KEY INTERVAL_BEFORE_SEND = new XPG_CONFIG_KEY("INTERVAL_BEFORE_SEND");
  public final static XPG_CONFIG_KEY CONNECT_TIMEOUT = new XPG_CONFIG_KEY("CONNECT_TIMEOUT");
  public final static XPG_CONFIG_KEY LOG_LEVEL = new XPG_CONFIG_KEY("LOG_LEVEL");
  public final static XPG_CONFIG_KEY LAN_TTL_TIMEOUT = new XPG_CONFIG_KEY("LAN_TTL_TIMEOUT");
  public final static XPG_CONFIG_KEY SERVER_ADDR = new XPG_CONFIG_KEY("SERVER_ADDR");
  public final static XPG_CONFIG_KEY SERVER_PORT = new XPG_CONFIG_KEY("SERVER_PORT");
  public final static XPG_CONFIG_KEY SERVER_ADDR_GET = new XPG_CONFIG_KEY("SERVER_ADDR_GET");
  public final static XPG_CONFIG_KEY DEVICE_FOUND_TIMER = new XPG_CONFIG_KEY("DEVICE_FOUND_TIMER");

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static XPG_CONFIG_KEY swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + XPG_CONFIG_KEY.class + " with value " + swigValue);
  }

  private XPG_CONFIG_KEY(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private XPG_CONFIG_KEY(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private XPG_CONFIG_KEY(String swigName, XPG_CONFIG_KEY swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static XPG_CONFIG_KEY[] swigValues = { INTERVAL_BEFORE_SEND, CONNECT_TIMEOUT, LOG_LEVEL, LAN_TTL_TIMEOUT, SERVER_ADDR, SERVER_PORT, SERVER_ADDR_GET, DEVICE_FOUND_TIMER };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

