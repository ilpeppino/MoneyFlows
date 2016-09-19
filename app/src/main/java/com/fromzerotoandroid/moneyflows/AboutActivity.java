package com.fromzerotoandroid.moneyflows;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.about);
        dialog.setTitle("About");
        dialog.show();

//        setContentView(R.details_trx_activity.activity_about);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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

    public void closeDialogWindow(View v) {
        finish();
    }

}
