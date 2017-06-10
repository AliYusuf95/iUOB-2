package com.muqdd.iuob2.features.schedule_builder;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.app.Constants;
import com.muqdd.iuob2.models.SectionTimeModel;
import com.muqdd.iuob2.network.ConnectivityInterceptor.NoConnectivityException;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.UOBSchedule;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class OptionsFragment extends BaseFragment {

    private final static String COURSES_LIST = "COURSES_LIST";
    private final static String SEMESTER = "SEMESTER";
    private final static Type COURSES_LIST_TYPE = new TypeToken<List<BCourseModel>>() {}.getType();
    private static final int SECTIONS_FILTER = 1;

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.combinations) TextView combinations;
    @BindView(R.id.alert) TextView alertText;
    @BindView(R.id.days) SegmentedGroup daySegment;
    @BindView(R.id.start) SegmentedGroup startSegment;
    @BindView(R.id.finish) SegmentedGroup finishSegment;
    @BindView(R.id.location) SegmentedGroup locationSegment;
    @BindView(R.id.section_filter) Button btnSectionFilter;
    @BindView(R.id.section_filter_status) ImageView imgSectionFilter;

    private View mView;
    private List<BCourseModel> allCourseList; // all courses data
    private List<BCourseModel> myCourseList; // filtered courses data
    private Dialog failDialog;
    private int dataLoadingCounter; // check if all courses data loaded
    private MenuItem nextMenuItem;
    private boolean sectionFilter;
    // selected filter options
    private String days;
    private String start;
    private String finish;
    private String location;

    public OptionsFragment() {
        // Required empty public constructor
    }

    public static OptionsFragment newInstance(String title, String semester, List<BCourseModel> courseList) {
        OptionsFragment fragment = new OptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(SEMESTER, semester);
        bundle.putString(COURSES_LIST, new Gson().toJson(courseList, COURSES_LIST_TYPE));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECTIONS_FILTER && resultCode == Activity.RESULT_OK){
            if (data.hasExtra(Constants.INTENT_SECTIONS_LIST)){
                String json = data.getStringExtra(Constants.INTENT_SECTIONS_LIST);
                allCourseList = new Gson().fromJson(json, COURSES_LIST_TYPE);
                sectionFilter = true;
                imgSectionFilter.setVisibility(View.VISIBLE);
                calculateCombinations();
            }
        }
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
        nextMenuItem = menu.findItem(R.id.next);
        if (btnSectionFilter != null) {
            nextMenuItem.setEnabled(btnSectionFilter.isEnabled());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.next:
                // start options fragment
                SummaryFragment fragment =
                        SummaryFragment.newInstance(getString(R.string.fragment_schedule_builder_summary), myCourseList);
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
        // stop hiding toolbar
        params.setScrollFlags(0);
    }

    private void initiate() {
        // initialize variables
        allCourseList = new Gson().fromJson(getArguments().getString(COURSES_LIST), COURSES_LIST_TYPE);
        if (allCourseList == null){
            Dialog dialog = infoDialog("Sorry","Some thing goes wrong pleas try again later.", "Cancel");
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    getActivity().onBackPressed();
                }
            });
            dialog.show();
        }
        myCourseList = new ArrayList<>();
        sectionFilter = false;

        // init options values
        days = "";
        start = "";
        finish = "";
        location = "";

        // options listeners
        daySegment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.d_any:
                        days = "";
                        break;
                    case R.id.d_mw:
                        days = "MW";
                        break;
                    case R.id.d_uth:
                        days = "UTH";
                        break;
                    case R.id.d_umth:
                        days = "UMTH";
                        break;
                    case R.id.d_utwh:
                        days = "UTWH";
                        break;
                }
                removeSectionFilter();
                calculateCombinations();
            }
        });
        startSegment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.s_any:
                        start = "";
                        break;
                    case R.id.s_9:
                        start = "09:00";
                        break;
                    case R.id.s_10:
                        start = "10:00";
                        break;
                    case R.id.s_11:
                        start = "11:00";
                        break;
                    case R.id.s_12:
                        start = "12:00";
                        break;
                }
                removeSectionFilter();
                calculateCombinations();
            }
        });
        finishSegment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.f_any:
                        finish = "";
                        break;
                    case R.id.f_12:
                        finish = "12:00";
                        break;
                    case R.id.f_1:
                        finish = "13:00";
                        break;
                    case R.id.f_2:
                        finish = "14:00";
                        break;
                    case R.id.f_3:
                        finish = "15:00";
                        break;
                }
                removeSectionFilter();
                calculateCombinations();
            }
        });
        locationSegment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.l_any:
                        location = "";
                        break;
                    case R.id.l_s:
                        location = "s";
                        break;
                    case R.id.l_i:
                        location = "i";
                        break;
                }
                removeSectionFilter();
                calculateCombinations();
            }
        });

        String semester = getArguments().getString(SEMESTER);
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        if ("2".equals(semester) || "3".equals(semester))
            year = String.valueOf(Integer.parseInt(year)-1);

        // disable filter options
        setSegmentGroupEnabled(daySegment, false);
        setSegmentGroupEnabled(startSegment, false);
        setSegmentGroupEnabled(finishSegment, false);
        setSegmentGroupEnabled(locationSegment, false);
        btnSectionFilter.setEnabled(false);

        // fetching data
        dataLoadingCounter = 0;
        for (int i = 0; i < allCourseList.size() ; i++){
            getCourseData(i, allCourseList.get(i), String.valueOf(year), semester);
        }
    }

    private void getCourseData(final int index, final BCourseModel course, final String year, final String semester){
        ServiceGenerator.createService(UOBSchedule.class)
                .sectionsList("1",course.courseName,course.departmentCode,course.courseNumber,
                        "0", year, semester)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200){
                            try {
                                String responseString = response.body().string();
                                if (responseString.contains("The Schedule Will Be Announced Later") ||
                                        responseString.trim().isEmpty()){
                                    throw new IllegalStateException();
                                }
                                // add sections list to the course
                                BCourseModel.parseSectionsData(allCourseList.get(index),responseString);
                            } catch (IOException e) {
                                if (failDialog == null) {
                                    failDialog = infoDialog("Sorry", "Some thing goes wrong while " +
                                            "getting data. Please try again later.", "close");
                                    failDialog.show();
                                }
                                Logger.w("Something goes wrong");
                            } catch (IllegalStateException e){
                                if (failDialog == null) {
                                    failDialog = infoDialog("Sorry", "Schedule for " + year + "\\" + semester + " is not " +
                                            "available. Make sure you select the right semester.", "close");
                                    failDialog.show();
                                }
                                Logger.w("Something goes wrong");
                            }
                            // is loading data finished
                            if (++dataLoadingCounter == allCourseList.size()){
                                dataLoadingCounter = 0;
                                // enable filter options
                                setSegmentGroupEnabled(daySegment, true);
                                setSegmentGroupEnabled(startSegment, true);
                                setSegmentGroupEnabled(finishSegment, true);
                                setSegmentGroupEnabled(locationSegment, true);
                                btnSectionFilter.setEnabled(true);
                                // calculate combinations
                                calculateCombinations();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (failDialog == null) {
                            failDialog = t instanceof NoConnectivityException ?
                                    infoDialog("Error", "The Internet Connection appears to be offline.", "close") :
                                    infoDialog("Sorry", "Schedule for " + year + "\\" + semester + " is not " +
                                            "available. Make sure you select the right semester.", "close");
                            failDialog.show();
                        }
                    }
                });
    }

    private void calculateCombinations(){
        int cCount = 0; // combinations count
        myCourseList.clear(); // clear filtered courses list
        for (BCourseModel course : allCourseList){
            int sCount = 0; // available sections count
            List<BSectionModel> validSections = new ArrayList<>(); // valid section list
            for (BSectionModel section : course.sections){
                // check if selection required or not; from [section filter]
                boolean isValidSection = !sectionFilter || section.isSelected();
                for (SectionTimeModel time: section.times){
                    isValidSection = isValidTime(time) && isValidSection;
                }
                // add to section count if the section is valid
                sCount += isValidSection ? 1:0;
                // set section validity
                section.withSetSelected(isValidSection);
                if (isValidSection) {
                    // add the section in the list
                    validSections.add(section);
                }
            }
            // no section available for this course; then stop counting
            if (sCount == 0) {
                // reset cCount
                cCount = 0;
                break;
            }
            // init cCount if needed
            cCount = cCount == 0 ? 1 : cCount;

            BCourseModel tempCourse = new BCourseModel(course);
            tempCourse.sections = validSections; // set valid sections
            myCourseList.add(tempCourse); // add course to filtered list
            // multiply cCount with sCount if available
            cCount *= sCount > 0 ? sCount : 1;
        }
        if (cCount < 1){
            // clear filtered courses list if combinations count = 0
            myCourseList.clear();
        } else if (cCount > 20000) {
            alertText.setVisibility(View.VISIBLE);
            nextMenuItem.setEnabled(false);
        } else {
            alertText.setVisibility(View.INVISIBLE);
            nextMenuItem.setEnabled(true);
        }
        combinations.setText(String.valueOf(cCount));
    }

    private boolean isValidTime(SectionTimeModel time){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        boolean isValid = true;

        if (!days.equals(""))
            isValid = checkDays(days, time.days);

        if (!start.equals("")) {
            try {
                Date from = dateFormat.parse(time.from);
                Date start = dateFormat.parse(this.start);
                isValid = (from.equals(start) || from.after(start)) && isValid;
            } catch (ParseException e) {
                isValid = false;
            }
        }

        if (!finish.equals("")) {
            try {
                Date to = dateFormat.parse(time.to);
                Date finish = dateFormat.parse(this.finish);
                isValid = (to.equals(finish) || to.before(finish)) && isValid;
            } catch (ParseException e) {
                isValid = false;
            }
        }

        if (!location.equals("")) {
            boolean isSakeer;
            try {
                int number = Integer.parseInt(time.room.substring(0, time.room.indexOf("-") - 1));
                isSakeer = !(number > 0 && number < 38);
            } catch (NumberFormatException e) {
                isSakeer = !time.room.startsWith("A27");
            }
            isValid = (location.equals("s") == isSakeer) && isValid;
        }

        return isValid;
    }

    private boolean checkDays(String availableDays, String days) {
        if (days.length() == 0)
            return false;
        if (availableDays.length() == 0)
            return true;

        availableDays = availableDays.toUpperCase();
        days = days.toUpperCase();
        boolean found = true;
        for (char c : days.toCharArray()){
            found = availableDays.indexOf(c) > -1 && found;
        }
        return found;
    }

    private void setSegmentGroupEnabled(SegmentedGroup segmentedGroup, boolean enabled) {
        segmentedGroup.setTintColor(ContextCompat.getColor(getContext(),
                enabled ? R.color.colorPrimaryDark : R.color.colorPrimaryLight));
        for(int i = 0; i < segmentedGroup.getChildCount(); i++){
            segmentedGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    private void removeSectionFilter() {
        if (sectionFilter){
            infoDialog("Reset","[Section Filter] has been reset. Please reselect your options again.","close").show();
            sectionFilter = false;
            imgSectionFilter.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.probability_info)
    protected void showProbabilityInfo() {
        infoDialog("Info","Number of possible combinations is the sectionNumber of MAXIMUM possible " +
                "combinations for the courses you entered. It should be less than 20000 in order to " +
                "continue to the next step which will show you the TRUE sectionNumber of combinations. " +
                "You can minimize the sectionNumber of possible combinations by selecting different " +
                "options below.","Close")
        .show();
    }

    @OnClick(R.id.filter_info)
    protected void showFilterInfo() {
        infoDialog("Info","Use [Section Filter] for extra options. You can choose what section you" +
                "want to be included individually. Please use [Section Filter] after selecting the " +
                "Working Days/Times/Location.","Close")
        .show();
    }

    @OnClick(R.id.section_filter)
    protected void startSectionFilter() {
        Intent intent = new Intent(getActivity(), SectionsFilterActivity.class);
        intent.putExtra(Constants.INTENT_SECTIONS_LIST, new Gson().toJson(allCourseList, COURSES_LIST_TYPE));
        startActivityForResult(intent, SECTIONS_FILTER);
    }
}
