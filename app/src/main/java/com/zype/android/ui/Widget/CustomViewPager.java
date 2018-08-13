package com.zype.android.ui.Widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Evgeny Cherkasov on 17.06.2018
 */
public class CustomViewPager extends ViewPager {
    private CustomScroller scroller = null;
    private boolean swipeEnabled = true;

    public CustomViewPager(Context context) {
        super(context);
        init();
    }

    public CustomViewPager(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }

    private void init() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field fieldScroller = viewpager.getDeclaredField("mScroller");
            fieldScroller.setAccessible(true);
            scroller = new CustomScroller(getContext(), new DecelerateInterpolator());
            fieldScroller.set(this, scroller);
        }
        catch (Exception ignored) {
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onTouchEvent(event);
    }

    public void setSwipeEnabled(boolean enabled){
        this.swipeEnabled = enabled;
    }

    /*
     * Set the factor by which the duration will change
     */
    public void setScrollDuration(int duration) {
        scroller.setScrollDuration(duration);
    }

    private class CustomScroller extends Scroller {

        private int duration = 500;

        public CustomScroller(Context context) {
            super(context);
        }

        public CustomScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public CustomScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, this.duration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, duration);
        }

        public void setScrollDuration(int duration) {
            this.duration = duration;
        }
    }

}
