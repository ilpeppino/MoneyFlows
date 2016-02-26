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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {


    private boolean simulateFirstUse = true;

    // Defines the SharedPreferences for keeping the values for each category
    public static final String VALUES_CATEGORY = "ValuesCategory";
    public static final String NAMES_CATEGORY = "NamesCategory";
    public static final String USERS_SETTINGS = "UserSettings";
    public static final String COLORS_CATEGORY = "ColorsCategory";
    public static final String TAG = "MainActivity";
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

    // Defines the spinner for selecting the category cost
    Spinner spinner;
    int nrChildren;

    // Variables used by the graphical view of the data
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;
    private SharedPreferences.Editor editor;
    private SharedPreferences usersSettings;
    private SharedPreferences valuesCategory;
    private SharedPreferences namesCategory;
    private SharedPreferences colorCategory;
    private LinearLayout chart;

    private EditText et_Cost, et_Description;
    private String mCost, mDescription;

    // Array of colors used by graphical view to represent categories
    private int[] arrColors = new int[20];
    //    float[] arrayvaluecategories = new float[20];
    private int accessnumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_Cost = (EditText) findViewById(R.id.etCost);
        et_Description = (EditText) findViewById(R.id.etDescription);


        // Read the shared preferences
        Log.d(TAG, "onCreate");
        usersSettings = getSharedPreferences(USERS_SETTINGS, Context.MODE_PRIVATE);
        valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
        namesCategory = getSharedPreferences(NAMES_CATEGORY, Context.MODE_PRIVATE);
        colorCategory = getSharedPreferences(COLORS_CATEGORY, Context.MODE_PRIVATE);

        // Checks if this the first time the app is accessed
        accessnumber = usersSettings.getInt("accessnumber", 0);

        // For first time usage simulation only, clean up the shared preferences and purge table
        if (simulateFirstUse || accessnumber == 0) {

            accessFirstTime();

        }

        // Populate the spinner with the categories
        populateSpinnerCategories();


        // Increment accessnumber
        accessnumber += 1;
        editor = usersSettings.edit();
        editor.putInt("accessnumber", accessnumber);
        editor.commit();

        paintGraphics();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);


    }


    private void accessFirstTime() {

        // Clear shared preferences
        editor = usersSettings.edit();
        editor.clear();
        editor.commit();

        editor = valuesCategory.edit();
        editor.clear();
        editor.commit();

        editor = namesCategory.edit();
        editor.clear();
        editor.commit();

        // For colors, i clear what we have and repopulate with standard colors
        editor = colorCategory.edit();
        editor.clear();
        editor.commit();

        Resources r = getResources();
        String[] cat = r.getStringArray(R.array.categories);

        editor = colorCategory.edit();
        for (int i = 0; i < cat.length; i++) {
            String currentCategory = cat[i].toString();

            editor.putInt(currentCategory, COLOR_PALETTE[i]);

        }
        editor.commit();

        // Clear the cost table (for now)
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(FeedReaderContract.Methods.ERASE_ALL, null);


    }

    // Method called when ADD button is clicked

    // PS: methods associated with click buttons or other user interaction defined in xml
    // must be PUBLIC

    public void addCost(View v) {

        mCost = et_Cost.getText().toString();
        mDescription = et_Description.getText().toString();

        if (!(mCost.equals(""))) {

            // First of all, I save data in the SharedPreference

            // Get the selected item from the spinner
            Spinner s = (Spinner) findViewById(R.id.spinner);
            String selectedItem = s.getSelectedItem().toString();
            // Retrieves the amount for the selected category
            valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
            float actualCost = valuesCategory.getFloat(selectedItem, 0);
            // Updates the value for the selected category in the SharedPreferences
            SharedPreferences.Editor editor = valuesCategory.edit();
            float updatedCost = actualCost + Float.parseFloat(mCost);
            editor.putFloat(selectedItem, updatedCost);
            editor.commit();
            // Refresh the graphical view
            paintGraphics();

            // Insert this value in the table for the history
            String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
            BackgroundTask backgroundTask = new BackgroundTask(this);
            // The execute method trigger the doInBackground method in the backgroundtask
            backgroundTask.execute(FeedReaderContract.Methods.ADD_COST, mCost, mDescription, selectedItem, date);

        } else {
            Toast.makeText(this, "Please insert cost", Toast.LENGTH_LONG).show();
        }


    }

    private void populateSpinnerCategories() {

        // Customized spinner with spinner_categories xml layour
        spinner = (Spinner) findViewById(R.id.spinner);

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_categories);

//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    private void paintGraphics() {


        nrChildren = spinner.getCount();

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

//        CategoryColor categoryColor = new CategoryColor();
//        arrColors = categoryColor.getColors(nrChildren);

        mSeries.clear();
        for (int i = 0; i < nrChildren; i++) {

            // Retrieves the category from the spinner
            valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
            colorCategory = getSharedPreferences(COLORS_CATEGORY, Context.MODE_PRIVATE);
            String currentCategory = spinner.getAdapter().getItem(i).toString();
            float currentValue = valuesCategory.getFloat(currentCategory, 0);


            if (currentValue > 0) {
                // Add the category to the serie and set the color
                mSeries.add(currentCategory + " " + currentValue, currentValue);

                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                renderer.setColor(colorCategory.getInt(currentCategory, 0));

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
}










