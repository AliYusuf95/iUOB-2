package com.muqdd.iuob2.app;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.muqdd.iuob2.utils.SPHelper;

/**
 * Created by Ali Yusuf on 9/3/2017.
 * iUOB-2
 */

public class Auth {

    private transient final static String TAG = Auth.class.getSimpleName();
    private transient static final String FCM_TOKEN = "FCM_TOKEN";
    private transient static final String FCM_TOKEN_SENT = "FCM_TOKEN_SENT";
    private transient static final String LOGGED_IN_USER = "LOGGED_IN_USER";
    @SerializedName("user")
    @Expose
    private User user;

    /**
     * Adds/Replace login data from {@link User} object into SharedPreferences.
     *
     * @param context The application context
     * @param user User data
     * @return true if the add was successful; false otherwise.
     */
    public static boolean login(Context context, User user) {
        // null check
        if (user == null) {
            Log.e(TAG, "Auth#login(): Error: attempted to login with null user.");
            return false;
        }

        // Check login data
        if (user.getEmail() == null || "".equals(user.getEmail()) ||
                user.getToken() == null || "".equals(user.getToken())) {
            Log.e(TAG, "Auth#login(): Error: attempted to login with empty data.");
            return false;
        }

        SPHelper.saveToPrefs(context, LOGGED_IN_USER, user.getId());

        Log.i(TAG, "Auth#login(): Group of services with email (" + user.getEmail() + ") has been saved.");
        return true;
    }

    /**
     * Check if there is login data stored in SharedPreferences.
     *
     * @param context The application context
     * @return true if login data is saved; false otherwise.
     */
    public static boolean isLoggedIn(Context context) {
        User user = getUserData(context);
        return user != null && isValidUser(user);
    }

    /**
     * Retrieve stored user data. This method must used after {@link #isLoggedIn(Context)} to
     * avoid NULL return.
     *
     * @param context The application context
     * @return current user data if available;
     */
    public static User getUserData(Context context) {
        return User.fromJson(SPHelper.getFromPrefs(context, LOGGED_IN_USER));
    }

    /**
     * Checks whenever the passed user have required credentials or not
     *
     * @param user The application context
     * @return true if user has email and token; false otherwise
     */
    public static boolean isValidUser(User user) {
        return user != null && user.getEmail() != null && !"".equals(user.getEmail()) &&
                user.getToken() != null && !"".equals(user.getToken());
    }
}
