package com.muqdd.iuob2.features.semester_schedule;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.models.SemesterCourseModel;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.UOBSchedule;
import com.orhanobut.logger.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

public class SemesterFragment extends BaseFragment {

    public final static String SEMESTER_COURSE = "SEMESTER_COURSE";
    public final static Type SEMESTER_COURSE_TYPE = new TypeToken<SemesterCourseModel>() {}.getType();

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private List<SemesterCourseModel> semesterCoursesList;
    private SemesterCourseModel mSemesterCourses;
    private FastItemAdapter<SemesterCourseModel> fastAdapter;
    private View mView;

    public SemesterFragment() {
        // Required empty public constructor
    }

    public static SemesterFragment newInstance(String title, SemesterCourseModel semesterCourses) {
        SemesterFragment fragment = new SemesterFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(SEMESTER_COURSE, new Gson().toJson(semesterCourses,SEMESTER_COURSE_TYPE));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mSemesterCourses = new Gson().fromJson(getArguments().getString(SEMESTER_COURSE), SEMESTER_COURSE_TYPE);
            tabLayout.setVisibility(View.VISIBLE);
            mView = inflater.inflate(R.layout.fragment_list, container, false);
            ButterKnife.bind(this, mView);
            // Setup variables and layouts
            initiate();
            getSemesterCoursesFromNet(mSemesterCourses);
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initiate() {
        // initialize variables
        fastAdapter = new FastItemAdapter<>();
        //semesterCoursesList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation()));

        // set refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(true);
        recyclerView.getSwipeToRefresh().setColorSchemeResources(
                R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryLight);

        // refresh request
        recyclerView.getSwipeToRefresh().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (semesterCoursesList.size() > 0) {
                    getSemesterCoursesFromNet(mSemesterCourses);
                } else {
                    recyclerView.getSwipeToRefresh().setRefreshing(false);
                }
            }
        });

        // On item click
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<SemesterCourseModel>() {
            @Override
            public boolean onClick(View v, IAdapter<SemesterCourseModel> adapter,
                                   SemesterCourseModel item, int position) {
                CoursesFragment fragment = CoursesFragment.newInstance(item.title,item);
                displayFragment(fragment);
                return false;
            }
        });

    }

    public void getSemesterCoursesFromNet(final SemesterCourseModel request) {
        ServiceGenerator.createService(UOBSchedule.class)
                .semesterCourses("1",request.year,request.semester).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.code() == 200) {
                    // do parsing in background
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                semesterCoursesList = parseSemesterCoursesData(response.body().string());
                                if (semesterCoursesList.size() > 0) {
                                    // attach the adapter
                                    runOnUi(new Runnable() {
                                        @Override
                                        public void run() {
                                            fastAdapter.set(semesterCoursesList);
                                            recyclerView.setAdapter(fastAdapter);
                                        }
                                    });
                                    //writeHTMLDataToCache(request.fileName(), response.body().string().getBytes());
                                }
                            } catch (IOException e) {
                                Logger.e(e.getMessage());
                            }
                        }
                    });
                }
                // stop refreshing
                recyclerView.getSwipeToRefresh().setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // stop refreshing
                recyclerView.getSwipeToRefresh().setRefreshing(false);
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
}
