package com.muqdd.iuob2.features.schedule_builder;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.Menu;
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
import com.muqdd.iuob2.models.FinalExam;
import com.muqdd.iuob2.models.RestResponse;
import com.muqdd.iuob2.models.Timing;
import com.muqdd.iuob2.network.ConnectivityInterceptor.NoConnectivityException;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.UOBSchedule;
import com.muqdd.iuob2.utils.SPHelper;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
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
    private final static Type COURSES_LIST_TYPE = new TypeToken<List<BCourse>>() {
    }.getType();
    private static final int SECTIONS_FILTER = 1;

    @BindView(R.id.main_content)
    LinearLayout mainContent;
    @BindView(R.id.combinations)
    TextView combinations;
    @BindView(R.id.alert)
    TextView alertText;
    @BindView(R.id.days)
    SegmentedGroup daySegment;
    @BindView(R.id.start)
    SegmentedGroup startSegment;
    @BindView(R.id.finish)
    SegmentedGroup finishSegment;
    @BindView(R.id.location)
    SegmentedGroup locationSegment;
    @BindView(R.id.section_filter)
    Button btnSectionFilter;
    @BindView(R.id.section_filter_status)
    ImageView imgSectionFilter;

    private View mView;
    private List<BCourse> allCourseList; // all courses data
    private List<BCourse> myCourseList; // filtered courses data
