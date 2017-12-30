package com.muqdd.iuob2.network;

import com.muqdd.iuob2.features.my_schedule.MyCourse;
import com.muqdd.iuob2.features.schedule_builder.BCourse;
import com.muqdd.iuob2.models.CalendarSemester;
import com.muqdd.iuob2.models.Course;
import com.muqdd.iuob2.models.RestResponse;
import com.muqdd.iuob2.models.Section;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Public iUOB api for UOB Schedule
 *
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

public interface UOBSchedule {

    @GET("ucs/fetch-course-prefixes")
    Call<RestResponse<List<String>>> coursesPrefix(
            @Query("year") int year,
            @Query("sem") int semester
    );

    @GET("ucs/fetch-courses")
    Call<RestResponse<List<Course>>> courses(
            @Query("year") int year,
            @Query("sem") int semester,
            @Query("codePrefix") String codePrefix
    );

    @GET("ucs/fetch-sections")
    Call<RestResponse<List<Section>>> sections(
            @Query("year") String year,
            @Query("sem") String semester,
            @Query("courseId") String courseId
    );

    @GET("ucs/fetch-sections-list")
    Call<RestResponse<List<List<MyCourse>>>> sectionsList(
            @Query("year") int year,
            @Query("sem") int semester,
            @QueryMap Map<String,String> sections
    );

    @GET("ucs/fetch-sections-for-courses")
    Call<RestResponse<List<BCourse>>> coursesSectionsList(
            @Query("year") int year,
            @Query("sem") int semester,
            @Query("coursesId[]") List<String> coursesId
    );

    @GET
    Call<List<CalendarSemester>> semesterCalendar(@Url String url);
}
