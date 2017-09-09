package com.muqdd.iuob2.features.semester_schedule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.muqdd.iuob2.models.CoursePrefix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */
@SuppressWarnings({"unused","WeakerAccess"})
class SemesterPagerAdapter extends FragmentStatePagerAdapter {

    private final List<List<CoursePrefix>> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    SemesterPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    void addCoursePrefixFragment(List<CoursePrefix> list, String title) {
        fragmentList.add(list);
        fragmentTitleList.add(title);
        notifyDataSetChanged();
    }

    void addCoursePrefixFragment(List<CoursePrefix> list, String title, int pos) {
        if (pos > -1 && pos <= fragmentList.size()) {
            fragmentList.add(pos, list);
            fragmentTitleList.add(pos, title);
            notifyDataSetChanged();
        } else {
            addCoursePrefixFragment(list, title);
        }
    }

    void removeCoursePrefixFragment(List<CoursePrefix> fragment){
        int index = fragmentList.indexOf(fragment);
        if (index != -1){
            fragmentList.remove(index);
            fragmentTitleList.remove(index);
            notifyDataSetChanged();
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (fragmentList.get(position).size() < 0) {
            return null;
        }
        int year = fragmentList.get(position).get(0).getYear();
        int semester = fragmentList.get(position).get(0).getSemester();
        return PrefixesFragment.newInstance(year, semester, fragmentList.get(position));
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public String getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

}