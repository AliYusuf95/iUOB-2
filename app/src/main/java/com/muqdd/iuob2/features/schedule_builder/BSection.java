package com.muqdd.iuob2.features.schedule_builder;

import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;
import com.muqdd.iuob2.models.FinalExam;
import com.muqdd.iuob2.models.Timing;
import com.orhanobut.logger.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

@SuppressWarnings({"unused","WeakerAccess"})
public class BSection extends BaseModel<BSection, BSection.ViewHolder> {

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
    @SerializedName("exam")
    @Expose
    private FinalExam exam;

    public BSection() {
        this.mSelected = true;
        this.mSelectable = true;
    }

    public String getId() {
        return id;
    }

    public int getHeaderId() {
        return ((courseId).hashCode() & 0xfffffff);
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

    public FinalExam getExam() {
        return exam;
    }

    public void setExam(FinalExam exam) {
        this.exam = exam;
    }

    public static List<BSection> getSectionsFromCourseList(List<BCourse> courses) {
        List<BSection> sections = new ArrayList<>();
        for (BCourse course: courses) {
            sections.addAll(course.getSections());
        }
        return sections;
    }

    public static void putSectionsIntoCoursesList(List<BCourse> courses, List<BSection> sections) {
        for (BCourse course: courses) {
            course.getSections().clear();
            for (BSection section: sections) {
                if (section.getCourseId().equals(course.getId())){
                    course.getSections().add(section);
                }
            }
        }
    }

    public boolean hasClash(BSection section){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        for (Timing sectionATime : timingLegacy) {
            try {
                Date sectionAStartTime = dateFormat.parse(sectionATime.getTimeFrom());
                Date sectionAEndTime = dateFormat.parse(sectionATime.getTimeTo());
                for (Timing sectionBTime : section.timingLegacy) {
                    try {
                        Date sectionBStartTime  = dateFormat.parse(sectionBTime.getTimeFrom());
                        Date sectionBEndTime = dateFormat.parse(sectionBTime.getTimeTo());
                        boolean sameDays = false;
                        for (char c : sectionBTime.getDay().toCharArray()){
                            sameDays = sectionATime.getDay().indexOf(c) > -1 || sameDays;
                        }
                        if(sameDays &&
                                ((afterOrEqual(sectionAStartTime, sectionBStartTime) &&
                                    beforeOrEqual(sectionAStartTime, sectionBEndTime)) ||
                            (afterOrEqual(sectionAEndTime, sectionBStartTime) &&
                                    beforeOrEqual(sectionAEndTime,  sectionBEndTime)) ||
                            (afterOrEqual(sectionBStartTime, sectionAStartTime) &&
                                    beforeOrEqual(sectionBStartTime, sectionAEndTime)) ||
                            (afterOrEqual(sectionBEndTime, sectionAStartTime) &&
                                    beforeOrEqual(sectionBEndTime, sectionAEndTime))))
                        {
                            // if start or end time is between other lectures times -> CLASH
                            return true;
                        }
                    } catch (ParseException e) {
                        // wong time format don't pass this sections
                        return true;
                    }
                }
            } catch (ParseException e) {
                // wong time format don't pass this sections
                return true;
            }
        }
        return false;
    }

    private boolean afterOrEqual(Date a, Date b){
        return a.after(b) || a.equals(b);
    }

    private boolean beforeOrEqual(Date a, Date b){
        return a.before(b) || a.equals(b);
    }

    public String getFinalExam() {
        return exam == null ? "" : exam.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof BSection))
            return false;
        else if (o == this)
            return true;
        return courseId != null && ((BSection) o).courseId != null &&
                sectionNo != null && ((BSection) o).sectionNo != null &&
                courseId.equals(((BSection) o).courseId) &&
                sectionNo.equals(((BSection) o).sectionNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, sectionNo);
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
        viewHolder.title.setText(String.format(Locale.getDefault(), "[%s] %s", sectionNo, instructor));
        String subTitle = "";
        for (Timing time : timingLegacy) {
            subTitle += String.format(Locale.getDefault(), "[%s %s] ", time.getLocation(), time.getDuration());
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

    public static class ClickEvent extends ClickEventHook<BSection> {
        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof BSection.ViewHolder) {
                return ((ViewHolder) viewHolder).view;
            }
            return null;
        }

        @Override
        public void onClick(View v, int position, FastAdapter<BSection> fastAdapter, BSection item) {
            fastAdapter.toggleSelection(position);
        }
    }
}
