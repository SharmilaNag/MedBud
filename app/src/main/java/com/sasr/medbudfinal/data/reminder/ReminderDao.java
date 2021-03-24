package com.sasr.medbudfinal.data.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.sasr.medbudfinal.data.MedBudDatabaseHelper;
import com.sasr.medbudfinal.data.inv.InventoryDao;
import com.sasr.medbudfinal.model.Inventory;
import com.sasr.medbudfinal.model.Reminder;
import com.sasr.medbudfinal.util.ReminderUtil;

import java.util.ArrayList;

public class ReminderDao {
    private static final String TAG = "ReminderDao";
    private Context context;

    public ReminderDao(Context context) {
        this.context = context;
    }

    public long addReminderEntry (Reminder reminder) {
        SQLiteDatabase db= MedBudDatabaseHelper.getInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ReminderUtil.KEY_INVENTORY_ID,reminder.getInventory().getId());
        cv.put(ReminderUtil.KEY_DOSAGE,reminder.getDosage());
        cv.put(ReminderUtil.KEY_DURATION,reminder.getDuration());
        cv.put(ReminderUtil.KEY_INSTRUCTION,reminder.getInstruction());
        cv.put(ReminderUtil.KEY_TIME,reminder.getTime());
        cv.put(ReminderUtil.KEY_DATE_FROM,reminder.getFromDate());
        cv.put(ReminderUtil.KEY_DATE_TO,reminder.getToDate());
        cv.put(ReminderUtil.KEY_DAY_MARKER,reminder.getDayMarker());

        long id =  db.insert(ReminderUtil.TABLE_NAME,null,cv);
        db.close();
        return id;

    }

    public int updateReminderEntry (Reminder reminder) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ReminderUtil.KEY_ID,reminder.getId());
        cv.put(ReminderUtil.KEY_INVENTORY_ID,reminder.getInventory().getId());
        cv.put(ReminderUtil.KEY_DOSAGE,reminder.getDosage());
        cv.put(ReminderUtil.KEY_DURATION,reminder.getDuration());
        cv.put(ReminderUtil.KEY_INSTRUCTION,reminder.getInstruction());
        cv.put(ReminderUtil.KEY_TIME,reminder.getTime());
        cv.put(ReminderUtil.KEY_DATE_FROM,reminder.getFromDate());
        cv.put(ReminderUtil.KEY_DATE_TO,reminder.getToDate());
        cv.put(ReminderUtil.KEY_DAY_MARKER,reminder.getDayMarker());

        int done =  db.update(ReminderUtil.TABLE_NAME,cv,ReminderUtil.KEY_ID+"=?",
                new String[] {String.valueOf(reminder.getId())});
        db.close();
        return done;
    }

    public int deleteReminderEntry (int id) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();
        int done =  db.delete(ReminderUtil.TABLE_NAME,ReminderUtil.KEY_ID+"=?",
                new String[] {String.valueOf(id)});
        db.close();
        return done;
    }

    public Reminder getReminderEntry (int id) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cur = db.query(ReminderUtil.TABLE_NAME,null,ReminderUtil.KEY_ID+"=?",
                new String[]{String.valueOf(id)},null,null,null );
        if (cur.moveToNext()) {
            Inventory inventory = new InventoryDao(context).getSingleInventoryEntry(
                    cur.getInt(cur.getColumnIndex(ReminderUtil.KEY_INVENTORY_ID))
            );
            Reminder reminder = new Reminder(
                    cur.getInt(cur.getColumnIndex(ReminderUtil.KEY_ID)),
                    inventory,
                    cur.getString(cur.getColumnIndex(ReminderUtil.KEY_TIME)),
                    cur.getInt(cur.getColumnIndex(ReminderUtil.KEY_DOSAGE)),
                    cur.getInt(cur.getColumnIndex(ReminderUtil.KEY_INSTRUCTION)),
                    cur.getInt(cur.getColumnIndex(ReminderUtil.KEY_DURATION)),
                    cur.getString(cur.getColumnIndex(ReminderUtil.KEY_DATE_FROM)),
                    cur.getString(cur.getColumnIndex(ReminderUtil.KEY_DATE_TO)),
                    cur.getString(cur.getColumnIndex(ReminderUtil.KEY_DAY_MARKER))
            );
            cur.close();
            db.close();
            return reminder;
        }
        cur.close();
        db.close();
        return null;
    }

    public ArrayList<Reminder>  getAllReminderEntries () {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cur = db.query(ReminderUtil.TABLE_NAME,null,null,null,
                null,null,ReminderUtil.KEY_INVENTORY_ID+" ASC");
        ArrayList<Reminder> reminderList = new ArrayList<>();
        while (cur.moveToNext()) {
            Inventory inventory = new InventoryDao(context).getSingleInventoryEntry(
                    cur.getInt(cur.getColumnIndex(ReminderUtil.KEY_INVENTORY_ID))
            );
            Reminder reminder = new Reminder(
                    cur.getInt(cur.getColumnIndex(ReminderUtil.KEY_ID)),
                    inventory,
                    cur.getString(cur.getColumnIndex(ReminderUtil.KEY_TIME)),
                    cur.getInt(cur.getColumnIndex(ReminderUtil.KEY_DOSAGE)),
                    cur.getInt(cur.getColumnIndex(ReminderUtil.KEY_INSTRUCTION)),
                    cur.getInt(cur.getColumnIndex(ReminderUtil.KEY_DURATION)),
                    cur.getString(cur.getColumnIndex(ReminderUtil.KEY_DATE_FROM)),
                    cur.getString(cur.getColumnIndex(ReminderUtil.KEY_DATE_TO)),
                    cur.getString(cur.getColumnIndex(ReminderUtil.KEY_DAY_MARKER))

            );
            reminderList.add(reminder);
        }

        cur.close();
        db.close();
        return reminderList;
    }

    public int getLastAddedReminderId () {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT MAX("+ReminderUtil.KEY_ID+") FROM "+ReminderUtil.TABLE_NAME;
        Cursor cur = db.rawQuery(query,null);
        if (cur.moveToFirst()) {
            int id =  cur.getInt(0);
            cur.close();
            db.close();
            return id;
        }
        cur.close();
        db.close();
        return -1;
    }

    public long getReminderCountOfInventory (int invid) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, ReminderUtil.TABLE_NAME,
                ReminderUtil.KEY_INVENTORY_ID+"=?", new String[] {String.valueOf(invid)});
        db.close();
        return count;
    }
}
