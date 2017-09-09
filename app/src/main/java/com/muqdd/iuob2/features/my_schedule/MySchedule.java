package com.muqdd.iuob2.features.my_schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ali Yusuf on 9/8/2017.
 * iUOB-2
 */

public class MySchedule {

    private List<MyCourse> courseList;
    private int year;
    private int semester;

    public MySchedule() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int semester;
        if (month > 1 && month < 7){ // Second semester
            year -= 1;
            semester = 2;
        }
        else if (month > 6 && month < 9) { // Summer semester
            year -= 1;
            semester = 3;
        }
        else { // First semester (from 9 to 12)
            semester = 1;
        }
        this.year = year;
        this.semester = semester;
        this.courseList = new ArrayList<>();
    }

    public MySchedule(int year, int semester) {
        this.year = year;
        this.semester = semester;
        this.courseList = new ArrayList<>();
    }

    public void addCourse(MyCourse course) {
        courseList.add(course);
    }

    public List<MyCourse> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<MyCourse> courseList) {
        this.courseList = courseList;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public Map<String, String> getSectionsParam() {
        Map<String,String> params = new HashMap<>();
        for (int i=0; i < courseList.size(); i++) {
            MyCourse course = courseList.get(i);
            params.put("sectionList["+i+"][courseId]",course.getCourseId());
            params.put("sectionList["+i+"][sectionNo]",course.getSectionNo());
        }
        return params;
    }

    public static List<MyCourse> getCoursesList(List<List<MyCourse>> courseList) {
        List<MyCourse> list = new ArrayList<>();
        if (courseList == null){
            return list;
        }
        for (List<MyCourse> temp : courseList) {
            if (temp.size() > 0 && temp.get(0) != null) {
                list.add(temp.get(0));
            }
        }
        return list;
    }
}
