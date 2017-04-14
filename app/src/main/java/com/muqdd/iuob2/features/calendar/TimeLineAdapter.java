package com.muqdd.iuob2.features.calendar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.vipulasri.timelineview.TimelineView;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.models.CalendarSemesterInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ali Yusuf on 4/12/2017.
 * iUOB-2
 */

@SuppressWarnings({"unused","WeakerAccess"})
public class TimeLineAdapter extends RecyclerView.Adapter<CalendarSemesterInfo.ViewHolder> {

    private List<CalendarSemesterInfo> mFeedList;

    public TimeLineAdapter(List<CalendarSemesterInfo> feedList) {
        mFeedList = feedList;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public CalendarSemesterInfo.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CalendarSemesterInfo.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.row_timeline,parent,false)
                , viewType);
    }

    @Override
    public void onBindViewHolder(CalendarSemesterInfo.ViewHolder holder, int position) {
        holder.title.setText(mFeedList.get(position).getEventName());
        holder.date.setText(mFeedList.get(position).getDate());
        try {
            Long time = new Date().getTime();
            Date now = new Date(time - time % (24 * 60 * 60 * 1000));
            Date dateFrom = CalendarSemesterInfo.parser.parse(mFeedList.get(position).getDateFrom());
            Date dateTo = CalendarSemesterInfo.parser.parse(mFeedList.get(position).getDateTo());
            if (now.before(dateFrom)){
                holder.timelineView.setMarker(holder.normal);
            } else if (isSameDay(now,dateFrom) || isSameDay(now,dateTo) ||
                    (now.after(dateFrom) && now.before(dateTo))){
                holder.timelineView.setMarker(holder.active);
            } else {
                holder.timelineView.setMarker(holder.inactive);
            }
        } catch (ParseException e) {
            holder.timelineView.setMarker(holder.normal);
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(date1).equals(dateFormat.format(date2));
    }

    @Override
    public void onViewRecycled(CalendarSemesterInfo.ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.title.setText(null);
        holder.date.setText(null);
    }

    @Override
    public int getItemCount() {
        return mFeedList != null ? mFeedList.size() : 0;
    }
}