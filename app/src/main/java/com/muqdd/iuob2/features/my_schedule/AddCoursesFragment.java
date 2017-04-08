package com.muqdd.iuob2.features.my_schedule;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.app.Constants;
import com.muqdd.iuob2.features.semester_schedule.SectionsFragment;
import com.muqdd.iuob2.models.CourseModel;
import com.muqdd.iuob2.models.MyCourseModel;
import com.muqdd.iuob2.models.SectionModel;
import com.muqdd.iuob2.models.SemesterCourseModel;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.UOBSchedule;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class AddCoursesFragment extends BaseFragment {

    public final static String COURSE = "COURSE";
    private final static Pattern pDialogCourse =
            Pattern.compile("^([\\w]+[a-z])\\s*?([\\d]+)$",Pattern.CASE_INSENSITIVE);

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private List<MyCourseModel> coursesList;
    private FastItemAdapter<MyCourseModel> fastAdapter;
    private View mView;

    public AddCoursesFragment() {
        // Required empty public constructor
    }

    public static AddCoursesFragment newInstance(String title) {
        AddCoursesFragment fragment = new AddCoursesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_list, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_courses_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                alertDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // set fragment title
        toolbar.setTitle(title);
        // stop hiding toolbar
        params.setScrollFlags(0);
    }

    private void initiate() {
        // initialize variables
        fastAdapter = new FastItemAdapter<>();
        coursesList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation()));
        // disable refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(false);
        recyclerView.setAdapter(fastAdapter);

        fastAdapter.add(coursesList);
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<MyCourseModel>() {
            @Override
            public boolean onClick(View v, IAdapter<MyCourseModel> adapter,
                                   MyCourseModel item, int position) {
                return true;
            }
        });
    }

    public void alertDialog() {
        final Dialog dialog = new Dialog(getContext());

        // prepare dialog layout
        LayoutInflater inflater =
                (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_add_course, null);
        // set course autocomplete list
        final AutoCompleteTextView  textCourse =
                (AutoCompleteTextView)dialogView.findViewById((R.id.course));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, Constants.coursesList);
        textCourse.setAdapter(adapter);
        // force all caps
        textCourse.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        // init section EditText
        final EditText textSection = (EditText)dialogView.findViewById((R.id.section));
        // init cancel button
        Button btnCancel = (Button)dialogView.findViewById((R.id.cancel));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
        // init add button
        Button btnAdd = (Button)dialogView.findViewById((R.id.add));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String course = textCourse.getText().toString().trim();
                String section = textSection.getText().toString().trim();
                String courseName;
                String courseNumber;
                // empty text
                if (section.trim().isEmpty() || course.trim().isEmpty()){
                    dialog.dismiss();
                    Snackbar.make(mainContent,"Empty input",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                // check course input
                Matcher m = pDialogCourse.matcher(course);
                if(m.find()){
                    courseName = m.group(1);
                    courseNumber = m.group(2);
                } else {
                    dialog.dismiss();
                    Snackbar.make(mainContent,"Wrong course input",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                // course name not in the list
                if (!Arrays.asList(Constants.coursesList).contains(courseName)){
                    dialog.dismiss();
                    Snackbar.make(mainContent,"Wrong course input",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (section.length() == 1){
                    section = "0"+section;
                }
                fastAdapter.add(new MyCourseModel(courseName,courseNumber,section,"Ali Yusuf"));
                Snackbar.make(mainContent,"Course added",Snackbar.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        // show dialog
        dialog.setContentView(dialogView);
        dialog.show();
    }
}
