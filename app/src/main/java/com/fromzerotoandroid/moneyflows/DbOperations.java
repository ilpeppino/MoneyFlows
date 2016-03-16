package com.fromzerotoandroid.moneyflows;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ilpep on 2/18/2016.
 */
public class DbOperations extends SQLiteOpenHelper {


    private static final String TAG = "Class: DbOperations";
    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "cost_history.db";

    private static final String CREATE_TABLE =
            "create table " + FeedReaderContract.CostEntry.TABLE_NAME + "(" +
                    FeedReaderContract.CostEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_COST + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_CATEGORY + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_DATE + " text);";

    private static final String DELETE_TABLE = "DELETE FROM ";

    // This constructor MUST be defined
    public DbOperations(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

    }

    // on Create and onUpgrade must be defined
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
        Log.d(TAG, "Creating table...");
    }

    public void addRowToTable(SQLiteDatabase db, String cost, String desc, String category, String date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_COST, cost);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION, desc);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_CATEGORY, category);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_DATE, date);
        db.insert(FeedReaderContract.CostEntry.TABLE_NAME, null, contentValues);
        Log.d(TAG, "Row inserted in the table...");
    }

    public void purgeTable(SQLiteDatabase db, String tableNameToPurge) {

        Log.d(TAG, "Table purged");
        db.delete(tableNameToPurge, null, null);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
