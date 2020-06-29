package com.muqdd.iuob2.models;

import androidx.recyclerview.widget.RecyclerView;
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
 * Created by Ali Yusuf on 9/8/2017.
 * iUOB-2
 */

@SuppressWarnings("WeakerAccess")
public class Course extends BaseModel<Course, Course.ViewHolder> {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("codePrefix")
    @Expose
    private String codePrefix;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("prereq")
    @Expose
    private String prereq;
    @SerializedName("exam")
    @Expose
    private FinalExam exam;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("sem")
    @Expose
    private String semester;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrereq() {
        return prereq;
    }

    public void setPrereq(String prereq) {
        this.prereq = prereq;
    }

    public FinalExam getExam() {
        if (exam == null){
            exam = new FinalExam();
        }
        return exam;
    }

    public void setExam(FinalExam exam) {
        this.exam = exam;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String sem) {
        this.semester = sem;
    }

    @Override
    public int getType() {
        return R.id.course_title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.row_course;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.title.setText(code);
        if (prereq == null || "".equals(prereq)) {
            viewHolder.pre.setVisibility(View.GONE);
        } else {
            viewHolder.pre.setText(prereq);
            viewHolder.pre.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.title.setText(null);
        holder.pre.setText(null);
        holder.pre.setVisibility(View.GONE);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_title)
        TextView title;
        @BindView(R.id.course_pre) TextView pre;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
