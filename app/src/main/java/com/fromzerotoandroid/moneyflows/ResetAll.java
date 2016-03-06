package com.fromzerotoandroid.moneyflows;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class ResetAll extends AppCompatActivity {

    public final static String TAG = "RESETALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        Log.d(TAG, "getIntent");

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.resetall);
        dialog.setTitle("Erase data");
        dialog.show();

//        setContentView(R.layout.activity_reset_all);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void resetAllData(View v) {

        Log.d(TAG, "Clicked reset all data");

        BackgroundTask backgroundTask = new BackgroundTask(this);
        // The execute method trigger the doInBackground method in the backgroundtask
        backgroundTask.execute(FeedReaderContract.Methods.ERASE_ALL);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", RESULT_OK);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void dontResetAllData(View v) {

        Log.d(TAG, "Clicked NOT reset all data");
        finish();
    }


}
