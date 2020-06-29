package com.muqdd.iuob2.features.account;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.Auth;
import com.muqdd.iuob2.app.AuthCallBack;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.app.Constants;
import com.muqdd.iuob2.features.main.Menu;
import com.muqdd.iuob2.models.ApiResponse;
import com.muqdd.iuob2.models.User;
import com.muqdd.iuob2.network.IUOBApi;
import com.muqdd.iuob2.network.ServiceGenerator;
import com.muqdd.iuob2.utils.TextUtils;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Response;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class AccountFragment extends BaseFragment {

    @BindView(R.id.main_content)
    protected SwipeRefreshLayout mainContent;

    @BindView(R.id.txt_name)
    protected EditText txtName;

    @BindView(R.id.txt_email)
    protected EditText txtEmail;

    @BindView(R.id.txt_sid)
    protected EditText txtSid;

    @BindView(R.id.spinner_gender)
    protected MaterialSpinner spinnerGender;

    @BindView(R.id.txt_birthday)
    protected EditText txtBirthday;

    @BindView(R.id.spinner_college)
    protected MaterialSpinner spinnerCollege;

    @BindView(R.id.btn_update)
    protected Button btnUpdate;

    private View mView;
    private Date dob;
    private int mYear;
    private int mMonth;
    private int mDay;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, Menu.ACCOUNT.toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_account, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.account_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                Auth.logout(getContext());
                getBaseActivity().replaceFragment(LoginFragment.newInstance());
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
    }

    private void initiate() {
        List<String> genderList = Arrays.asList(getString(R.string.male), getString(R.string.female));
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

        mainContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        if (!Auth.isLoggedIn(getContext())){
            replaceFragment(LoginFragment.newInstance());
        } else {
            getData();
        }
    }

    private void getData() {
        mainContent.setRefreshing(true);

        User currentUser = Auth.getUserData(getContext());
        ServiceGenerator.createService(IUOBApi.class, currentUser).getUser(currentUser.getId())
                .enqueue(new AuthCallBack<ApiResponse>() {
                    @Override
                    public void onResponse(Response<ApiResponse> response) {
                        if (response.isSuccessful() && !isEmptyResponse()) {
                            User user = response.body().getUser();
                            if (user != null) {
                                Auth.login(getContext(), user);
                                setData(user);
                            }
                        }
                    }

                    @Override
                    public void onFinish() {
                        mainContent.setRefreshing(false);
                    }
                });
    }

    private void setData(User user) {
        if (getActivity() == null || user == null) return;
        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        txtSid.setText(user.getStudentID());
        if (TextUtils.hasText(user.getDateOfBirth())) {
            try {
                SimpleDateFormat defaultDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                Date day = defaultDateFormat.parse(user.getDateOfBirth());
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                txtBirthday.setText(dateFormat.format(day));
                Calendar c = Calendar.getInstance();
                c.setTime(day);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                Logger.e("cant parse date of birth");
            }
        }

        if (TextUtils.hasText(user.getGender())) {
            if (user.getGender().trim().equalsIgnoreCase("M") ||
                    user.getGender().toLowerCase().equals(getString(R.string.male))) {
                spinnerGender.setSelection(1);
            } else {
                spinnerGender.setSelection(2);
            }
        } else {
            spinnerGender.setSelection(0);
        }

        if (TextUtils.hasText(user.getCollege())) {
            int index = Constants.collegesNameList.indexOf(user.getCollege().trim());
            if (index > -1) {
                spinnerCollege.setSelection(index+1);
            }
        } else {
            spinnerCollege.setSelection(0);
        }
    }

    @OnClick(R.id.btn_update)
    void update() {
        if (!TextUtils.hasText(txtName) || !TextUtils.isEmailValid(txtEmail, true) ||
                !TextUtils.hasText(txtSid)) {
            return;
        }

        btnUpdate.setEnabled(false);

        Map<String, String> options = new HashMap<>();
        options.put("name", txtName.getText().toString().trim());
        options.put("email", txtEmail.getText().toString().trim());
        options.put("studentID", txtSid.getText().toString().trim());
        String gender = spinnerGender.getSelectedItem() == null ?
                "" : String.valueOf(spinnerGender.getSelectedItem()).trim();
        options.put("gender", gender.equals(getString(R.string.male)) ? "M" :
                gender.equals(getString(R.string.female)) ? "F" : "");

        if (dob != null) {
            SimpleDateFormat defaultDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            options.put("dateOfBirth", defaultDateFormat.format(dob));
        }

        options.put("college", spinnerCollege.getSelectedItem() == null ?
                "" : String.valueOf(spinnerCollege.getSelectedItem()).trim());

        ServiceGenerator.createService(IUOBApi.class, Auth.getUserData(getContext()))
                .updateUserProfile(options).enqueue(new AuthCallBack<Auth>() {
            @Override
            public void onResponse(Response<Auth> response) {
                if (getContext() == null) return;
                if (response.isSuccessful() && !isEmptyResponse()) {
                    infoDialog("Success", "Profile updated successfully! OMG", "Close").show();
                } else {
                    infoDialog("Oops!", "Cant perform the request please try again later", "Cancel").show();
                }
            }

            @Override
            public void onFinish() {
                btnUpdate.setEnabled(true);
            }
        });
    }

}
