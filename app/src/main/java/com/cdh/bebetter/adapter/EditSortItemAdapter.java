package com.cdh.bebetter.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdh.bebetter.R;
import com.cdh.bebetter.dao.Memo;
import com.cdh.bebetter.dao.SortMemo;
import com.cdh.bebetter.fragment.MemoFragment;

import java.util.List;

public class EditSortItemAdapter extends RecyclerView.Adapter<EditSortItemAdapter.ViewHolder>{

    Context context;
    List<SortMemo> sortMemoList;
    EditSortItemAdapter.OnSortItemClickListener onSortItemClickListener;

    public EditSortItemAdapter(Context context, List<SortMemo> sortMemoList) {
        this.context = context;
        this.sortMemoList = sortMemoList;
        onSortItemClickListener = (EditSortItemAdapter.OnSortItemClickListener) context;
    }
    @NonNull
    @Override
    public EditSortItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.edit_sort_item, null);
        return new EditSortItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditSortItemAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        viewHolder.mainSortIcon.setColorFilter(sortMemoList.get(i).getSortIconColor());
        viewHolder.mainSortName.setText(sortMemoList.get(i).getSortText());
        viewHolder.deleteSortItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSortItemClickListener.deleteIconClick(i);
            }
        });
        viewHolder.mainSortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSortItemClickListener.onItemClick(sortMemoList.get(i).getSortText(),sortMemoList.get(i).getSortBackgroundColor());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sortMemoList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mainSortIcon;
        TextView mainSortName;
        ImageView deleteSortItem;
        CardView mainSortItemCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainSortIcon = itemView.findViewById(R.id.mainSortIcon);
            mainSortName = itemView.findViewById(R.id.mainSortName);
            deleteSortItem = itemView.findViewById(R.id.deleteSortItem);
            mainSortItemCard = itemView.findViewById(R.id.mainSortItemCard);
        }
    }

    public interface OnSortItemClickListener {
        void onItemClick(String sortText, int color);
        void deleteIconClick(int i);
    }

}
