package com.muqdd.iuob2.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.logger.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ali Yusuf on 9/8/2017.
 * iUOB-2
 */

public class FinalExam {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("to")
    @Expose
    private String to;

    public FinalExam() {
        this.date = "";
        this.from = "";
        this.to = "";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean hasClash(FinalExam exam) {
        Logger.d("check");
        boolean sameDay = date.equals(exam.date);
        if (!sameDay) {
            // not same day
            return false;
        }
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date examAFrom  = dateFormat.parse(from);
            Date examATo = dateFormat.parse(to);
            Date examBFrom  = dateFormat.parse(exam.from);
            Date examBTo = dateFormat.parse(exam.to);
            if (((afterOrEqual(examAFrom, examBFrom) && beforeOrEqual(examAFrom, examBTo)) ||
                    (afterOrEqual(examATo, examBFrom) && beforeOrEqual(examATo,  examBTo)) ||
                    (afterOrEqual(examBFrom, examAFrom) && beforeOrEqual(examBFrom, examATo)) ||
                    (afterOrEqual(examBTo, examAFrom) && beforeOrEqual(examBTo, examATo)))) {
                // if start or end time is between other exams times -> CLASH
                return true;
            }
        } catch (ParseException e) {
            // wong time format don't pass this course
            return true;
        }
        // there is no clash between tha exams
        return false;
    }

    private boolean afterOrEqual(Date a, Date b){
        return a.after(b) || a.equals(b);
    }

    private boolean beforeOrEqual(Date a, Date b){
        return a.before(b) || a.equals(b);
    }

    @Override
    public String toString() {
        return date + " @ " + from + " - " + to;
    }
}
