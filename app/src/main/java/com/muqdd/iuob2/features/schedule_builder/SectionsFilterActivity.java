package com.muqdd.iuob2.features.schedule_builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SectionsFilterActivity extends BaseActivity {

    private final static Type COURSES_LIST_TYPE = new TypeToken<List<BCourseModel>>() {}.getType();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.main_content) CoordinatorLayout mainContent;
    @BindView(R.id.recycler_view) SuperRecyclerView recyclerView;

    private List<BCourseModel> myCourseList; // filtered courses data
    private FastItemAdapter<BSectionModel> mItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_filter);
        ButterKnife.bind(this);

        init(savedInstanceState);
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

        if (getIntent().hasExtra(Constants.INTENT_SECTIONS_LIST)){
            String json = getIntent().getStringExtra(Constants.INTENT_SECTIONS_LIST);
            myCourseList = new Gson().fromJson(json, COURSES_LIST_TYPE);
        }
        if (myCourseList == null || myCourseList.size() == 0) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

        // init adapter
        mItemAdapter = new FastItemAdapter<>();
        List<BSectionModel> sectionList = BSectionModel.getSectionsFromCourseList(myCourseList);
        mItemAdapter.withSelectable(true);
        mItemAdapter.withMultiSelect(true);
        mItemAdapter.withPositionBasedStateManagement(false);
        mItemAdapter.withItemEvent(new BSectionModel.ClickEvent());

        // init list
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        recyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(stickyHeaderAdapter.wrap(mItemAdapter));

        mItemAdapter.add(sectionList);
        //restore selections (this has to be done after the items were added
        mItemAdapter.withSavedInstanceState(savedInstanceState);

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
                Intent intent = new Intent();
                myCourseList = BSectionModel.getCoursesFromSectionsList(mItemAdapter.getAdapterItems());
                intent.putExtra(Constants.INTENT_SECTIONS_LIST, new Gson().toJson(myCourseList, COURSES_LIST_TYPE));
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.select_all:
                break;
            case R.id.deselect_all:
                break;
            case R.id.deselect_to_be:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = mItemAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
