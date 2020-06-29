package com.muqdd.iuob2.features.stories;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahmedadeltito.photoeditorsdk.BrushDrawingView;
import com.ahmedadeltito.photoeditorsdk.OnPhotoEditorSDKListener;
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK;
import com.ahmedadeltito.photoeditorsdk.ViewType;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseActivity;
import com.muqdd.iuob2.app.PermissionCallback;
import com.muqdd.iuob2.vendor.photoEditSDK.ColorPickerAdapter;
import com.muqdd.iuob2.vendor.photoEditSDK.EmojiFragment;
import com.muqdd.iuob2.vendor.photoEditSDK.ImageFragment;
import com.muqdd.iuob2.vendor.photoEditSDK.widget.SlidingUpPanelLayout;
import com.viewpagerindicator.PageIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 9/5/2017.
 * iUOB-2
 */

public class CaptureActivity extends BaseActivity implements View.OnClickListener, OnPhotoEditorSDKListener,
        CameraLayout.OnCaptureListener, CameraLayout.OnRecordListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    public static final String RESULT_PATH = "RESULT_PATH";

    /** Activity required permissions */
    private List<String> requiredPermissions = Arrays.asList(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    );
    private CameraLayout mCameraLayout;

    // Views
    @BindView(R.id.parent_image_rl)
    protected RelativeLayout parentImageRelativeLayout;
    @BindView(R.id.drawing_view_color_picker_recycler_view)
    protected RecyclerView drawingViewColorPickerRecyclerView;
    @BindView(R.id.undo_tv)
    protected TextView undoTextView;
    @BindView(R.id.undo_text_tv)
    protected TextView undoTextTextView;
    @BindView(R.id.done_drawing_tv)
    protected TextView doneDrawingTextView;
    @BindView(R.id.erase_drawing_tv)
    protected TextView eraseDrawingTextView;
    @BindView(R.id.sliding_layout)
    protected SlidingUpPanelLayout mLayout;
    @BindView(R.id.top_shadow)
    protected View topShadow;
    @BindView(R.id.top_parent_rl)
    protected RelativeLayout topShadowRelativeLayout;
    @BindView(R.id.bottom_shadow)
    protected View bottomShadow;
    @BindView(R.id.bottom_parent_rl)
    protected RelativeLayout bottomShadowRelativeLayout;

    protected Typeface emojiFont;
    private ArrayList<Integer> colorPickerColors;
    private int colorCodeTextView = -1;
    private PhotoEditorSDK photoEditorSDK;
    private PopupWindow pop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasAllPermissions()) {
            setContentView(renewCameraLayout());
        } else {
            requestPermissions();
        }

