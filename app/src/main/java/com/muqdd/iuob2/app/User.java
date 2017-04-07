package com.muqdd.iuob2.app;

import android.content.Context;
import android.util.Log;

import com.muqdd.iuob2.utils.SPHelper;

/**
 * Created by Ali Yusuf on 4/7/2017.
 * iUOB-2
 */

public class User {
    private final static String TAG = User.class.getSimpleName();
    private final static String NOTIFICATION = "notification";

    public static boolean setNotification(Context context, boolean state) {
        if (state) {
            SPHelper.saveToPrefs(context, NOTIFICATION, "on");
            Log.i(TAG,"notifications set to ON");
        } else {
            SPHelper.deleteFromPrefs(context,NOTIFICATION);
            Log.i(TAG,"notifications set to OFF");
        }
        return state;
    }

    public static boolean isNotificationOn(Context context) {
        return SPHelper.getFromPrefs(context,NOTIFICATION) != null;
    }
}