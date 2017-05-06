package com.muqdd.iuob2.models;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 4/11/2017.
 * iUOB-2
 */

public class CalendarSemesterInfo{

    public final static SimpleDateFormat parser =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @SerializedName("event_name")
    @Expose
    private String eventName;
    @SerializedName("date_from")
    @Expose
    private String dateFrom;
    @SerializedName("date_to")
    @Expose
    private String dateTo;
    @SerializedName("comments")
    @Expose
    private String comments;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDate() {
        return dateFrom + " - " + dateTo;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.event_date)
        public TextView date;
        @BindView(R.id.event_title)
        public TextView title;
        @BindView(R.id.time_marker)
        public TimelineView timelineView;
        @BindDrawable(R.drawable.ic_marker)
        public Drawable normal;
        @BindDrawable(R.drawable.ic_marker_active)
        public Drawable active;
        @BindDrawable(R.drawable.ic_marker_inactive)
        public Drawable inactive;

        public ViewHolder(View view, int viewType) {
            super(view);
            ButterKnife.bind(this, view);
            timelineView.initLine(viewType);
        }
    }

}