package com.muqdd.iuob2.features.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import android.widget.RemoteViews;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.features.my_schedule.MyCourse;
import com.muqdd.iuob2.features.splash.SplashActivity;
import com.muqdd.iuob2.models.Timing;
import com.muqdd.iuob2.models.User;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of App Widget functionality.
 */
public class FullScheduleWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // section time comparator
        Comparator<Timing> comparator = (t1, t2) -> {
            // compare from then to then room to sort sections
            int f,t;
            return (f = t1.getTimeFrom().compareTo(t2.getTimeFrom())) == 0 ?
                    ((t = t1.getLocation().compareTo(t2.getLocation())) == 0 ?
                            t1.getLocation().compareTo(t2.getLocation()) : t ): f;
        };

        // init lists with comparator
        Map<Timing, MyCourse> uList = new TreeMap<>(comparator);
        Map<Timing, MyCourse> mList = new TreeMap<>(comparator);
        Map<Timing, MyCourse> tList = new TreeMap<>(comparator);
        Map<Timing, MyCourse> wList = new TreeMap<>(comparator);
        Map<Timing, MyCourse> hList = new TreeMap<>(comparator);

        for (MyCourse course : User.getMySchedule(context).getCourseList()) {
            if (course.getTimingLegacy() != null) {
                for (Timing time : course.getTimingLegacy()) {
                    if (time.getDay().contains("U")) {
                        uList.put(time, course);
                    }
                    if (time.getDay().contains("M")) {
                        mList.put(time, course);
                    }
                    if (time.getDay().contains("T")) {
                        tList.put(time, course);
                    }
                    if (time.getDay().contains("W")) {
                        wList.put(time, course);
                    }
                    if (time.getDay().contains("H")) {
                        hList.put(time, course);
                    }
                }
            }
        }


        // define the RemoteViews object
        RemoteViews views;

        // update UI
        if (uList.size() == 0 && mList.size() == 0 && tList.size() == 0 && wList.size() == 0 && hList.size() == 0) {
            views = new RemoteViews(context.getPackageName(), R.layout.row_finalexam_time);
            views.setInt(R.id.layout, "setBackgroundColor", ContextCompat.getColor(context, R.color.semi_white));
            views.setTextViewText(R.id.text, "Go to 'My Schedule' in iUOB App to add courses.");
            Intent intent = new Intent(context, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.text, pendingIntent);
        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.widget_full_schedule);
            addCoursesForLayout(context, views, R.id.u_layout, uList);
            addCoursesForLayout(context, views, R.id.m_layout, mList);
            addCoursesForLayout(context, views, R.id.t_layout, tList);
            addCoursesForLayout(context, views, R.id.w_layout, wList);
            addCoursesForLayout(context, views, R.id.h_layout, hList);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void addCoursesForLayout(Context context, RemoteViews remoteViews, @IdRes int layoutId, Map<Timing, MyCourse> list) {
        remoteViews.removeAllViews(layoutId);
        for (Timing time : list.keySet()){
            if (list.get(time) != null) {
                remoteViews.addView(layoutId, createScheduleCell(context, list.get(time), time));
            }
        }
    }

    private static RemoteViews createScheduleCell(Context context, final MyCourse course, Timing time) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.cell_schedule_widget);
        remoteViews.setInt(R.id.layout, "setBackgroundColor", course.getBgColor());
        remoteViews.setTextViewText(R.id.course, course.getCourseId());
        remoteViews.setTextViewText(R.id.time_from, time.getTimeFrom());
        remoteViews.setTextViewText(R.id.time_to, time.getTimeTo());
        remoteViews.setTextViewText(R.id.room, time.getLocation());
        return remoteViews;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

