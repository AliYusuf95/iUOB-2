package com.muqdd.iuob2.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.materialdrawer.Drawer;
import com.muqdd.iuob2.BuildConfig;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * Created by Ali Yusuf on 3/10/2017.
 * iUOB-2
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9999;

    protected Drawer drawerMenu;
    protected OnBackPressedListener mOnBackPressedListener;

    private Map<Integer, PermissionRequest> permissionsRequests;

    // User location
    protected FusedLocationProviderClient mFusedLocationClient;
    protected Location userLocation;
    private OnFailureListener onFailureListener = e -> {
        int statusCode = ((ApiException) e).getStatusCode();
        switch (statusCode) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Logger.i("Location settings are not satisfied. Attempting to upgrade " +
                        "location settings ");
                isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                String errorMessage = "Location settings are inadequate, and cannot be " +
                        "fixed here. Fix in Settings.";
                Logger.e(errorMessage);
                Toast.makeText(BaseActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        }
    };
    protected FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialAd;

    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServiceGenerator.init(this);
        // register FCM token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Logger.e("getInstanceId failed", task.getException());
                return;
            }

            // Get new FCM registration token
            String token = task.getResult();
            Logger.d("FCM Registration Token: " + token);
        });
        permissionsRequests = new HashMap<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, initializationStatus -> {
            // Create the InterstitialAd and set the adUnitId.
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(this, getString(R.string.google_admob_add_course_unit_id), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            });
        });
    }

    final public void showInterstitial() {
        // Show the ad if it's ready.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(BaseActivity.this);
        }
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

    protected boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission(String permission, @Nullable PermissionCallback callBack) {
        int requestCode = permissionsRequests.size();
        PermissionRequest request = new PermissionRequest(requestCode, permission, callBack);
        permissionsRequests.put(requestCode, request);
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    protected void requestPermission(String[] permissions, @Nullable PermissionCallback callBack) {
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

    @SuppressWarnings("MissingPermission")
    public void requestLocationUpdates(@NonNull LocationCallback locationCallback) {
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            mFusedLocationClient.requestLocationUpdates(
                    new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
                    locationCallback, Looper.myLooper())
                    .addOnFailureListener(onFailureListener);
        } else {
            userLocation = null;
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, null);
        }
    }

    @SuppressWarnings("MissingPermission")
    public void getLastLocation(@NonNull OnSuccessListener<Location> onSuccessListener) {
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        } else {
            userLocation = null;
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, null);
        }
    }

    public interface OnBackPressedListener {
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
    public void replaceFragment(Fragment fragment) {
    }

    /**
     * Display fragment and add the old one to the back stack
     *
     * @param fragment Fragment instance
     */
    public void displayFragment(Fragment fragment) {
    }
}
