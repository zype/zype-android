package com.zype.android.ui.Widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Evgeny Cherkasov on 17.06.2018
 */
public class CustomViewPager extends ViewPager {
    private boolean swipeEnabled = true;

    public CustomViewPager(Context context) {
        super(context);
    }
    public CustomViewPager(Context context, AttributeSet attrs){
        super(context,attrs);
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
}
