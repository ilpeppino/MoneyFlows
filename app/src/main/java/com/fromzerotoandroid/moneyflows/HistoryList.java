package com.fromzerotoandroid.moneyflows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class HistoryList extends AppCompatActivity {

    public static final String QUERY_ALL = "select * from " + FeedReaderContract.CostEntry.TABLE_NAME + " ORDER BY " + FeedReaderContract.CostEntry.COLUMN_NAME_DATE + " DESC";
    public static final String TAG = "Class: HistoryList";
    // Shared preferences vars
    public static final String INT_TYPE = "INT";
    public static final String FLOAT_TYPE = "FLOAT";
    public static final String STRING_TYPE = "STRING";
    public static final String VALUES_CATEGORY = "ValuesCategory";
    public static final String USERS_SETTINGS = "UserSettings";
    private static final int REQUEST_CODE_DETAILS_TRX = 10;
    private static final int DEFAULT_INT_VALUE = 0;
    private static final float DEFAULT_FLOAT_VALUE = 0;
    private static final String DEFAULT_STRING_VALUE = "";
    private CustomAdapterHistoryList adapter;
    private List<ListViewItem> listViewItems;
    private List<ListViewItem> filteredListViewItems;
    private ListViewItem selectedItemListView;
    private int position;
    private EditText editText_Search;
    private SharedPreferences spUserSettings, spValuesCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.historylist_activity);
        // Toolbar is defined in content_history.xml
        Toolbar toolbar = (Toolbar) findViewById(R.id.history_toolbar);

        toolbar.showOverflowMenu();
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("History");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spValuesCategory = getSharedPreferences(Helper.VALUES_CATEGORY, Context.MODE_PRIVATE);

        Intent i = getIntent();
        Log.d(TAG, "Receving intent...");

        queryAllDb();

        // set listview and its adapter
        editText_Search = (EditText) findViewById(R.id.searchforiteminhistory);
        editText_Search.setFocusable(false);
        final ListView listview = (ListView) findViewById(R.id.listView);
        adapter = new CustomAdapterHistoryList(this, listViewItems);
        listview.setAdapter(adapter);
        // ocntext menu on listview
        registerForContextMenu(listview);
        // set filter on description
        listview.setTextFilterEnabled(true);
        filteredListViewItems = adapter.getFilteredResult();


        editText_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "Text [" + s + "]");
                adapter.getFilter().filter(s.toString());
                filteredListViewItems = adapter.getFilteredResult();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void queryAllDb() {
        // result will contain the result of the query. It must be defined as ListArray
        listViewItems = new ArrayList<ListViewItem>();

        try {
            DbOperations dbOperations = new DbOperations(this);
            SQLiteDatabase db = dbOperations.getWritableDatabase();
            Cursor c = db.rawQuery(QUERY_ALL, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String idtimestamp = c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_TIMESTAMP));
                        String cost = c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_COST));
                        String category = c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_CATEGORY));
                        String desc = c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION));
                        String date = Helper.formatDate("yyyyMMdd", "dd-MM-yyyy", c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DATE)));

                        ListViewItem lvItem = new ListViewItem();
                        lvItem.idtimestamp = idtimestamp;
                        lvItem.cost = cost;
                        lvItem.category = category;
                        lvItem.date = date;
                        lvItem.description = desc;
                        listViewItems.add(lvItem);
                    } while (c.moveToNext());
                }
                db.close();
            }
        } catch (Exception e) {
            Log.e("History", "Error during database processing");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Inflating toolbar...");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history_toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, as long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent upIntent = new Intent(this, MainActivity.class);
            NavUtils.navigateUpTo(this, upIntent);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d(TAG, "On long click");
        getMenuInflater().inflate(R.menu.history_rowitem_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        position = info.position;
        filteredListViewItems = adapter.getFilteredResult();
        selectedItemListView = filteredListViewItems.get(position);

        switch (item.getItemId()) {
            case R.id.deleterowitem:
                // Delete row from db
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute(FeedReaderContract.Methods.DELETE_ROW, selectedItemListView.idtimestamp);

                // Remove item from listview
                filteredListViewItems.remove(info.position);
                adapter.notifyDataSetChanged();
                listViewItems.remove(selectedItemListView);

//            // Update value in shared preferences

                SharedPreferences.Editor editor = spValuesCategory.edit();
                editor.putFloat(selectedItemListView.category, spValuesCategory.getFloat(selectedItemListView.category, DEFAULT_FLOAT_VALUE) - Float.valueOf(selectedItemListView.cost));
                editor.commit();
                break;

            case R.id.updaterowitem:
                Intent myIntent = new Intent(this, UpdateTrxNew.class);
                myIntent.putExtra("IdTimestamp", selectedItemListView.idtimestamp);
                myIntent.putExtra("Cost", selectedItemListView.cost);
                myIntent.putExtra("Category", selectedItemListView.category);
                myIntent.putExtra("Date", selectedItemListView.date);
                myIntent.putExtra("Description", selectedItemListView.description);
                startActivityForResult(myIntent, REQUEST_CODE_DETAILS_TRX);
                break;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Receiving intent back from UpdateTransaction...");

        if (requestCode == REQUEST_CODE_DETAILS_TRX) {
            if (resultCode == Activity.RESULT_OK) {

                String newCost = data.getStringExtra("newCost");
                String newDescription = data.getStringExtra("newDescription");
                String newCategory = data.getStringExtra("newCategory");
                String newDate = data.getStringExtra("newDate");
                // Update value in shared preferences
                SharedPreferences.Editor editor = spValuesCategory.edit();
                editor.putFloat(selectedItemListView.category, spValuesCategory.getFloat(selectedItemListView.category, DEFAULT_FLOAT_VALUE) - Float.valueOf(selectedItemListView.cost));
                editor.putFloat(newCategory, spValuesCategory.getFloat(newCategory, DEFAULT_FLOAT_VALUE) + Float.valueOf(newCost));
                editor.commit();

                ListViewItem modifiedListItem = new ListViewItem();
                modifiedListItem.idtimestamp = selectedItemListView.idtimestamp;
                modifiedListItem.cost = newCost;
                modifiedListItem.description = newDescription;
                modifiedListItem.category = newCategory;
                //  modifiedListItem.date = selectedItemListView.date;
                modifiedListItem.date = newDate;
                listViewItems.set(position, modifiedListItem);
                adapter.notifyDataSetChanged();
                queryAllDb();

                super.onActivityResult(requestCode, resultCode, data);
            }

        }
    }

    class ListViewItem {

        public String idtimestamp, cost, date, category, description;

    }
}