package com.muqdd.iuob2.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 9/8/2017.
 * iUOB-2
 */

public class CoursePrefix extends BaseModel<CoursePrefix, CoursePrefix.ViewHolder> {

    private int year;
    private int semester;
    private String prefix;

    public CoursePrefix(String prefix, int year, int semester) {
        this.prefix = prefix;
        this.year = year;
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public static List<CoursePrefix> createList(List<String> prefixList, int year, int semester) {
        List<CoursePrefix> list = new ArrayList<>();
        for (String prefix : prefixList) {
            list.add(new CoursePrefix(prefix, year, semester));
        }
        return list;
    }

    @Override
    public int getType() {
        return R.id.course_title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.row_semester;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.title.setText(prefix);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.title.setText("");
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        protected @BindView(R.id.course_title)
        TextView title;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
