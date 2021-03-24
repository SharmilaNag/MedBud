package com.sasr.medbudfinal.util;

public final class PillUtil {
    private PillUtil () {}

    //pill table details
    public static final String TABLE_NAME = "pill_details";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TYPE = "type";

    public static final String CREATE_STRING = "CREATE TABLE "+TABLE_NAME+"("
            +KEY_ID+" INTEGER PRIMARY KEY,"
            +KEY_NAME+" TEXT,"
            +KEY_TYPE+" INTEGER"
            +")";

    public static final String DROP_STRING = "DROP TABLE IF EXISTS "+TABLE_NAME;


}
