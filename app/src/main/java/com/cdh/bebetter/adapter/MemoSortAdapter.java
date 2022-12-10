package com.cdh.bebetter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdh.bebetter.R;
import com.cdh.bebetter.dao.SortMemo;

import java.util.List;

public class MemoSortAdapter extends RecyclerView.Adapter<MemoSortAdapter.ViewHolder> {
    Context context;
    List<SortMemo> sortMemoList;
    OnSortItemClickListener onSortItemClickListener;

    public MemoSortAdapter(Context context, List<SortMemo> sortMemoList) {
        this.context = context;
        this.sortMemoList = sortMemoList;
        onSortItemClickListener = (OnSortItemClickListener) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.memo_sort_popwindow_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sortText.setText(sortMemoList.get(position).getSortText());
        holder.sortIcon.setColorFilter(sortMemoList.get(position).getSortIconColor());
        holder.sortText.setTag(sortMemoList.get(position).getSortBackgroundColor());
        holder.sortText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("123456", "onClick: "+holder.sortText.getText());
                onSortItemClickListener.onItemClick(holder.sortText.getText().toString(), (int) holder.sortText.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sortMemoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView sortIcon;
        TextView sortText;
        public ViewHolder(View itemView) {
            super(itemView);
            sortIcon = itemView.findViewById(R.id.sortIcon);
            sortText = itemView.findViewById(R.id.sortText);
        }
    }

    public interface OnSortItemClickListener {
        void onItemClick(String sortText, int color);
    }
}

