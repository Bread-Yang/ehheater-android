package com.easylink.android;

import java.util.ArrayList;

public class Ftc20 {
	private ArrayList<Integer> mData;
	private String mSsid;
	private byte[] mKey;

	public void encodePackets() throws Exception {
		int T_START = 1399;
		int T_MID = 1459;

		this.mData = new ArrayList();

		this.mData.add(Integer.valueOf(1399));
		constructSsid();
		this.mData.add(Integer.valueOf(1459));
		constructKey();
	}

	private void constructSsid() throws Exception {
		int ConstOffset_1 = 1;
		int ConstOffset_2 = 27;

		int ssidLength = this.mSsid.length();

		int ssidL = ssidLength + 1 + 27;

		this.mData.add(Integer.valueOf(ssidL));

		encodeSsidString(this.mSsid);
	}

	private void encodeSsidString(String ssid) throws Exception {
		int DataOffset = 593;
		byte prevNibble = 0;
		int currentIndex = 0;

		byte[] stringBuffer = new byte[ssid.length()];
		stringBuffer = convertStringToBytes(ssid);

		for (int i = 0; i < ssid.length(); i++) {
			byte currentChar = stringBuffer[i];

			int lowNibble = currentChar & 0xF;
			int highNibble = currentChar >> 4;

			this.mData
					.add(Integer
							.valueOf(((prevNibble ^ currentIndex++) << 4 | highNibble) + 593));
			prevNibble = (byte) highNibble;
			this.mData
					.add(Integer
							.valueOf(((prevNibble ^ currentIndex++) << 4 | lowNibble) + 593));
			prevNibble = (byte) lowNibble;

			currentIndex &= 15;
		}
	}

	private void constructKey() throws Exception {
		int ConstOffset_1 = 1;
		int ConstOffset_2 = 27;

		int keyLength = this.mKey.length;

		int keyL = keyLength + 1 + 27;

		this.mData.add(Integer.valueOf(keyL));

		encodeKeyString(this.mKey);
	}

	private void encodeKeyString(byte[] key) throws Exception {
		int DataOffset = 593;
		byte prevNibble = 0;
		int currentIndex = 0;

		for (int i = 0; i < key.length; i++) {
			int currentChar = intToUint8(key[i]);
			int lowNibble = currentChar & 0xF;
			int highNibble = currentChar >> 4;

			this.mData
					.add(Integer
							.valueOf(((prevNibble ^ currentIndex++) << 4 | highNibble) + 593));
			prevNibble = (byte) highNibble;
			this.mData
					.add(Integer
							.valueOf(((prevNibble ^ currentIndex++) << 4 | lowNibble) + 593));
			prevNibble = (byte) lowNibble;

			currentIndex &= 15;
		}
	}

	private byte[] convertStringToBytes(String string) throws Exception {
		return string.getBytes();
	}

	private int intToUint8(int number) throws Exception {
		return number & 0xFF;
	}

	public ArrayList<Integer> getmData() throws Exception {
		return this.mData;
	}

	public String getmSsid() throws Exception {
		return this.mSsid;
	}

	public void setmSsid(String mSsid) throws Exception {
		this.mSsid = mSsid;
	}

	public byte[] getmKey() throws Exception {
		return this.mKey;
	}

	public void setmKey(byte[] mKey) throws Exception {
		this.mKey = mKey;
	}
}