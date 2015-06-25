package com.vanward.ehheater.util;

import android.content.Context;

import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.HeaterInfoService;

public class IntranetLoginUtil {

	public HeaterInfo getCurrentDevice(Context context) {

		HeaterInfo curHeater = new HeaterInfoService(context)
				.getCurrentSelectedHeater();

		return curHeater;
	}

}
