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
@SuppressWarnings({"unused","WeakerAccess"})
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

    void addFragment(Fragment fragment, String title, int pos) {
        if (pos > -1 && pos <= fragmentList.size()) {
            fragmentList.add(pos, fragment);
            fragmentTitleList.add(pos, title);
            notifyDataSetChanged();
        } else {
            addFragment(fragment,title);
        }
    }

    void removeFragment(Fragment fragment){
        int index = fragmentList.indexOf(fragment);
        if (index != -1){
            fragmentList.remove(index);
            fragmentTitleList.remove(index);
            notifyDataSetChanged();
        }
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