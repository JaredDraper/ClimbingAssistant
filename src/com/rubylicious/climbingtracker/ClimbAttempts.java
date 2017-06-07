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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ClimbAttempts extends FragmentActivity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
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
			sendTotal, firstAttemptTotal, photo, history;
	Spinner injury, type, slope, status, grade, color;
	TextView climbName, comments, locationArea;
	RadioButton gymRbtn;
	RadioButton cragRbtn;
	RadioButton trainRbtn;
	ArrayAdapter<String> statusAdapter;
	ArrayAdapter<String> colorAdapter;
	ArrayAdapter<String> gradeAdapter;
	private PopupWindow pw;
	private TextView numberPickerInput;
	private RatingBar rating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attempt);
		db = new DataSource(context);
		save = (Button) findViewById(R.id.btnSaveAttempt);
		save.setOnClickListener(saveBtnListener);
		gymRbtn = (RadioButton) findViewById(R.id.gymRbtn);
		gymRbtn.setChecked(true);
		cragRbtn = (RadioButton) findViewById(R.id.cragRbtn);
		locationArea = (TextView) findViewById(R.id.locationArea);
		climbName = (TextView) findViewById(R.id.climbName);
		history = (Button) findViewById(R.id.historyViewBtn);
		history.setOnClickListener(historyBtnListener);
//		photo = (Button) findViewById(R.id.btnPhoto);
//		photo.setOnClickListener(startPhotoListener);
		rating = (RatingBar) findViewById(R.id.ratingBar);
		rating.setStepSize((float) 0.5);
		status = (Spinner) findViewById(R.id.spnStatus);
		status.setAdapter(new ImageDropDownAdapter(getLayoutInflater(), ClimbAttempts.this, R.layout.row,
				getResources().getStringArray(R.array.status), statusIconIndexes));

		grade = (Spinner) findViewById(R.id.spnGrade);
		gradeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.grade));
		grade.setAdapter(gradeAdapter);

		color = (Spinner) findViewById(R.id.spnColor);
		color.setAdapter(new ImageDropDownAdapter(getLayoutInflater(),ClimbAttempts.this, R.layout.row,
				getResources().getStringArray(R.array.color), colorIconIndexes));

		injury = (Spinner) findViewById(R.id.spnInjury);
		injury.setAdapter(new ImageDropDownAdapter(getLayoutInflater(),ClimbAttempts.this, R.layout.row,
				getResources().getStringArray(R.array.injury), injuryIconIndexes));

		type = (Spinner) findViewById(R.id.spnType);
		type.setAdapter(new ImageDropDownAdapter(getLayoutInflater(),ClimbAttempts.this, R.layout.row,
				getResources().getStringArray(R.array.type), typeIconIndexes));

		slope = (Spinner) findViewById(R.id.spnSlope);
		slope.setAdapter(new ImageDropDownAdapter(getLayoutInflater(),ClimbAttempts.this, R.layout.row,
				getResources().getStringArray(R.array.slope), slopeIconIndexes));

		comments = (TextView) findViewById(R.id.comments);

		firstAttemptDate = (Button) findViewById(R.id.firstAttemptDateBtn);
		sendDate = (Button) findViewById(R.id.sendDateBtn);
		moves = (Button) findViewById(R.id.btnMoves);
		moves.setOnClickListener(movesBtnListener);
		//CAL = (Button) findViewById(R.id.btnCal);
		//CAL.setOnClickListener(calBtnListener);
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

	private void populateClimb(Integer chosenClimbId) {
		if (chosenClimbId != null) {
			isNewClimb = false;

			Climb climb = db.getClimb(chosenClimbId);
			climbId = climb.getId();
			Location loc = db.getLocation(climb.getLocationId());
			Area area = db.getArea(climb.getLocationId(),climb.getAreaId());
			locationArea.setText(loc.getName() + " - " + area.getName());
			injury.setSelection(climb.getInjury());
			type.setSelection(climb.getTypeId());
			slope.setSelection(climb.getSlopeId());
			climbName.setText(climb.getName());
			comments.setText(climb.getComments());
			grade.setSelection(climb.getGradeId());
			color.setSelection(climb.getColor());
			moves.setText(climb.getMoves().toString());
			//CAL.setText(climb.getCal().toString());
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

	public void showDatePickerDialog(View v) {
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
	
	public OnClickListener historyBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(context, HistoryView.class);
			if (getIntent().getExtras() != null) {
				climbId = getIntent().getExtras().getInt("climbId");
				Bundle bundle = new Bundle();
				bundle.putInt("climbId", climbId);
				i.putExtras(bundle);
			}		
			
			startActivity(i);
		}
	};

	private OnClickListener saveBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(validate() == false){
				return;
			}
			int injuryIndex = injury.getSelectedItemPosition();
			int typeIndex = type.getSelectedItemPosition();
			int slopeIndex = slope.getSelectedItemPosition();
			String strName = climbName.getText().toString();
			String strComments = comments.getText().toString();
			int gradeIndex = grade.getSelectedItemPosition();
			int colorIndex = color.getSelectedItemPosition();
			int statusIndex = status.getSelectedItemPosition();
			int intMoves = Integer.parseInt(moves.getText().toString());
			//int intCal = Integer.parseInt(CAL.getText().toString());
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

			Climb climb = new Climb(strName, 0, 0,
					gradeIndex, intMoves, 0, slopeIndex, typeIndex,
					colorIndex, injuryIndex, statusIndex, isInside,
					firstAttemptDateDt, intSendTotal, sendDateDt,
					intFirstAttemptTotal, bmpPhoto, strComments,
					Double.valueOf(rating.getRating()));

				climb.setId(climbId);
				db.updateClimbFromAttempt(climb);
				History history = new History(climbId, intMoves);
				db.addHistory(history);
			
			setResult(RESULT_OK, getIntent());
			Toast.makeText(context, "Attempt was Saved", Toast.LENGTH_SHORT)
					.show();

		}

		private boolean validate() {
			if (climbName.getText().length() == 0) {
				Helper.createErrorPopup(ClimbAttempts.this, "Cannot Save Attempt", "Climb Name is Required");
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
				LayoutInflater inflater = (LayoutInflater) ClimbAttempts.this
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
	}


}
