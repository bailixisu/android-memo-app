package com.cdh.bebetter.views;

import android.support.v4.view.ViewPager;

public class NoSwipeViewPager extends ViewPager {
    public NoSwipeViewPager(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent ev) {
        return false;
    }
}

