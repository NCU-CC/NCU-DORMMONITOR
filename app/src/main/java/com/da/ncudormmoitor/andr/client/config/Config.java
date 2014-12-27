package com.da.ncudormmoitor.andr.client.config;

public class Config {
	public final static String DORM_URL = "https://uncia.cc.ncu.edu.tw/dormnet/index.php?section=netflow&ip=";
	// test
	public final static boolean isNotifiy = true;
	public final static String IP_NUM = "140.115.204.179";
	public final static String IP_NUM_LOCK = "140.115.231.4";
	public final static int predictInterval = 1;
	public final static float Maxupload = (float) 0.5;

	// state
	public final static String STATE_ = "UserPrefs";

	// preference name
	public final static String PREFS_NAME = "UserPrefs";
	// preference key
	public final static String PREF_IP = "PREF_IP";
	public final static String PREF_UPLOAD = "PREF_UPLOAD";
	public final static String PREF_DOWNLOAD = "PREF_DOWNLOAD";
	public final static String PREF_UPLOAD_ALL = "PREF_UPLOAD_ALL";
	public final static String PREF_DOWNLOAD_ALL = "PREF_DOWNLOAD_ALL";
	public final static String PREF_UpperBoundary = "PREF_UpperBoundary";
	public final static String PREF_Interval = "PREF_Interval";
	public final static String PREF_NotificationTurnOn = "PREF_NotificationTurnOn";
	public final static String PREF_LAST_UPDATE_TIME = "PREF_LAST_UPDATE_TIME";
	// preference key for MainActivity
	public final static String PREF_MAIN_FLOWS = "PREF_MAIN_FLOWS";
	public final static String PREF_MAIN_FLOWS_DATAIL = "PREF_MAIN_FLOWS_DATAIL";
	public final static String PREF_MAIN_STATE = "PREF_MAIN_STATE";
	public final static String PREF_MAIN_STATE_COLOR = "PREF_MAIN_STATE_COLOR";

	// filter's key
	public final static String FILTER_MAIN = "FILTER_MAIN";
	public final static String FILTER_DETAIL = "FILTER_DETAIL";

	// intent's key
	public final static String INTENT_FLOW = "INTENT_FLOW";
	public final static String INTENT_DOWNLOAD_ALL = "INTENT_DOWNLOAD_ALL";
	public final static String INTENT_DOWNLOAD = "INTENT_DOWNLOAD";
	public final static String INTENT_UPLOAD_ALL = "INTENT_UPLOAD_ALL";
	public final static String INTENT_UPLOAD = "INTENT_UPLOAD";
	public final static String INTENT_SAFETY = "INTENT_SAFETY";
	public final static String INTENT_LOCK_MSG = "INTENT_LOCK_MSG";
	public final static String INTENT_MAIN_TO_DETAIL = "INTENT_MAIN_TO_DETAIL";
	public final static String INTENT_SETTINGS_TO_MAIN = "INTENT_SETTINGS_TO_MAIN";
	public final static String INTENT_CHANGE_STATE = "INTENT_CHANGE_STATE";
	// state
	public final static String INTENT_STATE_SAFE = "INTENT_STATE_SAFE";
	public final static String INTENT_STATE_DANGEROUS = "INTENT_STATE_DANGEROUS";
	public final static String INTENT_STATE_LOCK = "INTENT_STATE_LOCK";
	//

	public final static int DELAY = 30 * 1000;

	public final static int UPDATE_DELAY = 10 * 60 * 1000;

}
