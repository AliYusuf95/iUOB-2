package com.muqdd.iuob2.features.semester_schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class SemesterFragment extends BaseFragment {

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.semester_rv) SuperRecyclerView recyclerView;
    @BindView(R.id.semester_swipeContainer) SwipeRefreshLayout swipeContainer;

    private String title;

    public SemesterFragment() {
        // Required empty public constructor
    }

    public static SemesterFragment newInstance() {
        return new SemesterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // super.onCreateView(inflater,container,savedInstanceState);
        // Inflate the layout for this fragment
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
//        FragmentManager childFragMan = getChildFragmentManager();
//
//        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
//        BaseFragment fragB = BaseFragment.newInstance();
//        childFragTrans.add(fragB,"asdas").addToBackStack("B").commit();
    }
}
