package com.rubylicious.climbingtracker;

import android.app.AlertDialog;
import android.content.Context;

public class Helper {

	public static void createErrorPopup(Context context, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setPositiveButton(R.string.ok, null);
		builder.setMessage(message);
		AlertDialog errorDialog = builder.create();
		errorDialog.show();
	}
}
