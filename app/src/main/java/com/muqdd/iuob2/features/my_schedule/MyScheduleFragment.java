package com.muqdd.iuob2.features.my_schedule;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.app.User;
import com.muqdd.iuob2.features.main.Menu;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */
@SuppressWarnings("FieldCanBeLocal")
public class MyScheduleFragment extends BaseFragment {

    @BindView(R.id.u_layout) LinearLayout uLayout;
    @BindView(R.id.m_layout) LinearLayout mLayout;
    @BindView(R.id.t_layout) LinearLayout tLayout;
    @BindView(R.id.w_layout) LinearLayout wLayout;
    @BindView(R.id.h_layout) LinearLayout hLayout;
    @BindDrawable(R.drawable.ic_notifications_active_24dp) Drawable notificationActive;
    @BindDrawable(R.drawable.ic_notifications_off_24dp) Drawable notificationOff;

    private View mView;

    public MyScheduleFragment() {
        // Required empty public constructor
    }

    public static MyScheduleFragment newInstance() {
        MyScheduleFragment fragment = new MyScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.MY_SCHEDULE.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_my_schedule, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_schedule_menu, menu);
        // init notification icon
        menu.findItem(R.id.notification)
            .setIcon(User.isNotificationOn(getContext()) ? notificationActive : notificationOff);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.edit:
                AddCoursesFragment fragment =
                        AddCoursesFragment.newInstance(getString(R.string.fragment_add_courses));
                displayFragment(fragment);
                return true;
            case R.id.notification:
                changeNotificationState(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        // stop hiding toolbar
        params.setScrollFlags(0);
    }

    private void initiate() {

    }

    private void changeNotificationState(MenuItem item) {
        // change current notification state
        boolean notificationState = User.setNotification(getContext(), !User.isNotificationOn(getContext()));
        // set new icon based on state
        item.setIcon(notificationState ? notificationActive : notificationOff);

        //TODO: add system alarm functionality
    }

}