//    private int dataLoadingCounter; // check if all courses data loaded
    private int cCount; // combinations count
    private MenuItem nextMenuItem;
    private boolean sectionFilter;
    // selected filter options
    private String days;
    private String start;
    private String finish;
    private String location;
    private Dialog mClashDialog;

    public OptionsFragment() {
        // Required empty public constructor
    }

    public static OptionsFragment newInstance(String title, int semester, List<BCourse> courseList) {
        OptionsFragment fragment = new OptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putInt(SEMESTER, semester);
        bundle.putString(COURSES_LIST, new Gson().toJson(courseList, COURSES_LIST_TYPE));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECTIONS_FILTER){
            if (resultCode == Activity.RESULT_OK){
                // get data from shared preference
                String json = SPHelper.getFromPrefs(getContext(), Constants.SB_SECTIONS_LIST);
                if (json != null && !json.equals("")){
                    allCourseList = new Gson().fromJson(json, COURSES_LIST_TYPE);
                    sectionFilter = true;
                    imgSectionFilter.setVisibility(View.VISIBLE);
                    calculateCombinations();
                }
            }
            // clear shared preference
            SPHelper.deleteFromPrefs(getContext(), Constants.SB_SECTIONS_LIST);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.schedule_builder_menu, menu);
        nextMenuItem = menu.findItem(R.id.next);
        nextMenuItem.setEnabled(cCount > 0 && cCount < 20000);
        if (btnSectionFilter != null) {
            nextMenuItem.setEnabled(btnSectionFilter.isEnabled());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.next:
                // start summary fragment
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
        // check primary data
        checkPrimaryData();
    }

    private void checkPrimaryData() {
        if (allCourseList == null && getContext() != null){
            allCourseList = new ArrayList<>();
            Dialog dialog = infoDialog("Sorry", "Some thing goes wrong pleas try again later.", "Cancel");
            dialog.setOnDismissListener(dialogInterface -> getActivity().onBackPressed());
            dialog.show();
        }
    }

    private void initiate() {
        // initialize variables
        allCourseList = new Gson().fromJson(getArguments().getString(COURSES_LIST), COURSES_LIST_TYPE);
        checkPrimaryData();
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

        int semester = getArguments().getInt(SEMESTER);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        // Second semester
        if (month > 3 && month < 7){
            year -=1;
            Logger.d("case 1");
        }
        // Summer semester
        else if (month > 6 && month < 9) {
            if (semester != 1) {
                year -=1;
            }
            Logger.d("case 2");
        }
        // First semester (from 9 to 12)
        else {
            if (semester == 3) {
                year -=1;
            }
            Logger.d("case 3");
        }

        // disable filter options
        setSegmentGroupEnabled(daySegment, false);
        setSegmentGroupEnabled(startSegment, false);
        setSegmentGroupEnabled(finishSegment, false);
        setSegmentGroupEnabled(locationSegment, false);
        btnSectionFilter.setEnabled(false);

        // fetching data
        getCourseData(year, semester);
    }

    private void getCourseData(final int year, final int semester){
        ServiceGenerator.createService(UOBSchedule.class)
                .coursesSectionsList(year, semester, BCourse.getCoursesIds(allCourseList))
                .enqueue(new Callback<RestResponse<List<BCourse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<RestResponse<List<BCourse>>> call, @NonNull Response<RestResponse<List<BCourse>>> response) {
                        if (response.body().getStatusCode() != 200) {
                            if (getContext() != null) {
                                infoDialog("Sorry", "Some thing goes wrong while " +
                                        "getting data. Please try again later.", "close").show();
                            }
                            Logger.w("Something goes wrong");
                        } else {
                            if (response.body().getData().size() < 0) {
                                if (getContext() != null) {
                                    infoDialog("Sorry", "Some thing goes wrong while " +
                                            "getting data. Please try again later.", "close").show();
                                }
                                Logger.w("Something goes wrong");
                            } else {
                                allCourseList = response.body().getData();
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
                    public void onFailure(@NonNull Call<RestResponse<List<BCourse>>> call, @NonNull Throwable t) {
                        if (getContext() != null) {
                            Dialog dialog = t instanceof NoConnectivityException ?
                                    infoDialog("Error", "The Internet Connection appears to be offline.", "close") :
                                    infoDialog("Sorry", "Cannot perform the request please try again later.", "close");
                            dialog.show();
                        }
                    }
                });
    }

    private void calculateCombinations() {
        checkPrimaryData(); // just to make sure and try to avoid crashes
        cCount = 0; // combinations count
        myCourseList.clear(); // clear filtered courses list
        mClashDialog = null;
        AsyncTask.execute(() -> {
            for (BCourse course : allCourseList) {
                // check final clash
                for (BCourse tempCourse : allCourseList) {
                    // check if the courses have sections
                    if (course.getSections() == null || course.getSections().size() < 1 ||
                            tempCourse.getSections() == null || tempCourse.getSections().size() < 1)
                        continue;
                    // check in there is clash in exam day
                    FinalExam examA = course.getSections().get(0).getExam();
                    FinalExam examB = tempCourse.getSections().get(0).getExam();
                    if (!course.equals(tempCourse) && examA != null && examB != null && examA.hasClash(examB)) {
                        if ((mClashDialog == null || !mClashDialog.isShowing()) && getContext() != null) {
                            mClashDialog = infoDialog("Final exam clash", "There is a clash in the final exam" +
                                    "between " + course.getId() + " and " + tempCourse.getId(), "close");
                            runOnUi(() -> mClashDialog.show());
                            break;
                        }
                    }
                }
                int sCount = 0; // available sections count
                List<BSection> validSections = new ArrayList<>(); // valid section list
                for (BSection section : course.getSections()) {
                    // check if selection required or not; from [section filter]
                    boolean isValidSection = !sectionFilter || section.isSelected();
                    for (Timing time : section.getTimingLegacy()) {
                        isValidSection = isValidTime(time) && isValidSection;
                    }
                    // add to section count if the section is valid
                    sCount += isValidSection ? 1 : 0;
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

                BCourse tempCourse = new BCourse(course);
                tempCourse.setSections(validSections); // set valid sections
                myCourseList.add(tempCourse); // add course to filtered list
                // multiply cCount with sCount if available
                cCount *= sCount > 0 ? sCount : 1;
            }

            runOnUi(() -> {
                if (cCount < 1) {
                    // clear filtered courses list if combinations count = 0
                    myCourseList.clear();
                    alertText.setVisibility(View.INVISIBLE);
                    if (nextMenuItem != null) {
                        nextMenuItem.setEnabled(false);
                    }
                } else if (cCount > 20000) {
                    alertText.setVisibility(View.VISIBLE);
                    if (nextMenuItem != null) {
                        nextMenuItem.setEnabled(false);
                    }
                } else {
                    alertText.setVisibility(View.INVISIBLE);
                    if (nextMenuItem != null) {
                        nextMenuItem.setEnabled(true);
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putLong("combinations_count", cCount);
                bundle.putLong("curses_count", allCourseList.size());
                mFirebaseAnalytics.logEvent("schedule_combinations", bundle);
                combinations.setText(String.valueOf(cCount));
            });
        });
    }

    private boolean isValidTime(Timing time) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        boolean isValid = true;

        if (!days.equals(""))
            isValid = checkDays(days, time.getDay());

        if (!start.equals("")) {
            try {
                Date from = dateFormat.parse(time.getTimeFrom());
                Date start = dateFormat.parse(this.start);
                isValid = (from.equals(start) || from.after(start)) && isValid;
            } catch (ParseException e) {
                isValid = false;
            }
        }

        if (!finish.equals("")) {
            try {
                Date to = dateFormat.parse(time.getTimeTo());
                Date finish = dateFormat.parse(this.finish);
                isValid = (to.equals(finish) || to.before(finish)) && isValid;
            } catch (ParseException e) {
                isValid = false;
            }
        }

        if (!location.equals("")) {
            boolean isSakeer;
            try {
                int number = Integer.parseInt(time.getLocation().substring(0, time.getLocation().indexOf("-") - 1));
                isSakeer = !(number > 0 && number < 38);
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                isSakeer = !time.getLocation().startsWith("A27");
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
        for (char c : days.toCharArray()) {
            found = availableDays.indexOf(c) > -1 && found;
        }
        return found;
    }

    private void setSegmentGroupEnabled(SegmentedGroup segmentedGroup, boolean enabled) {
        // avoid null exception
        if (getContext() == null)
            return;
        segmentedGroup.setTintColor(ContextCompat.getColor(getContext(),
                enabled ? R.color.colorPrimaryDark : R.color.colorPrimaryLight));
        for (int i = 0; i < segmentedGroup.getChildCount(); i++) {
            segmentedGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    private void removeSectionFilter() {
        if (sectionFilter && getContext() != null) {
            infoDialog("Reset", "[Section Filter] has been reset. Please reselect your options again.", "close").show();
            sectionFilter = false;
            imgSectionFilter.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.probability_info)
    protected void showProbabilityInfo() {
        if (getContext() == null) {
            return;
        }
        infoDialog("Info", "Number of possible combinations is the sectionNumber of MAXIMUM possible " +
                "combinations for the courses you entered. It should be less than 20000 in order to " +
                "continue to the next step which will show you the TRUE sectionNumber of combinations. " +
                "You can minimize the sectionNumber of possible combinations by selecting different " +
                "options below.", "Close")
                .show();
    }

    @OnClick(R.id.filter_info)
    protected void showFilterInfo() {
        if (getContext() == null) {
            return;
        }
        infoDialog("Info", "Use [Section Filter] for extra options. You can choose what section you" +
                "want to be included individually. Please use [Section Filter] after selecting the " +
                "Working Days/Times/Location.", "Close")
                .show();
    }

    @OnClick(R.id.section_filter)
    protected void startSectionFilter() {
        // save sections data in shared preference
        SPHelper.saveToPrefs(getContext(), Constants.SB_SECTIONS_LIST, new Gson().toJson(allCourseList, COURSES_LIST_TYPE));
        Intent intent = new Intent(getActivity(), SectionsFilterActivity.class);
        startActivityForResult(intent, SECTIONS_FILTER);
    }
}
