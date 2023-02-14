package com.cdh.bebetter.provider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.cdh.bebetter.Constant;
import com.cdh.bebetter.R;
import com.cdh.bebetter.adapter.DatabaseAdapter;
import com.cdh.bebetter.dao.Memo;

public class MyAlarmReceiver extends BroadcastReceiver {
    DatabaseAdapter databaseAdapter;
    @Override
    public void onReceive(Context context, Intent intent) {
        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();
        Log.d("TAG", "onReceive: 在这里面了");
        Toast.makeText(context, "能收到", Toast.LENGTH_SHORT).show();
        Memo memoInIntent = (Memo) intent.getSerializableExtra("memo");
        if(memoInIntent == null){
            return;
        }
        Memo memoInDatabase = databaseAdapter.memoFindById(memoInIntent.getId());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("BeBetter")
                .setContentText(memoInDatabase.getContent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(1, builder.build());
    }
}
