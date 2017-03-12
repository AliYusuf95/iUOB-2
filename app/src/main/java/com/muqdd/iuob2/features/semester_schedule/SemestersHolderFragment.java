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
import com.muqdd.iuob2.models.SemesterCoursesModel;
import com.muqdd.iuob2.rest.ServiceGenerator;
import com.muqdd.iuob2.rest.UOBSchedule;
import com.orhanobut.logger.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        getSemesters();
    }

    @Override
    public void onResume() {
        super.onResume();
        tabLayout.setVisibility(View.VISIBLE);
    }

    private void initiate() {
        pagerAdapter = new SemesterPagerAdapter(getActivity().getSupportFragmentManager());
        //pagerAdapter.addFragment(SemesterFragment.newInstance(), "2017/2");
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new PageListener());
        tabLayout.setupWithViewPager(viewPager);
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    private void getSemesters() {
        ServiceGenerator.createService(UOBSchedule.class)
                .semesterCourses("1","2016","2").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ArrayList<SemesterCoursesModel> list = new ArrayList<>();;
                    Document document = Jsoup.parse(response.body().string());
                    Elements subjects = document.body().select("a");
                    for (Element subject : subjects) {
                        list.add(new SemesterCoursesModel(subject.text(),subject.attr("href")));
                    }
                    pagerAdapter.addFragment(SemesterFragment.newInstance("2017/1", list), "2017/1");
                } catch (IOException e) {
                    Logger.e(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            Logger.i("page selected " + position);
            currentPage = position;
        }
    }
}
