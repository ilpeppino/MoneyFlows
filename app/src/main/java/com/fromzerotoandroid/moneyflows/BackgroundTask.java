package com.fromzerotoandroid.moneyflows;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

//The 4 steps
//        When an asynchronous task is executed, the task goes through 4 steps:
//        onPreExecute(), invoked on the UI thread before the task is executed. This step is normally used to setup the task, for instance by showing a progress bar in the user interface.
//        doInBackground(Params...), invoked on the background thread immediately after onPreExecute() finishes executing. This step is used to perform background computation that can take a long time. The parameters of the asynchronous task are passed to this step. The result of the computation must be returned by this step and will be passed back to the last step. This step can also use publishProgress(Progress...) to publish one or more units of progress. These values are published on the UI thread, in the onProgressUpdate(Progress...) step.
//        onProgressUpdate(Progress...), invoked on the UI thread after a call to publishProgress(Progress...). The timing of the execution is undefined. This method is used to display any form of progress in the user interface while the background computation is still executing. For instance, it can be used to animate a progress bar or show logs in a text field.
//        onPostExecute(Result), invoked on the UI thread after the background computation finishes. The result of the background computation is passed to this step as a parameter.
//
//        Async task parameters
//        1. Type of input for doInBackground
//        2.
//        3. Type of return for doInBackgroud

/**
 * Created by ilpep on 2/19/2016.
 */

public class BackgroundTask extends AsyncTask<String, Void, String> {

    public static final String TAG = "Class: BackgroundTask";

    Context c;
    String mIdTimestamp, mCost, mDescription, mCategory, mDate;
    int mPosition;
    //DbOperations.TransactionAtRowId trx;


    BackgroundTask(Context context) {

        this.c = context;

    }


    @Override
    protected void onPreExecute() {

        Log.d(TAG, "onPreExecute");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        Log.d(TAG, "doInBackground");

        // Get the method name as first parameter
        String method = params[0];
        String toReturn = "";
        DbOperations dbOperations = new DbOperations(c);
        SQLiteDatabase db = dbOperations.getWritableDatabase();

        switch (method) {
            // Add the cost in the db
            case FeedReaderContract.Methods.ADD_COST:

                Log.d(TAG, "Database operation: ADD_COST");

                mIdTimestamp = params[1];
                mCost = params[2];
                mDescription = params[3];
                mCategory = params[4];
                mDate = params[5];


                dbOperations.addRowToTable(db, mIdTimestamp, mCost, mDescription, mCategory, mDate);
                dbOperations.close();

                toReturn = "One row inserted...";
                break;

            case FeedReaderContract.Methods.ERASE_ALL:

                Log.d(TAG, "Database operation: RESET_ALL");
//
//                // Purge the table from data
                dbOperations.purgeTable(db, FeedReaderContract.CostEntry.TABLE_NAME);
                dbOperations.close();


                toReturn = "Data cleared...";


                break;

            case FeedReaderContract.Methods.DELETE_ROW:
                Log.d(TAG, "Database operation: DELETE_ROW");

                mIdTimestamp = params[1];

                dbOperations.deleteRowFromTable(db, mIdTimestamp);
                dbOperations.close();

                toReturn = "Row deleted...";
                break;


            case FeedReaderContract.Methods.UPDATE_ROW:

                mIdTimestamp = params[1];
                // mPosition = Integer.valueOf(params[1]);
                mCost = params[2];
                mDescription = params[3];
                mCategory = params[4];
                mDate = params[5];


                dbOperations.updateRow(db, mIdTimestamp, mCost, mDescription, mCategory, mDate);
                dbOperations.close();

                toReturn = "Row updated...";
                break;
        }
        db.close();
        return toReturn;
    }

    @Override
    protected void onProgressUpdate(Void... values) {

        Log.d(TAG, "onProgressUpdate");
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute");
        Toast.makeText(c, result, Toast.LENGTH_SHORT).show();
    }


}
