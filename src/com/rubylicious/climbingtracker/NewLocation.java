package com.rubylicious.climbingtracker;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NewLocation extends Activity {
	final Context cont = this;
	DataSource db;
	TextView location;
	TextView address;
	TextView lattitude;
	TextView longitude;
	TextView GPSLabel;
	TextView comments;
	double lat = 0;
	double lon = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_location);
		db = new DataSource(cont);

		location = (TextView) findViewById(R.id.edtLocation);		
		address = (TextView) findViewById(R.id.edtAddress);
		lattitude = (TextView) findViewById(R.id.lattitude);
		longitude = (TextView) findViewById(R.id.longitude);
		GPSLabel = (TextView) findViewById(R.id.edtGPSlbl);

		GPSTracker gps = new GPSTracker(this);
		if(gps.canGetLocation()){
			lat = gps.getLatitude();
			lon = gps.getLongitude();
		}else{
			gps.showSettingsAlert();
		}
		lattitude.setText(String.valueOf(lat));
		longitude.setText(String.valueOf(lon));
		GPSLabel.setClickable(true);
		GPSLabel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lon);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				cont.startActivity(intent);				
			}
		});
		
		comments = (TextView) findViewById(R.id.edtLocationComments);
		
		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(saveBtnListener);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(cancelBtnListener);
	}
	public void onClickGPS(){
		String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lon);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		cont.startActivity(intent);
	}

	public OnClickListener saveBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(validate() == false){
				return;
			}
			// save fields to DB.
			String name = location.getText().toString();
			String addr = address.getText().toString();
			String coordinates = lattitude.getText().toString() + " " + longitude.getText().toString();
			String cmmts = comments.getText().toString();
			Location loc = new Location(name, addr, coordinates, cmmts);
			db.addLocation(loc);
			getIntent().putExtra("id", db.getLocationId());
			setResult(RESULT_OK, getIntent());
			Toast.makeText(cont, "Location was Saved", Toast.LENGTH_SHORT)
			.show();
			finish();
		}

		private boolean validate() {
			if (location.getText().length() == 0) {
				Helper.createErrorPopup(NewLocation.this, "Cannot Save Location", "Location Name is Required");
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

}
