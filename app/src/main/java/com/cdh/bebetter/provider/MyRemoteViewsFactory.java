package com.cdh.bebetter.provider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.activity.MemoEditActivity;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.dao.Memo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    List<Memo> memoList;
    DatabaseAdapter databaseAdapter;
    private static final String TAG = "MyRemoteViewsFactory";
    public MyRemoteViewsFactory(Context context) {
        mContext = context;
    }


    @Override
    public void onCreate() {
        databaseAdapter = new DatabaseAdapter(mContext);
        databaseAdapter.open();
        memoList = new ArrayList<>();
        List<Memo> tempMemoList = databaseAdapter.memoFindAllRecords();
        for (Memo memo : tempMemoList) {
            if (memo.getStatus() != Constant.COMPLETE) {
                memoList.add(memo);
            }
        }

    }

    @Override
    public void onDataSetChanged() {
        memoList = new ArrayList<>();
        List<Memo> tempMemoList = databaseAdapter.memoFindAllRecords();
        for (Memo memo : tempMemoList) {
            if (memo.getStatus() != Constant.COMPLETE) {
                memoList.add(memo);
            }
        }
    }

    @Override
    public void onDestroy() {
        databaseAdapter.close();
    }

    @Override
    public int getCount() {
        return memoList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (i < 0 || i >= memoList.size())
            return null;
        Memo memo = memoList.get(i);
        // 创建在当前索引位置要显示的View
        final RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_memo_item);

        // 设置要显示的内容
        rv.setTextViewText(R.id.widget_content,memo.getContent());
        rv.setTextViewText(R.id.widget_time,getTimeString(memo));

        // 填充Intent，填充在AppWdigetProvider中创建的PendingIntent
//        Intent intent = new Intent();
//        // 传入点击行的数据
//        intent.putExtra("content", content);
//        rv.setOnClickFillInIntent(R.id.widget_list_item_tv, intent);
        Intent intent = new Intent(mContext, MemoEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("memo", memoList.get(i));
        intent.putExtras(bundle);
        rv.setOnClickFillInIntent(R.id.widget_layout, intent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    private String getTimeString(Memo memo){
        if (memo.getStartTime().equals(Constant.START_TIME)&&memo.getDeadline().equals(Constant.DEADLINE)){
            return "无时间限制";
        }
        if (memo.getStartTime().equals(Constant.START_TIME)){
            return " - "+memo.getDeadline();
        }
        if (memo.getDeadline().equals(Constant.DEADLINE)){
            return memo.getStartTime()+" - ";
        }
        return memo.getStartTime()+" - "+memo.getDeadline();
    }
}
