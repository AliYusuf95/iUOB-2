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
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.muqdd.iuob2.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * Created by Ali Yusuf on 9/4/2017.
 * iUOB-2
 */

public class CameraLayout extends RelativeLayout implements ProgressButton.EventListener {

    private static final String TAG = CameraLayout.class.getSimpleName();

    private CameraPreview mPreview;
    private VideoView mVideoView;
    private ImageView mImageView;
    private ProgressButton mButton;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;

    /* Results Uri */
    private Uri videoResult;
    private Uri pictureResult;

    /* Listeners */
    private OnRecordListener onRecordListener;
    private OnCaptureListener onCaptureListener;

    /* Flags */
    private boolean safeToTakePicture = false;
    private boolean isRecording = false;

    private PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            pictureResult = Utils.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            mCamera.startPreview();
            if (pictureResult == null){
                //no path to picture, return
                safeToTakePicture = true;
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureResult.getPath());
                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

                // change image orientation
                realImage = Utils.bitmapRotate(realImage, 90);
                realImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                //fos.write(data);
                fos.close();

//                Canvas bitmapCanvas = new Canvas();
//                realImage = Bitmap.createBitmap(mPreview.getWidth(), mPreview.getHeight(), Bitmap.Config.ARGB_8888);
//                bitmapCanvas.setBitmap(realImage);
//                bitmapCanvas.scale(2.0f, 2.0f);
//                mPreview.draw(bitmapCanvas);
//                realImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

                if (onCaptureListener != null) {
                    onCaptureListener.onCapture(realImage, pictureResult);
                }
//                showImageView(realImage);

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
                Camera.Parameters params = mCamera.getParameters();
                List<String> focusModes = params.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                mCamera.setParameters(params);
            }
        }

        LayoutParams parentParams =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        parentParams.addRule(CENTER_IN_PARENT);
        setLayoutParams(parentParams);

        // init VideoView
        mVideoView = new VideoView(getContext());
        mVideoView.setLayoutParams(parentParams);
        mVideoView.setVisibility(GONE);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mVideoView.start();
            }
        });
        addView(mVideoView);

        mImageView = new ImageView(getContext());
        mImageView.setLayoutParams(parentParams);
        mImageView.setVisibility(GONE);
        addView(mImageView);

        // init camera preview
        addCameraView();

        // init button
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
        mButton.setMax(200);
        mButton.setInnerSize((int) Utils.dpToPx(getContext(), 50));
        mButton.setProgressSpace((int) Utils.dpToPx(getContext(), 15));
        mButton.setProgressWidth((int) Utils.dpToPx(getContext(), 3));
        mButton.setEventListener(this);
        addView(mButton);
    }

    /**
     * Implement Progress Button listeners {@link ProgressButton.EventListener}
     */
    @Override
    public void onClick(View view) {
        if (mCamera != null && safeToTakePicture) {
            // get an image from the camera
            mCamera.takePicture(null, null, mPicture);
            safeToTakePicture = false;
        }
    }

    @Override
    public void onLongClick(View view) {
        if (safeToTakePicture){
            // initialize video camera
            mButton.stopAnimating();
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();
                mButton.startAnimating();
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
    }

    @Override
    public void onLongClickUp(View view) {
        if (isRecording) {
            // stop recording and release camera
            try {
                mMediaRecorder.stop();  // stop the recording
            } catch (RuntimeException ignored) {
                //handle cleanup here
            }
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            isRecording = false;
            if (onRecordListener != null) {
                onRecordListener.onRecord(videoResult);
            }
//            showVideoView();
        }
    }

    @Override
    public void onProgress(View view, int progress) {

    }

    @Override
    public void onProgressStop(View view, int progress) {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            isRecording = false;
            if (onRecordListener != null) {
                onRecordListener.onRecord(videoResult);
            }
//            showVideoView();
        }
    }

    private void addCameraView() {
        if (mPreview != null){
            removeView(mPreview);
        }
        LayoutParams camViewParams =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        mPreview = new CameraPreview(getContext(), mCamera);
        mPreview.setLayoutParams(camViewParams);
        addView(mPreview, 0);
    }

    private Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            if (c == null){
                int cameraId = -1;
                // Search for the front facing camera
                int numberOfCameras = Camera.getNumberOfCameras();
                for (int i = 0; i < numberOfCameras; i++) {
                    CameraInfo info = new CameraInfo();
                    Camera.getCameraInfo(i, info);
                    if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                        Log.d(TAG, "Camera found");
                        cameraId = i;
                        break;
                    }
                }
                c = Camera.open(cameraId);
            }
        } catch (Exception ignored) {

        }
        return c; // returns null if camera is unavailable
    }

    private boolean prepareVideoRecorder(){
        mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setMaxDuration(10000);

        // Step 3: Set a CamcorderProfile
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        videoResult = Utils.getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        if (videoResult != null) {
            mMediaRecorder.setOutputFile(videoResult.getPath());
        } else {
            Log.d(TAG, "Can't create file");
            videoResult = null;
            releaseMediaRecorder();
            return false;
        }

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void showImageView(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
        mPreview.setVisibility(INVISIBLE);
        mVideoView.setVisibility(GONE);
        mImageView.setVisibility(VISIBLE);
        mImageView.bringToFront();
        invalidate();
    }

    private void showVideoView() {
        Log.d(TAG, (videoResult != null)+" - "+!isRecording);
        if (!isRecording && videoResult != null) {
            mVideoView.setVideoURI(videoResult);
            mVideoView.setMediaController(null);
            mVideoView.requestFocus();

            mPreview.setVisibility(INVISIBLE);
            mVideoView.setVisibility(VISIBLE);
            mImageView.setVisibility(GONE);
            mVideoView.bringToFront();
            invalidate();
        }
    }

    public void hideViews() {
        mImageView.setImageBitmap(null);
        mVideoView.setVisibility(GONE);
        mImageView.setVisibility(GONE);
        startCameraPreview();
    }

    public void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mButton.stopAnimating();
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    public void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    public void startCameraPreview(){
        mCamera = getCameraInstance();
        addCameraView();
        if (mCamera != null){
            mCamera.setDisplayOrientation(90);
            Camera.Parameters params = mCamera.getParameters();
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            mCamera.setParameters(params);
            mCamera.startPreview();
            safeToTakePicture = true;
        }
    }

    public OnRecordListener getOnRecordListener() {
        return onRecordListener;
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    public OnCaptureListener getOnCaptureListener() {
        return onCaptureListener;
    }

    public void setOnCaptureListener(OnCaptureListener onCaptureListener) {
        this.onCaptureListener = onCaptureListener;
    }

    public boolean isCaptureImages() {
        return mButton.isWithClick();
    }

    public void withCaptureImages(boolean captureImages) {
        mButton.setWithClick(captureImages);
    }

    public boolean isRecordVideo() {
        return mButton.isWithLongClick();
    }

    public void withRecordVideo(boolean recordVideo) {
        mButton.setWithLongClick(recordVideo);
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

    interface OnCaptureListener {
        void onCapture(Bitmap bitmap, Uri resultFile);
    }

    interface OnRecordListener {
        void onRecord(Uri resultFile);
    }
}
