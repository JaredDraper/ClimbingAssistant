package com.rubylicious.climbingtracker;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.rubylicious.climbingtracker.PinnedSectionListView.PinnedSectionListAdapter;

public class PinnedSectionListActivity extends ListActivity implements
		OnClickListener,OnTouchListener {
	private Context context = this;
	DataSource db;
	private ArrayList<Item> climbInfo = new ArrayList<Item>();

	class ClimbListAdapter extends ArrayAdapter<Item> implements
			PinnedSectionListAdapter {

		private final int[] COLORS = new int[] { R.color.green_light,
				R.color.orange_light, R.color.blue_light, R.color.red_light };

		public ClimbListAdapter(Context context) {
			super(context, R.layout.activity_list_view, climbInfo);
			
		

			// final int sectionsNumber = 'Z' - 'A' + 1;
			// prepareSections(sectionsNumber);

			// int sectionPosition = 0, listPosition = 0;
			// for (char i = 0; i < sectionsNumber; i++) {
			// Item section = new Item(Item.SECTION,
			// String.valueOf((char) ('A' + i)));
			// section.sectionPosition = sectionPosition;
			// section.listPosition = listPosition++;
			// onSectionAdded(section, sectionPosition);
			// add(section);
			//
			// final int itemsNumber = (int) Math.abs((Math.cos(2f * Math.PI
			// / 3f * sectionsNumber / (i + 1f)) * 25f));
			// for (int j = 0; j < itemsNumber; j++) {
			// Item item = new Item(Item.ITEM,
			// section.id.toUpperCase(Locale.ENGLISH) + " - " + j);
			// item.sectionPosition = sectionPosition;
			// item.listPosition = listPosition++;
			// add(item);
			// }
			//
			// sectionPosition++;
			// }
		}
		

		/**
		 * Return a generated view for a position.
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// reuse a given view, or inflate a new one from the xml
			View view;

			if (convertView == null) {
				LayoutInflater inflator = ((Activity) context)
						.getLayoutInflater();
				view = inflator.inflate(R.layout.climb_row_view, null);
			} else {
				view = convertView;
			}
			// TextView view = (TextView) super.getView(position, convertView,
			// parent);

			// view.setTextColor(Color.DKGRAY);
			view.setTag("" + position);
			Item item = getItem(position);
			if (item.type == Item.SECTION) {
				// view.setOnClickListener(PinnedSectionListActivity.this);
				view.setBackgroundColor(parent.getResources().getColor(
						COLORS[item.sectionPosition % COLORS.length]));
				Button name = (Button) view.findViewById(R.id.nameBtn);
				name.setText("Overhang");
				
				
				view.setClickable(true);
				return view;
			}

			// bind the data to the view object
			return this.bindData(view, position);
		}

		/**
		 * Bind the provided data to the view. This is the only method not
		 * required by base adapter.
		 */
		public View bindData(View view, int position) {
			// make sure it's worth drawing the view
			if (climbInfo.get(position) == null) {
				return view;
			}

			db = new DataSource(context);
			db.openRead();

			// pull out the object
			Item item = climbInfo.get(position);
			Climb climb = db.getClimb(Integer.parseInt(item.id));

			Button type = (Button) view.findViewById(R.id.typeBtn);
			int typeId = getResourceId(context,
					AttrMaps.typeMap.get(climb.getTypeId()), "drawable");
			type.setBackgroundResource(typeId);
			//type.setOnClickListener(climbListener);
			type.setTag(climb.getId());

			Button color = (Button) view.findViewById(R.id.colorBtn);
			int colorId = getResourceId(context,
					AttrMaps.colorMap.get(climb.getColor()), "drawable");
			color.setBackgroundResource(colorId);
			//color.setOnClickListener(climbListener);
			color.setTag(climb.getId());

			Button name = (Button) view.findViewById(R.id.nameBtn);
			name.setText(climb.getName());
			//name.setOnClickListener(climbListener);
			name.setTag(climb.getId());

			Button date = (Button) view.findViewById(R.id.dateBtn);
			date.setText(climb.getFirstAttemptDate().toString());
			//date.setOnClickListener(climbListener);
			date.setTag(climb.getId());

			Button grade = (Button) view.findViewById(R.id.gradeBtn);
			grade.setText(AttrMaps.gradeMap.get(climb.getGradeId()));
			//grade.setOnClickListener(climbListener);
			grade.setTag(climb.getId());

			Button status = (Button) view.findViewById(R.id.statusBtn);
			status.setText(AttrMaps.statusMap.get(climb.getStatus()));
			//status.setOnClickListener(climbListener);
			status.setTag(climb.getId());

			
			// return the final view object
			return view;
		}
		
		
		private OnClickListener climbListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Integer climbId = (Integer) v.getTag();
				Intent i = new Intent(context, ClimbingAttributes.class);
				if(climbId != null){
				Bundle bundle = new Bundle();
				bundle.putInt("climbId", climbId);
				i.putExtras(bundle);
				}

				startActivity(i);

			}
		};

		public int getResourceId(Context context, String name,
				String resourceType) {
			return context.getResources().getIdentifier(name, resourceType,
					context.getPackageName());
		}

		protected void prepareSections(int sectionsNumber) {
		}

		protected void onSectionAdded(Item section, int sectionPosition) {
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position).type;
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return viewType == Item.SECTION;
		}

	}

	class FastScrollAdapter extends ClimbListAdapter implements SectionIndexer {

		private Item[] sections;

		public FastScrollAdapter(Context context) {
			super(context);
		}

		@Override
		protected void prepareSections(int sectionsNumber) {
			sections = new Item[sectionsNumber];
		}

		@Override
		protected void onSectionAdded(Item section, int sectionPosition) {
			sections[sectionPosition] = section;
		}

		@Override
		public Item[] getSections() {
			return sections;
		}

		@Override
		public int getPositionForSection(int section) {
			if (section >= sections.length) {
				section = sections.length - 1;
			}
			return sections[section].listPosition;
		}

		@Override
		public int getSectionForPosition(int position) {
			if (position >= getCount()) {
				position = getCount() - 1;
			}
			return getItem(position).sectionPosition;
		}

	}

	static class Item {

		public String id;
		public static final int ITEM = 0;
		public static final int SECTION = 1;

		public final int type;
		public int sectionPosition;
		public int listPosition;

		public Item(int type, String id) {
			super();
			this.type = type;
			this.id = id;
		}

		@Override
		public String toString() {
			return id;
		}
	}

	private boolean hasHeaderAndFooter;
	private boolean isFastScroll;
	private boolean addPadding;
	private boolean isShadowVisible = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);
		if (savedInstanceState != null) {
			isFastScroll = savedInstanceState.getBoolean("isFastScroll");
			addPadding = savedInstanceState.getBoolean("addPadding");
			isShadowVisible = savedInstanceState.getBoolean("isShadowVisible");
			hasHeaderAndFooter = savedInstanceState
					.getBoolean("hasHeaderAndFooter");
		}
		initializeHeaderAndFooter();
		initializePadding();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isFastScroll", isFastScroll);
		outState.putBoolean("addPadding", addPadding);
		outState.putBoolean("isShadowVisible", isShadowVisible);
		outState.putBoolean("hasHeaderAndFooter", hasHeaderAndFooter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Item item = (Item) getListView().getAdapter().getItem(position);
		if (item != null) {
			Intent i = new Intent(this, ClimbingAttributes.class);
			// if (v.getTag() != null) {
			Bundle bundle = new Bundle();
			Integer climbId = 1; // Integer.valueOf(item.text);
			bundle.putInt("climbId", climbId);
			i.putExtras(bundle);
			// }
			startActivity(i);
		} else {
			Toast.makeText(this, "Item " + position, Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.getItem(0).setChecked(isFastScroll);
		menu.getItem(1).setChecked(addPadding);
		menu.getItem(2).setChecked(isShadowVisible);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_fastscroll:
			isFastScroll = !isFastScroll;
			item.setChecked(isFastScroll);
			initializeAdapter();
			break;
		case R.id.action_addpadding:
			addPadding = !addPadding;
			item.setChecked(addPadding);
			initializePadding();
			break;
		case R.id.action_showShadow:
			isShadowVisible = !isShadowVisible;
			item.setChecked(isShadowVisible);
			((PinnedSectionListView) getListView())
					.setShadowVisible(isShadowVisible);
			break;
		case R.id.action_showHeaderAndFooter:
			hasHeaderAndFooter = !hasHeaderAndFooter;
			item.setChecked(hasHeaderAndFooter);
			initializeHeaderAndFooter();
			break;
		}
		return true;
	}

	private void initializePadding() {
		float density = getResources().getDisplayMetrics().density;
		int padding = addPadding ? (int) (16 * density) : 0;
		getListView().setPadding(padding, padding, padding, padding);
	}

	private void initializeHeaderAndFooter() {
		setListAdapter(null);
		if (hasHeaderAndFooter) {
			ListView list = getListView();

			LayoutInflater inflater = LayoutInflater.from(this);
			TextView header1 = (TextView) inflater.inflate(
					android.R.layout.simple_list_item_1, list, false);
			header1.setText("First header");
			list.addHeaderView(header1);

			TextView header2 = (TextView) inflater.inflate(
					android.R.layout.simple_list_item_1, list, false);
			header2.setText("Second header");
			list.addHeaderView(header2);

			TextView footer = (TextView) inflater.inflate(
					android.R.layout.simple_list_item_1, list, false);
			footer.setText("Single footer");
			list.addFooterView(footer);
		}
		initializeAdapter();
	}

	@SuppressLint("NewApi")
	private void initializeAdapter() {
		loadAllClimbs(context);
		getListView().setFastScrollEnabled(isFastScroll);
		if (isFastScroll) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				getListView().setFastScrollAlwaysVisible(true);
			}
			setListAdapter(new ClimbListAdapter(this));
		} else {
			// specify the list adaptor
			setListAdapter(new ClimbListAdapter(this));
			// setListAdapter(new SimpleAdapter(this,
			// android.R.layout.simple_list_item_1, android.R.id.text1));
		}
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(this, "Item: " + v.getTag(), Toast.LENGTH_SHORT).show();
	}

	private void loadAllClimbs(Context context) {
		db = new DataSource(context);
		List<Climb> climbs = db.getAllClimbs();
		for (Climb climb : climbs) {
			Item item = new Item(ListItem.ITEM, String.valueOf(climb.getId()));
			climbInfo.add(item);
		}
		Item item = new Item(ListItem.SECTION, "12");
		climbInfo.add(item);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Toast.makeText(this, "Item: " + v.getTag(), Toast.LENGTH_SHORT).show();
		return true;
	}

}