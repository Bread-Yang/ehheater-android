package com.vanward.ehheater.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

public class IntelligentPatternUtil {
	/**
	 * 采集用户前30次设定水温,以最多次数的温度为默认值
	 */
	public static int getMostSetTemperature(Context context) {
		ArrayList<Integer> last30TemperatureArray = getLast30TemperatureArray(context);
		HashMap<String, Integer> last30TemperatureMap = getLast30TemperatureMap(context);

		if (last30TemperatureArray == null) {
			last30TemperatureArray = new ArrayList<Integer>();
		} else {
			for (Integer i : last30TemperatureArray) {
				Log.e("getLast30TemperatureArray遍历 : ", i + "");
			}
		}

		if (last30TemperatureMap == null) {
			last30TemperatureMap = new HashMap<String, Integer>();
		} else {
			for (Map.Entry<String, Integer> entry : last30TemperatureMap
					.entrySet()) {
				Log.e("getLast30TemperatureMap遍历是 : ", "key : " + entry.getKey() + " value : "
						+ entry.getValue());
			}
		}

		int maxTemper = 0; // 最多次数的温度
		int maxCount = 0; // 最多次数
		for (Integer item : last30TemperatureArray) {
			if (null != last30TemperatureMap.get(String.valueOf(item))) {
				int count = last30TemperatureMap.get(String.valueOf(item));
				// Log.e("count是 : ", count + "");
				if (count >= maxCount) {
					maxTemper = item;
					maxCount = count;
				}
			}
		}
		if (maxTemper == 0) { // 第一次默认是45°
			maxTemper = 45;
		}
		return maxTemper;
	}

	/**
	 * 采集用户前30次设定功率,以最多次数的功率为默认值
	 */
	public static int getMostSetPower(Context context) {
		ArrayList<Integer> last30PowerArray = getLast30PowerArray(context);
		HashMap<String, Integer> last30PowerMap = getLast30PowerMap(context);

		if (last30PowerArray == null) {
			last30PowerArray = new ArrayList<Integer>();
		} else {
			for (Integer i : last30PowerArray) {
				Log.e("getLast30PowerArray遍历 : ", i + "");
			}
		}

		if (last30PowerMap == null) {
			last30PowerMap = new HashMap<String, Integer>();
		} else {
			for (Map.Entry<String, Integer> entry : last30PowerMap.entrySet()) {
				Log.e("getLast30PowerMap遍历是 : ", "key : " + entry.getKey()
						+ " value : " + entry.getValue());
			}
		}

		int maxPower = 0; // 最多次数的温度
		int maxCount = 0; // 最多次数
		for (Integer item : last30PowerArray) {
			if (null != last30PowerMap.get(String.valueOf(item))) {
				int count = last30PowerMap.get(String.valueOf(item));
				// Log.e("count是 : ", count + "");
				if (count >= maxCount) {
					maxPower = item;
					maxCount = count;
				}
			}
		}
		if (maxPower == 0) { // 第一次默认是3kw
			maxPower = 3;
		}
		return maxPower;
	}

	public static void addLastTemperature(Context context, int temperature) {
		ArrayList<Integer> last30TemperatureArray = getLast30TemperatureArray(context);
		HashMap<String, Integer> last30TemperatureMap = getLast30TemperatureMap(context);

		if (last30TemperatureArray == null) {
			last30TemperatureArray = new ArrayList<Integer>();
		}

		if (last30TemperatureMap == null) {
			last30TemperatureMap = new HashMap<String, Integer>();
		}

		if (last30TemperatureArray.size() >= 30) {
			last30TemperatureArray.remove(0);
		}
		last30TemperatureArray.add(temperature);

		if (null != last30TemperatureMap.get(String.valueOf(temperature))) {
			int count = last30TemperatureMap.get(String.valueOf(temperature));
			last30TemperatureMap.put(String.valueOf(temperature), count + 1);
		} else {
			last30TemperatureMap.put(String.valueOf(temperature), 1);
		}
		saveLast30TemperatureToSharedPreferences(context,
				last30TemperatureArray, last30TemperatureMap);
	}

	public static void addLastPower(Context context, int power) {
		ArrayList<Integer> last30PowerArray = getLast30PowerArray(context);
		HashMap<String, Integer> last30PowerMap = getLast30PowerMap(context);

		if (last30PowerArray == null) {
			last30PowerArray = new ArrayList<Integer>();
		}

		if (last30PowerMap == null) {
			last30PowerMap = new HashMap<String, Integer>();
		}

		if (last30PowerArray.size() >= 30) {
			last30PowerArray.remove(0);
		}
		last30PowerArray.add(power);

		if (null != last30PowerMap.get(String.valueOf(power))) {
			int count = last30PowerMap.get(String.valueOf(power));
			last30PowerMap.put(String.valueOf(power), count + 1);
		} else {
			last30PowerMap.put(String.valueOf(power), 1);
		}
		saveLast30PowerToSharedPreferences(context, last30PowerArray,
				last30PowerMap);
	}

