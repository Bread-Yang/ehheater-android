package com.vanward.ehheater.service;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.vanward.ehheater.activity.configure.DummySendBindingReqActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;

public class HeaterInfoService {

	private static final String TAG = "HeaterInfoService";

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
		generateDefaultName(heater);
		changeDuplicatedName(heater);
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
	public void saveDownloadedHeaterByUid(String uid, HeaterInfo heater) {
		generateDefaultName(heater);
		changeDuplicatedName(heater);
		heater.setBinded(1);

		HeaterInfoDao hdao = new HeaterInfoDao(context);
		HeaterInfo old = hdao.getHeaterByUidAndMac(uid, heater.getMac()); // 如果之前已经保存过,就不保存了
		if (old == null) {
			hdao.save(heater);
		}
	}

	public void updateDidByUid(String uid, String mac, String did) {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		HeaterInfo he = hdao.getHeaterByUidAndMac(uid, mac);
		he.setDid(did);
		hdao.save(he);
	}

	public void updatePasscode(String uid, String mac, String passcode) {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		HeaterInfo he = hdao.getHeaterByUidAndMac(uid, mac);
		he.setPasscode(passcode);
		hdao.save(he);
	}

	public void updateBindedByUid(String uid, String mac, boolean isBinded) {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		HeaterInfo he = hdao.getHeaterByUidAndMac(uid, mac);
		he.setBinded(isBinded ? 1 : 0);
		hdao.save(he);
	}

	public HeaterInfo getCurrentSelectedHeater() {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		String curSelectMac = new SharedPreferUtils(context).get(
				ShareKey.CurDeviceMac, "");
		HeaterInfo currentHeater = hdao.getHeaterByUidAndMac(
				AccountService.getUserId(context), curSelectMac);
		if (currentHeater == null) {
			List<HeaterInfo> devices = hdao.getHeaterByUid(AccountService
					.getUserId(context));
			if (devices != null && devices.size() > 0) {
				return devices.get(devices.size() - 1);
			} else {
				return null;
			}
		}
		return currentHeater;
	}

	public void setCurrentSelectedHeater(String macAddress) {
		new SharedPreferUtils(context).put(ShareKey.CurDeviceMac, macAddress);
	}

	public String getCurrentSelectedHeaterMac() {
		return new SharedPreferUtils(context).get(ShareKey.CurDeviceMac, "");
	}

	public void deleteHeaterByUid(String uid, String mac) {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		HeaterInfo hi = hdao.getHeaterByUidAndMac(uid, mac);
		if (hi == null) {
			return;
		}
		hdao.getDb().deleteById(HeaterInfo.class, hi.getId());
	}

	public void deleteAllHeatersByUid(String uid) {
		HeaterInfoDao hdao = new HeaterInfoDao(context);
		// hdao.getDb().dropTable(HeaterInfo.class);
		// hdao.getDb().createTable(HeaterInfo.class);
		hdao.getDb().deleteByWhere(HeaterInfo.class, " uid = '" + uid + "'");
	}

	public enum HeaterType {

		ELECTRIC_HEATER(Consts.ELECTRIC_HEATER_DEFAULT_NAME,
				Consts.ELECTRIC_HEATER_P_KEY), GAS_HEATER(
				Consts.GAS_HEATER_DEFAULT_NAME, Consts.GAS_HEATER_P_KEY), FURNACE(
				Consts.FURNACE_DEFAULT_NAME, Consts.FURNACE_PRODUCT_KEY), Unknown(
				Consts.HEATER_DEFAULT_NAME, "");

		public String defName, pkey;

		HeaterType(String defName, String pkey) {
			this.defName = defName;
			this.pkey = pkey;
		}

	}

	public HeaterType getHeaterType(HeaterInfo hinfo) {

		if (hinfo == null) {
			L.e(this, "getHeaterType的hinfo为null");
			return HeaterType.Unknown;
		}
		// L.e(this, hinfo.getProductKey());
		if (Consts.ELECTRIC_HEATER_P_KEY.equals(hinfo.getProductKey())) {
			return HeaterType.ELECTRIC_HEATER;
		} else if (Consts.GAS_HEATER_P_KEY.equals(hinfo.getProductKey())) {
			return HeaterType.GAS_HEATER;
		} else if (Consts.FURNACE_PRODUCT_KEY.equals(hinfo.getProductKey())) {
			return HeaterType.FURNACE;
		} else {
			return HeaterType.Unknown;
		}

	}

	public HeaterType getCurHeaterType() {
		return getHeaterType(getCurrentSelectedHeater());
	}

	public void generateDefaultName(HeaterInfo hinfo) {
		HeaterType type = getHeaterType(hinfo);
		// int count = new HeaterInfoDao(context).getHeaterCountOfType(type);

		// if (count == 0) {
		hinfo.setName(type.defName);
		// }
		// else {
		// hinfo.setName(type.defName + " (" + (count + 1) + ")");
		// }

	}

	public void changeDuplicatedName(HeaterInfo hinfo) {
		duplicates = 0;
		origName = hinfo.getName();
		recurseChangeDuplicatedName(hinfo);
	}

	private void recurseChangeDuplicatedName(HeaterInfo hinfo) {
		// check name exists
		// true
		// reset hinfo.name according to a regulation
		// method recurse
		// fasle
		// return (just do nothing)

		duplicates++;
		boolean nameExists = new HeaterInfoDao(context)
				.isDeviceNameExistsByUid(hinfo.getUid(), hinfo.getName());
		if (nameExists) {
			hinfo.setName(origName + " (" + duplicates + ")");
			recurseChangeDuplicatedName(hinfo);
		}
	}

	int duplicates;
	String origName;

	public static boolean shouldExecuteBinding(HeaterInfo hinfo) {
		return hinfo.getBinded() == 0
				&& !TextUtils.isEmpty(hinfo.getPasscode())
				&& !TextUtils.isEmpty(hinfo.getDid());
	}

	public static void setBinding(Activity act, String did, String passcode) {

		Intent intent = new Intent();
		intent.setClass(act.getBaseContext(), DummySendBindingReqActivity.class);

		intent.putExtra(Consts.INTENT_EXTRA_USERNAME,
				AccountService.getUserId(act.getBaseContext()));
		intent.putExtra(Consts.INTENT_EXTRA_USERPSW,
				AccountService.getUserPsw(act.getBaseContext()));
		intent.putExtra(Consts.INTENT_EXTRA_DID2BIND, did);
		intent.putExtra(Consts.INTENT_EXTRA_PASSCODE2BIND, passcode);

		act.startActivityForResult(intent, Consts.REQUESTCODE_UPLOAD_BINDING);

	}

	/**
	 * 设备是否有效, 即检查productkey是否为电热, 燃热, 壁挂炉等
	 * 
	 * @param hinfo
	 * @return
	 */
	public boolean isValidDevice(HeaterInfo hinfo) {
		return !(getHeaterType(hinfo).equals(HeaterType.Unknown));
	}

}
