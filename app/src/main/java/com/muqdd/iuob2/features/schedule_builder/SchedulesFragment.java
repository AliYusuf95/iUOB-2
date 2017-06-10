package com.muqdd.iuob2.features.schedule_builder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;

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
public class SchedulesFragment extends BaseFragment {

    private final static String SCHEDULES_LIST = "SCHEDULES_LIST";
    private final static Type SCHEDULES_LIST_TYPE = new TypeToken<List<List<BSectionModel>>>() {}.getType();

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private View mView;
    private List<BScheduleModel> mySchedulesList;
    private FastItemAdapter<BScheduleModel> fastAdapter;

    public SchedulesFragment() {
        // Required empty public constructor
    }

    public static SchedulesFragment newInstance(String title, List<List<BSectionModel>> list) {
        SchedulesFragment fragment = new SchedulesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(SCHEDULES_LIST, new Gson().toJson(list, SCHEDULES_LIST_TYPE));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        // stop hiding toolbar
        params.setScrollFlags(0);
    }

    private void initiate() {
        // initialize variables
        List<List<BSectionModel>> allSectionsList = new Gson().fromJson(getArguments().getString(SCHEDULES_LIST), SCHEDULES_LIST_TYPE);
        if (allSectionsList == null){
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

        mySchedulesList = new ArrayList<>();

        for (List<BSectionModel> list : allSectionsList){
            mySchedulesList.add(new BScheduleModel(list));
        }

        fastAdapter = new FastItemAdapter<>();
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
