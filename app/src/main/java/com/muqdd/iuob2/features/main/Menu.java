package com.muqdd.iuob2.features.main;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public enum Menu {
    STORIES ("Stories"),
    SEMESTER_SCHEDULE ("Semester Schedule"),
    CALENDAR ("Calendar"),
    MY_SCHEDULE ("My Schedule"),
    SCHEDULE_BUILDER ("Schedule Builder"),
    MAP ("UOB Map"),
    ACCOUNT ("Account"),
    LINKS ("Useful Links"),
    ABOUT ("About");

    private String title;

    Menu(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}