package com.muqdd.iuob2.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikepenz.materialdrawer.Drawer;
import com.muqdd.iuob2.BuildConfig;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.notification.RegistrationIntentService;
import com.orhanobut.logger.Logger;

/**
 * Created by Ali Yusuf on 3/10/2017.
 * iUOB-2
 */

public class BaseActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 123;

    protected Drawer drawerMenu;
    protected OnBackPressedListener mOnBackPressedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServiceGenerator.init(this);
        // register FCM token
        if(checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        // deniable google analytic in debug
        GoogleAnalytics.getInstance(this).setDryRun(BuildConfig.DEBUG);
        // Send analytic event
        Tracker tracker = ((iUobApplication) this.getApplication()).getDefaultTracker();
        tracker.setScreenName(getString(R.string.app_name));
        // Send a screen view.
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.mOnBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (mOnBackPressedListener == null || !mOnBackPressedListener.onBack())
            super.onBackPressed();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Logger.w("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    public interface OnBackPressedListener{
        /**
         * @return true to prevent default onBackPressed; false otherwise
         */
        boolean onBack();
    }
}
