package com.muqdd.iuob2.features.main;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseActivity;
import com.muqdd.iuob2.app.User;
import com.muqdd.iuob2.features.about.AboutFragment;
import com.muqdd.iuob2.features.calendar.CalendarSemestersFragment;
import com.muqdd.iuob2.features.links.LinksFragment;
import com.muqdd.iuob2.features.map.MapFragment;
import com.muqdd.iuob2.features.my_schedule.MyScheduleFragment;
import com.muqdd.iuob2.features.schedule_builder.ScheduleBuilderFragment;
import com.muqdd.iuob2.features.semester_schedule.SemestersHolderFragment;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    protected @BindView(R.id.toolbar) Toolbar toolbar;
    protected @BindView(R.id.main_content) CoordinatorLayout mainContent;

    private FragmentManager fragmentManager;
    private PrimaryDrawerItem semesterSchedule;
    private PrimaryDrawerItem calendarSchedule;
    private PrimaryDrawerItem mySchedule;
    private PrimaryDrawerItem scheduleBuilder;
    private PrimaryDrawerItem map;
    private PrimaryDrawerItem links;
    private PrimaryDrawerItem about;
    private IDrawerItem lastSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init(savedInstanceState);
        initDrawerMenu(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // reset drawerMenu selection on orientation changed
        setDrawerMenuSelection();
    }

    private void init(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(Menu.SEMESTER_SCHEDULE.toString());
        }

        fragmentManager = getSupportFragmentManager();

        // if there is change in fragment stack change drawer icon.
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    // show arrow icon
                    drawerMenu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    // disable swipe to open menu
                    drawerMenu.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    drawerMenu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                    // enable swipe to open menu
                    drawerMenu.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                // reset drawerMenu selection on orientation changed
                setDrawerMenuSelection();
            }
        });

        // if it is not change orientation call append default fragment
        if (savedInstanceState == null){
            replaceFragment(SemestersHolderFragment.newInstance());
//            fragmentManager.beginTransaction()
//                    .add(R.id.frameLayout, SemestersHolderFragment.newInstance())
//                    .commit();
        }
    }

    private void initDrawerMenu(Bundle savedInstanceState) {

        semesterSchedule = new PrimaryDrawerItem()
                .withTag(Menu.SEMESTER_SCHEDULE)
                .withName(Menu.SEMESTER_SCHEDULE.toString())
                .withIcon(R.drawable.semester)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        calendarSchedule = new PrimaryDrawerItem()
                .withTag(Menu.CALENDAR)
                .withName(Menu.CALENDAR.toString())
                .withIcon(R.drawable.calendar)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        mySchedule = new PrimaryDrawerItem()
                .withTag(Menu.MY_SCHEDULE)
                .withName(Menu.MY_SCHEDULE.toString())
                .withIcon(R.drawable.current)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        scheduleBuilder = new PrimaryDrawerItem()
                .withTag(Menu.SCHEDULE_BUILDER)
                .withName(Menu.SCHEDULE_BUILDER.toString())
                .withIcon(R.drawable.builder)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        map = new PrimaryDrawerItem()
                .withTag(Menu.MAP)
                .withName(Menu.MAP.toString())
                .withIcon(R.drawable.map)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        links = new PrimaryDrawerItem()
                .withTag(Menu.LINKS)
                .withName(Menu.LINKS.toString())
                .withIcon(R.drawable.links)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        about = new PrimaryDrawerItem()
                .withTag(Menu.ABOUT)
                .withName(Menu.ABOUT.toString())
                .withIcon(R.drawable.credit)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        drawerMenu = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.custom_drawer_header)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true) // for hamburger icon
                .withActionBarDrawerToggleAnimated(true) // to animate hamburger icon
                .withTranslucentStatusBar(false) // for embedded drawer
                .addDrawerItems(
                        semesterSchedule,
                        calendarSchedule,
                        //new DividerDrawerItem(), // just test divider
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
                        if (User.isFetchingData() && lastSelectedItem != null && drawerItem != lastSelectedItem){
                            drawerMenu.setSelection(lastSelectedItem);
                            drawerMenu.closeDrawer();
                            Snackbar.make(mainContent,"Please wait fetching data",Snackbar.LENGTH_SHORT).show();
                            Logger.d(drawerItem.getTag());
                            Logger.d(lastSelectedItem.getTag());
                        } else {
                            lastSelectedItem = drawerItem;
                            switch ((Menu) drawerItem.getTag()) {
                                case SEMESTER_SCHEDULE:
                                    replaceFragment(SemestersHolderFragment.newInstance());
                                    break;
                                case CALENDAR:
                                    replaceFragment(CalendarSemestersFragment.newInstance());
                                    break;
                                case MY_SCHEDULE:
                                    replaceFragment(MyScheduleFragment.newInstance());
                                    break;
                                case SCHEDULE_BUILDER:
                                    replaceFragment(ScheduleBuilderFragment.newInstance());
                                    break;
                                case MAP:
                                    replaceFragment(MapFragment.newInstance());
                                    break;
                                case LINKS:
                                    replaceFragment(LinksFragment.newInstance());
                                    break;
                                case ABOUT:
                                    replaceFragment(AboutFragment.newInstance());
                                    break;
                                default:
                                    Logger.w("not handled select");
                                    break;
                            }
                        }
                        return false;
                    }
                })
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {
                        //this method is only called if the Arrow icon is shown.
                        //if the back arrow is shown.
                        onBackPressed();
                        //return true if we have consumed the event
                        return true;
                    }
                })
                .build();

        // setup current drawer icon (in case orientation changed)
        if(savedInstanceState != null){
            if (getSupportActionBar() != null) {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    drawerMenu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    drawerMenu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                }
            }
        } else {
            // set last item only if it is new create
            lastSelectedItem = semesterSchedule;
        }
    }

    /**
     * Set drawerMenu selection based on current fragment
     */
    private void setDrawerMenuSelection(){
        Fragment mFragment = fragmentManager.findFragmentById(R.id.frameLayout);
        if(mFragment != null){
            if (mFragment instanceof SemestersHolderFragment){
                drawerMenu.setSelection(semesterSchedule);
            }
            if (mFragment instanceof CalendarSemestersFragment){
                drawerMenu.setSelection(calendarSchedule);
            }
            if (mFragment instanceof MyScheduleFragment){
                drawerMenu.setSelection(mySchedule);
            }
            if (mFragment instanceof ScheduleBuilderFragment){
                drawerMenu.setSelection(scheduleBuilder);
            }
            if (mFragment instanceof MapFragment){
                drawerMenu.setSelection(map);
            }
            if (mFragment instanceof LinksFragment){
                drawerMenu.setSelection(links);
            }
            if (mFragment instanceof AboutFragment){
                drawerMenu.setSelection(about);
            }
        }
    }

    /**
     * Replace current fragment with another one
     *
     * @param fragment Fragment instance
     */
    public void replaceFragment(Fragment fragment){
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frameLayout);
        if (currentFragment == null || !fragment.getClass().toString().equals(currentFragment.getTag())) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout,fragment,fragment.getClass().toString())
                    .commit();
            fragmentManager.executePendingTransactions();
        }
    }

    /**
     * @param fragment Fragment instance
     */
    public void displayFragment(Fragment fragment){
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frameLayout);
        if (currentFragment == null || !fragment.getClass().toString().equals(currentFragment.getTag())) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, fragment, fragment.getClass().toString())
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
        }
    }
}
