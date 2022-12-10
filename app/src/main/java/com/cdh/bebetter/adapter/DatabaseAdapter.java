package com.cdh.bebetter.adapter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.cdh.bebetter.dao.Memo;
import com.cdh.bebetter.properties.Database;
import com.cdh.bebetter.properties.MemoTable;
import com.cdh.bebetter.sqlite.DatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseAdapter {
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private DatabaseHelper databaseHelper;
    private static String TAG = "In DatabaseAdapter";

    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    public void open() throws SQLiteException{
        databaseHelper = new DatabaseHelper(context, Database.DB_NAME,null,Database.DB_VERSION);
        try {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
        } catch (SQLiteException e){
            sqLiteDatabase = databaseHelper.getReadableDatabase();
        }
        sqLiteDatabase.execSQL(MemoTable.CREATE_TABLE);
    }

    public void close(){
        if (sqLiteDatabase != null){
            sqLiteDatabase.close();
            sqLiteDatabase = null;
        }
    }

    public void memoInsert(Memo memo){
        //将memo对象插入数据库
        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoTable.START_TIME,memo.getStartTime());
        contentValues.put(MemoTable.ID,memo.getId());
        contentValues.put(MemoTable.DEADLINE,memo.getDeadline());
        contentValues.put(MemoTable.CONTENT,memo.getContent());
        contentValues.put(MemoTable.COMPLETE_TIME,memo.getCompleteTime());
        contentValues.put(MemoTable.NOTE,memo.getNote());
        contentValues.put(MemoTable.SORT,memo.getSort());
        contentValues.put(MemoTable.STATUS,memo.getStatus());
        contentValues.put(MemoTable.LIKE,memo.getLike());
        contentValues.put(MemoTable.CIRCULATE,memo.getCirculate());
        contentValues.put(MemoTable.COLOR,memo.getColor());
        sqLiteDatabase.insert(MemoTable.TABLE_NAME,null,contentValues);
    }

    @SuppressLint("Range")
    public List<Memo> memoFindAllRecords(){
        Cursor cursor = sqLiteDatabase.query(MemoTable.TABLE_NAME,null,null,null,null,null,null);
        List<Memo> memos = new ArrayList<>();
        int recordsCount = cursor.getCount();
        cursor.moveToFirst();
        for (int i = 0; i < recordsCount; i++) {
            Memo memo = new Memo();
            memo.setId(cursor.getLong(cursor.getColumnIndex(MemoTable.ID)));
            memo.setContent(cursor.getString(cursor.getColumnIndex(MemoTable.CONTENT)));
            memo.setStartTime(cursor.getString(cursor.getColumnIndex(MemoTable.START_TIME)));
            memo.setDeadline(cursor.getString(cursor.getColumnIndex(MemoTable.DEADLINE)));
            memo.setCompleteTime(cursor.getString(cursor.getColumnIndex(MemoTable.COMPLETE_TIME)));
            memo.setNote(cursor.getString(cursor.getColumnIndex(MemoTable.NOTE)));
            memo.setSort(cursor.getString(cursor.getColumnIndex(MemoTable.SORT)));
            memo.setStatus(cursor.getInt(cursor.getColumnIndex(MemoTable.STATUS)));
            memo.setLike(cursor.getInt(cursor.getColumnIndex(MemoTable.LIKE)));
            memo.setCirculate(cursor.getInt(cursor.getColumnIndex(MemoTable.CIRCULATE)));
            memo.setColor(cursor.getInt(cursor.getColumnIndex(MemoTable.COLOR)));
            memos.add(memo);
            cursor.moveToNext();
        }
        //按时间排序

        return memos;
    }

    public void memoDelete(Memo memo){
        sqLiteDatabase.delete(MemoTable.TABLE_NAME,MemoTable.ID + "=?",new String[]{memo.getId().toString()});
    }

    //更新数据库中的memo
    public void memoUpdate(Memo memo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoTable.START_TIME,memo.getStartTime());
        contentValues.put(MemoTable.ID,memo.getId());
        contentValues.put(MemoTable.DEADLINE,memo.getDeadline());
        contentValues.put(MemoTable.CONTENT,memo.getContent());
        contentValues.put(MemoTable.COMPLETE_TIME,memo.getCompleteTime());
        contentValues.put(MemoTable.NOTE,memo.getNote());
        contentValues.put(MemoTable.SORT,memo.getSort());
        contentValues.put(MemoTable.STATUS,memo.getStatus());
        contentValues.put(MemoTable.LIKE,memo.getLike());
        contentValues.put(MemoTable.CIRCULATE,memo.getCirculate());
        contentValues.put(MemoTable.COLOR,memo.getColor());
        sqLiteDatabase.update(MemoTable.TABLE_NAME,contentValues,MemoTable.ID + "=?",new String[]{memo.getId().toString()});
    }

    //删除某个分类下的所有memo
    public void memoDeleteBySort(String sort){
        sqLiteDatabase.delete(MemoTable.TABLE_NAME,MemoTable.SORT + "=?",new String[]{sort});
    }
}
