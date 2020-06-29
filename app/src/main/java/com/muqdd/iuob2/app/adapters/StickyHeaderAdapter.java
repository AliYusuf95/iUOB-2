package com.muqdd.iuob2.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.muqdd.iuob2.R;
import com.muqdd.iuob2.features.schedule_builder.BSection;
import com.orhanobut.logger.Logger;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Ali Yusuf on 6/5/2017.
 * iUOB-2
 */

public class StickyHeaderAdapter extends AbstractAdapter implements StickyRecyclerHeadersAdapter {
    @Override
    public long getHeaderId(int position) {
        IItem item = getItem(position);
        if (item instanceof BSection) {
            return (long) ((BSection) item).getHeaderId();
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        //we create the view for the header
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_sticky_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;

        IItem item = getItem(position);
        if (item instanceof BSection) {
            textView.setText(String.format(Locale.getDefault(), "%s", ((BSection) item).getCourseId()));
        }
    }

    /**
     * REQUIRED FOR THE FastAdapter. Set order to < 0 to tell the FastAdapter he can ignore this one.
     **/
    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public List<IItem> getAdapterItems() {
        return null;
    }

    @Override
    public IItem getAdapterItem(int position) {
        return null;
    }

    @Override
    public int getAdapterPosition(IItem item) {
        return -1;
    }

    @Override
    public int getAdapterPosition(long identifier) {
        return -1;
    }

    @Override
    public int getGlobalPosition(int position) {
        return -1;
    }
}
