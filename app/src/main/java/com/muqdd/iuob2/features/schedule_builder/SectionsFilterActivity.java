package com.muqdd.iuob2.features.schedule_builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseActivity;
import com.muqdd.iuob2.app.Constants;
import com.muqdd.iuob2.app.adapters.StickyHeaderAdapter;
import com.muqdd.iuob2.utils.SPHelper;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SectionsFilterActivity extends BaseActivity {

    private final static String SECTIONS_LIST = "SECTIONS_LIST";
    private final static Type COURSES_LIST_TYPE = new TypeToken<List<BCourse>>() {}.getType();
    private final static Type SECTIONS_LIST_TYPE = new TypeToken<List<BSection>>() {}.getType();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private List<BCourse> mCourseList; // filtered courses data
    private FastItemAdapter<BSection> mItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_filter);
        ButterKnife.bind(this);

        init(savedInstanceState);
        sendAnalyticTracker(R.string.activity_sections_filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.activity_sections_filter);
        }

        String json = SPHelper.getFromPrefs(this, Constants.SB_SECTIONS_LIST);
        if (json != null && !json.equals("")){
            mCourseList = new Gson().fromJson(json, COURSES_LIST_TYPE);
        }
        if (mCourseList == null || mCourseList.size() == 0) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

        // init adapter
        mItemAdapter = new FastItemAdapter<>();
        List<BSection> sectionList = BSection.getSectionsFromCourseList(mCourseList);
        mItemAdapter.withSelectable(true);
        mItemAdapter.withMultiSelect(true);
        mItemAdapter.withPositionBasedStateManagement(false);
        mItemAdapter.withItemEvent(new BSection.ClickEvent());

        // init list
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        recyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(stickyHeaderAdapter.wrap(mItemAdapter));

        if (savedInstanceState != null && savedInstanceState.containsKey(SECTIONS_LIST)) {
            List<BSection> savedSectionList =
                    new Gson().fromJson(savedInstanceState.getString(SECTIONS_LIST), SECTIONS_LIST_TYPE);
            if (savedSectionList != null && savedSectionList.size() > 0){
                sectionList = savedSectionList;
            }
        }
        mItemAdapter.add(sectionList);
        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.section_filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done:
                // save sections data in shared preference
                BSection.putSectionsIntoCoursesList(mCourseList, mItemAdapter.getAdapterItems());
                SPHelper.saveToPrefs(this , Constants.SB_SECTIONS_LIST, new Gson().toJson(mCourseList, COURSES_LIST_TYPE));
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case R.id.select_all:
                mItemAdapter.select();
                break;
            case R.id.deselect_all:
                mItemAdapter.deselect();
                break;
            case R.id.deselect_to_be:
                for (int i=0; i<mItemAdapter.getAdapterItems().size(); i++) {
                    String doc = mItemAdapter.getAdapterItems().get(i).getInstructor().trim().toLowerCase();
                    if (doc.equals("To be announced".trim().toLowerCase())) {
                        mItemAdapter.deselect(i);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Stop saving data, try to avoid android.os.TransactionTooLargeException error
        /*if (mItemAdapter != null) {
            outState.putString(SECTIONS_LIST, new Gson().toJson(mItemAdapter.getAdapterItems(), SECTIONS_LIST_TYPE));
        }*/
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
