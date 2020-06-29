package com.muqdd.iuob2.views;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Ali Yusuf on 12/30/2017.
 * iUOB-2
 */

public class MultiTouchDetectLayout extends LinearLayout {

    private OnMultiTouchEvent multiTouchEvent;
    private int multiTouchCount;

    public MultiTouchDetectLayout(Context context) {
        super(context);
    }

    public MultiTouchDetectLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiTouchDetectLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultiTouchDetectLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount >= multiTouchCount && multiTouchEvent != null) {
            multiTouchEvent.touchEvent(event, pointerCount);
        }
        return super.onTouchEvent(event);
    }

    public void setMultiTouchEvent(int multiTouchCount, OnMultiTouchEvent multiTouchEvent) {
        this.multiTouchCount = multiTouchCount;
        this.multiTouchEvent = multiTouchEvent;
    }

    public interface OnMultiTouchEvent {
        void touchEvent(MotionEvent event, int count);
    }
}
