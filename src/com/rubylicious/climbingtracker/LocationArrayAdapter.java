package com.rubylicious.climbingtracker;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LocationArrayAdapter{
//	    private final Activity activity;
//	    private final List stocks;
//
//	    public LocationArrayAdapter(Activity activity, List objects) {
//	        super(activity, R.layout.single_list_item_view, objects);
//	        this.activity = activity;
//	        this.stocks = objects;
//	    }
//
//	    @Override
//	    public View getView(int position, View convertView, ViewGroup parent) {
//
//	        View rowView = convertView;
//	        ContactStockView sv = null;
//	        if (rowView == null) {
//	            // Get a new instance of the row layout view
//	            LayoutInflater inflater = activity.getLayoutInflater();
//	            rowView = inflater.inflate(
//	                    R.layout.listview_detail_tab_contact_list, null);
//
//	            // Hold the view objects in an object,
//	            // so they don't need to be re-fetched
//	            sv = new ContactStockView();
//	            sv.name = (TextView) rowView.findViewById(R.id.contact_name);
//
//	            sv.number = (TextView) rowView.findViewById(R.id.contact_number);
//
//	            // Cache the view objects in the tag,
//	            // so they can be re-accessed later
//	            rowView.setTag(sv);
//	        } else {
//	            sv = (ContactStockView) rowView.getTag();
//	        }
//	        // Transfer the stock data from the data object
//	        // to the view objects
//	        Location currentStock = (Location) stocks.get(position);
//	        sv.name.setText(currentStock.getName());
//	        sv.
//	        sv.number.setText(currentStock.getNumber());
//
//	        // TODO Auto-generated method stub
//	        return rowView;
//	    }
//
//	    protected static class ContactStockView {
//	        protected TextView name;
//	        protected TextView number;
//	    }
//	
}
