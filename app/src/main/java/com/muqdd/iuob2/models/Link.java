package com.muqdd.iuob2.models;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.app.BaseModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 3/12/2017.
 * iUOB-2
 */

public class Link extends BaseModel<Link, Link.ViewHolder> {

    public String title;
    public String url;

    public Link(String title, String url) {
        this.title = title;
        this.url = url;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.link_title;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.row_link;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(Link.ViewHolder holder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(holder, payloads);
        holder.title.setText(title);
        holder.url.setText(url);
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(Link.ViewHolder holder) {
        super.unbindView(holder);
        holder.title.setText(null);
        holder.url.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.link_title) TextView title;
        @BindView(R.id.link_url) TextView url;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
