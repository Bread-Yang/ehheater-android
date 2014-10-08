package com.vanward.ehheater.dao;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.vanward.ehheater.bean.HeaterInfo;

public class HeaterInfoDao extends BaseDao {

	public HeaterInfoDao(Context context) {
		super(context);
	}
	
	public HeaterInfo getHeaterByMac(String mac) {
		List<HeaterInfo> result = getDb().findAllByWhere(HeaterInfo.class, " mac = '" + mac + "'");
		
		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}
	
	public void save(HeaterInfo heater) {
		Log.d("emmm", "saving heater(allow empty passcode): " + heater);
//		getDb().replace(heater);
		
		HeaterInfo old = getHeaterByMac(heater.getMac());
		if (old == null) {
			getDb().save(heater);
		} else {
			heater.setId(old.getId());
			getDb().update(heater);
		}
		
	}
	
	public List<HeaterInfo> getAll(){
		return getDb().findAll(HeaterInfo.class);
	}
}
