package com.muqdd.iuob2.features.calendar;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.CalendarSemester;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.UOBSchedule;

import java.util.List;

import androidx.recyclerview.widget.DividerItemDecoration;
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
@SuppressWarnings("FieldCanBeLocal")
public class CalendarSemestersFragment extends BaseFragment {

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private View mView;
    private FastItemAdapter<CalendarSemester> fastAdapter;

    public CalendarSemestersFragment() {
        // Required empty public constructor
    }

    public static CalendarSemestersFragment newInstance() {
        CalendarSemestersFragment fragment = new CalendarSemestersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.CALENDAR.toString());
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
            getCalendarFromNet();
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
        fastAdapter = new FastItemAdapter<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation()));
        // disable refreshable list
        recyclerView.getSwipeToRefresh().setEnabled(false);

        // init list

        // open link on click
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<CalendarSemester>() {
            @Override
            public boolean onClick(View v, IAdapter<CalendarSemester> adapter, CalendarSemester item, int position) {
                CalendarTimeLineFragment fragment = CalendarTimeLineFragment.newInstance(item.getSemesterInfo());
                displayFragment(fragment);
                return false;
            }
        });
    }

    private void getCalendarFromNet(){
        ServiceGenerator.createService(UOBSchedule.class)
                .semesterCalendar("uobmo/uobmo.calendar").enqueue(new Callback<List<CalendarSemester>>() {
            @Override
            public void onResponse(Call<List<CalendarSemester>> call,
                                   Response<List<CalendarSemester>> response) {
                if (response.code() == 200 && response.body().size() > 0){
                    fastAdapter.set(response.body());
                    recyclerView.setAdapter(fastAdapter);
                }

            }

            @Override
            public void onFailure(Call<List<CalendarSemester>> call, Throwable t) {

            }
        });
    }

}
