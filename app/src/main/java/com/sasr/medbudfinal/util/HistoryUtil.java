package com.sasr.medbudfinal.util;

public final class HistoryUtil {
    private HistoryUtil(){}

    //table info
    public static final String TABLE_NAME = "history";

    public static final String KEY_ID = "id";
    public static final String KEY_MED_NAME = "med_name";
    public static final String KEY_MED_TYPE = "med_type";
    public static final String KEY_QUANTITY_TAKEN = "quantity_taken";
    public static final String KEY_DUE_TIME = "due_time";
    public static final String KEY_DUE_DATE = "due_date";
    public static final String KEY_DUE_DAY = "due_day";
    public static final String KEY_TAKE_TIME = "take_time";
    public static final String KEY_TAKE_DATE = "take_date";
    public static final String KEY_TAKE_DAY = "take_day";

    public static final String CREATE_STRING = "CREATE TABLE "+TABLE_NAME+"("
            +KEY_ID+" INTEGER PRIMARY KEY,"
            +KEY_MED_NAME+" TEXT,"
            +KEY_MED_TYPE+" INTEGER,"
            +KEY_QUANTITY_TAKEN+" TEXT,"
            +KEY_DUE_TIME+" TEXT,"
            +KEY_DUE_DATE+" TEXT,"
            +KEY_DUE_DAY+" TEXT,"
            +KEY_TAKE_TIME+" TEXT,"
            +KEY_TAKE_DATE+" TEXT,"
            +KEY_TAKE_DAY+" TEXT"
            +")";
    public static final String DROP_STRING = "DROP TABLE IF EXISTS "+TABLE_NAME;


}
