package com.vanward.ehheater.dao;

import java.util.List;

import android.content.Context;

import com.vanward.ehheater.bean.AccountBean;
import com.vanward.ehheater.util.L;

public class AccountDao extends BaseDao {

	public AccountDao(Context context) {
		super(context);
	}

	public void saveLoginAccountIntoDatabaseForInsideLogin(String userName,
			String passcode) {
		userName = userName.trim();
		passcode = passcode.trim();
		
		AccountBean newAccount = new AccountBean(userName, passcode);
		
		AccountBean oldAccount = null;
		
		List<AccountBean> result = getDb().findAllByWhere(AccountBean.class,
				" userName = '" + userName + "'");
		
		L.e(this, "result.size() : " + result.size());
		
		if (result != null && result.size() > 0) { 
			oldAccount = result.get(0);
		}
		L.e(this, "oldAccount == null : " + (oldAccount == null));
		
		if (oldAccount != null) {
			newAccount.setId(oldAccount.getId());
			getDb().update(newAccount);
		} else {
			getDb().save(newAccount);
		}
	}
	
	public boolean isAccountHasLogined(String uid, String passcode) {
		List<AccountBean> result = getDb().findAllByWhere(AccountBean.class,
				"uid = '" + uid + "' and passcode = '" + passcode + "'");
		if (result != null && result.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
