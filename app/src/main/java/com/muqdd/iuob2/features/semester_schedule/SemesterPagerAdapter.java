package com.muqdd.iuob2.features.semester_schedule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

class SemesterPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    SemesterPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public String getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    public Fragment getFragment(int position) {
        return fragmentList.get(position);
    }
}