package com.muqdd.iuob2.features.stories;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.models.Story;
import com.orhanobut.logger.Logger;
import com.rahuljanagouda.statusstories.StoryStatusView;
import com.rahuljanagouda.statusstories.glideProgressBar.DelayBitmapTransformation;
import com.rahuljanagouda.statusstories.glideProgressBar.LoggingListener;
import com.rahuljanagouda.statusstories.glideProgressBar.ProgressTarget;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Ali Yusuf on 12/29/2017.
 * iUOB-2
 */

public class StatusStoriesActivity extends AppCompatActivity implements StoryStatusView.UserInteractionListener {

    public static final String STATUS_RESOURCES_KEY = "statusStoriesResources";
    public static final String STATUS_DURATION_KEY = "statusStoriesDuration";
    public static final String STATUS_DURATIONS_ARRAY_KEY = "statusStoriesDurations";
    public static final String IS_IMMERSIVE_KEY = "isImmersive";
    public static final String IS_CACHING_ENABLED_KEY = "isCaching";
    public static final String IS_TEXT_PROGRESS_ENABLED_KEY = "isText";
    private static StoryStatusView storyStatusView;
    private ImageView image;
    private TextView name;
    private TextView time;
    private TextView views;
    private int counter = 0;
    private String[] statusResources;
    private long statusDuration;
    private boolean isImmersive = true;
    private boolean isCaching = true;
    private static boolean isTextEnabled = true;
    private ProgressTarget<String, Bitmap> target;

    public StatusStoriesActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_stories);

        statusResources = getIntent().getStringArrayExtra(STATUS_RESOURCES_KEY);
        statusDuration = getIntent().getLongExtra(STATUS_DURATION_KEY, 3000L);
