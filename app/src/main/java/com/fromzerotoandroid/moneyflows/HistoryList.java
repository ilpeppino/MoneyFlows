package com.fromzerotoandroid.moneyflows;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryList extends AppCompatActivity {

    public static final String QUERY_ALL = "select * from " + FeedReaderContract.CostEntry.TABLE_NAME + " ORDER BY " + FeedReaderContract.CostEntry.COLUMN_NAME_DATE + " DESC";
    public static final String TAG = "Class: HistoryList";

    public BaseAdapter adapter;
    List<ListViewItem> listViewItems;


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
                        String cost = c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_COST));
                        String category = c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_CATEGORY));
                        String desc = c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DESCRIPTION));

                        //  Formatting date in nice format
                        DateFormat fromFormat = new SimpleDateFormat("yyyyMMdd");
                        fromFormat.setLenient(false);
                        DateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
                        toFormat.setLenient(false);
                        Date temp_date = fromFormat.parse(c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DATE)));
                        String date = toFormat.format(temp_date);

                        // String date = c.getString(c.getColumnIndex(FeedReaderContract.CostEntry.COLUMN_NAME_DATE));

                        ListViewItem lvItem = new ListViewItem();
                        lvItem.cost = cost;
                        lvItem.category = category;
                        lvItem.date = date;
                        lvItem.description = desc;
                        listViewItems.add(lvItem);

                    } while (c.moveToNext());

                }
                db.close();
                c.close();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d(TAG, "On long click");
        getMenuInflater().inflate(R.menu.history_rowitem_menu, menu);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        DbOperations dbOperations = new DbOperations(this);
        SQLiteDatabase db = dbOperations.getWritableDatabase();
        if (item.getItemId() == R.id.deleterowitem) {


            String[] args = {String.valueOf(info.id)};
            db.delete(FeedReaderContract.CostEntry.TABLE_NAME, "_ROWID_=?", args);
            Log.d(TAG, "Get item with rowid " + info.id);

            //ListViewItem lv = (ListViewItem) adapter.getItem(info.id);

//            int selectedItem = (Integer) adapter.getItem(info.position);
            // int rowID = Integer.parseInt(selectedItem.id);
//            listViewItems.remove(selectedItem);
//            adapter.notifyDataSetChanged();

//            db.delete(FeedReaderContract.CostEntry.TABLE_NAME, " rowid = " + selectedItem, null);
//            Log.d(TAG, FeedReaderContract.CostEntry.TABLE_NAME + " _rowid_ = " + selectedItem );

        }
        db.close();
        return super.onContextItemSelected(item);

    }

    class ListViewItem {

        public String cost, date, category, description;

    }


}
