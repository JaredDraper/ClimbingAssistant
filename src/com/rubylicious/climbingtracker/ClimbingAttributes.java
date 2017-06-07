package com.rubylicious.climbingtracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ClimbingAttributes extends FragmentActivity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int NEW_LOCATION_ACTIVITY_REQUEST_CODE = 101;
	private static final int NEW_AREA_ACTIVITY_REQUEST_CODE = 102;
	final Context context = this;
	static final int DATE_DIALOG_ID = 0;
	DataSource db;
	boolean isInside = true;
	boolean isNewClimb = true;
	Integer climbId = null;
	byte[] bmpPhoto;
	List<Map<String, Object>> injuryList = new ArrayList<Map<String, Object>>();
	ArrayAdapter<String> injuryDataAdapter;
	// stores the image database icons
	private static Integer[] injuryIconIndexes = { R.drawable.ok_injury,
			R.drawable.ow };
	private static Integer[] typeIconIndexes = { R.drawable.top_rope,
			R.drawable.bouldering, R.drawable.sport_route, R.drawable.gear };
	private static Integer[] slopeIconIndexes = { R.drawable.mixed_slope,
			R.drawable.slab, R.drawable.vertical, R.drawable.overhanging };
	private static Integer[] colorIconIndexes = { R.drawable.all_colors,
			R.drawable.black, R.drawable.brown, R.drawable.dark_blue,
			R.drawable.dark_green, R.drawable.green, R.drawable.hot_pink,
			R.drawable.light_blue, R.drawable.olive_green, R.drawable.orange,
			R.drawable.red, R.drawable.tan, R.drawable.yellow };
	private static Integer[] statusIconIndexes = { R.drawable.project,
			R.drawable.redpoint, R.drawable.onsight };
	Map<String, Object> map;
	Button newLocation, newArea, moves, CAL, save, firstAttemptDate, sendDate,
			sendTotal, firstAttemptTotal, photo;
	Spinner location, area, injury, type, slope, status, grade, color;
	TextView name, comments;
	RadioButton gymRbtn;
	RadioButton cragRbtn;
	RadioButton trainRbtn;

	IdBlackAdapter locationsAdapter;
	IdBlackAdapter areasAdapter;
	ArrayAdapter<String> statusAdapter;
	ArrayAdapter<String> colorAdapter;
	ArrayAdapter<String> gradeAdapter;
	private PopupWindow pw;
	private TextView numberPickerInput;
	private RatingBar rating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.climbing_attributes);
		db = new DataSource(context);
		save = (Button) findViewById(R.id.btnSave);
		save.setOnClickListener(saveBtnListener);
		gymRbtn = (RadioButton) findViewById(R.id.gymRbtn);
		gymRbtn.setChecked(true);
		cragRbtn = (RadioButton) findViewById(R.id.cragRbtn);
		newLocation = (Button) findViewById(R.id.btnNewLocation);
		newLocation.setOnClickListener(chooseLocationListener);
		newArea = (Button) findViewById(R.id.btnNewArea);
		newArea.setOnClickListener(chooseAreaListener);
		photo = (Button) findViewById(R.id.btnPhoto);
		photo.setOnClickListener(startPhotoListener);
		rating = (RatingBar) findViewById(R.id.ratingBar);
		rating.setStepSize((float) 0.5);

		location = (Spinner) findViewById(R.id.spnLocation);
		populateLocations();
		location.setOnItemSelectedListener(locationListener);

		area = (Spinner) findViewById(R.id.spnArea);
		populateAreas(location.getSelectedItemPosition());

		status = (Spinner) findViewById(R.id.spnStatus);
		status.setAdapter(new MyAdapter(ClimbingAttributes.this, R.layout.row,
				getResources().getStringArray(R.array.status), statusIconIndexes));

		grade = (Spinner) findViewById(R.id.spnGrade);
		gradeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.grade));
		grade.setAdapter(gradeAdapter);

		color = (Spinner) findViewById(R.id.spnColor);
		color.setAdapter(new MyAdapter(ClimbingAttributes.this, R.layout.row,
				getResources().getStringArray(R.array.color), colorIconIndexes));

		injury = (Spinner) findViewById(R.id.spnInjury);
		injury.setAdapter(new MyAdapter(ClimbingAttributes.this, R.layout.row,
				getResources().getStringArray(R.array.injury), injuryIconIndexes));

		type = (Spinner) findViewById(R.id.spnType);
		type.setAdapter(new MyAdapter(ClimbingAttributes.this, R.layout.row,
				getResources().getStringArray(R.array.type), typeIconIndexes));

		slope = (Spinner) findViewById(R.id.spnSlope);
		slope.setAdapter(new MyAdapter(ClimbingAttributes.this, R.layout.row,
				getResources().getStringArray(R.array.slope), slopeIconIndexes));

		name = (TextView) findViewById(R.id.climbNameEdt);
		comments = (TextView) findViewById(R.id.comments);

		firstAttemptDate = (Button) findViewById(R.id.firstAttemptDateBtn);
		sendDate = (Button) findViewById(R.id.sendDateBtn);
		moves = (Button) findViewById(R.id.btnMoves);
		moves.setOnClickListener(movesBtnListener);
		CAL = (Button) findViewById(R.id.btnCal);
		CAL.setOnClickListener(calBtnListener);
		sendTotal = (Button) findViewById(R.id.sendTotal);
		sendTotal.setOnClickListener(sendTotalListener);
		firstAttemptTotal = (Button) findViewById(R.id.firstAttemptTotal);
		firstAttemptTotal.setOnClickListener(firstAttemptTotalListener);
		Integer climbId = null;
		if (getIntent().getExtras() != null) {
			climbId = getIntent().getExtras().getInt("climbId");
		}
		populateClimb(climbId);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_climb, menu);
		return true;
	}

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

	private void populateAreas(int locationId) {
		List<Model> areas = Area.populateAreas(locationId, db);
		areasAdapter = new IdBlackAdapter(context, areas);
		area.setAdapter(areasAdapter);
	}

	private void populateLocations() {
		List<Model> locations = Location.populateLocations(db);
		locationsAdapter = new IdBlackAdapter(context, locations);
		location.setAdapter(locationsAdapter);
	}

	private void populateClimb(Integer chosenClimbId) {
		if (chosenClimbId != null) {
			isNewClimb = false;

			Climb climb = db.getClimb(chosenClimbId);
			climbId = climb.getId();
			int locIdx = locationsAdapter.getIndexWithId(climb.getLocationId());
			location.setSelection(locIdx);

			populateAreas(climb.getLocationId());
			int areaIdx = areasAdapter.getIndexWithId(climb.getAreaId());
			area.setSelection(areaIdx);
			injury.setSelection(climb.getInjury());
			type.setSelection(climb.getTypeId());
			slope.setSelection(climb.getSlopeId());
			name.setText(climb.getName());
			comments.setText(climb.getComments());
			grade.setSelection(climb.getGradeId());
			color.setSelection(climb.getColor());
			moves.setText(climb.getMoves().toString());
			CAL.setText(climb.getCal().toString());
			sendTotal.setText(climb.getSendTotal().toString());
			firstAttemptTotal.setText(climb.getFirstAttemptTotal().toString());
			status.setSelection(climb.getStatus());
			bmpPhoto = climb.getPhoto();
			rating.setRating(climb.getRating().floatValue());

			if (climb.getSendDate() != null) {
				sendDate.setText(dateToString(climb.getSendDate()));
			}
			if (climb.getFirstAttemptDate() != null) {
				firstAttemptDate.setText(dateToString(climb
						.getFirstAttemptDate()));
			}
			isInside = climb.isGym();
			RadioGroup rg = (RadioGroup) findViewById(R.id.radioBtnGroup);
			if (isInside) {
				rg.check(R.id.gymRbtn);
			} else {
				rg.check(R.id.cragRbtn);
			}
		}

	}

	private String dateToString(LocalDate dt) {
		String month = String.valueOf(dt.getMonthOfYear());
		String day = String.valueOf(dt.getDayOfMonth());
		String year = String.valueOf(dt.getYear());
		return (month + "/" + day + "/" + year);
	}

	public class MyAdapter extends ArrayAdapter<String> {
		Integer[] icons;

		public MyAdapter(Context context, int textViewResourceId,
				String[] objects, Integer[] icons) {
			super(context, textViewResourceId, objects);
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

			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);

			ImageView icon = (ImageView) row.findViewById(R.id.image);
			icon.setImageResource(icons[position]);

			return row;
		}
	}

	public void showDatePickerDialog(View v) {
		showDatePicker(v);
	}

	private void showDatePicker(View v) {
		String dateId = (String) v.getTag();
		DatePickerFragment date = new DatePickerFragment();
		/**
		 * Set Up Current Date Into dialog
		 */
		Bundle args = new Bundle();
		String dateStr = ((Button) v).getText().toString();
		if (((Button) v).getText().toString().equals("") == false) {
			String[] dateArgs = dateStr.split("/");
			args.putInt("month", (Integer.parseInt(dateArgs[0]) - 1));
			args.putInt("day", Integer.parseInt(dateArgs[1]));
			args.putInt("year", Integer.parseInt(dateArgs[2]));

		} else {
			Calendar calender = Calendar.getInstance();
			args.putInt("year", calender.get(Calendar.YEAR));
			args.putInt("month", calender.get(Calendar.MONTH));
			args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
		}
		date.setArguments(args);
		/**
		 * Set Call back to capture selected date
		 */
		if (dateId.equals("sendDate")) {
			date.setCallBack(onSendDate);
		} else {
			date.setCallBack(onFirstAttemptDate);
		}
		date.show(getSupportFragmentManager(), "Date Picker");
	}

	OnDateSetListener onSendDate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			String date = String.valueOf(month + 1) + "/" + String.valueOf(day)
					+ "/" + String.valueOf(year);
			sendDate.setText(date);
		}
	};

	OnDateSetListener onFirstAttemptDate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			String date = String.valueOf(month + 1) + "/" + String.valueOf(day)
					+ "/" + String.valueOf(year);
			firstAttemptDate.setText(date);
		}
	};

	// implement action listener type of OnItemSelectedListener
	private OnItemSelectedListener locationListener = new OnItemSelectedListener() {
		// do what ever you want to do when item selected
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			// i get the item using selected item position and set it into
			// selectedWeekDay
			Model locModel = (Model) location.getSelectedItem();
			populateAreas(locModel.getId());
		}

		public void onNothingSelected(AdapterView<?> parent) {

		}

	};

	public OnClickListener movesBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			new NumberPickerWidget(moves);
		}
	};

	public OnClickListener calBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			new NumberPickerWidget(CAL);
		}
	};

	public OnClickListener sendTotalListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			new NumberPickerWidget(sendTotal);
		}
	};

	public OnClickListener firstAttemptTotalListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			new NumberPickerWidget(firstAttemptTotal);
		}
	};

	private OnClickListener saveBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(validate() == false){
				return;
			}
			Model locModel = (Model) location.getSelectedItem();
			int locationIndex = locModel.getId();
			Model areaModel = (Model) area.getSelectedItem();
			int areaIndex = areaModel.getId();
			int injuryIndex = injury.getSelectedItemPosition();
			int typeIndex = type.getSelectedItemPosition();
			int slopeIndex = slope.getSelectedItemPosition();
			String strName = name.getText().toString();
			String strComments = comments.getText().toString();
			int gradeIndex = grade.getSelectedItemPosition();
			int colorIndex = color.getSelectedItemPosition();
			int statusIndex = status.getSelectedItemPosition();
			int intMoves = Integer.parseInt(moves.getText().toString());
			int intCal = Integer.parseInt(CAL.getText().toString());
			int intSendTotal = Integer.parseInt(sendTotal.getText().toString());
			int intFirstAttemptTotal = Integer.parseInt(firstAttemptTotal
					.getText().toString());
			LocalDate sendDateDt = null;
			LocalDate firstAttemptDateDt = null;
			if (sendDate.getText().length() > 0) {
				String strSendDate = sendDate.getText().toString();
				sendDateDt = LocalDate.parse(strSendDate,
						DateTimeFormat.forPattern("MM/dd/yyyy"));
			}
			if (firstAttemptDate.getText().length() > 0) {
				String strfirstAttemptDate = firstAttemptDate.getText()
						.toString();
				firstAttemptDateDt = LocalDate.parse(strfirstAttemptDate,
						DateTimeFormat.forPattern("MM/dd/yyyy"));
			}

			Climb climb = new Climb(strName, locationIndex, areaIndex,
					gradeIndex, intMoves, intCal, slopeIndex, typeIndex,
					colorIndex, injuryIndex, statusIndex, isInside,
					firstAttemptDateDt, intSendTotal, sendDateDt,
					intFirstAttemptTotal, bmpPhoto, strComments,
					Double.valueOf(rating.getRating()));
			if (climbId == null) {
				db.addClimb(climb);
				climbId = db.getClimbId();
			} else {
				climb.setId(climbId);
				db.updateClimb(climb);
			}
			setResult(RESULT_OK, getIntent());
			Toast.makeText(context, "Climb was Saved", Toast.LENGTH_SHORT)
					.show();

		}

		private boolean validate() {
			if (name.getText().length() == 0) {
				Helper.createErrorPopup(ClimbingAttributes.this, "Cannot Save Climb", "Climb Name is Required");
				return false;
			}
			if (((Model) location.getSelectedItem()) == null) {			
				Helper.createErrorPopup(ClimbingAttributes.this, "Cannot Save Climb", "Location is Required");
				return false;
			}
			if (((Model) area.getSelectedItem()) == null) {
				Helper.createErrorPopup(ClimbingAttributes.this, "Cannot Save Climb", "Area is Required");
				return false;
			}
			return true;
		}
	};

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch (view.getId()) {
		case R.id.cragRbtn:
			if (checked)
				isInside = false;
			break;
		case R.id.gymRbtn:
			if (checked)
				isInside = true;
			break;
		}
	}

	public OnClickListener chooseLocationListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(context, NewLocation.class);
			startActivityForResult(i, NEW_LOCATION_ACTIVITY_REQUEST_CODE);
		}
	};

	public OnClickListener chooseAreaListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(context, NewArea.class);
			Model locModel = (Model) location.getSelectedItem();
			i.putExtra("locationId", locModel.getId());
			i.putExtra("new", true);
			startActivityForResult(i, NEW_AREA_ACTIVITY_REQUEST_CODE);
		}
	};

	public OnClickListener startPhotoListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(context, NewPhoto.class);
			if (bmpPhoto != null) {
				i.putExtra("data", bmpPhoto);
			}
			startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}
	};

	private class NumberPickerWidget {
		Button button;

		public NumberPickerWidget(Button button) {
			this.button = button;
			initiatePopupWindow();
		}

		private void initiatePopupWindow() {
			try {
				// We need to get the instance of the LayoutInflater, use the
				// context of this activity
				LayoutInflater inflater = (LayoutInflater) ClimbingAttributes.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// Inflate the view from a predefined XML layout
				View layout = inflater.inflate(R.layout.number_picker_pref,
						(ViewGroup) findViewById(R.id.pref_num_picker));
				// create a 300px width and 470px height PopupWindow
				pw = new PopupWindow(layout, 500, 600, true);
				// display the popup in the center
				pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

				numberPickerInput = (TextView) layout
						.findViewById(R.id.numberPickerInput);
				numberPickerInput.setText(button.getText().toString());
				Button btnOk = (Button) layout.findViewById(R.id.btnOk);
				btnOk.setOnClickListener(okBtnListener);
				Button btnCancel = (Button) layout.findViewById(R.id.btnCancel);
				btnCancel.setOnClickListener(cancelBtnListener);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private OnClickListener cancelBtnListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				pw.dismiss();
			}
		};

		private OnClickListener okBtnListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				button.setText(numberPickerInput.getText().toString());
				pw.dismiss();
			}
		};

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				bmpPhoto = data.getByteArrayExtra("data");
			}
		}
		if (requestCode == NEW_AREA_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				// refresh area spinner
				populateLocations();
				int locIdx = locationsAdapter.getIndexWithId(data.getIntExtra(
						"locationId", 0));
				location.setSelection(locIdx);
				Model locModel = (Model) location.getSelectedItem();
				populateAreas(locModel.getId());

				int areaIdx = areasAdapter.getIndexWithId(data.getIntExtra(
						"id", 0));
				area.setSelection(areaIdx);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the image capture
			}
		}
		if (requestCode == NEW_LOCATION_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				// refresh location spinner
				populateLocations();
				int locIdx = locationsAdapter.getIndexWithId(data.getIntExtra(
						"id", 0));
				location.setSelection(locIdx);
				Model locModel = (Model) location.getSelectedItem();
				populateAreas(locModel.getId());
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the image capture
			}
		}
	}


}
