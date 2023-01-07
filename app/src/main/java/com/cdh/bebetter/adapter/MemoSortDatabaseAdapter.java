package com.cdh.bebetter.adapter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.cdh.bebetter.dao.Memo;
import com.cdh.bebetter.dao.SortMemo;
import com.cdh.bebetter.properties.Database;
import com.cdh.bebetter.properties.MemoSortTable;
import com.cdh.bebetter.properties.MemoTable;
import com.cdh.bebetter.sqlite.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MemoSortDatabaseAdapter {
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private DatabaseHelper databaseHelper;
    private static String TAG = "In MemoSortDatabaseAdapter";

    public MemoSortDatabaseAdapter(Context context) {
        this.context = context;
    }

    public void open() throws SQLiteException {
        databaseHelper = new DatabaseHelper(context, Database.MEMO_SORT_DB_NAME,null,Database.DB_VERSION);
        try {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
        } catch (SQLiteException e){
            sqLiteDatabase = databaseHelper.getReadableDatabase();
        }
        sqLiteDatabase.execSQL(MemoSortTable.CREATE_TABLE);
    }

    public void close(){
        if (sqLiteDatabase != null){
            sqLiteDatabase.close();
            sqLiteDatabase = null;
        }
    }

    public void memoSortInsert(SortMemo sortMemo){
        if (isRecordExist(sortMemo.getSortText())){
            return;
        }
        //将memoSort对象插入数据库
        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoSortTable.SORT_TEXT,sortMemo.getSortText());
        contentValues.put(MemoSortTable.SORT_ICON_COLOR,sortMemo.getSortIconColor());
        contentValues.put(MemoSortTable.SORT_BACKGROUND_COLOR,sortMemo.getSortBackgroundColor());
        sqLiteDatabase.insert(MemoSortTable.TABLE_NAME,null,contentValues);
    }



    @SuppressLint("Range")
    public List<SortMemo> memoFindAllRecords(){
        //查询所有记录
        List<SortMemo> sortMemoList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(MemoSortTable.TABLE_NAME,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                SortMemo sortMemo = new SortMemo();
                sortMemo.setSortText(cursor.getString(cursor.getColumnIndex(MemoSortTable.SORT_TEXT)));
                sortMemo.setSortIconColor(cursor.getInt(cursor.getColumnIndex(MemoSortTable.SORT_ICON_COLOR)));
                sortMemo.setSortBackgroundColor(cursor.getInt(cursor.getColumnIndex(MemoSortTable.SORT_BACKGROUND_COLOR)));
                sortMemoList.add(sortMemo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sortMemoList;
    }

    public void memoDelete(SortMemo sortMemo){
        //删除一条记录
        sqLiteDatabase.delete(MemoSortTable.TABLE_NAME,MemoSortTable.SORT_TEXT + "=?",new String[]{sortMemo.getSortText()});
    }

    //查询记录是否存在
    public boolean isRecordExist(String sortText){
        Cursor cursor = sqLiteDatabase.query(MemoSortTable.TABLE_NAME,null,MemoSortTable.SORT_TEXT + "=?",new String[]{sortText},null,null,null);
        if (cursor.moveToFirst()){
            return true;
        } else {
            return false;
        }
    }

    //更新一条记录
    public void sortMemoUpdate(SortMemo sortMemo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoSortTable.SORT_TEXT,sortMemo.getSortText());
        contentValues.put(MemoSortTable.SORT_ICON_COLOR,sortMemo.getSortIconColor());
        contentValues.put(MemoSortTable.SORT_BACKGROUND_COLOR,sortMemo.getSortBackgroundColor());
        sqLiteDatabase.update(MemoSortTable.TABLE_NAME,contentValues,MemoSortTable.SORT_TEXT + "=?",new String[]{sortMemo.getSortText()});
    }


}
