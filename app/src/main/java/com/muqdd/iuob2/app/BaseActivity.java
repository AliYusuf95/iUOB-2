package com.muqdd.iuob2.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.muqdd.iuob2.rest.ServiceGenerator;

/**
 * Created by Ali Yusuf on 3/10/2017.
 * iUOB-2
 */

public class BaseActivity extends AppCompatActivity {

    protected Drawer drawerMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServiceGenerator.init(this);
    }

}
