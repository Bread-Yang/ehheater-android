package com.vanward.ehheater.dao;

import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.vanward.ehheater.bean.AccountBean;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.L;

public class HeaterInfoDao extends BaseDao {

	private static final String TAG = "HeaterInfoDao";

	public HeaterInfoDao(Context context) {
		super(context);
	}

	public List<HeaterInfo> getHeaterByUid(String uid) {
		return getDb().findAllByWhere(HeaterInfo.class, " uid = '" + uid + "'");
	}

	public HeaterInfo getHeaterByUidAndMac(String uid, String mac) {
		List<HeaterInfo> result = getDb().findAllByWhere(HeaterInfo.class,
				" mac = '" + mac + "' and uid = '" + uid + "'");

		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	public HeaterInfo getHeaterByUidAndDid(String uid, String did) {
		List<HeaterInfo> result = getDb().findAllByWhere(HeaterInfo.class,
				" did = '" + did + "' and uid = '" + uid + "'");

		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	public void save(HeaterInfo heater) {
		L.e(this, "saving heater(allow empty passcode): " + heater);
		// getDb().replace(heater);

		HeaterInfo old = getHeaterByUidAndMac(
				AccountService.getUserId(context), heater.getMac());
		if (old == null) {
			getDb().save(heater);
		} else {
			heater.setId(old.getId());
			getDb().update(heater);
		}
	}

	public List<HeaterInfo> getAllByUid(String uid) {
		return getDb().findAllByWhere(HeaterInfo.class,
				" uid = '" + uid + "' and uid = '" + uid + "'");
	}

	public int getHeaterCountOfTypeByUid(String uid, HeaterType type) {
		List<HeaterInfo> ret;

		if (type == HeaterType.Unknown) {
			return getUnknownHeaterCountByUid(uid);
		} else {
			ret = getDb()
					.findAllByWhere(
							HeaterInfo.class,
							" productKey = '" + type.pkey + "' and uid = '"
									+ uid + "'");
		}

		if (ret == null) {
			return 0;
		} else {
			return ret.size();
		}
	}

	public List<HeaterInfo> getAllDeviceOfType(String uid, HeaterType type) {
		List<HeaterInfo> deviceList;
		if (type == HeaterType.Unknown) {
			return null;
		} else {
			deviceList = getDb()
					.findAllByWhere(
							HeaterInfo.class,
							" productKey = '" + type.pkey + "' and uid = '"
									+ uid + "'");
			return deviceList;
		}
	}

	private int getUnknownHeaterCountByUid(String uid) {
		List<HeaterInfo> allHeaters = getAllByUid(uid);
		int totalCount = 0;
		if (allHeaters != null) {
			totalCount = allHeaters.size();
		}

		List<HeaterType> allTypes = Arrays.asList(HeaterType.values());
		// allTypes.remove(HeaterType.Unknown);

		int totalKnown = 0;
		for (HeaterType type : allTypes) {
			if (type != HeaterType.Unknown) {
				totalKnown += getHeaterCountOfTypeByUid(uid, type);
			}
		}

		return totalCount - totalKnown;
	}

	public boolean isDeviceNameExistsByUid(String uid, String deviceName) {
		boolean ret = false;

		List<HeaterInfo> devices = getDb().findAllByWhere(HeaterInfo.class,
				" uid = '" + uid + "'");
		if (devices == null) {
			return false;
		} else {
			for (HeaterInfo hinfo : devices) {
				if (deviceName.equals(hinfo.getName())) {
					return true;
				}
			}
		}

		return ret;
	}

}
