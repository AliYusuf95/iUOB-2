package com.muqdd.iuob2.features.schedule_builder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class ScheduleBuilderFragment extends BaseFragment {

    private final static String COURSES_LIST = "COURSES_LIST";
    private final static Type COURSES_LIST_TYPE = new TypeToken<List<BCourse>>() {}.getType();

    private final static Pattern pCourse =
            Pattern.compile("^([\\w]+[a-z])\\s*?([\\d]{3})$",Pattern.CASE_INSENSITIVE);

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.semester) SegmentedGroup semesterRadio;
    @BindView(R.id.course) AutoCompleteTextView txtCourse;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private View mView;
    private FastItemAdapter<BCourse> fastItemAdapter;
    private List<BCourse> mCourseList;
    private MenuItem nextMenuItem;
    private int semester;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_schedule_builder, container, false);
            ButterKnife.bind(this, mView);
            initiate(savedInstanceState);
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.schedule_builder_menu, menu);
        nextMenuItem = menu.findItem(R.id.next);
        refreshNextMenuItem();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.next:
                if (mCourseList.size() == 0) {
                    Snackbar.make(mainContent, "Please add courses first", Snackbar.LENGTH_LONG).show();
                } else {
                    // start options fragment
                    OptionsFragment fragment =
                            OptionsFragment.newInstance(getString(R.string.fragment_schedule_builder_options), semester, mCourseList);
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
    }

    private void initiate(Bundle savedInstanceState) {
        // initialize variables
        fastItemAdapter = new FastItemAdapter<>();
        mCourseList = new ArrayList<>();

        fastItemAdapter.withItemEvent(new ClickEventHook<BCourse>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof BCourse.ViewHolder){
                    return ((BCourse.ViewHolder) viewHolder).delete;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<BCourse> fastAdapter, BCourse item) {
                mCourseList.remove(position);
                fastItemAdapter.remove(position);
                refreshNextMenuItem();
            }
        });

        // load data from savedInstanceState
        if (savedInstanceState != null && savedInstanceState.containsKey(COURSES_LIST)) {
            mCourseList = new Gson().fromJson(savedInstanceState.getString(COURSES_LIST), COURSES_LIST_TYPE);
            fastItemAdapter.add(mCourseList);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // disable refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(false);
        recyclerView.setAdapter(fastItemAdapter);

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH)+1;
        // Second semester
        if (month > 3 && month < 7){
            semesterRadio.check(R.id.second);
            semester = 2;
        }
        // Summer semester
        else if (month > 6 && month < 9) {
            semesterRadio.check(R.id.summer);
            semester = 3;
        }
        // First semester (from 9 to 12)
        else {
            semesterRadio.check(R.id.first);
            semester = 1;
        }

        semesterRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.first:
                        semester = 1;
                        break;
                    case R.id.second:
                        semester = 2;
                        break;
                    case R.id.summer:
                        semester = 3;
                        break;
                }
            }
        });

        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, Constants.coursesList);
            txtCourse.setAdapter(adapter);
        }
        // validate input
        txtCourse.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence charSequence) {
                Matcher m = pCourse.matcher(charSequence.toString().toUpperCase().trim());
                Logger.d(charSequence.toString().toUpperCase().trim());
                if (!m.find()) {return false;}
                String courseName = m.group(1);
                Logger.d(courseName);
                return Arrays.binarySearch(Constants.coursesNameList, courseName) > 0;
            }

            @Override
            public CharSequence fixText(CharSequence charSequence) {
                Snackbar.make(mainContent, "Wrong course", Snackbar.LENGTH_LONG).show();
                return "";
            }
        });
        txtCourse.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addCourse();
                return true;
            }
            return false;
        });
        txtCourse.setOnFocusChangeListener((view, hasFocus) -> {
            if (view.getId() == R.id.course && !hasFocus) {
                ((AutoCompleteTextView)view).performValidation();
            }
        });
        txtCourse.setOnItemClickListener((adapterView, view, i, l) -> addCourse());
    }

    private void refreshNextMenuItem(){
        nextMenuItem.setEnabled(mCourseList.size() > 0);
        nextMenuItem.setCheckable(mCourseList.size() > 0);
    }

    @OnClick(R.id.add)
    void addCourse(){
        txtCourse.performValidation();
        String courseString = txtCourse.getText().toString().toUpperCase().trim();
        Matcher m = pCourse.matcher(courseString);
        if (!courseString.equals("") && m.find()) {
            BCourse myCourse = new BCourse(courseString);
            if (!mCourseList.contains(myCourse)) {
                mCourseList.add(myCourse);
                fastItemAdapter.add(myCourse);
                txtCourse.setText("");
            } else {
                Snackbar.make(mainContent, "This course already added", Snackbar.LENGTH_LONG).show();
            }
        }
        refreshNextMenuItem();
    }

    @Override
    public void onStop() {
        super.onStop();
        // hide keyboard
        txtCourse.requestFocus();
        InputMethodManager imm =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtCourse.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCourseList != null){
            outState.putString(COURSES_LIST, new Gson().toJson(mCourseList, COURSES_LIST_TYPE));
        }
        super.onSaveInstanceState(outState);
    }
}
