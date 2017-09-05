package com.muqdd.iuob2.features.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.muqdd.iuob2.BuildConfig;
import com.muqdd.iuob2.features.main.MainActivity;
import com.muqdd.iuob2.features.stories.StoriesActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            startActivity(new Intent(this, StoriesActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
