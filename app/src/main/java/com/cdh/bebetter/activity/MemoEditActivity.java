package com.cdh.bebetter.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.adapter.MemoSortAdapter;
import com.cdh.bebetter.adapter.MemoSortDatabaseAdapter;
import com.cdh.bebetter.dao.Memo;
import com.cdh.bebetter.dao.SortMemo;
import com.cdh.bebetter.dialog.TimePickerDialog;
import com.cdh.bebetter.provider.MyAlarmReceiver;
import com.cdh.bebetter.views.FontIconView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemoEditActivity extends AppCompatActivity
        implements TimePickerDialog.NoticeDialogListener,MemoSortAdapter.OnSortItemClickListener {
    private String TAG = "MemoEditActivity";
    TimePickerDialog startTimePickerDialog;
    TimePickerDialog deadlinePickerDialog;
    EditText contentEdit;
    TextView startTimeEdit;
    TextView deadlineEdit;
    TextView noteEdit;
    TextView sortEdit;
    FontIconView saveMemoIcon;
    DatabaseAdapter databaseAdapter;
    MemoSortDatabaseAdapter memoSortDatabaseAdapter;
    RecyclerView sortMemoRecyclerView;
    TextView memoItemSort;
    CardView memoEditSortTitleCardView;
    Boolean isAdd = false;
    private PopupWindow popupWindow;
    private PopupWindow addSortPopupWindow;
    private static final String startTimeID = "startTime";
    private static final String deadlineID = "deadline";
    private Memo memo;
    private String sortText;
    private int sortBackgroundColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);
        sortText = Constant.NOT_SORT_MEMO_STRING;
        memo = (Memo) getIntent().getSerializableExtra("memo");
        isAdd = false;
        if(memo == null){
            memo = new Memo();
//            memo.setSort("未分类");
            isAdd = true;
        }
        if(getIntent().getStringExtra("sort") != null && getIntent().getIntExtra("backgroundColor",0) != 0){
            sortText = getIntent().getStringExtra("sort");
            sortBackgroundColor = getIntent().getIntExtra("backgroundColor",0);
//            Log.d(TAG, "onCreate: sortText = " + sortText);
            if(sortText.equals(Constant.ALL_MEMO_STRING)){
                sortText = Constant.NOT_SORT_MEMO_STRING;
            }
            memo.setColor(sortBackgroundColor);
            memo.setSort(sortText);
            Log.d(TAG, "onCreate: sortText = " + sortText);
        }
        startTimePickerDialog = new TimePickerDialog(startTimeID);
        deadlinePickerDialog = new TimePickerDialog(deadlineID);
        databaseAdapter = new DatabaseAdapter(this);
        memoSortDatabaseAdapter = new MemoSortDatabaseAdapter(this);
        memoSortDatabaseAdapter.open();
        databaseAdapter.open();
        initFindById();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        memoSortDatabaseAdapter.close();
        databaseAdapter.close();
    }

    @Override
    public void onDialogPositiveClick(TimePickerDialog dialog) throws ParseException {
        Log.d(TAG, "onDialogPositiveClick: "+ dialog.getFormatString("yyyy-MM-dd HH:mm"));
        switch (dialog.getIdentity()){
            case deadlineID:
                deadlineEdit.setText(dialog.getFormatString("yyyy-MM-dd HH:mm"));
                break;
            case startTimeID:
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = simpleDateFormat.parse(dialog.getFormatString("yyyy-MM-dd HH:mm"));
                if(date.getTime() < System.currentTimeMillis() && memo.getStatus() != Constant.COMPLETE){
                    memo.setStatus(Constant.NOT_COMPLETE);
                }
                startTimeEdit.setText(dialog.getFormatString("yyyy-MM-dd HH:mm"));
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(TimePickerDialog dialog) {

    }

    void initFindById() {
        contentEdit = findViewById(R.id.memoContent);
        //
        startTimeEdit = findViewById(R.id.startTimeEdit);
        startTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimePickerDialog.show(getSupportFragmentManager(),null);
            }
        });
        //
        memoEditSortTitleCardView = findViewById(R.id.memoEditSortTitleCardView);
        deadlineEdit = findViewById(R.id.deadlineEdit);
        deadlineEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deadlinePickerDialog.show(getSupportFragmentManager(),null);
            }
        });

        //
        saveMemoIcon = findViewById(R.id.saveMemoItem);
        saveMemoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memo.setContent(contentEdit.getText().toString());
                memo.setStartTime(startTimeEdit.getText().toString());
                memo.setDeadline(deadlineEdit.getText().toString());
                if (memo.getContent().equals("")){
                    Toast.makeText(MemoEditActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                memo.setNote(noteEdit.getText().toString());
                if (isAdd) {
                    databaseAdapter.memoInsert(memo);
                } else {
                    databaseAdapter.memoUpdate(memo);
                }
                if(!memo.getStartTime().equals(Constant.START_TIME)){
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent intent = new Intent(MemoEditActivity.this, MyAlarmReceiver.class);
                    intent.setAction(Constant.ALARM_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("memo", memo);
                    intent.putExtras(bundle);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MemoEditActivity.this,0,intent,0);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        Date date = simpleDateFormat.parse(memo.getStartTime());
                        alarmManager.set(AlarmManager.RTC_WAKEUP,date.getTime(),pendingIntent);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                sendBroadcast(new Intent(Constant.UPDATE_ACTION));
                finish();

            }
        });
        FontIconView backMemoIcon = findViewById(R.id.backIcon);
        backMemoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //点击后弹出分类列表
        memoItemSort = findViewById(R.id.memoItemSort);
        memoItemSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopWindow(view);
            }
        });
        //完成按钮，点击后表示备忘已完成，将备忘状态改为已完成
        RadioButton completeButton = findViewById(R.id.completeButton);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton button = view.findViewById(R.id.completeButton);
                //再次点击按钮，将状态改为未完成
                if(button.getTag()==null){
                    button.setTag("complete");
                    button.setChecked(true);
                    contentEdit.setText(getSpannableString(contentEdit.getText().toString()));
                    memo.setStatus(1);
                }else if(button.getTag().equals("complete")){
                    button.setTag(null);
                    contentEdit.setText(contentEdit.getText().toString());
                    button.setChecked(false);
                    memo.setStatus(0);
                }
                contentEdit.setSelection(contentEdit.getText().length());
            }
        });

        contentEdit.addTextChangedListener(new TextWatcher() {
            String oldText;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                oldText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                Log.d(TAG, "afterTextChanged: "+contentEdit.getText().toString().equals(editable.toString()));
                String tag = (String) completeButton.getTag();
                if(tag != null && !oldText.equals(editable.toString())){
                    contentEdit.setText(getSpannableString(editable.toString()));
                    contentEdit.setSelection(contentEdit.getText().length());
                }
            }
        });
        //初始化备忘分类列表
        sortEdit = findViewById(R.id.memoItemSort);
        //初始化备忘内容
        noteEdit = findViewById(R.id.noteEdit);

        FloatingActionButton deleteMemoItem = findViewById(R.id.deleteMemoItem);
        deleteMemoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(memo.getId() != 0){
                    databaseAdapter.memoDelete(memo);
                }
                finish();
            }
        });
        initMemo();
    }

    //初始化分类弹框
    private void initPopWindow(View v){
        View view = LayoutInflater.from(this).inflate(R.layout.memo_sort_popwindow,null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        view.findViewById(R.id.notSortMemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memoItemSort.setText("未分类");
                memo.setSort("未分类");
                memo.setColor(0xffffffff);
                memoEditSortTitleCardView.setCardBackgroundColor(memo.getColor());
                popupWindow.dismiss();
            }
        });
        TextView addMemoSort = view.findViewById(R.id.addMemoSort);
        addMemoSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                initAddSortPopWindow();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(v,50,0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        setBackgroundAlpha(0.8f);
        sortMemoRecyclerView = view.findViewById(R.id.sortMemoList);
        List<SortMemo> sortMemoList= memoSortDatabaseAdapter.memoFindAllRecords();
        sortMemoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sortMemoRecyclerView.setAdapter(new MemoSortAdapter(this,sortMemoList));
    }

    private void initAddSortPopWindow(){
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
                        if (!memoSortDatabaseAdapter.isRecordExist(sortMemo.getSortText())){
                            memoSortDatabaseAdapter.memoSortInsert(sortMemo);
                            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                            addSortPopupWindow.dismiss();
                        }else {
                            Toast.makeText(MemoEditActivity.this,"分类已存在",Toast.LENGTH_SHORT).show();
                        }
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
    @Override
    public void onItemClick(String sortText, int color) {
        memoItemSort.setText(sortText);
        memo.setSort(sortText);
        Log.d(TAG, "onItemClick: "+color);
        memo.setColor(color);
        memoEditSortTitleCardView.setCardBackgroundColor(color);
        popupWindow.dismiss();
    }

    private void initMemo(){;
//        Log.d(TAG, "initMemo: "+sortText);
//        memoItemSort.setText(sortText);
        if (memo.getContent() != null) {
            contentEdit.setText(memo.getContent());
        }
        if (memo.getStartTime() != null) {
            startTimeEdit.setText(memo.getStartTime());
        }
        if (memo.getDeadline() != null) {
            deadlineEdit.setText(memo.getDeadline());
        }
        if (memo.getSort() != null) {
            memoItemSort.setText(memo.getSort());
        }
        if (memo.getNote() != null) {
            noteEdit.setText(memo.getNote());
        }
        if (memo.getColor() != 0) {
            memoEditSortTitleCardView.setCardBackgroundColor(memo.getColor());
        }
        if (memo.getStatus() == 1) {
            RadioButton completeButton = findViewById(R.id.completeButton);
            completeButton.setChecked(true);
            completeButton.setTag("complete");
            contentEdit.setText(getSpannableString(contentEdit.getText().toString()));
        }
    }

    private SpannableString getSpannableString(String content){
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new StrikethroughSpan(),0,spannableString.length(),0);
        return spannableString;
    }


    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

}