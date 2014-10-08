package com.easylink.android;

import java.util.ArrayList;

public class FtcEncode {
	private ArrayList<Integer> mData;

	public FtcEncode(String ssid, byte[] key) throws Exception {
		Ftc20 ftc = new Ftc20();

		ftc.setmSsid(ssid);

		ftc.setmKey(key);

		ftc.encodePackets();

		this.mData = ftc.getmData();
	}

	public ArrayList<Integer> getmData() throws Exception {
		return this.mData;
	}
}
