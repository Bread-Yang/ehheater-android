package com.vanward.ehheater.util;

public class TcpPacketCheckUtil {
	
	static byte xAA = (byte) 0xAA;
	static byte x02 = (byte) 0x02;
	static byte x00 = (byte) 0x00;
	static byte x20 = (byte) 0x20;
	
	
	/**
	 * 检查是否为电热, 即前几个字节为 AA AA 02 00
	 * @param data
	 * @return
	 */
	private static boolean isElectricHeaterData(byte[] data) {
		
		return data[0] == xAA && data[1] == xAA && data[2] == x02 && data[3] == x00;
	}

	/**
	 * 检查是否为电热状态上报, 即前几个字节为 AA AA 02 00 20
	 * @param data
	 * @return
	 */
	public static boolean isEhStateData(byte[] data) {
		return isElectricHeaterData(data) && data[4] == x20;
	}
}
