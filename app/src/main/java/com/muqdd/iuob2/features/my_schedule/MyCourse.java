package com.muqdd.iuob2.features.my_schedule;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;
import com.muqdd.iuob2.features.schedule_builder.BSection;
import com.muqdd.iuob2.models.FinalExam;
import com.muqdd.iuob2.models.Timing;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 9/8/2017.
 * iUOB-2
 *
 * This class is behave like section {@link com.muqdd.iuob2.models.Section},
 * but with course details as well.
 *
 */

public class MyCourse extends BaseModel<MyCourse, MyCourse.ViewHolder> {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("courseId")
    @Expose
    private String courseId;
    @SerializedName("sectionNo")
    @Expose
    private String sectionNo;
    @SerializedName("instructor")
    @Expose
    private String instructor;
    @SerializedName("examLocation")
    @Expose
    private String examLocation;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("sem")
    @Expose
    private String sem;
    @SerializedName("timingLegacy")
    @Expose
    private List<Timing> timingLegacy = null;
    @SerializedName("timing")
    @Expose
    private List<Timing> timing = null;
    @SerializedName("exam")
    @Expose
    private FinalExam exam;

    public MyCourse(String courseId, String sectionNo) {
        this.courseId = courseId;
        this.sectionNo = sectionNo;
    }

    public MyCourse(BSection section) {
        this.id = section.getId();
        this.courseId = section.getCourseId();
        this.sectionNo = section.getSectionNo();
        this.instructor = section.getInstructor();
        this.examLocation = section.getExamLocation();
        this.status = section.getStatus();
        this.remarks = section.getRemarks();
        this.year = section.getYear();
        this.sem = section.getSemester();
        this.timingLegacy = section.getTimingLegacy();
        this.exam = section.getExam();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSectionNo() {
        return sectionNo;
    }

    public void setSectionNo(String sectionNo) {
        this.sectionNo = sectionNo;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getExamLocation() {
        return examLocation;
    }

    public void setExamLocation(String examLocation) {
        this.examLocation = examLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<Timing> getTimingLegacy() {
        return timingLegacy;
    }

    public void setTimingLegacy(List<Timing> timingLegacy) {
        this.timingLegacy = timingLegacy;
    }

    public FinalExam getExam() {
        return exam;
    }

    public void setExam(FinalExam exam) {
        this.exam = exam;
    }

    public int getBgColor() {
        Logger.d("%s ,%s", String.format("#50%06X", (0xFFFFFF & (courseId+sectionNo).hashCode())),
                Color.parseColor(String.format("#50%06X", (0xFFFFFF & (courseId+sectionNo).hashCode()))));
        return Color.parseColor(String.format("#50%06X", (0xFFFFFF & (courseId+sectionNo).hashCode())));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_title) TextView courseTitle;
        @BindView(R.id.course_pre) TextView section;
        public @BindView(R.id.delete)
        LinearLayout delete;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public String toString() {
        String str = "Sec. [" + sectionNo + "] => "+ instructor + "\n";
        if (timingLegacy.size() > 0) {
            for (Timing time : timingLegacy) {
                str += "\tDays: "+time.getDay()+", Time: "+time.getDuration()+", Room: "+time.getLocation()+"\n";
            }
        }
        return str;
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
        viewHolder.courseTitle.setText(courseId);
        if (sectionNo != null && !"".equals(sectionNo)) {
            viewHolder.section.setText(String.format("Section: %s", sectionNo));
        }
        else {
            viewHolder.section.setVisibility(View.GONE);
            viewHolder.courseTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.courseTitle.setText(null);
        holder.section.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof MyCourse))
            return false;
        else if (o == this)
            return true;
        MyCourse object = (MyCourse) o;
        return courseId.equals(object.courseId) &&
                        sectionNo.equals(object.sectionNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, sectionNo);
    }
}
