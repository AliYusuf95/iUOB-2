package com.muqdd.iuob2.models;

import android.support.v7.widget.RecyclerView;
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

public class LinkModel extends BaseModel<LinkModel, LinkModel.ViewHolder> {

    public String title;
    public String url;

    public LinkModel(String title, String url) {
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
    public void bindView(LinkModel.ViewHolder holder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(holder, payloads);
        holder.title.setText(title);
        holder.url.setText(url);
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(LinkModel.ViewHolder holder) {
        super.unbindView(holder);
        holder.title.setText(null);
        holder.url.setText(null);
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
