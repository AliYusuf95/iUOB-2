package com.muqdd.iuob2.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ali Yusuf on 9/5/2017.
 * iUOB-2
 */

public class PermissionRequest {
    private String[] permissions;
    private PermissionCallback callBack;
    private int code;

    public PermissionRequest(int code, String[] permissions, PermissionCallback callBack) {
        this.code = code;
        this.permissions = permissions;
        this.callBack = callBack;
    }

    public PermissionRequest(int code, String permissions, PermissionCallback callBack) {
        this.code = code;
        this.permissions = new String[]{permissions};
        this.callBack = callBack;
    }

    public List<String> getPermissions() {
        return Arrays.asList(permissions);
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public PermissionCallback getCallBack() {
        return callBack;
    }

    public void setCallBack(PermissionCallback callBack) {
        this.callBack = callBack;
    }
}
