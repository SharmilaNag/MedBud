package com.sasr.medbudfinal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.sasr.medbudfinal.util.AppContext;
import com.sasr.medbudfinal.util.HistoryUtil;
import com.sasr.medbudfinal.util.InventoryUtil;
import com.sasr.medbudfinal.util.PillUtil;
import com.sasr.medbudfinal.util.ReminderUtil;
import com.sasr.medbudfinal.util.UserInfoUtil;

public class MedBudDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static MedBudDatabaseHelper dbInstance;

    private MedBudDatabaseHelper(@Nullable Context context) {
        super(context, AppContext.DATABASE_NAME,null, AppContext.DATABASE_VERSION);
        this.context = context;
    }

    public static MedBudDatabaseHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new MedBudDatabaseHelper(context);
        }
        return dbInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PillUtil.CREATE_STRING);
        db.execSQL(InventoryUtil.CREATE_STRING);
        db.execSQL(ReminderUtil.CREATE_STRING);
        db.execSQL(UserInfoUtil.CREATE_STRING);
        db.execSQL(HistoryUtil.CREATE_STRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PillUtil.DROP_STRING);
        db.execSQL(InventoryUtil.DROP_STRING);
        db.execSQL(ReminderUtil.DROP_STRING);
        db.execSQL(UserInfoUtil.DROP_STRING);
        db.execSQL(HistoryUtil.DROP_STRING);
        onCreate(db);
    }

    public void closeDb () {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db!=null) {
            db.close();
        }
        this.close();
    }
}
