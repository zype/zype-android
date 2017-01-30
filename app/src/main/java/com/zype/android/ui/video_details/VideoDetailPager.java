package com.zype.android.ui.video_details;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VideoDetailPager extends ViewPager {

    public VideoDetailPager(Context context) {
        super(context);
    }

    public VideoDetailPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }
}