package com.sasr.medbudfinal.data.userinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sasr.medbudfinal.data.MedBudDatabaseHelper;
import com.sasr.medbudfinal.model.UserInfo;
import com.sasr.medbudfinal.util.UserInfoUtil;

public class UserInfoDao {
    private static final String TAG = "UserInfoDao";
    private Context context;

    public UserInfoDao(Context context) {
        this.context = context;
    }

    public long inserUserData (UserInfo userInfo) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(UserInfoUtil.KEY_ID,userInfo.getId());
        cv.put(UserInfoUtil.KEY_EMAIL,userInfo.getEmail());
        cv.put(UserInfoUtil.KEY_NAME,userInfo.getName());
        cv.put(UserInfoUtil.KEY_AGE,userInfo.getAge());
        cv.put(UserInfoUtil.KEY_GENDER,userInfo.getGender());
        cv.put(UserInfoUtil.KEY_DOCTOR_NAME,userInfo.getDoctorName());
        cv.put(UserInfoUtil.KEY_MEDICAL_HISTORY,userInfo.getMedicalHistory());
        cv.put(UserInfoUtil.KEY_PRESCRIPTION,userInfo.getPrescriptionImage());
        long id = db.insert(UserInfoUtil.TABLE_NAME,null,cv);
        db.close();
        return id;
    }

    public int updateUserData (UserInfo userInfo) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(UserInfoUtil.KEY_NAME,userInfo.getName());
        cv.put(UserInfoUtil.KEY_AGE,userInfo.getAge());
        cv.put(UserInfoUtil.KEY_GENDER,userInfo.getGender());
        cv.put(UserInfoUtil.KEY_DOCTOR_NAME,userInfo.getDoctorName());
        cv.put(UserInfoUtil.KEY_MEDICAL_HISTORY,userInfo.getMedicalHistory());
        cv.put(UserInfoUtil.KEY_PRESCRIPTION,userInfo.getPrescriptionImage());
        int done = db.update(UserInfoUtil.TABLE_NAME,cv,UserInfoUtil.KEY_ID+"=?",
                new String[]{userInfo.getId()});
        db.close();
        return done;
    }

    public int deleteUserData (UserInfo userInfo) {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getWritableDatabase();
        int done = db.delete(UserInfoUtil.TABLE_NAME,UserInfoUtil.KEY_ID+"=?",new String[]{userInfo.getId()});
        db.close();
        return done;
    }

    public UserInfo getUserData () {
        SQLiteDatabase db = MedBudDatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cur = db.query(UserInfoUtil.TABLE_NAME,null,null,null,null,null,null);
        UserInfo userInfo = null;
        if (cur.moveToNext()) {
            userInfo = new UserInfo();
            userInfo.setId(cur.getString(cur.getColumnIndex(UserInfoUtil.KEY_ID)));
            userInfo.setEmail(cur.getString(cur.getColumnIndex(UserInfoUtil.KEY_EMAIL)));
            userInfo.setName(cur.getString(cur.getColumnIndex(UserInfoUtil.KEY_NAME)));
            userInfo.setAge(cur.getInt(cur.getColumnIndex(UserInfoUtil.KEY_AGE)));
            userInfo.setGender(cur.getInt(cur.getColumnIndex(UserInfoUtil.KEY_GENDER)));
            userInfo.setDoctorName(cur.getString(cur.getColumnIndex(UserInfoUtil.KEY_DOCTOR_NAME)));
            userInfo.setMedicalHistory(cur.getString(cur.getColumnIndex(UserInfoUtil.KEY_MEDICAL_HISTORY)));
            userInfo.setPrescriptionImage(cur.getBlob(cur.getColumnIndex(UserInfoUtil.KEY_PRESCRIPTION)));
        }
        cur.close();
        db.close();
        return userInfo;
    }
}
