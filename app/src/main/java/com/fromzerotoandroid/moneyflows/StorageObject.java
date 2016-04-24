package com.fromzerotoandroid.moneyflows;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by M06F806 on 11-4-2016.
 */
public class StorageObject {

    public static final String INT_TYPE = "INT";
    public static final String FLOAT_TYPE = "FLOAT";
    public static final String STRING_TYPE = "STRING";
    public static final String VALUES_CATEGORY = "ValuesCategory";
    public static final String USERS_SETTINGS = "UserSettings";
    private static final int DEFAULT_INT_VALUE = 0;
    private static final float DEFAULT_FLOAT_VALUE = 0;
    private static final String DEFAULT_STRING_VALUE = "";
    private static StorageObject storageObject;
    private SharedPreferences spUserSettings, spValuesCategory;

    // Constructor
    private StorageObject(Context context) {
        spUserSettings = context.getSharedPreferences(USERS_SETTINGS, Context.MODE_PRIVATE);
        spValuesCategory = context.getSharedPreferences(VALUES_CATEGORY, Context.MODE_PRIVATE);
    }

    // Call to get the instance of StorageObject (dont call the constructor)
    public static StorageObject getInstance(Context context) {
        if (storageObject == null) {
            storageObject = new StorageObject(context);
        }
        return storageObject;
    }

    ////////////////////////////
    // VALUESCATEGORY operations
    ////////////////////////////
    public void setValuesCategory(String key, Float value) {
        SharedPreferences.Editor editor = spValuesCategory.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public Float getValuesCategory(String key) {
        return spValuesCategory.getFloat(key, DEFAULT_FLOAT_VALUE);
    }

    public void removeValuesCategory(String key, Float valueToRemove) {
        SharedPreferences.Editor editor = spValuesCategory.edit();
        editor.putFloat(key, spValuesCategory.getFloat(key, DEFAULT_FLOAT_VALUE) - valueToRemove);
        editor.commit();
    }

    public void resetValuesCategory() {
        SharedPreferences.Editor editor = spValuesCategory.edit();
        editor.clear();
        editor.commit();
    }

    public void updateValuesCategory(String key, Float oldValue, Float newValue) {
        SharedPreferences.Editor editor = spValuesCategory.edit();
        editor.putFloat(key, spValuesCategory.getFloat(key, DEFAULT_FLOAT_VALUE) - oldValue + newValue);
        editor.commit();
    }

    ////////////////////////////
    // USERSETTINGS operations
    ////////////////////////////
    public void setUsersSettings(String key, int value) {
        SharedPreferences.Editor editor = spUserSettings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getUsersSettings(String key) {
        return spUserSettings.getInt(key, DEFAULT_INT_VALUE);
    }


}
