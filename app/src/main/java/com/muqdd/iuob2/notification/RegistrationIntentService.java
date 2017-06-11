package com.muqdd.iuob2.notification;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.muqdd.iuob2.BuildConfig;
import com.muqdd.iuob2.R;

/**
 * Created by Ali Yusuf on 4/12/2017.
 * iUOB-2
 */

public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = RegistrationIntentService.class.getSimpleName();
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String FCM_TOKEN = "FCMToken";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) { // prevent some errors
            return;
        }
        // Make a call to Instance API
        FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
        // String senderId = getResources().getString(R.string.gcm_defaultSenderId);

        // Subscribe into topics
        if (BuildConfig.DEBUG)
            FirebaseMessaging.getInstance().subscribeToTopic("debug");

        // request token that will be used by the server to send push notifications
        String token = instanceID.getToken();
        Log.d(TAG, "FCM Registration Token: " + token);

        // pass along this data
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }
}