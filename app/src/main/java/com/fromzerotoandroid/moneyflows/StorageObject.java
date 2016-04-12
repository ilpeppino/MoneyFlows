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
    private static final int DEFAULT_INT_VALUE = 0;
    private static final int DEFAULT_FLOAT_VALUE = 0;
    private static final String DEFAULT_STRING_VALUE = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String mFileType;

    public StorageObject(Context context, String fileType, String fileName) {
        sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mFileType = fileType;
    }

    public void setValue(String key, Object value) {

        switch (mFileType) {
            case INT_TYPE:
                editor.putInt(key, (int) value);
                editor.commit();
                break;
            case FLOAT_TYPE:
                editor.putFloat(key, (float) value);
                editor.commit();
                break;
            case STRING_TYPE:
                editor.putString(key, (String) value);
                editor.commit();
                break;
        }

    }

    public Object getValue(String key) {

        switch (mFileType) {
            case INT_TYPE:
                return sharedPreferences.getInt(key, DEFAULT_INT_VALUE);
            case FLOAT_TYPE:
                return sharedPreferences.getFloat(key, DEFAULT_FLOAT_VALUE);
            case STRING_TYPE:
                return sharedPreferences.getString(key, DEFAULT_STRING_VALUE);
        }
        return null;
    }

    public void resetValues() {
        editor.clear();
        editor.commit();
    }

    public void removeValue(String key, String valueToRemove) {
        switch (mFileType) {
            case FLOAT_TYPE:
                editor.putFloat(key, sharedPreferences.getFloat(key, DEFAULT_FLOAT_VALUE) - Float.valueOf(valueToRemove));
                editor.commit();
                break;
        }
    }

    public void updateValue(String key, String oldValue, String newValue) {
        switch (mFileType) {
            case FLOAT_TYPE:
                editor.putFloat(key, sharedPreferences.getFloat(key, DEFAULT_FLOAT_VALUE) - Float.valueOf(oldValue) + Float.valueOf(newValue));
                editor.commit();
                break;
        }
    }


}
