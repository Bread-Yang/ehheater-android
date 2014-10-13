package com.vanward.ehheater.activity.global;

import android.text.TextUtils;

import com.vanward.ehheater.bean.HeaterInfo;

public class Consts {

	public static final String INTENT_FILTER_KILL_LOGIN_ACTIVITY = "kill_login_activity";
	public static final String INTENT_FILTER_KILL_CONFIGURE_ACTIVITY = "kill_configure_activity";
	public static final String INTENT_FILTER_KILL_AUTO_CONFIGURE_FAIL_ACTIVITY = "kill_auto_conf_fail";
	public static final String INTENT_FILTER_HEATER_NAME_CHANGED = "heater_name_changed";

	public static final String INTENT_EXTRA_FLAG_REENTER = "reenter";
	public static final String INTENT_EXTRA_FLAG_AS_DIALOG = "as_dialog";

	public static final String INTENT_EXTRA_USERNAME = "username";
	public static final String INTENT_EXTRA_USERPSW = "userpsw";
	public static final String INTENT_EXTRA_DID2BIND = "did2bind";
	public static final String INTENT_EXTRA_PASSCODE2BIND = "passcode2bind";
	public static final String INTENT_EXTRA_CONFIGURE_ACTIVITY_SHOULD_KILL_PROCESS_WHEN_FINISH = "configure_activity_should_kill_process";

	public static final String HEATER_DEFAULT_NAME = "热水器";

	public static String getHeaterName(HeaterInfo heater) {
		String name = heater.getName();
		if (TextUtils.isEmpty(name)) {
			if (TextUtils.isEmpty(heater.getProductKey())) {
				name = "燃" + Consts.HEATER_DEFAULT_NAME + heater.getId();
			} else if (heater.getProductKey().equals(
					"c2db7fd028fd11e4b605001ec9b6dcfe")) {
				name = "电" + Consts.HEATER_DEFAULT_NAME + heater.getId();
			} else if (heater.getProductKey().equals(
					"b82d55ee2f3e11e488f7001ec9b6dcfe")) {
				name = "燃" + Consts.HEATER_DEFAULT_NAME + heater.getId();
			}
		}
		return name;
	}
}
