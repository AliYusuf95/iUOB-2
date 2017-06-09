package com.muqdd.iuob2.models;

/**
 * Created by Ali Yusuf on 6/7/2017.
 * iUOB-2
 */

public class SectionTimeModel {
    public String days;
    public String from;
    public String to;
    public String room;

    public SectionTimeModel(String days, String from, String to, String room) {
        this.days = days;
        this.from = from;
        this.to = to;
        this.room = room;
    }

    public String getDuration() {
        return from+" - "+to;
    }
}