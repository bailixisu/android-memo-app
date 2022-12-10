package com.cdh.bebetter.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cdh.bebetter.R;
import com.cdh.bebetter.activity.MemoEditActivity;
import com.cdh.bebetter.dao.Memo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MemoAdapter extends BaseAdapter {
    private List<Memo> memoList;
    private Context context;
    private LayoutInflater layoutInflater;
    private ViewHolder viewHolder;
    LinearLayout linearLayout;
    private String TAG = "MemoAdapter";

    public MemoAdapter(List<Memo> memoList, Context context) {
        this.memoList = memoList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return memoList.size();
    }

    @Override
    public Object getItem(int i) {
        return memoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.memo_item, null);
            viewHolder.content = view.findViewById(R.id.content);
            viewHolder.deadline = view.findViewById(R.id.date);
//            viewHolder.progressBar = view.findViewById(R.id.progressBar);
            viewHolder.startTime = view.findViewById(R.id.startTime);
            viewHolder.completeButtonInList = view.findViewById(R.id.completeButtonInList);
//            RadioButton button = view.findViewById(R.id.completeButton);
            //再次点击按钮，将状态改为未完成
            viewHolder.completeButtonInList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.content.setText(getSpannableString(viewHolder.content.getText().toString()));
//                    if(viewHolder.completeButtonInList.getTag()==null){
//                        viewHolder.completeButtonInList.setTag("complete");
//                        viewHolder.completeButtonInList.setChecked(true);
//                        viewHolder.content.setText(getSpannableString(viewHolder.content.getText().toString()));
////                memo.setStatus(1);
//                    }else if(viewHolder.completeButtonInList.getTag().equals("complete")){
//                        viewHolder.completeButtonInList.setTag(null);
//                        viewHolder.content.setText(viewHolder.content.getText().toString());
//                        viewHolder.completeButtonInList.setChecked(false);
////                memo.setStatus(0);
//                    }
                }
            });
//            viewHolder.content.setSelection(viewHolder.content.getText().length());
            viewHolder.linearLayout = view.findViewById(R.id.memoItemLinearLayout);
            viewHolder.linearLayout.setTag(i);
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: " + view.getTag());
                    Intent intent = new Intent(context, MemoEditActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("memo", memoList.get((Integer) view.getTag()));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        init(i);
        return view;
    }

    private void init(int i) {
        Memo memo = memoList.get(i);
        viewHolder.content.setText(memo.getContent());
        if (memo.getStatus() == 1) {
            viewHolder.content.setPaintFlags(viewHolder.content.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.completeButtonInList.setChecked(true);
        } else {
            viewHolder.content.setPaintFlags(viewHolder.content.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            viewHolder.completeButtonInList.setChecked(false);
        }
        if(memo.getDeadline() != null) {
            viewHolder.deadline.setText(memo.getDeadline().toString());
        }
        if (memo.getStartTime() != null) {
            viewHolder.startTime.setText(memo.getStartTime().toString());
        }
//        if ((memo.getDeadline() == null && memo.getStartTime() == null)||
//                (memo.getDeadline().equals(" 截止时间") && memo.getStartTime().equals(" 开始时间"))) {
//            Log.d(TAG, "init: in ................................");
//            changeMemoItemCardView(i);
//        }
    }
    private static class ViewHolder {
        private TextView content;
        private TextView deadline;
        private TextView startTime;
        private RadioButton completeButtonInList;
        private LinearLayout linearLayout;
//        private ProgressBar progressBar;
    }


    //如果时间没有设置，就不显示，让context变大
    private void changeMemoItemCardView(int i) {
        Memo memo = memoList.get(i);
        linearLayout.removeAllViews();
        linearLayout.addView(viewHolder.content);
    }

    private SpannableString getSpannableString(String content){
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new StrikethroughSpan(),0,spannableString.length(),0);
        return spannableString;
    }
}


