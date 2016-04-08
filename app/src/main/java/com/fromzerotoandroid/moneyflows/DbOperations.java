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
    private static final String CREATE_TABLE =
            "create table " + FeedReaderContract.CostEntry.TABLE_NAME + "(" +
                    FeedReaderContract.CostEntry.COLUMN_NAME_TIMESTAMP + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_COST + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_CATEGORY + " text, " +
                    FeedReaderContract.CostEntry.COLUMN_NAME_DATE + " text);";

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


        db.execSQL(CREATE_TABLE);


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

//    public TransactionAtRowId moveCursorToRowId(SQLiteDatabase db, int position) {
//
//        // TransactionAtRowId trx = new TransactionAtRowId();
//        Cursor c1 = db.rawQuery("SELECT _ROWID_,* FROM HISTORY", null);
//        c1.moveToPosition(position);
//        rowidAtPosition = c1.getString(c1.getColumnIndex("rowid"));
//        categoryAtPosition = c1.getString(c1.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_CATEGORY));
//        costAtPosition = c1.getString(c1.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_COST));
//        descriptionAtPosition = c1.getString(c1.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION));
//        dateAtPosition = c1.getString(c1.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DATE));
//
//        TransactionAtRowId trx = new TransactionAtRowId(rowidAtPosition, categoryAtPosition, costAtPosition, dateAtPosition, descriptionAtPosition);
//
//        return trx;
//
//    }

//    public TransactionAtRowId getTransactionAtRowId() {
//
//        return new TransactionAtRowId(rowidAtPosition, categoryAtPosition, costAtPosition, dateAtPosition, descriptionAtPosition);
//
//    }

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

//    public class TransactionAtRowId {
//
//        String rowidAtPosition,
//                categoryAtPosition,
//                costAtPosition,
//                dateAtPosition,
//                descriptionAtPosition;
//
//        TransactionAtRowId() {
//            this.categoryAtPosition = "";
//            this.rowidAtPosition = "";
//            this.costAtPosition = "";
//            this.dateAtPosition = "";
//            this.descriptionAtPosition = "";
//        }
//
//        TransactionAtRowId(String rowid, String category, String cost, String date, String description) {
//
//            this.rowidAtPosition = rowid;
//            this.categoryAtPosition = category;
//            this.costAtPosition = cost;
//            this.dateAtPosition = date;
//            this.descriptionAtPosition = description;
//
//        }
//
//    }


}
