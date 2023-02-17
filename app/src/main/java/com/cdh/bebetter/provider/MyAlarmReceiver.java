package com.cdh.bebetter.provider;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.activity.MainActivity;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.dao.Memo;
import com.cdh.bebetter.service.NotificationService;

public class MyAlarmReceiver extends BroadcastReceiver {
    DatabaseAdapter databaseAdapter;
    @Override
    public void onReceive(Context context, Intent intent) {
        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();
        //弹出通知
        Long id = intent.getLongExtra("id", 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        Memo memoInDatabase = databaseAdapter.memoFindById(id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(Constant.NOTICE_HINT)
                .setContentText(memoInDatabase.getContent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        notificationManager.notify(1, builder.build());
    }
}
