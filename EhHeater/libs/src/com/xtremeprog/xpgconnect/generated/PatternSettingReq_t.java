/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class PatternSettingReq_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected PatternSettingReq_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(PatternSettingReq_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_PatternSettingReq_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setHeader(int value) {
    generatedJNI.PatternSettingReq_t_header_set(swigCPtr, this, value);
  }

  public int getHeader() {
    return generatedJNI.PatternSettingReq_t_header_get(swigCPtr, this);
  }

  public void setP0_version(short value) {
    generatedJNI.PatternSettingReq_t_p0_version_set(swigCPtr, this, value);
  }

  public short getP0_version() {
    return generatedJNI.PatternSettingReq_t_p0_version_get(swigCPtr, this);
  }

  public void setResp_address(short value) {
    generatedJNI.PatternSettingReq_t_resp_address_set(swigCPtr, this, value);
  }

  public short getResp_address() {
    return generatedJNI.PatternSettingReq_t_resp_address_get(swigCPtr, this);
  }

  public void setCommand(short value) {
    generatedJNI.PatternSettingReq_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.PatternSettingReq_t_command_get(swigCPtr, this);
  }

  public void setPattern_setting(short value) {
    generatedJNI.PatternSettingReq_t_pattern_setting_set(swigCPtr, this, value);
  }

  public short getPattern_setting() {
    return generatedJNI.PatternSettingReq_t_pattern_setting_get(swigCPtr, this);
  }

  public void setChecksum(int value) {
    generatedJNI.PatternSettingReq_t_checksum_set(swigCPtr, this, value);
  }

  public int getChecksum() {
    return generatedJNI.PatternSettingReq_t_checksum_get(swigCPtr, this);
  }

  public PatternSettingReq_t() {
    this(generatedJNI.new_PatternSettingReq_t(), true);
  }

}