package com.sasr.medbudfinal.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sasr.medbudfinal.model.Pill;
import com.sasr.medbudfinal.util.PillUtil;

import java.util.ArrayList;

public class PillDao {
    private static final String TAG = "PillDao";
    Context context;

    public PillDao(Context context) {
        this.context = context;
    }

    public long addPill (Pill pill,SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(PillUtil.KEY_NAME,pill.getName());
        cv.put(PillUtil.KEY_TYPE,pill.getType());
        return db.insert(PillUtil.TABLE_NAME,null,cv);
    }

    public Pill getSinglePill (int id,SQLiteDatabase db) {
        Cursor cur = db.query(PillUtil.TABLE_NAME,null,
                PillUtil.KEY_ID+"=?",
                new String[] {String.valueOf(id)},
                null,null,null);
        if (cur.moveToFirst()) {
            Pill pill = new Pill(
                    cur.getInt(cur.getColumnIndex(PillUtil.KEY_ID)),
                    cur.getString(cur.getColumnIndex(PillUtil.KEY_NAME)),
                    cur.getInt(cur.getColumnIndex(PillUtil.KEY_TYPE))
            );
            return pill;
        }
        return null;
    }

    public Pill getSinglePillByName (String name,SQLiteDatabase db) {
        Cursor cur = db.query(PillUtil.TABLE_NAME,null,
                PillUtil.KEY_NAME+"=?",
                new String[] {name},
                null,null,null);
        if (cur.moveToFirst()) {
            Pill pill = new Pill(
                    cur.getInt(cur.getColumnIndex(PillUtil.KEY_ID)),
                    cur.getString(cur.getColumnIndex(PillUtil.KEY_NAME)),
                    cur.getInt(cur.getColumnIndex(PillUtil.KEY_TYPE))
            );
            return pill;
        }
        return null;
    }

    public int updatePill (Pill pill,SQLiteDatabase db) {
        Log.d(TAG, "updatePill: "+pill.getId()+" "+pill.getName());
        ContentValues cv = new ContentValues();
        cv.put(PillUtil.KEY_ID,pill.getId());
        cv.put(PillUtil.KEY_NAME,pill.getName());
        cv.put(PillUtil.KEY_TYPE,pill.getType());
        return db.update(PillUtil.TABLE_NAME,cv,PillUtil.KEY_ID+"=?",
                new String[] {String.valueOf(pill.getId())});
    }

    public int deletePill (int id,SQLiteDatabase db) {
        return db.delete(PillUtil.TABLE_NAME,PillUtil.KEY_ID+"=?",
                new String[]{String.valueOf(id)});
    }

    public ArrayList<Pill> getAllPills () {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cur = db.query(PillUtil.TABLE_NAME,null,null,null,
                null,null,PillUtil.KEY_NAME+" ASC");

        ArrayList<Pill> pillList = new ArrayList<>();
        while (cur.moveToNext()) {
            Pill pill = new Pill();
            pill.setId(cur.getInt(cur.getColumnIndex(PillUtil.KEY_ID)));
            pill.setName(cur.getString(cur.getColumnIndex(PillUtil.KEY_NAME)));
            pill.setType(cur.getInt(cur.getColumnIndex(PillUtil.KEY_TYPE)));
            pillList.add(pill);
        }
        return pillList;
    }
}
