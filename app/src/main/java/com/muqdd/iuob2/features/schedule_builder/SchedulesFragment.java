package com.muqdd.iuob2.features.schedule_builder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */
@SuppressWarnings("FieldCanBeLocal")
public class SchedulesFragment extends BaseFragment {

    private final static String SCHEDULES_LIST = "SCHEDULES_LIST";
    private final static Type SCHEDULES_LIST_TYPE = new TypeToken<List<List<BSection>>>() {}.getType();

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private View mView;
    private List<BSchedule> mySchedulesList;
    private FastItemAdapter<BSchedule> fastAdapter;
    private List<List<BSection>> allSectionsList;

    public SchedulesFragment() {
        // Required empty public constructor
    }

    public static SchedulesFragment newInstance(String title, List<List<BSection>> list) {
        SchedulesFragment fragment = new SchedulesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(SCHEDULES_LIST, new Gson().toJson(list, SCHEDULES_LIST_TYPE));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_list, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        // check primary data
        checkPrimaryData();
    }

    private void checkPrimaryData() {
        if (allSectionsList == null && getContext() != null){
            allSectionsList = new ArrayList<>();
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
        // initialize variables
        allSectionsList = new Gson().fromJson(getArguments().getString(SCHEDULES_LIST), SCHEDULES_LIST_TYPE);
        checkPrimaryData();
        mySchedulesList = new ArrayList<>();

        for (List<BSection> list : allSectionsList){
            mySchedulesList.add(new BSchedule(list));
        }

        fastAdapter = new FastItemAdapter<>();
        fastAdapter.withItemEvent(new ClickEventHook<BSchedule>() {
            @Nullable
            @Override
            public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof BSchedule.ViewHolder){
                    return Arrays.asList(viewHolder.itemView, ((BSchedule.ViewHolder) viewHolder).imgInfo);
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<BSchedule> fastAdapter, BSchedule item) {
                if (v.getId() == R.id.img_info){
                    // start schedule details fragment
                    ScheduleDetailsFragment fragment =
                            ScheduleDetailsFragment.newInstance(getString(R.string.fragment_schedule_details), item);
                    displayFragment(fragment);
                } else {
                    // start time table fragment
                    TimeTableFragment fragment =
                            TimeTableFragment.newInstance(getString(R.string.fragment_time_table), item);
                    displayFragment(fragment);
                }
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        // disable refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(false);

        // setup adapter
        fastAdapter.set(mySchedulesList);
        recyclerView.setAdapter(fastAdapter);

    }

}
