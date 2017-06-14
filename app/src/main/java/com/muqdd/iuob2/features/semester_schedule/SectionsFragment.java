package com.muqdd.iuob2.features.semester_schedule;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.models.CourseModel;
import com.muqdd.iuob2.models.SectionModel;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.UOBSchedule;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

public class SectionsFragment extends BaseFragment {

    public final static String COURSE = "COURSE";
    public final static Type TYPE = new TypeToken<CourseModel>() {}.getType();

    // static variables to enhance performance
    private final static String seatsPattern = "Sec\\.[\\s\\S]*?color=\".*\">(.*?)<[\\s\\S]*?=&gt;[\\s\\S]*?size=\"2\">(\\d*?)<";
    private final static Pattern pSeats =
            Pattern.compile(seatsPattern,Pattern.UNIX_LINES | Pattern.CASE_INSENSITIVE);

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private List<SectionModel> sectionsList;
    private CourseModel course;
    private FastItemAdapter<SectionModel> fastAdapter;
    private View finalView;

    public SectionsFragment() {
        // Required empty public constructor
    }

    public static SectionsFragment newInstance(String title, CourseModel course) {
        SectionsFragment fragment = new SectionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(COURSE, new Gson().toJson(course,TYPE));
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
        // Inflate the layout for this fragment
        course = new Gson().fromJson(getArguments().getString(COURSE), TYPE);
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        // Setup variables and layouts
        initiate();
        // get courses list
        getSectionsListFromNet();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // set fragment course
        toolbar.setTitle(title);
        // stop hiding toolbar
        params.setScrollFlags(0);
    }

    private void initiate() {
        // initialize variables
        fastAdapter = new FastItemAdapter<>();
        sectionsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // set refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(true);
        recyclerView.getSwipeToRefresh().setColorSchemeResources(
                R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryLight);

        // refresh request
        recyclerView.getSwipeToRefresh().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSectionsListFromNet();
            }
        });
        fastAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener<SectionModel>() {
            @Override
            public boolean onLongClick(View v, IAdapter<SectionModel> adapter, SectionModel item, int position) {
                Logger.d(item.toString());
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("iUOB2", item.toString());
                clipboard.setPrimaryClip(clip);
                Snackbar.make(mainContent,"Section details copied",Snackbar.LENGTH_LONG).show();
                return false;
            }
        });
    }

    public void getSectionsListFromNet() {
        ServiceGenerator.createService(UOBSchedule.class)
                .sectionsList(course.prog,course.abv,course.departmentCode,course.courseNumber,
                        course.credits,course.year,course.semester)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            // do parsing in background
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        sectionsList.clear();
                                        sectionsList.addAll(SectionModel.parseSectionsData(response.body().string()));
                                        if (sectionsList.size() > 0) {
                                            getAvailableSeats();
                                            // attach the adapter
                                            runOnUi(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addFinalExamView();
                                                    fastAdapter.set(sectionsList);
                                                    recyclerView.setAdapter(fastAdapter);
                                                }
                                            });
                                        }
                                    } catch (IOException e) {
                                        Logger.e(e.getMessage());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {}
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
        String color = Integer.toHexString(ContextCompat.getColor(getActivity(),R.color.colorAccent) & 0x00ffffff);
        String finalExam = sectionsList.get(0).getFinalExam();
        finalExam = finalExam.equals("") ? getString(R.string.row_finalexam_time_no_exam) : finalExam;
        String text = String.format(Locale.getDefault(), "<font color=\"#%s\">%s</font> %s",
                color,
                getString(R.string.row_finalexam_time_final),
                finalExam);
        ((TextView)finalView.findViewById(R.id.text)).setText(Html.fromHtml(text));
        mainContent.addView(finalView,0);
    }

    private void getAvailableSeats(){
        ServiceGenerator.createService(UOBSchedule.class)
                .availableSeats(course.courseNumber,course.departmentCode)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200){
                            try {
                                parseAvailableSeatsData(response.body().string());
                            } catch (IOException e) {
                                Logger.e(e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Logger.e(t.getMessage());
                    }
                });
    }

    public void parseAvailableSeatsData(String data){
        final Matcher m = pSeats.matcher(data);
        while (m.find()){
            for(SectionModel s : sectionsList){
                if(s.number != null && s.number.equals(m.group(1))) {
                    s.seats = m.group(2);
                    s.showSeats = true;
                    break;
                }
            }
        }
    }
}