package com.fromzerotoandroid.moneyflows;

import android.provider.BaseColumns;

// A contract class is a container for constants that define names for URIs, tables, and columns.
// The contract class allows you to use the same constants across all the other classes in the same package.
// This lets you change a column name in one place and have it propagate throughout your code.
// A good way to organize a contract class is to put definitions that are global to your whole database in the root level of the class.
// Then create an inner class for each table that enumerates its columns.


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

    // Table definition for the cost entries
    public static abstract class CostEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_TIMESTAMP = "id_ts";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_DATE = "date";

    }

    // Table definition for the cost entries archive
    public static abstract class CostEntryArchive implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_TIMESTAMP = "id_ts";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_DATE = "date";

    }

    public static abstract class UserSettings implements BaseColumns {
        public static final String TABLE_NAME = "usersettings";
        public static final String COLUMN_NAME_RESETDAY = "resetday";
        public static final String COLUMN_NAME_LASTACCESS = "lastaccess";
        public static final String COLUMN_NAME_CURRENTACCESS = "curraccess";
        public static final String COLUMN_NAME_LASTBACKUP = "lastbackup";
    }

    public static abstract class Methods {

        public static final String ADD_COST = "add_cost";
        public static final String PREPAREFORFIRSTUSAGE = "prepareforfirstusage";
        public static final String DELETE_ROW = "delete_row";
        public static final String UPDATE_ROW = "update_row";
        public static final String GETLASTBACKUP = "getlastbackup";
        public static final String SETUPUSERSETTINGS = "setupusersettings";


    }

//    private static final String TEXT_TYPE = " TEXT";
//    private static final String INT_TYPE = " INT";
//    private static final String REAL_TYPE = " REAL";
//    private static final String COMMA_SEP = ",";

    // In SQLLite, there is no DATE type. It can be TEXT for example,
    // formatted like YYYY-MM-DD HH:MM:SS.SSS

//    private static final String SQL_CREATE_ENTRIES =
//            "CREATE TABLE " + CostEntry.TABLE_NAME + " (" +
//                    CostEntry._ID + INT_TYPE + " PRIMARY KEY NOT NULL," +
//                    CostEntry.COLUMN_NAME_COST + REAL_TYPE + COMMA_SEP +
//                    CostEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
//                    CostEntry.COLUMN_NAME_DATE + TEXT_TYPE +
//                    " )";
//
//    private static final String SQL_DELETE_ENTRIES =
//            "DROP TABLE IF EXISTS " + CostEntry.TABLE_NAME;
}

