package com.rubylicious.climbingtracker;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FirstListView extends Activity {

	private View viewContainer;
	private Context context = this;
	DataSource db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);
		ListView l = (ListView) findViewById(R.id.listview);
		ClimbsAdapter adapter = new ClimbsAdapter(context, loadAllClimbs(context));

		viewContainer = findViewById(R.id.undobar);
		l.setAdapter(adapter);
		l.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Toast.makeText(FirstListView.this, "Item long clicked",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		l.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Item item = (Item)
				// getListView().getAdapter().getItem(position);
				// if (item != null) {
				Intent i = new Intent(FirstListView.this,
						ClimbingAttributes.class);
				// if (v.getTag() != null) {
				// Bundle bundle = new Bundle();
				// Integer climbId = 1; // Integer.valueOf(item.text);
				// bundle.putInt("climbId", climbId);
				// i.putExtras(bundle);
				// }
				startActivity(i);
				Toast.makeText(FirstListView.this, "Item clicked",
						Toast.LENGTH_SHORT).show();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private Model get(int id, String name) {
		return new Model(id, name);
	}

	private ArrayList<Model> loadAllClimbs(Context context) {
		ArrayList<Model> list = new ArrayList<Model>();
		db = new DataSource(context);
		List<Climb> climbs = db.getAllClimbs();
		for (Climb climb : climbs) {
			list.add(get(climb.getId(), climb.getName()));
			// Item item = new Item(ListItem.ITEM,
			// String.valueOf(climb.getId()));
		}
		return list;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		showUndo(viewContainer);
		return true;
	}

	public void onClick(View view) {
		Toast.makeText(this, "Deletion undone", Toast.LENGTH_LONG).show();
		viewContainer.setVisibility(View.GONE);
	}

	@SuppressLint("NewApi")
	public static void showUndo(final View viewContainer) {
		viewContainer.setVisibility(View.VISIBLE);
		viewContainer.setAlpha(1);
		viewContainer.animate().alpha(0.4f).setDuration(5000)
				.withEndAction(new Runnable() {

					@Override
					public void run() {
						viewContainer.setVisibility(View.GONE);
					}
				});

	}
}