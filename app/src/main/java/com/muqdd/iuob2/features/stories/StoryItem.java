package com.muqdd.iuob2.features.stories;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.models.Story;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali Yusuf on 12/29/2017.
 * iUOB-2
 */

public class StoryItem extends AbstractItem<StoryItem, StoryItem.ViewHolder> {

    private Story story;

    public StoryItem(Story story) {
        this.story = story;
    }

    public static List<StoryItem> fromList(List<Story> dataList) {
        List<StoryItem> list = new ArrayList<>();
        for (Story data : dataList) {
            list.add(new StoryItem(data));
        }
        return list;
    }

    public Story getData() {
        return story;
    }

    @Override
    public int getType() {
        return R.id.lbl_title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.row_story;
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.title.setText("My Story");
        viewHolder.time.setText(story.getCreatedAtDuration());

        Glide.with(viewHolder.img.getContext())
                .load(story.getUrl())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_send_48dp)
                .into(new BitmapImageViewTarget(viewHolder.img) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(viewHolder.img.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        viewHolder.img.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    @Override
    public void unbindView(ViewHolder viewHolder) {
        super.unbindView(viewHolder);
        viewHolder.time.setText(null);
        viewHolder.title.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_story)
        protected ImageView img;
        @BindView(R.id.lbl_title)
        protected TextView title;
        @BindView(R.id.lbl_time)
        protected TextView time;

        @BindView(R.id.settings_layout)
        View settingsLayout;
        @BindView(R.id.main_content)
        View mainContent;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
