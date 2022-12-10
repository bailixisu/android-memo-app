package com.cdh.bebetter.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.activity.MemoEditActivity;
import com.cdh.bebetter.dao.Memo;
import com.cdh.bebetter.fragment.MemoFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MemoRecycleAdapter extends RecyclerView.Adapter<MemoRecycleAdapter.ViewHolder> {
    private List<Memo> memoList;
    private Context context;
//    private LayoutInflater layoutInflater;
//    LinearLayout linearLayout;
    private String TAG = "MemoAdapter";
    MemoRecycleAdapter.OnSortItemClickListener onSortItemClickListener;
    private Boolean isClear = true;

    public MemoRecycleAdapter(List<Memo> memoList, Context context, MemoFragment memoFragment) {
        this.memoList = memoList;
        this.context = context;
        onSortItemClickListener = (MemoRecycleAdapter.OnSortItemClickListener) memoFragment;
    }

    @NonNull
    @Override
    public MemoRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.memo_item, null);
        return new MemoRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoRecycleAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        viewHolder.startTime.setTextColor(context.getResources().getColor(R.color.gray));
        viewHolder.deadline.setTextColor(context.getResources().getColor(R.color.gray));
        viewHolder.completeButtonInList.setTag(Constant.NOT_COMPLETE);
        switch (memoList.get(i).getStatus()){
            case Constant.NOT_COMPLETE:
                viewHolder.content.setText(memoList.get(i).getContent());
                break;
            case Constant.COMPLETE:
                viewHolder.content.setText(getSpannableString(memoList.get(i).getContent()));
                viewHolder.completeButtonInList.setChecked(true);
                viewHolder.completeButtonInList.setTag(Constant.COMPLETE);
                break;
            case Constant.OUT_DATE:
                viewHolder.content.setText(memoList.get(i).getContent());
                viewHolder.startTime.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.deadline.setTextColor(context.getResources().getColor(R.color.red));
        }
        viewHolder.startTime.setText(memoList.get(i).getStartTime());
        viewHolder.deadline.setText(memoList.get(i).getDeadline());
        viewHolder.linearLayout.setTag(i);
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MemoEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("memo", memoList.get((Integer) view.getTag()));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        viewHolder.completeButtonInList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Integer) view.getTag() == Constant.COMPLETE){
                    memoList.get(i).setStatus(Constant.NOT_COMPLETE);
                    if (compareToToday(memoList.get(i).getStartTime())){
                        memoList.get(i).setStatus(Constant.OUT_DATE);
                    }
                    viewHolder.completeButtonInList.setTag(Constant.NOT_COMPLETE);
                    viewHolder.completeButtonInList.setChecked(false);
                    notifyDataSetChanged();
                }else {
                    memoList.get((Integer) viewHolder.linearLayout.getTag()).setStatus(Constant.COMPLETE);
                    viewHolder.completeButtonInList.setTag(Constant.COMPLETE);
                    notifyDataSetChanged();
                }
                onSortItemClickListener.onItemChange(memoList.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return isClear ? memoList.size():0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setIsClear(Boolean isClear){
        this.isClear = isClear;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView content;
        private TextView deadline;
        private TextView startTime;
        private RadioButton completeButtonInList;
        private LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            deadline = itemView.findViewById(R.id.date);
            startTime = itemView.findViewById(R.id.startTime);
            completeButtonInList = itemView.findViewById(R.id.completeButtonInList);
            linearLayout = itemView.findViewById(R.id.memoItemLinearLayout);
        }
    }

    private SpannableString getSpannableString(String content){
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new StrikethroughSpan(),0,spannableString.length(),0);
        return spannableString;
    }

    private Boolean compareToToday(String date){
        if (date.equals(Constant.START_TIME)){
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date time = simpleDateFormat.parse(date);
            Date today = new Date();
            Log.d(TAG, "compareToToday: "+time.before(today));
            if (time.before(today)){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface OnSortItemClickListener {
        void onRadioButtonClick();
        void onItemChange(Memo memo);
//        void deleteIconClick(int i);
    }

}
