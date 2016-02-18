package com.fromzerotoandroid.moneyflows;

import android.provider.BaseColumns;

/**
 * Created by ilpep on 2/18/2016.
 */
public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FeedReaderContract() {
    }

    /* Inner class that defines the table contents
    You need to implement BaseColumns
    **/
    public static abstract class CostEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DATE = "date";

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    // In SQLLite, there is no DATE type. It can be TEXT for example,
    // formatted like YYYY-MM-DD HH:MM:SS.SSS

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CostEntry.TABLE_NAME + " (" +
                    CostEntry._ID + INT_TYPE + " PRIMARY KEY NOT NULL," +
                    CostEntry.COLUMN_NAME_COST + REAL_TYPE + COMMA_SEP +
                    CostEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    CostEntry.COLUMN_NAME_DATE + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CostEntry.TABLE_NAME;
}

