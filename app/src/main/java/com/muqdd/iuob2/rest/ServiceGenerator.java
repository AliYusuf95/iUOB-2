package com.muqdd.iuob2.rest;

import android.content.Context;
import android.net.ConnectivityManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class ServiceGenerator {

    public static final String API_BASE_URL = "https://www.online.uob.edu.bh/cgi/enr/";

    private static ConnectivityManager connectivityManager;

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor().setLevel(Level.NONE);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder().addInterceptor(logging);

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
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

    public static void init(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
