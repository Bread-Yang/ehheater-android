package com.vanward.ehheater.service;

import android.content.Context;

import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;

public class HeaterInfoService {
	Context context;

	public HeaterInfoService(Context context) {
		this.context = context;
	}

	/**
	 * 增加新配置的热水器, 增加后设为默认热水器
	 * 
	 * @param heater
	 */
	public void addNewHeater(HeaterInfo heater) {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		hdao.save(heater);
		new SharedPreferUtils(context).put(ShareKey.CurDeviceMac,
				heater.getMac());
	}

	/**
	 * 保存下载到的热水器, 自动设置为已绑定
	 * 
	 * @param heater
	 */
	public void saveDownloadedHeater(HeaterInfo heater) {
		heater.setBinded(1);
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		hdao.save(heater);
	}

	public void updateDid(String mac, String did) {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		HeaterInfo he = hdao.getHeaterByMac(mac);
		he.setDid(did);
		hdao.save(he);
	}

	public void updatePasscode(String mac, String passcode) {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		HeaterInfo he = hdao.getHeaterByMac(mac);
		he.setPasscode(passcode);
		hdao.save(he);
	}

	public void updateBinded(String mac, boolean isBinded) {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		HeaterInfo he = hdao.getHeaterByMac(mac);
		he.setBinded(isBinded ? 1 : 0);
		hdao.save(he);
	}

	public HeaterInfo getCurrentSelectedHeater() {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		String curSelectMac = new SharedPreferUtils(context).get(
				ShareKey.CurDeviceMac, "");
		return hdao.getHeaterByMac(curSelectMac);
	}

	public void setCurrentSelectedHeater(String macAddress) {
		new SharedPreferUtils(context).put(ShareKey.CurDeviceMac, macAddress);
	}

	public void deleteHeater(String mac) {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		HeaterInfo hi = hdao.getHeaterByMac(mac);
		hdao.getDb().deleteById(HeaterInfo.class, hi.getId());
	}
	
	public void deleteAllHeaters() {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
//		hdao.getDb().deleteAll(HeaterInfo.class);
		hdao.getDb().dropTable(HeaterInfo.class);
		hdao.getDb().createTable(HeaterInfo.class);
	}

}
