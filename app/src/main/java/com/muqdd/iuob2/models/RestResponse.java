package com.muqdd.iuob2.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ali Yusuf on 9/8/2017.
 * iUOB-2
 */

@SuppressWarnings("WeakerAccess")
public class RestResponse<T> {

    @SerializedName("statusCode")
    @Expose
    protected int statusCode;
    @SerializedName("msg")
    @Expose
    protected String msg;
    @SerializedName("data")
    @Expose
    protected T data = null;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
