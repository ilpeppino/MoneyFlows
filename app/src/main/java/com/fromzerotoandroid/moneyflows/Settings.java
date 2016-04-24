package com.fromzerotoandroid.moneyflows;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by ilpep on 3/11/2016.
 */
public class Settings extends AppCompatActivity {

    public static final String TAG = "Class: Settings";

    Spinner s;
    ArrayAdapter<String> spinner_adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        Log.d(TAG, "Receving intent...");

        s = (Spinner) findViewById(R.id.spinner_daytoreset);
        spinner_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, Helper.DAY_NUMBER);
        s.setAdapter(spinner_adapter);

    }
}
