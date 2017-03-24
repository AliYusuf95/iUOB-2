package com.muqdd.iuob2.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

public class CourseModel extends BaseModel<CourseModel, CourseModel.ViewHolder> {

    // static variables to enhance performance
    private final static String pattern = "\\?.*?=(.*)?&.*?=(.*)?&.*?=(.*)?&.*?=(.*)?&.*?=(.*)?&.*?=(.*)?&.*?=(.*)?";
    private final static Pattern pHref = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);

    public String title;
    public String subTitle;
    public String prog;
    public String abv;
    public String inl;
    public String courseNumber;
    public String credits;
    public String year;
    public String semester;
    public String pre;

    public CourseModel(String href, String title, String pre) {
        this.title = title;
        this.subTitle = title.split("-")[0];
        this.pre = pre;
        Matcher mHref = pHref.matcher(href);
        if(mHref.find()) {
            this.prog = mHref.group(1);
            this.abv = mHref.group(2);
            this.inl = mHref.group(3);
            this.courseNumber = mHref.group(4);
            this.credits = mHref.group(5);
            this.year = mHref.group(6);
            this.semester = mHref.group(7);
        }
    }

    public String fileName() {
        return abv+courseNumber+"_"+year+"_"+semester+".html";
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.course_title;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.row_course;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(CourseModel.ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        viewHolder.title.setText(title);
        if (pre == null || "".equals(pre)) {
            viewHolder.pre.setVisibility(View.GONE);
        } else {
            viewHolder.pre.setText(pre);
            viewHolder.pre.setVisibility(View.VISIBLE);
        }
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(CourseModel.ViewHolder holder) {
        super.unbindView(holder);
        holder.title.setText("");
        holder.pre.setText("");
        holder.pre.setVisibility(View.GONE);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    static class ViewHolder extends RecyclerView.ViewHolder {
        protected @BindView(R.id.course_title) TextView title;
        protected @BindView(R.id.course_pre) TextView pre;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
