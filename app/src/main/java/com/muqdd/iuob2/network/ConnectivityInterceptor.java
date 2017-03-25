package com.muqdd.iuob2.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */
@SuppressWarnings({"unused","WeakerAccess"})
public class ConnectivityInterceptor implements Interceptor {

    private ConnectivityManager connectivityManager;

    public ConnectivityInterceptor(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!isOnline(connectivityManager)) {
            throw new NoConnectivityException("No connectivity exception");
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

    private boolean isOnline(ConnectivityManager connectivityManager) {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public class NoConnectivityException extends IOException {

        private String message;

        public NoConnectivityException(String str) {
            message = str;
        }

        @Override
        public String getMessage() {return message;}

    }

}