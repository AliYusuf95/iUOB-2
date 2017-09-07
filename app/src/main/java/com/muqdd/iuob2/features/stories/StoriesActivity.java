package com.muqdd.iuob2.features.stories;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.muqdd.iuob2.app.BaseActivity;
import com.muqdd.iuob2.app.PermissionCallback;

/**
 * Created by Ali Yusuf on 9/5/2017.
 * iUOB-2
 */

public class StoriesActivity extends BaseActivity {

    private static final String TAG = StoriesActivity.class.getSimpleName();

    private CameraLayout mCameraLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasAllPermissions()) {
            mCameraLayout = new CameraLayout(this);
            setContentView(mCameraLayout);
        } else {
            requestPermissions();
        }

        mCameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraLayout.hideViews();
            }
        });
    }

    public void requestPermissions() {
        PermissionCallback callback = new PermissionCallback() {
            @Override
            public void onGranted(String permission) {
                if (hasAllPermissions()) {
                    Log.d(TAG, "All permission granted");
                    mCameraLayout = new CameraLayout(StoriesActivity.this);
                    setContentView(mCameraLayout);
                }
            }

            @Override
            public void onDenied(String permission) {
                Log.d(TAG, "Permission request has been denied: "+permission);
            }
        };

        if (!isPermissionGranted(Manifest.permission.CAMERA)) {
            requestPermission(Manifest.permission.CAMERA, callback);
        }

        if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, callback);
        }

        if (!isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            requestPermission(Manifest.permission.RECORD_AUDIO, callback);
        }
    }

    private boolean hasAllPermissions() {
        return isPermissionGranted(Manifest.permission.CAMERA) &&
                isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                isPermissionGranted(Manifest.permission.RECORD_AUDIO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraLayout != null) {
            mCameraLayout.startCameraPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraLayout != null) {
            mCameraLayout.releaseMediaRecorder();       // if you are using MediaRecorder, release it first
            mCameraLayout.releaseCamera();              // release the camera immediately on pause event
        }
    }
}
