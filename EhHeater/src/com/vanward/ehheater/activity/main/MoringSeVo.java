package com.vanward.ehheater.activity.main;

import net.tsz.afinal.annotation.sqlite.Id;

public class MoringSeVo {
	@Id
	String id = "1";
	int people;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}

}