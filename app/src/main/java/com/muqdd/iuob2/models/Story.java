package com.muqdd.iuob2.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ali Yusuf on 9/3/2017.
 * iUOB-2
 */

public class Story {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    private Date createdAtDate;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("isApproved")
    @Expose
    private Boolean isApproved;
    @SerializedName("isDeletedByUser")
    @Expose
    private Boolean isDeletedByUser;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("views")
    @Expose
    private int views;
    @SerializedName("createdBy")
    @Expose
    private User createdBy;
    @SerializedName("location")
    @Expose
    private List<Double> location = null;
    @SerializedName("watchedBy")
    @Expose
    private List<User> watchedBy = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtDuration() {
        int seconds = calculateCreatedAtDuration();
        String out ="";
        int hr = seconds/(60*60);
        seconds = seconds%(60*60);
        int min = seconds/(60);
        seconds = seconds%(60);

        boolean mtrue = false;
        if(min > 0){
            mtrue = true;
            out =  min + " min" +(min == 1 ? " ":"s ");
        }
        if(seconds > 0){
            if(mtrue)
                out += "and ";
            out +=  seconds + " s";
        }
        if(hr>0) {
            out =  hr + " hour" +(hr == 1 ? " ":"s ");
        }
        return out;
    }

    private int calculateCreatedAtDuration() {
        int diff = 0;
        if (createdAtDate == null) {
            SimpleDateFormat format =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            try {
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                createdAtDate = format.parse(createdAt);
                long diffInMillies = Math.abs(createdAtDate.getTime() - new Date().getTime());
                diff = (int) TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            } catch (ParseException e) {
                createdAtDate = new Date();
            }
        } else {
            long diffInMillies = Math.abs(createdAtDate.getTime() - new Date().getTime());
            diff = (int) TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }
        return diff;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Boolean getIsDeletedByUser() {
        return isDeletedByUser;
    }

    public void setIsDeletedByUser(Boolean isDeletedByUser) {
        this.isDeletedByUser = isDeletedByUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public int getViews() {
        return views;
    }

    public String getViewsFormat() {
        return String.format(Locale.getDefault(), "# Views: %s", views);
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public List<User> getWatchedBy() {
        return watchedBy;
    }

    public void setWatchedBy(List<User> watchedBy) {
        this.watchedBy = watchedBy;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, Story.class);
    }

    public static Story fromJson(String json) {
        return json == null ? null : new Gson().fromJson(json, Story.class);
    }
}
