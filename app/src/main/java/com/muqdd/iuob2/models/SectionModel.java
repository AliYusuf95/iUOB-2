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
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

@SuppressWarnings({"unused","WeakerAccess"})
public class SectionModel extends BaseModel<SectionModel, SectionModel.ViewHolder> {

    // static variables to enhance performance
    private final static String coursePattern =
            "<center><B>\\s*?([^\\s][\\S\\s]*?)\\s*?</B></center>";
    private final static String sectionPattern =
            "Sec.*?>(\\d\\d)<.*?size=\"2\">([\\S\\s]*),[\\s\\S]*?(<TR>[\\s\\S]*?)</TABLE>";
    private final static String timesPattern =
            "<TD.*?height=\"17\">([UMTWH]*?)<[\\s\\S]*?TD.*?>([\\S ]*?)"+
            "<[\\s\\S]*?TD.*?>([\\S ]*?)<[\\s\\S]*?TD.*?>([\\S ]*?)</TD>";
    private final static String finalPattern =
            "<TD.*?height=\"19\"><FONT color=\"#0000FF\">[\\S ]*?"+
            "<[\\s\\S]*?TD.*?>.*?>([\\S ]*?)</FONT><[\\s\\S]*?TD.*?>.*?>([\\S ]*?)"+
            "</FONT><[\\s\\S]*?TD.*?>.*?>([\\S ]*?)</FONT></TD>";
    private final static Pattern pCourse = Pattern.compile(coursePattern,Pattern.CASE_INSENSITIVE);
    private final static Pattern pData = Pattern.compile(sectionPattern,Pattern.CASE_INSENSITIVE);
    private final static Pattern pTimes = Pattern.compile(timesPattern,Pattern.CASE_INSENSITIVE);
    private final static Pattern pFinal = Pattern.compile(finalPattern,Pattern.CASE_INSENSITIVE);

    public String title;
    public String number;
    public String doctor;
    public List<SectionTime> times;
    // TODO: Final Exam
    public String finalExamDate;
    public String finalExamTime;
    public String seats;
    public boolean showSeats;

    public SectionModel(String title, String data) {
        Matcher mData = pData.matcher(data);
        this.times = new ArrayList<>();
        if(mData.find()) {
            this.number = mData.group(1);
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
        Matcher mCourse = pCourse.matcher(data);
        this.title = title;
        this.showSeats = false;
    }

    public String getFinalExam() {
        return finalExamDate.trim().equals("")? "" : finalExamDate+" @ "+finalExamTime;
    }

    @Override
    public String toString() {
        String str = title+"\n"+
                "Sec. [" + number + "] => "+ doctor + "\n";
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
        return R.id.section_card;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.row_section;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(SectionModel.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        viewHolder.sectionNumber.setText(number);
        viewHolder.doctorName.setText(doctor);
        for (SectionTime sectionTime: times) {
            viewHolder.addRow(sectionTime);
        }
        if (!showSeats) {
            viewHolder.availableSeats.setVisibility(View.GONE);
        }
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(SectionModel.ViewHolder viewHolder) {
        super.unbindView(viewHolder);
        viewHolder.sectionNumber.setText(null);
        viewHolder.doctorName.setText(null);
        viewHolder.timeRows.removeAllViews();
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        protected @BindView(R.id.section_number) TextView sectionNumber;
        protected @BindView(R.id.doctor_name) TextView doctorName;
        protected @BindView(R.id.section_time_rows) LinearLayout timeRows;
        protected @BindView(R.id.available_seats) TextView availableSeats;
        private Context context;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = view.getContext();
        }

        @SuppressLint("InflateParams")
        public void addRow(SectionTime sectionTime){
            // separator line
            View line = new View(context);
            int hLine = (int) context.getResources().getDimension(R.dimen.row_section_time_divider);
            line.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,hLine));
            line.setBackgroundColor(ContextCompat.getColor(context,R.color.divider));
            // row layout
            View row = LayoutInflater.from(context).inflate(R.layout.row_section_time, null, false);
            ((TextView)row.findViewById(R.id.time)).setText(sectionTime.getDuration());
            ((TextView)row.findViewById(R.id.room)).setText(sectionTime.room);
            ((TextView)row.findViewById(R.id.days)).setText(sectionTime.days);
            // add to the card
            timeRows.addView(line);
            timeRows.addView(row);
        }
    }
}
