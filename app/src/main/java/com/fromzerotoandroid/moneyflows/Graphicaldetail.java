package com.fromzerotoandroid.moneyflows;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GraphicalDetail extends AppCompatActivity {

    String nameCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphicaldetail_activity);

        Intent i = getIntent();
        nameCategory = i.getStringExtra("nameCategory");
    }
}
