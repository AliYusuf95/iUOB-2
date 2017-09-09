package com.muqdd.iuob2.features.semester_schedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.models.CoursePrefix;
import com.muqdd.iuob2.models.RestResponse;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.IUOBApi;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class PrefixesFragment extends BaseFragment {

    public final static String PREFIX_LIST = "PREFIX_LIST";
    public final static String YEAR = "YEAR";
    public final static String SEMESTER = "SEMESTER";
    public final static Type PREFIX_LIST_TYPE = new TypeToken<List<CoursePrefix>>() {}.getType();

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private int year;
    private int semester;
    private List<CoursePrefix> mPrefixesList;
    private FastItemAdapter<CoursePrefix> fastAdapter;
    private View mView;

    public PrefixesFragment() {
        // Required empty public constructor
    }

    public static PrefixesFragment newInstance(int year, int semester, List<CoursePrefix> semesterCourses) {
        PrefixesFragment fragment = new PrefixesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, year+"/"+semester);
        bundle.putInt(YEAR, year);
        bundle.putInt(SEMESTER, semester);
        bundle.putString(PREFIX_LIST, new Gson().toJson(semesterCourses, PREFIX_LIST_TYPE));
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
            // Inflate the layout for this fragment
            year = getArguments().getInt(YEAR);
            semester = getArguments().getInt(SEMESTER);
            mPrefixesList = new Gson().fromJson(getArguments().getString(PREFIX_LIST), PREFIX_LIST_TYPE);
            tabLayout.setVisibility(View.VISIBLE);
            mView = inflater.inflate(R.layout.fragment_list, container, false);
            ButterKnife.bind(this, mView);
            // Setup variables and layouts
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initiate() {
        // initialize variables
        fastAdapter = new FastItemAdapter<>();
        fastAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<CoursePrefix>() {
            @Override
            public boolean filter(CoursePrefix item, CharSequence constraint) {
                return !item.getPrefix().toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        //semesterCoursesList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
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
                getPrefixes();
            }
        });

        // On item click
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<CoursePrefix>() {
            @Override
            public boolean onClick(View v, IAdapter<CoursePrefix> adapter,
                                   CoursePrefix item, int position) {
                CoursesFragment fragment = CoursesFragment.newInstance(item);
                displayFragment(fragment);
                return false;
            }
        });

        // Register to search view listener
        Fragment parent = getParentFragment();
        if (parent != null && parent instanceof SemestersHolderFragment){
            ((SemestersHolderFragment) parent)
                    .addSearchTextListeners(title, new SemestersHolderFragment.SearchTextListener() {
                        @Override
                        public void onTextChange(String s) {
                            fastAdapter.filter(s);
                        }
                    });
        }

        fastAdapter.set(mPrefixesList);
        recyclerView.setAdapter(fastAdapter);
    }

    public void getPrefixes() {
        ServiceGenerator.createService(IUOBApi.class)
                .coursesPrefix(year, semester).enqueue(new Callback<RestResponse<List<String>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<String>>> call, final Response<RestResponse<List<String>>> response) {
                if (response.body().getStatusCode() == 200) {
                    mPrefixesList = CoursePrefix.createList(response.body().getData(), year, semester);
                    if (mPrefixesList.size() > 0) {
                        fastAdapter.set(mPrefixesList);
                    }
                }
                // stop refreshing
                recyclerView.getSwipeToRefresh().setRefreshing(false);
            }

            @Override
            public void onFailure(Call<RestResponse<List<String>>> call, Throwable t) {
                // stop refreshing
                recyclerView.getSwipeToRefresh().setRefreshing(false);
            }
        });
    }

}
