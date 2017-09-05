package com.muqdd.iuob2.features.stories;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.muqdd.iuob2.app.BaseActivity;

/**
 * Created by Ali Yusuf on 9/5/2017.
 * iUOB-2
 */

public class StoriesActivity extends BaseActivity {

    private static final String TAG = StoriesActivity.class.getSimpleName();

    private final int PERMISSION_REQUEST_CAMERA = 1;
    private final int PERMISSION_REQUEST_STORAGE = 2;
    private final int PERMISSION_REQUEST_LOCATION = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
        setContentView(new CameraLayout(this));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    setContentView(new CameraLayout(this));
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void checkPermmistions() {

    }
}
