package com.fromzerotoandroid.moneyflows;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ilpep on 3/11/2016.
 */
public class Helper {

    // Number of categories defined in strings.xml

    public static final int TOTALNRCATEGORIES = 7;

    public static final String INT_TYPE = "INT";
    public static final String FLOAT_TYPE = "FLOAT";
    public static final String STRING_TYPE = "STRING";
    public static final int DEFAULT_INT_VALUE = 0;
    public static final float DEFAULT_FLOAT_VALUE = 0;
    public static final String DEFAULT_STRING_VALUE = "";
    public static final String VALUES_CATEGORY = "ValuesCategory";
    public static final String USERS_SETTINGS = "UserSettings";

    public static final String[] DAY_NUMBER = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};

    public static final int[] CATEGORY_COLORS = {R.color.Groceries,
            R.color.Family,
            R.color.Leisure,
            R.color.Transportation,
            R.color.Health,
            R.color.Pets,
            R.color.Bills};

    public static final int[] CATEGORY_ICONS = {R.drawable.groceries,
            R.drawable.family,
            R.drawable.entertainment,
            R.drawable.car,
            R.drawable.health,
            R.drawable.pets,
            R.drawable.bills};

    public static final String PIE_CHART = "PieChart";

    public static final String[] CATEGORY_NAMES = {"Groceries",
            "Family",
            "Leisure",
            "Transportation",
            "Health",
            "Pets",
            "Bills"};

    public static String formatDate(String fromDateFormat, String toDateFormat, String dateToProcess) {
        String returnDate = "";

        try {
            DateFormat fromFormat = new SimpleDateFormat(fromDateFormat);
            fromFormat.setLenient(false);
            DateFormat toFormat = new SimpleDateFormat(toDateFormat);
            toFormat.setLenient(false);
            Date temp_date = fromFormat.parse(dateToProcess);
            returnDate = toFormat.format(temp_date);
            return returnDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnDate;
    }


}
