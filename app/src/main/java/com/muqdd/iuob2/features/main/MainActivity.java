package com.muqdd.iuob2.features.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseActivity;
import com.muqdd.iuob2.features.about.AboutFragment;
import com.muqdd.iuob2.features.account.AccountFragment;
import com.muqdd.iuob2.features.account.LoginFragment;
import com.muqdd.iuob2.features.calendar.CalendarSemestersFragment;
import com.muqdd.iuob2.features.links.LinksFragment;
import com.muqdd.iuob2.features.map.MapFragment;
import com.muqdd.iuob2.features.my_schedule.MyScheduleFragment;
import com.muqdd.iuob2.features.schedule_builder.ScheduleBuilderFragment;
import com.muqdd.iuob2.features.semester_schedule.SemestersHolderFragment;
import com.muqdd.iuob2.models.User;
import com.orhanobut.logger.Logger;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    protected @BindView(R.id.toolbar)
    Toolbar toolbar;
    protected @BindView(R.id.main_content)
    CoordinatorLayout mainContent;

    private FragmentManager fragmentManager;
    private PrimaryDrawerItem stories;
    private PrimaryDrawerItem semesterSchedule;
    private PrimaryDrawerItem calendarSchedule;
    private PrimaryDrawerItem mySchedule;
    private PrimaryDrawerItem scheduleBuilder;
    private PrimaryDrawerItem map;
    private PrimaryDrawerItem account;
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

        // send tracker
        sendAnalyticTracker(R.string.app_name);


        MobileAds.initialize(this, initializationStatus -> {
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        });
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
        fragmentManager.addOnBackStackChangedListener(() -> {
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

//        stories = new PrimaryDrawerItem()
//                .withTag(Menu.STORIES)
//                .withName(Menu.STORIES.toString())
//                .withIcon(R.drawable.ic_send_24dp)
//                .withIconColorRes(R.color.colorPrimaryDark)
//                .withIconTintingEnabled(true)
//                .withSelectedTextColorRes(R.color.colorPrimaryDark);

        semesterSchedule = new PrimaryDrawerItem()
                .withTag(Menu.SEMESTER_SCHEDULE)
                .withName(Menu.SEMESTER_SCHEDULE.toString())
                .withIcon(R.drawable.semester)
                .withSelectedTextColorRes(R.color.colorPrimaryDark);

//        calendarSchedule = new PrimaryDrawerItem()
//                .withTag(Menu.CALENDAR)
//                .withName(Menu.CALENDAR.toString())
//                .withIcon(R.drawable.calendar)
//                .withSelectedTextColorRes(R.color.colorPrimaryDark);

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

        account = new PrimaryDrawerItem()
                .withTag(Menu.ACCOUNT)
                .withName(Menu.ACCOUNT.toString())
                .withIcon(R.drawable.ic_person)
                .withIconColorRes(R.color.blue_color_picker)
                .withIconTintingEnabled(true)
                .withSelectedTextColorRes(R.color.colorPrimaryDark)
                .withSelectedIconColorRes(R.color.blue_color_picker);

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
                        // stories,
                        semesterSchedule,
                        // calendarSchedule,
                        //new DividerDrawerItem(), // just test divider
                        mySchedule,
                        scheduleBuilder,
                        map,
                        account,
                        links,
                        about
                )
                .withSavedInstance(savedInstanceState)
                .withCloseOnClick(true)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (User.isFetchingData() && lastSelectedItem != null && drawerItem != lastSelectedItem){
                        drawerMenu.setSelection(lastSelectedItem);
                        drawerMenu.closeDrawer();
                        Snackbar.make(mainContent,"Please wait fetching data", Snackbar.LENGTH_SHORT).show();
                        Logger.d(drawerItem.getTag());
                        Logger.d(lastSelectedItem.getTag());
                    } else {
                        lastSelectedItem = drawerItem;
                        switch ((Menu) drawerItem.getTag()) {
//                            case STORIES:
//                                replaceFragment(StoriesFragment.newInstance());
//                                break;
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
                            case ACCOUNT:
                                replaceFragment(AccountFragment.newInstance());
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
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, drawerItem.getTag().toString());
                        mFirebaseAnalytics.logEvent("app_menu_item", bundle);
                    }
                    return false;
                })
                .withOnDrawerNavigationListener(clickedView -> {
                    //this method is only called if the Arrow icon is shown.
                    //if the back arrow is shown.
                    onBackPressed();
                    return true;
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        if (getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager =
                                    (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            if (inputMethodManager != null) {
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

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
    private void setDrawerMenuSelection(Fragment mFragment, boolean fireOnClick){
        if(mFragment != null && drawerMenu != null){
//            if (mFragment instanceof StoriesFragment){
//                drawerMenu.setSelection(stories, fireOnClick);
//            }
            if (mFragment instanceof SemestersHolderFragment){
                drawerMenu.setSelection(semesterSchedule, fireOnClick);
            }
//            if (mFragment instanceof CalendarSemestersFragment){
//                drawerMenu.setSelection(calendarSchedule);
//            }
            else if (mFragment instanceof MyScheduleFragment){
                drawerMenu.setSelection(mySchedule, fireOnClick);
            }
            else if (mFragment instanceof ScheduleBuilderFragment){
                drawerMenu.setSelection(scheduleBuilder, fireOnClick);
            }
            else if (mFragment instanceof MapFragment){
                drawerMenu.setSelection(map, fireOnClick);
            }
            else if (mFragment instanceof AccountFragment || mFragment instanceof LoginFragment){
                if (!account.isSelected()){
                    drawerMenu.setSelection(account, fireOnClick);
                }
            }
            else if (mFragment instanceof LinksFragment){
                drawerMenu.setSelection(links, fireOnClick);
            }
            else if (mFragment instanceof AboutFragment){
                drawerMenu.setSelection(about, fireOnClick);
            }
        }
    }

    private void setDrawerMenuSelection(){
        Fragment mFragment = fragmentManager.findFragmentById(R.id.frameLayout);
        setDrawerMenuSelection(mFragment, true);
    }

    @Override
    public void replaceFragment(Fragment fragment){
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frameLayout);
        if (currentFragment == null || !fragment.getClass().toString().equals(currentFragment.getTag())) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout,fragment,fragment.getClass().toString())
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        setDrawerMenuSelection(fragment, false);
    }

    @Override
    public void displayFragment(Fragment fragment){
        if (isFinishing()) return;
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
