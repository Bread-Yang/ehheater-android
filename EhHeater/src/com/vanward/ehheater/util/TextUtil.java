package com.vanward.ehheater.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

/**
 * 文字工具类
 * 
 */
public class TextUtil {
	private Application mApplication;

	public TextUtil(Application application) {
		mApplication = application;
	}

	/**
	 * 根据输入流转换成字符串
	 * 
	 * @param inputStream
	 *            文字输入流
	 * @return 文字字符串(String 类型)
	 */
	public String readTextFile(InputStream inputStream) {
		String readedStr = "";
		BufferedReader br; // BfferedReader类用于从缓冲区中读取内容,所有的输入字节数据
		// 都将放在缓冲区中.BufferedReader中定义的构造方法只能接受字符输入流的实例,所有必须
		// 使用字符输入流和字节输入流的转换类InputStreamReader将字节输入流Sysem.in变为字符流
		// 1个汉字字符存储需要2个字节，1个英文字符存储需要1个字节
		// 字符>=字节 , 字符不是固定的,可以变得
		try {
			br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String tmp;
			while ((tmp = br.readLine()) != null) {
				readedStr += tmp;
			}
			br.close();
			inputStream.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return readedStr;
	}
}
