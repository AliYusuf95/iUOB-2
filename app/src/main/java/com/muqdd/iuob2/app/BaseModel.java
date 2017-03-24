package com.muqdd.iuob2.app;

import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.mikepenz.fastadapter.IClickable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

/**
 * Created by Ali Yusuf on 3/24/2017.
 * iUOB-2
 */

public abstract class BaseModel<Item extends IItem & IClickable, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> {

    /**
     * Override this variable from {@link AbstractItem} to make {@link Gson}
     * works perfectly.
     */
    private transient ViewHolderFactory<? extends VH> mFactory;

    /**
     * Override this method to use {@link #mFactory}
     */
    @Override
    public Item withFactory(ViewHolderFactory<? extends VH> factory) {
        this.mFactory = factory;
        return (Item) this;
    }

    /**
     * Override this method to use {@link #mFactory}
     */
    @Override
    public ViewHolderFactory<? extends VH> getFactory() {
        if (mFactory == null) {
            try {
                this.mFactory = new ReflectionBasedViewHolderFactory<>(viewHolderType());
            } catch (Exception e) {
                throw new RuntimeException("please set a ViewHolderFactory");
            }
        }
        return mFactory;
    }

}