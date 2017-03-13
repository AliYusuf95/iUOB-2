package com.muqdd.iuob2.app;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.features.main.MainActivity;
import com.muqdd.iuob2.features.semester_schedule.CoursesFragment;
import com.muqdd.iuob2.models.SemesterCoursesModel;
import com.orhanobut.logger.Logger;

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
        if (getArguments().containsKey(TITLE))
            title = getArguments().getString(TITLE);
        toolbar = ButterKnife.findById(getActivity(), R.id.toolbar);
        tabLayout = ButterKnife.findById(getActivity(), R.id.tabLayout);
        tabLayout.setVisibility(View.GONE);
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // If nested fragments show back arrow
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0 ||
                getChildFragmentManager().getBackStackEntryCount() > 0) {
            setBackArrow(true);
        } else
            setBackArrow(false);
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

    protected void setBackArrow (boolean backArrow) {
        if (getActivity() instanceof BaseActivity){
            ((BaseActivity) getActivity()).setBackArrow(backArrow);
        }
    }

    public static boolean handleBackPressed(FragmentManager fm) {
        if(fm.getFragments() != null){
            for(Fragment frag : fm.getFragments()){
                if(frag != null && frag.isVisible() && frag instanceof BaseFragment){
                    if(((BaseFragment)frag).onBackPressed()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean onBackPressed() {
        FragmentManager fm = getChildFragmentManager();
        if(handleBackPressed(fm)){
            return true;
        }
        else if(getUserVisibleHint() && fm.getBackStackEntryCount() > 0){
            fm.popBackStack();
            return true;
        }
        return false;
    }
}
