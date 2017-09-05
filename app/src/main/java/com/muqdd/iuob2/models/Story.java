package com.muqdd.iuob2.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Yusuf on 9/3/2017.
 * iUOB-2
 */

public class Story {
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
    private Integer views;
    @SerializedName("mediaDuration")
    @Expose
    private Integer mediaDuration;
    @SerializedName("location")
    @Expose
    private List<Integer> location = new ArrayList<>();
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("watchedBy")
    @Expose
    private List<String> watchedBy = new ArrayList<>();

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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getMediaDuration() {
        return mediaDuration;
    }

    public void setMediaDuration(Integer mediaDuration) {
        this.mediaDuration = mediaDuration;
    }

    public List<Integer> getLocation() {
        return location;
    }

    public void setLocation(List<Integer> location) {
        this.location = location;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getWatchedBy() {
        return watchedBy;
    }

    public void setWatchedBy(List<String> watchedBy) {
        this.watchedBy = watchedBy;
    }
}
