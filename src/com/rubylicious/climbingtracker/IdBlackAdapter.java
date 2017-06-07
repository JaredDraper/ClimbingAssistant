package com.rubylicious.climbingtracker;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IdBlackAdapter extends BaseAdapter {
	// store the context (as an inflated layout)
	private LayoutInflater inflater;
	final Context context;
	// store (a reference to) the data
	private List<Model> models;
	
	/**
	 * Default constructor. Creates the new Adaptor object to
	 * provide a ListView with data.
	 * @param context
	 * @param resource
	 * @param data
	 */
	public IdBlackAdapter(Context context, List<Model> climbs) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.models = climbs;
	}

	// How many items are in the data set represented by this Adapter.
	@Override
	public int getCount() {
		return models.size();
	}

	// Get the data item associated with the specified position in the data
	// set.
	@Override
	public Object getItem(int position) {
		return models.get(position);
	}
	
	public int getIndexWithId(int id) {
		for(Model model: models){
			if(model.getId() == id){
				return models.indexOf(model);
			}
		}
		return 0;
	}

	// Get the row id associated with the specified position in the list.
	@Override
	public long getItemId(int position) {
		return models.get(position).getId();
	}

	// Get a View that displays the data at the specified position in the
	// data set.
	// You can either create a View manually or inflate it from an XML
	// layout file.
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			// LayoutInflater class is used to instantiate layout XML file
			// into its corresponding View objects.
			//
			convertView = inflater.inflate(
					R.layout.row_black_text, null);
		}

		// pull out the object
		Model model = models.get(position);

		convertView.setTag(model.getId());

		TextView name = (TextView) convertView.findViewById(R.id.name);
		name.setText(model.getName());

		return convertView;
	}
}

