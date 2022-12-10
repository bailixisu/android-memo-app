package com.cdh.bebetter.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.adapter.EditSortItemAdapter;
import com.cdh.bebetter.adapter.MemoSortDatabaseAdapter;
import com.cdh.bebetter.dao.SortMemo;

import java.util.List;

public class SortEditActivity extends AppCompatActivity implements EditSortItemAdapter.OnSortItemClickListener {

    MemoSortDatabaseAdapter memoSortDatabaseAdapter;
    DatabaseAdapter databaseAdapter;
    List<SortMemo> sortMemoList;
    RecyclerView memoSortList;
    PopupWindow addSortPopupWindow;
    private String TAG = "SortEditActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_edit);
        initFindById();
    }

    public void initFindById(){
        ImageView imageView = findViewById(R.id.backIconSortEdit);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        memoSortList = findViewById(R.id.editSortList);
        TextView addMemoSort = findViewById(R.id.addMemoSort);
        addMemoSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initAddSortPopWindow("",Constant.COLOR_1);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }


    private void initData(){
        memoSortDatabaseAdapter = new MemoSortDatabaseAdapter(this);
        databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.open();
        memoSortDatabaseAdapter.open();
        sortMemoList = memoSortDatabaseAdapter.memoFindAllRecords();
        EditSortItemAdapter editSortItemAdapter = new EditSortItemAdapter(this,sortMemoList);
        memoSortList.setLayoutManager(new LinearLayoutManager(this));
        memoSortList.setAdapter(editSortItemAdapter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        memoSortDatabaseAdapter.close();
        databaseAdapter.close();
    }

    @Override
    public void onItemClick(String sortText, int color) {
        initAddSortPopWindow(sortText,color);
    }

    @Override
    public void deleteIconClick(int i) {
        memoSortDatabaseAdapter.memoDelete(sortMemoList.get(i));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除该分类会将该分类下的所有待办删除，是否继续？");
        builder.setPositiveButton("确定", (dialogInterface, i1) -> {
            memoSortDatabaseAdapter.memoDelete(sortMemoList.get(i));
            databaseAdapter.memoDeleteBySort(sortMemoList.get(i).getSortText());
            initData();
        });
        builder.setNegativeButton("取消", (dialogInterface, i1) -> {

        });
        builder.create().show();
    }

    private void initAddSortPopWindow(String sortText, int color){
        View view = LayoutInflater.from(this).inflate(R.layout.add_memo_sort_popwindow,null);
        SortMemo sortMemo = new SortMemo();
        sortMemo.setSortIconColor(Constant.COLOR_1);
        sortMemo.setSortBackgroundColor(Constant.COLOR_BACKGROUND_COLOR_1);
        addSortPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        addSortPopupWindow.setOutsideTouchable(true);
        addSortPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        setBackgroundAlpha(0.8f);
        addSortPopupWindow.setAnimationStyle(R.style.pw_bottom_anim_style);
        addSortPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//        addSortPopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        addSortPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        addSortPopupWindow.showAtLocation(view, Gravity.BOTTOM,0,826);
        addSortPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
        EditText addSortEdit = view.findViewById(R.id.sortName);
        ImageView addMemoSortIcon = view.findViewById(R.id.addMemoSortIcon);
        addMemoSortIcon.setColorFilter(Constant.COLOR_1);
        addSortEdit.setFocusable(true);
        addSortEdit.setFocusableInTouchMode(true);
        addSortEdit.setText(sortText);
        addSortEdit.setSelection(sortText.length());
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        TextView addMemoSortCancel = view.findViewById(R.id.addMemoSortCancel);
        addMemoSortCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                addSortPopupWindow.dismiss();
            }
        });
        TextView addMemoSortSave = view.findViewById(R.id.addMemoSortSave);
        addMemoSortSave.setAlpha(0.6f);
        addMemoSortSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getAlpha() == 1.0f){
                    try {
                        Log.d(TAG, "onClick: "+sortMemo.toString());
                        memoSortDatabaseAdapter.memoSortInsert(sortMemo);
                        imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                        addSortPopupWindow.dismiss();
                        initData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//        addSortEdit.requestFocus();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        new Handler().postDelayed(new Runnable(){
            public void run() {
                addSortEdit.requestFocus();
                imm.showSoftInput(addSortEdit, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
        RadioGroup memoSortEditPopWindowColorRadioGroup = view.findViewById(R.id.memoSortEditPopWindowColorRadioGroup);
        memoSortEditPopWindowColorRadioGroup.check(R.id.memoSortEditPopWindowRadioButton1);
        memoSortEditPopWindowColorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int index = 0;
                switch (i){
                    case R.id.memoSortEditPopWindowRadioButton1:
                        index = 0;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton2:
                        index = 1;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton3:
                        index = 2;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton4:
                        index = 3;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton5:
                        index = 4;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton6:
                        index = 5;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton7:
                        index = 6;
                        break;
                    case R.id.memoSortEditPopWindowRadioButton8:
                        index = 7;
                        break;
                }
                sortMemo.setSortBackgroundColor(Constant.COLORS_BACKGROUND[index]);
                sortMemo.setSortIconColor(Constant.COLORS[index]);
                addMemoSortIcon.setColorFilter(Constant.COLORS[index]);
            }
        });
        addSortEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addMemoSortSave.setAlpha(editable.toString().length() > 0?1.0f:0.6f);
                sortMemo.setSortText(editable.toString());
            }
        });
    }
    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }
}