package com.fromzerotoandroid.moneyflows;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ResetAll extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

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

        BackgroundTask backgroundTask = new BackgroundTask(this);
        // The execute method trigger the doInBackground method in the backgroundtask
        backgroundTask.execute(FeedReaderContract.Methods.ERASE_ALL);
        finish();
    }

    public void dontResetAllData(View v) {
        finish();
    }


}
