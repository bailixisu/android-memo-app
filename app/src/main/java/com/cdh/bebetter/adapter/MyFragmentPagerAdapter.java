package com.cdh.bebetter.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.fragment.FootFragment;
import com.cdh.bebetter.fragment.MemoFragment;
import com.cdh.bebetter.fragment.MyFragment;
import com.cdh.bebetter.fragment.UserFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 4;
    private MemoFragment memoFragment = null;
    private FootFragment footFragment = null;
    private MyFragment myFragment = null;
    private UserFragment userFragment = null;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        memoFragment = new MemoFragment();
        footFragment = new FootFragment();
        myFragment = new MyFragment();
        userFragment = new UserFragment();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
            case Constant.MEMO_PAGE:
                fragment = memoFragment;
                break;
            case Constant.FOOT_PAGE:
                fragment = footFragment;
                break;
            case Constant.USER_PAGE:
                fragment = userFragment;
                break;
            case Constant.MY_PAGE:
                fragment = myFragment;
                break;
        }
        return fragment;
    }
}
