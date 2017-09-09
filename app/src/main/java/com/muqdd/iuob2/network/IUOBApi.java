package com.muqdd.iuob2.network;

import com.muqdd.iuob2.app.Auth;
import com.muqdd.iuob2.features.my_schedule.MyCourse;
import com.muqdd.iuob2.features.schedule_builder.BCourse;
import com.muqdd.iuob2.models.Course;
import com.muqdd.iuob2.models.RestResponse;
import com.muqdd.iuob2.models.Section;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

public interface IUOBApi {

    @FormUrlEncoded
    @POST("signup")
    Call<List<Auth>> signup(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );

    /**
     * Login User
     */
    @FormUrlEncoded
    @POST("login")
    Call<List<Auth>> login(
            @Field("email") String email,
            @Field("password") String password
    );

    /**
     * Update user profile
     * @param options account options to update:
     *                name | email | gender | studentID | dateOfBirth | college
     *                To change password you need to pass:
     *                password & confirm
     */
    @FormUrlEncoded
    @POST("account")
    Call<List<Auth>> updateUserProfile(
            @QueryMap Map<String, String> options
    );

    @FormUrlEncoded
    @DELETE("account")
    Call<List<Auth>> delete();

    @GET("get-user")
    Call<List<Auth>> getUser(
            @Query("id") String id
    );

    /**
     * Upload story image
     * RequestBody usage:
     *      RequestBody.create(MediaType.parse("text/plain"), "...");
     */
    @Multipart
    @POST("upload")
    Call<Void> storyUpload(
            @Part("createdBy") RequestBody createdBy,
            @Part("type") RequestBody type,
            @Part("mediaDuration") RequestBody mediaDuration,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part MultipartBody.Part image
    );

    @GET("get-stories")
    Call<Void> getStories();

    @GET("get-my-stories")
    Call<Void> getMyStories();

    @FormUrlEncoded
    @POST("delete-story")
    Call<List<Auth>> deleteStory(
            @Field("storyId") String storyId
    );

    @GET("view-story")
    Call<List<Auth>> viewStory(
            @Query("userId") String userId,
            @Query("storyId") String storyId
    );

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


}
