package com.rubylicious.climbingtracker;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewArea extends Activity {
	final Context context = this;
	Spinner location;
	TextView area;
	TextView comments;
	DataSource db;
	IdBlackAdapter locationsAdapter;
	int areaId = 0;
	int locationId = 0;
	boolean isNew = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_area);
		db = new DataSource(context);

		location = (Spinner) findViewById(R.id.spnNewLocation);
		populateLocations();
		area = (TextView) findViewById(R.id.edtArea);
		comments = (TextView) findViewById(R.id.edtAreaComments);
		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(saveBtnListener);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(cancelBtnListener);

		if (getIntent().getExtras() != null) {
			if(getIntent().getBooleanExtra("new", false)){
				int locIdx = locationsAdapter.getIndexWithId(getIntent().getIntExtra("locationId", 0));
			    location.setSelection(locIdx);
			    isNew = true;
			}else{
				areaId = getIntent().getIntExtra("areaId", 0);
				locationId = getIntent().getIntExtra("locationId", 0);
				if(areaId != 0){
					populateArea();
				}
			}		
		}
	}

	public OnClickListener saveBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(validate() == false){
				return;
			}
			// save fields to DB.
			Model locModel = (Model)location.getSelectedItem();
			String name = area.getText().toString();
			String cmmts = comments.getText().toString();
			if(isNew){
				Area area = new Area(name, locModel.getId(), cmmts);
			    db.addArea(area);
			} else{
				Area area = new Area(areaId, name, locModel.getId(), cmmts);
			    db.updateArea(area);
			}
			
			getIntent().putExtra("id", db.getAreaId());
			getIntent().putExtra("locationId", locModel.getId());
			setResult(RESULT_OK, getIntent());
			Toast.makeText(context, "Area was Saved", Toast.LENGTH_SHORT).show();
			finish();
		}

		private boolean validate() {
			if (area.getText().length() == 0) {
				Helper.createErrorPopup(NewArea.this, "Cannot Save Area", "Area Name is Required");
				return false;
			}
			return true;
		}
	};

	public OnClickListener cancelBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			setResult(RESULT_CANCELED, getIntent());
			finish();
		}
	};
	
	private void populateLocations() {
		List<Model> locations = Location.populateLocations(db);
		locationsAdapter = new IdBlackAdapter(context, locations);		
		location.setAdapter(locationsAdapter);
	}
	
	private void populateArea() {
		db.openRead();
		Area areaObj = db.getArea(areaId, locationId);
		int locIdx = locationsAdapter.getIndexWithId(locationId);
		location.setSelection(locIdx);
		area.setText(areaObj.getName());
		comments.setText(areaObj.getComments());	
	}
}
