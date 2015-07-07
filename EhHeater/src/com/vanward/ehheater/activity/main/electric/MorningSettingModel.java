package com.vanward.ehheater.activity.main.electric;

import android.content.Context;

import com.vanward.ehheater.dao.BaseDao;

public class MorningSettingModel {

	private Context context;
	private static MorningSettingModel model;

	private MorningSettingModel(Context context) {
		this.context = context;

	}

	public static MorningSettingModel getInstance(Context context) {
		if (model == null) {
			model = new MorningSettingModel(context);
		}
		return model;
	}

	public void saveSetting(MoringSeVo moringSeVo) {
		BaseDao baseDao = new BaseDao(context);
		baseDao.getDb().replace(moringSeVo);
	}

	public MoringSeVo getSetting(String id) {
		return new BaseDao(context).getDb().findById(id, MoringSeVo.class);
	}

}
