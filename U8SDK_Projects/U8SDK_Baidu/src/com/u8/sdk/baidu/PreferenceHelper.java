package com.u8.sdk.baidu;

import com.u8.sdk.log.Log;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceHelper {

    private static final String PREFERENCE = "demo";

    public static final String DEBUG = "DEBUG";
    public static final String ORIENTATION = "ORIENTATION";

    public static boolean getValue(Context context, String key) {
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......1");
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        boolean value = settings.getBoolean(key, false);
        return value;
    }

    public static void setValue(Context context, String key, boolean value) {
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......2");
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......3");
        Editor editor = settings.edit();
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......4");
        editor.putBoolean(key, value);
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......5");
        editor.commit();
    }

    public static int getIntValue(Context context, String key, int defaultValue) {
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......6");
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......7");
        int value = settings.getInt(key, defaultValue);
        return value;
    }

    public static void setIntValue(Context context, String key, int value) {
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......8");
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......9");
        Editor editor = settings.edit();
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......10");
        editor.putInt(key, value);
		Log.d("U8SDK", "bdSDK PreferenceHelper error debug......11");
        editor.commit();
    }

}
