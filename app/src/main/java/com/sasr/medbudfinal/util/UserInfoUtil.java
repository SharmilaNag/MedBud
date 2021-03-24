package com.sasr.medbudfinal.util;

public final class UserInfoUtil {
    private UserInfoUtil(){}

    //table info
    public static final String TABLE_NAME = "user_info";
    public static final String KEY_ID = "id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_AGE = "age";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_DOCTOR_NAME = "doctor_name";
    public static final String KEY_MEDICAL_HISTORY = "medical_history";
    public static final String KEY_PRESCRIPTION = "prescription";

    public static final String CREATE_STRING = "CREATE TABLE "+TABLE_NAME+"("
            +KEY_ID+" TEXT,"
            +KEY_EMAIL+" TEXT,"
            +KEY_NAME+" TEXT,"
            +KEY_AGE+" INTEGER,"
            +KEY_GENDER+" INTEGER,"
            +KEY_DOCTOR_NAME+" TEXT,"
            +KEY_MEDICAL_HISTORY+" TEXT,"
            +KEY_PRESCRIPTION+" BLOB"
            +")";
    public static final String DROP_STRING = "DROP TABLE IF EXISTS "+TABLE_NAME;
}
