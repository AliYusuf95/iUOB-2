package com.muqdd.iuob2.features.schedule_builder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.app.User;
import com.muqdd.iuob2.models.MyCourseModel;
import com.muqdd.iuob2.models.SectionTimeModel;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */
@SuppressWarnings("FieldCanBeLocal")
public class TimeTableFragment extends BaseFragment {

    private final static String SCHEDULE = "SCHEDULE";
    private static final int MENU_DETAILS = Menu.FIRST;

    @BindView(R.id.main_content) SwipeRefreshLayout mainContent;
    @BindView(R.id.u_layout) LinearLayout uLayout;
    @BindView(R.id.m_layout) LinearLayout mLayout;
    @BindView(R.id.t_layout) LinearLayout tLayout;
    @BindView(R.id.w_layout) LinearLayout wLayout;
    @BindView(R.id.h_layout) LinearLayout hLayout;

    private View mView;
    private Map<SectionTimeModel, MyCourseModel> uList;
    private Map<SectionTimeModel, MyCourseModel> mList;
    private Map<SectionTimeModel, MyCourseModel> tList;
    private Map<SectionTimeModel, MyCourseModel> wList;
    private Map<SectionTimeModel, MyCourseModel> hList;
    private List<MyCourseModel> mCourses;
    private BScheduleModel mSchedule;

    public TimeTableFragment() {
        // Required empty public constructor
    }

    public static TimeTableFragment newInstance(String title, BScheduleModel schedule) {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(SCHEDULE, new Gson().toJson(schedule, BScheduleModel.class));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_time_table, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem detailsMenuItem = menu.add(0, MENU_DETAILS, Menu.NONE, R.string.share)
                .setIcon(R.drawable.ic_info_outline_24dp);
        detailsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        detailsMenuItem.getIcon().setTint(ContextCompat.getColor(getContext(), R.color.white));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case MENU_DETAILS:
                // just skip that
                if (mSchedule == null){
                    return true;
                }
                // start schedule details fragment
                ScheduleDetailsFragment fragment =
                        ScheduleDetailsFragment.newInstance(getString(R.string.fragment_schedule_details), mSchedule);
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
        // check primary data
        checkPrimaryData();
        buildMySchedule();
    }

    private void checkPrimaryData() {
        if (mSchedule == null && getContext() != null) {
            mSchedule = new BScheduleModel(new ArrayList<BSectionModel>());
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
        mSchedule = new Gson().fromJson(getArguments().getString(SCHEDULE), BScheduleModel.class);
        checkPrimaryData();
        // convert courses data
        mCourses = new ArrayList<>();
        for (BSectionModel section : mSchedule.sections) {
            MyCourseModel course = new MyCourseModel(section.parentCourse.courseName,
                    section.parentCourse.courseNumber, section.sectionNumber);
            course.doctor = section.doctor;
            course.times = section.times;
            course.finalExamDate = section.finalExamDate;
            course.finalExamTime = section.finalExamTime;
            mCourses.add(course);
        }

        // disable swipe to refresh
        mainContent.setEnabled(false);

        // section time comparator
        Comparator comparator = new Comparator<SectionTimeModel>(){
            @Override
            public int compare(SectionTimeModel t1, SectionTimeModel t2) {
                // compare from then to then room to sort sections
                int f,t;
                return (f = t1.from.compareTo(t2.from)) == 0 ?
                        ((t = t1.room.compareTo(t2.room)) == 0 ? t1.room.compareTo(t2.room) : t ): f;
            }
        };

        // init lists with comparator
        uList = new TreeMap<>(comparator);
        mList = new TreeMap<>(comparator);
        tList = new TreeMap<>(comparator);
        wList = new TreeMap<>(comparator);
        hList = new TreeMap<>(comparator);
    }

    private void buildMySchedule() {
        // clear lists
        uList.clear();
        mList.clear();
        tList.clear();
        wList.clear();
        hList.clear();
        for (MyCourseModel course : mCourses) {
            if (course.times != null) {
                for (SectionTimeModel time : course.times) {
                    if (time.days.contains("U")) {
                        uList.put(time, course);
                    }
                    if (time.days.contains("M")) {
                        mList.put(time, course);
                    }
                    if (time.days.contains("T")) {
                        tList.put(time, course);
                    }
                    if (time.days.contains("W")) {
                        wList.put(time, course);
                    }
                    if (time.days.contains("H")) {
                        hList.put(time, course);
                    }
                }
            }
        }
        // update UI
        runOnUi(new Runnable() {
            @Override
            public void run() {
                addCoursesForLayout(uLayout,uList);
                addCoursesForLayout(mLayout,mList);
                addCoursesForLayout(tLayout,tList);
                addCoursesForLayout(wLayout,wList);
                addCoursesForLayout(hLayout,hList);

                mainContent.setRefreshing(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        User.setFetchingData(false);
                    }
                },200);
            }
        });
    }

    private void addCoursesForLayout(LinearLayout layout, Map<SectionTimeModel, MyCourseModel> list) {
        layout.removeAllViews();
        for (SectionTimeModel time : list.keySet()){
            if(list.get(time) != null)
                layout.addView(createScheduleCell(list.get(time), time));
            else {
                Logger.i("null");
                Logger.i(time.toString());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private View createScheduleCell(final MyCourseModel course, SectionTimeModel time) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.cell_schedule, null, false);
        view.findViewById(R.id.layout).setBackgroundColor(course.bgColor);
        ((TextView)view.findViewById(R.id.course)).setText(course.getCourseTitle());
        ((TextView)view.findViewById(R.id.time_from)).setText(time.from);
        ((TextView)view.findViewById(R.id.time_to)).setText(time.to);
        ((TextView)view.findViewById(R.id.room)).setText(time.room);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());

                // prepare dialog layout
                LayoutInflater inflater =
                        (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.dialog_course_details, null);
                ((TextView)dialogView.findViewById((R.id.title))).setText(course.getCourseTitle());
                ((TextView)dialogView.findViewById((R.id.section))).setText("Section: "+course.sectionNumber);
                ((TextView)dialogView.findViewById((R.id.doctor))).setText("Doctor: "+course.doctor);
                ((TextView)dialogView.findViewById((R.id.final_time))).setText("Final : "+course.getFinalExam());
                dialogView.findViewById((R.id.close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                    }
                });
                // show dialog
                dialog.setContentView(dialogView);
                dialog.show();
            }
        });
        return view;
    }
}