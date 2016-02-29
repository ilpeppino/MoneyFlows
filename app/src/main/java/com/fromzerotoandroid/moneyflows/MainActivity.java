package com.fromzerotoandroid.moneyflows;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
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
    public static final String COLORS_CATEGORY = "ColorsCategory";
    private SharedPreferences sharedpref_colorCategory;
    private SharedPreferences.Editor editor_colorCategory;
    public static final String USERS_SETTINGS = "UserSettings";
    private SharedPreferences sharedpref_usersSettings;
    private SharedPreferences.Editor editor_usersSettings;

    // Constants for logging tags
    public static final String TAG = "MainActivity";
    public static final String TAG_COLORS = "Colors";

    // Array of constants colors for the categories
    public static final int[] COLOR_PALETTE = {

            Color.argb(255, 255, 0, 0),
            Color.argb(255, 0, 255, 0),
            Color.argb(255, 0, 0, 255),
            Color.argb(255, 128, 0, 128),
            Color.argb(255, 0, 128, 128),
            Color.argb(255, 128, 128, 128),
            Color.argb(255, 200, 128, 128),
            Color.argb(255, 255, 128, 255),
            Color.argb(255, 128, 0, 255),
            Color.argb(255, 0, 0, 0),

    };

    // Information about names, values and colors of categories are defined here.
    // It references to ListItems class in this main class
    // List<ListItems> listViewItems = new ArrayList<ListItems>();

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
    private String[] array_categoryNames = new String[TOTALNRCATEGORIES];
    private float[] array_categoryValues = new float[TOTALNRCATEGORIES];
    private int[] array_categoryColors = new int[TOTALNRCATEGORIES];


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int accessnumber;

        // Creates the layout and toolbar for the main layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu(); // NOTE: this method shows the menu on top-right corner
        setSupportActionBar(toolbar);
        Log.d(TAG, "onCreate");

        // gets all the categorynames from strings.xml
        Resources r = getResources(); // NOTE: this method gets all the references to the resources defined in the /res directory
        array_categoryNames = r.getStringArray(R.array.categories); // it reads from the string-array in the strings.xml

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
        accessnumber += 1;
        editor_usersSettings = sharedpref_usersSettings.edit();
        editor_usersSettings.putInt("accessnumber", accessnumber);
        editor_usersSettings.commit();

        // Populate the spinner with the categories, create the list of names, values and colors of
        // the categories and draw the pie chart
        populateArrays();
        populateSpinnerCategories();
        paintGraphics();

    }

    private void populateArrays() {

        for (int i = 0; i < array_categoryNames.length; i++) {

            String currCategory = array_categoryNames[i];
            sharedpref_valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
            sharedpref_colorCategory = getSharedPreferences(COLORS_CATEGORY, Context.MODE_PRIVATE);

            array_categoryValues[i] = sharedpref_valuesCategory.getFloat(currCategory, 0);
            array_categoryColors[i] = sharedpref_colorCategory.getInt(currCategory, 0);

            Log.d("LISTITEMS", array_categoryNames[i] + " " + array_categoryValues[i] + " " + array_categoryColors[i] + "\n");
        }
    }


    private void accessFirstTime() {

        // Clear shared preferences
        sharedpref_usersSettings = getSharedPreferences(USERS_SETTINGS, Context.MODE_PRIVATE);
        editor_usersSettings = sharedpref_usersSettings.edit();
        editor_usersSettings.clear();
        editor_usersSettings.commit();

        sharedpref_valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
        editor_valuesCategory = sharedpref_valuesCategory.edit();
        editor_valuesCategory.clear();
        editor_valuesCategory.commit();

        sharedpref_colorCategory = getSharedPreferences(COLORS_CATEGORY, Context.MODE_PRIVATE);
        editor_colorCategory = sharedpref_colorCategory.edit();
        editor_colorCategory.clear();
        editor_colorCategory.commit();

        // For colors, repopulate with standard colors by reading the categories defined in the
        // strings.xml and assign the colors from COLOR_PALETTE in sequence
        for (int i = 0; i < array_categoryNames.length; i++) {
            String currentCategory = array_categoryNames[i].toString();
            editor_colorCategory.putInt(currentCategory, COLOR_PALETTE[i]);
            editor_colorCategory.commit();

            Log.d(TAG_COLORS, "Category: " + currentCategory +
                    " - Color: " + COLOR_PALETTE[i] +
                    " - Alpha: " + Color.alpha(COLOR_PALETTE[i]) +
                    " - Red: " + Color.red(COLOR_PALETTE[i]) +
                    " - Green: " + Color.green(COLOR_PALETTE[i]) +
                    " - Blue: " + Color.blue(COLOR_PALETTE[i]));
        }


        // Clear the cost table (for now)
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(FeedReaderContract.Methods.ERASE_ALL, null);

    }

    // Method called when ADD button is clicked
    // PS: methods associated with click buttons or other user interaction defined in xml
    // must be PUBLIC and View as parameter

    public void addCost(View v) {

        // reads the values inputted in cost and description
        mCost = et_Cost.getText().toString();
        mDescription = et_Description.getText().toString();


        // if the cost is not empty and contains only numbers155

        // ^(?:[1-9]\d*|0)?(?:\.\d+)?$
        // [0-9]+
        if (!(mCost.isEmpty()) && mCost.matches("^(?:[1-9]\\d*|0)?(?:\\.\\d+)?$")) {


            // calculate the new value of the selected category in the Sharedpreferences
            Spinner s = (Spinner) findViewById(R.id.spinner);
            String selectedItem = s.getSelectedItem().toString();
            // sharedpref_valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
            index = Arrays.asList(array_categoryNames).indexOf(selectedItem);

            float actualCost = Float.valueOf(array_categoryValues[index]);
            float updatedCost = actualCost + Float.parseFloat(mCost);


            Log.d("LISTITEMS", "Selected item: " + selectedItem + " at index " + index + " with original value " + actualCost + " and updated to " + updatedCost);

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

        // Customized spinner with spinner_categories xml layout
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_categories);
        spinner.setAdapter(adapter);

        // IMPORTANT!!!
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            // it sets the background color of the textview color beside the spinner
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sharedpref_colorCategory = getSharedPreferences(COLORS_CATEGORY, Context.MODE_PRIVATE);
                Log.d(TAG_COLORS, spinner.getAdapter().getItem(position).toString());

                // TextView tvColor = (TextView) findViewById(R.id.tvCategoryColor);
                // tvColor.setBackgroundColor(sharedpref_colorCategory.getInt(spinner.getAdapter().getItem(position).toString(), 0));
                // ((TextView) parent.getChildAt(0)).setTextColor(sharedpref_colorCategory.getInt(spinner.getAdapter().getItem(position).toString(), 0));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                parent.getChildAt(0).setBackgroundColor(sharedpref_colorCategory.getInt(spinner.getAdapter().getItem(position).toString(), 0));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void paintGraphics() {

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

//        CategoryColor categoryColor = new CategoryColor();
//        arrColors = categoryColor.getColors(nrChildren);

//        sharedpref_valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
//        sharedpref_colorCategory = getSharedPreferences(COLORS_CATEGORY, Context.MODE_PRIVATE);

        mSeries.clear();
        for (int i = 0; i < TOTALNRCATEGORIES; i++) {

            // Retrieves the values of each category from the spinner
            String currentCategory = array_categoryNames[i].toString();
            // float currentValue = sharedpref_valuesCategory.getFloat(currentCategory, 0);
            float currentValue = array_categoryValues[i];


            if (currentValue > 0) {
                // Add the category to the serie and set the color
                mSeries.add(currentCategory + " " + currentValue, currentValue);

                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                renderer.setColor(sharedpref_colorCategory.getInt(currentCategory, 0));
                Log.d("COLORS", "Category: " + currentCategory +
                        " - Color: " + sharedpref_colorCategory.getInt(currentCategory, 0) +
                        " - Alpha: " + Color.alpha(sharedpref_colorCategory.getInt(currentCategory, 0)) +
                        " - Red: " + Color.red(sharedpref_colorCategory.getInt(currentCategory, 0)) +
                        " - Green: " + Color.green(sharedpref_colorCategory.getInt(currentCategory, 0)) +
                        " - Blue: " + Color.blue(sharedpref_colorCategory.getInt(currentCategory, 0)));

                mRenderer.addSeriesRenderer(renderer);

            }
        }

        drawPieChart();

    }

    private void drawPieChart() {

        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            layout.addView(mChartView, new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));

        } else {

            mChartView.repaint();

        }

    }


    @Override
    // The parameter in the menuinflater points to the actions_toolbar.xml menu, where
    // it's possible to define custom options.
    // Dependencies: xml file in the menu folder, accessible via R.menu command
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_toolbar, menu);
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

        // The ids accessible via R.id are defined in the actions_toolbar.xml
        // menu file.
        if (id == R.id.about) {
            // In order to call another activity that generates a new layout,
            // use the following intent declaration. The second parameter specifies
            // the class of the activity to be called. With startActivity, the onCreate
            // on the called activity is called.
            // Remember to specify a finish() to terminate the ativity when it's finished
            Intent i = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(i);
            // finish();
            return true;
        }

        if (id == R.id.resetall) {
            Intent i = new Intent(getApplicationContext(), ResetAll.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.history) {
            Intent i = new Intent(getApplicationContext(), HistoryList.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // drawPieChart();

    }


    //    class CategoryColor {
//
//        private int[] colors;
//        private double red, blue, green, alpha;
//
//        CategoryColor() {
//            red = 0;
//            blue = 0;
//            green = 0;
//            alpha = 0;
//            colors = new int[20];
//
//        }
//
//        public int[] getColors(int nrCategories) {
//            Random r = new Random();
//
//            for (int i = 0; i < nrCategories; i++) {
//
//                red = Math.random() * 255;
//                blue = Math.random() * 255;
//                green = Math.random() * 255;
//                alpha = Math.random() * 255;
//                colors[i] = Color.argb((int) alpha, (int) red, (int) blue, (int) green);
//
//            }
//
//            return colors;
//        }
//
//    }

    class ListItems {

        public String cat_name, cat_value;
        public int cat_color;

    }


}










