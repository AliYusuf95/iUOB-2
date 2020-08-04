package com.muqdd.iuob2.models;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
public class Section extends BaseModel<Section, Section.ViewHolder> {

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
    @SerializedName("semester")
    @Expose
    private String semester;
    @SerializedName("timing")
    @Expose
    private List<Timing> timing = null;
    @SerializedName("timingLegacy")
    @Expose
    private List<Timing> timingLegacy = null;

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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<Timing> getTimingLegacy() {
        return timingLegacy;
    }

    public void setTimingLegacy(List<Timing> timingLegacy) {
        this.timingLegacy = timingLegacy;
    }

    @Override
    public String toString() {
        String str = courseId + "\n" +
                "Sec. [" + sectionNo + "] => " + instructor + "\n";
        if (timing.size() > 0) {
            for (Timing time : timingLegacy) {
                str += "\tDays: " + time.getDay() +
                        ", Time: " + time.getDuration() +
                        ", Room: " + time.getLocation() + "\n";
            }
        }
        return str;
    }

    @Override
    public int getType() {
        return R.id.section_card;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.row_section;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.sectionNumber.setText(sectionNo);
        viewHolder.doctorName.setText(instructor);
        viewHolder.status.setText(status);
        viewHolder.status.setTextColor(ContextCompat.getColor(viewHolder.context,
                status.trim().equals("OPEN") ? R.color.green_color_picker : R.color.red_color_picker
        ));
        for (Timing time : timingLegacy) {
            viewHolder.addRow(time);
        }
    }

    @Override
    public void unbindView(ViewHolder viewHolder) {
        super.unbindView(viewHolder);
        viewHolder.sectionNumber.setText(null);
        viewHolder.doctorName.setText(null);
        viewHolder.status.setText(null);
        viewHolder.timeRows.removeAllViews();
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.section_number)
        protected TextView sectionNumber;
        @BindView(R.id.doctor_name)
        protected TextView doctorName;
        @BindView(R.id.section_time_rows)
        protected LinearLayout timeRows;
        @BindView(R.id.status)
        protected TextView status;
        private Context context;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = view.getContext();
        }

        @SuppressLint("InflateParams")
        public void addRow(Timing sectionTime) {
            // separator line
            View line = new View(context);
            int hLine = (int) context.getResources().getDimension(R.dimen.row_section_time_divider);
            line.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hLine));
            line.setBackgroundColor(ContextCompat.getColor(context, R.color.divider));
            // row layout
            View row = LayoutInflater.from(context).inflate(R.layout.row_section_time, null, false);
            TextView dateView = ((TextView) row.findViewById(R.id.days));
            dateView.setText(sectionTime.getDay());
            if (sectionTime.getType() != null &&  sectionTime.getType().toUpperCase().equals("LAB")) {
                dateView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_science_20db, 0, 0, 0);
            }
            ((TextView) row.findViewById(R.id.time)).setText(sectionTime.getDuration());
            ((TextView) row.findViewById(R.id.room)).setText(sectionTime.getLocation());
            // add to the card
            timeRows.addView(line);
            timeRows.addView(row);
        }
    }
}
