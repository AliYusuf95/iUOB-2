package com.muqdd.iuob2.app;

/**
 * Created by Ali Yusuf on 10/20/2017.
 * iUOB-2
 */

import android.app.Activity;
import android.support.annotation.NonNull;

import com.muqdd.iuob2.features.account.LoginFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is an Authenticated Callback {@link Callback} to check whenever the response of
 * response is valid or not.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AuthCallBack<T> implements Callback<T> {

    private BaseActivity mActivity;
    private Response<T> response;

    public AuthCallBack(BaseActivity activity) {
        this.mActivity = activity;
    }

    public AuthCallBack() {}

    /**
     * Check if response has 401 then the passed mActivity will
     * finished {@link Activity#finish()} and login Activity will be started
     */
    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        this.response = response;
        this.onStart();
        if (mActivity != null && !mActivity.isFinishing() && !mActivity.isDestroyed() && response.code() == 401){
            Auth.logout(mActivity);
            mActivity.replaceFragment(LoginFragment.newInstance());
        } else {
            this.onResponse(response);
        }
        this.onFinish();
    }

    /**
     * {@link Callback#onFailure(Call, Throwable)}
     */
    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        this.onStart();
        this.onFailure(t);
        this.onFinish();
    }

    /**
     * Invoked for a received HTTP response.
     * {@link Callback#onResponse(Call, Response)}
     */
    abstract public void onResponse(Response<T> response);

    /**
     * Invoked when a network exception occurred.
     * {@link Callback#onFailure(Call, Throwable)}
     */
    public void onFailure(Throwable t){}

    /**
     * Invoked whenever there is response or failure in request
     */
    public void onStart() {}

    /**
     * Invoked whenever there is response or failure in request
     */
    public void onFinish() {}

    /**
     * Check if response object has a specific value of statusCode.
     * @param response response object
     * @param statusCode status code wanted
     * @return true if statusCode equal response statusCode; false otherwise
     */
    public static <T> boolean checkStatusCode(Response<T> response, int statusCode) {
        return response.code() == statusCode;
    }

    public boolean checkStatusCode(int statusCode) {
        return AuthCallBack.checkStatusCode(response, statusCode);
    }

    public T getBody() {
        return response.body();
    }

    public static <T> boolean isEmptyResponse(Response<T> response) {
        try {
            T restResponse = response.body();
            if (restResponse != null) {
                restResponse.getClass();
            } else {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public boolean isEmptyResponse() {
        return response == null || AuthCallBack.isEmptyResponse(response);
    }
}