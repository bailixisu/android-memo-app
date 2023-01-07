package com.cdh.bebetter.activity;

import android.Manifest;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.adapter.MemoSortAdapter;
import com.cdh.bebetter.adapter.MyFragmentPagerAdapter;
import com.cdh.bebetter.dao.SortMemo;
import com.cdh.bebetter.fragment.FootFragment;
import com.cdh.bebetter.fragment.MemoFragment;
import com.cdh.bebetter.views.NoSwipeViewPager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MemoFragment.OnMemoFragmentListener {
    private Toolbar toolbar;
    private RadioGroup bottomBar;
    private RadioButton memoButton;
    private RadioButton footButton;
    private RadioButton userButton;
    private RadioButton myButton;
    private NoSwipeViewPager viewPager;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private FootFragment footFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        findByIdAndInit();
        memoButton.setChecked(true);
        requestPermission();
    }



    private void findByIdAndInit(){
        footFragment = myFragmentPagerAdapter.getFootFragment();
//        toolbar = findViewById(R.id.toolbar);
        bottomBar = findViewById(R.id.bottom_bar);
        memoButton = findViewById(R.id.rb_memo);
        footButton = findViewById(R.id.rb_foot);
        userButton = findViewById(R.id.rb_user);
        myButton = findViewById(R.id.rb_my);
        bottomBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                viewPager.setCurrentItem(
                        radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()))
                );
            }
        });

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setCurrentItem(Constant.MEMO_PAGE);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == 2) {
                    switch (viewPager.getCurrentItem()) {
                        case Constant.MEMO_PAGE:
                            bottomBar.check(memoButton.getId());
                            break;
                        case Constant.FOOT_PAGE:
                            bottomBar.check(footButton.getId());
                            footFragment.initData();
                            break;
                        case Constant.USER_PAGE:
                            bottomBar.check(userButton.getId());
                            break;
                        case Constant.MY_PAGE:
                            bottomBar.check(myButton.getId());
                            break;
                    }
                }
            }
        });
//        toolbar.setTitle("BeBetter");
    }

    //动态申请权限
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onBackGroundChange(int color) {
        Log.d("TAG", "onBackGroundChange: ");
        bottomBar.setBackgroundColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public void getMyLocation(Long memo_id) {
        try {
            footFragment.location(memo_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}