package com.fromzerotoandroid.moneyflows;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilpep on 4/8/2016.
 */
public class GraphicalObject {

    private DefaultRenderer mRenderer = new DefaultRenderer();
    private CategorySeries mSeries = new CategorySeries("");
    private GraphicalView mChartView;
    private List<GraphicalItem> mGraphicalItems;

    GraphicalObject() {
        mGraphicalItems = new ArrayList<GraphicalItem>();
        mRenderer = new DefaultRenderer();
        mSeries = new CategorySeries("");
    }

//    GraphicalObject(List<GraphicalItem> graphicalItemList) {
//        mGraphicalItems = new ArrayList<GraphicalItem>(graphicalItemList);
//    }

    public CategorySeries getmSeries() {
        return mSeries;
    }

    public DefaultRenderer getmRenderer() {
        return mRenderer;
    }

    public void setmGraphicalItems(List<GraphicalItem> graphicalItems) {
        mGraphicalItems = graphicalItems;
    }

    private void setSeriesRenderer() {

        setGraphicalProperties();
        mRenderer.removeAllRenderers();
        mSeries.clear();
        for (int i = 0; i < Helper.TOTALNRCATEGORIES; i++) {
            if (Float.valueOf(mGraphicalItems.get(i).mValue) > 0) {
                mSeries.add(mGraphicalItems.get(i).mCategory + " " + mGraphicalItems.get(i).mValue, Double.valueOf(mGraphicalItems.get(i).mValue));
                SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
                simpleRenderer.setColor(mGraphicalItems.get(i).mColor);
                mRenderer.addSeriesRenderer(simpleRenderer);
            }
        }
    }

//    public GraphicalView getChartView (Context context, String chartType) {
//        setSeriesRenderer();
//
//
//            switch (chartType) {
//                // Add the cost in the db
//                case Helper.PIE_CHART:
//                    mChartView = ChartFactory.getPieChartView(context, mSeries, mRenderer);
//                    break;
//            }
//
//
//        return mChartView;
//    }

    public void drawChart(Context context, LinearLayout mGraphicalLayout, String chartType) {
        setSeriesRenderer();

        if (mChartView == null) {
            switch (chartType) {
                // Add the cost in the db
                case Helper.PIE_CHART:
                    mChartView = ChartFactory.getPieChartView(context, mSeries, mRenderer);
                    break;
            }

            mGraphicalLayout.addView(mChartView, new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }


    }


//    public void drawPieChart(CategorySeries series, DefaultRenderer renderer) {
//        setSeriesRenderer();
//        if (mChartView == null) {
//            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
//            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
//            layout.addView(mChartView, new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
//
//        } else {
//
//            mChartView.repaint();
//
//        }
//    }

    private void setGraphicalProperties() {
        // sets graphical properties for the renderer
        mRenderer.setApplyBackgroundColor(false);
        // mRenderer.setBackgroundColor(Color.argb(255, 255, 255, 255));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setShowLegend(false);
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setLabelsTextSize(25);
        mRenderer.setLegendTextSize(20);
        mRenderer.setMargins(new int[]{20, 30, 15, 0});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);
    }

}

class GraphicalItem {

    protected String mCategory, mValue;
    protected int mColor;

    GraphicalItem() {
        mCategory = "";
        mValue = "";
        mColor = 0;
    }


    // Getters and setters
    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getmValue() {
        return mValue;
    }

    public void setmValue(String mValue) {
        this.mValue = mValue;
    }

}
