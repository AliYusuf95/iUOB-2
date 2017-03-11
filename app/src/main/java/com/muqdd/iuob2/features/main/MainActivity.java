package com.muqdd.iuob2.features.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseActivity;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.semester_schedule.SemestersHolderFragment;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    private AccountHeader drawerHeader;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
        initDrawerMenu(savedInstanceState);
        displayFragment(SemestersHolderFragment.newInstance());
    }

    private void init() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        fragmentManager = getSupportFragmentManager();
    }

    private void initDrawerMenu(Bundle savedInstanceState) {

        PrimaryDrawerItem semesterSchedule = new PrimaryDrawerItem()
                .withName("Semester Schedule")
                .withIcon(R.drawable.semester)
                .withTag(Menu.SEMESTER_SCHEDULE)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        PrimaryDrawerItem mySchedule = new PrimaryDrawerItem()
                .withName("My Schedule")
                .withIcon(R.drawable.current)
                .withTag(Menu.MY_SCHEDULE)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        PrimaryDrawerItem scheduleBuilder = new PrimaryDrawerItem()
                .withName("Schedule Builder")
                .withIcon(R.drawable.builder)
                .withTag(Menu.SCHEDULE_BUILDER)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        PrimaryDrawerItem map = new PrimaryDrawerItem()
                .withName("UOB Map")
                .withIcon(R.drawable.map)
                .withTag(Menu.MAP)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        PrimaryDrawerItem links = new PrimaryDrawerItem()
                .withName("Useful Links")
                .withIcon(R.drawable.links)
                .withTag(Menu.LINKS)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        PrimaryDrawerItem about = new PrimaryDrawerItem()
                .withName("About")
                .withIcon(R.drawable.credit)
                .withTag(Menu.ABOUT)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        drawerMenu = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.custom_drawer_header)
                .withTranslucentStatusBar(false) // for embedded drawer
                .withActionBarDrawerToggle(true) // for hamburger icon
                .withActionBarDrawerToggleAnimated(true) // to animate hamburger icon
                .addDrawerItems(
                        semesterSchedule,
                        new DividerDrawerItem(),
                        mySchedule,
                        scheduleBuilder,
                        map,
                        links,
                        about
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withCloseOnClick(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((Menu)drawerItem.getTag()){
                            case SEMESTER_SCHEDULE:
                                displayFragment(SemestersHolderFragment.newInstance());
                                return false;
                            default:
                                Logger.w("not handled select");
                                return true;
                        }
                    }
                })
                .build();
    }

    private void displayFragment(Fragment fragment){
        fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frameLayout);
        if (currentFragment == null || !fragment.getClass().toString().equals(currentFragment.getTag())) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout,fragment,fragment.getClass().toString())
                    .commit();
            fragmentManager.executePendingTransactions();
        }
    }
}
