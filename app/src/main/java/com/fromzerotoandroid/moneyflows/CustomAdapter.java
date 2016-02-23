package com.fromzerotoandroid.moneyflows;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fromzerotoandroid.moneyflows.HistoryList.ListViewItem;

import java.util.ArrayList;
import java.util.List;


public class CustomAdapter extends BaseAdapter {

    List<ListViewItem> result = new ArrayList<>();
    Context context;
    int[] imageId;
    private static LayoutInflater inflater = null;

    public CustomAdapter(HistoryList historyList, List<ListViewItem> result) {
        // TODO Auto-generated constructor stub

        context = historyList;
        this.result = result;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tvCost, tvCategory, tvDate;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.customlistitem, null);

        ListViewItem item = result.get(position);

        holder.tvCost = (TextView) rowView.findViewById(R.id.customlistiem_Cost);
        holder.tvCategory = (TextView) rowView.findViewById(R.id.customlistiem_Category);
        holder.tvDate = (TextView) rowView.findViewById(R.id.customlistiem_Date);

        holder.tvCost.setText(item.cost);
        holder.tvCategory.setText(item.category);
        holder.tvDate.setText(item.date);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked " + result.get(position), Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }
}
