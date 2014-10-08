package com.xtremeprog.xpgconnect;

import java.util.UUID;
import java.nio.ByteBuffer;

/**
 * Utility class for transform from byte array to data type and vice versa.
 */
public class Coding {
	// Boolean
	public static byte[] EncodeBoolean(boolean arg) {
		byte i = (byte) (arg ? 1 : 0);

		byte[] res = new byte[1];
		res[0] = i;

		return res;
	}

	public static boolean DecodeBoolean(byte[] data) {
		boolean res = data[0] == 0 ? false : true;

		return res;
	}

	// Int8
	public static byte[] EncodeInt8(byte arg) {
		byte[] res = new byte[1];
		res[0] = arg;

		return res;
	}

	public static byte DecodeInt8(byte[] data) {
		byte res = data[0];

		return res;
	}

	// UInt8
	public static byte[] EncodeUInt8(short arg) {
		byte i = (byte) arg;
		byte[] res = new byte[1];
		res[0] = i;

		return res;
	}

	public static short DecodeUInt8(byte[] data) {
		short res = (short) data[0];
		res = (short) (res & 0xFF);

		return res;
	}

	// Int16
	public static byte[] EncodeInt16(short arg) {
		byte[] res = new byte[2];
		res[0] = (byte) (arg >> 8);
		res[1] = (byte) arg;

		return res;
	}

	public static short DecodeInt16(byte[] data) {
		short res = (short) (data[0] << 8 | (data[1] & 0xFF));

		return res;
	}

	// UInt16
	public static byte[] EncodeUInt16(int arg) {
		byte[] res = new byte[2];
		res[0] = (byte) (arg >> 8);
		res[1] = (byte) arg;

		return res;
	}

	public static int DecodeUInt16(byte[] data) {
		int res = (int) (data[0] << 8 | (data[1] & 0xFF));

		return res;
	}

	// Int32
	public static byte[] EncodeInt32(int arg) {
		byte[] res = new byte[4];
		res[0] = (byte) (arg >> 24);
		res[1] = (byte) (arg >> 16);
		res[2] = (byte) (arg >> 8);
		res[3] = (byte) arg;

		return res;
	}

	public static int DecodeInt32(byte[] data) {
		int res = (int) ((data[0] << 24) | (data[1] << 16) | (data[2] << 8) | (data[3] & 0xFF));

		return res;
	}

	// UInt32
	public static byte[] EncodeUInt32(long arg) {
		byte[] res = new byte[4];
		res[0] = (byte) (arg >> 24);
		res[1] = (byte) (arg >> 16);
		res[2] = (byte) (arg >> 8);
		res[3] = (byte) arg;

		return res;
	}

	public static long DecodeUInt32(byte[] data) {
		long res = (long) ((data[0] << 24) | (data[1] << 16) | (data[2] << 8) | (data[3] & 0xFF)) & 0xFFFFFFFFL;

		return res;
	}

	// UUID
	public static byte[] EncodeUUID(UUID uuid) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());

		return bb.array();
	}

	public static UUID DecodeUUID(byte[] uuidBytes) {
		ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
		UUID uuid = new UUID(bb.getLong(), bb.getLong());

		return uuid;
	}

	// String
	public static byte[] EncodeString(String arg) {
		byte[] res = new byte[0];
		try {
			res = arg.getBytes("UTF-8");
		} catch (Exception ex) {
		}

		return res;
	}

	public static String DecodeString(byte[] data) {
		String res = "";
		try {
			res = new String(data, 0, data.length, "UTF-8");
		} catch (Exception ex) {
		}

		return res;
	}

	// -- others -----------------------
	public static int GetStringByteCount(String str) {
		return EncodeString(str).length;
	}
}
