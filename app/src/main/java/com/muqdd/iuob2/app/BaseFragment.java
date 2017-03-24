package com.muqdd.iuob2.app;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.features.main.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class BaseFragment extends Fragment {
    protected static final String TITLE = "TITLE";

    protected String title;
    protected Toolbar toolbar;
    protected TabLayout tabLayout;
    protected AppBarLayout.LayoutParams params;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // setup toolbar and tabLayout
        if (getArguments() != null && getArguments().containsKey(TITLE))
            title = getArguments().getString(TITLE);
        toolbar = ButterKnife.findById(getActivity(), R.id.toolbar);
        tabLayout = ButterKnife.findById(getActivity(), R.id.tabLayout);
        tabLayout.setVisibility(View.GONE);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        // Setup scroll flags
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        return null;
    }

    protected String readHTMLDataToCache(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(new File(getActivity().getCacheDir(), fileName));
        InputStreamReader isr = new InputStreamReader (fis) ;
        BufferedReader buffReader = new BufferedReader (isr) ;
        StringBuilder data = new StringBuilder("");
        String readString = buffReader.readLine();
        while (readString != null) {
            data.append(readString);
            readString = buffReader.readLine();
        }
        isr.close() ;

        return data.toString();
    }

    protected void writeHTMLDataToCache(String fileName, byte[] data) throws IOException {
        File file = new File(getActivity().getCacheDir(), fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
    }

    protected void displayFragment(Fragment fragment){
        if (getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).displayFragment(fragment);
        }
    }

}
