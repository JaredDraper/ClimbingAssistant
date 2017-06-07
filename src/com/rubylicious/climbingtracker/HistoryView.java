package com.rubylicious.climbingtracker;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryView extends FragmentActivity{
	
	private final Context context = this;
	private DataSource db;
	private TextView climbName;
	private Climb climb;
	private ListView listView;
	private HistoryAdapter adapter;
	protected Object mActionMode;
	public int selectedItem = -1;
	
@Override
protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	setContentView(R.layout.history_view);
	db = new DataSource(context);
	climbName = (TextView) findViewById(R.id.nameTxt);
	listView = (ListView) findViewById(R.id.historyListView);
	listView.setOnItemLongClickListener(new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			if (mActionMode != null) {
				return false;
			}
			selectedItem = position;

			// start the CAB using the ActionMode.Callback defined above
			mActionMode = HistoryView.this.startActionMode(mActionModeCallback);
			view.setSelected(true);
			return true;
		}
	});
	Integer climbId = null;
	if (getIntent().getExtras() != null) {
		climbId = getIntent().getExtras().getInt("climbId");
		climb = db.getClimb(climbId);
		populateView(climb.getName(), db.getAllHistoryPerClimb(climbId));
	}
	
}

private void populateView(String name, List<History> allHistoryPerClimb) {
	climbName.setText("Climb: " + name);
	adapter = new HistoryAdapter(context, allHistoryPerClimb);
	listView.setAdapter(adapter);
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
			History history = (History) listView.getAdapter().getItem(selectedItem);
			db.deleteHistory(history.getHistoryId());
			populateView(climb.getName(), db.getAllHistoryPerClimb(climb.getId()));
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

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	Intent intent;
	switch (item.getItemId()) {
	case R.id.action_find:
		Toast.makeText(this, "Menu Item find selected", Toast.LENGTH_SHORT)
				.show();
		break;
	case R.id.action_photos:
		intent = new Intent(context, LocationAdmin.class);
		startActivity(intent);
		break;

	default:
		break;
	}

	return true;
}
}

