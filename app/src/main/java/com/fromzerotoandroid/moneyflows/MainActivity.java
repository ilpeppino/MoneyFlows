package com.fromzerotoandroid.moneyflows;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private boolean simulateFirstUse = true;

    // Defines the SharedPreferences for keeping the values for each category

    public static final String VALUES_CATEGORY = "ValuesCategory";
    public static final String COLORS_CATEGORY = "ColorsCategory";
    public static final String USERS_SETTINGS = "UserSettings";
    public static final String TAG = "MainActivity";

    // Defines the spinner for selecting the category cost
    Spinner spinner;
    int nrChildren;

    // Variables used by the graphical view of the data
    //  private static int[] COLORS = new int[]{Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.GRAY, Color.YELLOW};
    private static double[] VALUES = new double[]{10, 11, 12, 13};
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;
    private SharedPreferences.Editor editor;
    private SharedPreferences usersSettings;
    private SharedPreferences valuesCategory;
    private LinearLayout chart;

    // Array of colors used by graphical view to represent categories
    private int[] arrColors = new int[20];
    float[] arrayvaluecategories = new float[20];


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        populateSpinnerCategories();

        usersSettings = getSharedPreferences(USERS_SETTINGS, Context.MODE_PRIVATE);
        valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
        int accessnumber = usersSettings.getInt("accessnumber", 0);

        if (simulateFirstUse) {
            editor = usersSettings.edit();
            editor.putInt("accessnumber", 0);
            editor.commit();
            accessnumber = 0;

            editor = valuesCategory.edit();
            editor.clear();
            editor.commit();

        }

        // Check if this is the first time the app is accessed
        if (accessnumber == 0) {
            Log.d(TAG, "First use");
//           setVisibilityGraph(View.INVISIBLE);
        } else {
            paintGraphics();
//            setVisibilityGraph(View.VISIBLE);

        }
        // If it has been previously accessed, increment accessnumber

        accessnumber += 1;
        editor = usersSettings.edit();
        editor.putInt("accessnumber", accessnumber);
        editor.commit();


        // Populate the spinner with the categories defined in strings.xml
        // Create an ArrayAdapter using the string array and a default spinner layout
        // Specify the layout to use when the list of choices appears
        // Apply the adapter to the spinner


        // Set properties for the renderer that will be used for the graphical view


        // Dinamically generates colors for the categories
        paintGraphics();

        // Generates renderers for the categories and assign colors

//        SharedPreferences sharedPreferences = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putFloat("Groceries", 15.23f);
//        editor.putFloat("Children", 13.45f);
//        editor.putFloat("Entertainment", 16.87f);
//        editor.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);



    }


    // Method called when ADD button is clicked
    public void addCost(View v) {

        // Get the selected item from the spinner
        Spinner s = (Spinner) findViewById(R.id.spinner);
        String selectedItem = s.getSelectedItem().toString();

        // Retrieves the amount for the selected category
        valuesCategory = getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
        float actualCost = valuesCategory.getFloat(selectedItem, 0);

        // Updates the value for the selected category
        SharedPreferences.Editor editor = valuesCategory.edit();
        TextView tCost = (TextView) findViewById(R.id.etCost);
        String inputCost = tCost.getText().toString();
        float updatedCost = actualCost + Float.parseFloat(inputCost);
        editor.putFloat(selectedItem, updatedCost);
        editor.commit();

        // Refresh the graphical view
        paintGraphics();
        // setVisibilityGraph(View.VISIBLE);

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

//            if (arrayvaluecategories[i] > 0) {
//                // Add the category to the serie and set the color
//                mSeries.add(spinner.getAdapter().getItem(i).toString() + " " + arrayvaluecategories[i], arrayvaluecategories[i]);
//                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
//                // renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
//                renderer.setColor(arrColors[i]);
//                mRenderer.addSeriesRenderer(renderer);
//            }


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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







