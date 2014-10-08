package com.vanward.ehheater.dao;

import com.vanward.ehheater.util.db.DBService;

import net.tsz.afinal.FinalDb;
import android.content.Context;


public class BaseDao {

	protected Context context;
	private FinalDb db;

	public FinalDb getDb() {
		return db;
	}

	public BaseDao(Context context) {
		db = DBService.getInstance(context);
		this.context = context;
	}
	
}
