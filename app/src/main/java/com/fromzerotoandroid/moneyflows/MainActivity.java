package com.fromzerotoandroid.moneyflows;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.GraphicalView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // Constants for logging tags
    public static final String TAG = "Class: MainActivity";
    // Defines request codes for intents
    public static final int REQUEST_CODE_RESET_ALL = 1;
    public static final int REQUEST_CODE_SETTINGS = 2;
    public static final int DIALOG_ID = 0;
    // SharedPreferences, editors and names definition
    int accessnumber;
    ArrayAdapter<String> desc_adapter;
    // Only for testing purposes
    private boolean simulateFirstUse = false;

    // Shared preferences vars

    private SharedPreferences spUserSettings, spValuesCategory;


    // Object references to cost and description in the main mGraphicalLayout screen
    private EditText et_Cost;
    private AutoCompleteTextView et_Description;
    private String mCost, mDescription, selectedDate;
    private Button btnDateDialog;
    private int dpYear, dpMonth, dpDay;
    private GraphicalView mChartView;
    private LinearLayout mGraphicalLayout;
    private Spinner s;
    private GraphicalObject mGraphicalObject;

    // it stores the category names and the index when a cost is added
    private int index;
    private float[] array_categoryValues = new float[Helper.TOTALNRCATEGORIES];
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dpYear = year;
            dpMonth = monthOfYear + 1;
            dpDay = dayOfMonth;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Creates the mGraphicalLayout and toolbar for the main mGraphicalLayout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_activity);

        spUserSettings = this.getSharedPreferences(Helper.USERS_SETTINGS, Context.MODE_PRIVATE);
        spValuesCategory = this.getSharedPreferences(Helper.VALUES_CATEGORY, Context.MODE_PRIVATE);

        final Calendar cal = Calendar.getInstance();
        dpYear = cal.get(Calendar.YEAR);
        dpMonth = cal.get(Calendar.MONTH) + 1;
        dpDay = cal.get(Calendar.DAY_OF_MONTH);
        showDialogDateOnButtonClick();

        // Toolbar is defined in mainactivity_activityactivity.xml
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu(); // NOTE: this method shows the menu on top-right corner
        setSupportActionBar(toolbar);
        Log.d(TAG, "onCreate");

        // gets all the categorynames from strings.xml
        // Resources r = getResources(); // NOTE: this method gets all the references to the resources defined in the /res directory
        //array_categoryNames = r.getStringArray(R.array.categories); // it reads from the string-array in the strings.xml

        // Set the icon to Groceries (since that's the first time to be shown in the spinner)
        ImageView imgIconCategory = (ImageView) findViewById(R.id.imgIconCategory);
        imgIconCategory.setImageResource(Helper.CATEGORY_ICONS[0]);


        // Object reference to the cost and description text views
        et_Cost = (EditText) findViewById(R.id.etCost);
        et_Description = (AutoCompleteTextView) findViewById(R.id.etDescription);
        TextView tv_Text = (TextView) findViewById(R.id.graphLabel);
        tv_Text.setText(new SimpleDateFormat("MMM yyyy").format(new Date()));
        s = (Spinner) findViewById(R.id.spinner);

        et_Cost.setSelectAllOnFocus(true);
        mGraphicalLayout = (LinearLayout) findViewById(R.id.chart);
        mGraphicalObject = new GraphicalObject(getApplicationContext());

        // Fill values for the autocomplete on the description
        DbOperations dbOperations = new DbOperations(this);
        String[] descriptions = dbOperations.getAllDescriptions();
        desc_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, descriptions);
        et_Description.setAdapter(desc_adapter);

        // Read the shared preferences and checks if this the first time the app is accessed
        // For first time usage simulation only, clean up the shared preferences and purge table

        accessnumber = spUserSettings.getInt("accessnumber", Helper.DEFAULT_INT_VALUE);
        if (simulateFirstUse || accessnumber == 0) {
            accessFirstTime();
        }

        accessnumber++;
        SharedPreferences.Editor editor = spUserSettings.edit();
        editor.putInt("accessnumber", accessnumber);
        editor.commit();


        // Populate the spinner with the categories, create the list of names, values and colors of
        // the categories and draw the pie chart
        populateSpinnerCategories();
        drawPieChart();
        populateArrayValuesCategory();


        // IMPORTANT!!!
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            // it sets the background color of the textview color beside the spinner
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "Spinner - OnItemSelectedListener: " + s.getAdapter().getItem(position).toString());
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                parent.getChildAt(0).setBackgroundResource(Helper.CATEGORY_COLORS[position]);
                ImageView imgIconCategory = (ImageView) findViewById(R.id.imgIconCategory);
                imgIconCategory.setImageResource(Helper.CATEGORY_ICONS[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void showDialogDateOnButtonClick() {
        btnDateDialog = (Button) findViewById(R.id.btnDateDialog);
        btnDateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID) {
            return new DatePickerDialog(this, datePickerListener, dpYear, dpMonth - 1, dpDay);
        }
        return null;
    }

    //////////////////////////////////////
    // GRAPHICAL VIEW
    //////////////////////////////////////
    private void drawPieChart() {

        mGraphicalObject.setmGraphicalItems(populateGraphicalItemList());
        mGraphicalObject.drawChart(getApplicationContext(), mGraphicalLayout, Helper.PIE_CHART);
//        if (mChartView == null) {
//            mChartView = graphicalObject.getChartView(getApplicationContext(), Helper.PIE_CHART);
//            mGraphicalLayout.addView(mChartView, new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
//        } else {
//            mChartView.repaint();
//        }
    }

    private List<GraphicalItem> populateGraphicalItemList() {

        Log.d(TAG, "Populating graphical items...");
        List<GraphicalItem> graphicalItemList = new ArrayList<GraphicalItem>();
        for (int i = 0; i < Helper.CATEGORY_NAMES.length; i++) {
            GraphicalItem graphicalItem = new GraphicalItem();
            graphicalItem.mCategory = Helper.CATEGORY_NAMES[i];
            graphicalItem.mValue = String.valueOf(spValuesCategory.getFloat(Helper.CATEGORY_NAMES[i], Helper.DEFAULT_FLOAT_VALUE));
            graphicalItem.mColor = ContextCompat.getColor(this, Helper.CATEGORY_COLORS[i]);
            graphicalItemList.add(graphicalItem);
            Log.d(TAG, "Item " + i + ": " + graphicalItem.mCategory + " " + graphicalItem.mValue + " " + graphicalItem.mColor + "\n");

        }
        return graphicalItemList;

    }

    private void populateArrayValuesCategory() {
        Log.d(TAG, "Populating values category...");
        for (int i = 0; i < Helper.CATEGORY_NAMES.length; i++) {

            array_categoryValues[i] = spValuesCategory.getFloat(Helper.CATEGORY_NAMES[i], Helper.DEFAULT_FLOAT_VALUE);
        }
    }

    public void addCost(View v) {

        Log.d(TAG, "Adding cost...");
        // reads the values inputted in cost and description
        mCost = et_Cost.getText().toString();
        mDescription = et_Description.getText().toString();

        // if the cost is not empty and contains only numbers
        // ^(?:[1-9]\d*|0)?(?:\.\d+)?$
        // [0-9]+
        if (!(mCost.isEmpty()) && mCost.matches("^(?:[1-9]\\d*|0)?(?:\\.\\d+)?$")) {

            // calculate the new value of the selected category in the Sharedpreferences
            String selectedItem = s.getSelectedItem().toString();

            // Get the index from spinner. so i can reference to color and vsalue for that category
            index = Arrays.asList(Helper.CATEGORY_NAMES).indexOf(selectedItem);

            // calculate updated cost
            float actualCost = Float.valueOf(array_categoryValues[index]);
            float updatedCost = actualCost + Float.parseFloat(mCost);
            Log.d(TAG, "Selected item: " + selectedItem + " at index " + index + " with original value " + actualCost + " and updated to " + updatedCost);

            // Updates the value for the selected category in the SharedPreferences
            array_categoryValues[index] = updatedCost;

            // update file values category
            SharedPreferences.Editor editor = spValuesCategory.edit();
            editor.putFloat(selectedItem, updatedCost);
            editor.commit();


            // Refresh the graphical view
            drawPieChart();

            // Insert this value in the table for the history and execute method ADD_COST via doInBackground method in the backgroundtask
            String dpMonthAdjusted = String.valueOf(dpMonth);
            if (dpMonthAdjusted.length() == 1) {
                dpMonthAdjusted = '0' + dpMonthAdjusted;
            }
            selectedDate = String.valueOf(dpYear) + dpMonthAdjusted + String.valueOf(dpDay);
            //String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
            BackgroundTask backgroundTask = new BackgroundTask(this);
            String currentTimeInMs = String.valueOf(System.currentTimeMillis());
            backgroundTask.execute(FeedReaderContract.Methods.ADD_COST, currentTimeInMs, mCost, mDescription, selectedItem, selectedDate);
            updateDescriptionsForAutocomplete();

        } else {

            Toast.makeText(this, "Please insert cost", Toast.LENGTH_SHORT).show();

        }

        et_Cost.requestFocus();
        et_Description.setText("");


    }


    private void accessFirstTime() {
        // To remove when flag is removed
        SharedPreferences.Editor editor = spValuesCategory.edit();
        editor.clear();
        editor.commit();

        // To remove when flag is removed
        // Clear the cost table (for now)
        BackgroundTask btEraseAll = new BackgroundTask(this);
        btEraseAll.execute(FeedReaderContract.Methods.PREPAREFORFIRSTUSAGE, null);

        Log.d(TAG, "Database and values cleared");

    }


    public void updateDescriptionsForAutocomplete() {

        DbOperations dbOperations = new DbOperations(this);
        String[] descriptions = dbOperations.getAllDescriptions();
        desc_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, descriptions);
        et_Description.setAdapter(desc_adapter);

    }


    private void populateSpinnerCategories() {

        Log.d(TAG, "Populating spinner with items...");
        ArrayAdapter<String> ArrAdpt = new ArrayAdapter<String>(getBaseContext(), R.layout.mainactivity_spinner, Helper.CATEGORY_NAMES);
        s.setAdapter(ArrAdpt);

    }


    @Override
    // The parameter in the menuinflater points to the main_toolbarl menu, where
    // it's possible to define custom options.
    // Dependencies: xml file in the menu folder, accessible via R.menu command
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Inflating toolbar...");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    // When an option is selected in the menu, this method is called. The getItemId
    // method gives back the id of the selected option
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, as long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.settings:
                Log.d(TAG, "Clicked settings");
                Intent intentSettings = new Intent(getApplicationContext(), Settings.class);
                startActivityForResult(intentSettings, REQUEST_CODE_SETTINGS);
                return true;

            case R.id.about:
                Log.d(TAG, "Clicked about...");
                Intent intentAbout = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intentAbout);
                // finish();
                return true;

            case R.id.resetall:
                Log.d(TAG, "Clicked reset all...");
                Intent intentResetAll = new Intent(getApplicationContext(), ResetAll.class);
                startActivityForResult(intentResetAll, REQUEST_CODE_RESET_ALL);
                return true;

            case R.id.history:
                Log.d(TAG, "Clicked history...");
                Intent intentHistory = new Intent(getApplicationContext(), HistoryList.class);
                startActivity(intentHistory);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    //TODO adjust onActivityResult with graphic refactored code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Receiving intent back...");

        if (requestCode == REQUEST_CODE_RESET_ALL) {
            if (resultCode == Activity.RESULT_OK) {
                accessFirstTime();
                populateSpinnerCategories();
                populateArrayValuesCategory();
                drawPieChart();
                updateDescriptionsForAutocomplete();

            } else {

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    private boolean isResetNeeded() {

        java.util.Date date = new Date();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(date);

        DbOperations dbOperations = new DbOperations(this);
        String lastBackupDate = dbOperations.getLastBackup();

        Calendar backupCal = Calendar.getInstance();
        int backupDay = Integer.valueOf(lastBackupDate.substring(6, lastBackupDate.length() + 1));
        int backupMonth = Integer.valueOf(lastBackupDate.substring(4, 6));
        int backupYear = Integer.valueOf(lastBackupDate.substring(0, 4));
        backupCal.set(backupYear, backupMonth, backupDay);

        //TODO backup data to archive and clear the cost table
        return currentCal.compareTo(backupCal) > -1;


    }

}










