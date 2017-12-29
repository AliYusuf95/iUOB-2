package com.muqdd.iuob2.features.account;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.Auth;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.app.Constants;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.ApiResponse;
import com.muqdd.iuob2.network.ConnectivityInterceptor;
import com.muqdd.iuob2.network.IUOBApi;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.utils.TextUtils;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class SignupFragment extends BaseFragment {

    @BindView(R.id.main_content)
    protected LinearLayout mainContent;

    @BindView(R.id.txt_name)
    protected EditText txtName;

    @BindView(R.id.txt_email)
    protected EditText txtEmail;

    @BindView(R.id.txt_sid)
    protected EditText txtSid;

    @BindView(R.id.txt_password)
    protected EditText txtPassword;

    @BindView(R.id.spinner_gender)
    protected MaterialSpinner spinnerGender;

    @BindView(R.id.txt_birthday)
    protected EditText txtBirthday;

    @BindView(R.id.spinner_college)
    protected MaterialSpinner spinnerCollege;

    private View mView;
    private Date dob;
    private int mYear;
    private int mMonth;
    private int mDay;

    public SignupFragment() {
        // Required empty public constructor
    }

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.SIGN_UP.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_signup, container, false);
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
        String[] genderList = {getString(R.string.male), getString(R.string.female)};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> collegeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Constants.collegesNameList);
        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCollege.setAdapter(collegeAdapter);
        spinnerCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mYear = year;
                        mMonth = month;
                        mDay = day;
                        txtBirthday.setText(year+"-"+(month+1)+"-"+day);

                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, mYear);
                        c.set(Calendar.MONTH, mMonth);
                        c.set(Calendar.DAY_OF_MONTH, mDay);
                        dob = c.getTime();
                    }
                }, mYear, mMonth, mDay);
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });
    }

    @OnClick(R.id.btn_signup)
    void signup() {
        if (!TextUtils.hasText(txtName) || !TextUtils.isEmailValid(txtEmail, true) ||
                !TextUtils.hasText(txtSid) || !TextUtils.hasText(txtPassword)) {
            return;
        }
        String name = txtName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String sid = txtSid.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem() == null ?
                "" : String.valueOf(spinnerGender.getSelectedItem()).trim();
        gender = gender.equals(getString(R.string.male)) ? "M" :
                gender.equals(getString(R.string.female)) ? "F" : "";
        String birthday = txtBirthday.getText().toString().trim();
        if (dob != null) {
            SimpleDateFormat defaultDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            birthday = defaultDateFormat.format(dob);
        }
        String college = spinnerCollege.getSelectedItem() == null ?
                "" : String.valueOf(spinnerCollege.getSelectedItem()).trim();
        ServiceGenerator.createService(IUOBApi.class).signup(
                name, email, password, sid, gender, birthday, college
        ).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    Auth.login(getContext(), response.body().getUser());
                    replaceFragment(AccountFragment.newInstance());
                } else {
                    try {
                        List<ApiResponse> apiResponses = new Gson().fromJson(
                                response.errorBody().string(),
                                new TypeToken<List<ApiResponse>>() {}.getType()
                        );
                        infoDialog("Oops!", apiResponses.get(0).getMsg(), "Close").show();
                    } catch (Exception e){
                        infoDialog("Oops!", "Cant perform the request please try again later", "Cancel").show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Dialog dialog = t instanceof ConnectivityInterceptor.NoConnectivityException ?
                        infoDialog("Error", "The Internet Connection appears to be offline.", "close") :
                        infoDialog("Sorry", "Cant perform the request please try again later.", "close");
                dialog.show();
                infoDialog("Oops!", "", "Cancel");
            }
        });
    }

    @OnClick(R.id.lbl_login)
    void login() {
        replaceFragment(LoginFragment.newInstance());
    }
}
