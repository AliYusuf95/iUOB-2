package com.muqdd.iuob2.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseActivity.OnBackPressedListener;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */
@SuppressWarnings("unused")
public class BaseFragment extends Fragment {
    protected static final String TITLE = "TITLE";

    protected String title;
    protected Toolbar toolbar;
    protected TabLayout tabLayout;
    protected AppBarLayout.LayoutParams params;
    protected FirebaseAnalytics mFirebaseAnalytics;

    public BaseFragment() {
        // Required empty public constructor
    }

    public static BaseFragment newInstance(String title) {
        BaseFragment fragment = new BaseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // check in the parent activity is BaseActivity or not
        if (!(getActivity() instanceof BaseActivity)){
            throw new IllegalStateException("Parent activity must be BaseActivity");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // setup toolbar and tabLayout
        if (getArguments() != null && getArguments().containsKey(TITLE)) {
            title = getArguments().getString(TITLE);
            // send Analytic Tracker with fragment title
            getBaseActivity().sendAnalyticTracker(title);
        }
        if (getActivity() != null) {
            toolbar = getActivity().findViewById(R.id.toolbar);
            tabLayout = getActivity().findViewById(R.id.tabLayout);
        }
        tabLayout.setVisibility(View.GONE);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        // remove scroll flags from toolbar
        params.setScrollFlags(0);
        setHasOptionsMenu(false);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getBaseActivity());
        return null;
    }

    protected final BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @SuppressWarnings("ConstantConditions")
    protected Dialog infoDialog(String title, String message, String cancel){
        if (getContext() == null) return null;
        final Dialog dialog = new Dialog(getContext());
        // prepare dialog layout
        LayoutInflater inflater =
                (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_info, null);
        // set text
        ((TextView)dialogView.findViewById(R.id.title)).setText(title);
        ((TextView)dialogView.findViewById(R.id.message)).setText(message);
        // init cancel button
        Button cancelBtn = dialogView.findViewById(R.id.cancel);
        cancelBtn.setText(cancel);
        cancelBtn.setOnClickListener(view -> {
            if (dialog.isShowing())
                dialog.dismiss();
        });
        // set dialog view
        dialog.setContentView(dialogView);
        return dialog;
    }

    protected final void setToolbarScrollFlag() {
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
    }

    protected final void setOnBackPressedListener(@NonNull OnBackPressedListener listener) {
        getBaseActivity().setOnBackPressedListener(listener);
    }

    protected final void replaceFragment(@NonNull final Fragment fragment) {
        // Run in handler to avoid fragment manager transactions error
        new Handler().post(() -> getBaseActivity().replaceFragment(fragment));
    }

    protected final void displayFragment(@NonNull final Fragment fragment) {
        // Run in handler to avoid fragment manager transactions error
        new Handler().post(() -> getBaseActivity().displayFragment(fragment));
    }

    protected final void runOnUi(@NonNull Runnable runnable){
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().runOnUiThread(runnable);
        }
    }

}
