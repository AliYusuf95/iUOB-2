package com.muqdd.iuob2.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;
import com.muqdd.iuob2.BuildConfig;
import com.muqdd.iuob2.R;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by Ali Yusuf on 3/10/2017.
 * iUOB-2
 */

public class iUobApplication extends Application {
    private Tracker mTracker;
    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("FieldCanBeLocal")
    private static AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        MobileAds.initialize(this, initializationStatus -> {});
        appOpenManager = new AppOpenManager(this);

        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(new AndroidLogAdapter());
            // disable Firebase crashes
            Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
                Log.wtf("Crash", paramThrowable.getMessage(), paramThrowable);
                //System.exit(2); //Prevents the service/app from freezing
            });
        }
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}