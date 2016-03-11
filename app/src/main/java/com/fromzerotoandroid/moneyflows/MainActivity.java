package com.fromzerotoandroid.moneyflows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    // Only for testing purposes
    private boolean simulateFirstUse = false;

    // Number of categories defined in strings.xml
    private static final int TOTALNRCATEGORIES = 6;


    // SharedPreferences, editors and names definition
    public static final String VALUES_CATEGORY = "ValuesCategory";
    private SharedPreferences sharedpref_valuesCategory;
    private SharedPreferences.Editor editor_valuesCategory;
    public static final String USERS_SETTINGS = "UserSettings";
    private SharedPreferences sharedpref_usersSettings;
    private SharedPreferences.Editor editor_usersSettings;

    // Constants for logging tags
    public static final String TAG = "Class: MainActivity";


    // Defines the spinner for selecting the category cost
    Spinner spinner;

    // Variables used by the graphical view of the data
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;
    private LinearLayout chart;

    // Object references to cost and description in the main layout screen
    private EditText et_Cost, et_Description;
    private String mCost, mDescription;

    // it stores the category names and the index when a cost is added
    private int index;
    private float[] array_categoryValues = new float[TOTALNRCATEGORIES];

    int accessnumber;

    // Defines request codes for intents
    public static final int REQUEST_CODE_RESET_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        // Creates the layout and toolbar for the main layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_activity);

        // Toolbar is defined in mainactivity_activityactivity.xml
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu(); // NOTE: this method shows the menu on top-right corner
        setSupportActionBar(toolbar);
        Log.d(TAG, "onCreate");

        // gets all the categorynames from strings.xml
        Resources r = getResources(); // NOTE: this method gets all the references to the resources defined in the /res directory
        //array_categoryNames = r.getStringArray(R.array.categories); // it reads from the string-array in the strings.xml

        // Set the icon to Groceries (since that's the first time to be shown in the spinner)
        ImageView imgIconCategory = (ImageView) findViewById(R.id.imgIconCategory);
        imgIconCategory.setImageResource(Helper.categoryIcons[0]);

        // Object reference to the cost and description text views
        et_Cost = (EditText) findViewById(R.id.etCost);
        et_Description = (EditText) findViewById(R.id.etDescription);

        // Read the shared preferences and checks if this the first time the app is accessed
        // For first time usage simulation only, clean up the shared preferences and purge table
        sharedpref_usersSettings = getSharedPreferences(USERS_SETTINGS, Context.MODE_PRIVATE);
        accessnumber = sharedpref_usersSettings.getInt("accessnumber", 0);
        if (simulateFirstUse || accessnumber == 0) {
            accessFirstTime();
        }

        editor_usersSettings = sharedpref_usersSettings.edit();
        editor_usersSettings.putInt("accessnumber", accessnumber + 1);
        editor_usersSettings.commit();

        // Populate the spinner with the categories, create the list of names, values and colors of
        // the categories and draw the pie chart
        populateArrays();
        populateSpinnerCategories();
        paintGraphics();

        accessnumber += 1;

    }

    private void populateArrays() {

        Log.d(TAG, "Populating arrays...");

        for (int i = 0; i < Helper.categoryNames.length; i++) {

            String currCategory = Helper.categoryNames[i];
            sharedpref_valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
            array_categoryValues[i] = sharedpref_valuesCategory.getFloat(currCategory, 0);

            Log.d(TAG, "Item " + i + ": " + Helper.categoryNames[i] + " " + array_categoryValues[i] + " " + Helper.categoryColors[i] + "\n");
        }
    }


    private void accessFirstTime() {

        sharedpref_valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
        editor_valuesCategory = sharedpref_valuesCategory.edit();
        editor_valuesCategory.clear();
        editor_valuesCategory.commit();

        // Clear the cost table (for now)
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(FeedReaderContract.Methods.ERASE_ALL, null);

        Log.d(TAG, "Database and values cleared");

    }

    // Method called when ADD button is clicked
    // PS: methods associated with click buttons or other user interaction defined in xml
    // must be PUBLIC and View as parameter

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
            Spinner s = (Spinner) findViewById(R.id.spinner);
            String selectedItem = s.getSelectedItem().toString();
            // sharedpref_valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
            // Get the index from spinner. so i can reference to color and vsalue
            // for that category
            index = Arrays.asList(Helper.categoryNames).indexOf(selectedItem);

            // calculate updated cost
            float actualCost = Float.valueOf(array_categoryValues[index]);
            float updatedCost = actualCost + Float.parseFloat(mCost);
            Log.d(TAG, "Selected item: " + selectedItem + " at index " + index + " with original value " + actualCost + " and updated to " + updatedCost);

            // Updates the value for the selected category in the SharedPreferences
            array_categoryValues[index] = updatedCost;
            editor_valuesCategory = sharedpref_valuesCategory.edit();
            editor_valuesCategory.putFloat(selectedItem, updatedCost);
            editor_valuesCategory.commit();

            // Refresh the graphical view
            paintGraphics();

            // Insert this value in the table for the history and execute method ADD_COST via doInBackground method in the backgroundtask
            String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(FeedReaderContract.Methods.ADD_COST, mCost, mDescription, selectedItem, date);

        } else {

            Toast.makeText(this, "Please insert cost", Toast.LENGTH_SHORT).show();

        }


    }


    private void populateSpinnerCategories() {

        Log.d(TAG, "Populating spinner with items...");

        // Customized spinner with mainactivity_spinner xml layout
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.mainactivity_spinner);
        spinner.setAdapter(adapter);

        // IMPORTANT!!!
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            // it sets the background color of the textview color beside the spinner
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "Spinner - OnItemSelectedListener: " + spinner.getAdapter().getItem(position).toString());

                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                parent.getChildAt(0).setBackgroundResource(Helper.categoryColors[position]);
                ImageView imgIconCategory = (ImageView) findViewById(R.id.imgIconCategory);
                imgIconCategory.setImageResource(Helper.categoryIcons[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void paintGraphics() {

        Log.d(TAG, "Painting graphics...");

        // sets graphical properties for the renderer
        mRenderer.setApplyBackgroundColor(false);
        // mRenderer.setBackgroundColor(Color.argb(255, 255, 255, 255));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setShowLegend(false);
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setLabelsTextSize(20);
        mRenderer.setLegendTextSize(20);
        mRenderer.setMargins(new int[]{20, 30, 15, 0});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);

        // it removes all the existing renderers
        mRenderer.removeAllRenderers();

        mSeries.clear();
        for (int i = 0; i < TOTALNRCATEGORIES; i++) {

            // Retrieves the values of each category from the spinner
            String currentCategory = Helper.categoryNames[i].toString();
            // float currentValue = sharedpref_valuesCategory.getFloat(currentCategory, 0);
            float currentValue = array_categoryValues[i];
            int currentColor = ContextCompat.getColor(this, Helper.categoryColors[i]);


            if (currentValue > 0) {
                // Add the category to the serie and set the color
                mSeries.add(currentCategory + " " + currentValue, currentValue);

                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                renderer.setColor(currentColor);
                Log.d("COLORS", "Category: " + currentCategory +
                        " - Color: " + currentColor +
                        " - Alpha: " + Color.alpha(currentColor) +
                        " - Red: " + Color.red(currentColor) +
                        " - Green: " + Color.green(currentColor) +
                        " - Blue: " + Color.blue(currentColor));

                mRenderer.addSeriesRenderer(renderer);

            }
        }

        drawPieChart();

    }

    private void drawPieChart() {

        Log.d(TAG, "Drawing pie chart...");

        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            layout.addView(mChartView, new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));

        } else {

            mChartView.repaint();

        }

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

        // The ids accessible via R.id are defined in the main_toolbarl
        // menu file.
        if (id == R.id.about) {
            // In order to call another activity that generates a new layout,
            // use the following intent declaration. The second parameter specifies
            // the class of the activity to be called. With startActivity, the onCreate
            // on the called activity is called.
            // Remember to specify a finish() to terminate the ativity when it's finished
            Log.d(TAG, "Clicked about...");
            Intent i = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(i);
            // finish();
            return true;
        }

        if (id == R.id.resetall) {
            Log.d(TAG, "Clicked reset all...");
            Intent i = new Intent(getApplicationContext(), ResetAll.class);
            startActivityForResult(i, REQUEST_CODE_RESET_ALL);
            return true;
        }

        if (id == R.id.history) {
            Log.d(TAG, "Clicked history...");
            Intent i = new Intent(getApplicationContext(), HistoryList.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Receiving intent back...");

        if (requestCode == REQUEST_CODE_RESET_ALL) {
            if (resultCode == Activity.RESULT_OK) {
                accessFirstTime();
                populateArrays();
                populateSpinnerCategories();

                paintGraphics();

            } else {

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

}










