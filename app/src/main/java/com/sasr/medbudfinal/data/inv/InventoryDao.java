package com.sasr.medbudfinal.data.inv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sasr.medbudfinal.data.MedBudDatabaseHelper;
import com.sasr.medbudfinal.data.PillDao;
import com.sasr.medbudfinal.model.Inventory;
import com.sasr.medbudfinal.model.Pill;
import com.sasr.medbudfinal.util.InventoryUtil;
import com.sasr.medbudfinal.util.PillUtil;

import java.util.ArrayList;

public class InventoryDao {
    private static final String TAG = "InventoryDao";
    private Context context;
    private PillDao pillDao;

    public InventoryDao(Context context) {
        this.context = context;
        this.pillDao = new PillDao(context);
    }

    public long addInventoryEntry (Inventory inventory) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();
        pillDao.addPill(inventory.getPill(),db);
        Cursor cur = db.query(PillUtil.TABLE_NAME,
                new String[]{PillUtil.KEY_ID},
                PillUtil.KEY_NAME+"=?",
                new String[]{inventory.getPill().getName()},
                null,null,null);
        if (cur.moveToFirst()) {
            ContentValues cv = new ContentValues();
            cv.put(InventoryUtil.KEY_PILL_ID,cur.getInt(cur.getColumnIndex(PillUtil.KEY_ID)));
            cv.put(InventoryUtil.KEY_QUANTITY,inventory.getQuantity());
            cv.put(InventoryUtil.KEY_WARNING_QUANTITY,inventory.getWarningQuantity());
            cv.put(InventoryUtil.KEY_QUANTITY_UNIT,inventory.getQuantityUnit());
            cur.close();
            long id =  db.insert(InventoryUtil.TABLE_NAME,null,cv);
            db.close();
            return id;
        }
        cur.close();
        db.close();
        return -1;

    }

    public Inventory getSingleInventoryEntry (int id) {
        //Log.d(TAG, "getSingleInventoryEntry: "+id);
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cur = db.query(InventoryUtil.TABLE_NAME,null,InventoryUtil.KEY_ID+"=?",
                new String[] {String.valueOf(id)},null,null,null);
        if (cur.moveToFirst()) {
            Pill pill = pillDao.getSinglePill(cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_PILL_ID)),db);
            Inventory inventory = new Inventory(
                    cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_ID)),
                    pill,
                    cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_QUANTITY)),
                    cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_WARNING_QUANTITY)),
                    cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_QUANTITY_UNIT))
            );
            cur.close();
            db.close();
            return  inventory;
        }
        cur.close();
        db.close();
        return null;
    }

    public Inventory getSingleInventoryEntryByName (Inventory qinventory) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        Pill qpill = pillDao.getSinglePillByName (qinventory.getPill().getName(),db);
        Cursor cur = db.query(InventoryUtil.TABLE_NAME,null,InventoryUtil.KEY_ID+"=?",
                new String[] {String.valueOf(qpill.getId())},null,null,null);
        if (cur.moveToFirst()) {
            Pill pill = pillDao.getSinglePill(cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_PILL_ID)),db);
            Inventory inventory = new Inventory(
                    cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_ID)),
                    pill,
                    cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_QUANTITY)),
                    cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_WARNING_QUANTITY)),
                    cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_QUANTITY_UNIT))
            );
            cur.close();
            db.close();
            return  inventory;
        }
        cur.close();
        db.close();
        return null;
    }

    public int updateInventoryEntry (Inventory inventory) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();
        if (pillDao.updatePill(inventory.getPill(),db) > 0) {
            //Log.d(TAG, "updateInventoryEntry: " + inventory.getPill().getId() + " ," + inventory.getId());
            ContentValues cv = new ContentValues();
            cv.put(InventoryUtil.KEY_ID, inventory.getId());
            cv.put(InventoryUtil.KEY_PILL_ID, inventory.getPill().getId());
            cv.put(InventoryUtil.KEY_QUANTITY, inventory.getQuantity());
            cv.put(InventoryUtil.KEY_WARNING_QUANTITY, inventory.getWarningQuantity());
            cv.put(InventoryUtil.KEY_QUANTITY_UNIT, inventory.getQuantityUnit());

            int done =  db.update(InventoryUtil.TABLE_NAME, cv, InventoryUtil.KEY_ID + "=?",
                    new String[]{String.valueOf(inventory.getId())});
            db.close();
            return done;
        }
        db.close();
        return 0;
    }

    public int deleteInventoryEntry (int id) {
        Inventory inventory = getSingleInventoryEntry(id);
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();
        pillDao.deletePill(inventory.getPill().getId(),db);
        int done =  db.delete(InventoryUtil.TABLE_NAME,InventoryUtil.KEY_ID+" =?",
                new String[] {String.valueOf(id)});
        db.close();
        return done;
    }

    public ArrayList<Inventory> getAllInventoryEntries () {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cur = db.query(InventoryUtil.TABLE_NAME,null,null,null,
                null,null,null);
        ArrayList<Inventory> inventoryLIst = new ArrayList<>();
        while (cur.moveToNext()) {
            Inventory inventory = new Inventory();
            inventory.setId(cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_ID)));
            inventory.setPill(pillDao.getSinglePill(cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_PILL_ID)),db));
            inventory.setQuantity(cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_QUANTITY)));
            inventory.setWarningQuantity(cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_WARNING_QUANTITY)));
            inventory.setQuantityUnit(cur.getInt(cur.getColumnIndex(InventoryUtil.KEY_QUANTITY_UNIT)));
            inventoryLIst.add(inventory);
        }
        cur.close();
        db.close();
        return inventoryLIst;
    }

    public int getInventorySize() {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, InventoryUtil.TABLE_NAME);
        closeDB();
        return (int) count;
    }

    public void closeDB () {
        MedBudDatabaseHelper.getInstance(context).closeDb();
    }
}
