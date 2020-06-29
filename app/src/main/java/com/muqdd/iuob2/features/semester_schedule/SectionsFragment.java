package com.muqdd.iuob2.features.semester_schedule;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.models.Course;
import com.muqdd.iuob2.models.RestResponse;
import com.muqdd.iuob2.models.Section;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.IUOBApi;
import com.muqdd.iuob2.network.UOBSchedule;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class SectionsFragment extends BaseFragment {

    public final static String COURSE = "COURSE";

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private Course course;
    private FastItemAdapter<Section> fastAdapter;
    private View finalView;

    public SectionsFragment() {
        // Required empty public constructor
    }

    public static SectionsFragment newInstance(Course course) {
        SectionsFragment fragment = new SectionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, course.getCode());
        bundle.putString(COURSE, new Gson().toJson(course, Course.class));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        // Inflate the layout for this fragment
        if (getArguments() != null) {
            course = new Gson().fromJson(getArguments().getString(COURSE), Course.class);
        }
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        // Setup variables and layouts
        initiate();
        // get coursesList list
        getSectionsList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // set fragment title
        toolbar.setTitle(title);
    }

    private void initiate() {
        // initialize variables
        fastAdapter = new FastItemAdapter<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // set refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(true);
        recyclerView.getSwipeToRefresh().setColorSchemeResources(
                R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryLight);

        // refresh request
        recyclerView.getSwipeToRefresh().setOnRefreshListener(this::getSectionsList);
        fastAdapter.withOnLongClickListener((v, adapter, item, position) -> {
            Logger.d(item.toString());
            if (getActivity() == null) return false;
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("iUOB2", item.toString());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
            }
            Snackbar.make(mainContent,"Section details copied", Snackbar.LENGTH_LONG).show();
            return false;
        });
    }

    public void getSectionsList() {
        ServiceGenerator.createService(UOBSchedule.class)
                .sections(course.getYear(), course.getSemester(), course.getCode())
                .enqueue(new Callback<RestResponse<List<Section>>>() {
                    @Override
                    public void onResponse(@NonNull Call<RestResponse<List<Section>>> call, @NonNull final Response<RestResponse<List<Section>>> response) {
                        RestResponse<List<Section>> restResponse = response.body();
                        if (restResponse != null && restResponse.getStatusCode() == 200) {
                            fastAdapter.set(restResponse.getData());
                            if (recyclerView.getAdapter() == null) {
                                recyclerView.setAdapter(fastAdapter);
                            }
                            addFinalExamView();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RestResponse<List<Section>>> call, @NonNull Throwable t) {}
                });
    }

    private void addFinalExamView() {
        // avoid some crashes
        if (getContext() == null)
            return;
        // remove final view if exist
        mainContent.removeView(finalView);
        // create new view
        finalView =
                LayoutInflater.from(getContext()).inflate(R.layout.row_finalexam_time,null,false);
        String color = Integer.toHexString(ContextCompat.getColor(getContext(), R.color.colorAccent) & 0x00ffffff);
        String finalExam = course.getExam().toString();
        finalExam = finalExam.equals("") ? getString(R.string.row_finalexam_time_no_exam) : finalExam;
        String text = String.format(Locale.getDefault(), "<font color=\"#%s\">%s</font> %s",
                color,
                getString(R.string.row_finalexam_time_final),
                finalExam);
        ((TextView)finalView.findViewById(R.id.text)).setText(Html.fromHtml(text));
        mainContent.addView(finalView,0);
    }

}