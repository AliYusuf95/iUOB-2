package com.muqdd.iuob2.features.semester_schedule;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.CoursePrefix;
import com.muqdd.iuob2.models.RestResponse;
import com.muqdd.iuob2.network.IUOBApi;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.UOBSchedule;
import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class SemestersHolderFragment extends BaseFragment {

    protected @BindView(R.id.viewPager) ViewPager viewPager;
    protected @BindView(R.id.progress_bar) ProgressBar progressBar;

    private SemesterPagerAdapter pagerAdapter;
    private View mView;
    private Map<String,SearchTextListener> searchTextListeners;

    public SemestersHolderFragment() {
        // Required empty public constructor
    }

    public static SemestersHolderFragment newInstance() {
        SemestersHolderFragment fragment = new SemestersHolderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.SEMESTER_SCHEDULE.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_semesters_holder, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) { // just in case
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    for (String key:searchTextListeners.keySet()){
                        searchTextListeners.get(key).onTextChange(s);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    for (String key:searchTextListeners.keySet()){
                        searchTextListeners.get(key).onTextChange(s);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup variables and layouts
        if (pagerAdapter.getCount() == 0) {
            getSemesters();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tabLayout.setVisibility(View.VISIBLE);
        toolbar.setTitle(title);
    }

    private void initiate() {
        searchTextListeners = new HashMap<>();
        pagerAdapter = new SemesterPagerAdapter(getChildFragmentManager());
        //pagerAdapter.addFragment(PrefixesFragment.newInstance(), "2017/2");
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new PageListener());
        tabLayout.setupWithViewPager(viewPager);
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    private void getSemesters() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        // Second semester
        if (month > 3 && month < 7){
            getSemesterCourses(0, year-1, 2);
            getSemesterCourses(1, year-1, 3);
            getSemesterCourses(2, year, 1);
            Logger.d("case 1");
        }
        // Summer semester
        else if (month > 6 && month < 9) {
            getSemesterCourses(0, year-1, 3);
            getSemesterCourses(1, year, 1);
            getSemesterCourses(2, year, 2);
            Logger.d("case 2");
        }
        // First semester (from 9 to 12)
        else {
            getSemesterCourses(0, year, 1);
            getSemesterCourses(1, year, 2);
            getSemesterCourses(2, year, 3);
            Logger.d("case 3");
        }
    }

    public void getSemesterCourses(final int pos, final int year, final int semester) {
        ServiceGenerator.createService(UOBSchedule.class)
                .coursesPrefix(year, semester).enqueue(new Callback<RestResponse<List<String>>>() {
            @Override
            public void onResponse(@NonNull Call<RestResponse<List<String>>> call, @NonNull Response<RestResponse<List<String>>> response) {
                RestResponse<List<String>> restResponse = response.body();
                if (restResponse != null && restResponse.getStatusCode() == 200){
                    List<CoursePrefix> list = CoursePrefix.createList(restResponse.getData(), year, semester);
                    if (list.size() > 0) {
                        progressBar.setVisibility(View.GONE);
                        pagerAdapter.addCoursePrefixFragment(list, year+"/"+semester, pos);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RestResponse<List<String>>> call, @NonNull Throwable t) {

            }
        });
    }

    public void addSearchTextListeners(String k, SearchTextListener listener) {
        searchTextListeners.put(k,listener);
    }

    interface SearchTextListener {
        void onTextChange(String s);
    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            Logger.i("page selected " + position);
        }
    }
}
