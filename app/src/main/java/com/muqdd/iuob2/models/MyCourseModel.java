package com.muqdd.iuob2.models;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;
import com.muqdd.iuob2.app.Constants;
import com.orhanobut.logger.Logger;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

@SuppressWarnings({"unused","WeakerAccess"})
public class MyCourseModel extends BaseModel<MyCourseModel, MyCourseModel.ViewHolder> {

    private final static String sectionPattern =
            "Sec.*?>(\\d\\d)<.*?size=\"2\">([\\S\\s]*),[\\s\\S]*?(<TR>[\\s\\S]*?)</TABLE>";
    private final static String timesPattern =
            "<TD.*?height=\"17\">([UMTWH]*?)<[\\s\\S]*?TD.*?>([\\S ]*?)"+
                    "<[\\s\\S]*?TD.*?>([\\S ]*?)<[\\s\\S]*?TD.*?>([\\S ]*?)</TD>";
    private final static String finalPattern =
            "<TD.*?height=\"19\"><FONT color=\"#0000FF\">[\\S ]*?"+
                    "<[\\s\\S]*?TD.*?>.*?>([\\S ]*?)</FONT><[\\s\\S]*?TD.*?>.*?>([\\S ]*?)"+
                    "</FONT><[\\s\\S]*?TD.*?>.*?>([\\S ]*?)</FONT></TD>";
    private final static Pattern pData = Pattern.compile(sectionPattern,Pattern.CASE_INSENSITIVE);
    private final static Pattern pTimes = Pattern.compile(timesPattern,Pattern.CASE_INSENSITIVE);
    private final static Pattern pFinal = Pattern.compile(finalPattern,Pattern.CASE_INSENSITIVE);

    public String courseName;
    public String courseNumber;
    public String sectionNumber;
    public String departmentCode;
    public String doctor;
    public String room;
    public List<SectionTime> times;
    public String finalExamDate;
    public String finalExamTime;
    public int bgColor;

    public MyCourseModel(String courseName, String courseNumber, String sectionNumber) {
        this.courseName = courseName;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
        this.departmentCode = Constants.getDebCode(courseName);
        this.doctor = "";
        int rand = 0;
        for (char b : courseName.toCharArray()){
            rand += b%2 == 0 ? b*b : b*10;
        }
        float caj = (float) (((Integer.parseInt(courseNumber) * Integer.parseInt(courseNumber) * 13) % 15) / 100.0);
        float hue = (float) (rand % 255) + caj;
        this.bgColor = Color.HSVToColor(30, new float[]{hue, 0.7f,0.8f});
        this.times = new ArrayList<>();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof MyCourseModel))
            return false;
        else if (o == this)
            return true;
        MyCourseModel object = (MyCourseModel) o;
        return courseName.equals(object.courseName) &&
                courseNumber.equals(object.courseNumber) &&
                sectionNumber.equals(object.sectionNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseName, courseNumber, sectionNumber);
    }

    public String getCourseTitle() {
        return courseName+courseNumber;
    }

    public void update(String data) {
        Matcher mData = pData.matcher(data);
        this.times = new ArrayList<>();
        if(mData.find()) {
            this.doctor = mData.group(2);
            Matcher mTimes = pTimes.matcher(mData.group(3));
            while (mTimes.find()){
                times.add(new SectionTime(mTimes.group(1),mTimes.group(2),mTimes.group(3),mTimes.group(4)));
            }
            Matcher mFinal = pFinal.matcher(mData.group(3));
            if (mFinal.find()){
                finalExamDate = mFinal.group(1);
                finalExamTime = mFinal.group(2)+"-"+mFinal.group(3);
            }
        }
    }

    public String getFinalExam() {
        return finalExamDate.trim().equals("")? "" : finalExamDate+" @ "+finalExamTime;
    }

    public class SectionTime {
        public String days;
        public String from;
        public String to;
        public String room;

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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_title) TextView courseTitle;
        @BindView(R.id.course_pre) TextView section;
        public @BindView(R.id.delete) LinearLayout delete;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
