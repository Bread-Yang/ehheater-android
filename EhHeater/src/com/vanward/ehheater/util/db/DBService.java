package com.vanward.ehheater.util.db;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalDb.DaoConfig;
import net.tsz.afinal.FinalDb.DbUpdateListener;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBService implements DbUpdateListener {

	private Context context;
	private static FinalDb db;

	private DBService(Context context) {
		DaoConfig daoConfig = new DaoConfig();
		daoConfig.setContext(context);
		daoConfig.setDbName("vanward");
		daoConfig.setDbUpdateListener(this);
		daoConfig.setDebug(true);
		db = FinalDb.create(daoConfig);
	}

	public static FinalDb getInstance(Context context) {
		if (db == null) {
			new DBService(context);
		}
		return db;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
