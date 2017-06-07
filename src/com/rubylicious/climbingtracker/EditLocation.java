package com.rubylicious.climbingtracker;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditLocation extends Activity {
	private static final int AREA_ACTIVITY_REQUEST_CODE = 80;
	final Context cont = this;
	DataSource db;
	TextView location;
	TextView address;
	TextView lattitude;
	TextView longitude;
	TextView GPSLabel;
	TextView comments;
	ListView areaList;
	Integer locationId;
	double lat;
	double lon;
	protected Object mActionMode;
	public int selectedItem = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_location);
		db = new DataSource(cont);

		location = (TextView) findViewById(R.id.edtLocation);
		address = (TextView) findViewById(R.id.edtAddress);
		lattitude = (TextView) findViewById(R.id.lattitude);
		longitude = (TextView) findViewById(R.id.longitude);
		GPSLabel = (TextView) findViewById(R.id.edtGPSlbl);
		GPSLabel.setClickable(true);
		GPSLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(lat != 0){
				String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat,
						lon);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				cont.startActivity(intent);
				}
			}
		});
		comments = (TextView) findViewById(R.id.edtLocationComments);
		areaList = (ListView) findViewById(R.id.areaList);
		locationId = getIntent().getIntExtra("locationId", 1000);
		Location loc = db.getLocation(locationId);
		if (loc != null) {
			location.setText(loc.getName());
			address.setText(loc.getAddress());
			if (loc.getGpsCoordinates().contains(" ")) {
				String[] coordinates = loc.getGpsCoordinates().split(" ");
				try{
				lat = Double.parseDouble(coordinates[0].trim());
				lon = Double.parseDouble(coordinates[1].trim());
				}catch(Exception e){
					// do nothing
				}
			}
			lattitude.setText(String.valueOf(lat));
			longitude.setText(String.valueOf(lon));
			comments.setText(loc.getComments());
			loadAllAreas();

		}

		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(saveBtnListener);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(cancelBtnListener);
		areaList.setOnItemClickListener(itemClickListener);
		areaList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (mActionMode != null) {
					return false;
				}
				selectedItem = position;

				// start the CAB using the ActionMode.Callback defined above
				mActionMode = EditLocation.this
						.startActionMode(mActionModeCallback);
				view.setSelected(true);
				return true;
			}
		});
	}

	public OnClickListener saveBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// save fields to DB.
			String name = location.getText().toString();
			String addr = address.getText().toString();
			String coordinates = lattitude.getText().toString() + " " + longitude.getText().toString();
			String cmmts = comments.getText().toString();
			Location loc = new Location(locationId, name, addr, coordinates,
					cmmts);
			db.updateLocation(loc);
			// update list of areas
			getIntent().putExtra("id", loc.getId());
			setResult(RESULT_OK, getIntent());
			Toast.makeText(cont, "Location was Saved", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	};

	public OnClickListener cancelBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};

	private void loadAllAreas() {
		List<Model> areas = Area.populateAreas(locationId, db);
		IdAdapter adapter = new IdAdapter(this, areas);
		areaList.setAdapter(adapter);

	}

	// add settings menu to add new areas
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_area, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addArea:
			Intent intent = new Intent(cont, NewArea.class);
			intent.putExtra("locationId", locationId);
			intent.putExtra("new", true);
			startActivityForResult(intent, AREA_ACTIVITY_REQUEST_CODE);
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AREA_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				loadAllAreas();
			}
		}
	}

	public OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			Model item = (Model) areaList.getAdapter().getItem(position);
			Intent i = new Intent(cont, NewArea.class);
			Integer climbId = item.getId();
			i.putExtra("areaId", climbId);
			i.putExtra("locationId", locationId);

			startActivity(i);
		}
	};

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
				Model model = (Model) areaList.getAdapter().getItem(
						selectedItem);
				if (db.canDeleteArea(model.getId())) {
					db.deleteArea(model.getId());
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							EditLocation.this);
					builder.setTitle("Cannot Delete Area");
					builder.setPositiveButton(R.string.ok, null);
					builder.setMessage("Area is attached to a climb.");
					AlertDialog errorDialog = builder.create();
					errorDialog.show();
				}
				loadAllAreas();
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
