package com.muqdd.iuob2.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.util.Log;

/**
 * Shared Preferences Helper {@link android.content.SharedPreferences}
 *
 * Created by Ali Yusuf on 4/7/2017.
 * iUOB-2
 */

@SuppressLint("ApplySharedPref")
public class SPHelper {
    private static final String TAG = SPHelper.class.getSimpleName();

    /**
     * Saves the key and value to SharedPreferences.
     *
     * @param context: The application context
     * @param key: Key to save to SharedPreferences
     * @param value: Value to save to SharedPreferences
     */
    public static void saveToPrefs(Context context,@NonNull String key, String value) {
        if (context != null) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(key, value);
            editor.commit();
        } else {
            Log.e(TAG,"Null context @ saveToPrefs");
        }
    }

    /**
     * Gets the value in Shared Preferences with the specified key. Null if the key does not exist.
     *
     * @param context: The application context
     * @param key: Key to look up in Shared Preferences
     */
    public static String getFromPrefs(Context context,@NonNull String key) {
        if (context != null) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPrefs.getString(key, null);
        } else {
            Log.e(TAG,"Null context @ getFromPrefs");
            return null;
        }
    }

    /**
     * Deletes the key-value pair in Shared Preferences with the specified key.
     *
     * @param context The application context
     * @param key Key to look up in Shared Preferences
     * @return true if deletion was successful; false otherwise
     */
    public static boolean deleteFromPrefs(Context context,@NonNull String key) {
        if (context != null) {
            if (getFromPrefs(context, key) == null)
                return false;
            PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).commit();
            return true;
        } else {
            Log.e(TAG,"Null context @ deleteFromPrefs");
            return false;
        }
    }
}