//        mStoriesLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mStoriesLayout.hideViews();
//            }
//        });

    }

    public void requestPermissions() {
        PermissionCallback callback = new PermissionCallback() {
            @Override
            public void onGranted(String permission) {
                if (hasAllPermissions()) {
                    Log.d(TAG, "All permission granted");
                    setContentView(renewCameraLayout());
                } else {
                    requestPermissions();
                }
            }

            @Override
            public void onDenied(String permission) {
                requiredPermissions.remove(permission);
                Log.d(TAG, "Permission request has been denied: "+permission);
            }
        };

        // Request needed permissions
        for (String permission : requiredPermissions) {
            if (!isPermissionGranted(permission)) {
                requestPermission(permission, callback);
            }
        }
    }

    private boolean hasAllPermissions() {
        for (String permission : requiredPermissions) {
            if (!isPermissionGranted(permission)) {
                return false;
            }
        }
        return true;
    }

    private CameraLayout renewCameraLayout() {
        mCameraLayout = new CameraLayout(CaptureActivity.this);
        mCameraLayout.withRecordVideo(false);
        mCameraLayout.setOnCaptureListener(this);
        mCameraLayout.setOnRecordListener(this);
        return mCameraLayout;
    }

    private void initPhotoEditor(String selectedImagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

        Typeface newFont = Typeface.createFromAsset(getAssets(), "Eventtus-Icons.ttf");
        emojiFont = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        BrushDrawingView brushDrawingView = findViewById(R.id.drawing_view);
        TextView closeTextView = findViewById(R.id.close_tv);
        TextView addTextView = findViewById(R.id.add_text_tv);
        TextView addPencil = findViewById(R.id.add_pencil_tv);
        RelativeLayout deleteRelativeLayout = findViewById(R.id.delete_rl);
        TextView deleteTextView = findViewById(R.id.delete_tv);
        TextView addImageEmojiTextView = findViewById(R.id.add_image_emoji_tv);
        TextView saveTextView = findViewById(R.id.save_tv);
        TextView saveTextTextView = findViewById(R.id.save_text_tv);
        TextView clearAllTextView = findViewById(R.id.clear_all_tv);
        TextView clearAllTextTextView = findViewById(R.id.clear_all_text_tv);
        TextView goToNextTextView = findViewById(R.id.go_to_next_screen_tv);
        ImageView photoEditImageView = findViewById(R.id.photo_edit_iv);

        ViewPager pager = findViewById(R.id.image_emoji_view_pager);
        PageIndicator indicator = findViewById(R.id.image_emoji_indicator);

        photoEditImageView.setImageBitmap(bitmap);

        closeTextView.setTypeface(newFont);
        addTextView.setTypeface(newFont);
        addPencil.setTypeface(newFont);
        addImageEmojiTextView.setTypeface(newFont);
        saveTextView.setTypeface(newFont);
        undoTextView.setTypeface(newFont);
        clearAllTextView.setTypeface(newFont);
        goToNextTextView.setTypeface(newFont);
        deleteTextView.setTypeface(newFont);

        final List<Fragment> fragmentsList = new ArrayList<>();
        fragmentsList.add(new ImageFragment());
        fragmentsList.add(new EmojiFragment());

        PreviewSlidePagerAdapter adapter = new PreviewSlidePagerAdapter(getSupportFragmentManager(), fragmentsList);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(5);
        indicator.setViewPager(pager);

        photoEditorSDK = new PhotoEditorSDK.PhotoEditorSDKBuilder(this)
                .parentView(parentImageRelativeLayout) // add parent image view
                .childView(photoEditImageView) // add the desired image view
                .deleteView(deleteRelativeLayout) // add the deleted view that will appear during the movement of the views
                .brushDrawingView(brushDrawingView) // add the brush drawing view that is responsible for drawing on the image view
                .toolsViews(topShadowRelativeLayout, bottomShadowRelativeLayout)
                .buildPhotoEditorSDK(); // build photo editor sdk
        photoEditorSDK.setOnPhotoEditorSDKListener(this);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    mLayout.setScrollableView(((ImageFragment) fragmentsList.get(position)).getImageRecyclerView());
                else if (position == 1)
                    mLayout.setScrollableView(((EmojiFragment) fragmentsList.get(position)).getEmojiRecyclerView());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        closeTextView.setOnClickListener(this);
        addImageEmojiTextView.setOnClickListener(this);
        addTextView.setOnClickListener(this);
        addPencil.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        saveTextTextView.setOnClickListener(this);
        undoTextView.setOnClickListener(this);
        undoTextTextView.setOnClickListener(this);
        doneDrawingTextView.setOnClickListener(this);
        eraseDrawingTextView.setOnClickListener(this);
        clearAllTextView.setOnClickListener(this);
        clearAllTextTextView.setOnClickListener(this);
        goToNextTextView.setOnClickListener(this);

        colorPickerColors = new ArrayList<>();
        colorPickerColors.add(getCompatColor(R.color.black));
        colorPickerColors.add(getCompatColor(R.color.blue_color_picker));
        colorPickerColors.add(getCompatColor(R.color.brown_color_picker));
        colorPickerColors.add(getCompatColor(R.color.green_color_picker));
        colorPickerColors.add(getCompatColor(R.color.orange_color_picker));
        colorPickerColors.add(getCompatColor(R.color.red_color_picker));
        colorPickerColors.add(getCompatColor(R.color.red_orange_color_picker));
        colorPickerColors.add(getCompatColor(R.color.sky_blue_color_picker));
        colorPickerColors.add(getCompatColor(R.color.violet_color_picker));
        colorPickerColors.add(getCompatColor(R.color.white));
        colorPickerColors.add(getCompatColor(R.color.yellow_color_picker));
        colorPickerColors.add(getCompatColor(R.color.yellow_green_color_picker));

        mLayout.setScrollableView(((ImageFragment) fragmentsList.get(0)).getImageRecyclerView());
    }

    public int getCompatColor(@ColorRes int id) {
        return ContextCompat.getColor(this, id);
    }

    private boolean stringIsNotEmpty(String string) {
        if (string != null && !string.equals("null")) {
            if (!string.trim().equals("")) {
                return true;
            }
        }
        return false;
    }

    public void addEmoji(String emojiName) {
        photoEditorSDK.addEmoji(emojiName, emojiFont);
        if (mLayout != null)
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void addImage(Bitmap image) {
        photoEditorSDK.addImage(image);
        if (mLayout != null)
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void addText(String text, int colorCodeTextView) {
        photoEditorSDK.addText(text, colorCodeTextView);
    }

    private void clearAllViews() {
        photoEditorSDK.clearAllViews();
    }

    private void undoViews() {
        photoEditorSDK.viewUndo();
    }

    private void eraseDrawing() {
        photoEditorSDK.brushEraser();
    }

    private void openAddTextPopupWindow(String text, int colorCode) {
        colorCodeTextView = colorCode;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addTextPopupWindowRootView;
        if (inflater == null) {
            return;
        }
        addTextPopupWindowRootView = inflater.inflate(R.layout.add_text_popup_window, null);
        final EditText addTextEditText = addTextPopupWindowRootView.findViewById(R.id.add_text_edit_text);
        TextView addTextDoneTextView = addTextPopupWindowRootView.findViewById(R.id.add_text_done_tv);
        RecyclerView addTextColorPickerRecyclerView = addTextPopupWindowRootView.findViewById(R.id.add_text_color_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);
        addTextEditText.setTextColor(colorCode);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(this, colorPickerColors);
        colorPickerAdapter.setOnColorPickerClickListener(colorCode1 -> {
            addTextEditText.setTextColor(colorCode1);
            colorCodeTextView = colorCode1;
        });
        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter);
        if (stringIsNotEmpty(text)) {
            addTextEditText.setText(text);
            addTextEditText.setTextColor(colorCode == -1 ? getCompatColor(R.color.white) : colorCode);
        }
        if (pop != null && pop.isShowing() && !isFinishing()) {
            pop.dismiss();
            pop = null;
        }
//        final PopupWindow pop = new PopupWindow(PhotoEditorActivity.this);
//        pop.setContentView(addTextPopupWindowRootView);
//        pop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
//        pop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        pop = new PopupWindow(addTextPopupWindowRootView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setFocusable(true);
        pop.setBackgroundDrawable(null);
        if(!isFinishing()) {
            pop.showAtLocation(addTextPopupWindowRootView, Gravity.TOP, 0, 0);

        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        addTextDoneTextView.setOnClickListener(view -> {
            addText(addTextEditText.getText().toString(), colorCodeTextView);
            InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm1 != null) {
                imm1.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            if (pop != null && pop.isShowing() && !isFinishing()) {
                pop.dismiss();
                pop = null;
            }
        });
    }

    private void updateView(int visibility) {
        topShadow.setVisibility(visibility);
        topShadowRelativeLayout.setVisibility(visibility);
        bottomShadow.setVisibility(visibility);
        bottomShadowRelativeLayout.setVisibility(visibility);
    }

    private void updateBrushDrawingView(boolean brushDrawingMode) {
        photoEditorSDK.setBrushDrawingMode(brushDrawingMode);
        if (brushDrawingMode) {
            updateView(View.GONE);
            drawingViewColorPickerRecyclerView.setVisibility(View.VISIBLE);
            doneDrawingTextView.setVisibility(View.VISIBLE);
            eraseDrawingTextView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            drawingViewColorPickerRecyclerView.setLayoutManager(layoutManager);
            drawingViewColorPickerRecyclerView.setHasFixedSize(true);
            ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(this, colorPickerColors);
            colorPickerAdapter.setOnColorPickerClickListener(colorCode -> photoEditorSDK.setBrushColor(colorCode));
            drawingViewColorPickerRecyclerView.setAdapter(colorPickerAdapter);
        } else {
            updateView(View.VISIBLE);
            drawingViewColorPickerRecyclerView.setVisibility(View.GONE);
            doneDrawingTextView.setVisibility(View.GONE);
            eraseDrawingTextView.setVisibility(View.GONE);
        }
    }

    private void returnBackWithSavedImage() {
        updateView(View.GONE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentImageRelativeLayout.setLayoutParams(layoutParams);

        // Save image and send result
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageName = "IMG_" + timeStamp + ".jpg";
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RESULT_PATH, photoEditorSDK.saveImage("iUOB", imageName));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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

    @Override
    public void onCapture(Bitmap bitmap, Uri resultFile) {
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);
        if (mCameraLayout != null) {
            mCameraLayout.releaseCamera();
        }
        initPhotoEditor(resultFile.getPath());
    }

    @Override
    public void onRecord(Uri resultFile) {
        // TODO: add video stories
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_tv) {
            onBackPressed();
        } else if (v.getId() == R.id.add_image_emoji_tv) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        } else if (v.getId() == R.id.add_text_tv) {
            openAddTextPopupWindow("", Color.WHITE);
        } else if (v.getId() == R.id.add_pencil_tv) {
            updateBrushDrawingView(true);
        } else if (v.getId() == R.id.done_drawing_tv) {
            updateBrushDrawingView(false);
        } else if (v.getId() == R.id.save_tv || v.getId() == R.id.save_text_tv) {
            returnBackWithSavedImage();
        } else if (v.getId() == R.id.clear_all_tv || v.getId() == R.id.clear_all_text_tv) {
            clearAllViews();
        } else if (v.getId() == R.id.undo_text_tv || v.getId() == R.id.undo_tv) {
            undoViews();
        } else if (v.getId() == R.id.erase_drawing_tv) {
            eraseDrawing();
        } else if (v.getId() == R.id.go_to_next_screen_tv) {
            returnBackWithSavedImage();
        }
    }

    @Override
    public void onEditTextChangeListener(String text, int colorCode) {
        openAddTextPopupWindow(text, colorCode);
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        if (numberOfAddedViews > 0) {
            undoTextView.setVisibility(View.VISIBLE);
            undoTextTextView.setVisibility(View.VISIBLE);
        }
        switch (viewType) {
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onAddViewListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onAddViewListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onAddViewListener");
                break;
            case TEXT:
                Log.i("TEXT", "onAddViewListener");
                break;
        }
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        Log.i(TAG, "onRemoveViewListener");
        if (numberOfAddedViews == 0) {
            undoTextView.setVisibility(View.GONE);
            undoTextTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        switch (viewType) {
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onStartViewChangeListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onStartViewChangeListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onStartViewChangeListener");
                break;
            case TEXT:
                Log.i("TEXT", "onStartViewChangeListener");
                break;
        }
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        switch (viewType) {
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onStopViewChangeListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onStopViewChangeListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onStopViewChangeListener");
                break;
            case TEXT:
                Log.i("TEXT", "onStopViewChangeListener");
                break;
        }
    }

    private class PreviewSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments;

        PreviewSlidePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            if (mFragments == null) {
                return (null);
            }
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
