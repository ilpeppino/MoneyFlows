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

    public static final int[] categoryColors = {R.color.Groceries,
            R.color.Family,
            R.color.Leisure,
            R.color.Transportation,
            R.color.Health,
            R.color.Pets,
            R.color.Bills};


    public static final int[] categoryIcons = {R.drawable.groceries,
            R.drawable.family,
            R.drawable.entertainment,
            R.drawable.car,
            R.drawable.health,
            R.drawable.pets,
            R.drawable.bills};

    public static final String[] categoryNames = {"Groceries",
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
