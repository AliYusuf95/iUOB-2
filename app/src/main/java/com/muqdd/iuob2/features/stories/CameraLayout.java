package com.muqdd.iuob2.features.stories;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.muqdd.iuob2.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created by Ali Yusuf on 9/4/2017.
 * iUOB-2
 */

public class CameraLayout extends RelativeLayout {

    private static final String TAG = CameraLayout.class.getSimpleName();

    private CameraPreview mPreview;
    private ProgressButton mButton;
    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;
    private boolean safeToTakePicture = false;
    private PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = Utils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            mCamera.startPreview();
            if (pictureFile == null){
                //no path to picture, return
                safeToTakePicture = true;
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

                // change image orientation
                realImage = Utils.bitmapRotate(realImage, 90);
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                //fos.write(data);
                fos.close();
                Log.d(TAG, "File saved");
            } catch (FileNotFoundException e) {
                Log.w(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.w(TAG, "Error accessing file: " + e.getMessage());
            }

            //finished saving picture
            safeToTakePicture = true;
        }
    };

    public CameraLayout(Context context) {
        this(context, null);
    }

    public CameraLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Initialise the {@link CameraLayout}.
     *  @param context the application environment
     * @param attrs Attribute Set provided
     */
    private void init(Context context, AttributeSet attrs) {
        Resources r = context.getResources();
        DisplayMetrics m = r.getDisplayMetrics();

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCamera = getCameraInstance();
            if (mCamera != null){
                mCamera.setDisplayOrientation(90);
            }
        }

        LayoutParams parentParams =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        setLayoutParams(parentParams);

        // init camera preview
        LayoutParams camViewParams =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        mPreview = new CameraPreview(getContext(), mCamera);
        mPreview.setLayoutParams(camViewParams);
        addView(mPreview);


        LayoutParams btnParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        btnParams.bottomMargin = (int) Utils.dpToPx(getContext(), 20);
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        btnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        mButton = new ProgressButton(getContext());
        mButton.setLayoutParams(btnParams);
        mButton.setGravity(Gravity.CENTER_HORIZONTAL);
        mButton.setPadding(0,0,0,(int) Utils.dpToPx(getContext(), 10));
        mButton.setMax(100);
        mButton.setInnerSize((int) Utils.dpToPx(getContext(), 50));
        mButton.setProgressSpace((int) Utils.dpToPx(getContext(), 15));
        mButton.setProgressWidth((int) Utils.dpToPx(getContext(), 3));
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCamera != null && safeToTakePicture) {
                    // get an image from the camera
                    mCamera.takePicture(null, null, mPicture);
                    safeToTakePicture = false;
                }
            }
        });
        addView(mButton);
    }


    private Camera getCameraInstance(){
        Camera c = Camera.open(); // attempt to get a Camera instance
        if (c == null){
            int cameraId = -1;
            // Search for the front facing camera
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(i, mCameraInfo);
                if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                    Log.d(TAG, "Camera found");
                    cameraId = i;
                    break;
                }
            }
            c = Camera.open(cameraId);
        }
        return c; // returns null if camera is unavailable
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                safeToTakePicture = true;
            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }
}
