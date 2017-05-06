package com.muqdd.iuob2.app;

import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.IClickable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Created by Ali Yusuf on 3/24/2017.
 * iUOB-2
 */

@SuppressWarnings("unchecked")
public abstract class BaseModel<Item extends IItem & IClickable, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> {

}