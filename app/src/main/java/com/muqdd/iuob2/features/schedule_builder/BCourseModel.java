package com.muqdd.iuob2.features.schedule_builder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;
import com.muqdd.iuob2.app.Constants;
import com.muqdd.iuob2.models.SectionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 6/7/2017.
 * iUOB-2
 */

@SuppressWarnings({"unused","WeakerAccess"})
public class BCourseModel extends BaseModel<BCourseModel, BCourseModel.ViewHolder> {

    public String courseName;
    public String courseNumber;
    public String departmentCode;
    public int courseId;
    public List<BSectionModel> sections;

    public BCourseModel(String courseName, String courseNumber) {
        this.courseName = courseName;
        this.courseNumber = courseNumber;
        this.departmentCode = Constants.getDebCode(courseName);
        int rand = 0;
        for (char b : courseName.toCharArray()){
            rand += b%2 == 0 ? b*b : b*10;
        }
        float caj = (float) (((Integer.parseInt(courseNumber) * Integer.parseInt(courseNumber) * 13) % 15) / 100.0);
        float hue = (float) (rand % 255) + caj;
        this.courseId = Color.HSVToColor(30, new float[]{hue, 0.7f,0.8f});
        this.sections = new ArrayList<>();
    }

    public BCourseModel(BCourseModel course) {
        this(course.courseName, course.courseNumber);
    }

    public static void parseSectionsData(BCourseModel course, String data) {
        course.sections = new ArrayList<>();
        for (SectionModel section : SectionModel.parseSectionsData(data)){
            course.sections.add(new BSectionModel(course, section));
        }
        // remove empty sections
        for (int i=course.sections.size()-1; i>=0; i--){
            if (course.sections.get(i).times.size() == 0) {
                course.sections.remove(i);
            }
        }
        // update section header
        for (BSectionModel section : course.sections){
            section.headerTitle += course.sections.size() + " Sections";
        }
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
        String courseTitle = courseName+courseNumber;
        viewHolder.courseTitle.setText(courseTitle);
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.courseTitle.setText("");
        holder.section.setText("");
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof BCourseModel))
            return false;
        else if (o == this)
            return true;
        return courseId == ((BCourseModel) o).courseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_title) TextView courseTitle;
        @BindView(R.id.course_pre) TextView section;
        public @BindView(R.id.delete) LinearLayout delete;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            section.setVisibility(View.GONE);
            courseTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }
}
