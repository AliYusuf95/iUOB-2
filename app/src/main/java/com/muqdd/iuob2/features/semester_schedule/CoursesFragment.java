package com.muqdd.iuob2.features.semester_schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.models.CoursesModel;
import com.muqdd.iuob2.models.SemesterCoursesModel;
import com.muqdd.iuob2.rest.ServiceGenerator;
import com.muqdd.iuob2.rest.UOBSchedule;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

public class CoursesFragment extends BaseFragment {

    public final static String COURSE = "COURSE";
    public final static Type TYPE = new TypeToken<SemesterCoursesModel>() {}.getType();

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.semester_rv) SuperRecyclerView recyclerView;

    private List<CoursesModel> coursesList;
    private SemesterCoursesModel semesterCourse;
    private FastItemAdapter<CoursesModel> fastAdapter;

    public CoursesFragment() {
        // Required empty public constructor
    }

    public static CoursesFragment newInstance(String title, SemesterCoursesModel course) {
        CoursesFragment fragment = new CoursesFragment();
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
        semesterCourse = new Gson().fromJson(getArguments().getString(COURSE), TYPE);
        View view = inflater.inflate(R.layout.fragment_semester, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup variables and layouts
        initiate();
        // get courses list
        getCoursesListFromNet(semesterCourse);
    }

    private void initiate() {
        // set fragment title
        toolbar.setTitle(title);

        // stop hiding toolbar
        params.setScrollFlags(0);

        // initialize variables
        fastAdapter = new FastItemAdapter<>();
        coursesList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fastAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation()));

        // set refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(true);
        recyclerView.getSwipeToRefresh().setColorSchemeResources(
                R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryLight);

        // refresh request
        recyclerView.getSwipeToRefresh().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCoursesListFromNet(semesterCourse);
            }
        });
        fastAdapter.add(coursesList);
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<CoursesModel>() {
            @Override
            public boolean onClick(View v, IAdapter<CoursesModel> adapter,
                                   CoursesModel item, int position) {
                return false;
            }
        });
    }

    public void getCoursesListFromNet(final SemesterCoursesModel request) {
        ServiceGenerator.createService(UOBSchedule.class)
                .coursesList(request.inll,request.theabv,request.prog,request.year,request.semester)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                coursesList.clear();
                                coursesList.addAll(parseCoursesListData(response.body().string()));

                                if (coursesList.size() > 0) {
                                    fastAdapter.clear().add(coursesList);
                                    writeHTMLDataToCache(request.fileName(), response.body().string().getBytes());
                                }
                            } catch (IOException e) {
                                Logger.e(e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    public static ArrayList<CoursesModel> parseCoursesListData (String data) {
        String pattern = "<font size=\"2\">\\n?\\r?\\s?<B>\\n?\\r?\\s?<A HREF=\"(.*)?\" "+
                "TARGET=\"main\">\\n?\\r?\\s?<FONT color=\"#000000\">(.*)?</font>(.*)?</A>"+
                "\\n?\\r?\\s?</B>\\n?\\r?\\s?</font>\\n?\\r?\\s?(<br>\\n?\\r?\\s?<a href=\""+
                "javascript:onclick=viewpre\\('(.*)?'\\)\">\\n?\\r?\\s?<font color=\"#FF0000\" "+
                "size=\"?2\"?>\\n?\\r?\\s?Pre-Requisite for this course\\n?\\r?\\s?</font>"+
                "\\n?\\r?\\s?</a>\\n?\\r?\\s?)?<br>";
        Matcher m = Pattern.compile(pattern,Pattern.UNIX_LINES | Pattern.CASE_INSENSITIVE).matcher(data);
        ArrayList<CoursesModel> list = new ArrayList<>();
        while (m.find()){
            list.add(new CoursesModel(m.group(1), m.group(2) +" - "+ m.group(3), m.group(5)));
        }
        return list;
    }
}
