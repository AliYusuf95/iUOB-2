package com.muqdd.iuob2.features.account;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.Auth;
import com.muqdd.iuob2.app.AuthCallBack;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.ApiResponse;
import com.muqdd.iuob2.network.IUOBApi;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.utils.TextUtils;
import com.muqdd.iuob2.utils.Utils;
import com.muqdd.iuob2.vendor.photoEditSDK.UtilFunctions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class LoginFragment extends BaseFragment {

    @BindView(R.id.main_content)
    protected LinearLayout mainContent;

    @BindView(R.id.txt_email)
    protected EditText txtEmail;

    @BindView(R.id.txt_password)
    protected EditText txtPassword;

    @BindView(R.id.btn_login)
    protected Button btnLogin;

    private View mView;

    private String ok;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.LOGIN.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_login, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
    }

    private void initiate() {
        if (Auth.isLoggedIn(getContext())) {
            replaceFragment(AccountFragment.newInstance());
        }
    }

    @OnClick(R.id.btn_login)
    void login() {
        if (!TextUtils.isEmailValid(txtEmail, true) || !TextUtils.hasText(txtPassword)) {
            return;
        }
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, new Bundle());
        btnLogin.setEnabled(false);
        Utils.hideKeyboardFrom(getBaseActivity());
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        ServiceGenerator.createService(IUOBApi.class).login(email, password)
                .enqueue(new AuthCallBack<ApiResponse>() {
                    @Override
                    public void onResponse(Response<ApiResponse> response) {
                        if (response.isSuccessful() && !isEmptyResponse()){
                            Auth.login(getContext(), response.body().getUser());
                            replaceFragment(AccountFragment.newInstance());
                        } else if (getContext() != null){
                            try {
                                ApiResponse apiResponses = new Gson().fromJson(
                                        response.errorBody().string(),
                                        new TypeToken<ApiResponse>() {}.getType()
                                );
                                infoDialog("Oops!", apiResponses.getMsg(), "Close").show();
                            } catch (Exception e){
                                infoDialog("Oops!", "Cant perform the request please try again later", "Cancel").show();
                            }
                        }
                    }

                    @Override
                    public void onFinish() {
                        btnLogin.setEnabled(true);
                    }
                });
    }

    @OnClick(R.id.lbl_signup)
    void signup() {
        replaceFragment(SignupFragment.newInstance());
    }
}
