package com.muqdd.iuob2.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 4/11/2017.
 * iUOB-2
 */

public class CalendarSemesterModel extends BaseModel<SemesterCourseModel, SemesterCourseModel.ViewHolder> {

    @SerializedName("semester")
    @Expose
    private String semester;
    @SerializedName("semester_info")
    @Expose
    private List<CalendarSemesterInfo> semesterInfo = null;

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<CalendarSemesterInfo> getSemesterInfo() {
        if (semesterInfo != null && semesterInfo.size()>0) {
            CalendarSemesterInfo.setItemCount(semesterInfo.size());
        } else {
            CalendarSemesterInfo.setItemCount(0);
        }
        return semesterInfo;
    }

    public void setSemesterInfo(List<CalendarSemesterInfo> semesterInfo) {
        this.semesterInfo = semesterInfo;
    }

    @Override
    public int getType() {
        return R.id.course_title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.row_semester;
    }

    @Override
    public void bindView(SemesterCourseModel.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        viewHolder.title.setText(semester);
    }

    @Override
    public void unbindView(SemesterCourseModel.ViewHolder holder) {
        super.unbindView(holder);
        holder.title.setText(null);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        protected @BindView(R.id.course_title) TextView title;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}