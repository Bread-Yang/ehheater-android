/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class OnOffReq_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected OnOffReq_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(OnOffReq_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_OnOffReq_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setHeader(int value) {
    generatedJNI.OnOffReq_t_header_set(swigCPtr, this, value);
  }

  public int getHeader() {
    return generatedJNI.OnOffReq_t_header_get(swigCPtr, this);
  }

  public void setP0_version(short value) {
    generatedJNI.OnOffReq_t_p0_version_set(swigCPtr, this, value);
  }

  public short getP0_version() {
    return generatedJNI.OnOffReq_t_p0_version_get(swigCPtr, this);
  }

  public void setResp_address(short value) {
    generatedJNI.OnOffReq_t_resp_address_set(swigCPtr, this, value);
  }

  public short getResp_address() {
    return generatedJNI.OnOffReq_t_resp_address_get(swigCPtr, this);
  }

  public void setCommand(short value) {
    generatedJNI.OnOffReq_t_command_set(swigCPtr, this, value);
  }

  public short getCommand() {
    return generatedJNI.OnOffReq_t_command_get(swigCPtr, this);
  }

  public void setOn_off(short value) {
    generatedJNI.OnOffReq_t_on_off_set(swigCPtr, this, value);
  }

  public short getOn_off() {
    return generatedJNI.OnOffReq_t_on_off_get(swigCPtr, this);
  }

  public void setChecksum(int value) {
    generatedJNI.OnOffReq_t_checksum_set(swigCPtr, this, value);
  }

  public int getChecksum() {
    return generatedJNI.OnOffReq_t_checksum_get(swigCPtr, this);
  }

  public OnOffReq_t() {
    this(generatedJNI.new_OnOffReq_t(), true);
  }

}