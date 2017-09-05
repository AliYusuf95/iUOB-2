package com.muqdd.iuob2.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Ali Yusuf on 9/3/2017.
 * iUOB-2
 */

@SuppressWarnings({"WeakerAccess","unused"})
public class AuthenticationInterceptor implements Interceptor {

    private String authName;
    private String authValue;

    public AuthenticationInterceptor(String authValue) {
        this.authName = "Authorization";
        this.authValue = authValue;
    }

    public AuthenticationInterceptor(String authName, String authValue) {
        this.authName = authName;
        this.authValue = authValue;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();

        requestBuilder.header(authName, authValue);

        // Accept only json response
        requestBuilder.header("Accept", "application/json");

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
