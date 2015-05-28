package com.vanward.ehheater.dao;

import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.L;

public class HeaterInfoDao extends BaseDao {

    private static final String TAG = "HeaterInfoDao";

	public HeaterInfoDao(Context context) {
		super(context);
	}

	public HeaterInfo getHeaterByMac(String mac) {
		List<HeaterInfo> result = getDb().findAllByWhere(HeaterInfo.class,
				" mac = '" + mac + "'");

		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}
	
	public HeaterInfo getHeaterByDid(String did) {
		List<HeaterInfo> result = getDb().findAllByWhere(HeaterInfo.class,
				" did = '" + did + "'");

		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	public void save(HeaterInfo heater) {
		L.e(this, "saving heater(allow empty passcode): " + heater);
		// getDb().replace(heater);

		HeaterInfo old = getHeaterByMac(heater.getMac());
		if (old == null) {
			getDb().save(heater);
		} else {
			heater.setId(old.getId());
			getDb().update(heater);
		}

	}

	public List<HeaterInfo> getAll() {
		return getDb().findAll(HeaterInfo.class);
	}

	public int getHeaterCountOfType(HeaterType type) {
		List<HeaterInfo> ret;

		if (type == HeaterType.Unknown) {
			return getUnknownHeaterCount();
		} else {
			ret = getDb().findAllByWhere(HeaterInfo.class,
					" productKey = '" + type.pkey + "'");
		}

		if (ret == null) {
			return 0;
		} else {
			return ret.size();
		}
	}

	public List<HeaterInfo> getAllDeviceOfType(HeaterType type) {
		List<HeaterInfo> deviceList;
		if (type == HeaterType.Unknown) {
			return null;
		} else {
			deviceList = getDb().findAllByWhere(HeaterInfo.class,
					" productKey = '" + type.pkey + "'");
			return deviceList;
		}
	}

	private int getUnknownHeaterCount() {
		List<HeaterInfo> allHeaters = getAll();
		int totalCount = 0;
		if (allHeaters != null) {
			totalCount = allHeaters.size();
		}

		List<HeaterType> allTypes = Arrays.asList(HeaterType.values());
		// allTypes.remove(HeaterType.Unknown);

		int totalKnown = 0;
		for (HeaterType type : allTypes) {
			if (type != HeaterType.Unknown) {
				totalKnown += getHeaterCountOfType(type);
			}
		}

		return totalCount - totalKnown;
	}

	public boolean nameExists(String name) {
		boolean ret = false;

		List<HeaterInfo> all = getAll();
		if (all == null) {
			return false;
		} else {
			for (HeaterInfo hinfo : all) {
				if (name.equals(hinfo.getName())) {
					return true;
				}
			}
		}

		return ret;
	}

}
