/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xtremeprog.xpgconnect.generated;

public class XpgConnection {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected XpgConnection(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(XpgConnection obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        generatedJNI.delete_XpgConnection(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setId(int value) {
    generatedJNI.XpgConnection_id_set(swigCPtr, this, value);
  }

  public int getId() {
    return generatedJNI.XpgConnection_id_get(swigCPtr, this);
  }

  public void setType(XPG_CONN_TYPE value) {
    generatedJNI.XpgConnection_type_set(swigCPtr, this, value.swigValue());
  }

  public XPG_CONN_TYPE getType() {
    return XPG_CONN_TYPE.swigToEnum(generatedJNI.XpgConnection_type_get(swigCPtr, this));
  }

  public void setFd(int value) {
    generatedJNI.XpgConnection_fd_set(swigCPtr, this, value);
  }

  public int getFd() {
    return generatedJNI.XpgConnection_fd_get(swigCPtr, this);
  }

  public void setPBio(SWIGTYPE_p_BIO value) {
    generatedJNI.XpgConnection_pBio_set(swigCPtr, this, SWIGTYPE_p_BIO.getCPtr(value));
  }

  public SWIGTYPE_p_BIO getPBio() {
    long cPtr = generatedJNI.XpgConnection_pBio_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_BIO(cPtr, false);
  }

  public void setBroker(SWIGTYPE_p_mqtt_broker_handle_t value) {
    generatedJNI.XpgConnection_broker_set(swigCPtr, this, SWIGTYPE_p_mqtt_broker_handle_t.getCPtr(value));
  }

  public SWIGTYPE_p_mqtt_broker_handle_t getBroker() {
    return new SWIGTYPE_p_mqtt_broker_handle_t(generatedJNI.XpgConnection_broker_get(swigCPtr, this), true);
  }

  public void setEndpoint(XpgEndpoint value) {
    generatedJNI.XpgConnection_endpoint_set(swigCPtr, this, XpgEndpoint.getCPtr(value), value);
  }

  public XpgEndpoint getEndpoint() {
    long cPtr = generatedJNI.XpgConnection_endpoint_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgEndpoint(cPtr, false);
  }

  public void setTopics(XpgMqttTopic value) {
    generatedJNI.XpgConnection_topics_set(swigCPtr, this, XpgMqttTopic.getCPtr(value), value);
  }

  public XpgMqttTopic getTopics() {
    long cPtr = generatedJNI.XpgConnection_topics_get(swigCPtr, this);
    return (cPtr == 0) ? null : new XpgMqttTopic(cPtr, false);
  }

  public XpgConnection() {
    this(generatedJNI.new_XpgConnection(), true);
  }

}
