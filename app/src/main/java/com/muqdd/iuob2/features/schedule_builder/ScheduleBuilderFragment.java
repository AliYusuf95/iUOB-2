package com.muqdd.iuob2.features.schedule_builder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.MyCourseModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class ScheduleBuilderFragment extends BaseFragment {

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.semester) SegmentedGroup semesterRadio;
    @BindView(R.id.course) EditText course;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private View mView;
    private FastItemAdapter<MyCourseModel> fastItemAdapter;
    private List<MyCourseModel> myCourseList;

    public ScheduleBuilderFragment() {
        // Required empty public constructor
    }

    public static ScheduleBuilderFragment newInstance() {
        ScheduleBuilderFragment fragment = new ScheduleBuilderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.SCHEDULE_BUILDER.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_schedule_builder, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        // stop hiding toolbar
        params.setScrollFlags(0);
    }

    private void initiate() {
        // initialize variables
        fastItemAdapter = new FastItemAdapter<>();
        myCourseList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // disable refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(false);
        recyclerView.setAdapter(fastItemAdapter);
    }

    @OnClick(R.id.add)
    void addCourse(){
        if (!course.getText().toString().trim().equals("")) {
            MyCourseModel myCourse = new MyCourseModel(course.getText().toString(), "101","");
            fastItemAdapter.add(myCourse);
        }
    }

}
