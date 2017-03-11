package com.muqdd.iuob2.app;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.features.main.MainActivity;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class BaseFragment extends Fragment {

    protected Toolbar toolbar;
    protected TabLayout tabLayout;

    public BaseFragment() {
        // Required empty public constructor
    }

    public static BaseFragment newInstance() {
        return new BaseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // setup toolbar and tabLayout
        toolbar = ButterKnife.findById(getActivity(), R.id.toolbar);
        tabLayout = ButterKnife.findById(getActivity(), R.id.tabLayout);
        tabLayout.setVisibility(View.GONE);
        // If nested fragments show back arrow
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            setBackArrow(true);
        }
        return null;
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
