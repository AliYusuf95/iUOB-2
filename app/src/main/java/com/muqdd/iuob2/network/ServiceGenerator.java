package com.muqdd.iuob2.network;

import android.content.Context;
import android.net.ConnectivityManager;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.muqdd.iuob2.BuildConfig;
import com.muqdd.iuob2.app.Auth;
import com.muqdd.iuob2.models.User;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */
@SuppressWarnings("WeakerAccess")
public class ServiceGenerator {

    public static final String API_BASE_URL = "https://iuob.net/";

    private static ConnectivityManager connectivityManager;

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder().addInterceptor(logging);

    public static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public static <S> S createService(Class<S> serviceClass,@Nullable User user) {
        // Add Authentication Interceptors if available
        if (Auth.isValidUser(user)) {
            addUserAuth(user);
        }
        if (connectivityManager != null) {
            ConnectivityInterceptor interceptor =
                    new ConnectivityInterceptor(connectivityManager);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }

    /**
     * Add user auth to the request header
     *
     * @param user current logged in user
     */
    private static void addUserAuth(User user) {
        AuthenticationInterceptor authHeader =
                new AuthenticationInterceptor(user.getAuthHeader());

        if (!httpClient.interceptors().contains(authHeader)) {
            httpClient.addInterceptor(authHeader);

            builder.client(httpClient.build());
            retrofit = builder.build();
        }
    }

    public static void init(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
