package com.muqdd.iuob2.features.schedule_builder;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.app.Constants;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.MyCourseModel;
import com.muqdd.iuob2.models.SemesterCourseModel;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class OptionsFragment extends BaseFragment {

    private final static String COURSES_LIST = "COURSES_LIST";
    private final static Type COURSES_LIST_TYPE = new TypeToken<List<MyCourseModel>>() {}.getType();

    @BindView(R.id.main_content) LinearLayout mainContent;

    private View mView;
    private List<MyCourseModel> myCourseList;

    public OptionsFragment() {
        // Required empty public constructor
    }

    public static OptionsFragment newInstance(String title, List<MyCourseModel> courseList) {
        OptionsFragment fragment = new OptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(COURSES_LIST, new Gson().toJson(courseList, COURSES_LIST_TYPE));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_schedule_builder_options, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.schedule_builder_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.next:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        myCourseList = new Gson().fromJson(getArguments().getString(COURSES_LIST), COURSES_LIST_TYPE);
        if (myCourseList == null){
            myCourseList = new ArrayList<>();
        }
        Logger.d(myCourseList);
    }
}
