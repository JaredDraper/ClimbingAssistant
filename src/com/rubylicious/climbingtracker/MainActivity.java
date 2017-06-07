package com.rubylicious.climbingtracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int CLIMB_REQUEST = 1; // The request code
	
	final Context context = this;
	DataSource db;
	Button btnClimbAttr, editLocation;
	Button refresh;
	Button delete;
	View climbAttrView;
	View climbRow;
	ListView list;
	ClimbsAdapter adapter;
	protected Object mActionMode;
	public int selectedItem = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AttrMaps.load();
		db = new DataSource(context);
		list = (ListView) findViewById(R.id.listview);
		loadAllClimbs(context);		
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (mActionMode != null) {
					return false;
				}
				selectedItem = position;

				// start the CAB using the ActionMode.Callback defined above
				mActionMode = MainActivity.this
						.startActionMode(mActionModeCallback);
				view.setSelected(true);
				return true;
			}
		});
		list.setOnItemClickListener(climbAttrListener);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.action_find:
			Toast.makeText(this, "Still need to create a find screen", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.action_photos:
			intent = new Intent(context, LocationAdmin.class);
			startActivity(intent);
			break;
		case R.id.action_climbs:
			intent = new Intent(context, ClimbingAttributes.class);
			startActivityForResult(intent, CLIMB_REQUEST);
			break;

		default:
			break;
		}

		return true;
	}

	public OnItemClickListener climbAttrListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			Intent i = new Intent(context, ClimbAttempts.class);
			Model item = (Model) list.getAdapter().getItem(position);
			if (item != null) {
				Bundle bundle = new Bundle();
				bundle.putInt("climbId", item.getId());
				i.putExtras(bundle);
			}
			startActivityForResult(i, CLIMB_REQUEST);
			
		}
	};
	
	private void loadAllClimbs(Context context) {
		
		ArrayList<Model> models = new ArrayList<Model>();
		db = new DataSource(context);
		List<Climb> climbs = db.getAllClimbs();
		for (Climb climb : climbs) {
			models.add(new Model(climb.getId(), climb.getName()));
		}
		adapter = new ClimbsAdapter(context, models);
		list.setAdapter(adapter);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CLIMB_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				loadAllClimbs(context);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the image capture
			}
		}
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// called when the action mode is created; startActionMode() was called
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.delete, menu);
			return true;
		}
		
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// called when the user selects a contextual menu item
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			if (item.getItemId() == R.id.delete) {
				Model model = (Model) list.getAdapter().getItem(selectedItem);
				db.deleteClimb(model.getId());
				loadAllClimbs(context);
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
