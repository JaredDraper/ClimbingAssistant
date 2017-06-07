package com.rubylicious.climbingtracker;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class LocationAdmin extends ListActivity {
	private static final int LOCATION_ACTIVITY_REQUEST_CODE = 90;
	final Context cont = this;
	protected Object mActionMode;
	public int selectedItem = -1;
	private DataSource db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DataSource(cont);

		refreshLocationsList();
		final ListView listView = getListView();
		listView.setBackgroundResource(R.drawable.background);
		registerForContextMenu(getListView());
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (mActionMode != null) {
					return false;
				}
				selectedItem = position;

				// start the CAB using the ActionMode.Callback defined above
				mActionMode = LocationAdmin.this
						.startActionMode(mActionModeCallback);
				view.setSelected(true);
				return true;
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Model item = (Model) getListAdapter().getItem(position);
		Intent i = new Intent(cont, EditLocation.class);
		Integer climbId = item.getId();
		i.putExtra("locationId", climbId);

		startActivity(i);
	}

	// add settings menu to add new locations
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			Intent intent = new Intent(cont, NewLocation.class);
			startActivityForResult(intent, LOCATION_ACTIVITY_REQUEST_CODE);
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOCATION_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				refreshLocationsList();
			}
		}
	}

	private void refreshLocationsList() {
		List<Model> locations = Location.populateLocations(db);
		IdAdapter adapter = new IdAdapter(this, locations);
		setListAdapter(adapter);
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// called when the action mode is created; startActionMode() was called
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.delete, menu);
			return true;
		}

		// the following method is called each time
		// the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// called when the user selects a contextual menu item
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			if (item.getItemId() == R.id.delete) {
				Model model = (Model) getListAdapter().getItem(selectedItem);
				if (db.canDeleteLocation(model.getId())) {
					db.deleteLocation(model.getId());
				}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(
							LocationAdmin.this);
					builder.setTitle("Cannot Delete Location");
					builder.setPositiveButton(R.string.ok, null);
					builder.setMessage("Location is attached to a climb.");
					AlertDialog errorDialog = builder.create();
					errorDialog.show();
				}
					
				refreshLocationsList();
				mode.finish();
			}
			return true;
		}

		// called when the user exits the action mode
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			selectedItem = -1;
		}
	};

}
