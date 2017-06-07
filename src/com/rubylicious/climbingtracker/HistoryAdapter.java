package com.rubylicious.climbingtracker;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter{
	
	private LayoutInflater inflater;
	DataSource db;
	final Context context;
	// store (a reference to) the data
	private List<History> histories;

	public HistoryAdapter(Context context, List<History> histories) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.db = new DataSource(context);
		this.histories = histories;
	}

	@Override
	public int getCount() {
		return histories.size();
	}

	@Override
	public Object getItem(int position) {
		return histories.get(position);
	}

	// Get the row id associated with the specified position in the list.
	@Override
	public long getItemId(int position) {
		return histories.get(position).getHistoryId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			// LayoutInflater class is used to instantiate layout XML file
			// into its corresponding View objects.
			//
			convertView = inflater.inflate(
					R.layout.history_row_view, null);
		}

		// pull out the object
		History history = histories.get(position);

		convertView.setTag(history.getHistoryId());
		TextView date = (TextView) convertView.findViewById(R.id.dateBtn);
		date.setText(formatDate(history.getDate()));


		TextView grade = (TextView) convertView.findViewById(R.id.movesBtn);
		grade.setText("Moves: " + String.valueOf(history.getMoves()));


		return convertView;
	}

	private CharSequence formatDate(DateTime date) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM-dd-yyyy hh:mmaa");
		return date.toString(fmt);
	}
}
