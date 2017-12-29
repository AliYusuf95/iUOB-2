package com.muqdd.iuob2.features.calendar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.CalendarSemesterInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */
@SuppressWarnings("FieldCanBeLocal")
public class CalendarTimeLineFragment extends BaseFragment {

    public static final Type TYPE = new TypeToken<List<CalendarSemesterInfo>>() {}.getType();
    public static final String LIST = "LIST";

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private View mView;
    private List<CalendarSemesterInfo> timeLineList;
    private TimeLineAdapter fastAdapter;

    public CalendarTimeLineFragment() {
        // Required empty public constructor
    }

    public static CalendarTimeLineFragment newInstance(List<CalendarSemesterInfo> list) {
        CalendarTimeLineFragment fragment = new CalendarTimeLineFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.CALENDAR.toString());
        bundle.putString(LIST, new Gson().toJson(list,TYPE));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_list, container, false);
            timeLineList = new Gson().fromJson(getArguments().getString(LIST), TYPE);
            ButterKnife.bind(this, mView);
            initiate();
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
    }

    private void initiate() {
        // initialize variables
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // disable refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(false);

        // add data in list
        if (timeLineList == null){
            timeLineList = new ArrayList<>();
        }
        fastAdapter = new TimeLineAdapter(timeLineList);
        recyclerView.setAdapter(fastAdapter);

        // fixed size
        recyclerView.getRecyclerView().setHasFixedSize(true);
    }
}
