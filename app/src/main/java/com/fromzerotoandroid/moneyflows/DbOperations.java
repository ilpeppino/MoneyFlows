package com.fromzerotoandroid.moneyflows;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ilpep on 2/18/2016.
 */
public class DbOperations extends SQLiteOpenHelper {


    public static final String DB_NAME = "cost_history.db";
    private static final String TAG = "Class: DbOperations";
    private static final int DB_VERSION = 1;
    private static final String CREATE_TABLE_COST_HISTORY =
            "create table " + FeedReaderContract.CostEntry.TABLE_NAME + "(" +
                    FeedReaderContract.CostEntry.COLUMN_NAME_TIMESTAMP + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_COST + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_CATEGORY + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_DATE + " text);";

    private static final String CREATE_TABLE_COST_HISTORY_ARCHIVE =
            "create table " + FeedReaderContract.CostEntryArchive.TABLE_NAME + "(" +
                    FeedReaderContract.CostEntryArchive.COLUMN_NAME_TIMESTAMP + " text, " +
                    FeedReaderContract.CostEntryArchive.COLUMN_NAME_COST + " text, " +
                    FeedReaderContract.CostEntryArchive.COLUMN_NAME_DESCRIPTION + " text, " +
                    FeedReaderContract.CostEntryArchive.COLUMN_NAME_CATEGORY + " text, " +
                    FeedReaderContract.CostEntryArchive.COLUMN_NAME_DATE + " text);";

    private static final String CREATE_TABLE_USER_SETTINGS =
            "create table " + FeedReaderContract.UserSettings.TABLE_NAME + "(" +
                    FeedReaderContract.UserSettings.COLUMN_NAME_CURRENTACCESS + " text, " +
                    FeedReaderContract.UserSettings.COLUMN_NAME_LASTACCESS + " text, " +
                    FeedReaderContract.UserSettings.COLUMN_NAME_RESETDAY + " text, " +
                    FeedReaderContract.UserSettings.COLUMN_NAME_LASTBACKUP + " text);";

//    private static final String DELETE_TABLE = "DELETE FROM ";

//    String rowidAtPosition,
//            categoryAtPosition,
//            costAtPosition,
//            dateAtPosition,
//            descriptionAtPosition;



    // This constructor MUST be defined
    public DbOperations(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

    }

    // on Create and onUpgrade must be defined
    @Override
    public void onCreate(SQLiteDatabase db) {


        try {
            db.execSQL(CREATE_TABLE_COST_HISTORY);
        } catch (Exception e) {
            Log.d(TAG, "Error when creating table COST_HISTORY");
        }

        try {
            db.execSQL(CREATE_TABLE_COST_HISTORY_ARCHIVE);
        } catch (Exception e) {
            Log.d(TAG, "Error when creating table COST_HISTORY_ARCHIVE");
        }

        try {
            db.execSQL(CREATE_TABLE_USER_SETTINGS);
        } catch (Exception e) {
            Log.d(TAG, "Error when creating table USER_SETTINGS");
        }



        Log.d(TAG, "Creating table...");
    }

    public void addRowToTable(SQLiteDatabase db, String idTimestamp, String cost, String desc, String category, String date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_TIMESTAMP, idTimestamp);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_COST, cost);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION, desc);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_CATEGORY, category);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_DATE, date);

        db.insert(FeedReaderContract.CostEntry.TABLE_NAME, null, contentValues);

        Log.d(TAG, "Row inserted in the table...");
    }

    public void purgeTable(SQLiteDatabase db, String tableNameToPurge) {


        db.delete(tableNameToPurge, null, null);

        Log.d(TAG, "Table purged");
    }

    public void deleteRowFromTable(SQLiteDatabase db, String idTimestamp) {

        // TransactionAtRowId trx = moveCursorToRowId(db, position);
        String[] args = {idTimestamp};
        db.delete(FeedReaderContract.CostEntry.TABLE_NAME, FeedReaderContract.CostEntry.COLUMN_NAME_TIMESTAMP + "=?", args);

    }

    public void setupUserSettings(SQLiteDatabase db, String dayOfToday) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedReaderContract.UserSettings.COLUMN_NAME_CURRENTACCESS, dayOfToday);
        contentValues.put(FeedReaderContract.UserSettings.COLUMN_NAME_LASTACCESS, dayOfToday);
        contentValues.put(FeedReaderContract.UserSettings.COLUMN_NAME_LASTBACKUP, dayOfToday);
        contentValues.put(FeedReaderContract.UserSettings.COLUMN_NAME_RESETDAY, "");
        db.insert(FeedReaderContract.UserSettings.TABLE_NAME, null, contentValues);
    }

    public String getLastBackup() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true, FeedReaderContract.UserSettings.TABLE_NAME, new String[]{FeedReaderContract.UserSettings.COLUMN_NAME_LASTBACKUP}, null, null, null, null, null, null);
        return cursor.getString(cursor.getColumnIndex(FeedReaderContract.UserSettings.COLUMN_NAME_LASTBACKUP));
    }

    public void updateRow(SQLiteDatabase db, String idTimestamp, String newCost, String newDescription, String category, String date) {
        // TODO implement update row database operation
        // TransactionAtRowId trx = moveCursorToRowId(db, position);
        String[] args = {idTimestamp};
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_TIMESTAMP, idTimestamp);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_COST, newCost);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION, newDescription);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_CATEGORY, category);
        String formattedDate = Helper.formatDate("dd-MM-yyyy", "yyyyMMdd", date);
        contentValues.put(FeedReaderContract.CostEntry.COLUMN_NAME_DATE, formattedDate);
        db.update(FeedReaderContract.CostEntry.TABLE_NAME, contentValues, FeedReaderContract.CostEntry.COLUMN_NAME_TIMESTAMP + "=?", args);
    }

    public String[] getAllDescriptions() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true, FeedReaderContract.CostEntry.TABLE_NAME, new String[]{FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION}, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            String[] str = new String[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                str[i] = cursor.getString(cursor.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION));
                i++;
            }
            cursor.close();
            return str;
        } else {
            cursor.close();
            return new String[]{};
        }


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }


}
