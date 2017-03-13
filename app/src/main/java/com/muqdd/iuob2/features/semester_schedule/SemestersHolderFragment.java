package com.muqdd.iuob2.features.semester_schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.main.Menu;
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
import java.util.Calendar;
import java.util.List;

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
        SemestersHolderFragment fragment = new SemestersHolderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.SEMESTER_SCHEDULE.toString());
        fragment.setArguments(bundle);
        return fragment;
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
        toolbar.setTitle(title);
    }

    private void initiate() {
        pagerAdapter = new SemesterPagerAdapter(getChildFragmentManager());
        //pagerAdapter.addFragment(SemesterFragment.newInstance(), "2017/2");
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
        List<SemesterCoursesModel> requests = new ArrayList<>();
        // Second semester
        if (month > 3 && month < 7){
            requests.add(new SemesterCoursesModel(year-1, 2));
            requests.add(new SemesterCoursesModel(year-1, 3));
            requests.add(new SemesterCoursesModel(year, 1));
        }
        // Summer semester
        else if (month > 6 && month < 10) {
            requests.add(new SemesterCoursesModel(year-1, 3));
            requests.add(new SemesterCoursesModel(year, 1));
            requests.add(new SemesterCoursesModel(year, 2));
        }
        // First semester (from 9 to 12)
        else {
            requests.add(new SemesterCoursesModel(year-1, 1));
            requests.add(new SemesterCoursesModel(year-1, 2));
            requests.add(new SemesterCoursesModel(year-1, 3));
        }

        for (SemesterCoursesModel request : requests) {
            try {
                getSemesterCoursesFromCache(request);
                Logger.i("Load from cache");
            } catch (IOException e) {
                getSemesterCoursesFromNet(request);
                Logger.i("Load from internet");
            }
        }
    }

    private void getSemesterCoursesFromCache(SemesterCoursesModel request) throws IOException {
        // Read data
        String data = readHTMLDataToCache(request.fileName());
        // Parse data
        ArrayList<SemesterCoursesModel> list = parseSemesterCoursesData(data);
        if (list.size() > 0) {
            pagerAdapter.addFragment(SemesterFragment.newInstance(request.semesterTitle(),
                    list), request.semesterTitle());
        } else {
            throw new IOException();
        }
    }

    public void getSemesterCoursesFromNet(final SemesterCoursesModel request) {
        ServiceGenerator.createService(UOBSchedule.class)
                .semesterCourses("1",request.year,request.semester).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        ArrayList<SemesterCoursesModel> list = parseSemesterCoursesData(response.body().string());
                        if (list.size() > 0) {
                            pagerAdapter.addFragment(SemesterFragment.newInstance(request.semesterTitle(),
                                    list), request.semesterTitle());
                            writeHTMLDataToCache(request.fileName(), response.body().string().getBytes());
                        }
                    } catch (IOException e) {
                        Logger.e(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public static ArrayList<SemesterCoursesModel> parseSemesterCoursesData (String data) {
        ArrayList<SemesterCoursesModel> list = new ArrayList<>();
        Document document = Jsoup.parse(data);
        Elements subjects = document.body().select("a");
        for (Element subject : subjects) {
            if (!subject.text().endsWith(".")) {
                list.add(new SemesterCoursesModel(subject.text(), subject.attr("href")));
            }
        }
        return list;
    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            Logger.i("page selected " + position);
            currentPage = position;
        }
    }
}
