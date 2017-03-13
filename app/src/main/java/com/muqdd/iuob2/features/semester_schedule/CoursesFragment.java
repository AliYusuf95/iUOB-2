package com.muqdd.iuob2.features.semester_schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class CoursesFragment extends BaseFragment {

    public final static String COURSE = "COURSE";
    public final static Type TYPE = new TypeToken<SemesterCoursesModel>() {}.getType();

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.semester_rv) SuperRecyclerView recyclerView;

    private String json;
    private List<SemesterCoursesModel> semesterCoursesList;
    private FastItemAdapter<SemesterCoursesModel> fastAdapter;

    public CoursesFragment() {
        // Required empty public constructor
    }

    public static CoursesFragment newInstance(String title, SemesterCoursesModel course) {
        CoursesFragment fragment = new CoursesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(COURSE, new Gson().toJson(course,TYPE));
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
        json = getArguments().getString(COURSE);
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
        toolbar.setTitle(title);
        fastAdapter = new FastItemAdapter<>();
        //semesterCoursesList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastAdapter);
        //semesterCoursesList = new Gson().fromJson(json, TYPE);
        //fastAdapter.add(semesterCoursesList);

    }
}
