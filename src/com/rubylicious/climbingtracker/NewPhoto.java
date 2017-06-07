package com.rubylicious.climbingtracker;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class NewPhoto extends Activity{
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	final Context cont = this;
	Button btnOpenCamera;
	ImageView image;
	byte[] byteArray;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);
		image = (ImageView) findViewById(R.id.imgvPhoto);
		Button btnOpenCamera = (Button) findViewById(R.id.btnRetrieveImage);		
		btnOpenCamera.setOnClickListener(openCameraBtnListener);
		Button btnSave = (Button) findViewById(R.id.btnPhotoOk);
		btnSave.setOnClickListener(saveImageBtnListener);
		if(getIntent().getByteArrayExtra("data") != null){
			byteArray = getIntent().getByteArrayExtra("data");
			Bitmap bmImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			image.setImageBitmap(bmImage);
		}
	}

	public OnClickListener openCameraBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}
	};
	
	public OnClickListener saveImageBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			getIntent().putExtra("data", byteArray);
			//TODO save fields to DB.
			setResult(RESULT_OK, getIntent());
			finish();
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {        
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == Activity.RESULT_OK) {
	            Bitmap bmp = (Bitmap) data.getExtras().get("data");
	            ByteArrayOutputStream stream = new ByteArrayOutputStream();
	            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
	            byteArray = stream.toByteArray();
	            image.setImageBitmap(bmp);
	            getIntent().putExtra("data", byteArray);

	        } else if (resultCode == Activity.RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }               
	}
}
