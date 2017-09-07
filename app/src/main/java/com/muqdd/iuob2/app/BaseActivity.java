package com.muqdd.iuob2.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikepenz.materialdrawer.Drawer;
import com.muqdd.iuob2.BuildConfig;
import com.muqdd.iuob2.features.stories.CameraLayout;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.notification.RegistrationIntentService;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ali Yusuf on 3/10/2017.
 * iUOB-2
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9999;

    protected Drawer drawerMenu;
    protected OnBackPressedListener mOnBackPressedListener;

    private Map<Integer, PermissionRequest> permissionsRequests;

    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServiceGenerator.init(this);
        // register FCM token
        if(checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        permissionsRequests = new HashMap<>();
    }

    public void sendAnalyticTracker(@StringRes int screenName) {
        sendAnalyticTracker(getString(screenName));
    }

    public void sendAnalyticTracker(String screenName) {
        // deniable google analytic in debug
        GoogleAnalytics.getInstance(this).setDryRun(BuildConfig.DEBUG);
        // Send analytic event
        Tracker tracker = ((iUobApplication) this.getApplication()).getDefaultTracker();
        tracker.setScreenName(screenName);
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

    protected boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED ;
    }

    protected void requestPermission(String permission, @NonNull PermissionCallback callBack) {
        int requestCode = permissionsRequests.size();
        PermissionRequest request = new PermissionRequest(requestCode, permission, callBack);
        permissionsRequests.put(requestCode, request);
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    protected void requestPermission(String[] permissions, @NonNull PermissionCallback callBack) {
        int requestCode = permissionsRequests.size();
        PermissionRequest request = new PermissionRequest(requestCode, permissions, callBack);
        permissionsRequests.put(requestCode, request);
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (permissionsRequests.keySet().contains(requestCode)) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                PermissionCallback callback = permissionsRequests.get(requestCode).getCallBack();
                if (callback != null) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        callback.onGranted(permission);
                    } else {
                        callback.onDenied(permission);
                    }
                }
            }
        }
    }

    public interface OnBackPressedListener{
        /**
         * @return return true to prevent default onBackPressed; false otherwise
         */
        boolean onBack();
    }

    /**
     * Replace current fragment with another one
     *
     * @param fragment Fragment instance
     */
    public void replaceFragment(Fragment fragment){}

    /**
     * Display fragment and add the old one to the back stack
     *
     * @param fragment Fragment instance
     */
    public void displayFragment(Fragment fragment){}
}
