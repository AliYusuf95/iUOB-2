package com.muqdd.iuob2.features.semester_schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.muqdd.iuob2.models.SemesterCoursesModel;
import com.muqdd.iuob2.rest.ServiceGenerator;
import com.muqdd.iuob2.rest.UOBSchedule;
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

    public final static String LIST = "LIST";
    public final static Type LIST_TYPE = new TypeToken<List<SemesterCoursesModel>>() {}.getType();

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.semester_rv) SuperRecyclerView recyclerView;

    private String jsonList;
    private List<SemesterCoursesModel> semesterCoursesList;
    private FastItemAdapter<SemesterCoursesModel> fastAdapter;

    public SemesterFragment() {
        // Required empty public constructor
    }

    public static SemesterFragment newInstance(String title, ArrayList<SemesterCoursesModel> list) {
        SemesterFragment fragment = new SemesterFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(LIST, new Gson().toJson(list,LIST_TYPE));
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
        // Inflate the layout for this fragment
        jsonList = getArguments().getString(LIST);
        tabLayout.setVisibility(View.VISIBLE);
        View view = inflater.inflate(R.layout.fragment_semester, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup variables and layouts
        initiate();
    }

    private void initiate() {
        fastAdapter = new FastItemAdapter<>();
        semesterCoursesList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastAdapter);
        recyclerView.getSwipeToRefresh().setEnabled(true);
        recyclerView.getSwipeToRefresh().setColorSchemeResources(
                R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryLight);
        recyclerView.getSwipeToRefresh().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (semesterCoursesList.size() > 0)
                    getSemesterCoursesFromNet(semesterCoursesList.get(0));
                recyclerView.getSwipeToRefresh().setRefreshing(false);
            }
        });
        semesterCoursesList = new Gson().fromJson(jsonList, LIST_TYPE);
        fastAdapter.add(semesterCoursesList);

        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<SemesterCoursesModel>() {
            @Override
            public boolean onClick(View v, IAdapter<SemesterCoursesModel> adapter,
                                   SemesterCoursesModel item, int position) {
                CoursesFragment fragment = CoursesFragment.newInstance(item.title,item);
                displayFragment(fragment);
                return false;
            }
        });

    }

    public void getSemesterCoursesFromNet(final SemesterCoursesModel request) {
        ServiceGenerator.createService(UOBSchedule.class)
                .semesterCourses("1",request.year,request.semester).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        ArrayList<SemesterCoursesModel> list =
                                SemestersHolderFragment.parseSemesterCoursesData(response.body().string());

                        if (list.size() > 0) {
                            fastAdapter.clear().add(list);
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
}
