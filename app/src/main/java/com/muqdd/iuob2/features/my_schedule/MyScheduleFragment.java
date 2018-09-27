package com.muqdd.iuob2.features.my_schedule;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.widgets.FullScheduleWidget;
import com.muqdd.iuob2.models.User;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.RestResponse;
import com.muqdd.iuob2.models.Timing;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.UOBSchedule;
import com.orhanobut.logger.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */
@SuppressWarnings("FieldCanBeLocal")
public class MyScheduleFragment extends BaseFragment {

    @BindView(R.id.main_content) SwipeRefreshLayout mainContent;
    @BindView(R.id.u_layout) LinearLayout uLayout;
    @BindView(R.id.m_layout) LinearLayout mLayout;
    @BindView(R.id.t_layout) LinearLayout tLayout;
    @BindView(R.id.w_layout) LinearLayout wLayout;
    @BindView(R.id.h_layout) LinearLayout hLayout;
    @BindDrawable(R.drawable.ic_notifications_active_24dp) Drawable notificationActive;
    @BindDrawable(R.drawable.ic_notifications_off_24dp) Drawable notificationOff;

    private View mView;
    private Map<Timing, MyCourse> uList;
    private Map<Timing, MyCourse> mList;
    private Map<Timing, MyCourse> tList;
    private Map<Timing, MyCourse> wList;
    private Map<Timing, MyCourse> hList;

    public MyScheduleFragment() {
        // Required empty public constructor
    }

    public static MyScheduleFragment newInstance() {
        MyScheduleFragment fragment = new MyScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.MY_SCHEDULE.toString());
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
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_schedule_menu, menu);
        // init notification icon
        menu.findItem(R.id.notification)
                .setIcon(User.isNotificationOn(getContext()) ? notificationActive : notificationOff)
                .setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.edit:
                if (User.isFetchingData()){
                    Snackbar.make(mainContent,"Please wait fetching data",Snackbar.LENGTH_SHORT).show();
                } else {
                    AddCoursesFragment fragment =
                            AddCoursesFragment.newInstance(getString(R.string.fragment_add_courses));
                    displayFragment(fragment);
                }
                return true;
            case R.id.notification:
                changeNotificationState(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        // build schedule
        mainContent.setRefreshing(true);
        if (User.isCoursesUpdated(getContext())) {
            // update UI
            buildMySchedule();
        } else {
            fetchMyScheduleData();
        }
    }

    private void initiate() {
        //mainContent.setRefreshing(true);
        mainContent.setColorSchemeResources(
                R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryLight);
        mainContent.setOnRefreshListener(this::fetchMyScheduleData);

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

    private void fetchMyScheduleData() {
        MySchedule mySchedule = User.getMySchedule(getContext());
        ServiceGenerator.createService(UOBSchedule.class).sectionsList(mySchedule.getYear(),
                mySchedule.getSemester(), mySchedule.getSectionsParam())
                .enqueue(new Callback<RestResponse<List<List<MyCourse>>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<List<MyCourse>>>> call, Response<RestResponse<List<List<MyCourse>>>> response) {
                if (response.body() != null && response.body().getStatusCode() == 200) {
                    User.updateCourses(getContext(), MySchedule.getCoursesList(response.body().getData()));
                    Logger.d("build");
                    buildMySchedule();
                } else {
                    mainContent.setRefreshing(false);
                    Logger.e("error accord while fetching data");
                }
            }

            @Override
            public void onFailure(Call<RestResponse<List<List<MyCourse>>>> call, Throwable t) {
                mainContent.setRefreshing(false);
                Logger.e("error accord while fetching data");
            }
        });
    }

    private void buildMySchedule() {
        // clear lists
        uList.clear();
        mList.clear();
        tList.clear();
        wList.clear();
        hList.clear();
        for (MyCourse course : User.getMySchedule(getContext()).getCourseList()) {
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
        addCoursesForLayout(uLayout,uList);
        addCoursesForLayout(mLayout,mList);
        addCoursesForLayout(tLayout,tList);
        addCoursesForLayout(wLayout,wList);
        addCoursesForLayout(hLayout,hList);

        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), FullScheduleWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int ids[] = AppWidgetManager.getInstance(getActivity().getApplication())
                    .getAppWidgetIds(new ComponentName(getActivity().getApplication(), FullScheduleWidget.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            getActivity().sendBroadcast(intent);
        }

        mainContent.setRefreshing(false);
    }

    private void addCoursesForLayout(LinearLayout layout, Map<Timing, MyCourse> list) {
        layout.removeAllViews();
        for (Timing time : list.keySet()){
            if(list.get(time) != null)
                layout.addView(createScheduleCell(list.get(time), time));
            else {
                Logger.i("null");
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

    private void changeNotificationState(MenuItem item) {
        // change current notification state
        boolean notificationState = User.setNotification(getContext(), !User.isNotificationOn(getContext()));
        // set new icon based on state
        item.setIcon(notificationState ? notificationActive : notificationOff);
    }
}