package com.muqdd.iuob2.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mikepenz.materialdrawer.Drawer;
import com.muqdd.iuob2.network.ServiceGenerator;

/**
 * Created by Ali Yusuf on 3/10/2017.
 * iUOB-2
 */

public class BaseActivity extends AppCompatActivity {

    protected Drawer drawerMenu;
    protected OnBackPressedListener mOnBackPressedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServiceGenerator.init(this);
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.mOnBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (mOnBackPressedListener == null || !mOnBackPressedListener.onBack())
            super.onBackPressed();
    }

    public interface OnBackPressedListener{
        /**
         * @return true to prevent default onBackPressed; false otherwise
         */
        boolean onBack();
    }
}
