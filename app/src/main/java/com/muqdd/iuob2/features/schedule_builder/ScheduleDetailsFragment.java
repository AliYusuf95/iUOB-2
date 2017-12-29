package com.muqdd.iuob2.features.schedule_builder;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseFragment;
import com.muqdd.iuob2.models.Timing;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/11/2017.
 * iUOB-2
 */

public class ScheduleDetailsFragment extends BaseFragment {

    private final static String SCHEDULE = "SCHEDULE";
    private static final int MENU_SHARE = Menu.FIRST;
    private static final int MENU_COPY = Menu.FIRST +1;

    @BindView(R.id.main_content) LinearLayout mainContent;
    @BindView(R.id.lbl_details) TextView lblDetails;

    private View mView;
    private String summaryText;
    private BSchedule mSchedule;

    public ScheduleDetailsFragment() {
        // Required empty public constructor
    }

    public static ScheduleDetailsFragment newInstance(String title, BSchedule schedule) {
        ScheduleDetailsFragment fragment = new ScheduleDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(SCHEDULE, new Gson().toJson(schedule, BSchedule.class));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (mView == null) {
            // Inflate the layout for this fragment
            mView = inflater.inflate(R.layout.fragment_schedule_builder_details, container, false);
            ButterKnife.bind(this, mView);
            initiate();
        }
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, MENU_SHARE, Menu.NONE, R.string.share).setIcon(R.drawable.ic_share_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(1, MENU_COPY, Menu.NONE, R.string.copy).setIcon(R.drawable.ic_content_copy_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case MENU_SHARE:
                if (summaryText == null){
                    summaryText = "";
                }
                String title = "Share Schedule Details";
                //create the sharing intent
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, summaryText);
                startActivity(Intent.createChooser(sharingIntent, title));
                return true;
            case MENU_COPY:
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("iUOB2", summaryText);
                clipboard.setPrimaryClip(clip);
                Snackbar.make(mainContent,"Schedule details copied",Snackbar.LENGTH_LONG).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        // check primary data
        checkPrimaryData();
    }

    private void checkPrimaryData() {
        if (mSchedule == null && getContext() != null) {
            mSchedule = new BSchedule(new ArrayList<BSection>());
            Dialog dialog = infoDialog("Sorry","Some thing goes wrong pleas try again later.", "Cancel");
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    getActivity().onBackPressed();
                }
            });
            dialog.show();
        }
    }

    private void initiate() {
        // initialize variables
        mSchedule = new Gson().fromJson(getArguments().getString(SCHEDULE), BSchedule.class);
        checkPrimaryData();
        summaryText = "";
        for (BSection section : mSchedule.sections) {
            Locale locale = Locale.getDefault();
            summaryText += String.format(locale, "Course: %s\n", section.getCourseId());
            summaryText += String.format(locale, "Final Exam: %s\n", section.getFinalExam());
            String timeStr = "";
            for (Timing time : section.getTimingLegacy()){
                timeStr += String.format(locale, "%s [%s] in [%s]\n", time.getDay(), time.getDuration(), time.getLocation());
            }
            summaryText += String.format(locale, "Time: %s", timeStr);
            summaryText += "-------------------------------\n";
        }

        lblDetails.setText(summaryText);
        lblDetails.setMovementMethod(new ScrollingMovementMethod());
    }
}
