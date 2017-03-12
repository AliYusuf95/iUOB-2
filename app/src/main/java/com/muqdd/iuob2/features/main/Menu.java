package com.muqdd.iuob2.features.main;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

enum Menu {
    SEMESTER_SCHEDULE ("Semester Schedule"),
    MY_SCHEDULE ("My Schedule"),
    SCHEDULE_BUILDER ("Schedule Builder"),
    MAP ("UOB Map"),
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