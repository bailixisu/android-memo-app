package com.cdh.bebetter.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.activity.MainActivity;
import com.cdh.bebetter.activity.MemoEditActivity;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.dao.Memo;

import java.util.List;

public class WidgetProvider extends AppWidgetProvider {
    private static final String CLICK_ACTION = "com.barry.widgetapp.plus.CLICK";
    private static final String TAG = "WidgetProvider";
    DatabaseAdapter databaseAdapter;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        //当我们点击桌面上的widget按钮（这个按钮我们在onUpdate中已经为它设置了监听），widget就会发送广播
        //这个广播我们也在onUpdate中为它设置好了意图，设置了action，在这里我们接收到对应的action并做相应处理
        if (intent.getAction().equals(Constant.UPDATE_ACTION)) {
            //这里我们更新一下界面
            databaseAdapter = new DatabaseAdapter(context);
            ComponentName componentName = new ComponentName(context, WidgetProvider.class);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.widget_memos_count,String.valueOf(getMemoListCount()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.appwidget_list);
            appWidgetManager.updateAppWidget(componentName,remoteViews);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //因为可能有多个widget，所以要对它们全部更新
        for (int appWidgetId : appWidgetIds) {
            //创建一个远程view，绑定我们要操控的widget布局文件
            databaseAdapter = new DatabaseAdapter(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
            Intent intentClick = new Intent();
            //这个必须要设置，不然点击效果会无效
            intentClick.setClass(context,MemoEditActivity.class);
            intentClick.setAction(CLICK_ACTION);
            //PendingIntent表示的是一种即将发生的意图，区别于Intent它不是立即会发生的
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intentClick,0);

            remoteViews.setTextViewText(R.id.widget_memos_count,String.valueOf(getMemoListCount()));
            //为布局文件中的按钮设置点击监听
            remoteViews.setOnClickPendingIntent(R.id.appwidget_add_button,pendingIntent);
            //设置其他点击位置跳至主页面
            Intent intent = new Intent();
            intent.setClass(context, MainActivity.class);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context,0,intent,0);
            remoteViews.setOnClickPendingIntent(R.id.widget_layout_left,pendingIntent2);
            remoteViews.setOnClickPendingIntent(R.id.widget_layout_right,pendingIntent2);
            //告诉AppWidgetManager对当前应用程序小部件执行更新
            Intent serviceIntent = new Intent(context, MyRemoteViewsService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            remoteViews.setRemoteAdapter(R.id.appwidget_list, serviceIntent);
            remoteViews.setEmptyView(R.id.appwidget_list,R.id.widget_empty);
            Intent clickIntentTemplate = new Intent(context, MemoEditActivity.class);
            PendingIntent clickPendingIntentTemplate = PendingIntent.getActivity(context, 0, clickIntentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.appwidget_list, clickPendingIntentTemplate);
            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }


    private int getMemoListCount(){
        databaseAdapter.open();
        List<Memo> memoList = databaseAdapter.memoFindAllRecords();
        int count = 0;
        for (Memo memo : memoList){
            if (memo.getStatus() != Constant.COMPLETE){
                count++;
            }
        }
        databaseAdapter.close();
        return count;
    }
}
