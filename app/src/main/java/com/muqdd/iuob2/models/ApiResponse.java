package com.muqdd.iuob2.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ali Yusuf on 10/14/2017.
 * iUOB-2
 */

public class ApiResponse {

    @SerializedName("token")
    @Expose
    private String token = null;
    @SerializedName("user")
    @Expose
    private User user;

    /* If request has error */
    @SerializedName("param")
    @Expose
    private String param = null;
    @SerializedName(value = "msg", alternate = "message")
    @Expose
    private String msg = null;
    @SerializedName("value")
    @Expose
    private String value = null;
    @SerializedName("code")
    @Expose
    private int code = 0;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
