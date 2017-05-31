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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.EventHook;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.app.Constants;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.MyCourseModel;
import com.orhanobut.logger.Logger;

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

public class ScheduleBuilderFragment extends BaseFragment {

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.semester) SegmentedGroup semesterRadio;
    @BindView(R.id.course) AutoCompleteTextView course;
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
                if (myCourseList.size() == 0) {
                    Snackbar.make(mainContent, "Please add courses first", Snackbar.LENGTH_LONG).show();
                } else {
                    OptionsFragment fragment =
                            OptionsFragment.newInstance(getString(R.string.fragment_schedule_builder_options), myCourseList);
                    displayFragment(fragment);
                }
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
        fastItemAdapter = new FastItemAdapter<>();
        myCourseList = new ArrayList<>();

        fastItemAdapter.withItemEvent(new ClickEventHook<MyCourseModel>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof MyCourseModel.ViewHolder){
                    return ((MyCourseModel.ViewHolder) viewHolder).delete;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<MyCourseModel> fastAdapter, MyCourseModel item) {
                myCourseList.remove(position);
                fastItemAdapter.remove(position);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // disable refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(false);
        recyclerView.setAdapter(fastItemAdapter);

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH)+1;
        // Second semester
        if (month > 3 && month < 7){
            semesterRadio.check(R.id.second);
            Logger.d("case 1");
        }
        // Summer semester
        else if (month > 6 && month < 10) {
            semesterRadio.check(R.id.summer);
            Logger.d("case 2");
        }
        // First semester (from 9 to 12)
        else {
            semesterRadio.check(R.id.first);
            Logger.d("case 3");
        }

        semesterRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.first:
                        break;
                    case R.id.second:
                        break;
                    case R.id.summer:
                        break;
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, Constants.coursesList);
        course.setAdapter(adapter);
        // force all caps
        course.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        // validate input
        course.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence charSequence) {
                Arrays.sort(Constants.coursesList);
                return Arrays.binarySearch(Constants.coursesList, charSequence.toString()) > 0;
            }

            @Override
            public CharSequence fixText(CharSequence charSequence) {
                Snackbar.make(mainContent, "Wrong course", Snackbar.LENGTH_LONG).show();
                return "";
            }
        });
        course.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.course && !hasFocus) {
                    ((AutoCompleteTextView)view).performValidation();
                }
            }
        });
        course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addCourse();
            }
        });
    }

    @OnClick(R.id.add)
    void addCourse(){
        course.performValidation();
        if (!course.getText().toString().trim().equals("")) {
            String courseString = course.getText().toString().trim();
            String courseNumber = courseString.substring(courseString.length()-3);
            String CourseName = courseString.substring(0,courseString.length()-3);
            MyCourseModel myCourse = new MyCourseModel(CourseName, courseNumber,"");
            if (!myCourseList.contains(myCourse)) {
                myCourseList.add(myCourse);
                fastItemAdapter.add(myCourse);
                course.setText("");
            } else {
                Snackbar.make(mainContent, "This course already added", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
