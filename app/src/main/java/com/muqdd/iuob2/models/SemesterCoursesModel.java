package com.muqdd.iuob2.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.muqdd.iuob2.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

public class SemesterCoursesModel extends AbstractItem<SemesterCoursesModel, SemesterCoursesModel.ViewHolder> {

    public String title;
    private String inll;
    private String theabv;
    private String prog;
    public String year;
    public String semester;

    public SemesterCoursesModel(String title, String href) {
        this.title = title;
        Matcher m = Pattern.compile("\\?.*?=(.*)?&.*?=(.*)?&.*?=(.*)?&.*?=(.*)?&.*?=(.*)?$",Pattern.CASE_INSENSITIVE).matcher(href);
        if(m.find()) {
            this.inll = m.group(1);
            this.theabv = m.group(2);
            this.prog = m.group(3);
            this.year = m.group(4);
            this.semester = m.group(5);
        }
    }

    public SemesterCoursesModel(int year, int semester) {
        this.year = String.valueOf(year);
        this.semester = String.valueOf(semester);
    }

    public String fileName() {
        return "abrv_"+year+"_"+semester+".html";
    }

    public String semesterTitle()  {
        return year+"/"+semester;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.course_title;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.row_semester;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(SemesterCoursesModel.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        viewHolder.title.setText(title);
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(SemesterCoursesModel.ViewHolder holder) {
        super.unbindView(holder);
        holder.title.setText("");
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    static class ViewHolder extends RecyclerView.ViewHolder {
        protected @BindView(R.id.course_title) TextView title;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
