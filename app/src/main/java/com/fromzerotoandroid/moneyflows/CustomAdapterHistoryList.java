package com.fromzerotoandroid.moneyflows;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fromzerotoandroid.moneyflows.HistoryList.ListViewItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CustomAdapterHistoryList extends BaseAdapter implements Filterable {

    public static final String TAG = "Class: CustomAdapter";
    private static LayoutInflater inflater = null;
    List<ListViewItem> result = new ArrayList<>();
    List<ListViewItem> filteredResult = new ArrayList<>();
    Context context;
    private ItemFilter mFilter = new ItemFilter();
//    public int mSelectedItem;
//    public View mSelectedView;
//    public boolean isItemSelected;


    public CustomAdapterHistoryList(HistoryList historyList, List<ListViewItem> result) {

        context = historyList;
        this.result = result;
        this.filteredResult = result;
//        isItemSelected = false;
//        mSelectedItem = -1;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        Log.d(TAG, "Calling getCount method...Return: " + result.size());


        return filteredResult.size();
    }

    @Override
    public Object getItem(int position) {
        Log.d(TAG, "Calling getItem method...Return: " + position);

        return filteredResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "Calling getItemId method...Return: " + position);

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Calling getView...Position: " + position);

        final Holder holder = new Holder();
        final View rowView;

        rowView = inflater.inflate(R.layout.historylist_rowitem, null);


        ListViewItem item = filteredResult.get(position);

        holder.tvTimestamp = (TextView) rowView.findViewById(R.id.customlistitem_Id);
        holder.tvCost = (TextView) rowView.findViewById(R.id.customlistitem_Cost);
        holder.tvCategory = (TextView) rowView.findViewById(R.id.customlistitem_Category);
        holder.tvDate = (TextView) rowView.findViewById(R.id.customlistitem_Date);
        holder.tvDescription = (TextView) rowView.findViewById(R.id.customlistitem_Description);

        int index = Arrays.asList(Helper.categoryNames).indexOf(item.category);

        holder.tvTimestamp.setText(item.idtimestamp);
        holder.tvCost.setText(item.cost);
        holder.tvCategory.setText(item.category);
        holder.tvCategory.setBackgroundResource(Helper.categoryColors[index]);
        holder.tvDate.setText(item.date);
        holder.tvDescription.setText(item.description);

//        rowView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        return rowView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class Holder {
        TextView tvTimestamp, tvCost, tvCategory, tvDate, tvDescription;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<ListViewItem> list = result;

            int count = list.size();
            final ArrayList<ListViewItem> nlist = new ArrayList<ListViewItem>(count);

            ListViewItem listItem;

            for (int i = 0; i < count; i++) {
                listItem = list.get(i);
                if (listItem.description.toLowerCase().contains(filterString)) {
                    nlist.add(listItem);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredResult = (ArrayList<ListViewItem>) results.values;
            notifyDataSetChanged();
        }

    }
}

