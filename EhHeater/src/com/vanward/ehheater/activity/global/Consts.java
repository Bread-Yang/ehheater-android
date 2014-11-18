package com.vanward.ehheater.activity.global;

import com.vanward.ehheater.bean.HeaterInfo;

public class Consts {

	public static final String INTENT_FILTER_KILL_MAIN_ACTIVITY = "kill_main_activity";
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

	public static final String INTENT_EXTRA_MAC = "mac";
	public static final String INTENT_EXTRA_DID = "did";
	public static final String INTENT_EXTRA_PASSCODE = "passcode";
	public static final String INTENT_EXTRA_CONNID = "connid";
	public static final String INTENT_EXTRA_ISONLINE = "isOnline";

	public static final int REQUESTCODE_CONNECT_ACTIVITY = 1000;
	public static final int REQUESTCODE_UPLOAD_BINDING = 1002;
	public static final int REQUESTCODE_LOGIN = 1003;

	public static final String HEATER_DEFAULT_NAME = "热水器";
	public static final String E_HEATER_DEFAULT_NAME = "电热水器";
	public static final String ST_HEATER_DEFAULT_NAME = "燃气热水器";
	public static final String EH_FURNACE_DEFAULT_NAME = "万和壁挂炉";

	public static final String EH_P_KEY = "c2db7fd028fd11e4b605001ec9b6dcfe";
	public static final String ST_P_KEY = "b82d55ee2f3e11e488f7001ec9b6dcfe";
	public static final String EH_FURNACE_PRODUCT_KEY = "d787108d040f4e838d04dff28e95ea30";

	public static String getHeaterName(HeaterInfo heater) {
		return heater.getName();
	}
}
