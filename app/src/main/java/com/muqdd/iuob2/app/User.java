package com.muqdd.iuob2.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.muqdd.iuob2.features.my_schedule.MyCourse;
import com.muqdd.iuob2.features.my_schedule.MySchedule;
import com.muqdd.iuob2.models.MyCourseModel;
import com.muqdd.iuob2.models.SectionTimeModel;
import com.muqdd.iuob2.models.Timing;
import com.muqdd.iuob2.notification.AlarmNotificationReceiver;
import com.muqdd.iuob2.utils.SPHelper;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ali Yusuf on 4/7/2017.
 * iUOB-2
 */

public class User {

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("studentID")
    @Expose
    private String studentID;
    @SerializedName("dateOfBirth")
    @Expose
    private String dateOfBirth;
    @SerializedName("lastLogin")
    @Expose
    private String lastLogin;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("token")
    @Expose
    private String token;

    /* Transient variables */
    private transient final static String TAG = User.class.getSimpleName();
    private transient final static String NOTIFICATION = "notification";
    private transient static final String MY_COURSES = "my_courses";
    private transient static final String MY_COURSES_UPDATE = "my_courses_update";
    private transient static final Type MY_COURSES_TYPE = new TypeToken<List<MyCourse>>() {}.getType();
    private transient static boolean fetchingData = false;


    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuthHeader() {
        return "Token "+token;
    }

    public static boolean setNotification(Context context, boolean state) {
        if (state) {
            SPHelper.saveToPrefs(context, NOTIFICATION, "on");
            for (MyCourse course : getMySchedule(context).getCourseList()){
                for (Timing time : course.getTimingLegacy()) {
                    //TODO : change classes
//                    scheduleNotification(context, course, time);
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

    public static void addCourse(Context context, MyCourse course) {
        if (course == null){
            Log.e(TAG,"Empty data passed");
            return;
        }
        MySchedule mySchedule = getMySchedule(context);
        mySchedule.getCourseList().add(course);
        String coursesJson = new Gson().toJson(mySchedule, MySchedule.class);
        SPHelper.saveToPrefs(context, MY_COURSES, coursesJson);
        setCoursesUpdated(context, false);
    }

    public static void deleteCourse(Context context, MyCourse course) {
        if (course == null){
            Log.e(TAG,"Empty data passed");
            return;
        }
        MySchedule mySchedule = getMySchedule(context);
        mySchedule.getCourseList().remove(course);
        String coursesJson = new Gson().toJson(mySchedule, MySchedule.class);
        SPHelper.saveToPrefs(context, MY_COURSES, coursesJson);
    }

    public static MySchedule getMySchedule(Context context) {
        String coursesJson = SPHelper.getFromPrefs(context, MY_COURSES);
        MySchedule mySchedule;
        if (coursesJson != null){
            // avoid exceptions from old data
            try {
                mySchedule = new Gson().fromJson(coursesJson, MySchedule.class);
            } catch (Exception e){
                mySchedule = new MySchedule();
            }
        } else {
            mySchedule = new MySchedule();
        }
        return mySchedule;
    }

    public static void updateCourses(Context context, List<MyCourse> courseList) {
        if (courseList == null){
            Log.e(TAG,"Empty data passed");
            return;
        }
        MySchedule mySchedule = getMySchedule(context);
        mySchedule.setCourseList(courseList);
        String coursesJson = new Gson().toJson(mySchedule, MySchedule.class);
        SPHelper.saveToPrefs(context, MY_COURSES, coursesJson);
        setCoursesUpdated(context, true);
    }

    public static void setCourses(Context context, List<MyCourse> courseList) {
        updateCourses(context, courseList);
        setCoursesUpdated(context, false);
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

    private static void updateCourse(Context context, MyCourse course) {
        if (course == null){
            Log.e(TAG,"Empty data passed");
            return;
        }
        MySchedule mySchedule = getMySchedule(context);
        mySchedule.getCourseList().remove(course);
        mySchedule.getCourseList().add(course);
        String coursesJson = new Gson().toJson(mySchedule, MySchedule.class);
        SPHelper.saveToPrefs(context,MY_COURSES,coursesJson);
    }

    private static void scheduleNotification(Context context, MyCourseModel course, SectionTimeModel time) {
        List<String> timeResult = Arrays.asList(time.to.split(":"));
        // wrong time
        if (timeResult.size() != 2) {
            Log.e(TAG, "Fail to parse given time");
            return;
        }

        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeResult.get(0))+1);
        cal.set(Calendar.MINUTE, Integer.valueOf(timeResult.get(1))-20);

        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
        cal.set(Calendar.YEAR, cur_cal.get(Calendar.YEAR));

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
                default:
                    Log.e(TAG,"Wrong date");
                    return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE HH:mm", Locale.getDefault());
            Log.i(TAG,"Schedule notification set for "+course.getCourseTitle()+", at: "+
                    sdf.format(cal.getTimeInMillis())+" on day: "+cal.get(Calendar.DAY_OF_WEEK));

            // Check we aren't setting it in the past which would trigger it to fire instantly
            if (System.currentTimeMillis() > cal.getTimeInMillis()){
                cal.add(Calendar.DAY_OF_YEAR, 7);
                Log.w(TAG,"Alarm will start from tomorrow");
            }

            Intent nIntent = new Intent(context, AlarmNotificationReceiver.class);
            // pass notification data
            nIntent.putExtra(AlarmNotificationReceiver.NOTIFICATION_ID,
                    Integer.valueOf(course.courseNumber));
            nIntent.putExtra(AlarmNotificationReceiver.NOTIFICATION_TITLE,
                    course.getCourseTitle());
            nIntent.putExtra(AlarmNotificationReceiver.NOTIFICATION_TEXT,
                    "Your lecture will start soon at "+time.from);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    (int)System.currentTimeMillis(), nIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
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

    public static boolean isFetchingData() {
        return fetchingData;
    }

    public static void setFetchingData(boolean status) {
        fetchingData = status;
    }

    private static void testAlarm(Context context) {

        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 44);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        Logger.d("Schedule notification, at: "+
                sdf.format(cal.getTimeInMillis())+" on day: "+cal.get(Calendar.DAY_OF_WEEK));

        Intent nIntent = new Intent(context, AlarmNotificationReceiver.class);
        nIntent.putExtra(AlarmNotificationReceiver.NOTIFICATION_ID, 1);
        nIntent.putExtra(AlarmNotificationReceiver.NOTIFICATION_TITLE, "Test");
        nIntent.putExtra(AlarmNotificationReceiver.NOTIFICATION_TEXT, "Your lecture will start soon");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                (int)System.currentTimeMillis(), nIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    public static User fromJson(String json) {
        return json == null ? null : new Gson().fromJson(json, User.class);
    }
}