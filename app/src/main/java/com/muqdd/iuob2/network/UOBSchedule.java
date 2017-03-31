package com.muqdd.iuob2.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

public interface UOBSchedule {

    @GET("schedule2.abrv")
    Call<ResponseBody> semesterCourses(
            @Query("prog") String prog,
            @Query("cyer") String year,
            @Query("csms") String semester
    );

    @GET("schedule2.thecourses")
    Call<ResponseBody> coursesList(
            @Query("inll") String departmentCode,
            @Query("theabv") String theabv,
            @Query("prog") String prog,
            @Query("cyer") String year,
            @Query("csms") String semester
    );

    @GET("schedule2.contentpage")
    Call<ResponseBody> sectionsList(
            @Query("prog") String prog,
            @Query("abv") String arabm,
            @Query("inl") String departmentCode,
            @Query("crsno") String courseNo,
            @Query("crd") String credits,
            @Query("cyer") String year,
            @Query("csms") String semester
    );

    @GET("enr_sections")
    Call<ResponseBody> availableSeats(
            @Query("pcrsnbr") String courseNo,
            @Query("pcrsinlcde") String departmentCode
    );
}
