
package com.android.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class PreferenceUtil {

    private static final String TAG = "PreferenceUtil";

    public static final String KEY_LOGIN_TYPE = "KEY_LOGIN_TYPE";

    public static final String KEY_INIT_FACEBOOK_SDK = "KEY_INIT_FACEBOOK_SDK";

//    public static final String KEY_INIT_TWITTER_SDK = "KEY_INIT_TWITTER_SDK";

    public static final String KEY_INIT_GOOGLE_SDK = "KEY_INIT_GOOGLE_SDK";

//    public static final String KEY_INIT_YAHOO_SDK = "KEY_INIT_YAHOO_SDK";

//    public final static String KEY_YAHOO_ACCESS_TOKEN = "KEY_YAHOO_ACCESS_TOKEN";

//    public final static String KEY_YAHOO_REFRESH_TOKEN = "KEY_YAHOO_REFRESH_TOKEN";

    public final static String KEY_NONCE = "KEY_NONCE";

    public final static String KEY_STATE = "KEY_STATE";

//    public final static String KEY_YAHOO_URL_CALLBACK = "KEY_YAHOO_URL_CALLBACK";

    public static boolean getBoolean(Context con, String key, boolean defValue) {
        try {
            return pref(con).getBoolean(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static int getInt(Context con, String key, int defValue) {
        try {
            return pref(con).getInt(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static String getString(Context con, String key, String defValue) {
        try {
            return pref(con).getString(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static long getLong(Context con, String key, long defValue) {
        try {
            return pref(con).getLong(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static float getFloat(Context con, String key, float defValue) {
        try {
            return pref(con).getFloat(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static Set<String> getStringSet(Context con, String key, Set<String> defValue) {
        try {
            HashSet<String> ret = null;
            Set<String> set = pref(con).getStringSet(key, null);
            if (set != null) {
                ret = new HashSet<String>(set);
            }
            return ret;
        } catch (Exception e) {
            return defValue;
        }
    }

    public static void writeString(Context con, String key, String value) {
        try {
            pref(con).edit().putString(key, value).commit();
        } catch (Exception e) {
        }
    }

    public static void writeBoolean(Context con, String key, boolean value) {
        try {
            pref(con).edit().putBoolean(key, value).commit();
        } catch (Exception e) {
        }
    }

    public static void writeInt(Context con, String key, int value) {
        try {
            pref(con).edit().putInt(key, value).commit();
        } catch (Exception e) {
        }
    }

    public static void writeLong(Context con, String key, long value) {
        try {
            pref(con).edit().putLong(key, value).commit();
        } catch (Exception e) {
        }
    }

    public static void writeFloat(Context con, String key, float value) {
        try {
            pref(con).edit().putFloat(key, value).commit();
        } catch (Exception e) {
        }
    }

    public static void writeSet(Context con, String key, Set<String> set) {
        try {
            pref(con).edit().putStringSet(key, set).commit();
        } catch (Exception e) {
        }
    }

    public static void remove(Context con, String key) {
        try {
            pref(con).edit().remove(key).commit();
        } catch (Exception e) {
        }
    }

    private static SharedPreferences pref(Context con) {
        return PreferenceManager.getDefaultSharedPreferences(con);
    }
}
