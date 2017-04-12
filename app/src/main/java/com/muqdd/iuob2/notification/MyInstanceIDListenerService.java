package com.muqdd.iuob2.notification;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Ali Yusuf on 4/12/2017.
 * iUOB-2
 */

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify of changes
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
