package com.muqdd.iuob2.network;

import com.muqdd.iuob2.app.Auth;
import com.muqdd.iuob2.models.CalendarSemesterModel;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

public interface iUOBApi {

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
            @Field("userId") String userId,
            @Field("storyId") String storyId
    );
}
