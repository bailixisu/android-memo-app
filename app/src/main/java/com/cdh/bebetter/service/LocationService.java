package com.cdh.bebetter.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.amap.api.maps.AMap;
import com.cdh.bebetter.adapter.MyLocationDatabaseAdapter;

public class LocationService extends Service {
    MyLocationDatabaseAdapter myLocationDatabaseAdapter;
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return  null;
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myLocationDatabaseAdapter = new MyLocationDatabaseAdapter(this);
        myLocationDatabaseAdapter.open();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myLocationDatabaseAdapter.close();
    }
}