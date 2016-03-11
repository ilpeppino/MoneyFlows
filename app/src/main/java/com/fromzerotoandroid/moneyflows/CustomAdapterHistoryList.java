package com.fromzerotoandroid.moneyflows;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fromzerotoandroid.moneyflows.HistoryList.ListViewItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CustomAdapterHistoryList extends BaseAdapter {

    public static final String TAG = "Class: CustomAdapter";

    List<ListViewItem> result = new ArrayList<>();
    Context context;
    private static LayoutInflater inflater = null;

    public CustomAdapterHistoryList(HistoryList historyList, List<ListViewItem> result) {
        // TODO Auto-generated constructor stub

        context = historyList;
        this.result = result;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        Log.d(TAG, "Calling getCount method...Return: " + result.size());

        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        Log.d(TAG, "Calling getItem method...Return: " + position);
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "Calling getItemId method...Return: " + position);
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tvCost, tvCategory, tvDate, tvDescription;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Calling getView...Position: " + position);
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.historylist_rowitem, null);

        ListViewItem item = result.get(position);

        holder.tvCost = (TextView) rowView.findViewById(R.id.customlistitem_Cost);
        holder.tvCategory = (TextView) rowView.findViewById(R.id.customlistitem_Category);
        holder.tvDate = (TextView) rowView.findViewById(R.id.customlistitem_Date);
        holder.tvDescription = (TextView) rowView.findViewById(R.id.customlistitem_Description);

        int index = Arrays.asList(Helper.categoryNames).indexOf(item.category);

        holder.tvCost.setText(item.cost);
        holder.tvCategory.setText(item.category);
        holder.tvCategory.setBackgroundResource(Helper.categoryColors[index]);
        holder.tvDate.setText(item.date);
        holder.tvDescription.setText(item.description);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked " + result.get(position).category.toString() + " " + result.get(position).cost.toString() + " " + result.get(position).date.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return rowView;
    }
}
