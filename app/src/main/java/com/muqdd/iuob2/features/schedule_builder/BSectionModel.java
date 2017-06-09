package com.muqdd.iuob2.features.schedule_builder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;
import com.muqdd.iuob2.models.SectionModel;
import com.muqdd.iuob2.models.SectionTimeModel;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

@SuppressWarnings({"unused","WeakerAccess"})
public class BSectionModel extends BaseModel<BSectionModel, BSectionModel.ViewHolder> {

    public BCourseModel parentCourse;
    public String headerTitle;
    public String sectionNumber;
    public String doctor;
    public List<SectionTimeModel> times;
    public int courseId;

    public BSectionModel(BCourseModel parentCourse, SectionModel section) {
        this.parentCourse = new BCourseModel(parentCourse);
        this.headerTitle = parentCourse.courseName+parentCourse.courseNumber+ " - ";
        this.sectionNumber = section.number;
        this.doctor = section.doctor;
        this.times = section.times;
        this.courseId = parentCourse.courseId;
        this.mSelected = true;
        this.mSelectable = true;
    }

    public static List<BSectionModel> getSectionsFromCourseList(List<BCourseModel> courses) {
        List<BSectionModel> sections = new ArrayList<>();
        for (BCourseModel course: courses) {
            sections.addAll(course.sections);
        }
        return sections;
    }

    public static List<BCourseModel> getCoursesFromSectionsList(List<BSectionModel> sections) {
        List<BCourseModel> courses = new ArrayList<>();
        for (BSectionModel section: sections) {
            if (!courses.contains(section.parentCourse)){
                BCourseModel courseModel = new BCourseModel(section.parentCourse);
                for (BSectionModel addSection: sections) {
                    if (addSection.parentCourse.equals(section.parentCourse)) {
                        courseModel.sections.add(addSection);
                    }
                }
                courses.add(courseModel);
            }
        }
        return courses;
    }

    @Override
    public int getType() {
        return R.id.checkbox;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.row_filter_section;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        viewHolder.title.setText(String.format(Locale.getDefault(), "[%s] %s", sectionNumber, doctor));
        String subTitle = "";
        for (SectionTimeModel time : times) {
            subTitle += String.format(Locale.getDefault(), "[%s %s] ", time.room, time.getDuration());
        }
        viewHolder.subTitle.setText(subTitle);
        viewHolder.selection.setVisibility(isSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void unbindView(ViewHolder viewHolder) {
        super.unbindView(viewHolder);
        viewHolder.title.setText(null);
        viewHolder.subTitle.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.sub_title) TextView subTitle;
        @BindView(R.id.selection) ImageView selection;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

    public static class ClickEvent extends ClickEventHook<BSectionModel> {
        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof BSectionModel.ViewHolder) {
                return ((ViewHolder) viewHolder).view;
            }
            return null;
        }

        @Override
        public void onClick(View v, int position, FastAdapter<BSectionModel> fastAdapter, BSectionModel item) {
            fastAdapter.toggleSelection(position);
        }
    }
}
