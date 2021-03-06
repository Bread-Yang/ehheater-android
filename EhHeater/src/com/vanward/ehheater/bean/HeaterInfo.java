package com.vanward.ehheater.bean;

import net.tsz.afinal.annotation.sqlite.Id;

import com.vanward.ehheater.util.L;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;

public class HeaterInfo {

    private static final String TAG = "HeaterInfo";

	@Id
	int id;
	int binded; // (仅本地使用, 服务器无此字段) 0-no, 1-yes
	String name; // (仅本地使用, 服务器无此字段)

	// @Id
	String mac;
	String uid;
	String did;
	String passcode;

	long p0Version;
	long piVersion;
	int port;
	String fwVersion;
	String productKey;

	public HeaterInfo() {

	}

	public HeaterInfo(String uid, XpgEndpoint endpoint) {

		L.e(this, "返回的endpoint的mac是 " + endpoint.getSzMac().toLowerCase());
		L.e(this, "返回的endpoint的did是 " + endpoint.getSzDid());
		L.e(this, "返回的endpoint的passcode是 " + endpoint.getSzPasscode());
		L.e(this, "返回的endpoint的productKey是 " + endpoint.getSzProductKey());
		
		setMac(endpoint.getSzMac().toLowerCase());
		setDid(endpoint.getSzDid());
		setPasscode(endpoint.getSzPasscode());
		setP0Version(endpoint.getP0Version());
		setPiVersion(endpoint.getPiVersion());
		setPort(endpoint.getPort());
		setFwVersion(endpoint.getSzFwVer());
		setProductKey(endpoint.getSzProductKey());
		setUid(uid);
	}

	@Override
	public String toString() {
		return "HeaterInfo [binded=" + binded + ", name=" + name + ", mac="
				+ mac + ", did=" + did + ", passcode=" + passcode
				+ ", p0Version=" + p0Version + ", piVersion=" + piVersion
				+ ", port=" + port + ", fwVersion=" + fwVersion
				+ ", productKey=" + productKey + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBinded() {
		return binded;
	}

	public void setBinded(int binded) {
		this.binded = binded;
	}

	public String getName() {
		return name;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	public long getP0Version() {
		return p0Version;
	}

	public void setP0Version(long p0Version) {
		this.p0Version = p0Version;
	}

	public long getPiVersion() {
		return piVersion;
	}

	public void setPiVersion(long piVersion) {
		this.piVersion = piVersion;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}

	public String getProductKey() {
		return productKey;
	}

	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

}
