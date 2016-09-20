package com.fromzerotoandroid.moneyflows;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;


public class UpdateTrxNew extends AppCompatActivity {

    public static final String TAG = "UpdateTransaction";
    String position, IdTimestamp, cost, description, category, date, currentDate;

    EditText editTextCost, editTextDescription;
    Spinner spCategory;
    CalendarView cvDate;

    String selectedDate, selectedDateForDb;
    Calendar selectedCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_trx_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.details_trx_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get intent from History list when Update is selected in context menu
        Intent myIntent = getIntent();
        IdTimestamp = myIntent.getStringExtra("IdTimestamp");
        cost = myIntent.getStringExtra("Cost");
        description = myIntent.getStringExtra("Description");
        category = myIntent.getStringExtra("Category");
        date = myIntent.getStringExtra("Date");

        // Hook the edit texts in UpdateTransaction content_details_trx
        editTextCost = (EditText) findViewById(R.id.details_trx_cost);
        editTextDescription = (EditText) findViewById(R.id.details_trx_description);
        spCategory = (Spinner) findViewById(R.id.details_trx_category);
        cvDate = (CalendarView) findViewById(R.id.details_trx_date);

        // Get index for category
        int ind = Arrays.asList(Helper.CATEGORY_NAMES).indexOf(category);


        // Set text from clicked item to be updated
        editTextCost.setText(cost, TextView.BufferType.EDITABLE);
        editTextCost.setSelectAllOnFocus(true);
        editTextDescription.setText(description, TextView.BufferType.EDITABLE);
        editTextDescription.setSelectAllOnFocus(true);
        ArrayAdapter<String> ArrAdpt = new ArrayAdapter<String>(getBaseContext(), R.layout.mainactivity_spinner, Helper.CATEGORY_NAMES);
        spCategory.setAdapter(ArrAdpt);

        String parts[] = date.split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        String monthAdjusted = String.valueOf(month);
        if (monthAdjusted.length() == 1) {
            monthAdjusted = '0' + monthAdjusted;
        }
        currentDate = String.valueOf(year) + monthAdjusted + String.valueOf(day);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long milliTime = calendar.getTimeInMillis();
        cvDate.setDate(milliTime);


        cvDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat dateFormatDb = new SimpleDateFormat("yyyyMMdd");
                selectedCal = cal;
                selectedDateForDb = dateFormatDb.format(cal.getTimeInMillis());
                selectedDate = dateFormat.format(cal.getTimeInMillis());

            }
        });


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    public void onUpdateTrxClick(View v) {


        Log.d(TAG, "Clicked update transaction");

        editTextCost = (EditText) findViewById(R.id.details_trx_cost);
        editTextDescription = (EditText) findViewById(R.id.details_trx_description);
        String newCost = editTextCost.getText().toString();
        String newDescription = editTextDescription.getText().toString();
        String newCategory = spCategory.getSelectedItem().toString();

        BackgroundTask backgroundTask = new BackgroundTask(this);
        // The execute method trigger the doInBackground method in the backgroundtask
        String newDate;
        if (selectedCal == null) {
            backgroundTask.execute(FeedReaderContract.Methods.UPDATE_ROW, IdTimestamp, newCost, newDescription, newCategory, date);
        } else {
            backgroundTask.execute(FeedReaderContract.Methods.UPDATE_ROW, IdTimestamp, newCost, newDescription, newCategory, selectedDate);
        }
        Intent returnIntent = new Intent();


        returnIntent.putExtra("result", RESULT_OK);

        returnIntent.putExtra("newCost", newCost);
        returnIntent.putExtra("newDescription", newDescription);
        returnIntent.putExtra("newCategory", newCategory);


        if (selectedCal == null) {
            returnIntent.putExtra("newDate", date);
        } else {
            returnIntent.putExtra("newDate", selectedDate);
        }

        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }
}
