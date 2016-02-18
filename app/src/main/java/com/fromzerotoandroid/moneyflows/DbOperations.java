package com.fromzerotoandroid.moneyflows;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ilpep on 2/18/2016.
 */
public class DbOperations extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "cost_history.db";

    // This constructor MUST be defined
    public DbOperations(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // on Create and onUpgrade must be defined
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