//        statusResourcesDuration = getIntent().getLongArrayExtra(STATUS_DURATIONS_ARRAY_KEY);
        isImmersive = getIntent().getBooleanExtra(IS_IMMERSIVE_KEY, true);
        isCaching = getIntent().getBooleanExtra(IS_CACHING_ENABLED_KEY, true);
        isTextEnabled = getIntent().getBooleanExtra(IS_TEXT_PROGRESS_ENABLED_KEY, false);

        ProgressBar imageProgressBar = findViewById(R.id.imageProgressBar);
        TextView textView = findViewById(R.id.textView);
        image = findViewById(R.id.image);
        name = findViewById(R.id.lbl_name);
        time = findViewById(R.id.lbl_time);
        views = findViewById(R.id.lbl_views);

        storyStatusView = findViewById(R.id.storiesStatus);
        storyStatusView.setStoriesCount(statusResources.length);
        storyStatusView.setStoryDuration(statusDuration);
        // or
        // statusView.setStoriesCountWithDurations(statusResourcesDuration);
        storyStatusView.setUserInteractionListener(this);
        storyStatusView.playStories();
        target = new MyProgressTarget<>(new BitmapImageViewTarget(image), imageProgressBar, textView);
        image.setOnClickListener(v -> storyStatusView.skip());

        storyStatusView.pause();
        Story story = Story.fromJson(statusResources[counter]);
        name.setText(story.getCreatedBy().getName());
        time.setText(story.getCreatedAtDuration());
        views.setText(story.getViewsFormat());
        target.setModel(story.getUrl());
        Glide.with(image.getContext())
                .load(target.getModel())
                .asBitmap()
                .crossFade()
                .skipMemoryCache(!isCaching)
                .diskCacheStrategy(isCaching ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
                .transform(new CenterCrop(image.getContext()), new DelayBitmapTransformation(1000))
                .listener(new LoggingListener<>())
                .into(target);

        // bind reverse view
        findViewById(R.id.reverse).setOnClickListener(v -> storyStatusView.reverse());

        // bind skip view
        findViewById(R.id.skip).setOnClickListener(v -> storyStatusView.skip());

        findViewById(R.id.actions).setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                storyStatusView.pause();
            } else {
                storyStatusView.resume();
            }
            view.performClick();
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onNext() {
        storyStatusView.pause();
        ++this.counter;
        Story story = Story.fromJson(this.statusResources[this.counter]);
        this.name.setText(story.getCreatedBy().getName());
        this.time.setText(story.getCreatedAtDuration());
        this.views.setText(story.getViewsFormat());
        this.target.setModel(story.getUrl());
        Glide.with(this.image.getContext()).load(this.target.getModel()).asBitmap().crossFade().centerCrop().skipMemoryCache(!this.isCaching).diskCacheStrategy(this.isCaching?DiskCacheStrategy.ALL:DiskCacheStrategy.NONE).transform(new BitmapTransformation[]{new CenterCrop(this.image.getContext()), new DelayBitmapTransformation(1000)}).listener(new LoggingListener()).into(this.target);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPrev() {
        if(this.counter - 1 >= 0) {
            storyStatusView.pause();
            --this.counter;
            Story story = Story.fromJson(this.statusResources[this.counter]);
            this.name.setText(story.getCreatedBy().getName());
            this.time.setText(story.getCreatedAtDuration());
            this.views.setText(story.getViewsFormat());
            this.target.setModel(story.getUrl());
            Glide.with(this.image.getContext()).load(this.target.getModel()).asBitmap().centerCrop().crossFade().skipMemoryCache(!this.isCaching).diskCacheStrategy(this.isCaching?DiskCacheStrategy.ALL:DiskCacheStrategy.NONE).transform(new BitmapTransformation[]{new CenterCrop(this.image.getContext()), new DelayBitmapTransformation(1000)}).listener(new LoggingListener()).into(this.target);
        }
    }

    @Override
    public void onComplete() {
        this.finish();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(this.isImmersive && hasFocus) {
            this.getWindow().getDecorView().setSystemUiVisibility(5126);
        }
    }

    protected void onDestroy() {
        storyStatusView.destroy();
        super.onDestroy();
    }

    @SuppressLint({"SetTextI18n"})
    private static class MyProgressTarget<Z> extends ProgressTarget<String, Z> {
        private final TextView text;
        private final ProgressBar progress;

        MyProgressTarget(Target<Z> target, ProgressBar progress, TextView text) {
            super(target);
            this.progress = progress;
            this.text = text;
        }

        public float getGranualityPercentage() {
            return 0.1F;
        }

        protected void onConnecting() {
            this.progress.setIndeterminate(true);
            this.progress.setVisibility(View.VISIBLE);
            if(isTextEnabled) {
                this.text.setVisibility(View.VISIBLE);
                this.text.setText("connecting");
            } else {
                this.text.setVisibility(View.INVISIBLE);
            }

            storyStatusView.pause();
        }

        protected void onDownloading(long bytesRead, long expectedLength) {
            this.progress.setIndeterminate(false);
            this.progress.setProgress((int)(100L * bytesRead / expectedLength));
            if(isTextEnabled) {
                this.text.setVisibility(View.VISIBLE);
                this.text.setText(String.format(Locale.ROOT, "downloading %.2f/%.2f MB %.1f%%", new Object[]{(double) bytesRead / 1000000.0D, (double) expectedLength / 1000000.0D, 100.0F * (float) bytesRead / (float) expectedLength}));
            } else {
                this.text.setVisibility(View.INVISIBLE);
            }

            storyStatusView.pause();
        }

        protected void onDownloaded() {
            this.progress.setIndeterminate(true);
            if(isTextEnabled) {
                this.text.setVisibility(View.VISIBLE);
                this.text.setText("decoding and transforming");
            } else {
                this.text.setVisibility(View.INVISIBLE);
            }

            storyStatusView.pause();
        }

        protected void onDelivered() {
            this.progress.setVisibility(View.INVISIBLE);
            this.text.setVisibility(View.INVISIBLE);
            storyStatusView.resume();
        }
    }
}
