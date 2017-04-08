package com.muqdd.iuob2.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

@SuppressWarnings({"unused","WeakerAccess"})
public class MyCourseModel extends BaseModel<MyCourseModel, MyCourseModel.ViewHolder> {

    public String courseName;
    public String courseNumber;
    public String sectionNumber;
    public String doctor;
    public List<SectionTime> times;

    public MyCourseModel(String courseName, String courseNumber, String sectionNumber, String doctor) {
        this.courseName = courseName;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        String str = "Sec. [" + sectionNumber + "] => "+ doctor + "\n";
        if (times.size() > 0) {
            for (SectionTime time : times) {
                str += "\tDays: "+time.days+", Time: "+time.getDuration()+", Room: "+time.room+"\n";
            }
        }
        return str;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.course_title;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.row_my_course;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(MyCourseModel.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        String courseTitle = courseName+courseNumber;
        viewHolder.courseTitle.setText(courseTitle);
        viewHolder.section.setText(String.format("Section: %s",sectionNumber));
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(MyCourseModel.ViewHolder holder) {
        super.unbindView(holder);
        holder.courseTitle.setText("");
        holder.section.setText("");
    }

    private class SectionTime {
        String days;
        String from;
        String to;
        String room;

        SectionTime(String days, String from, String to, String room) {
            this.days = days;
            this.from = from;
            this.to = to;
            this.room = room;
        }

        public String getDuration() {
            return from+" - "+to;
        }
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_title) TextView courseTitle;
        @BindView(R.id.course_pre) TextView section;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
