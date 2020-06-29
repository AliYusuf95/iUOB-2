package com.muqdd.iuob2.app;

import com.mikepenz.fastadapter.IClickable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Ali Yusuf on 3/24/2017.
 * iUOB-2
 */

@SuppressWarnings("unchecked")
public abstract class BaseModel<Item extends IItem & IClickable, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> {

}