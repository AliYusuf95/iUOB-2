package com.muqdd.iuob2.features.my_schedule;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
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

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.app.Constants;
import com.muqdd.iuob2.app.User;
import com.muqdd.iuob2.models.MyCourseModel;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class AddCoursesFragment extends BaseFragment {

    public final static String COURSE = "COURSE";
    private final static Pattern pDialogCourse =
            Pattern.compile("^([\\w]+[a-z])\\s*?([\\d]{3})$",Pattern.CASE_INSENSITIVE);

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
                showAddDialog();
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
        coursesList = User.getCourses(getContext());
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

        fastAdapter.withItemEvent(new ClickEventHook<MyCourseModel>(){
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
                showDeleteDialog(item,position);
            }
        });
    }

    private void showDeleteDialog(final MyCourseModel item, final int position) {
        final Dialog dialog = new Dialog(getContext());
        // prepare dialog layout
        LayoutInflater inflater =
                (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_delete_course, null);
        // init cancel button
        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
        // init delete button
        dialogView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.deleteCourse(getContext(),item);
                ((FastItemAdapter)fastAdapter).remove(position);
                Snackbar.make(mainContent,"Course deleted",Snackbar.LENGTH_SHORT).show();
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
        // show dialog
        dialog.setContentView(dialogView);
        dialog.show();
    }

    public void showAddDialog() {
        final Dialog dialog = new Dialog(getContext());
        // prepare dialog layout
        LayoutInflater inflater =
                (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_add_course, null);
        // set course autocomplete list
        final AutoCompleteTextView  textCourse =
                (AutoCompleteTextView)dialogView.findViewById((R.id.course));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, Constants.coursesNameList);
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
                if (!Arrays.asList(Constants.coursesNameList).contains(courseName)){
                    dialog.dismiss();
                    Snackbar.make(mainContent,"Wrong course input",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                // section number up to 99
                if (section.length() > 2){
                    dialog.dismiss();
                    Snackbar.make(mainContent,"Wrong section input",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                // add section leading 0
                if (section.length() == 1){
                    section = "0"+section;
                }
                MyCourseModel mCourseModel = new MyCourseModel(courseName,courseNumber,section);
                // check if section already added
                if (fastAdapter.getAdapterItems().contains(mCourseModel)){
                    dialog.dismiss();
                    Snackbar.make(mainContent,"Section already exist",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                // add section and store it
                fastAdapter.add(mCourseModel);
                User.addCourse(getContext(),mCourseModel);
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
