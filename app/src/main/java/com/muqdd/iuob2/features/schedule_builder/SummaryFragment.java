package com.muqdd.iuob2.features.schedule_builder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class SummaryFragment extends BaseFragment {

    private final static String COURSES_LIST = "COURSES_LIST";
    private final static String SCHEDULES_LIST = "SCHEDULES_LIST";
    private final static Type COURSES_LIST_TYPE = new TypeToken<List<BCourseModel>>() {}.getType();
    private final static Type SCHEDULES_LIST_TYPE = new TypeToken<List<List<BSectionModel>>>() {}.getType();

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.lbl_combinations) TextView lblCombinations;

    private View mView;
    private List<BCourseModel> mCourseList; // filtered courses data
    private List<List<BSectionModel>> mSchedulesList; // available schedules
    private MenuItem nextMenuItem;
    private AsyncTask<Void, Void, Set<List<BSectionModel>>> calculationTask;

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance(String title, List<BCourseModel> courseList) {
        SummaryFragment fragment = new SummaryFragment();
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
            mView = inflater.inflate(R.layout.fragment_schedule_builder_summary, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.schedule_builder_menu, menu);
        nextMenuItem = menu.findItem(R.id.next);
        if (mSchedulesList != null) {
            nextMenuItem.setEnabled(mSchedulesList.size() > 0); // default value
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.next:
                // start options fragment
                SchedulesFragment fragment =
                        SchedulesFragment.newInstance(getString(R.string.fragment_schedule_builder_schedules), mSchedulesList);
                displayFragment(fragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        params.setScrollFlags(0); // stop hiding toolbar
        checkPrimaryData();
        if (nextMenuItem != null && mSchedulesList != null) {
            nextMenuItem.setEnabled(mSchedulesList.size() > 0);
        } else if (nextMenuItem != null){
            nextMenuItem.setEnabled(false);
        }
    }

    private void checkPrimaryData() {
        if (mCourseList == null && getContext() != null){
            mCourseList = new ArrayList<>();
            Dialog dialog = infoDialog("Sorry","Some thing goes wrong pleas try again later.", "Cancel");
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    getActivity().onBackPressed();
                }
            });
            dialog.show();
        }
    }

    private void initiate() {
        // initialize variables
        mCourseList = new Gson().fromJson(getArguments().getString(COURSES_LIST), COURSES_LIST_TYPE);
        checkPrimaryData();
        mSchedulesList = new ArrayList<>();

        calculationTask = new AsyncTask<Void, Void, Set<List<BSectionModel>>>() {
            @Override
            protected Set<List<BSectionModel>> doInBackground(Void... voids) {
                List<List<BSectionModel>> allSectionsList = new ArrayList<>();
                for (BCourseModel course : mCourseList){
                    allSectionsList.add(course.sections);
                }
                return getCombinations(allSectionsList);
            }

            @Override
            protected void onPostExecute(Set<List<BSectionModel>> lists) {
                super.onPostExecute(lists);
                if (!isCancelled()) {
                    mSchedulesList =  new ArrayList(lists);
                    int count = mSchedulesList.size();
                    progressBar.setVisibility(View.GONE);
                    lblCombinations.setText(String.valueOf(count));
                    if (count > 0 && nextMenuItem != null) {
                        nextMenuItem.setEnabled(true);
                    } else if (getContext() != null){
                        infoDialog("Not found", "No schedule found. Pleas go back and choose other " +
                                "options or other coerces", "Close").show();
                    }
                }
            }
        };

        // run the task
        calculationTask.execute();
    }


    private Set<List<BSectionModel>> getCombinations(List<List<BSectionModel>> lists) {
        Set<List<BSectionModel>> combinations = new HashSet<>();
        Set<List<BSectionModel>> newCombinations;

        // just to make sure is not empty list
        if (lists.size() == 0) {
            return combinations;
        }

        int index = 0;

        // extract each of the element in the first list
        // and add each to ints as a new list
        for(BSectionModel i: lists.get(0)) {
            List<BSectionModel> newList = new ArrayList<>();
            newList.add(i);
            combinations.add(newList);
        }
        index++;

        while(index < lists.size()) {
            List<BSectionModel> nextList = lists.get(index);
            newCombinations = new HashSet<>();
            for(List<BSectionModel> first: combinations) {
                for(BSectionModel second: nextList) {
                    List<BSectionModel> newList = new ArrayList<>();
                    newList.addAll(first);
                    newList.add(second);
                    if (sectionsHasClash(newList)) {
                        continue;
                    }
                    newCombinations.add(newList);
                }
            }
            combinations = newCombinations;
            index++;
        }
        return combinations;
    }

    private boolean sectionsHasClash(List<BSectionModel> sections) {
        for (int i=0 ; i<sections.size()-1 ; i++){
            for (int j=i+1 ; j<sections.size() ; j++) {
                if (sections.get(i).hasClash(sections.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop calculating
        calculationTask.cancel(true);
    }
}
