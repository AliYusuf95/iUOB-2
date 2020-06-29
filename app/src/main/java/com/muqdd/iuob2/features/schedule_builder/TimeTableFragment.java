package com.muqdd.iuob2.features.schedule_builder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
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
import com.muqdd.iuob2.models.User;
import com.muqdd.iuob2.features.my_schedule.MyCourse;
import com.muqdd.iuob2.models.Timing;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

    @BindView(R.id.main_content)
    SwipeRefreshLayout mainContent;
    @BindView(R.id.u_layout) LinearLayout uLayout;
    @BindView(R.id.m_layout) LinearLayout mLayout;
    @BindView(R.id.t_layout) LinearLayout tLayout;
    @BindView(R.id.w_layout) LinearLayout wLayout;
    @BindView(R.id.h_layout) LinearLayout hLayout;

    private View mView;
    private Map<Timing, MyCourse> uList;
    private Map<Timing, MyCourse> mList;
    private Map<Timing, MyCourse> tList;
    private Map<Timing, MyCourse> wList;
    private Map<Timing, MyCourse> hList;
    private List<MyCourse> mCourses;
    private BSchedule mSchedule;

    public TimeTableFragment() {
        // Required empty public constructor
    }

    public static TimeTableFragment newInstance(String title, BSchedule schedule) {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(SCHEDULE, new Gson().toJson(schedule, BSchedule.class));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_my_schedule, container, false);
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
        // check primary data
        checkPrimaryData();
        buildMySchedule();
    }

    private void checkPrimaryData() {
        if (mSchedule == null && getContext() != null) {
            mSchedule = new BSchedule(new ArrayList<BSection>());
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
        mSchedule = new Gson().fromJson(getArguments().getString(SCHEDULE), BSchedule.class);
        checkPrimaryData();
        // convert courses data
        mCourses = new ArrayList<>();
        for (BSection section : mSchedule.sections) {
            MyCourse course = new MyCourse(section);
            mCourses.add(course);
        }

        // disable swipe to refresh
        mainContent.setEnabled(false);

        // section time comparator
        Comparator<Timing> comparator = new Comparator<Timing>(){
            @Override
            public int compare(Timing t1, Timing t2) {
                // compare from then to then room to sort sections
                int f,t;
                return (f = t1.getTimeFrom().compareTo(t2.getTimeFrom())) == 0 ?
                        ((t = t1.getLocation().compareTo(t2.getLocation())) == 0 ?
                                t1.getLocation().compareTo(t2.getLocation()) : t ): f;
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
        for (MyCourse course : mCourses) {
            if (course.getTimingLegacy() != null) {
                for (Timing time : course.getTimingLegacy()) {
                    if (time.getDay().contains("U")) {
                        uList.put(time, course);
                    }
                    if (time.getDay().contains("M")) {
                        mList.put(time, course);
                    }
                    if (time.getDay().contains("T")) {
                        tList.put(time, course);
                    }
                    if (time.getDay().contains("W")) {
                        wList.put(time, course);
                    }
                    if (time.getDay().contains("H")) {
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

    private void addCoursesForLayout(LinearLayout layout, Map<Timing, MyCourse> list) {
        layout.removeAllViews();
        for (Timing time : list.keySet()){
            if(list.get(time) != null)
                layout.addView(createScheduleCell(list.get(time), time));
            else {
                Logger.i(time.toString());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private View createScheduleCell(final MyCourse course, Timing time) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.cell_schedule, null, false);
        view.findViewById(R.id.layout).setBackgroundColor(course.getBgColor());
        ((TextView)view.findViewById(R.id.course)).setText(course.getCourseId());
        ((TextView)view.findViewById(R.id.time_from)).setText(time.getTimeFrom());
        ((TextView)view.findViewById(R.id.time_to)).setText(time.getTimeTo());
        ((TextView)view.findViewById(R.id.room)).setText(time.getLocation());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());

                // prepare dialog layout
                LayoutInflater inflater =
                        (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.dialog_course_details, null);
                ((TextView)dialogView.findViewById((R.id.title))).setText(course.getCourseId());
                ((TextView)dialogView.findViewById((R.id.section))).setText("Section: "+course.getSectionNo());
                ((TextView)dialogView.findViewById((R.id.doctor))).setText("Doctor: "+course.getInstructor());
                if (course.getExam() != null) {
                    ((TextView) dialogView.findViewById((R.id.final_time))).setText("Final : " + course.getExam().toString());
                } else {
                    dialogView.findViewById((R.id.final_time)).setVisibility(View.GONE);
                }
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