	public static ArrayList<Integer> getLast30TemperatureArray(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"intelligentPattern", Context.MODE_PRIVATE);
		ArrayList<Integer> last30Temperature = null;
		String dataBase64 = preferences.getString("last30TemperatureArray", "");

		// 读取字节
		byte[] base64 = Base64.decode(dataBase64, Base64.DEFAULT);

		// 封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			// 再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				// 读取对象
				last30Temperature = (ArrayList<Integer>) bis.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return last30Temperature;
	}

	public static HashMap<String, Integer> getLast30TemperatureMap(
			Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"intelligentPattern", Context.MODE_PRIVATE);
		HashMap<String, Integer> last30TemperatureMap = null;
		String dataBase64 = preferences.getString("last30TemperatureMap", "");

		// 读取字节
		byte[] base64 = Base64.decode(dataBase64, Base64.DEFAULT);

		// 封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			// 再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				// 读取对象
				last30TemperatureMap = (HashMap<String, Integer>) bis
						.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return last30TemperatureMap;
	}

	public static void saveLast30TemperatureToSharedPreferences(
			Context context, ArrayList<Integer> last30Temperature,
			HashMap<String, Integer> last30TemperatureMap) {
		SharedPreferences preferences = context.getSharedPreferences(
				"intelligentPattern", Context.MODE_PRIVATE);
		// 创建字节输出流
		ByteArrayOutputStream baosArray = new ByteArrayOutputStream();
		ByteArrayOutputStream baosMap = new ByteArrayOutputStream();
		try {
			// 创建对象输出流，并封装字节流
			ObjectOutputStream oos = new ObjectOutputStream(baosArray);
			// 将对象写入字节流
			oos.writeObject(last30Temperature);
			// 将字节流编码成base64的字符窜
			String data_Base64 = new String(Base64.encodeToString(
					baosArray.toByteArray(), Base64.DEFAULT));
			Editor editor = preferences.edit();
			editor.putString("last30TemperatureArray", data_Base64);

			oos = new ObjectOutputStream(baosMap);
			// 将对象写入字节流
			oos.writeObject(last30TemperatureMap);
			// 将字节流编码成base64的字符窜
			String map_Base64 = new String(Base64.encodeToString(
					baosMap.toByteArray(), Base64.DEFAULT));
			editor.putString("last30TemperatureMap", map_Base64);

			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Integer> getLast30PowerArray(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"intelligentPattern", Context.MODE_PRIVATE);
		ArrayList<Integer> last30PowerArray = null;
		String dataBase64 = preferences.getString("last30PowerArray", "");

		// 读取字节
		byte[] base64 = Base64.decode(dataBase64, Base64.DEFAULT);

		// 封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			// 再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				// 读取对象
				last30PowerArray = (ArrayList<Integer>) bis.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return last30PowerArray;
	}

	public static HashMap<String, Integer> getLast30PowerMap(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"intelligentPattern", Context.MODE_PRIVATE);
		HashMap<String, Integer> last30PowerMap = null;
		String dataBase64 = preferences.getString("last30PowerMap", "");

		// 读取字节
		byte[] base64 = Base64.decode(dataBase64, Base64.DEFAULT);

		// 封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			// 再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				// 读取对象
				last30PowerMap = (HashMap<String, Integer>) bis.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return last30PowerMap;
	}

	public static void saveLast30PowerToSharedPreferences(Context context,
			ArrayList<Integer> last30PowerArray,
			HashMap<String, Integer> last30PowerMap) {
		SharedPreferences preferences = context.getSharedPreferences(
				"intelligentPattern", Context.MODE_PRIVATE);
		// 创建字节输出流
		ByteArrayOutputStream baosArray = new ByteArrayOutputStream();
		ByteArrayOutputStream baosMap = new ByteArrayOutputStream();
		try {
			// 创建对象输出流，并封装字节流
			ObjectOutputStream oos = new ObjectOutputStream(baosArray);
			// 将对象写入字节流
			oos.writeObject(last30PowerArray);
			// 将字节流编码成base64的字符窜
			String array_Base64 = new String(Base64.encodeToString(
					baosArray.toByteArray(), Base64.DEFAULT));
			Editor editor = preferences.edit();
			editor.putString("last30PowerArray", array_Base64);

			oos = new ObjectOutputStream(baosMap);
			// 将对象写入字节流
			oos.writeObject(last30PowerMap);
			// 将字节流编码成base64的字符窜
			String map_Base64 = new String(Base64.encodeToString(
					baosMap.toByteArray(), Base64.DEFAULT));
			editor.putString("last30PowerMap", map_Base64);

			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
