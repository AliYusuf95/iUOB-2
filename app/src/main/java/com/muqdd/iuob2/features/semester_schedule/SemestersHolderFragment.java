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
import com.muqdd.iuob2.models.SemesterCourseModel;
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

    protected @BindView(R.id.viewPager) ViewPager viewPager;

    private SemesterPagerAdapter pagerAdapter;
    private int currentPage;
    private View mView;

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
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_semesters_holder, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        return mView;
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
        List<SemesterCourseModel> requests = new ArrayList<>();
        // Second semester
        if (month > 3 && month < 7){
            requests.add(new SemesterCourseModel(year-1, 2));
            requests.add(new SemesterCourseModel(year-1, 3));
            requests.add(new SemesterCourseModel(year, 1));
            Logger.d("case 1");
        }
        // Summer semester
        else if (month > 6 && month < 10) {
            requests.add(new SemesterCourseModel(year-1, 3));
            requests.add(new SemesterCourseModel(year, 1));
            requests.add(new SemesterCourseModel(year, 2));
            Logger.d("case 2");
        }
        // First semester (from 9 to 12)
        else {
            requests.add(new SemesterCourseModel(year-1, 1));
            requests.add(new SemesterCourseModel(year-1, 2));
            requests.add(new SemesterCourseModel(year-1, 3));
            Logger.d("case 3");
        }

        for (SemesterCourseModel request : requests) {
            // No more chase things
            /*try {
                getSemesterCoursesFromCache(request);
                Logger.i("Load from cache");
            } catch (IOException e) {
                getSemesterCoursesFromNet(request);
                Logger.i("Load from internet");
            }*/
            // Data should loaded in semester fragment not here !!
            getSemesterCoursesFromNet(request);
        }
    }

    // Data should loaded in semester fragment not here !!
    /*private void getSemesterCoursesFromCache(SemesterCourseModel request) throws IOException {
        // Read data
        String data = readHTMLDataToCache(request.fileName());
        // Parse data
        ArrayList<SemesterCourseModel> list = parseSemesterCoursesData(data);
        if (list.size() > 0) {
            pagerAdapter.addFragment(SemesterFragment.newInstance(request.semesterTitle(),
                    request), request.semesterTitle());
        } else {
            throw new IOException();
        }
    }*/

    public void getSemesterCoursesFromNet(final SemesterCourseModel request) {
        final SemesterFragment fragment = SemesterFragment.newInstance(request.semesterTitle(),
                request);
        pagerAdapter.addFragment(fragment, request.semesterTitle());

        ServiceGenerator.createService(UOBSchedule.class)
                .semesterCourses("1",request.year,request.semester).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        ArrayList<SemesterCourseModel> list = parseSemesterCoursesData(response.body().string());
                        if (list.size() == 0) {
                            pagerAdapter.removeFragment(fragment);
                            //writeHTMLDataToCache(request.fileName(), response.body().string().getBytes());
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

    private ArrayList<SemesterCourseModel> parseSemesterCoursesData (String data) {
        ArrayList<SemesterCourseModel> list = new ArrayList<>();
        Document document = Jsoup.parse(data);
        Elements subjects = document.body().select("a");
        for (Element subject : subjects) {
            if (!subject.text().endsWith(".")) {
                list.add(new SemesterCourseModel(subject.text(), subject.attr("href")));
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
