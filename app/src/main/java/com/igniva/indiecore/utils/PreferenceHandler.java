package com.igniva.indiecore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Common PrefrenceConnector class for storing preference values.
 * 
 */
public class PreferenceHandler {

	public static final String PREF_NAME = "APPFRAMEWORK_PREFERENCES";
	public static final int MODE = Context.MODE_PRIVATE;
	public static final String PREF_KEY_LOGIN = "PREF_KEY_LOGIN";
	public static final String PREF_KEY_USER_ID = "PREF_KEY_USER_ID";
	public static final String PREF_KEY_USER_TOKEN="PREF_KEY_USER_TOKEN";
	public static final String PREF_KEY_FIRST_NAME = "PREF_KEY_FIRST_NAME";
	public static final String PREF_KEY_LAST_NAME = "PREF_KEY_LAST_NAME";
	public static final String PREF_KEY_DOB = "PREF_KEY_DOB";
	public static final String PREF_KEY_DESCRIPTION = "PREF_KEY_DESCRIPTION";
	public static final String PREF_KEY_GENDER = "PREF_KEy_GENDER";

	public static final String PREF_KEY_USER_MOBILE = "PREF_KEY_USER_MOBILE";
	public static final String PREF_KEY_GCMID = "PREF_KEY_GCMID";
	public static  final String PROFILE_PIC_URL="PROFILE_PIC_URL";
	public static  final String COVER_PIC_URL="COVER_PIC_URL";

	public static void writeBoolean(Context context, String key, boolean value) {
		getEditor(context).putBoolean(key, value).commit();
	}

	public static boolean readBoolean(Context context, String key,
			boolean defValue) {
		return getPreferences(context).getBoolean(key, defValue);
	}

	public static void writeInteger(Context context, String key, int value) {
		getEditor(context).putInt(key, value).commit();
	}

	public static int readInteger(Context context, String key, int defValue) {
		return getPreferences(context).getInt(key, defValue);
	}

	public static void writeString(Context context, String key, String value) {
		getEditor(context).putString(key, value).commit();
	}

	public static String readString(Context context, String key, String defValue) {
		return getPreferences(context).getString(key, defValue);
	}

	public static void writeFloat(Context context, String key, float value) {
		getEditor(context).putFloat(key, value).commit();
	}

	public static float readFloat(Context context, String key, float defValue) {
		return getPreferences(context).getFloat(key, defValue);
	}

	public static void writeLong(Context context, String key, long value) {
		getEditor(context).putLong(key, value).commit();
	}

	public static long readLong(Context context, String key, long defValue) {
		return getPreferences(context).getLong(key, defValue);
	}

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREF_NAME, MODE);
	}

	public static Editor getEditor(Context context) {
		return getPreferences(context).edit();
	}
}
