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
    private static final int REQUEST_CODE_DETAILS_TRX = 10;
    public CustomAdapterHistoryList adapter;
    List<ListViewItem> listViewItems;
    ListViewItem selectedItemListView;
    int position;

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

        Intent i = getIntent();
        Log.d(TAG, "Receving intent...");
        // result will contain the result of the query. It must be defined as ListArray
        listViewItems = new ArrayList<ListViewItem>();

        // Inflate header layout
//        ListView listView = (ListView) findViewById(R.id.listView);
//        ViewGroup header_history_list = (ViewGroup) getLayoutInflater().inflate(R.layout.historylist_header, listView, false);
//        listView.addHeaderView(header_history_list);

        // Here the database is queried and returns the values from db in result
        // Best practice is to use try-catch
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

                        //  Formatting date in nice format
//                        DateFormat fromFormat = new SimpleDateFormat("yyyyMMdd");
//                        fromFormat.setLenient(false);
//                        DateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
//                        toFormat.setLenient(false);
//                        Date temp_date = fromFormat.parse(c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DATE)));
//                        String date = toFormat.format(temp_date);

                        String date = Helper.formatDate("yyyyMMdd", "dd-MM-yyyy", c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DATE)));

                        // String date = c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DATE));

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


        ListView listview = (ListView) findViewById(R.id.listView);
        // historylist_content.xml//
        // listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, result));
        registerForContextMenu(listview);
        adapter = new CustomAdapterHistoryList(this, listViewItems);
        listview.setAdapter(adapter);
        listview.setTextFilterEnabled(true);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        // TODO implement search functionality
        if (id == R.id.search) {

            EditText editText_Search = (EditText) findViewById(R.id.searchforiteminhistory);
            editText_Search.setVisibility(View.VISIBLE);
            editText_Search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d(TAG, "Text [" + s + "]");
                    adapter.getFilter().filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


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
        selectedItemListView = listViewItems.get(position);
        String idtimestamp = selectedItemListView.idtimestamp;
        String category = selectedItemListView.category;
        String cost = selectedItemListView.cost;
        String date = selectedItemListView.date;
        String description = selectedItemListView.description;

        if (item.getItemId() == R.id.deleterowitem) {
            // Delete row from db
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(FeedReaderContract.Methods.DELETE_ROW, idtimestamp);

            // Remove item from listview
            listViewItems.remove(info.position);
            adapter.notifyDataSetChanged();

//            // Update value in shared preferences
            SharedPreferences sharedpref_valuesCategory;
            SharedPreferences.Editor editor_valuesCategory;
            sharedpref_valuesCategory = getSharedPreferences(MainActivity.VALUES_CATEGORY, Context.MODE_PRIVATE);
            float totalCost = sharedpref_valuesCategory.getFloat(category, 0);
            editor_valuesCategory = sharedpref_valuesCategory.edit();
            editor_valuesCategory.putFloat(category, totalCost - Float.valueOf(cost));
            editor_valuesCategory.commit();


        }

        // TODO refactor update operation
        if (item.getItemId() == R.id.updaterowitem) {

            Intent myIntent = new Intent(this, UpdateTransaction.class);
            myIntent.putExtra("IdTimestamp", selectedItemListView.idtimestamp);
            myIntent.putExtra("Cost", selectedItemListView.cost);
            myIntent.putExtra("Category", selectedItemListView.category);
            myIntent.putExtra("Date", selectedItemListView.date);
            myIntent.putExtra("Description", selectedItemListView.description);
            startActivityForResult(myIntent, REQUEST_CODE_DETAILS_TRX);

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

                // TODO update the values in sharedpref correctly
                // Update value in shared preferences
                SharedPreferences sharedpref_valuesCategory;
                SharedPreferences.Editor editor_valuesCategory;
                sharedpref_valuesCategory = getSharedPreferences(MainActivity.VALUES_CATEGORY, Context.MODE_PRIVATE);
                Float oldCost = sharedpref_valuesCategory.getFloat(selectedItemListView.category, 0);
                editor_valuesCategory = sharedpref_valuesCategory.edit();
                editor_valuesCategory.putFloat(selectedItemListView.category, oldCost - Float.valueOf(selectedItemListView.cost) + Float.valueOf(newCost));
                editor_valuesCategory.commit();



                ListViewItem modifiedListItem = new ListViewItem();
                modifiedListItem.idtimestamp = selectedItemListView.idtimestamp;
                modifiedListItem.cost = newCost;
                modifiedListItem.description = newDescription;
                modifiedListItem.category = selectedItemListView.category;
                modifiedListItem.date = selectedItemListView.date;
                listViewItems.set(position, modifiedListItem);
                adapter.notifyDataSetChanged();



                super.onActivityResult(requestCode, resultCode, data);
            }

        }
    }

    class ListViewItem {

        public String idtimestamp, cost, date, category, description;

    }
}