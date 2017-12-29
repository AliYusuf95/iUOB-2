package com.muqdd.iuob2.features.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.muqdd.iuob2.features.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startActivity(new Intent(this, CaptureActivity.class));
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
