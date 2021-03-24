package com.sasr.medbudfinal.util;

public final class InventoryUtil {
    private InventoryUtil (){}

    //inventory table details
    public static final String TABLE_NAME = "inventory_details";
    public static final String KEY_ID = "id";
    public static final String KEY_PILL_ID = "pill_id";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_WARNING_QUANTITY = "warning_quantity";
    public static final String KEY_QUANTITY_UNIT = "quantity_unit";

    public static final String CREATE_STRING = "CREATE TABLE "+TABLE_NAME+" ("
            +KEY_ID+" INTEGER PRIMARY KEY,"
            +KEY_PILL_ID+" INTEGER,"
            +KEY_QUANTITY+" INTEGER,"
            +KEY_WARNING_QUANTITY+" INTEGER,"
            +KEY_QUANTITY_UNIT+" INTEGER,"
            +"FOREIGN KEY("+KEY_PILL_ID+") REFERENCES "+PillUtil.TABLE_NAME+"("+PillUtil.KEY_ID+")"
            +")";

    public static final String DROP_STRING = "DROP TABLE IF EXISTS "+TABLE_NAME;

}
