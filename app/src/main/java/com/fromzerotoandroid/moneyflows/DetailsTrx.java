package com.fromzerotoandroid.moneyflows;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

public class DetailsTrx extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_trx);
        Toolbar toolbar = (Toolbar) findViewById(R.id.details_trx_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get intent from History list when Update is selected in context menu
        Intent myIntent = getIntent();
        String cost = myIntent.getStringExtra("Cost");
        String description = myIntent.getStringExtra("Description");
        String category = myIntent.getStringExtra("Category");
        String date = myIntent.getStringExtra("Date");

        // Hook the edit texts in DetailsTrx layout
        EditText editTextCost = (EditText) findViewById(R.id.details_trx_cost);
        EditText editTextDescription = (EditText) findViewById(R.id.details_trx_description);
        TextView textViewCategory = (TextView) findViewById(R.id.details_trx_category);
        TextView textViewDate = (TextView) findViewById(R.id.details_trx_date);

        // Get index for category
        int ind = Arrays.asList(Helper.categoryNames).indexOf(category);


        // Set text from clicked item to be updated
        editTextCost.setText(cost, TextView.BufferType.EDITABLE);
        editTextCost.setSelectAllOnFocus(true);
        editTextDescription.setText(description, TextView.BufferType.EDITABLE);
        editTextDescription.setSelectAllOnFocus(true);
        textViewCategory.setText(category);
        textViewCategory.setBackgroundResource(Helper.categoryColors[ind]);
        textViewDate.setText(Helper.formatDate("yyyyMMdd", "dd-MM-yyyy", date));


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

}
