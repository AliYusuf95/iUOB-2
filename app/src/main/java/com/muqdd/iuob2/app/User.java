package com.muqdd.iuob2.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.models.MyCourseModel;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.network.UOBSchedule;
import com.muqdd.iuob2.notification.AlarmNotificationReceiver;
import com.muqdd.iuob2.utils.SPHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Ali Yusuf on 4/7/2017.
 * iUOB-2
 */

public class User {
    private final static String TAG = User.class.getSimpleName();
    private final static String NOTIFICATION = "notification";
    private static final String MY_COURSES = "my_courses";
    private static final String MY_COURSES_UPDATE = "my_courses_update";
    private static final Type MY_COURSES_TYPE = new TypeToken<List<MyCourseModel>>() {}.getType();

    public static boolean setNotification(Context context, boolean state) {
        if (state) {
            SPHelper.saveToPrefs(context, NOTIFICATION, "on");
            for (MyCourseModel course : getCourses(context)){
                for (MyCourseModel.SectionTime time : course.times) {
                    scheduleNotification(context, course, time);
                }
            }
            Log.i(TAG,"notifications set to ON");
        } else {
            SPHelper.deleteFromPrefs(context,NOTIFICATION);
            stopNotificationSchedule(context);
            Log.i(TAG,"notifications set to OFF");
        }
        return state;
    }

    public static boolean isNotificationOn(Context context) {
        return SPHelper.getFromPrefs(context,NOTIFICATION) != null;
    }

    public static void addCourse(Context context, MyCourseModel course) {
        if (course == null){
            Log.e(TAG,"Empty data passed");
        }
        List<MyCourseModel> myCoursesList = getCourses(context);
        myCoursesList.add(course);
        String coursesJson = new Gson().toJson(myCoursesList,MY_COURSES_TYPE);
        SPHelper.saveToPrefs(context,MY_COURSES,coursesJson);
        setCoursesUpdated(context, false);
    }

    public static void deleteCourse(Context context, MyCourseModel course) {
        if (course == null){
            Log.e(TAG,"Empty data passed");
        }
        List<MyCourseModel> myCoursesList = getCourses(context);
        myCoursesList.remove(course);
        String coursesJson = new Gson().toJson(myCoursesList,MY_COURSES_TYPE);
        SPHelper.saveToPrefs(context,MY_COURSES,coursesJson);
    }

    public static List<MyCourseModel> getCourses(Context context) {
        String coursesJson = SPHelper.getFromPrefs(context, MY_COURSES);
        ArrayList<MyCourseModel> myCoursesList;
        if (coursesJson != null){
            myCoursesList = new Gson().fromJson(coursesJson,MY_COURSES_TYPE);
        } else {
            myCoursesList = new ArrayList<>();
        }
        return myCoursesList;
    }

    public static boolean isCoursesUpdated(Context context) {
        String strDate;
        Long time = new Date().getTime();
        return (strDate = SPHelper.getFromPrefs(context, MY_COURSES_UPDATE)) != null &&
                new Date(time - time % (24 * 60 * 60 * 1000)).toString().equals(strDate);
    }

    private static void setCoursesUpdated(Context context, boolean updated) {
        if (updated) {
            Long time = new Date().getTime();
            Date now = new Date(time - time % (24 * 60 * 60 * 1000));
            SPHelper.saveToPrefs(context, MY_COURSES_UPDATE, now.toString());
        } else {
            SPHelper.deleteFromPrefs(context, MY_COURSES_UPDATE);
        }
    }

