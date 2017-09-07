package com.muqdd.iuob2.app;

/**
 * Created by Ali Yusuf on 9/5/2017.
 * iUOB-2
 */

public interface PermissionCallback {
    void onGranted(String permission);
    void onDenied(String permission);
}
