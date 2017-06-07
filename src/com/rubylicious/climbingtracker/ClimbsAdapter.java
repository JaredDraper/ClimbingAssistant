package com.rubylicious.climbingtracker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClimbsAdapter extends BaseAdapter {
	// store the context (as an inflated layout)
	private LayoutInflater inflater;
	DataSource db;
	final Context context;
	// store (a reference to) the data
	private ArrayList<Model> climbs;
	
	/**
	 * Default constructor. Creates the new Adaptor object to
	 * provide a ListView with data.
	 * @param context
	 * @param resource
	 * @param data
	 */
	public ClimbsAdapter(Context context, ArrayList<Model> climbs) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.db = new DataSource(context);
		this.climbs = climbs;
	}

	// How many items are in the data set represented by this Adapter.
	@Override
	public int getCount() {
		return climbs.size();
	}

	// Get the data item associated with the specified position in the data
	// set.
	@Override
	public Object getItem(int position) {
		return climbs.get(position);
	}

	// Get the row id associated with the specified position in the list.
	@Override
	public long getItemId(int position) {
		return climbs.get(position).getId();
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
					R.layout.climb_row_view, null);
		}

		// pull out the object
		Model model = climbs.get(position);
		Climb climb = db.getClimb(model.getId());

		convertView.setTag(climb.getId());
		TextView type = (TextView) convertView.findViewById(R.id.typeBtn);
		int typeId = getResourceId(context,
				AttrMaps.typeMap.get(climb.getTypeId()), "drawable");
		type.setBackgroundResource(typeId);

		TextView color = (TextView) convertView.findViewById(R.id.colorBtn);
		int colorId = getResourceId(context,
				AttrMaps.colorMap.get(climb.getColor()), "drawable");
		color.setBackgroundResource(colorId);

		TextView name = (TextView) convertView.findViewById(R.id.nameBtn);
		name.setText(climb.getName());

		TextView date = (TextView) convertView.findViewById(R.id.dateBtn);
		date.setText(climb.getFirstAttemptDate() == null? "No Date" :climb.getFirstAttemptDate().toString());

		TextView grade = (TextView) convertView.findViewById(R.id.gradeBtn);
		grade.setText(AttrMaps.gradeMap.get(climb.getGradeId()));

		TextView status = (TextView) convertView.findViewById(R.id.statusBtn);
		int statusId = getResourceId(context, AttrMaps.statusMap.get(climb.getStatus()), "drawable");
		status.setBackgroundResource(statusId);

		return convertView;
	}
	
	public static int getResourceId(Context context, String name,
			String resourceType) {
		return context.getResources().getIdentifier(name, resourceType,
				context.getPackageName());
	}
}
