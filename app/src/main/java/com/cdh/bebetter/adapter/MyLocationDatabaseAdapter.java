package com.cdh.bebetter.adapter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.cdh.bebetter.dao.MyLocation;
import com.cdh.bebetter.dao.SortMemo;
import com.cdh.bebetter.properties.Database;
import com.cdh.bebetter.properties.MemoSortTable;
import com.cdh.bebetter.properties.MyLocationTable;
import com.cdh.bebetter.sqlite.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MyLocationDatabaseAdapter {
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private DatabaseHelper databaseHelper;
    private static String TAG = "In MyLocationDatabaseAdapter";

    public MyLocationDatabaseAdapter(Context context) {
        this.context = context;
    }

    public void open() throws SQLiteException {
        databaseHelper = new DatabaseHelper(context, Database.DB_NAME,null,Database.DB_VERSION);
        try {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
        } catch (SQLiteException e){
            sqLiteDatabase = databaseHelper.getReadableDatabase();
        }
        sqLiteDatabase.execSQL(MyLocationTable.CREATE_TABLE);
    }

    public void close(){
        if (sqLiteDatabase != null){
            sqLiteDatabase.close();
            sqLiteDatabase = null;
        }
    }

   public void myLocationInsert(MyLocation myLocation){
        //将MyLocation对象插入数据库
       Log.d(TAG, "myLocationInsert: "+myLocation.toString());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyLocationTable.LATITUDE,myLocation.getLatitude());
        contentValues.put(MyLocationTable.LONGITUDE,myLocation.getLongitude());
        contentValues.put(MyLocationTable.TIME,myLocation.getTime());
        contentValues.put(MyLocationTable.ID,myLocation.getId());
        contentValues.put(MyLocationTable.MEMO_ID,myLocation.getMemoId());
        sqLiteDatabase.insert(MyLocationTable.TABLE_NAME,null,contentValues);
       Log.d(TAG, "myLocationInsert: 结束了");
    }

    //删除一条记录
    public void myLocationDelete(MyLocation myLocation){
        sqLiteDatabase.delete(MyLocationTable.TABLE_NAME,MyLocationTable.ID + "=?",new String[]{String.valueOf(myLocation.getId())});
    }

    //查找所有的记录
    @SuppressLint("Range")
    public List<MyLocation> myLocationFindAllRecords(){
        List<MyLocation> myLocationList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(MyLocationTable.TABLE_NAME,null,null,null,null,null,null);
        if (cursor != null){
            while (cursor.moveToNext()){
                MyLocation myLocation = new MyLocation();
                myLocation.setId(cursor.getLong(cursor.getColumnIndex(MyLocationTable.ID)));
                myLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex(MyLocationTable.LATITUDE)));
                myLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex(MyLocationTable.LONGITUDE)));
                myLocation.setTime(cursor.getString(cursor.getColumnIndex(MyLocationTable.TIME)));
                myLocation.setMemoId(cursor.getLong(cursor.getColumnIndex(MyLocationTable.MEMO_ID)));
                myLocationList.add(myLocation);
            }
        }
        return myLocationList;
    }

    //查找一条记录,根据memo_id
    @SuppressLint("Range")
    public MyLocation myLocationFindOneRecord(Long memoId){
        MyLocation myLocation = new MyLocation();
        Cursor cursor = sqLiteDatabase.query(MyLocationTable.TABLE_NAME,null,MyLocationTable.MEMO_ID + "=?",new String[]{String.valueOf(memoId)},null,null,null);
        if (cursor != null){
            while (cursor.moveToNext()){
                myLocation.setId(cursor.getLong(cursor.getColumnIndex(MyLocationTable.ID)));
                myLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex(MyLocationTable.LATITUDE)));
                myLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex(MyLocationTable.LONGITUDE)));
                myLocation.setTime(cursor.getString(cursor.getColumnIndex(MyLocationTable.TIME)));
                myLocation.setMemoId(cursor.getLong(cursor.getColumnIndex(MyLocationTable.MEMO_ID)));
            }
        }
        return myLocation;
    }

    //删除所有记录
    public void myLocationDeleteAllRecords(){
        sqLiteDatabase.delete(MyLocationTable.TABLE_NAME,null,null);
    }
}
