package com.vanward.ehheater.bean;

import net.tsz.afinal.annotation.sqlite.Id;

public class AccountBean {

	@Id
	int id;
	String userName;
	String passcode;
	
	public AccountBean() {
		
	}
	
	public AccountBean(String userName, String passcode) {
		this.userName = userName;
		this.passcode = passcode;
	}
	
	@Override
	public String toString() {
		return "AccountBean : [userName = " + this.userName + ", passcode = " + this.passcode + "]";
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasscode() {
		return passcode;
	}
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
}
