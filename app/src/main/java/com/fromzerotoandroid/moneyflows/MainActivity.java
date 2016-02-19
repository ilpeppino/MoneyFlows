package com.fromzerotoandroid.moneyflows;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity {


    private boolean simulateFirstUse = true;

    // Defines the SharedPreferences for keeping the values for each category
    public static final String VALUES_CATEGORY = "ValuesCategory";
    public static final String NAMES_CATEGORY = "NamesCategory";
    public static final String USERS_SETTINGS = "UserSettings";
    public static final String TAG = "MainActivity";

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
    private LinearLayout chart;

    private EditText et_Cost, et_Description;
    private String mCost, mDescription;

    // Array of colors used by graphical view to represent categories
    private int[] arrColors = new int[20];
    float[] arrayvaluecategories = new float[20];
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

        // Checks if this the first time the app is accessed
        accessnumber = usersSettings.getInt("accessnumber", 0);

        // For first time usage simulation only, clean up the shared preferences and purge table
        if (simulateFirstUse) {
            editor = usersSettings.edit();
            editor.clear();
            editor.commit();

            editor = valuesCategory.edit();
            editor.clear();
            editor.commit();

            editor = namesCategory.edit();
            editor.clear();
            editor.commit();

            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(FeedReaderContract.Methods.ERASE_ALL, null);


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


    // Method called when ADD button is clicked
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

    public void populateSpinnerCategories() {

        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    public void paintGraphics() {


        nrChildren = spinner.getCount();

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(255, 255, 255, 255));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setLabelsTextSize(20);
        mRenderer.setLegendTextSize(20);
        mRenderer.setMargins(new int[]{20, 30, 15, 0});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);

        CategoryColor categoryColor = new CategoryColor();
        arrColors = categoryColor.getColors(nrChildren);

        mSeries.clear();
        for (int i = 0; i < nrChildren; i++) {

            // Retrieves the category from the spinner
            valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
            arrayvaluecategories[i] = valuesCategory.getFloat(spinner.getAdapter().getItem(i).toString(), 0);

            if (arrayvaluecategories[i] > 0) {
                // Add the category to the serie and set the color
                mSeries.add(spinner.getAdapter().getItem(i).toString() + " " + arrayvaluecategories[i], arrayvaluecategories[i]);

                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                // renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
                renderer.setColor(arrColors[i]);
                mRenderer.addSeriesRenderer(renderer);

            }
        }


        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);

            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();


                }
            });

            mChartView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    return true;
                }
            });
            layout.addView(mChartView, new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.FILL_PARENT, LinearLayoutCompat.LayoutParams.FILL_PARENT));
        } else {
            mChartView.repaint();
        }

        if (mChartView != null) {
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);

            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();


                }
            });

            mChartView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    return true;
                }
            });
            layout.addView(mChartView, new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.FILL_PARENT, LinearLayoutCompat.LayoutParams.FILL_PARENT));
        } else {
            mChartView.repaint();
        }
    }
}

class CategoryColor {

    private int[] colors;
    private double red, blue, green, alpha;

    CategoryColor() {
        red = 0;
        blue = 0;
        green = 0;
        alpha = 0;
        colors = new int[20];

    }

    public int[] getColors(int nrCategories) {
        Random r = new Random();

        for (int i = 0; i < nrCategories; i++) {

            red = Math.random() * 255;
            blue = Math.random() * 255;
            green = Math.random() * 255;
            alpha = Math.random() * 255;
            colors[i] = Color.argb((int) alpha, (int) red, (int) blue, (int) green);

 /*           TextView tvRed = (TextView) findViewById(R.id.red);
            TextView tvBlue = (TextView) findViewById(R.id.blue);
            TextView tvGreen = (TextView) findViewById(R.id.green);

            tvRed.setBackgroundColor((int)red);
            tvBlue.setBackgroundColor((int)blue);
            tvGreen.setBackgroundColor((int)green); **/
        }

        return colors;
    }

}

// A contract class is a container for constants that define names for URIs, tables, and columns.
// The contract class allows you to use the same constants across all the other classes in the same package.
// This lets you change a column name in one place and have it propagate throughout your code.
// A good way to organize a contract class is to put definitions that are global to your whole database in the root level of the class.
// Then create an inner class for each table that enumerates its columns.








