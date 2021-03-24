package com.sasr.medbudfinal.util;

public final class ReminderUtil {
    private ReminderUtil () {}

    //table info
    public static final String TABLE_NAME = "reminder_details";
    public static final String KEY_ID = "id";
    public static final String KEY_INVENTORY_ID = "inventory_id";
    public static final String KEY_TIME = "time";
    public static final String KEY_INSTRUCTION = "instruction";
    public static final String KEY_DOSAGE = "dosage";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_DATE_FROM = "from_date";
    public static final String KEY_DATE_TO = "to_date";
    public static final String KEY_DAY_MARKER = "day_marker";

    //sql statement
    public static final String CREATE_STRING = "CREATE TABLE "+TABLE_NAME
            +"("+KEY_ID+" INTEGER PRIMARY KEY,"
            +KEY_INVENTORY_ID+" INTEGER,"
            +KEY_TIME+" TEXT,"
            +KEY_INSTRUCTION+" TEXT,"
            +KEY_DOSAGE+" INTEGER,"
            +KEY_DURATION+" INTEGER,"
            +KEY_DATE_FROM+" TEXT,"
            +KEY_DATE_TO+" TEXT,"
            +KEY_DAY_MARKER+" TEXT,"
            +"FOREIGN KEY ("+KEY_INVENTORY_ID+") REFERENCES "+InventoryUtil.TABLE_NAME+"("+InventoryUtil.KEY_ID+")"
            +")";

    public static final String DROP_STRING = "DROP TABLE IF EXISTS "+TABLE_NAME;

}
