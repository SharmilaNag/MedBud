package com.sasr.medbudfinal.data.history;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sasr.medbudfinal.data.MedBudDatabaseHelper;
import com.sasr.medbudfinal.model.History;
import com.sasr.medbudfinal.util.HistoryUtil;

import java.util.ArrayList;

public class HistoryDao {
    private Context context;

    public HistoryDao(Context context) {this.context = context;}

    public long inserHistoryEntry(History history) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(HistoryUtil.KEY_MED_NAME,history.getMedName());
        cv.put(HistoryUtil.KEY_MED_TYPE,history.getMedType());
        cv.put(HistoryUtil.KEY_QUANTITY_TAKEN,history.getQuantityTaken());
        cv.put(HistoryUtil.KEY_DUE_TIME,history.getDueTime());
        cv.put(HistoryUtil.KEY_DUE_DATE,history.getDueDate());
        cv.put(HistoryUtil.KEY_DUE_DAY,history.getDueDay());
        cv.put(HistoryUtil.KEY_TAKE_TIME,history.getTakeTime());
        cv.put(HistoryUtil.KEY_TAKE_DATE,history.getTakeDate());
        cv.put(HistoryUtil.KEY_TAKE_DAY,history.getTakeDay());

        long id =  db.insert(HistoryUtil.TABLE_NAME,null,cv);
        db.close();
        return id;
    }

    public int updateHistoryEntry(History history) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(HistoryUtil.KEY_ID,history.getId());
        cv.put(HistoryUtil.KEY_MED_NAME,history.getMedName());
        cv.put(HistoryUtil.KEY_MED_TYPE,history.getMedType());
        cv.put(HistoryUtil.KEY_QUANTITY_TAKEN,history.getQuantityTaken());
        cv.put(HistoryUtil.KEY_DUE_TIME,history.getDueTime());
        cv.put(HistoryUtil.KEY_DUE_DATE,history.getDueDate());
        cv.put(HistoryUtil.KEY_DUE_DAY,history.getDueDay());
        cv.put(HistoryUtil.KEY_TAKE_TIME,history.getTakeTime());
        cv.put(HistoryUtil.KEY_TAKE_DATE,history.getTakeDate());
        cv.put(HistoryUtil.KEY_TAKE_DAY,history.getTakeDay());

        int done =  db.update(HistoryUtil.TABLE_NAME,cv,HistoryUtil.KEY_ID+" =?",
                new String[] {String.valueOf(history.getId())});
        db.close();
        return done;
    }

    public int deleteHistoryEntry(int id) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();
        int done =  db.delete(HistoryUtil.TABLE_NAME,HistoryUtil.KEY_ID+" =?",
                new String[] {String.valueOf(id)});
        db.close();
        return done;
    }

    public History getHistoryEntry(int id) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cur = db.query(HistoryUtil.TABLE_NAME,null,HistoryUtil.KEY_ID+" =?",
                new String[] {String.valueOf(id)},null,null,null);
        if(cur.moveToNext()) {
            History history = new History();
            history.setId(cur.getInt(cur.getColumnIndex(HistoryUtil.KEY_ID)));
            history.setMedName(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_MED_NAME)));
            history.setMedType(cur.getInt(cur.getColumnIndex(HistoryUtil.KEY_MED_TYPE)));
            history.setQuantityTaken(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_QUANTITY_TAKEN)));
            history.setDueTime(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_DUE_TIME)));
            history.setDueDate(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_DUE_DATE)));
            history.setDueDay(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_DUE_DAY)));
            history.setTakeTime(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_TAKE_TIME)));
            history.setTakeDate(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_TAKE_DATE)));
            history.setTakeDay(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_TAKE_DAY)));
            cur.close();
            db.close();
            return history;
        }
        cur.close();
        db.close();
        return  null;
    }

    public ArrayList<History> getAllHistory() {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cur = db.query(HistoryUtil.TABLE_NAME,null,null,null,
                null,null,null);
        ArrayList<History> histories = new ArrayList<>();
        while (cur.moveToNext()) {
            History history = new History();
            history.setId(cur.getInt(cur.getColumnIndex(HistoryUtil.KEY_ID)));
            history.setMedName(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_MED_NAME)));
            history.setMedType(cur.getInt(cur.getColumnIndex(HistoryUtil.KEY_MED_TYPE)));
            history.setQuantityTaken(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_QUANTITY_TAKEN)));
            history.setDueTime(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_DUE_TIME)));
            history.setDueDate(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_DUE_DATE)));
            history.setDueDay(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_DUE_DAY)));
            history.setTakeTime(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_TAKE_TIME)));
            history.setTakeDate(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_TAKE_DATE)));
            history.setTakeDay(cur.getString(cur.getColumnIndex(HistoryUtil.KEY_TAKE_DAY)));
            histories.add(history);
        }
        cur.close();
        db.close();
        return  histories;
    }

}
