package com.muqdd.iuob2.features.schedule_builder;

import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;
import com.muqdd.iuob2.models.FinalExam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 6/7/2017.
 * iUOB-2
 */

@SuppressWarnings({"unused","WeakerAccess"})
public class BCourse extends BaseModel<BCourse, BCourse.ViewHolder> {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("exam")
    @Expose
    private FinalExam exam;
    @SerializedName("sections")
    @Expose
    private List<BSection> sections = null;

    public BCourse() {}

    public BCourse(BCourse course) {
        this(course.getId());
    }

    public BCourse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getHeaderId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BSection> getSections() {
        if (sections == null){
            sections = new ArrayList<>();
        }
        return sections;
    }

    public void setSections(List<BSection> sections) {
        this.sections = sections;
    }

    public FinalExam getExam() {
        return exam;
    }

    public void setExam(FinalExam exam) {
        this.exam = exam;
    }

    public static List<String> getCoursesIds(List<BCourse> list) {
        List<String> ids = new ArrayList<>();
        if (list == null) {
            return ids;
        }
        for (BCourse course : list) {
            ids.add(course.id);
        }
        return ids;
    }

    @Override
    public int getType() {
        return R.id.course_title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.row_my_course;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        viewHolder.courseTitle.setText(id);
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.courseTitle.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof BCourse))
            return false;
        else if (o == this)
            return true;
        return id != null && ((BCourse) o).id != null && id.equals(((BCourse) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_title) TextView courseTitle;
        @BindView(R.id.course_pre) TextView section;
        public @BindView(R.id.delete) LinearLayout delete;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            section.setText(null);
            section.setVisibility(View.GONE);
            courseTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }
}
