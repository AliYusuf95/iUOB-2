package com.muqdd.iuob2.features.semester_schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class SemestersHolderFragment extends BaseFragment {

    @BindView(R.id.viewPager) ViewPager viewPager;

    private SemesterPagerAdapter pagerAdapter;
    private int currentPage;

    public SemestersHolderFragment() {
        // Required empty public constructor
    }

    public static SemestersHolderFragment newInstance() {
        return new SemestersHolderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_semesters_holder, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup variables and layouts
        initiate();
    }

    @Override
    public void onResume() {
        super.onResume();
        tabLayout.setVisibility(View.VISIBLE);
    }

    private void initiate() {
        pagerAdapter = new SemesterPagerAdapter(getActivity().getSupportFragmentManager());
        pagerAdapter.addFragment(SemesterFragment.newInstance(), "2017/1");
        pagerAdapter.addFragment(SemesterFragment.newInstance(), "2017/2");
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new PageListener());
        pagerAdapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            Logger.i("page selected " + position);
            currentPage = position;
        }
    }
}
