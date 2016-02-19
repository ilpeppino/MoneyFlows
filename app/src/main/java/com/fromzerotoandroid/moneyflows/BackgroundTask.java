package com.fromzerotoandroid.moneyflows;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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

    Context c;
    String mCost, mDescription, mDate;

    BackgroundTask(Context context) {

        this.c = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String method = params[0];
        DbOperations dbOperations = new DbOperations(c);


        if (method.equals("add_cost")) {

            mCost = params[1];
            mDescription = params[2];
            mDate = params[3];
            SQLiteDatabase db = dbOperations.getWritableDatabase();


            dbOperations.addRowToTable(db, mCost, mDescription, mDate);


        }

        return "One row inserted";

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(c, result, Toast.LENGTH_LONG).show();
    }
}
