package com.rubylicious.climbingtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

	public class ImageDropDownAdapter extends ArrayAdapter<String> {
		Integer[] icons;
		LayoutInflater inflate;

		public ImageDropDownAdapter(LayoutInflater inflater, Context context, int textViewResourceId,
				String[] objects, Integer[] icons) {
			super(context, textViewResourceId, objects);
			this.inflate = inflater;
			this.icons = icons;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {

			View row = inflate.inflate(R.layout.row, parent, false);

			ImageView icon = (ImageView) row.findViewById(R.id.image);
			icon.setImageResource(icons[position]);

			return row;
		}
	}