    public static void fetchCoursesData(final Context context, final Runnable callback){
        // fetch data synchronously then rebuild schedule
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean hasError = false;
                // calculate current year and semester
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH)+1;
                String semester;
                if (month > 1 && month < 7){ // Second semester
                    year -= 1;
                    semester = "2";
                    Log.d(TAG, "case 1");
                }
                else if (month > 6 && month < 10) { // Summer semester
                    year -= 1;
                    semester = "3";
                    Log.d(TAG, "case 2");
                }
                else { // First semester (from 9 to 12)
                    semester = "1";
                    Log.d(TAG, "case 3");
                }
                // fetch data of current course
                for (final MyCourseModel course : getCourses(context)) {
                    try {
                        Response<ResponseBody> response = ServiceGenerator.createService(UOBSchedule.class)
                                .sectionsList("1",course.courseName,course.departmentCode,course.courseNumber,
                                        "0", String.valueOf(year),semester).execute();
                        if (response.code() == 200){
                            String sectionsPattern = "(Sec\\. \\[ </FONT><FONT color=\"#FF0000\">" + course.sectionNumber +
                                    "</FONT>[\\s\\S]*?<TABLE[\\s\\S]*?</TABLE>[\\s\\S]*?)";
                            Pattern pSections =
                                    Pattern.compile(sectionsPattern,Pattern.UNIX_LINES | Pattern.CASE_INSENSITIVE);
                            Matcher mSections = pSections.matcher(response.body().string());
                            if (mSections.find()){
                                course.update(mSections.group(1));
                                User.updateCourse(context, course);
                            }
                        }
                    } catch (IOException e) {
                        hasError = true;
                        Log.e(TAG,"Something goes wrong");
                        break;
                    }
                }
                if (!hasError && isNotificationOn(context)){
                    for (MyCourseModel course : getCourses(context)){
                        for (MyCourseModel.SectionTime time : course.times){
                            scheduleNotification(context, course, time);
                        }
                    }
                }
                setCoursesUpdated(context, !hasError);
                callback.run();
            }
        });
    }

    private static void updateCourse(Context context, MyCourseModel course) {
        if (course == null){
            Log.e(TAG,"Empty data passed");
        }
        List<MyCourseModel> myCoursesList = getCourses(context);
        myCoursesList.remove(course);
        myCoursesList.add(course);
        String coursesJson = new Gson().toJson(myCoursesList,MY_COURSES_TYPE);
        SPHelper.saveToPrefs(context,MY_COURSES,coursesJson);
    }

    private static void scheduleNotification(Context context,MyCourseModel course, MyCourseModel.SectionTime time) {
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(course.getCourseTitle())
                //.setContentText("Your lecture will start soon")
                .setSmallIcon(R.drawable.ic_stat_logo_white)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setDefaults(android.app.Notification.DEFAULT_ALL)
                .setAutoCancel(true);
        try {
            Calendar cur_cal = new GregorianCalendar();
            cur_cal.setTimeInMillis(System.currentTimeMillis());

            Calendar cal = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            cal.setTime(sdf.parse(time.from));
            cal.set(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
            cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)-15);
            cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
            cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
            cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));

            for (char day : time.days.toCharArray()){
                switch (day){
                    case 'U':
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        break;
                    case 'M':
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        break;
                    case 'T':
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                        break;
                    case 'W':
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                        break;
                    case 'H':
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                        break;
                }

                // set notification content
                builder.setContentText("Your lecture will start soon at "+time.from);

                Log.i(TAG,"Schedule notification set for "+course.getCourseTitle()+", at: "+
                        sdf.format(cal.getTimeInMillis())+" on day: "+cal.get(Calendar.DAY_OF_WEEK));

                if (System.currentTimeMillis() > cal.getTimeInMillis()){
                    cal.setTimeInMillis(cal.getTimeInMillis() + 24*60*60*1000);
                }

                Intent nIntent = new Intent(context, AlarmNotificationReceiver.class);
                nIntent.putExtra(AlarmNotificationReceiver.NOTIFICATION_ID, 1);
                nIntent.putExtra(AlarmNotificationReceiver.NOTIFICATION, builder.build());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, nIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

//                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
//                        AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            }

        } catch (ParseException e) {
            Log.e(TAG, "Fail to parse given time \n" + e.toString());
        }
    }

    private static void stopNotificationSchedule(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent nIntent = new Intent(context, AlarmNotificationReceiver.class);
        nIntent.putExtra(AlarmNotificationReceiver.NOTIFICATION_ID, 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, nIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {
            Log.e(TAG, "AlarmManager update was not canceled \n" + e.toString());
        }
    }
}