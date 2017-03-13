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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
//        if(!BaseFragment.handleBackPressed(getSupportFragmentManager())){
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back icon press
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setBackArrow (boolean backArrow) {
        // Show the back arrow
        if (backArrow) {
            if (drawerMenu != null) {
                drawerMenu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                drawerMenu.getActionBarDrawerToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else { // Show the hamburger icon
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
            if (drawerMenu != null)
                drawerMenu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        }
    }
}
