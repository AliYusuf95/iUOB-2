package com.muqdd.iuob2.features.schedule_builder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;
import com.muqdd.iuob2.models.SectionTimeModel;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 6/7/2017.
 * iUOB-2
 */

@SuppressWarnings({"unused","WeakerAccess"})
public class BScheduleModel extends BaseModel<BScheduleModel, BScheduleModel.ViewHolder> {

    private final static int clockEmoji = 0x1F552;
    private final static int teacherEmoji = 0x1F468;

    public List<BSectionModel> sections;

    public BScheduleModel(List<BSectionModel> sections) {
        this.sections = sections;
    }

    private String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private String getTimeBrief(List<SectionTimeModel> times){
        String str = "";
        for (SectionTimeModel time : times){
            str += time.days+time.from+" ";
        }
        return str;
    }

    @Override
    public int getType() {
        return R.id.lbl_number;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.row_schedule;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.lblNumber.setText(String.valueOf(viewHolder.getAdapterPosition()+1));
        String desc = "";
        for (BSectionModel section : sections) {
            desc += String.format(Locale.getDefault(), "[%s] %s %s %s %s %s\n", section.sectionNumber ,
                    section.parentCourse.courseName+section.parentCourse.courseNumber,
                    getEmojiByUnicode(teacherEmoji), section.doctor, getEmojiByUnicode(clockEmoji),
                    getTimeBrief(section.times));
        }
        desc = desc.substring(0, desc.length() - 2); // remove lase \n
        viewHolder.lblDesc.setText(desc);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lbl_number) TextView lblNumber;
        @BindView(R.id.lbl_desc) TextView lblDesc;
        @BindView(R.id.img_info) ImageView imgInfo;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
