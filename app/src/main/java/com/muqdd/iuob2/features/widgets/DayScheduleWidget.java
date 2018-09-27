package com.muqdd.iuob2.features.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.features.my_schedule.MyCourse;
import com.muqdd.iuob2.features.splash.SplashActivity;
import com.muqdd.iuob2.models.Timing;
import com.muqdd.iuob2.models.User;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of App Widget functionality.
 */
public class DayScheduleWidget extends AppWidgetProvider {

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
                        hList.put(time, course);
                        hList.put(time, course);
                        hList.put(time, course);
                        hList.put(time, course);
                        hList.put(time, course);
                        hList.put(time, course);
                        hList.put(time, course);
                        hList.put(time, course);
                    }
                }
            }
        }


        // define the RemoteViews object
        RemoteViews views;

        // get current day
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        // update UI
        if (uList.size() == 0 && mList.size() == 0 && tList.size() == 0 && wList.size() == 0 && hList.size() == 0) {
            views = new RemoteViews(context.getPackageName(), R.layout.row_finalexam_time);
            views.setTextViewText(R.id.text, "Go to 'My Schedule' in iUOB App to add courses.");
            Intent intent = new Intent(context, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.text, pendingIntent);
        } else if (day == Calendar.FRIDAY || day == Calendar.SATURDAY) {
            views = new RemoteViews(context.getPackageName(), R.layout.row_finalexam_time);
            views.setTextViewText(R.id.text, "Have a nice weekend \uD83D\uDE0E");
        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.widget_day_schedule);
            views.removeAllViews(R.id.container);
            views.setTextViewText(R.id.day, calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US));
            switch (day) {
                case Calendar.SUNDAY:
                    addCoursesForLayout(context, views, uList);
                    break;
                case Calendar.MONDAY:
                    addCoursesForLayout(context, views, mList);
                    break;
                case Calendar.TUESDAY:
                    addCoursesForLayout(context, views, tList);
                    break;
                case Calendar.WEDNESDAY:
                    addCoursesForLayout(context, views, wList);
                    break;
                case Calendar.THURSDAY:
                    addCoursesForLayout(context, views, hList);
                    break;
            }
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void addCoursesForLayout(Context context, RemoteViews remoteViews, Map<Timing, MyCourse> list) {
        for (Timing time : list.keySet()){
            if (list.get(time) != null) {
                remoteViews.addView(R.id.container, createScheduleCell(context, list.get(time), time));
            }
        }
    }

    private static RemoteViews createScheduleCell(Context context, final MyCourse course, Timing time) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.cell_day_schedule_widget);
        remoteViews.setTextViewText(R.id.course, "\uD83D\uDCDA " + course.getCourseId());
        remoteViews.setTextViewText(R.id.time, "\uD83D\uDD57 " + time.getTimeFrom() + "-" + time.getTimeTo());
        remoteViews.setTextViewText(R.id.room, "\uD83C\uDFE2 " + time.getLocation());
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

