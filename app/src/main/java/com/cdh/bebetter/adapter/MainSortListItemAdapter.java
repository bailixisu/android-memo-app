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

public class MainSortListItemAdapter extends RecyclerView.Adapter<MainSortListItemAdapter.ViewHolder> {

    Context context;
    List<SortMemo> sortMemoList;
    MainSortListItemAdapter.OnSortItemClickListener onSortItemClickListener;
    List<Memo> memoList;

    public MainSortListItemAdapter(Context context, List<SortMemo> sortMemoList, List<Memo> memoList,MemoFragment memoFragment) {
        this.context = context;
        this.sortMemoList = sortMemoList;
        onSortItemClickListener = (MainSortListItemAdapter.OnSortItemClickListener) memoFragment;
        this.memoList = memoList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.main_sort_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        viewHolder.mainSortIcon.setColorFilter(sortMemoList.get(i).getSortIconColor());
        viewHolder.mainSortName.setText(sortMemoList.get(i).getSortText());
        Log.d("TAG", "onBindViewHolder: "+getSortCount(sortMemoList.get(i).getSortText()));
        viewHolder.mainSortCount.setText(String.valueOf(getSortCount(sortMemoList.get(i).getSortText())));
        viewHolder.mainSortItemCard.setOnClickListener(new View.OnClickListener() {
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
        TextView mainSortCount;
        CardView mainSortItemCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainSortIcon = itemView.findViewById(R.id.mainSortIcon);
            mainSortName = itemView.findViewById(R.id.mainSortName);
            mainSortCount = itemView.findViewById(R.id.mainSortCount);
            mainSortItemCard = itemView.findViewById(R.id.mainSortItemCard);
        }
    }

    public interface OnSortItemClickListener {
        void onItemClick(String sortText, int color);
    }

    //计算集合特定元素的个数
    private Integer getSortCount(String sort){
        int count = 0;
        for (int i = 0; i < memoList.size(); i++) {
            if(sort.equals(memoList.get(i).getSort())){
                count++;
            }
        }
        return count;
    }
}
