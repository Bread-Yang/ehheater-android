/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class ModuleVersionResp_t {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected ModuleVersionResp_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ModuleVersionResp_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_ModuleVersionResp_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setPiVersion(long value) {
    generatedJNI.ModuleVersionResp_t_piVersion_set(swigCPtr, this, value);
  }

  public long getPiVersion() {
    return generatedJNI.ModuleVersionResp_t_piVersion_get(swigCPtr, this);
  }

  public void setP0Version(XpgDataField value) {
    generatedJNI.ModuleVersionResp_t_p0Version_set(swigCPtr, this, XpgDataField.getCPtr(value), value);
  }

  public XpgDataField getP0Version() {
    long cPtr = generatedJNI.ModuleVersionResp_t_p0Version_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgDataField(cPtr, false);
  }

  public ModuleVersionResp_t() {
    this(generatedJNI.new_ModuleVersionResp_t(), true);
  }

}
